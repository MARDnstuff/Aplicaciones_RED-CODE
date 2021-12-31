#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/socket.h>
#include <netdb.h>
#include <arpa/inet.h>

#define BUF_SIZE 500

int
main(int argc, char *argv[])
{
    struct addrinfo dir;
    struct addrinfo *result, *rp;
    int sd, s,v=1;
    char hbuf[NI_MAXHOST], sbuf[NI_MAXSERV];
    struct sockaddr_storage peer_addr;
    socklen_t peer_addr_len, ctam;
    ssize_t nread;
    char buf[BUF_SIZE];

   if (argc != 2) {
        fprintf(stderr, "Sintaxis: %s port\n", argv[0]);
        exit(EXIT_FAILURE);
    }

   memset(&dir, 0, sizeof(struct addrinfo));
    dir.ai_family = AF_INET6;    /* Allow IPv4 or IPv6 */
    dir.ai_socktype = SOCK_DGRAM; /* Datagram socket */
    dir.ai_flags = AI_PASSIVE;    /* For wildcard IP address */
    dir.ai_protocol = 0;          /* Any protocol */
    dir.ai_canonname = NULL;
    dir.ai_addr = NULL;
    dir.ai_next = NULL;

   s = getaddrinfo(NULL, argv[1], &dir, &result);
    if (s != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(s));
        exit(EXIT_FAILURE);
    }


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

   printf("Servidor iniciado.. esperando datagramas...\n");
   for (;;) {
        peer_addr_len = sizeof(struct sockaddr_storage);
        nread = recvfrom(sd, buf, BUF_SIZE, 0,(struct sockaddr *) &peer_addr, &peer_addr_len);
        if (nread == -1)
            continue;               /* Ignore failed request */

       char host[NI_MAXHOST], service[NI_MAXSERV];

       s = getnameinfo((struct sockaddr *) &peer_addr,peer_addr_len, host, NI_MAXHOST,service, NI_MAXSERV, NI_NUMERICHOST | NI_NUMERICSERV);
       if (s == 0)
            printf(" %ld bytes recibidos desde %s:%s\n datos:%s\n",
                    (long) nread, host, service,buf);
        else
            fprintf(stderr, "getnameinfo: %s\n", gai_strerror(s));

       if (sendto(sd, buf, nread, 0,(struct sockaddr *) &peer_addr,peer_addr_len) != nread)
            fprintf(stderr, "Error al enviar respuesta\n");
    }
}
