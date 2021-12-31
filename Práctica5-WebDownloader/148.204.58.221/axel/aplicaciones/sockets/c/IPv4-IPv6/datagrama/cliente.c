#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>//getaddrinfo()
#include <stdio.h>  //printf, perror
#include <stdlib.h>  //exit()
#include <unistd.h> //read(), write()
#include <string.h>

#define BUF_SIZE 500

int
main(int argc, char *argv[])
{
    struct addrinfo dir;
    struct addrinfo *result, *rp;
    int sfd, s, j;
    size_t len;
    ssize_t nread;
    char buf[BUF_SIZE];

   if (argc < 3) {
        fprintf(stderr, "Sintaxis: %s host port msg...\n", argv[0]);
        exit(EXIT_FAILURE);
    }



   memset(&dir, 0, sizeof(struct addrinfo));
    dir.ai_family = AF_UNSPEC;    /* Allow IPv4 or IPv6 */
    dir.ai_socktype = SOCK_DGRAM; 
    dir.ai_flags = 0;
    dir.ai_protocol = 0;          

   s = getaddrinfo(argv[1], argv[2], &dir, &result);
    if (s != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(s));
        exit(EXIT_FAILURE);
    }

   for (rp = result; rp != NULL; rp = rp->ai_next) {
        sfd = socket(rp->ai_family, rp->ai_socktype,
                     rp->ai_protocol);
        if (sfd == -1)
            continue;

       if (connect(sfd, rp->ai_addr, rp->ai_addrlen) != -1)
            break;                  

       close(sfd);
    }

   if (rp == NULL) {               
        fprintf(stderr, "Could not connect\n");
        exit(EXIT_FAILURE);
    }

   freeaddrinfo(result);           

   /* envía cada palabra como datagramas separados */

   for (j = 3; j < argc; j++) {
        len = strlen(argv[j]) + 1;
                /* +1 for terminating null byte */

       if (len + 1 > BUF_SIZE) {
            fprintf(stderr,
                    "mensaje demasiado largo, ignorado %d\n", j);
            continue;
        }

       if (write(sfd, argv[j], len) != len) {
            fprintf(stderr, "error de escritura\n");
            exit(EXIT_FAILURE);
        }

       nread = read(sfd, buf, BUF_SIZE);
        if (nread == -1) {
            perror("read");
            exit(EXIT_FAILURE);
        }

       printf("%ld bytes recibidos: %s\n", (long) nread, buf);
    }

   exit(EXIT_SUCCESS);
}
