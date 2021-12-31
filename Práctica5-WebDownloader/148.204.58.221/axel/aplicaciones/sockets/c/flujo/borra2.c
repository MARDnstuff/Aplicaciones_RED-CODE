#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<dirent.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

int main (int argc, char *argv[])
{
    if(2 != argc)
    {
        printf("\n Escribe el path de inicio \n");
        return 1;
    }

    DIR *dp = NULL;
    struct dirent *dptr = NULL;
    // Buffer for storing the directory path
    char buff[128];
    memset(buff,0,sizeof(buff));

    //copy the path set by the user
    strcpy(buff,argv[1]);

    // Open the directory stream
    if(NULL == (dp = opendir(argv[1])) )
    {
        printf("\n No se puede abrir el directorio [%s]\n",argv[1]);
        exit(1);
    }
    else
    {
        // Check if user supplied '/' at the end of directory name.
        // Based on it create a buffer containing path to new directory name 'newDir'
        if(buff[strlen(buff)-1]=='/')
        {
            strncpy(buff+strlen(buff),"newDir/",7);
        }
        else
        {
            strncpy(buff+strlen(buff),"/newDir/",8);
        }

        printf("\n Creando nuevo directorio [%s]\n",buff);
        // create a new directory
        mkdir(buff,S_IRWXU|S_IRWXG|S_IRWXO);
        printf("\n The contents of directory [%s] are as follows \n",argv[1]);
        // Read the directory contents
        while(NULL != (dptr = readdir(dp)) )
        {
            printf(" [%s] ",dptr->d_name);
        }
        // Close the directory stream
        closedir(dp);
        // Remove the new directory created by us
        rmdir(buff);
        printf("\n");
	char *buf2 ="./nada/dir";
	unlink(buf2);
    }

    return 0;
}
