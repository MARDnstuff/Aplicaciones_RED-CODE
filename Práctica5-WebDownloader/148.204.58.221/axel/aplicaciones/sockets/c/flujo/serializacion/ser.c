/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>

//Estructura que contiene la información que se le pasara al socket
struct informacion{
    char host_name[16]; //Direccion del servidor
    int host_port; //Puerto por el que se comunicaran
    int tamCubeta; //Tamaño de la cubeta
    int cubeta[1]; //Cubeta con los elementos a ordenar
};


void error(const char *msg)
{
    perror(msg);
    exit(1);
}

int main(int argc, char *argv[])
{
     int sockfd, newsockfd, port = 1101;
     socklen_t clilen;
     char buffer[1000];
     bzero(buffer, sizeof(buffer));
     struct sockaddr_in serv_addr, cli_addr;
     struct informacion *info;
     //info =(struct informacion *) malloc((sizeof(struct informacion)+(sizeof(int)*200)));
     int n;
     printf("tam struct informacion: %d\n",sizeof (info));
     sockfd = socket(AF_INET, SOCK_STREAM, 0);
     if (sockfd < 0) 
        error("ERROR al intentar abrir el socket");
     bzero((char *) &serv_addr, sizeof(serv_addr));
     serv_addr.sin_family = AF_INET;
     serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
     serv_addr.sin_port = htons(port);
     if (bind(sockfd, (struct sockaddr *) &serv_addr,
              sizeof(serv_addr)) < 0) 
              error("ERROR al ligar intentar realizar el bind");
     listen(sockfd,5);
     clilen = sizeof(cli_addr);
     newsockfd = accept(sockfd,(struct sockaddr *) &cli_addr, &clilen);
     if (newsockfd < 0) 
          error("ERROR al intentar realizar el accept");
     bzero(buffer,sizeof(buffer));
     printf("Cliente conectado.. intentando leer datos del socket..\n");
     n = read(newsockfd,buffer,(sizeof(struct informacion)+(sizeof(int)*200)));
   
     if (n < 0) error("ERROR al intentar leer del socket");
    
     printf("%d  bytes leidos..\n",n);
     info = (struct informacion *)buffer;

     printf("HOST: %s\n",info->host_name);
     printf("PUERTO: %d\n",info->host_port);
     int i;
     int pru =  ntohl(info -> tamCubeta);
     printf("TAM Cubeta: %d\n",pru);
    for (i = 0; i < pru; i++)
    {
        printf("ELEMENTO[%d]: %d\n",i,info->cubeta[i]);
    }
     //free(info);
     close(newsockfd);
     close(sockfd);
     return 0; 
}
