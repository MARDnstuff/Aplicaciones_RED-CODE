#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#define BUFLEN 256
#define NPACK 10
#define PORT 9930

 struct objeto{
 char nombre[30];
 char apellido[25];
 int edad;
};

  void error(char *s)
  {
   perror(s);
   exit (0);
  }

  int main(void)
  {
    struct sockaddr_in si_me, si_other;
    struct objeto *o2;
    int s, i, slen;
    char buf[BUFLEN];
    slen=sizeof(si_other);


    if ((s=socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP))==-1)
      error("socket");

    memset((char *) &si_me, 0, sizeof(si_me));
    si_me.sin_family = AF_INET;
    si_me.sin_port = htons(PORT);
    si_me.sin_addr.s_addr = htonl(INADDR_ANY);
    if (bind(s,( struct sockaddr *)&si_me, sizeof(si_me))==-1)
        error("bind");

   
      if (recvfrom(s, buf, BUFLEN, 0,(struct sockaddr *)&si_other, &slen)==-1)
        error("recvfrom()");
        o2 = (struct objeto *)buf;

       printf("Paquete recibido desde %s:%d\n\n", 
             inet_ntoa(si_other.sin_addr), ntohs(si_other.sin_port));
       printf("Este es el mensaje:");
       printf("\nNombre: %s",o2->nombre);
       printf("\nApellido: %s",o2->apellido);
       printf("\nEdad: %d\n",ntohl(o2->edad));

    
 
     close(s);
     return 0;
  }
