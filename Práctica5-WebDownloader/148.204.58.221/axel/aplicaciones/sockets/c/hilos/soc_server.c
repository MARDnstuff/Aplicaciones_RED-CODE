#define _REENTRANT
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <sys/uio.h>
#include <unistd.h>
#include <thread.h>

/* the TCP port that is used for this example */
#define TCP_PORT   6500

/* function prototypes and global variables */
void *do_chld(void *);
mutex_t lock;
int	service_count;

main()
{
	int 	sockfd, newsockfd, clilen;
	struct sockaddr_in cli_addr, serv_addr;
	thread_t chld_thr;

	if((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
		fprintf(stderr,"server: can't open stream socket\n"), exit(0);

	memset((char *) &serv_addr, 0, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	serv_addr.sin_port = htons(TCP_PORT);
	
	if(bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 
0)
		fprintf(stderr,"server: can't bind local address\n"), exit(0);

	/* set the level of thread concurrency we desire */
	thr_setconcurrency(5);

	listen(sockfd, 5);

	for(;;){
		clilen = sizeof(cli_addr);
		newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, 
&clilen);

		if(newsockfd < 0)
			fprintf(stderr,"server: accept error\n"), exit(0);

		/* create a new thread to process the incomming request */
		thr_create(NULL, 0, do_chld, (void *) newsockfd, THR_DETACHED, 
&chld_thr);

		/* the server is now free to accept another socket request */
		}
	return(0);
}

/* 
	This is the routine that is executed from a new thread 
*/

void *do_chld(void *arg)
{
int 	mysocfd = (int) arg;
char 	data[100];
int 	i;

	printf("Child thread [%d]: Socket number = %d\n", thr_self(), mysocfd);

	/* read from the given socket */
	read(mysocfd, data, 40);

	printf("Child thread [%d]: My data = %s\n", thr_self(), data);

	/* simulate some processing */
	for (i=0;i<1000000*thr_self();i++);

	printf("Child [%d]: Done Processing...\n", thr_self()); 

	/* use a mutex to update the global service counter */
	mutex_lock(&lock);

	service_count++;
	mutex_unlock(&lock);

	printf("Child thread [%d]: The total sockets served = %d\n", thr_self(), service_count);

	/* close the socket and exit this thread */
	close(mysocfd);
	thr_exit((void *)0);
}

