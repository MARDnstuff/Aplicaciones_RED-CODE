/* Includes */
#include <unistd.h>     /* Symbolic Constants */
#include <sys/types.h>  /* Primitive System Data Types */ 
#include <errno.h>      /* Errors */
#include <stdio.h>      /* Input/Output */
#include <stdlib.h>     /* General Utilities */
#include <pthread.h>    /* POSIX Threads */
#include <string.h>     /* String handling */
#include <semaphore.h>  /* Semaphore */

/* prototype for thread routine */
void handler ( void *ptr );

/* global vars */
/* semaphores are declared global so they can be accessed 
   in main() and in thread routine,
   here, the semaphore is used as a mutex */
sem_t mutex;
int counter; /* shared variable */

int main()
{
    int i[2];
    pthread_t thread_a;
    pthread_t thread_b;
    
    i[0] = 0; /* argument to threads */
    i[1] = 1;
    
    sem_init(&mutex, 0, 1);      /* initialize mutex to 1 - binary semaphore */
                                 /* second param = 0 - semaphore is local */
                                                                
    pthread_create (&thread_a, NULL, (void *) &handler, (void *) &i[0]);
    pthread_create (&thread_b, NULL, (void *) &handler, (void *) &i[1]);
    
    pthread_join(thread_a, NULL);
    pthread_join(thread_b, NULL);

    sem_destroy(&mutex); /* destroy semaphore */
                  
    /* exit */  
    exit(0);
} /* main() */

void handler ( void *ptr )
{
    int x,valor; 
    x = *((int *) ptr);
    printf("Hilo %d: esperando entrar a su region critica...\n", x);
    sem_getvalue(&mutex,&valor);
    printf("El valor inicial del semaforo es: %d\n",valor);
    sem_wait(&mutex);       /* down semaphore */
    /* START CRITICAL REGION */
    printf("Hilo %d: adquirio un permiso del semaforo...\n", x);
    sem_getvalue(&mutex,&valor);
    printf("El valor del semaforo es: %d\n",valor);
    printf("Hilo %d: dentro de su region critica...\n", x);
    printf("Hilo %d: valor del contador: %d\n", x, counter);
    sleep(2);
    printf("Hilo %d: incrementa contador...\n", x);
    counter++;
    printf("Hilo %d: contador actualizado: %d\n", x, counter);
    printf("Hilo %d: Termina region critica...\n", x);
    /* END CRITICAL REGION */    
    sem_post(&mutex);       /* up semaphore */
    printf("Hilo %d: devolvio permiso del semaforo...\n", x);   
    pthread_exit(0); /* exit thread */
}
