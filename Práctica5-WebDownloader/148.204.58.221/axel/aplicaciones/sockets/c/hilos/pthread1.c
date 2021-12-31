//pthread1.c
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

void *imprime(void *p); //prototipo

void main(){
 pthread_t t1,t2;
 char *msj1="Hilo 1";
 char *msj2="Hilo 2";
 int r1,r2;
 r1 = pthread_create(&t1, NULL, imprime, (void*)msj1);
 r2 = pthread_create(&t2, NULL, imprime, (void*)msj2);
 pthread_join(t1,NULL);
 pthread_join(t2,NULL);
 printf("la creación de t1 devolvio %d\n",r1);
 printf("la creación de t2 devolvio %d\n",r2);
 exit(0);
}

void *imprime(void *p){
 char *mensaje=(char *)p;
 printf("%s \n",mensaje);
}//imprime
