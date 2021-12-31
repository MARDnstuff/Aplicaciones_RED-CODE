#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> //getaddrinfo() getnameinfo() freeaddrinfo()
#include <string.h>
#include <unistd.h>//read
#include <stdlib.h>//exit
//#define pto "9999"

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
 char srv[50]="2001::1234:1";
char pto[10]="1234";
 char eco[2500];
  
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

FILE *f = fdopen(cd,"w+");
 printf("Conexionen establecida.. Escribe una serie de cadenas <enter> para enviar, SALIR para terminar\n");
 struct linger linger;
 linger.l_onoff = 1;
 linger.l_linger = 5;
 if(setsockopt(cd,SOL_SOCKET, SO_LINGER,(const char *)&linger,sizeof(linger))==-1){
  perror("error LINGER\n");
 }//if 

char *linea;
linea=NULL;
 
size_t tam; 
 tam=0;
//fflush(stdin);
while((n=getline(&linea,&tam,stdin))!=-1){
        printf("strlen da %d \n",(int)strlen(linea));
        if(strlen(linea)==1){
           printf("fflush\n");
          fflush(stdin);
          memset(linea,'\0',tam);
          continue;
        }
        linea[strlen(linea)-1]='\0';
       // char tmp[n+1];
       // bzero(tmp,sizeof(tmp));
       // strncpy(tmp,linea,n);
       // tmp[n]='\0';
       if(strncasecmp(linea,"SALIR",5)==0){
	printf("escribio SALIR\n");
        n1= send(cd,linea,strlen(linea),0);
        fflush(f);
	fclose(f);
	close(cd);
        free(linea);
        //linea=NULL;
        tam=0;
	exit(0);
	} else {
        printf("Preparado para enviar %d bytes, con el mensaje: %s\n",(int)strlen(linea),linea);
	n1= send(cd,linea,strlen(linea),0);
	printf("Se escribieron %d caracteres con el mensaje-> %s\n",n1,linea);
 	fflush(f);
	//bzero(linea,n*sizeof(char));
        memset(linea,'\0',tam);
	//char eco[n];
        memset(eco,'\0',2500);
 	n1=recv(cd,eco,2500,0);
        eco[strlen(eco)]='\0';
	printf("tamaño eco: %d\n",(int)strlen(eco));
        if(n1<0)
          error("Error al leer desde el socket\n");
        else if(n1==0)
          error("Socket cerrado\n");
        printf("%d bytes recibidos, con el ECO: %s\n",n1,eco);
       // bzero(tmp,sizeof(tmp));
	//free(eco);
        }//else

        fflush(stdin);
        //free(linea);
        //linea=NULL;
       // tam=0;
//       free(linea);
}//while
//free(linea);
tam=0;
return 0;
}//main
