package com.mycompany.drive;
import java.net.*;
import java.io.*;
//Envia Los archivos remotos
/**
 *
 * @author reyma
 */
public class ServerCloud {

    public static void main(String[] args)throws Exception{
        try{
            int pto = 1025;
            ServerSocket ss = new ServerSocket(pto);
            System.out.println("Servidor iniciado");
            String Udir = "C:\\Users\\reyma\\Desktop\\Practicas_RED\\Practica_1\\Aplicaciones_RED-CODE\\Cloud_Files";
            for(;;){
                Socket cl = ss.accept();
                ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
                //Creación de objeto a enviar
                Objeto n1 = new Objeto();
                Objeto ob2 = new Objeto(1,n1.MyCloud(Udir));
                //Envio de objeto
                oos.writeObject(ob2);
                oos.flush();
               
                System.out.println("Enviando al cliente: Flag-->" + ob2.getX());            
                cl.close();
            }//for   
        }catch(Exception e){
            e.printStackTrace();
        }
    }//main
}