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
  
            serverADDRESS.sin_family = hostINFO->h_addrtype;
            memcpy((char *) &serverADDRESS.sin_addr.s_addr,hostINFO->h_addr_list[0], hostINFO->h_length);
            serverADDRESS.sin_port = htons(PUERTO);

            if (connect(sd, (struct sockaddr *) &serverADDRESS, sizeof(serverADDRESS)) < 0) {
                printf("No fue posible establecer la conexion\n");
                return 1;
            }
	    printf("Conexion establecida..");
            //char *nombre;
	    //nombre = (char *)malloc(sizeof(char)*100); 	   
     	    char nombre[100];
	    memset(nombre,0,sizeof (nombre));
	    int recibidos=0;
//	    recibidos=read(sd,nombre,sizeof (nombre));
//	    if(recibidos<0){
//	     perror("No se pudo leer el nombre del archivo..");
//	     return 1;
//	    }//if

//	   printf("\nArchivo a guardar: %s\n",nombre);
	   //free(nombre);
    
	    char ta[50];
	   memset(ta,0,sizeof(ta));
	   int taa;
            recibidos=read(sd,ta,sizeof(ta));
            if(recibidos<0){
             perror("No se pudo leer el tamanio del archivo..");
             return 1;
            }//if
	   char *tok=strtok(ta,";");
	   char *tok1=strtok(NULL,";");
	   printf("\nt1: %s\n",tok);
           printf("t1: %s\n",tok1);
	   taa=atoi(tok1);
          // printf("\nTamanio del archivo: %d,  %s \n",taa,tok);
	   printf("Recibiendo archivo...\n");
	  int taml=0;
	  char buffer[100];
	 FILE *f2;
        if (f2 = fopen(tok, "wb"))//rt
        { 

	  while((taa-taml)>0){
	    memset((char *)&buffer,0,sizeof(buffer));
	    int ll=read(sd,buffer,sizeof(buffer));
           if(ll<0){
             perror("No se pudieron leer los datos del archivo..");
             return 1;
            }//if
	   taml = taml + ll;
	   printf("%s",buffer);
           fwrite(buffer,1,ll,f2);
            fflush(f2);

	  }//while
}//fopen
fclose(f2);
close(sd);
return 0;
}//main
