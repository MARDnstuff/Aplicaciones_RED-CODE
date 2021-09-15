package com.mycompany.drive;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
/**
 *
 * @author reyma
 */
class Objeto implements Serializable{
        private static final long serialVersionUID = 6529685098267757690L;

	int x;
        DefaultMutableTreeNode tree_;
        public List<String> selElements = new ArrayList<String>(); 
	float y;
        String z;
        Objeto(){
            //Constructor vacio
        }
        Objeto(List<String> lista_nombres){
            //Constructor vacio
            selElements.addAll(lista_nombres);
            //posible error
        }
        
        Objeto(int nFiles){
            //Constructor vacio
            this.x = nFiles;
        }
        
        Objeto (int flag_ , DefaultMutableTreeNode obj){
            this.x = flag_;
            this.tree_ = obj;
        }
        
	Objeto(String z){
                this.z = z;
	}
        
    public String getName (int index){
        return selElements.get(index);
    }
    
    public int getSize_List (){
        return selElements.size();
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
