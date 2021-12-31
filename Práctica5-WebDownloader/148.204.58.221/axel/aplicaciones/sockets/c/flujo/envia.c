#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h> //printf, perror,fdopen
#include <string.h>
#include <netdb.h> //gethostbyname, getaddrinfo
#include <unistd.h>//close
#include <stdlib.h> //exit
#include <netinet/in.h>//inet_addr
#include <arpa/inet.h>//inet_addr
int main(int argc, char* argv[]){
char host[20],pto[5];
printf("Escribe la direccion del servidor:");
//gets(host);
fgets(host,sizeof(host),stdin);
fflush(stdin);
printf("\nEscribe el puerto:");
fgets(pto,sizeof(pto),stdin);
fflush(stdin);

int status;
struct addrinfo hints;
struct addrinfo *servinfo; // will point to the results
memset(&hints, 0, sizeof(hints)); // make sure the struct is empty
hints.ai_family = AF_UNSPEC; // don't care IPv4 or IPv6
hints.ai_socktype = SOCK_STREAM; // TCP stream sockets
// get ready to connect
status = getaddrinfo(host, pto, &hints, &servinfo);
int cd = socket(servinfo->ai_family,servinfo->ai_socktype,servinfo->ai_protocol);


FILE *f = fdopen(cd,"w+");
//if (connect(cd,(struct sockaddr *)&sdir,sizeof(sdir))<0){
if (connect(cd,servinfo->ai_addr,servinfo->ai_addrlen)<0){
 perror("error en funcion connect()\n");
}//if
freeaddrinfo(servinfo);
printf("\n Conexion establecida.. enviando datos..\n");
int dato1 =5;
long  dato2= 70;
float dato3=3.0f;
char *dato4="un mensaje";
int n;
///dato1
n= write(cd,&dato1,sizeof(dato1));
if(n<0)
   perror("Error de escritura\n");
else if(n==0)
   perror("Socket cerrado error de escritura\n");
else
  fflush(f);
  printf("Se envio el dato: %d\n",dato1);
//dato2
n= write(cd,&dato2,sizeof(dato2));
if(n<0)
   perror("Error de escritura\n");
else if(n==0)
   perror("Socket cerrado error de escritura\n");
else
  printf("Se envio el dato: %ld\n",dato2);
  fflush(f);

//dato3
char datos[20];
sprintf(datos,"%f",dato3);
datos[strlen(datos)]='\0';
n= write(cd,datos,strlen(datos));
if(n<0)
   perror("Error de escritura\n");
else if(n==0)
   perror("Socket cerrado error de escritura\n");
else
  //printf("Se envio el dato: %s\n",datos);
printf("Se envio el dato %.02f\n",dato3);
fflush(f);

//dato4
n= write(cd,dato4,strlen(dato4));
if(n<0)
   perror("Error de escritura\n");
else if(n==0)
   perror("Socket cerrado error de escritura\n");
else
  printf("Se envio el dato: %s\n",dato4);
  fflush(f);
close(cd);
fclose(f);
return 0;
}//main
