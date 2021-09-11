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
}//UserCloud
