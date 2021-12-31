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

  void error(char *s)
  {
   perror(s);
   exit(1);
  }

  int main(void)
  {
    struct sockaddr_in si_me, si_other;
    int s, i, slen=sizeof(si_other);
    char buf[BUFLEN];

    if ((s=socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP))==-1)
      error("socket");

    memset((char *) &si_me, 0, sizeof(si_me));
    si_me.sin_family = AF_INET;
    si_me.sin_port = htons(PORT);
    si_me.sin_addr.s_addr = htonl(INADDR_ANY);
    if (bind(s, (struct sockaddr *)&si_me, sizeof(si_me))==-1)
        error("bind");

    for (i=0; i<NPACK; i++) {
      if (recvfrom(s, buf, BUFLEN, 0,(struct sockaddr *)&si_other, &slen)==-1)
        error("recvfrom()");
      printf("Datagrama recibido desde -> %s:%d\nDatos: %s\n\n", 
             inet_ntoa(si_other.sin_addr), ntohs(si_other.sin_port), buf);
    }
 
     close(s);
     return 0;
  }
