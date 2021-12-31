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

 void error(char *s)
  {
   perror(s);
   exit(1);
  }


  int main(void)
  {
    struct sockaddr_in si_other;
    int s, i, slen=sizeof(si_other);
    char buf[BUFLEN];

    if ((s=socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP))<0)
      error("socket");

    memset((char *) &si_other, 0, sizeof(si_other));
    si_other.sin_family = AF_INET;
    si_other.sin_port = htons(PORT);
    if (inet_aton(SRV_IP, &si_other.sin_addr)==0) {
      fprintf(stderr, "error en la funcion inet_aton() \n");
      exit(1);
    }

    for (i=0; i<NPACK; i++) {
      printf("Enviando el datagrama %d\n", i);
      sprintf(buf, "Datagrama numero %d\n", i);
      if (sendto(s, buf, BUFLEN, 0, (struct sockaddr *)&si_other, slen)==-1)
        error("sendto()");
    }

    close(s);
    return 0;
  }
