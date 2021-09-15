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
public class MultiDownloadServer {
     public static void main(String[] args){
        try{
            int pto = 1070;
            ServerSocket ss = new ServerSocket(pto);
            ss.setReuseAddress(true);
            System.out.println("Servidor iniciado - DESCARGAS MULTIPLES");
            for(;;){
                //Aceptación de la conexion del cliente 
                Socket cl = ss.accept();
                cl.setReuseAddress(true);
                //Recepción del nombre del archivo que selecciono el cliente
                ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
                Objeto ob = (Objeto)ois.readObject();
                System.out.println("Nombres de los archivos recibidos: " );
                for(int j=0; j<ob.getSize_List();j++){
                    System.out.println("\t--> " + ob.getName(j)+ "\n");
                }

                
                

                for(int n=0; n<ob.getSize_List();n++){  
                    //Abrimos para cambio de flujo
                    ServerSocket sF = new ServerSocket(pto+(n+1));// por aqui van los archivos
                     sF.setReuseAddress(true);
                    Socket sFiles = sF.accept();
                    //Extraemos dato del archivo n
                    System.out.println("ENVIO INICIADO");
                    File f = new File(ob.getName(n));
                    String arch = f.getName();
                    String dirname = "file"; 
                    String path = f.getAbsolutePath();
                    long tam = f.length();
                    //Preparamos para enviar
                    System.out.println("ENVIO INTERMEDIO");
                    DataOutputStream fileOS = new DataOutputStream(sFiles.getOutputStream());
                    DataInputStream fileIS = new DataInputStream(new FileInputStream(path));
                    fileOS.writeUTF(arch);
                    fileOS.flush();
                    fileOS.writeLong(tam);
                    System.out.println("Preparandose pare enviar archivo "+path+" de "+tam+" bytes\n\n");             
                    System.out.println("Servidor para envio de archivos iniciado ...");              
             
                    long enviados = 0;
                    int l=0,porcentaje=0;
                    while(enviados<tam){
                        byte[] b = new byte[2000];
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
                    sFiles.close();
                    sF.close();
                     System.out.println("ENVIO terminado");
                     
                }//for
                 ois.close();
               cl.close();                     
            }//for
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
