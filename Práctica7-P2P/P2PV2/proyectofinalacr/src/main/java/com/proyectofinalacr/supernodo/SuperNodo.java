/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinalacr.supernodo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.proyectofinalacr.RMI_SN_N;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 *
 * @author setjafet
 */
public class SuperNodo {
    
    String id;
    String idRMI;
    String numeroSuperNodo;
    
    String ipMulticast = "230.1.1.1";
    Integer puertoMulticast = 60000;
    
    String ipRMI;
    Integer puertoRMI;
    
    HashMap<String, SN> super_nodos = new HashMap<>();
    HashMap<String, String> super_nodos_stub = new HashMap<>();
    HashMap<String, N> nodos = new HashMap<>();
    HashMap<String, ArchivoInfo> listaArchivos = new HashMap<>();
    Integer numArchivos=0;
    
    public <K, V> K getKey(Map<K, SN> map, String id)
    {
        return map.entrySet()
                    .stream()
                    .filter(entry -> {
                        return entry.getValue().getId().equals(id);
                    })
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);
    }
    
    public <K, V> K getKeyNodo(Map<K, N> map, String id)
    {
        return map.entrySet()
                    .stream()
                    .filter(entry -> {
                        return entry.getValue().getId().equals(id);
                    })
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);
    }
    
    public <K, V> K getKeyArchivoByNombreNodo(Map<K, ArchivoInfo> map, String nombre,String nodo)
    {
        return map.entrySet()
                    .stream()
                    .filter(entry -> {
                        return entry.getValue().getNombre().equals(nombre) 
                                && entry.getValue().getIdNodo().equals(nodo);
                    })
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);
    }
    
    public <K, V> List<K> getKeyArchivosByNombre(Map<K, ArchivoInfo> map, String nombre)
    {
        return map.entrySet()
                    .stream()
                    .filter(entry -> {
                        return entry.getValue().getNombre().equals(nombre);
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
    }
    
    public <K, V> List<K> getKeyArchivosByNodo(Map<K, ArchivoInfo> map, String nodo)
    {
        return map.entrySet()
                    .stream()
                    .filter(entry -> {
                        return entry.getValue().getIdNodo().equals(nodo);
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
    }
    
    public <K, V> List<K> getKeyArchivosByMD5(Map<K, ArchivoInfo> map, String md5)
    {
        return map.entrySet()
                    .stream()
                    .filter(entry -> {
                        return entry.getValue().getMd5().equals(md5);
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
    }
    
    
    public String construirId(String ip, int port){
        return ip+":"+port;
    }
    
    /*----------------------------------------- Objetos -------------------------------------------------------------*/
       
    
    public class SN {
        private String ip;
        private int port;
        private TimerSN timer;
        private String idSN;

        public SN(String ip, int port, TimerSN t) {
            this.ip=ip;
            this.port=port;
            this.timer=t;
            this.idSN = construirId(ip, port);

            t.start();
        }

        @Override
        public String toString() {
            return String.format("{ IP: %s, PORT: %s, ID: %s, TIMER: %d}", ip, port, idSN,timer.time);
        }
        
        public TimerSN getTimer() {
            return timer;
        }

        public void setTimer(TimerSN timer) {
            this.timer = timer;
        }    

        public String getId() {
            return idSN;
        }

        public void setId(String id) {
            this.idSN = id;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
    
    public class N {
        private String ip;
        private int port;
        private TimerN timer;
        private String idN;

        public N(String ip, int port, TimerN t) {
            this.ip=ip;
            this.port=port;
            this.timer=t;
            this.idN = construirId(ip, port);

            t.start();
        }

        public String toString() {
            return String.format("{ IP: %s, PORT: %s, ID: %s, TIMER: %d}", ip, port, idN,timer.time);
        }
        
        public TimerN getTimer() {
            return timer;
        }

        public void setTimer(TimerN timer) {
            this.timer = timer;
        }    

        public String getId() {
            return idN;
        }

        public void setId(String id) {
            this.idN = id;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
    
    public class ArchivoInfo{
        String idNodo;
        String md5;
        String nombre;

        public ArchivoInfo(String nodo, String md5, String nombre) {
            this.idNodo = nodo;
            this.md5 = md5;
            this.nombre = nombre;
        }
        
        @Override
        public String toString() {
            return String.format("{MD5: %s, NOMBRE: %s, NODO: %s}", md5, nombre, idNodo);
        }

        public String getIdNodo() {
            return idNodo;
        }

        public void setIdNodo(String idNodo) {
            this.idNodo = idNodo;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }  
        
    }
    /*-----------------------------------------Fin Objetos----------------------------------------------------------*/
    
    
    /*------------------------------------------ Threads --------------------------------------------------------*/
    class ServidorMulticast extends Thread{
        
        String ip = ipMulticast;
        int puerto = puertoMulticast;
        DatagramSocket socket;
        InetAddress ip_grupo;
        
        public ServidorMulticast() throws SocketException, UnknownHostException {
            socket = new DatagramSocket();
            ip_grupo = InetAddress.getByName(ip);
            
            id = construirId(ip_grupo.getLocalHost().getHostAddress(),socket.getLocalPort());
            System.out.println("Id Super Nodo: "+id);
            //System.out.println("**"+InetAddress.getLocalHost().getHostAddress()+" - "+socket.getLocalPort());
        }
        
        void envia_mensaje(byte[] buffer) throws IOException{            
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length,ip_grupo,puerto);
            socket.send(paquete);
        }

        @Override
        public void run() {
            try {
                for (;;) {
                    //  ByteBuffer mensaje = ByteBuffer.allocate(4);
                    //  mensaje.putInt(1);
                    String mensaje = idRMI;
                    while(mensaje.length()!=21){
                       mensaje = mensaje+" ";
                    }
                    envia_mensaje(mensaje.getBytes());
                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                System.err.println("Super Error");
                System.err.println(e.getMessage());
            }
        }
        
    }
    
    class ClienteMulticast extends Thread{
        
        String ip = ipMulticast;
        int puerto = puertoMulticast;
        MulticastSocket socket;

        public ClienteMulticast() throws UnknownHostException, IOException {
            InetAddress ip_grupo = InetAddress.getByName(ipMulticast);
            this.socket = new MulticastSocket(puerto);
            socket.joinGroup(ip_grupo);
        }
               
        void recibe_mensaje() throws IOException{
            byte[] buffer =  new byte[21];
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquete);
            
            String address = paquete.getAddress().getHostAddress();
            int port = paquete.getPort();
            
            String stub = new String(paquete.getData(),"UTF-8").replace(" ","");
            
            String key = getKey(super_nodos,construirId(address,port));
            
            //if(key == null && !id.equals(construirId(address,port))){
            
            if(key == null && Integer.valueOf(id.split(":")[1]) != port){
                String new_key = construirId(address,port);
                super_nodos.put(new_key, new SN(address, port, new TimerSN(new_key)));
                super_nodos_stub.put(new_key, stub);
            }else if ( key!=null ){
                super_nodos.get(key).getTimer().reiniciar();
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
    
    class ServidorRMI_SN_N  extends Thread implements RMI_SN_N{

        @Override
        public String SolicitarConexion(String id) {
            if(nodos.size()<2){
                String[] d = id.split(":");
                String ip = d[0];
                int port = Integer.valueOf(d[1]);
                nodos.put(id, new N(ip, port, new TimerN(id) ));
                return idRMI;
            }
            return null;
        }

        @Override
        public void ActualizarListaArchivos(HashMap<String, String> listaArchivosNodo, String idNodo) {
            
            nodos.get(getKeyNodo(nodos, idNodo)).timer.reiniciar();
            
            getKeyArchivosByNodo(listaArchivos, idNodo).forEach(key->{
                listaArchivos.remove(key);
            });
            
            listaArchivosNodo.keySet().stream().map(md5 -> {
                return md5;
            }).forEachOrdered(md5 -> {
                String idArchivo = listaArchivosNodo.get(md5)+":"+idNodo;
                listaArchivos.put(idArchivo, new ArchivoInfo(idNodo, md5, listaArchivosNodo.get(md5)));
            });
            
        }

        @Override
        public HashMap<String, List<String>> BuscarArchivo(String nombre) throws RemoteException {
            List<String> locales;
            HashMap<String, List<String>> remotos = new HashMap<>();
            
            locales = getKeyArchivosByNombre(listaArchivos, nombre);
            for(String idStubSN : super_nodos_stub.values()){
                try {
                    RMI_SN_N stub = conectarRMI_SN(idStubSN);
                    
                    remotos = (HashMap<String, List<String>>) stub.BuscarArchivoSN(nombre);
                } catch (NotBoundException ex) {
                    Logger.getLogger(SuperNodo.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SuperNodo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            //Aún falta hacer busqueda en supernodos 
            System.out.println("Buscando Archivo");
            System.out.println("Locales - "+Arrays.asList(locales));
            System.out.println("Remotes - "+Arrays.asList(remotos));
            
            List<String> nodosConArchivo = new ArrayList<>();
            HashMap<String, List<String>> nodosConArchivoProv = new HashMap<>();
            locales.forEach(key -> {
                //nodosConArchivo.add(listaArchivos.get(key).idNodo);
                
                if(nodosConArchivoProv.containsKey(listaArchivos.get(key).md5)){
                    nodosConArchivoProv.get(listaArchivos.get(key).md5).add(listaArchivos.get(key).idNodo);
                }else{
                    List<String> nuevaLista = new ArrayList<>();
                    nuevaLista.add(listaArchivos.get(key).idNodo);
                    nodosConArchivoProv.put(
                            listaArchivos.get(key).md5, 
                            nuevaLista
                    );
                }                
            });
            
            for(String key : remotos.keySet()){
                //  List<String> v1 = nodosConArchivoProv.get(key);
                List<String> v2 = remotos.get(key);
                //  v1.addAll(v2);
                //nodosConArchivoProv.replace(key, v1);

                System.out.println("v2: "+v2);
                
                nodosConArchivoProv.merge(key, v2, (l1,l2)->{l1.addAll(l2); return l1;});
            }
            
            System.out.println(Arrays.asList(nodosConArchivoProv));
            return nodosConArchivoProv;
        }
        
        @Override
        public HashMap<String, List<String>> BuscarArchivoSN(String nombre) {
            List<String> locales;
            locales = getKeyArchivosByNombre(listaArchivos, nombre);
            //Aún falta hacer busqueda en supernodos 
            System.out.println("Buscando Archivo SN");
            System.out.println(Arrays.asList(locales));
            HashMap<String, List<String>> nodosConArchivo = new HashMap<>();
            locales.forEach(key -> {
                if(nodosConArchivo.containsKey(listaArchivos.get(key).md5)){
                    nodosConArchivo.get(listaArchivos.get(key).md5).add(listaArchivos.get(key).idNodo);
                }else{
                    List<String> nuevaLista = new ArrayList<>();
                    nuevaLista.add(listaArchivos.get(key).idNodo);
                    nodosConArchivo.put(
                            listaArchivos.get(key).md5, 
                            nuevaLista
                    );
                }                
            });
            System.out.println(Arrays.asList(nodosConArchivo));
            return nodosConArchivo;
        }
        
        public RMI_SN_N conectarRMI_SN(String sn) throws RemoteException, NotBoundException, InterruptedException{
                Registry registry = LocateRegistry.getRegistry(null);
                
                sn = sn.replace(" ", "");
                System.out.println("Intentando conectar a: "+sn);
                
                RMI_SN_N stubSN = (RMI_SN_N) registry.lookup(sn);
                
                Thread.sleep(1000);
                
                return stubSN;
        }

        @Override
        public void run() {
            try {
                //System.setProperty("java.rmi.server.codebase","file:/c:/Temp/Suma/"); windows
                // en linux lo hace en auto
                ServidorRMI_SN_N obj = new ServidorRMI_SN_N();
                RMI_SN_N stub = (RMI_SN_N) UnicastRemoteObject.exportObject(obj, puertoRMI);
                // Ligamos el objeto remoto en el registro
                Registry registry = LocateRegistry.getRegistry();
                System.out.println("Registrando RMI como: "+idRMI);
                registry.bind(idRMI, stub);

                System.out.println("ServidorRMI listo...");
            } catch (Exception e) {
                System.err.println("Excepcion del servidorRMI: " + e.toString());
                e.printStackTrace();
            }
        }
        
    }
    
    public class TimerSN extends Thread{
        String idSN;
        public int time;

        public TimerSN(String idPeer) {
            this.idSN = idPeer;
            this.time = 30;
        }      

        public String getIdPeer() {
            return idSN;
        }

        public void setIdPeer(String idPeer) {
            this.idSN = idPeer;
        }

        public int getTiempo() {
            return time;
        }

        public void setTiempo(int tiempo) {
            this.time = tiempo;
        }


        @Override
        public void run() {
            while(time>0){
                try {
                    Thread.sleep(1000);
                    time--;
                    //System.out.println(time);
                    //new UpdateModel().start();

                } catch (InterruptedException ex) {
                    System.out.println("Error de la muerte");
                }
            }
            System.out.println("\n*******************\n Murio SN "+idSN+"\n");
            //new UpdateHM(2, idSN).start();
            super_nodos.remove(idSN);
            super_nodos_stub.remove(idSN);
        }

        public void reiniciar(){
            //System.out.println("Reiniciado");
            this.time = 30;
        }
    }
    
    public class TimerN extends Thread{
        String idN;
        public int time;

        public TimerN(String idPeer) {
            this.idN = idPeer;
            this.time = 15;
        }      

        public String getIdPeer() {
            return idN;
        }

        public void setIdPeer(String idPeer) {
            this.idN = idPeer;
        }

        public int getTiempo() {
            return time;
        }

        public void setTiempo(int tiempo) {
            this.time = tiempo;
        }


        @Override
        public void run() {
            while(time>0){
                try {
                    Thread.sleep(1000);
                    time--;
                    //System.out.println(time);
                    //new UpdateModel().start();

                } catch (InterruptedException ex) {
                    System.out.println("Error de la muerte");
                }
            }
            System.out.println("\n*******************\n Murio N "+idN+" \n");
            //new UpdateHM(2, idSN).start();
            nodos.remove(idN);
            getKeyArchivosByNodo(listaArchivos, idN).forEach(key -> {
                listaArchivos.remove(key);
            });
        }

        public void reiniciar(){
            //System.out.println("Reiniciado");
            this.time = 30;
        }
    } 
    /*-------------------------------------------Fin Threads---------------------------------------------------------*/
    //C:/'Program Files'/Java/jdk1.8.0_301/bin/javac com/proyectofinalacr/supernodo/SuperNodo.java
    public static void main(String[] args) throws IOException {
        if(args.length < 1){
            System.err.println("Hacen falta argumentos");
            return;
        }
        
        String ip = args.length > 1?args[0]:"localhost";
        Integer puerto =  args.length > 1?Integer.valueOf(args[1]):Integer.valueOf(args[0]);
        
        SuperNodo sn = new SuperNodo();
        
        sn.puertoRMI = puerto;
        sn.idRMI = sn.construirId(ip, puerto);
        
        //sn.idSN = InetAddress.getLocalHost().getHostAddress() + puerto;
        
        
        sn.new ServidorMulticast().start();
        
        sn.new ClienteMulticast().start();
        
        sn.new ServidorRMI_SN_N().start();
        
        for(;;){
            Scanner keyboard = new Scanner(System.in);
            int myint = keyboard.nextInt();
            switch(myint){
                case 1:
                    System.out.println("Imprimiendo Super Nodos");
                    System.out.println(Arrays.asList(sn.super_nodos));
                    break;
                case 2:
                    System.out.println("Imprimiendo Stubs");
                    System.out.println(Arrays.asList(sn.super_nodos_stub));
                    break;
                case 3:
                    System.out.println("Imprimiendo Nodos");
                    System.out.println(Arrays.asList(sn.nodos));
                    break;
                case 4:
                    System.out.println("Imprimiendo Archivos");
                    System.out.println(Arrays.asList(sn.listaArchivos));
                    break;
            }
        }
        
    }
    
}