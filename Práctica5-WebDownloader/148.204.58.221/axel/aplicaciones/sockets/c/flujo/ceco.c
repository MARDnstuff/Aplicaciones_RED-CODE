#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <unistd.h>//read
#include <stdlib.h>//exit

void error(const char * msj){
 perror(msj);
 exit (1);
}//error

int main(){

 int cd,n,n1,pto=9999;
 struct sockaddr_in sdir;
 bzero((char *)&sdir,sizeof(sdir));
 char *srv="127.0.0.1";
 cd=socket(AF_INET,SOCK_STREAM,0);
 if(cd<0)
   error("Error al crear el socket\n");
 sdir.sin_family=AF_INET;
 sdir.sin_port= htons(pto);
 inet_aton(srv,&sdir.sin_addr);
 if(connect(cd,(struct sockaddr *)&sdir,sizeof(sdir))<0)
   error("Error al intentar establecer conexion con el servidor\n");
 FILE *f = fdopen(cd,"w+");
 printf("Conexionen establecida.. Escribe una serie de cadenas <enter> para enviar, SALIR para terminar\n");
 
char *linea=(char *)malloc(sizeof(char)*50);
bzero(linea,sizeof(linea)); 
size_t tam; 
while((n=getline(&linea,&tam,stdin))!=-1){

	if(strstr(linea, "SALIR")!=NULL){
	printf("escribio SALIR\n");
        n1= write(cd,linea,n);
        fflush(f);
	fclose(f);
	close(cd);
	exit(0);
	} else {
	int cantidad= strlen(linea);
	n1= write(cd,linea,cantidad+1);
	printf("Se escribieron %d caracteres-> %s\n",n,linea);
 	fflush(f);
	bzero(linea,sizeof(linea));
	char eco[cantidad+1];
	bzero(eco,sizeof(eco));
 	n1=read(cd,eco,sizeof(eco));
	printf("tamaño eco: %d",sizeof(eco));
        if(n1<0)
          error("Error al leer desde el socket\n");
        else if(n1==0)
          error("Socket cerrado\n");
        printf("\nECO recibido: %s\n",eco);
	//free(eco);
        }//else


}//while

return 0;
}//main
