#include <netdb.h>
#include <netinet/in.h>
#include <string.h>
#include <stdio.h>
//#include <sys/stat.h>
#include <libgen.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#define PUERTO 8000

  
  int main(int argc, char* argv[]){
  int sd,n;
  struct sockaddr_in serverADDRESS;
  struct hostent *hostINFO;
  FILE *f;
  sd = socket(AF_INET, SOCK_STREAM, 0);
  if (sd < 0) {
     printf("No fue posible crear el socket\n");
     return 1;
    }//if
    
    int v=1;
    int op= setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &v, sizeof(v));
            serverADDRESS.sin_family = AF_INET;
            serverADDRESS.sin_addr.s_addr = htonl(INADDR_ANY);
            serverADDRESS.sin_port = htons(PUERTO);
            if (bind(sd, (struct sockaddr *) &serverADDRESS, sizeof(serverADDRESS)) < 0) {
                printf("Cannot bind socket\n");
                close(sd);
                return 1;
            }

            listen(sd, 5);
            socklen_t clientADDRESSLENGTH;
            struct sockaddr_in clientADDRESS;
	    int cd,enviados=0;
            clientADDRESSLENGTH = sizeof(clientADDRESS);
            cd = accept(sd, (struct sockaddr *) &clientADDRESS, &clientADDRESSLENGTH);
            if (cd < 0) {
                printf("No fue posible aceptar la conexion\n");
                close(sd);
                return 1;
            }//if
	   f= fdopen(cd,"w+");     
	   char *arch1="/home/usuario/Escritorio/duke1.png", *arch2="duke2.png";
        char buffer[100];
/*********/
//char* local_file = "/foo/bar/baz.txt";

char* ts1 = strdup(arch1);
char* ts2 = strdup(arch1);

char* dir = dirname(ts1);
char* nombre = basename(ts2);
/********/
        memset((char *)&buffer,0,sizeof(buffer));
        if (f = fopen(arch1, "r+"))//rt
        {   //fread
          int prev=ftell(f);//medimos el tamaño del archivo
          fseek(f,0L,SEEK_END);
          long sz = (long)ftell(f);
          fseek(f,prev,SEEK_SET);
          printf("El archivo a leer mide %ld bytes\n",sz);
          
	  char msj[50];
	  memset(&msj,0,sizeof(msj));
	  sprintf(msj,"1_%s;%ld",nombre,sz);
          enviados=write(cd,msj,strlen(msj)+1);
	  fflush(f);
	  //printf("Presiona enter para continuar...\n");
	  //getchar();
          printf("Se escribio %s \n",msj);
         if(enviados<0){
            perror("No se pudo mandar el tamanio del archivo..");
         return 1;
         }
	  printf("Se envio cadena de %d bytes\n",enviados);
          int l;
	  char ok[3];
	  bzero(ok, sizeof(ok));
	  n=read(cd, ok, sizeof(ok));
          long leidos=0;
          while(leidos<sz){
            memset((char *)&buffer,0,sizeof(buffer));
            l=fread(buffer, 1, sizeof(buffer), f);
            leidos = leidos + l;
	    int es=write(cd,buffer,l);
	    if(es<0){
		perror("No se pudo mandar el buffer de datos");
		return 1;
	    }//if
	    fflush(f);
          }//while
          fclose(f);
	  printf("Archivo enviado.. \n");
	  close(cd);
  }//if fread
	    
            close(sd);

return 0;
}//main
