	#include <netdb.h>
	#include <netinet/in.h>
	#include <string.h>
	#include <stdio.h>
	
	#include <sys/types.h>
	#include <sys/stat.h>
	#include <unistd.h>
	 
	 
	int fileSEND(char *server, int PORT, char *lfile, char *rfile){
	    int socketDESC;
	    struct sockaddr_in serverADDRESS;
	    struct hostent *hostINFO;
	    FILE * file_to_send;
	    int ch;
	    char toSEND[1];
	    char remoteFILE[4096];
	    int count1=1,count2=1, percent;
	 
	 
	    hostINFO = gethostbyname(server);
	    if (hostINFO == NULL) {
	        printf("Problema al interpretar el host\n");
	        return 1;
	    }
	 
	    socketDESC = socket(AF_INET, SOCK_STREAM, 0);
	    if (socketDESC < 0) {
	        printf("No fue posible crear el socket\n");
	        return 1;
	    }
	 
	    serverADDRESS.sin_family = hostINFO->h_addrtype;
	    memcpy((char *) &serverADDRESS.sin_addr.s_addr,    hostINFO->h_addr_list[0], hostINFO->h_length);
	    serverADDRESS.sin_port = htons(PORT);
	                 
	    if (connect(socketDESC, (struct sockaddr *) &serverADDRESS, sizeof(serverADDRESS)) < 0) {
	        printf("No fue posible establecer la conexion\n");
	        return 1;
	    }
	 
	 
	    file_to_send = fopen (lfile,"rb");
	    if(!file_to_send) {
	        printf("Error al intentar abrir el archivo file\n");
	        close(socketDESC);
	        return 0;
	        } else {
	    long fileSIZE;
//	   struct stat st;
//	   fstat (file_to_send,&st);
//	   int tam = st.st_size;
	  int prev=ftell(file_to_send);
	  fseek(file_to_send,0L,SEEK_END);
	  int sz = ftell(file_to_send);
	  fseek(file_to_send,prev,SEEK_SET);


	   printf("tam: %d",sz);

	    fseek (file_to_send, 0, SEEK_END);     fileSIZE =ftell (file_to_send);
	    rewind(file_to_send);
	 
	    sprintf(remoteFILE,"FBEGIN:%s:%d\r\n", rfile, fileSIZE);
	    send(socketDESC, remoteFILE, sizeof(remoteFILE), 0);
	 
	    percent = (int)(fileSIZE / 100);
	    while((ch=getc(file_to_send))!=EOF){
	        toSEND[0] = ch;
	        send(socketDESC, toSEND, 1, 0);
	        if( count1 == count2 ) {
	            printf("33[0;0H");
	            printf( "\33[2J");
	            printf("Filename: %s\n", lfile);
	            printf("Filesize: %d Kb\n", sz);
	            printf("Percent : %d%% ( %d Kb)\n",count1 / percent ,count1 / 1024);
	            count1+=percent;
	        }
	        count2++;
	 
	    }
	    }
	    fclose(file_to_send);
	    close(socketDESC);
	 
	    return 0;
	}
	 
	int main(void)
	{
	     
	    fileSEND("localhost", 31337, "/bin/tar", "archivo2.txt");
	    return 0;
	}
