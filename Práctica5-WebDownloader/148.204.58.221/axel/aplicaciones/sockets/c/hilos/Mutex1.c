#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h> //sleep

pthread_mutex_t m1 = PTHREAD_MUTEX_INITIALIZER;
int cont = 0;

void msj() {
	printf("En espera del monitor de acceso\n ");
	return;
}

void *f() {
	//pthread_mutex_lock(&m1);
	/*if (!pthread_mutex_trylock(&m1)) {//Para que no deje de hacer nada el otro hilo.
		msj();
	}*/
	while(1){
	if (!pthread_mutex_trylock(&m1)) {//Para que no deje de hacer nada el otro hilo.
		msj();
		continue;
	} else{break;}
	}//while

	sleep(2);
	cont++;
	printf("El valor del contador es: %d\n", cont);
	pthread_mutex_unlock(&m1);
}


int main(int argc, char *argv[]) { 
	int r1, r2;
	pthread_t t1, t2;
	if((r1=pthread_create(&t1,NULL,f,NULL))){
		printf("Error al crear el hilo t1\n");
	}
	if((r2=pthread_create(&t2,NULL,f,NULL))){
		printf("Error al crear el hilo t2\n");
	}
	pthread_join(t1,NULL);
	pthread_join(t2,NULL);
	exit(0);
}
