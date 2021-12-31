//#include <arpa/inet.h>
//#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h> //close()
#include <stdlib.h> //malloc() free()
#include <string.h>  //memset()
#include <netdb.h>  //getaddrinfo()
#include <stdio.h> //gets() getline()
#define BUFLEN 512
#define PORT "9930"
#define SRV_IP "127.0.0.1"
#define SRV_IPP "192.168.1.71"
#define SRV_IP6 "2001::1234:1"
#define BC "255.255.255.255"
struct dato{
 char nombre[30];
 char apellido[25];
 int edad;
};


 void diep(char *s)
  {
   perror(s);
   exit(1);
  }


  int main(void)
  {
 
    struct addrinfo dir;
    struct addrinfo *result, *rp;
    int s,cd, i;
    char buf[BUFLEN];
    char hbuf[NI_MAXHOST], sbuf[NI_MAXSERV];
    memset(&dir, 0, sizeof(struct addrinfo));
    dir.ai_family = AF_UNSPEC;    /* Allow IPv4 or IPv6 */
    dir.ai_socktype = SOCK_DGRAM; /* Datagram socket */
    dir.ai_flags = 0;
    dir.ai_protocol = 0;          /* Any protocol */

   s = getaddrinfo(SRV_IP6, PORT, &dir, &result);
    if (s != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(s));
        exit(EXIT_FAILURE);
    }//if

 
   for (rp = result; rp != NULL; rp = rp->ai_next) {
        cd = socket(rp->ai_family, rp->ai_socktype,rp->ai_protocol);
        if (cd == -1)
            continue;
	break;
     
    //}

     if (rp == NULL) {               /* No address succeeded */
        fprintf(stderr, "no hay direcciones disponibles\n");
        exit(EXIT_FAILURE);
      }//if
    }//for
   freeaddrinfo(result);           /* No longer needed */
   
     struct dato *o1;      
     o1 = (struct dato *)malloc(sizeof (struct dato));
     char *tmp=(char *)malloc(sizeof(char)*30);
     bzero(tmp,sizeof(tmp));
     size_t tam;
     printf("Escribe el nombre:");
     //gets(o1->nombre);
     i=getline(&tmp,&tam,stdin);
     strncpy(o1->nombre,tmp, strlen(tmp));
     printf("Escribe el apellido:");
     bzero(tmp,sizeof(tmp));
     i=getline(&tmp,&tam,stdin);
     strncpy(o1->apellido,tmp, strlen(tmp));
     //gets(o1->apellido);
     printf("Escribe la edad:");
     int ed;
     scanf("%d",&ed);
     fflush(stdin);
     o1->edad=htonl(ed);
     printf("Enviando datagrama..\n");
      char *x="mensaje";
//////////////////////////////
      int bc=1;
      int p = setsockopt(cd, SOL_SOCKET, SO_BROADCAST, &bc, sizeof(bc));
      printf("p: %d\n",p);
   struct addrinfo dst;
   memset(&dst,0,sizeof(dst));
   dst.ai_family   = result->ai_family;
   dst.ai_socktype = SOCK_DGRAM;
   struct addrinfo *result1;
   if ( getaddrinfo(BC, "9930", &dst, &result1) != 0 )
   //if ( getaddrinfo(SRV_IP, "9930", &dst, &result1) != 0 )
   //if ( getaddrinfo(SRV_IP6, "9930", &dst, &result1) != 0 )
    {
        perror("getaddrinfo3() failed");
    }//if

   if (sendto(cd, (const char*)o1, sizeof(struct dato), 0, (struct sockaddr *)result1->ai_addr, result1->ai_addrlen)==-1){
        diep("sendto()");  }
     free(o1);
    close(cd);
    return 0;
  }
