package com.mycompany.drive;
import java.net.*;
import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author reyma
 */
public class UserCloud {
    //static String Udir = "C:\\Users\\reyma\\Desktop\\MARCO";

    public UserCloud(){

        //constructor
    }
    
    public   DefaultMutableTreeNode MyFiles(String Udir){
        File ff = new File("" + Udir);
        String ruta = ff.getAbsolutePath() + "\\";
        File ff2 = new File(ruta);
        File[] archivos = ff2.listFiles();
       DefaultMutableTreeNode raiz = new DefaultMutableTreeNode(Udir);
       
        for(int i=0; i<archivos.length;i++){
            //String n_Archivo = archivos[i].getAbsolutePath();
           // n_Archivo = (archivos[i].isDirectory())? n_Archivo + "->Directorio" :n_Archivo;
                
                if(archivos[i].isDirectory()){
                   DefaultMutableTreeNode hijo = new DefaultMutableTreeNode(archivos[i].getAbsolutePath());
                 
                   raiz.add(hijo);
                   //System.out.println("|-->" + n_Archivo);
                   UserCloud x = new UserCloud();
                    hijo.add(x.MyFiles(archivos[i].getAbsolutePath()));
                    
                }else{
                  DefaultMutableTreeNode hijo =  new DefaultMutableTreeNode(archivos[i].getAbsolutePath());
                  raiz.add(hijo); 
                }//if
        }//for
        return raiz;
    }//MyFiles
    /*
    public static void createConnection(){
        try {
            int pto = 1025;
            Socket cl = new Socket("localhost", pto);
            System.out.println("Conexion con servidor exitosa.. preparado para recibir objeto..");
            
            //INICIO DE CONFIGURACIÓN PARA RECIBIR OBJETO
            ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
            //Recepcion de objeto
            Objeto ob2 = (Objeto) ois.readObject();
            System.out.println("Objeto recibido desde " + cl.getInetAddress() + ":" + cl.getPort() + " con los datos:");
            System.out.println("Flag:" + ob2.getX());
            JTree arbol = new JTree(ob2.getTree());
            Tree1.setModel(arbol.getModel());
            //Envio de datos de confirmación
            
            Objeto ob = new Objeto(0,ob2.getTree());
            System.out.println("Enviando objeto con Flag: " + ob.getX());
            oos.writeObject(ob);
            oos.flush();
            System.out.println("Objeto enviado..");
            ois.close();
            oos.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }*/
}//UserCloud
