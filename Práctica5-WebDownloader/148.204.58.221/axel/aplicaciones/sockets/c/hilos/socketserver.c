#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <signal.h>

#define SOCKTESTING
#define BACKLOG 10

void *talker( void *d ) {
	int i = 0;
	char buf[1024];
	int fd;

	fd = (int)d;

	while( 1 ) {
		sprintf( buf, "%d\n", i++ );
		write( fd, buf, strlen( buf )+1 );
		sleep(4);
	}
}

int bindsockets() {
	int ss,s1,s2,n;
	fd_set sockfds;
	struct timeval tv;
	char buf1[256], buf2[256];
	struct sockaddr_in my_addr;
	struct sockaddr_in socks[50];
	pthread_t thread[50];
	int i=1;

	snprintf( buf1, 14, "Hello world" );

	ss = socket( PF_INET, SOCK_STREAM, 0 );
	my_addr.sin_family = AF_INET; // host byte order
	my_addr.sin_port = htons( 12345 ); // short network byte order
	memset( &(my_addr.sin_zero), '\0', 8 ); // zero the rest
	setsockopt( ss, SOL_SOCKET, SO_REUSEADDR, &i, sizeof(int) );

	bind( ss, (struct sockaddr* )&my_addr, sizeof( struct sockaddr ) );

	listen( ss, BACKLOG );

	n = sizeof( struct sockaddr_in );
	i=0;
	while( 1 ) {
		s1 = accept( ss, (struct sockaddr *)&socks[1], &n );
		pthread_create( &(thread[i++]), NULL, talker, (void *)s1 );
	}

	return(0);
}

#ifdef SOCKTESTING
int main( int argc, char *argv[] ) {
	bindsockets();

	return(0);
}
#endif
