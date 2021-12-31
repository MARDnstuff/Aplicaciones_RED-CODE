//#include <arpa/inet.h>
//#include <netinet/in.h>
#include <stdio.h> //printf
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h> //close()
#include <stdlib.h> //exit()
#include <string.h>
#include <netdb.h>//getaddrinfo()
#define BUFLEN 256
#define PORT "9930"

 struct dato{
 char nombre[30];
 char apellido[25];
 int edad;
};

  void diep(char *s)
  {
   perror(s);
   exit (0);
  }

  int main(void)
  {
    struct addrinfo hints;
    struct addrinfo *result, *rp;
    char host[NI_MAXHOST], service[NI_MAXSERV];
    struct sockaddr_storage emisor;
    struct dato *o2;
    int sd, i,v=1;
    socklen_t ctam;
    char buf[BUFLEN];
    ssize_t n;

    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_family = AF_INET6;    /* Allow IPv4 or IPv6 */
    hints.ai_socktype = SOCK_DGRAM; /* Datagram socket */
    hints.ai_flags = AI_PASSIVE;    /* For wildcard IP address */
    hints.ai_protocol = 0;          /* Any protocol */
    hints.ai_canonname = NULL;
    hints.ai_addr = NULL;
    hints.ai_next = NULL;

   i = getaddrinfo(NULL, PORT, &hints, &result);
    if (i != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(i));
        exit(EXIT_FAILURE);
    }

   /* getaddrinfo() returns a list of address structures.
       Try each address until we successfully bind(2).
       If socket(2) (or bind(2)) fails, we (close the socket
       and) try the next address. */

   for (rp = result; rp != NULL; rp = rp->ai_next) {
        sd = socket(rp->ai_family, rp->ai_socktype,
                rp->ai_protocol);
        if (sd == -1)
            continue;

	int op = 0;
        int r = setsockopt(sd, IPPROTO_IPV6, IPV6_V6ONLY, &op, sizeof(op));
	if (setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &v,sizeof(int)) == -1) {
            perror("setsockopt");
            exit(1);
        }

	
       if (bind(sd, rp->ai_addr, rp->ai_addrlen) == 0)
	  break;
                  /* Success */

       close(sd);
    }

   if (rp == NULL) {               /* No address succeeded */
        fprintf(stderr, "Could not bind\n");
        exit(EXIT_FAILURE);
    }

   freeaddrinfo(result);           /* No longer needed */

   ctam = sizeof(struct sockaddr_storage);
      if ((n=recvfrom(sd, buf, BUFLEN, 0,(struct sockaddr *)&emisor, &ctam))==-1)
        diep("recvfrom()");
        o2 = (struct dato *)buf;

       i = getnameinfo((struct sockaddr *) &emisor,ctam, host, NI_MAXHOST,service, NI_MAXSERV, NI_NUMERICSERV|NI_DGRAM);
       if (i == 0){
            printf(" %ld bytes recibidos desde %s puerto%s\n datos:\n",(long) n, host, service);

       printf("Este es el mensaje:");
       printf("\nNombre: %s",o2->nombre);
       printf("\nApellido: %s",o2->apellido);
       printf("\nEdad: %d\n\n",ntohl(o2->edad));
       }else{
	fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(i));
        exit(EXIT_FAILURE);
       }
 
     close(sd);
     return 0;
  }
