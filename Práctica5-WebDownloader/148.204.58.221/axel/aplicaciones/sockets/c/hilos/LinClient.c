#include <string.h>
#include <stdlib.h> //exit()
#include <errno.h>
#include <stdio.h>
#include <netinet/in.h>
#include <netdb.h> //getaddrinfo() getnameinfo() freeaddrinfo()
#include <sys/socket.h>
#include <unistd.h>//read
#define host_name "127.0.0.1"
#define host_port "1101"


int main(int argv, char** argc){
	struct addrinfo hints, *servinfo, *p;
	char buffer[1024];
	int cd,bytecount,buffer_len=0,err,rv,n,v=1;
	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;    /* Allow IPv4 or IPv6  familia de dir*/
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_protocol = 0;
	if ((rv = getaddrinfo(host_name, host_port, &hints, &servinfo)) != 0) {
	   fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
           return 1;
	}//if

	for(p = servinfo; p != NULL; p = p->ai_next) {
	    if ((cd = socket(p->ai_family, p->ai_socktype,p->ai_protocol)) == -1) {
                perror("client: socket");
                continue;
            }//if

	    /*if (setsockopt(cd, IPPROTO_IPV6, IPV6_V6ONLY, (void *)&op, sizeof(op)) == -1) {
                perror("setsockopt   no soporta IPv6");
                exit(1);
            }*/

            if (connect(cd, p->ai_addr, p->ai_addrlen) == -1) {
               close(cd);
               perror("Error en la funcion connect() \n");
               continue;
            }//if
            break;
	}//for

	if (p == NULL) {
           perror("Error al conectar con el servidor\n");
           return 2;
        }//if

	freeaddrinfo(servinfo); // all done with this structure
	//Now lets do the client related stuff
	buffer_len = 1024;
	memset(buffer, '\0', buffer_len);
	printf("Introduce el texto a ser enviado: (luego presiona enter):\n");
	fgets(buffer, 1024, stdin);
	buffer[strlen(buffer)-1]='\0';
	
	if( (bytecount=send(cd, buffer, strlen(buffer),0))== -1){
		fprintf(stderr, "Error en la funcion send(): %d\n", errno);
		exit(1);
	}
	printf("%d  bytes enviados..\n", bytecount);

	if((bytecount = recv(cd, buffer, buffer_len, 0))== -1){
		fprintf(stderr, "Error enla funcion recv() %d\n", errno);
		exit(1);
	}
	printf("%d  bytes recibidos..\nMensaje recibido: \"%s\"\n", bytecount, buffer);
	close(cd);
	return 0;
}//main
