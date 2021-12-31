#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

pthread_mutex_t m= PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cv = PTHREAD_COND_INITIALIZER;

void *f1();
void *f2();
int cont = 0;

#define LIM 10
#define L1 3
#define L2 6

int main(){
	pthread_t t1, t2;
	pthread_create(&t1,NULL,f1,NULL);
	pthread_create(&t2,NULL,f2,NULL);
	pthread_join(t1,NULL);
	pthread_join(t2,NULL);
	printf("La cuenta final es: %d\n", cont);
	exit(0);
}

void *f1(){
	for(;;){
		pthread_mutex_lock(&m);
		pthread_cond_wait(&cv,&m);
		cont++;
		printf("cont incrementado desde f1(): %d\n", cont);
		pthread_mutex_unlock(&m);
		if(cont >= LIM)
			return(NULL);
	}
}

void *f2(){
	for(;;){
		pthread_mutex_lock(&m);
		if(cont < L1 || cont > L2){
			pthread_cond_signal(&cv);
		}
		else{
			cont++;
			printf("cont incremntado desde f2(): %d\n", cont);
		}
		pthread_mutex_unlock(&m);
		if(cont >= LIM)
			return(NULL);
	}
}