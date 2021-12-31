#include <netdb.h>  //getaddrinfo
#include <netinet/in.h> //htons
#include <string.h>  //bzero
#include <stdio.h>   //printf perror
#include <stdlib.h> //atoi
#include <sys/types.h>
//#include <sys/stat.h>
#include <unistd.h>  //exit


  
  int main(int argc, char* argv[]){
  int cd,n;
  char *PUERTO="8000";
  long tam;
  struct sockaddr_in serverADDRESS;
  struct hostent *hostINFO;
  char* servidor="8.25.100.46";
  FILE *f;  

int status;
struct addrinfo hints;
struct addrinfo *servinfo; // will point to the results
memset(&hints, 0, sizeof(hints)); // make sure the struct is empty
hints.ai_family = AF_UNSPEC; // don't care IPv4 or IPv6
hints.ai_socktype = SOCK_STREAM; // TCP stream sockets
// get ready to connect
status = getaddrinfo(servidor, PUERTO, &hints, &servinfo);
cd = socket(servinfo->ai_family,servinfo->ai_socktype,servinfo->ai_protocol);


f = fdopen(cd,"w+");
//if (connect(cd,(struct sockaddr *)&sdir,sizeof(sdir))<0){
if (connect(cd,servinfo->ai_addr,servinfo->ai_addrlen)<0){
 perror("error en funcion connect()\n");
}//if
freeaddrinfo(servinfo);
printf("\n Conexion establecida.. enviando datos..\n");   
     	    char nombre[100];
	    memset(nombre,0,sizeof (nombre));
	    int recibidos=0;
	    char ta[50];
	   memset(ta,0,sizeof(ta));
            recibidos=read(cd,ta,sizeof(ta));
            if(recibidos<0){
             perror("No se pudo leer el tamanio del archivo..");
             return 1;
            }//if
	   int ttt = strlen(ta);
	   printf("Se recibio cadena de %d bytes  %d\n",recibidos,ttt);
	   char *tok=strtok(ta,";");
	   char *tok1=strtok(NULL,";");
	   printf("\nt1: %s\n",tok);
           printf("t1: %s\n",tok1);
	   tam=atol(tok1);
          // printf("\nTamanio del archivo: %d,  %s \n",taa,tok);
	   printf("Recibiendo archivo...\n");
	  int porcentaje=0;
          long a=0;
	  char buffer[100];
	  char *ok="ok";
	  n= write(cd,ok,strlen(ok)+1);
        if (f = fopen(tok, "w+"))//rt
        { 

	  while(a<tam){
	    memset((char *)&buffer,0,sizeof(buffer));
	    int ll=read(cd,buffer,sizeof(buffer));
           if(ll<0){
             perror("No se pudieron leer los datos del archivo..");
             return 1;
            }//if
	   a = a + ll;
           fwrite(buffer,1,ll,f);
            fflush(f);
	   porcentaje =(int)((a*100)/tam);
	   printf("recibidos: %ld   tam: %ld  porcentaje: %d\r",a,tam,porcentaje);

	  }//while
          fclose(f);
          close(cd);
}//fopen
return 0;
}//main
