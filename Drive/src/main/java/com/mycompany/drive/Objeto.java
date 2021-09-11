package com.mycompany.drive;
import java.io.*;
import javax.swing.tree.DefaultMutableTreeNode;
/**
 *
 * @author reyma
 */
class Objeto implements Serializable{
	int x;
        DefaultMutableTreeNode tree_;
	float y;
        String z;
        Objeto(){
            //Constructor vacio
        }
        Objeto (int flag_ , DefaultMutableTreeNode obj){
            this.x = flag_;
            this.tree_ = obj;
        }
        
	Objeto(int x, float y, String z){
		this.x = x;
		this.y = y;
                this.z = z;
	}

    public int getX() { //Flag de confirmación
        return x;
    }
    
    public DefaultMutableTreeNode getTree(){
        return tree_;
    }
    
    public float getY() {
        return y;
    }

    public String getZ() {
        return z;
    }
      
    public   DefaultMutableTreeNode MyCloud(String Udir){
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
                   Objeto x = new Objeto();
                    hijo.add(x.MyCloud(archivos[i].getAbsolutePath()));
                    
                }else{
                  DefaultMutableTreeNode hijo =  new DefaultMutableTreeNode(archivos[i].getAbsolutePath());
                  raiz.add(hijo); 
                }//if
        }//for
        return raiz;
    }//MyFiles
        
	
	}
