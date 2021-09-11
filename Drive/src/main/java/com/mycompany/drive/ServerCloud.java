package com.mycompany.drive;
import java.net.*;
import java.io.*;

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
        String Udir = "C:\\Users\\reyma\\Desktop\\Practicas_RED\\Practica_1\\FlujoArchivo\\Cloud_Files";
        for(;;){
            Socket cl = ss.accept();
            ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
            //Creación de objeto a enviar
            Objeto n1 = new Objeto();
            Objeto ob2 = new Objeto(1,n1.MyCloud(Udir));
            //Envio de objeto
            oos.writeObject(ob2);
            oos.flush();
            //System.out.println("Cliente conectado.. Enviando objeto con los datos\nX:"+ob2.getX()+" Y:"+ob2.getY()+" Z:"+ob2.getZ() );
            System.out.println("Enviando al cliente: Flag-->" + ob2.getX());
            
            //Recepcion de objeto
            ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
            Objeto ob = (Objeto)ois.readObject();
            System.out.println("Objeto recibido desde"+cl.getInetAddress()+":"+cl.getPort()+" con los datos");
            System.out.println("Numero de confirmación: Flag->" + ob.getX());
            ois.close();
            oos.close();
            cl.close();
        }//for   
    }catch(Exception e){
        e.printStackTrace();
    }
	}//main
	
}