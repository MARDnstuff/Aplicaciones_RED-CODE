/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.drive;
import java.net.*;
import java.io.*;

/**
 *
 * @author Mauricio
 */
public class WritterServer {
    public static void main(String[] args){
        try{
            // Server for metadata
            int i;
            int pto = 1026;
            String rootPath = "C:\\Users\\Mauricio\\Documents\\ESCOM\\5semestre\\RedesII\\Cloud_Files\\";
            ServerSocket sMetadata = new ServerSocket(pto);
            sMetadata.setReuseAddress(true);
            System.out.println("Servidor de metadatos iniciado");
            String dirName;
            for(;;){
                // Accept client connection
                Socket clMetadata = sMetadata.accept();
                System.out.println("Cliente conectado desde "+clMetadata.getInetAddress()+":"+clMetadata.getPort());
                DataInputStream nFilesStream = new DataInputStream(clMetadata.getInputStream());
                int nFiles = nFilesStream.readInt();
                dirName = nFilesStream.readUTF();
                
                System.out.println("N.Files: "+nFiles);
                System.out.println("Dirname: "+dirName);
                
                if(!dirName.equals("file")){
                    File newDir = new File(rootPath+dirName);
                    newDir.mkdirs();
                    newDir.setWritable(true);
                    System.out.println("Filedir: "+newDir.getAbsolutePath());
                }

                //Server for files
                ServerSocket sFiles = new ServerSocket(pto+1);
                sFiles.setReuseAddress(true);
                System.out.println("Servidor de archivos iniciado");

                for(i = 0; i < nFiles; i++){
                    Socket clFiles = sFiles.accept();
                    System.out.println("Cliente conectado desde "+clFiles.getInetAddress()+":"+clFiles.getPort());
                    DataInputStream fileIS = new DataInputStream(clFiles.getInputStream());
                    String nombre = fileIS.readUTF();
                    long tam = fileIS.readLong();
                    System.out.println("Comienza descarga del archivo "+nombre+" de "+tam+" bytes\n\n");
                    DataOutputStream fileOS;
                    if(!dirName.equals("file")){
                        fileOS = new DataOutputStream(new FileOutputStream(rootPath+dirName+"\\"+nombre));
                    }
                    else{
                        fileOS = new DataOutputStream(new FileOutputStream(rootPath+nombre));
                    }
                    long recibidos=0;
                    int l=0, porcentaje=0;
                    while(recibidos<tam){
                        byte[] b = new byte[20000];
                        l = fileIS.read(b);
                        System.out.println("leidos: "+l);
                        fileOS.write(b,0,l);
                        fileOS.flush();
                        recibidos = recibidos + l;
                        porcentaje = (int)((recibidos*100)/tam);
                        System.out.print("\rRecibido el "+ porcentaje +" % del archivo");
                    }//while
                    System.out.println("Archivo recibido..");
                    fileOS.close();
                    fileIS.close();
                    clFiles.close();
                }//for
            }
        }catch(Exception e){
            e.printStackTrace();
        }  
    }
}
