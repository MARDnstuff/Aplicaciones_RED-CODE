package com.mycompany.drive;
import java.net.*;
import java.io.*;

/**
 *
 * @author reyma
 */
public class DeleteServer {
    public static void main(String[] args)throws Exception{
        try{
            int pto = 1045;
            ServerSocket ss = new ServerSocket(pto);
            ss.setReuseAddress(true);
            System.out.println("Servidor iniciado - DELETE");
             String Udir = "C:\\Users\\reyma\\Desktop\\Practicas_RED\\Practica_1\\FlujoArchivo\\Cloud_Files";
             for(;;){
                 
                 //Recepcion de datos
                  Socket cl = ss.accept();
                  
                  cl.setReuseAddress(true);
                  ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
                  Objeto ob = (Objeto)ois.readObject();
                  System.out.println("Objeto recibido desde"+cl.getInetAddress()+":"+cl.getPort()+" con los datos");
                  System.out.println("Eliminando archivos: ");
                  System.out.println(ob.getName(0));
                  for(int i=0; i<ob.getSize_List();i++){
                      File  f = new File(ob.getName(i));
                      f.canWrite();
                      if(!f.delete()){
                          System.out.println("NOOOO");
                      }
                      System.out.println("Eliminando archivo |--> " + ob.getName(i));
                  }
                  
                  //Envio de confirmacion de eliminación de archivos
                  Objeto n1 = new Objeto(5); //flag: 5 archivo eliminado
                  ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
                   oos.writeObject(n1);
                   oos.flush();
                  System.out.println("Proceso de eliminacion completado");
                   ois.close();
                    oos.close();
                    cl.close();
                  
             }//for
            
            
        }catch(Exception e){
        e.printStackTrace();
        }//try
        
    
    }//main
}
