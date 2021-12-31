/*gcc seco.c thpool.c -D THPOOL_DEBUG -pthread -o seco
   -D: macro
*/

#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <unistd.h>//read
#include <stdlib.h>//exit
#include <netdb.h> //getaddrinfo() getnameinfo() freeaddrinfo()
#include "thpool.h"

#define pto "8888"

void error(const char * msj){
 perror(msj);
 exit (1);
}//error

void manejador(void *c){
     int cd = *(int *)c;
     int n=0,n1=0;
	//printf("Thread #%u working on task1\n", (int)pthread_self());
     FILE *f = fdopen(cd,"w+");
     char buf[2000];
     struct linger linger;
     linger.l_onoff = 1;
     linger.l_linger = 5;
     if(setsockopt(cd,SOL_SOCKET,SO_LINGER,(const char *)&linger,sizeof(linger))==-1){
       perror("error LINGER\n");
     }//if
     for(;;){
       memset(buf,'\0',sizeof(buf));
       n=recv(cd,buf,sizeof(buf),0);
       buf[strlen(buf)]='\0'; 
      if(n<0){
          perror("Error de lectura\n");
          close(cd);
          break;
        } else if(n==0){
	 perror("Socket cerrado\n");
         break;
        }//if
     printf("recibidos:  %d bytes con el mensaje:%s \n",(int)strlen(buf),buf);
     //char *tmp = (char *) malloc(strlen(buf));
    if(strncasecmp(buf, "SALIR",5)==0){
        printf("escribio SALIR. Cliene se va.\n");
        fclose(f);
        close(cd);
        break;
        } else {
     n1= send(cd,buf,strlen(buf),0);//+1
     fflush(f);
     printf("se enviaron %d bytes, con el eco: %s \n",n1,buf);
      }//else
    }//for
}//manejador


int main(){
 int sd,cd,v=1,rv,op=0;
 socklen_t ctam;
 char hbuf[NI_MAXHOST], sbuf[NI_MAXSERV];
 struct addrinfo hints, *servinfo, *p;
 struct sockaddr_storage their_addr; // connector's address 
 ctam= sizeof(their_addr);
 memset(&hints, 0, sizeof (hints));  //indicio
 hints.ai_family = AF_INET6;    /* Allow IPv4 or IPv6  familia de dir*/
 hints.ai_socktype = SOCK_STREAM;
 hints.ai_flags = AI_PASSIVE; // use my IP
 hints.ai_protocol = 0;          /* Any protocol */
 hints.ai_canonname = NULL;
 hints.ai_addr = NULL;
 hints.ai_next = NULL;

 if ((rv = getaddrinfo(NULL, pto, &hints, &servinfo)) != 0) {
     fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
     return 1;
 }//if

    for(p = servinfo; p != NULL; p = p->ai_next) {
        if ((sd = socket(p->ai_family, p->ai_socktype,p->ai_protocol)) == -1) {
            perror("server: socket");
            continue;
        }

        if (setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &v,sizeof(int)) == -1) {
            perror("setsockopt");
            exit(1);
        }

	if (setsockopt(sd, IPPROTO_IPV6, IPV6_V6ONLY, (void *)&op, sizeof(op)) == -1) {
            perror("setsockopt   no soporta IPv6");
            exit(1);
        }

        if (bind(sd, p->ai_addr, p->ai_addrlen) == -1) {
            close(sd);
            perror("server: bind");
            continue;
        }

        break;
    }

    freeaddrinfo(servinfo); // all done with this structure

    if (p == NULL)  {
        fprintf(stderr, "servidor: error en bind\n");
        exit(1);
    }

   listen(sd,100);
   printf("Servidor listo.. Esperando clientes \n");
   threadpool thpool = thpool_init(3);///////////////pool

  for(;;){
  
    ctam = sizeof their_addr;
      cd = accept(sd, (struct sockaddr *)&their_addr, &ctam);
      printf("Descriptor: %d\n",cd);
        if (cd == -1) {
            perror("accept");
            continue;
        }
     if (getnameinfo((struct sockaddr *)&their_addr, sizeof(their_addr), hbuf, sizeof(hbuf), sbuf,sizeof(sbuf), NI_NUMERICHOST | NI_NUMERICSERV) == 0)
        printf("cliente conectado desde %s:%s\n", hbuf,sbuf);
    thpool_add_work(thpool, (void*)manejador, &cd);
    
   }//for
  puts("Killing threadpool");
  thpool_destroy(thpool);
  close(sd);
return 0;
}//main
