/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.drive;
import java.net.*;
import java.io.*;
//Subir un archivo o carpeta 
/**
 *
 * @author Mauricio
 */
public class WritterServer {
    public static void main(String[] args){
        try{
            // Server for metadata
            int i;
            int pto = 1050;
            String rootPath = "C:\\Users\\reyma\\Desktop\\Practicas_RED\\Practica_1\\Aplicaciones_RED-CODE\\Cloud_Files\\";
            ServerSocket s = new ServerSocket(pto);
            s.setReuseAddress(true);
            System.out.println("Servidor de metadatos iniciado");
            String dirName;
            for(;;){
                // Accept client connection
                Socket cl = s.accept();
                System.out.println("Cliente conectado desde "+cl.getInetAddress()+":"+cl.getPort());
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                int nFiles = dis.readInt();
                dirName = dis.readUTF();
                
                System.out.println("N.Files: "+nFiles);
                System.out.println("Dirname: "+dirName);
                
                if(!dirName.equals("file")){
                    File newDir = new File(rootPath+dirName);
                    newDir.mkdirs();
                    newDir.setWritable(true);
                    System.out.println("Filedir: "+newDir.getAbsolutePath());
                }

                for(i = 0; i < nFiles; i++){
                    ServerSocket sF = new ServerSocket(pto+(i+1));
                    sF.setReuseAddress(true);
                    Socket sFiles = sF.accept();
                    DataInputStream fileIS = new DataInputStream(sFiles.getInputStream());
                    System.out.println("Cliente conectado desde "+sFiles.getInetAddress()+":"+sFiles.getPort());                   
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
                        byte[] b = new byte[2000];
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
                    sFiles.close();
                    sF.close();
                }//for
                dis.close();               
                cl.close();
            }//for
        }catch(Exception e){
            e.printStackTrace();
        }  
    }
}
