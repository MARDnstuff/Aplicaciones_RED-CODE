#include <netdb.h>  //getaddrinfo() getnameinfo() freeaddrinfo()
#include <netinet/in.h> //htons
#include <string.h>  //bzero
#include <stdio.h>   //printf perror
#include <stdlib.h> //atoi() exit()
#include <sys/types.h>
#include <unistd.h>  //exit

struct datos{
char nombre[30];
char apellido[30];
short edad;
};

  int main(int argc, char* argv[]){
  int cd,n,rv,op=0;
  char *PUERTO="8000";
  struct sockaddr_in serverADDRESS;
  struct hostent *hostINFO;
  char *srv="2001::1234:2";
  FILE *f, *f1;  

int status;
struct addrinfo hints, *servinfo, *p;

    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;    /* Allow IPv4 or IPv6  familia de dir*/
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = 0;

    if ((rv = getaddrinfo(srv, PUERTO, &hints, &servinfo)) != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
        return 1;
    }

    for(p = servinfo; p != NULL; p = p->ai_next) {
        if ((cd = socket(p->ai_family, p->ai_socktype,p->ai_protocol)) == -1) {
            perror("client: socket");
            continue;
        }

	/*if (setsockopt(cd, IPPROTO_IPV6, IPV6_V6ONLY, (void *)&op, sizeof(op)) == -1) {
            perror("setsockopt   no soporta IPv6");
            exit(1);
        }*/

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

	freeaddrinfo(servinfo); // all done with this structure
	printf("\n Conexion establecida..\n");  
	    f1= fdopen(cd,"w+"); 
	    struct datos *d = (struct datos *)malloc(sizeof(struct datos));
	    char *tmp = (char *)malloc(sizeof(char)*30);
	    size_t tam;
	    memset(tmp,0,sizeof(tmp));
	    printf("\nEscribe un nombre:");
	    n = getline(&tmp,&tam,stdin);
	    strncpy(d->nombre,tmp,strlen(tmp));
	    printf("\nEscribe un apellido:");
	    memset(tmp,0,sizeof(tmp));
	    n = getline(&tmp,&tam,stdin);
	    strncpy(d->apellido,tmp,strlen(tmp));
	    printf("\nEscribe la edad:");
	    int ed;
	    scanf("%d",&ed);
	    fflush(stdin);
	    d->edad = htons(ed);
	    printf("\nEnviendo estructura con %d bytes, datos:\nNombre: %s\nApellido: %s\nEdad: %d\n",(int)sizeof(struct datos),d->nombre,d->apellido,ntohs(d->edad));
	    n = write(cd,(const char *)d,sizeof(struct datos));
	    fflush(f1);
	    printf("\nSe enviaron: %d bytes\n",n);
	    fclose(f1);
	    free(tmp);
	    free(d);
	    close(cd);

return 0;
}//main
