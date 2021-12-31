/*Lista.c*/
#include <unistd.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

/* the number of threads */
int num_threads;

/* a linked list node */
struct node_t {
    int data;
    struct node_t* next;
};

/* add an element at front of list */
void list_insert(struct node_t** head, int data) {
    struct node_t* new = malloc(sizeof(struct node_t));
    new->data = data;
    new->next = *head;
    *head = new;
}

/* remove the first instance of an element from a list */
void list_remove(struct node_t** head, int data) {
    struct node_t* previous = NULL;
    struct node_t* current = *head;
    while (current) {
        if (current->data == data) {
            if (previous) {
                previous->next = current->next;
            } else {
                *head = current->next;
            }
            free(current);
            return;
        }
        current = current->next;
    }
}

/* print a linked list to the screen */
void list_print(struct node_t* head) {
    struct node_t* current = head;
    while (current) {
        printf("%d ", current->data);
        current = current->next;
    }
    printf("\n");
}

/* our list is global */
struct node_t* head = NULL;

/* thread function which inserts and removes elements */
void* writer(void* idp) {
    int seed = *((int*) idp);

    /* continually insert and remove items */
    while (1) {
        list_insert(&head, rand_r(&seed) % 100);
        list_remove(&head, rand_r(&seed) % 100);
        list_remove(&head, rand_r(&seed) % 100);
        list_remove(&head, rand_r(&seed) % 100);
    }

    pthread_exit(NULL);
}

/* thread function which tests if elements are in the list */
void* reader(void* idp) {
    /* continually print the list */
    while (1) {
        list_print(head);
    }

    pthread_exit(NULL);
}

int main (int argc, char** argv) {
    /* get the number of threads */
    if (argc < 2) {
        printf("Pass the number of threads to use!\n");
        pthread_exit(0);
    } else {
        num_threads = atoi(argv[1]);
    }

    /* an array of threads */
    pthread_t threads[2 * num_threads];
    int i;

    /* spawn all writer threads */
    int seeds[num_threads];
    for (i = 0; i < num_threads; i++) {
        seeds[i] = rand();
        pthread_create(&threads[i], NULL, writer, &seeds[i]);
    }

    /* spawn all reader threads */
    for (i = num_threads; i < 2 * num_threads; i++) {
        pthread_create(&threads[i], NULL, reader, NULL);
    }

    /* join all threads collecting answer */
    for (i = 0; i < 2 * num_threads; i++) {
        pthread_join(threads[i], (void**) NULL);
    }

    pthread_exit(0);
}
