//semaforo.c
#include <semaphore.h>
#include <pthread.h>
#include <stdio.h>

#define THREADS 20

sem_t OKToBuyMilk;
int milkAvailable;

void* buyer(void *arg)
{
    // P()
    sem_wait(&OKToBuyMilk);
    if(!milkAvailable)
    {
        // Buy some milk
        ++milkAvailable;
    }
    // V()
    sem_post(&OKToBuyMilk);

    return NULL;
}

int main(int argc, char **argv)
{
    pthread_t threads[THREADS];

    milkAvailable = 0;

    // Initialize the semaphore with a value of 1.
    // Note the second argument: passing zero denotes
    // that the semaphore is shared between threads (and
    // not processes).
    if(sem_init(&OKToBuyMilk, 0, 1))
    {
        printf("Could not initialize a semaphore\n");
        return -1;
    }

    for(int i = 0; i < THREADS; ++i)
    {
        if(pthread_create(&threads[i], NULL, &buyer, NULL))
        {
            printf("Could not create thread %d\n", i);
            return -1;
        }
    }

    for(int i = 0; i < THREADS; ++i)
    {
        if(pthread_join(threads[i], NULL))
        {
            printf("Could not join thread %d\n", i);
            return -1;
        }
    }

    sem_destroy(&OKToBuyMilk);

    // Make sure we don't have too much milk.
    printf("Total milk: %d\n", milkAvailable);

    return 0;
}