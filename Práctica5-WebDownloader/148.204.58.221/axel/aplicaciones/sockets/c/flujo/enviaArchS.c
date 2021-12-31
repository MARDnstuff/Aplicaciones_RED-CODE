#include <netdb.h>
#include <netinet/in.h>
#include <string.h>
#include <stdio.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#define PUERTO 8000

  
  int main(int argc, char* argv[]){
  int sd;
  struct sockaddr_in serverADDRESS;
  struct hostent *hostINFO;
  char* servidor="127.0.0.1";
  
  hostINFO = gethostbyname(servidor);
  if (hostINFO == NULL) {
     printf("Problema al interpretar el host\n");
     return 1;
  }//if

  sd = socket(AF_INET, SOCK_STREAM, 0);
  if (sd < 0) {
     printf("No fue posible crear el socket\n");
     return 1;
    }//if
  
            //serverADDRESS.sin_family = hostINFO->h_addrtype;
           // memcpy((char *) &serverADDRESS.sin_addr.s_addr,hostINFO->h_addr_list[0], hostINFO->h_length);
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
           //char * nombre;
	   //nombre = (char *)malloc(sizeof(char)*100);     
	   char *arch1="/home/ubuntu/archivo.txt", *arch2="archivo3.txt";
           FILE *f;
        char buffer[100];
        memset((char *)&buffer,0,sizeof(buffer));
        if (f = fopen(arch1, "rb"))//rt
        {   //fread
         //if (f2 = fopen(arch2, "wb")){
          int prev=ftell(f);//medimos el tamaño del archivo
          fseek(f,0L,SEEK_END);
          int sz = ftell(f);
          fseek(f,prev,SEEK_SET);
          printf("El archivo a leer mide %d bytes\n",sz);
          
	  char msj[50];
	  memset(&msj,0,sizeof(msj));
	  sprintf(msj,"%s;%d \n\r",arch2,sz);
          enviados=write(cd,msj,strlen(msj));
	 
	  getchar();
          printf("Se escribio %s \n",msj);
         if(enviados<0){
            perror("No se pudo mandar el tamanio del archivo..");
         return 1;
         }

          int tam_bloque = (sizeof(buffer)<sz)?sizeof(buffer):sz;
          printf("Tamaño de bloque: %d bytes\n",tam_bloque);
          int leidos=0,l;
          while(leidos<sz){
            memset((char *)&buffer,0,sizeof(buffer));
            l=fread(buffer, 1, tam_bloque, f);
            leidos = leidos + l;
	    int es=write(cd,buffer,l);
	    if(es<0){
		perror("No se pudo mandar el buffer de datos");
		return 1;
	    }//if
           // fwrite(buffer,1,l,f2);
           // fflush(f2);
            printf("Leyendo %d caracteres del archivo:\n%s\n",l, buffer);
          }//while
          //buffer[10] = 0;
          fclose(f);
 // }//if fwrite
  }//if fread


           //free(nombre);



            close(sd);

return 0;
}//main
