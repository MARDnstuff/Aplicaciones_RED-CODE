#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <netinet/in.h> //sockaddr_in
#include <string.h>
#include <arpa/inet.h>
void error(char *msj){
perror(msj);
exit(1);
}

int main(int argc, char *argv[]){

//int num[10]={2,4,6,8,0,1,3,5,7,9};
int num2[10];
int sd,cd,n,i,v=1;;
struct sockaddr_in sdir,cdir;
socklen_t ctam = sizeof(cdir);
bzero((char *)&sdir,sizeof(sdir));
sdir.sin_family= AF_INET;
sdir.sin_port=htons(5000);
sdir.sin_addr.s_addr=htonl(INADDR_ANY);
sd= socket(AF_INET, SOCK_DGRAM, 0);
FILE* f = fdopen(sd,"w+");
if(bind(sd, (struct sockaddr*)&sdir, sizeof(sdir))<0){
  perror("El puerto ya esta en uso\n");
  close(sd);
  exit(1);
  }//if

int op= setsockopt(sd, SOL_SOCKET, SO_REUSEADDR,&v,sizeof(v));
if (op<0)
  printf("No se pudo modificar la opcion de socket\n");

for(;;){
n= recvfrom(sd,num2, sizeof(num2),0, (struct sockaddr *)&cdir,&ctam);
fflush(f);
if(n<0)
  error("Error de lectura\n");
else if(n==0)
 error("socket cerrado\n");

printf("Tam arreglo: %d bytes \n",sizeof(num2));
printf ("%d  bytes recibidos\n desde %s: %d",n,inet_ntoa(cdir.sin_addr),ntohs(cdir.sin_port));
printf("\n Datos: \n");
for(i=0;i<10;i++)
  printf("%d \t",num2[i]);
  printf("\n");
//fflush(stdin);
}//for
close(sd);
fclose(f);

}//main
