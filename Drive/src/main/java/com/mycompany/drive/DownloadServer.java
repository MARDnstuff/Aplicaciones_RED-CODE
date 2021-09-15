/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.drive;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author reyma
 */
public class DownloadServer {
    public static void main(String[] args){
        try{
            int pto = 1030;
            ServerSocket ss = new ServerSocket(pto);
            ss.setReuseAddress(true);
            System.out.println("Servidor iniciado - DESCARGAS");
            for(;;){
                //Aceptación de la conexion del cliente 
                Socket cl = ss.accept();
                cl.setReuseAddress(true);
                //Recepción del nombre del archivo que selecciono el cliente
                ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
                Objeto ob = (Objeto)ois.readObject();
                System.out.println("Nombre del archivo recibido: " + ob.getZ());

                //Obtenemos info para saber si es dierectorio o no
                ServerSocket sF = new ServerSocket(pto+1);
                  sF.setReuseAddress(true);
                Socket sFiles = sF.accept();
                DataOutputStream nFilesStream = new DataOutputStream(sFiles.getOutputStream());
                File f = new File(ob.getZ());
                String arch = f.getName();
                String dirname = "file";
                
                if(!f.isDirectory()){ //no es un directorio
                    nFilesStream.writeInt(1);
                    nFilesStream.flush();
                    nFilesStream.writeUTF(arch);
                    nFilesStream.flush();
                    String nombre = f.getName();
                    String path = f.getAbsolutePath();
                long tam = f.length();
                System.out.println("Preparandose pare enviar archivo "+path+" de "+tam+" bytes\n\n");
                //Servidor para archivos -- Cambio de flujo               
              
                System.out.println("Servidor para envio de archivos iniciado ...");              
                DataOutputStream fileOS = new DataOutputStream(sFiles.getOutputStream());
                DataInputStream fileIS = new DataInputStream(new FileInputStream(path));
                    fileOS.writeUTF(nombre);
                    fileOS.flush();
                    fileOS.writeLong(tam);
                    fileOS.flush();
                    long enviados = 0;
                    int l=0,porcentaje=0;
                    while(enviados<tam){
                        byte[] b = new byte[1500];
                        l=fileIS.read(b);
                        //loadingLabel.setText("Enviando archivo...("+porcentaje+"%)");
                        System.out.println("enviados: "+l);
                        fileOS.write(b,0,l);
                        fileOS.flush();
                        enviados = enviados + l;
                        porcentaje = (int)((enviados*100)/tam);
                        System.out.print("\rEnviado el "+porcentaje+" % del archivo");
                    }//while
                    System.out.println("\nArchivo enviado..");
                    //loadingLabel.setText("Archivo enviado");
                    fileIS.close();
                    fileOS.close();
                    sF.close();
                    cl.close();  
                    
                }else{//Si es un directorio
                    File[] files = f.listFiles();
                    dirname = f.getName();
                    nFilesStream.writeInt(files.length);
                    nFilesStream.flush();
                    nFilesStream.writeUTF(dirname);
                    nFilesStream.flush();
                    
                    for(int i=0 ; i<files.length;i++){
                        String nombre = files[i].getName();
                        String path = files[i].getAbsolutePath();
                        long tam = files[i].length();
                        System.out.println("Preparandose pare enviar archivo "+path+" de "+tam+" bytes\n\n");
                        //Servidor para carpetas
//                        ServerSocket sF = new ServerSocket(pto+1);
//                        sF.setReuseAddress(true);
//                        Socket sFiles = sF.accept();
                        System.out.println("Servidor para envio de archivos iniciado ...");  
                        DataOutputStream fileOS = new DataOutputStream(sFiles.getOutputStream());
                        DataInputStream fileIS = new DataInputStream(new FileInputStream(path));
                        fileOS.writeUTF(nombre);
                        fileOS.flush();
                        fileOS.writeLong(tam);
                        fileOS.flush();
                        long enviados = 0;
                        int l=0,porcentaje=0;
                        while(enviados<tam){
                            byte[] b = new byte[1500];
                            l=fileIS.read(b);
                            System.out.println("enviados: "+l);
                            fileOS.write(b,0,l);
                            fileOS.flush();
                            enviados = enviados + l;
                            porcentaje = (int)((enviados*100)/tam);
                            System.out.print("\rEnviado el "+porcentaje+" % del archivo");
                        }//while
                        //fileIS.close();
                        //fileOS.close();
                    }//for
                    
                        
                }
                
                

                 sF.close();
                 cl.close();                 
            }//for
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
