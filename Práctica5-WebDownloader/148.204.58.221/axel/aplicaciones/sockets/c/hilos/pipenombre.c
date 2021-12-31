//pipenombre.c
#include <unistd.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <errno.h>
#include <fcntl.h>
#include <pthread.h>

#define WRITE_INTERVAL 500*1000 // 500 milliseconds

void* writer(void* fd);
void* reader(void* fd);

int main(int argc, char* argv[])
{
pthread_t readerThread;
pthread_t writerThread;
// Create named pipe
char pipeName[] = "/tmp/trypipe";
int ret_val = mkfifo(pipeName, 0666);
if ((ret_val == -1) && (errno != EEXIST)) {
perror("Error creating the named pipe");
}
// Open both ends within this process in on-blocking mode
// Must do like this otherwise open call will wait
// till other end of pipe is opened by another process
int readFd = open(pipeName, O_RDONLY|O_NONBLOCK);
int writeFd = open(pipeName, O_WRONLY|O_NONBLOCK);
// Now implement a reader and writer for these fds
// Launch Reader Thread
pthread_create( &readerThread, NULL, &reader, (void*) (&readFd));
pthread_create( &writerThread, NULL, &writer, (void*) (&writeFd));
pthread_join(readerThread, NULL);
pthread_join(writerThread, NULL);
// Use main thread for Writer (can invoke a separate thread instead)
//writer((void*) (&writeFd));
return 0;
}

void* writer(void* fd)
{
// Form descriptor
int writeFd = (*(int*)fd);
static int var = 0;
while(1) {
// Form data to be written on pipe
printf("\nWriter: %d", var);
// Do a simple write
write(writeFd, &var, sizeof(var)); ///
// Increment the variable, so that we can see the incremental communication
var++;  ///
// Give interval between next write
usleep(WRITE_INTERVAL);
}
}

void* reader(void* fd)
{
// Form descriptor
int readFd = (*(int*)fd);
fd_set readset;
int data=0, err = 0, size = 0;
// Initialize time out struct for select()
struct timeval tv;
tv.tv_sec = 0;
tv.tv_usec = 10000;
// Implement the receiver loop
while(1) {

//printf("\nREADING " STEP I…”);
// Initialize the set
FD_ZERO(&readset);
FD_SET(readFd, &readset);
// Now, check for readability
err = select(readFd+1, &readset, NULL, NULL, &tv);
if (err > 0 && FD_ISSET(readFd, &readset)) {
//printf(“\nREADING – STEP II – SUCCESS…”);
// Do a simple read on data
read(readFd, &data, sizeof(data));
// Dump read data
printf("\nReader: %d\n", data);
} else {
//printf(“\nREADING – STEP II – FAIL…”);
}
}

// Clear flags
FD_CLR(readFd, &readset);
}

