/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinalacr.nodo;

import com.proyectofinalacr.RMI_SN_N;
import com.proyectofinalacr.supernodo.SuperNodo;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author setjafet
 */
public class NodoInterfaz extends javax.swing.JFrame {
    
    RMI_SN_N stub;
    
    String id;
    String conectadoA=null;
    String numeroNodo;
    
    List<String> listaSuperNodos = new ArrayList<>();
    HashMap<String, SuperNodo.SN> super_nodos = new HashMap<>();
    HashMap<String, String> listaArchivos = new HashMap<>();
    
    Object objTextPanel = new Object();
    
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
            agregar_texto_jtextpanel("Escuchando SN disponibles");
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
                    //agregar_texto_jtextpanel("Enviando actualización de archivos al nodo");
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
                    agregar_texto_jtextpanel("Hubo un error al conectarse con el super nodo, intentando conectar con uno nuevo...");
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
                agregar_texto_jtextpanel("Error al conectar a otro SN");
            } catch (IOException ex) {
                Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error al conectar a otro SN");
                agregar_texto_jtextpanel("Error al conectar a otro SN");
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
            agregar_texto_jtextpanel("Servidor TCP corriendo en: "+ss.getInetAddress()+" : "+ss.getLocalPort());
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
                    
                    agregar_texto_jtextpanel("Se solicitó el archivo: "+nombreArchivo);
                    
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
                    
                    int tamanioParte = ((int)archivo.length()/partes);
                    int inicio = (parteEnviar-1)*tamanioParte;
                    
                    //Escribir rango de bytes
                    dos.writeUTF(inicio+" - "+(inicio+bufferEnviar.length));
                    
                    //try (FileOutputStream fos = new FileOutputStream("C:/descargas/"+nombreArchivo)) {
                    //
                    //        System.out.println(Arrays.asList(bufferEnviar));
                    //        System.out.println(bufferEnviar.length);
                    //        fos.write(bufferEnviar);
                    //    //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream

                    //}
                    agregar_texto_jtextpanel("Archivo Entregado");
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
                  
                  //fileInputStream.skip(inicio);
                  //fileInputStream.read(bFile);
                  //fileInputStream.close();
                  
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
            agregar_texto_jtextpanel("Solicitando archivos a los nodos: "+Arrays.asList(this.nodos));
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
                
                if(flag){
                    agregar_texto_jtextpanel("Archivos Recibidos");
                    break;
                }
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
                agregar_texto_jtextpanel("Conectandonos a : "+ip+" : "+ puerto);
                System.out.println("Conectandonos a : "+ip+" : "+ puerto);
                try {
                    for(;;){
                        con = new Socket(ip, puerto);
                        break;
                    }
                } catch (Exception e) {
                    agregar_texto_jtextpanel("Fallo conexion intentado de nuevo...");
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
                    
                    String rangoBytes = dis.readUTF();
                    
                    agregar_texto_jtextpanel("El rango de bytes entregados es: "+rangoBytes);
                    
                    listaBytes.set(indice, bytes);                    
                    System.out.println("Lista Bytes Fin: "+ listaBytes);
                } catch (IOException ex) {
                    Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
        }
        
    }
    
    boolean conectarASN() throws InterruptedException, IOException{
        for(;;){
            agregar_texto_jtextpanel("Escuchando en Multicasta a SNs");
            listaSuperNodos=new ArrayList<>();

            Thread.sleep(30000);

            for(String sn : this.listaSuperNodos){
                Registry registry;
                try {
                    registry = LocateRegistry.getRegistry(null);

                    sn = sn.replace(" ", "");
                    agregar_texto_jtextpanel("Intentando conectar a: "+sn);
                    System.out.println("Intentando conectar a: "+sn);

                    this.stub = (RMI_SN_N) registry.lookup(sn);

                    Thread.sleep(1000);

                    String respuesta = this.stub.SolicitarConexion(this.id);

                    if(respuesta!=null){
                        this.conectadoA=respuesta;
                        agregar_texto_jtextpanel("Nodo conectado a: "+this.conectadoA);
                        System.out.println("Nodo conectado a: "+this.conectadoA);
                        break;
                    }

                } catch (RemoteException ex) {
                    //Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                    agregar_texto_jtextpanel("Falló conexión a: "+sn);
                } catch (NotBoundException ex) {
                    Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
                    agregar_texto_jtextpanel("Falló conexión a: "+sn);
                }
            }

            if(this.conectadoA==null){
                agregar_texto_jtextpanel("No hay super nodos disponibles");
                System.out.println("No hay super nodos disponibles");
                continue;
            }

            this.new ClienteRMI_ActualizarArchivos().start();
            return true;
        }
    }
    
    void agregar_texto_jtextpanel(String mensaje){
        
        synchronized(objTextPanel){
            String[] contenidoViejo = jTextPane1.getText().split("<body>");
            
            String nuevoContenido = contenidoViejo[0]+"<body><p align='left' style='border-top:2px dotted blue;padding: 5px 0px;'>"+mensaje+"</p>"+contenidoViejo[1];
            //System.out.println(nuevoContenido);
            jTextPane1.setText(nuevoContenido);
    //        jTextPane1.setText("<html>"+jTextPane1.getText()+"<br>"+de+(privado?"(Privado)":"")+":"+mensaje);
        }
    }
    
    void descargarArchivos(HashMap<String, List<String>> resultadoNodos, String nombreArchivo) throws FileNotFoundException, IOException{
        System.out.println("Nodos que contienen el archivo: " + Arrays.asList(resultadoNodos));
        if(resultadoNodos.size()>0){   
            agregar_texto_jtextpanel("Se encontró el archivo en la topología");
            agregar_texto_jtextpanel("Nodos que contienen el archivo: " + Arrays.asList(resultadoNodos));
            String key = (String)JOptionPane.showInputDialog            (
                        null,
                        "Elije un MD5 para comenzar la descarga:",
                        "Coincidencias encontradas",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        resultadoNodos.keySet().toArray(),
                        resultadoNodos.keySet().toArray()[0]);
            System.out.println(key);
            ClienteArchivos ca = new ClienteArchivos(resultadoNodos.get(key), nombreArchivo);

            ca.solicitarArchivo();

            List<byte[]> listaBytesArchivo = ca.archivoListo();

            System.out.println(listaBytesArchivo);

            try (FileOutputStream fos = new FileOutputStream("C:/descargas"+id.replace(":", "")+"/"+nombreArchivo)) {

                for(byte[] bytes : listaBytesArchivo){
                    System.out.println(Arrays.asList(bytes));
                    System.out.println(bytes.length);
                    fos.write(bytes);
                }
                //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                JOptionPane.showMessageDialog(null,
                "El archivo "+nombreArchivo+" ya se ha guardado en el directorio "+"C:/descargas"+id.replace(":", "")+"/",
                "Descarga Completada",
                JOptionPane.INFORMATION_MESSAGE);
            }
        }else{
            agregar_texto_jtextpanel("No se encontró el archivo en la topología");
            JOptionPane.showMessageDialog(null,
            "No se encontró ningún archivo en la topología que coincida con el nombre ingresado",
            "Sin Archivo",
            JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates new form NodoInterfaz
     */
    public NodoInterfaz() {
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jTextPane1.setEditable(false);
        jTextPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 255)));
        jTextPane1.setContentType("text/html");
        jScrollPane1.setViewportView(jTextPane1);

        jButton1.setBackground(new java.awt.Color(51, 51, 255));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Buscar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.setText("Nombre del Archivo.ext");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 255));
        jLabel3.setText("Archivo a buscar:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 255));
        jLabel4.setText("Registro Acciones");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Nodo: ");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 51));
        jLabel2.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(96, 96, 96)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)))
                        .addContainerGap(36, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(21, 21, 21)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        HashMap<String, List<String>> resultadoNodos = null;
        try {
            agregar_texto_jtextpanel("Buscan el archivo "+jTextField1.getText()+" en los SN");
            resultadoNodos =(HashMap<String, List<String>>) stub.BuscarArchivo(jTextField1.getText());
        } catch (RemoteException ex) {
            Logger.getLogger(NodoInterfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            descargarArchivos(resultadoNodos,jTextField1.getText());
        } catch (IOException ex) {
            Logger.getLogger(NodoInterfaz.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(NodoInterfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NodoInterfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NodoInterfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NodoInterfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NodoInterfaz n = new NodoInterfaz();
                
                String ip = args.length > 1?args[0]:"localhost";
                n.numeroNodo = (String)JOptionPane.showInputDialog(
                    null,
                    "Ingresa el numero de Nodo",
                    "Iniciar Nodo",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "1");
        
                String sPuerto = (String)JOptionPane.showInputDialog(
                            null,
                            "Ingresa el numero de puerto",
                            "Iniciar Nodo",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "40000");
                Integer puerto = Integer.valueOf(sPuerto) ;
                
                n.id = n.construirId(ip, puerto);
                
                n.jLabel2.setText(n.numeroNodo);  
                
                n.setTitle("N "+n.numeroNodo);
                
                n.setVisible(true);
                
                n.agregar_texto_jtextpanel("Creando carpteas del nodo");

                Path path = Paths.get("C:/"+n.id.replace(":", ""));
                try {
                    Files.createDirectories(path);
                } catch (IOException ex) {
                    Logger.getLogger(NodoInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                }

                path = Paths.get("C:/descargas"+n.id.replace(":", ""));
                
                try {
                    Files.createDirectories(path);
                } catch (IOException ex) {
                    Logger.getLogger(NodoInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    n.new ServidorArchivos().start();                    

                    n.new ClienteMulticast().start();
                } catch (IOException ex) {
                    Logger.getLogger(NodoInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    if(!n.conectarASN()) return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(NodoInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(NodoInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
