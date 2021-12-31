#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <stdlib.h>   //exit
#include <netinet/in.h> //sockaddr_in
#include <string.h>
#include <netdb.h>


void error(char *msj){
perror(msj);
exit(1);
}

int main(int argc, char *argv[]){
int num[10]={2,4,6,8,0,1,3,5,7,9};
//int num2[10];
int cd,n,i,v=1;;
struct sockaddr_in sdir;
bzero((char *)&sdir,sizeof(sdir));

struct hostent *host;
host= gethostbyname("127.0.0.1");
if(host==NULL)
 error("Direccion no valida\n");

sdir.sin_family= AF_INET;
sdir.sin_port=htons(5000);
bcopy((char*)host->h_addr, (char *)&sdir.sin_addr.s_addr, host->h_length);
cd= socket(AF_INET, SOCK_DGRAM, 0);
FILE *f= fdopen(cd,"w+");
printf("Enviando los siguientes numeros:\n");
for(i=0;i<10;i++)
  printf("%d \t",num[i]);

n= sendto(cd,num, sizeof(num),0, (struct sockaddr *)&sdir, sizeof(sdir));
printf("Se enviarion: %d bytes \n",n);

if(n<0)
  error("Error de escritura\n");
else if(n==0)
 error("socket cerrado\n");
fflush(f);
close(cd);
fclose(f);

}//main


