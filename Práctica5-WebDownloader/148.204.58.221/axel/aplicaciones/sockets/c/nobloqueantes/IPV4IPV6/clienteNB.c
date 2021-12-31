#include <stdio.h>  ///sudo ifconfig inet6 add 2001::1234:1
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> //getaddrinfo() getnameinfo() freeaddrinfo()
#include <string.h>
#include <unistd.h>//read
#include <stdlib.h>//exit
#define pto "1234"

void error(const char * msj){
 perror(msj);
 exit (1);
}//error

// get sockaddr, IPv4 or IPv6:
void *get_in_addr(struct sockaddr *sa)
{
    if (sa->sa_family == AF_INET) {
        return &(((struct sockaddr_in*)sa)->sin_addr);
    }

    return &(((struct sockaddr_in6*)sa)->sin6_addr);
}//gwt_in_addr


int main(){
 
 struct addrinfo hints, *servinfo, *p;
 int cd,n,n1,rv,op=0;
 char *srv="127.0.0.1";
  
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;    /* Allow IPv4 or IPv6  familia de dir*/
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = 0;

    if ((rv = getaddrinfo(srv, pto, &hints, &servinfo)) != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
        return 1;
    }

    for(p = servinfo; p != NULL; p = p->ai_next) {
        if ((cd = socket(p->ai_family, p->ai_socktype,p->ai_protocol)) == -1) {
            perror("client: socket");
            continue;
        }

        if (connect(cd, p->ai_addr, p->ai_addrlen) == -1) {
            close(cd);
            perror("client: connect");
            continue;
        }

        break;
    }//for

    if (p == NULL) {
        fprintf(stderr, "client: error al conectar con el servidor\n");
        return 2;
    }

    freeaddrinfo(servinfo);
    printf("Conexionen establecida..esperando mensaje \n");
    //char *linea=(char *)malloc(sizeof(char)*100);
    char linea[100];
    bzero(linea,sizeof(linea)); 
    size_t tam;
    n = read(cd,linea,sizeof(linea));
    if(n<0){
     perror("Error en la funcion recv()\n");
    }else if(n==0){
     perror("Socket cerrado\n");
    }else{ 
     printf("%d bytes recibidos\nMensaje recibido: %s\n",n,linea);
    }//else
	close(cd);
	//free(linea);
	exit(0);

return 0;
}//main
