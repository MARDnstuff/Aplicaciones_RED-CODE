/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinalacr.nodo;

import com.proyectofinalacr.supernodo.SuperNodo;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.proyectofinalacr.RMI_SN_N;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

/**
 *
 * @author setjafet
 */
public class Nodo {
    
    RMI_SN_N stub;
    
    String id;
    String conectadoA=null;
    String numeroNodo;
    
    List<String> listaSuperNodos = new ArrayList<>();
    HashMap<String, SuperNodo.SN> super_nodos = new HashMap<>();
    HashMap<String, String> listaArchivos = new HashMap<>();
    
    public String construirId(String ip, int port){
        return ip+":"+port;
    }
    
    public static byte[] createChecksum(String filename) throws Exception {
       InputStream fis =  new FileInputStream(filename);

       byte[] buffer = new byte[1024];
       MessageDigest complete = MessageDigest.getInstance("MD5");
       int numRead;

       do {
           numRead = fis.read(buffer);
           if (numRead > 0) {
               complete.update(buffer, 0, numRead);
           }
       } while (numRead != -1);

       fis.close();
       return complete.digest();
   }
    
    public static String getMD5Checksum(String filename) throws Exception {
       byte[] b = createChecksum(filename);
       String result = "";

       for (int i=0; i < b.length; i++) {
           result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
       }
       return result;
   }
    
    public <K, V> K getKey(Map<K, SuperNodo.SN> map, String id)
    {
        return map.entrySet()
                    .stream()
                    .filter(entry -> {
                        return entry.getValue().getId().equals(id);
                    })
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);
    }
    
    class ClienteMulticast extends Thread{
        
        String ip = "230.1.1.1";
        int puerto = 60000;
        MulticastSocket socket;

        public ClienteMulticast() throws UnknownHostException, IOException {
            InetAddress ip_grupo = InetAddress.getByName(ip);
            this.socket = new MulticastSocket(puerto);
            socket.joinGroup(ip_grupo);
        }
               
        void recibe_mensaje() throws IOException{
            byte[] buffer =  new byte[21];
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquete);
            
//            String address = paquete.getAddress().getHostAddress();
//            int port = paquete.getPort();

            String idSuperNodoNuevoRMI;
            idSuperNodoNuevoRMI = new String(paquete.getData());
            
            //String idSuperNodoNuevo = construirId(address, port);
            
            if(!listaSuperNodos.contains(idSuperNodoNuevoRMI)){
                listaSuperNodos.add(idSuperNodoNuevoRMI);
            }
            
        }

        @Override
        public void run() {
            for(;;){
                try {
                    recibe_mensaje();
                } catch (IOException ex) {
                    Logger.getLogger(SuperNodo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        
       
    }
    
    class ClienteRMI_ActualizarArchivos extends Thread{

        @Override
        public void run() {
            for(;;){
                try {
                    
                    listaArchivos.clear();
                    String directorio = id.replace(":", "");
                    File f = new File("C:/"+directorio);
                    
                    for (String archivo : f.list()) {
                        //System.out.println(archivo);
                        String md5 = getMD5Checksum("C:/"+directorio+"/"+archivo);
                        listaArchivos.put(md5, archivo);
                    }
                    
                    //System.out.println(Arrays.asList(listaArchivos));
                    stub.ActualizarListaArchivos(listaArchivos, id);
                    Thread.sleep(5000);
                } catch (RemoteException ex) {
                    Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error");
                    System.out.println("Hubo un error al conectarse con el super nodo, intentando conectar con uno nuevo...");
                    break;
                } catch (InterruptedException ex) {
                    Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error");
                } catch (Exception ex) {
                    Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error");
                }
                
            }
            
            try {
                conectarASN();
            } catch (InterruptedException ex) {
                Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error al conectar a otro SN");
            } catch (IOException ex) {
                Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error al conectar a otro SN");
            }
            
        }
        
    }
    
    class ServidorArchivos extends Thread{
        
        ServerSocket ss;

        public ServidorArchivos() throws IOException {
            String ip = id.split(":")[0];
            String port = id.split(":")[1];
            ss = new ServerSocket(Integer.valueOf(port));
        }

        @Override
        public void run() {
            System.out.println("Servidor corriendo en: "+ss.getInetAddress()+" : "+ss.getLocalPort());
            for(;;){
                try {
                    Socket conexion = ss.accept();
                    new Worker(conexion).start();
                } catch (IOException ex) {
                    Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        class Worker  extends Thread{
            
            Socket con;
                
            public Worker(Socket c) {
                this.con = c;
            }
            
            @Override
            public void run() {
                try {
                    DataInputStream dis = new DataInputStream(con.getInputStream());
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    
                    String nombreArchivo = dis.readUTF();
                    String parteArchivo =dis.readUTF();
                    
                    int partes = Integer.valueOf(parteArchivo.split("/")[1]);
                    int parteEnviar = Integer.valueOf(parteArchivo.split("/")[0]);
                    
                    String directorio = id.replace(":", "");
                    File archivo = new File("C:/"+directorio+"/"+nombreArchivo);
                    
                    byte[] bufferEnviar = leerParteArchivo(archivo, partes, parteEnviar);
                    
                    //Se envía el tamanio del buffer
                    dos.writeInt(bufferEnviar.length);
                    //Se envía el buffer
                    dos.write(bufferEnviar,0,bufferEnviar.length);
                    
                    dos.flush();
                    
                    //try (FileOutputStream fos = new FileOutputStream("C:/descargas/"+nombreArchivo)) {
                    //
                    //        System.out.println(Arrays.asList(bufferEnviar));
                    //        System.out.println(bufferEnviar.length);
                    //        fos.write(bufferEnviar);
                    //    //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream

                    //}
                    
                    Thread.sleep(500);
                    
                    dos.close();
                    dis.close();
                    //con.close();
                
                } catch (IOException ex) {
                    Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }  
            
            private byte[] leerParteArchivo(File file, int partes, int parteEnviar)
            {
               FileInputStream fileInputStream = null;
               
               int tamanioParte = ((int)file.length()/partes);

               System.out.println("Tamanio archivo: "+ file.length());

               System.out.println("Tamanio pedazo: "+ tamanioParte);
               
               int tamanioBuffer = parteEnviar==partes
                       ?tamanioParte+((int)file.length()%partes)
                       :tamanioParte;
               
                System.out.println("Tamanio buffer: "+ tamanioBuffer);
               
                byte[] bArchivoCompleto = new byte[(int)file.length()];

                byte[] bFile = new byte[tamanioBuffer];
               
               try
               {
                  //convert file into array of bytes
                  fileInputStream = new FileInputStream(file);
                  
                  int inicio = (parteEnviar-1)*tamanioParte;

                  System.out.println("Byte inicio: "+inicio);
                  
                  bArchivoCompleto = Files.readAllBytes(file.toPath());
                  
                  fileInputStream.skip(inicio);
                  fileInputStream.read(bFile);
                  fileInputStream.close();
                  
                  bFile=Arrays.copyOfRange(bArchivoCompleto, inicio, inicio+tamanioBuffer);
                  
               }
               catch (Exception e)
               {
                  e.printStackTrace();
               }
               return bFile;
            }
        }
    }
    
    class ClienteArchivos{
        
        List<byte[]> listaBytes;
        List<String> nodos;
        String nombreArchivo;
        int numeroNodos;
                
        public ClienteArchivos(List<String> nodos, String nombreArchivo) {
            this.nodos = nodos;
            this.nombreArchivo = nombreArchivo;
            numeroNodos = nodos.size();
            System.out.println("Cliente numero nodos: "+ numeroNodos);
            listaBytes = new ArrayList<>();
            for (int i = 0; i < numeroNodos; i++) {
                listaBytes.add(i,null);
            }
            System.out.println("Lista Bytes Inicio: "+ listaBytes);
        }
        
        void solicitarArchivo(){
               
            for(int i=1; i<=numeroNodos;i++){
                new Worker(nombreArchivo, nodos.get(i-1), i+"/"+numeroNodos).start();
            }

        } 
        
        List<byte[]> archivoListo(){
            for(;;){
                
                boolean flag = true;
                
                for (int i = 0; i < numeroNodos; i++) {
                    if(listaBytes.get(i)==null){
                        flag=false;
                    }
                }
                
                if(flag)
                    break;                
            }
            return listaBytes;
        }
        
        class Worker extends Thread{
            String nombreArchivo,ip,parte;
            int puerto;
            Socket con;
            public Worker(String nombreArchivo, String nodo, String parte) {
                this.nombreArchivo=nombreArchivo;
                this.ip=nodo.split(":")[0];
                this.puerto=Integer.valueOf(nodo.split(":")[1]);
                this.parte=parte;
            }

            @Override
            public void run() {
                System.out.println("Conectandonos a : "+ip+" : "+ puerto);
                try {
                    for(;;){
                        con = new Socket(ip, puerto);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Fallo conexion intentado de nuevo...");
                }
                
                try {
                    DataInputStream dis = new DataInputStream(con.getInputStream());                    
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    
                    dos.writeUTF(nombreArchivo);
                    dos.writeUTF(parte);
                    
                    int tamanioBuffer = dis.readInt();
                    int indice = Integer.valueOf(parte.split("/")[0])-1;
                    byte[] bytes = new byte[tamanioBuffer];
                    dis.readFully(bytes);
                    listaBytes.set(indice, bytes);                    
                    System.out.println("Lista Bytes Fin: "+ listaBytes);
                } catch (IOException ex) {
                    Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
        }
        
    }
    
    boolean conectarASN() throws InterruptedException, IOException{
        
        listaSuperNodos=new ArrayList<>();
        
        Thread.sleep(30000);
        
        for(String sn : this.listaSuperNodos){
            Registry registry;
            try {
                registry = LocateRegistry.getRegistry(null);
                
                sn = sn.replace(" ", "");
                System.out.println("Intentando conectar a: "+sn);
                
                this.stub = (RMI_SN_N) registry.lookup(sn);
                
                Thread.sleep(1000);
                
                String respuesta = this.stub.SolicitarConexion(this.id);
                
                if(respuesta!=null){
                    this.conectadoA=respuesta;
                    System.out.println("Nodo conectado a: "+this.conectadoA);
                    break;
                }
                
            } catch (RemoteException ex) {
                Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotBoundException ex) {
                Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(this.conectadoA==null){
            System.out.println("No hay super nodos disponibles");
            return false;
        }
        
        this.new ClienteRMI_ActualizarArchivos().start();
        return true;
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length < 1){
            System.err.println("Hacen falta argumentos");
            return;
        }
        
        String ip = args.length > 1?args[0]:"localhost";
        Integer puerto =  args.length > 1?Integer.valueOf(args[1]):Integer.valueOf(args[0]);
        
        Nodo n = new Nodo();
        
        n.id = n.construirId(ip, puerto);
        
        Path path = Paths.get("C:/"+n.id.replace(":", ""));
        Files.createDirectories(path);
        
        path = Paths.get("C:/descargas"+n.id.replace(":", ""));
        Files.createDirectories(path);
        
        n.new ServidorArchivos().start();
        
        n.new ClienteMulticast().start();
        
        if(!n.conectarASN()) return;
        
        for(;;){
            Scanner keyboard = new Scanner(System.in);
            String mystring = keyboard.nextLine();
            
            HashMap<String, List<String>> resultadoNodos =(HashMap<String, List<String>>) n.stub.BuscarArchivo(mystring);
            
            System.out.println("Nodos que contienen el archivo: " + Arrays.asList(resultadoNodos));
            
            if(resultadoNodos.size()>0){
                System.out.println("Elije un archivo:\n");
                int opcion=0;
                for (String key : resultadoNodos.keySet()) {
                    System.out.println("\t "+opcion+".- "+key);
                }

                String mystrignint = keyboard.nextLine();

                int myint = Integer.valueOf(mystrignint);
                
                String key = (String) resultadoNodos.keySet().toArray()[myint];
                
                System.out.println(key);

                ClienteArchivos ca = n.new ClienteArchivos(resultadoNodos.get(key), mystring);

                ca.solicitarArchivo();

                List<byte[]> listaBytesArchivo = ca.archivoListo();

                System.out.println(listaBytesArchivo);

                try (FileOutputStream fos = new FileOutputStream("C:/descargas"+n.id.replace(":", "")+"/"+mystring)) {
                    
                    for(byte[] bytes : listaBytesArchivo){
                        System.out.println(Arrays.asList(bytes));
                        System.out.println(bytes.length);
                        fos.write(bytes);
                    }
                    //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                
                }
            }
        }
        
    }
}
