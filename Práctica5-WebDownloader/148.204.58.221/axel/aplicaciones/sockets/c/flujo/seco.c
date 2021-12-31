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
 int sd,cd,n,n1,pto=9999,v=1;
 socklen_t ctam;
 struct sockaddr_in sdir,cdir;
 ctam= sizeof(cdir);
 sd = socket(AF_INET,SOCK_STREAM,0);
 if(sd<0)
   error("Error al intentar crear al socket\n");

 int op = setsockopt(sd,SOL_SOCKET,SO_REUSEADDR,&v,sizeof(v));
 if(op<0)
  perror("No se pudo modificar la opcion de socket\n");
   bzero((char *)&sdir, sizeof(sdir));
   sdir.sin_family= AF_INET;
   sdir.sin_port=htons(pto);
   sdir.sin_addr.s_addr= htonl(INADDR_ANY);
   if(bind(sd,(struct sockaddr *)&sdir,sizeof(sdir))<0)
     error("El puerto ya esta en uso\n");
   listen(sd,5);
   printf("Servidor listo.. Esperando clientes \n");
   for(;;){
    cd=accept(sd,(struct sockaddr *)&cdir,&ctam);
    if(cd<0){
      perror("Error al intentar aceptar conexion\n");
      continue;
    }//if
    FILE *f = fdopen(cd,"w+");
    char buf[1024];
    for(;;){
       bzero(buf,sizeof(buf));
       n=read(cd,buf,sizeof(buf));
       if(n<0){
          perror("Error de lectura\n");
          close(cd);
          break;
        } else if(n==0){
	 perror("Socket cerrado\n");
         break;
        }//if
     printf("recibido:  %s  longitud:%d \n",buf,strlen(buf));
     //char *tmp = (char *) malloc(strlen(buf));
    if(strstr(buf, "SALIR")!=NULL){
        printf("escribio SALIR\n");
        fclose(f);
        close(cd);
        break;
        } else {
     n1= write(cd,buf,n);
     fflush(f);
      }//else
    }//for
   }//for
  close(sd);
return 0;
}//main
