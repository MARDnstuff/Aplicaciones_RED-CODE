#include <netdb.h> //getaddrinfo() getnameinfo() freeaddrinfo()
#include <string.h>
#include <stdlib.h>//exit()
#include <stdio.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>//read()
#include <pthread.h>
#define host_port "1101"

void* SocketHandler(void*);

int main(int argv, char** argc){
	char hbuf[NI_MAXHOST], sbuf[NI_MAXSERV];
	int sd,err,rv,n,v=1,op=0;
	socklen_t ctam = 0;
	int* cd;
	pthread_t thread_id=0;
	struct addrinfo hints, *servinfo, *p;
	struct sockaddr_storage their_addr; // connector's address 
 	ctam = sizeof(their_addr);
	memset(&hints, 0, sizeof (hints));  //indicio
	hints.ai_family = AF_INET6;    /* Allow IPv4 or IPv6  familia de dir*/
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE; // use my IP
	hints.ai_protocol = 0;          /* Any protocol */
	hints.ai_canonname = NULL;
	hints.ai_addr = NULL;
	hints.ai_next = NULL;
	if ((rv = getaddrinfo(NULL, host_port, &hints, &servinfo)) != 0) {
	    fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
	    return 1;
	}//if
	for(p = servinfo; p != NULL; p = p->ai_next) {
            if ((sd = socket(p->ai_family, p->ai_socktype,p->ai_protocol)) == -1) {
               perror("Error en funcion socket()\n");
               continue;
            }//if
            if (setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &v,sizeof(int)) == -1) {
               perror("error en opcion SO_REUSEADDR \n");
               exit(1);
            }//if
	    if (setsockopt(sd, IPPROTO_IPV6, IPV6_V6ONLY, (void *)&op, sizeof(op)) == -1) {
               perror("setsockopt   no soporta IPv6");
               exit(1);
            }//if
            if (bind(sd, p->ai_addr, p->ai_addrlen) == -1) {
               close(sd);
               perror("El puerto ya esta en uso \n");
               continue;
            }//if
            break;
        }//for

	freeaddrinfo(servinfo); // all done with this structure
	if (p == NULL)  {
           fprintf(stderr, "servidor: error en bind\n");
           exit(1);
        }//if
	listen(sd,5);
	printf("Servidor listo.. Esperando clientes \n");
	
	while(1){
		printf("Esperando un cliente..\n");
		cd = (int*)malloc(sizeof(int));
		if((*cd = accept( sd, (struct sockaddr*)&their_addr, &ctam))!= -1){
		  if (getnameinfo((struct sockaddr *)&their_addr, sizeof(their_addr), hbuf, sizeof(hbuf), sbuf,sizeof(sbuf), NI_NUMERICHOST | NI_NUMERICSERV) == 0)
	             printf("cliente conectado desde %s:%s\n", hbuf,sbuf);
		     pthread_create(&thread_id,0,&SocketHandler, (void*)cd );
		     pthread_detach(thread_id);
		}else{
			perror("Error en funcion accept()\n");
			free(cd);
			continue;
		}//else
	}//while
	free(cd);
	return 0;
}//main

void* SocketHandler(void* lp){
    int *cd = (int*)lp;

	char buffer[1024];
	int buffer_len = 1024;
	int bytecount;

	memset(buffer, 0, buffer_len);
	if((bytecount = recv(*cd, buffer, buffer_len, 0))== -1){
		perror("Error en funcion recv()\n");
		free(cd);
		exit(1);
	}//if
	printf(" %d bytes recibidos..\nMensaje recibido: \"%s\"\n", bytecount, buffer);
	strcat(buffer, " -ECO-");
	if((bytecount = send(*cd, buffer, strlen(buffer), 0))== -1){
	    perror("Error en la funcion send()\n");
	    free(cd);
	    exit(1);
	}	
	printf("%d  bytes enviados....\n", bytecount);
}//SocketHandler
