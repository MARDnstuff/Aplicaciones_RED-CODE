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
 * @author reyma
 */
public class MultiUploadServer {
    public static void main(String[] args){
      try{
          int pto = 1060;
          ServerSocket s = new ServerSocket(pto);
          
          s.setReuseAddress(true);
          System.out.println("Servidor iniciado esperando por archivos..");
          //Creación de carpeta en servidor y configuración
          File f = new File("");
            String ruta = "C:\\Users\\reyma\\Desktop\\Practicas_RED\\Practica_1\\Aplicaciones_RED-CODE";
            String carpeta="Cloud_Files";
            String ruta_archivos = ruta+"\\"+carpeta+"\\";
            System.out.println("ruta:"+ruta_archivos);
            File f2 = new File(ruta_archivos);
            f2.mkdirs();
            f2.setWritable(true);
 
            
            System.out.println("ruta:"+ruta_archivos);
           
          /////////////////////////////////////////////////
          
          for(;;){
              //Aceptamos conexion 
              Socket cl = s.accept();
              System.out.println("Cliente conectado desde "+cl.getInetAddress()+":"+cl.getPort());
              //Recepción del numero de archivos que selecciono el cliente
                ObjectInputStream dis = new ObjectInputStream(cl.getInputStream());
                Objeto ob = (Objeto)dis.readObject();
                int nFiles = ob.getX();
                System.out.println("Nombre del archivo recibido: " + ob.getX());
              
              //Proceso de descarga
             
              for(int i=0; i<nFiles; i++){               
                ServerSocket sF = new ServerSocket(pto+(i+1));
                sF.setReuseAddress(true);
                Socket sFiles = sF.accept();
                DataInputStream fileIS = new DataInputStream(sFiles.getInputStream());
                String nm = fileIS.readUTF();
                long tam = fileIS.readLong();
                String root = "C:\\Users\\reyma\\Desktop\\Practicas_RED\\Practica_1\\Aplicaciones_RED-CODE\\Cloud_Files\\"+nm;               
                System.out.println("Comienza descarga del archivo "+nm+" de "+tam+" bytes\n\n");
                DataOutputStream fileOS = new DataOutputStream(new FileOutputStream(root));
                long recibidos=0;                
                int l=0, porcentaje=0;
                while(recibidos<tam){
                    byte[] b = new byte[1500];
                    l = fileIS.read(b);
                    System.out.println("leidos: "+l);
                    fileOS.write(b,0,l);
                    fileOS.flush();
                    recibidos = recibidos + l;
                    porcentaje = (int)((recibidos*100)/tam);
                    System.out.print("\rRecibido el "+ porcentaje +" % del archivo");
                }//while
                System.out.println("Archivo recibido..");
                fileIS.close();
                 fileOS.close();
                 sFiles.close();
                 sF.close();
              }//for
              
              
              dis.close();              
              cl.close();
          }//for
          
      }catch(Exception e){
          e.printStackTrace();
      }  
    }//main
}
