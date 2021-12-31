#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#define BUFLEN 512
#define NPACK 10
#define PORT 9930

#define SRV_IP "127.0.0.1"

struct objeto{
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
    struct sockaddr_in si_other;
    int s, i, slen=sizeof(si_other);
    char buf[BUFLEN];

    if ((s=socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP))==-1)
      diep("socket");

    memset((char *) &si_other, 0, sizeof(si_other));
    si_other.sin_family = AF_INET;
    si_other.sin_port = htons(PORT);
    if (inet_aton(SRV_IP, &si_other.sin_addr)==0) {
      fprintf(stderr, "inet_aton() failed\n");
      exit(1);
    }
      struct objeto *o1;      o1 = (struct objeto *)malloc(sizeof (struct objeto));
     printf("Escribe el nombre:");
     gets(o1->nombre);
     printf("Escribe el apellido:");
     gets(o1->apellido);
     printf("Escribe la edad:");
     int ed;
     scanf("%d",&ed);
     fflush(stdin);
     o1->edad=htonl(ed);
  



      printf("Sending packet\n");
      //sprintf(buf, "This is packet %d\n", i);
      if (sendto(s, (const char*)o1, sizeof(struct objeto), 0, (struct sockaddr *)&si_other, slen)==-1)
        diep("sendto()");
   
     free(o1);
    close(s);
    return 0;
  }
