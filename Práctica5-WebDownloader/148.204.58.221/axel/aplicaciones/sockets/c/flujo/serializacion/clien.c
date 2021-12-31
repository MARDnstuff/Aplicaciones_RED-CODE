#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> 

#define maxtam 2000

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
    exit(0);
}

int main(int argc, char *argv[])
{
    int sockfd, port=1101, n;
    char* host="127.0.0.1";
    struct sockaddr_in serv_addr;
    struct hostent *server;

    char buffer[256];
    char *buf = malloc(maxtam);
    struct informacion *info; //Se crea la instructura
    //Se inicializa la estructura
    info = (struct informacion *)malloc(sizeof(struct informacion)+(sizeof(int)*200));
    //info -> host_name = (char*)malloc(sizeof(16));
    strcpy(info -> host_name,host); //Se asigna el nombre de host
    info -> host_port = port; //Se asigna el puerto de comunicación
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) 
        error("ERROR al intentar abrir el socket");
    server = gethostbyname(host);
    if (server == NULL) {
        fprintf(stderr,"ERROR, no existe el host\n");
        exit(0);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    bcopy((char *)server->h_addr, 
         (char *)&serv_addr.sin_addr.s_addr,
         server->h_length);
    serv_addr.sin_port = htons(port);
    if (connect(sockfd,(struct sockaddr *) &serv_addr,sizeof(serv_addr)) < 0) 
        error("ERROR al intentar conectarse");
    
     info -> tamCubeta = htonl(200);
    int i;
    //info -> cubeta = (int*)malloc(200 * sizeof(int));
    for(i = 0; i < 200; i++){
        info -> cubeta[i] = rand() % 200;
        //printf("%d\n",info->cubeta[i]);
    }

    printf("%s\n", info->host_name);
    printf("%d\n",sizeof(info->cubeta));
    printf("Enviando la siguiente cubeta\n");
    int k;
    for(k=0;k< 200;k++)
        printf("cubeta[%d]= %d\n",k,info->cubeta[k]);
    n = write(sockfd, (const char*)info, sizeof(struct informacion)+ sizeof(int)*200);

    printf("%d - %d\n",sizeof(struct informacion),n);

    if(n < 0){
        error("ERROR al intentar escribir en el socket");
    }
    printf("termina de escribir");
    free(info);

    //printf("Escribe un mensaje: ");
    //bzero(buffer,256);
    //fgets(buffer,255,stdin);
    /*n= write(sockfd,(const char*)o1,sizeof(struct objeto));
    //n = write(sockfd,buffer,strlen(buffer));
    if (n < 0) 
         error("ERROR al intentar escribir en el socket");
    //bzero(buffer,256);
    //n = read(sockfd,buffer,255);
    //if (n < 0) 
    //     error("ERROR al intentar leer del socket");
    //printf("%s\n",buffer);
   free(o1); */
   close(sockfd);
    return 0;
}
