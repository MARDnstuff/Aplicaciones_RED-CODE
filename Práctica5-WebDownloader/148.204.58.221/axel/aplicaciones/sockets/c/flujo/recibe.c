#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h> //perror, printf
#include <string.h>
#include <netinet/in.h> //htons htonl
#include <stdlib.h> //exit
#include <unistd.h>//close
#include <arpa/inet.h>//inet_ntoa

int main(int agrc, char* argv[]){

struct sockaddr_in sdir,cdir;
bzero((char *)&sdir, sizeof(sdir));

sdir.sin_family=AF_INET;
sdir.sin_port=htons(9999);
sdir.sin_addr.s_addr=htonl(INADDR_ANY);

socklen_t ctam = sizeof(cdir);
int sd,cd,v=1,op;

if((sd=socket(AF_INET,SOCK_STREAM,0))<0){
  perror("Error en la funcion socket()\n");
  exit(1);
}//if

op=setsockopt(sd,SOL_SOCKET,SO_REUSEADDR,&v,sizeof(v));
if(op<0)
  perror("Error en la opcion de socket\n");

if(bind(sd,(struct sockaddr *)&sdir,sizeof(sdir))<0){
  perror("El puerto ya esta en uso\n");
  close(sd);
  exit(1);

}//if
printf("Servicio iniciado..");
listen(sd,5);
for(;;){
  if((cd=accept(sd,(struct sockaddr *)&cdir,&ctam))<0){
     perror("Error en funcion accept()\n");
     continue;
   }//if
   printf("\nCliente conectado desde ->%s:%d\n Recibiendo datos...\n",inet_ntoa(cdir.sin_addr),ntohs(cdir.sin_port));
   int n,dato1;
   long dato2;
   float dato3;
   char dato4[20];
   bzero(dato4,sizeof(dato4));
//dato1
   n= read(cd,&dato1, sizeof(dato1));
   printf("dato1->%d\n",dato1);
//dato2
   n= read(cd,&dato2, sizeof(dato2));
   printf("dato2->%ld\n",dato2);
//dato3
  char datos[20];
   n= read(cd,datos, sizeof(datos));
   dato3= atof(datos);
   printf("dato3->%.02f\n",dato3);
//dato4
   n= read(cd,dato4, sizeof(dato4));
   printf("dato4->%s\n",dato4);


  close(cd);
}//for
close(sd);
return 0;
}//main

