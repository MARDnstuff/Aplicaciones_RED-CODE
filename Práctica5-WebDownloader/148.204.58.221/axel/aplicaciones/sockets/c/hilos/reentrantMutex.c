#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>//sleep()

pthread_mutex_t mt;
int i = 0;

void* fn1(void* agr)
{
  int err;

  pthread_mutex_lock(&mt);
  if((err = pthread_mutex_lock(&mt)) < 0)
  {
    printf("%s\n", strerror(err));
    exit(1);
  }

  ++i;
  printf("i= %d\n", i);

  //pthread_mutex_unlock(&mt);//-------②
  pthread_mutex_unlock(&mt);
}

void* fn2(void* arg)
{
  sleep(1);//The goal is for thread fn1 to execute first.
  pthread_mutex_lock(&mt);//-----------①
  ++i;
  printf("segunda funcion i= %d\n", i);
  pthread_mutex_unlock(&mt);
}

int main()
{
  pthread_t tid1, tid2;

  pthread_mutexattr_t mat;
  pthread_mutexattr_init(&mat);

  //The type of lock set is recursive
  pthread_mutexattr_settype(&mat, PTHREAD_MUTEX_RECURSIVE);
  pthread_mutex_init(&mt, &mat);
    
  pthread_create(&tid1, NULL, fn1, NULL);
  pthread_create(&tid2, NULL, fn2, NULL);

  pthread_join(tid1, NULL);
  pthread_join(tid2, NULL);

  pthread_mutex_destroy(&mt);
}
