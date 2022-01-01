/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinalacr.supernodo;

import com.proyectofinalacr.RMI_SN_N;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author setjafet
 */
public class SuperNodoInterfaz extends javax.swing.JFrame {
    
    
    String id;
    String idRMI;
    String numeroSuperNodo;
    
    String ipMulticast = "230.1.1.1";
    Integer puertoMulticast = 60000;
    
    String ipRMI;
    Integer puertoRMI;
    
    Object objSN = new Object();
    Object objN = new Object();
    Object objA = new Object();
    
    
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
    
    public class ArchivoInfo implements Comparable<ArchivoInfo>{
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

        @Override
        public int compareTo(ArchivoInfo o) {
            return this.getNombre().compareTo(o.getNombre());
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
            
            new UpdateModelA().start();
        }

        @Override
        public HashMap<String, List<String>> BuscarArchivo(String nombre) throws RemoteException {
            List<String> locales;
            HashMap<String, List<String>> remotos = new HashMap<>();
            
            locales = getKeyArchivosByNombre(listaArchivos, nombre);
            for(String idStubSN : super_nodos_stub.values()){
                try {
                    RMI_SN_N stub = conectarRMI_SN(idStubSN);
                    
                    HashMap<String, List<String>>remotosNuevos = (HashMap<String, List<String>>) stub.BuscarArchivoSN(nombre);
                    for(String key : remotosNuevos.keySet()){
                        //  List<String> v1 = nodosConArchivoProv.get(key);
                        List<String> v2 = remotosNuevos.get(key);
                        //  v1.addAll(v2);
                        //nodosConArchivoProv.replace(key, v1);

                        System.out.println("v2: "+v2);

                        remotos.merge(key, v2, (l1,l2)->{l1.addAll(l2); return l1;});
                    }
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
        int lifeTime=30;

        public TimerSN(String idPeer) {
            this.idSN = idPeer;
            this.time = lifeTime;
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
                    new UpdateModelSN().start();

                } catch (InterruptedException ex) {
                    System.out.println("Error de la muerte");
                }
            }
            System.out.println("\n*******************\n Murio SN "+idSN+"\n");
            //new UpdateHM(2, idSN).start();
            super_nodos.remove(idSN);
            super_nodos_stub.remove(idSN);
            new UpdateModelSN().start();
        }

        public void reiniciar(){
            //System.out.println("Reiniciado");
            this.time = lifeTime;
            
        }
    }
    
    public class TimerN extends Thread{
        String idN;
        public int time;
        int lifeTime=15;

        public TimerN(String idPeer) {
            this.idN = idPeer;
            this.time = lifeTime;
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
                    new UpdateModelN().start();

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
            new UpdateModelN().start();
            new UpdateModelA().start();
            
        }

        public void reiniciar(){
            //System.out.println("Reiniciado");
            this.time = lifeTime;
        }
    } 
    
    public class UpdateModelSN extends Thread{
        
        DefaultTableModel model;

        public UpdateModelSN() {            
            this.model=((DefaultTableModel)jTable2.getModel());
        }
        
        @Override
        public void run() {
            synchronized(objSN){
                model.setRowCount(0);

                jTable2.revalidate();

                for (Map.Entry<String, SN> entry : super_nodos.entrySet()) {
                    model.addRow(new Object[]{
                        super_nodos_stub.get(entry.getKey()),
                        entry.getValue().timer.time
                    });
                }                        
            }
        }
        
    }
    
    public class UpdateModelN extends Thread{
        
        DefaultTableModel model;

        public UpdateModelN() {            
            this.model=((DefaultTableModel)jTable1.getModel());
        }
        
        @Override
        public void run() {
            synchronized(objN){
                model.setRowCount(0);

                jTable1.revalidate();

                for (Map.Entry<String, N> entry : nodos.entrySet()) {
                    model.addRow(new Object[]{
                        entry.getValue().idN,
                        entry.getValue().timer.time
                    });
                }                        
            }
        }
        
    }
    
    public class UpdateModelA extends Thread{
        
        DefaultTableModel model;

        public UpdateModelA() {            
            this.model=((DefaultTableModel)jTable4.getModel());
        }
        
        @Override
        public void run() {
            synchronized(objA){
                model.setRowCount(0);

                jTable4.revalidate();
                
                listaArchivos.values().stream()
                .sorted(Comparator.reverseOrder()).forEachOrdered((e)->{
                    model.addRow(new Object[]{
                        e.nombre,
                        e.md5
                    });
                });

//                for (Map.Entry<String, ArchivoInfo> entry : listaArchivos.entrySet()) {
//                    model.addRow(new Object[]{
//                        entry.getValue().nombre,
//                        entry.getValue().md5
//                    });
//                }                        
            }
        }
        
    }
    /*-------------------------------------------Fin Threads---------------------------------------------------------*/

    /**
     * Creates new form SuperNodoInterfaz
     */
    public SuperNodoInterfaz() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jTable2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 255)));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "ID", "Tiempo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jLabel1.setBackground(new java.awt.Color(51, 102, 255));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 102, 255));
        jLabel1.setText("Lista de Servidores");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Servidor:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 0, 0));
        jLabel6.setText("0");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 102, 255));
        jLabel2.setText("Lista de nodos conectados al servidor");

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 255)));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "ID", "Tiempo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 102, 255));
        jLabel5.setText("Identificador SuperNodo");

        jTable4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 255)));
        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Nombre", "MD5"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTable4);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 102, 255));
        jLabel4.setText("Lista de archivos del servidor");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(215, 215, 215)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(55, 55, 55)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3))
                .addGap(16, 16, 16)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws SocketException, IOException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SuperNodoInterfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SuperNodoInterfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SuperNodoInterfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SuperNodoInterfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
            
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SuperNodoInterfaz sn = new SuperNodoInterfaz();
                
                sn.numeroSuperNodo = (String)JOptionPane.showInputDialog(
                    null,
                    "Ingresa el numero Super Nodo",
                    "Iniciar Super Nodo",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "1");
        
                String sPuerto = (String)JOptionPane.showInputDialog(
                            null,
                            "Ingresa el numero de puerto",
                            "Iniciar Super Nodo",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "50000");
                Integer puerto = Integer.valueOf(sPuerto) ;

                sn.puertoRMI = puerto;
                sn.idRMI = sn.construirId("localhost", puerto);
                
                sn.setTitle("SN "+sn.numeroSuperNodo);
                
                sn.jLabel6.setText(sn.numeroSuperNodo);
                sn.jLabel5.setText(sn.idRMI);

                //sn.idSN = InetAddress.getLocalHost().getHostAddress() + puerto;


                try {
                    sn.new ServidorMulticast().start();

                    sn.new ClienteMulticast().start();

                    sn.new ServidorRMI_SN_N().start();
                } catch (Exception e) {
                    System.err.println("Error");
                    e.printStackTrace();
                }               
                
                sn.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    // End of variables declaration//GEN-END:variables
}
