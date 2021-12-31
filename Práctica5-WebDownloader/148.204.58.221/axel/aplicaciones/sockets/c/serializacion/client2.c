#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> //gethostbyname, getaddrinfo
#define maxtam 2000

 struct objeto{
 char nombre[30];
 char apellido[25];
 int edad;
};


void error(const char *msg)
{
    perror(msg);
    exit(0);
}

int main(int argc, char *argv[])
{
    int sockfd, n;
    struct sockaddr_in serv_addr;
    //struct hostent *server;

    char buffer[256];
    char *buf = malloc(maxtam);
    char *host=argv[1];
    char *portno = argv[2];
    struct objeto *o1;
     o1 = (struct objeto *)malloc(sizeof (struct objeto));
    //o1 = buf;

    if (argc < 3) {
       fprintf(stderr,"sintaxis: %s hostname puerto\n", argv[0]);
       exit(0);
    }

    int status;
    struct addrinfo hints;
    struct addrinfo *servinfo; // will point to the results
    memset(&hints, 0, sizeof(hints)); // make sure the struct is empty
    hints.ai_family = AF_UNSPEC; // don't care IPv4 or IPv6
    hints.ai_socktype = SOCK_STREAM; // TCP stream sockets
    // get ready to connect
    status = getaddrinfo(host, portno, &hints, &servinfo);
    int cd = socket(servinfo->ai_family,servinfo->ai_socktype,servinfo->ai_protocol);


FILE *f = fdopen(cd,"w+");
//if (connect(cd,(struct sockaddr *)&sdir,sizeof(sdir))<0){
if (connect(cd,servinfo->ai_addr,servinfo->ai_addrlen)<0){
 perror("error en funcion connect()\n");
}//if
freeaddrinfo(servinfo);
printf("\n Conexion establecida.. enviando datos..\n");

  /********************************/
     //o1 = new (struct objeto);
     printf("Escribe el nombre:");
     //gets(o1->nombre);
     fgets(o1->nombre,sizeof(o1->nombre),stdin);
     printf("Escribe el apellido:");
//     gets(o1->apellido);
     fgets(o1->apellido,sizeof(o1->apellido),stdin);
     printf("Escribe la edad:");
     int ed;
     scanf("%d",&ed);
     fflush(stdin);  
     o1->edad=htonl(ed);
    /********************************/

    //printf("Escribe un mensaje: ");
    //bzero(buffer,256);
    //fgets(buffer,255,stdin);
    n= write(sockfd,(const char*)o1,sizeof(struct objeto));
    //n = write(sockfd,buffer,strlen(buffer));
    if (n < 0) 
         error("ERROR al intentar escribir en el socket");
    //bzero(buffer,256);
    //n = read(sockfd,buffer,255);
    //if (n < 0) 
    //     error("ERROR al intentar leer del socket");
    //printf("%s\n",buffer);
   free(o1); 
   close(sockfd);
    return 0;
}
