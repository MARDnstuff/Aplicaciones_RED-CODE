/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>

 struct objeto{
 char nombre[30];
 char apellido[25];
 int edad;
};


void error(const char *msg)
{
    perror(msg);
    exit(1);
}

int main(int argc, char *argv[])
{
     int sockfd, newsockfd, portno;
     socklen_t clilen;
     char buffer[256];
     struct sockaddr_in serv_addr, cli_addr;
     struct objeto *o2;
     int n;
     if (argc < 2) {
         fprintf(stderr,"ERROR, falta escribir el puerto\n");
         exit(1);
     }
     sockfd = socket(AF_INET, SOCK_STREAM, 0);
     if (sockfd < 0) 
        error("ERROR al intentar abrir el socket");
     bzero((char *) &serv_addr, sizeof(serv_addr));
     portno = atoi(argv[1]);
     serv_addr.sin_family = AF_INET;
     serv_addr.sin_addr.s_addr = INADDR_ANY;
     serv_addr.sin_port = htons(portno);
     if (bind(sockfd, (struct sockaddr *) &serv_addr,
              sizeof(serv_addr)) < 0) 
              error("ERROR al ligar intentar realizar el bind");
     listen(sockfd,5);
     clilen = sizeof(cli_addr);
     newsockfd = accept(sockfd, 
                 (struct sockaddr *) &cli_addr, 
                 &clilen);
     if (newsockfd < 0) 
          error("ERROR al intentar realizar el accept");
     bzero(buffer,256);
     //n = read(newsockfd,);
     n = read(newsockfd,buffer,255);
     o2 = (struct objeto *)buffer;

     if (n < 0) error("ERROR al intentar leer del socket");
     //struct objeto ob3 = (struct objeto)buffer;
     //printf("nombre: %s",ob3.nombre);
     printf("Este es el mensaje:");
     printf("\nNombre: %s",o2->nombre);
     printf("\nApellido: %s",o2->apellido);
     printf("\nEdad: %d",ntohl(o2->edad));
     //n = write(newsockfd,"esto es un mensaje",18);
     //if (n < 0) error("ERROR writing to socket");
     close(newsockfd);
     close(sockfd);
     return 0; 
}
