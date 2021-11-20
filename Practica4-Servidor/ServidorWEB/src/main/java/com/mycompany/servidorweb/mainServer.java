package com.mycompany.servidorweb;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

class Manejador implements Runnable {

    protected Socket socket;
    protected PrintWriter pw;
    protected BufferedOutputStream bos;
    protected BufferedReader br;
    DataOutputStream dos;
    DataInputStream dis;
    protected String FileName;

    public Manejador(Socket _socket) {
        this.socket = _socket;
    }//Manejador

    public void run() {
        try {

            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            byte[] b = new byte[1024];
            int t = dis.read(b);
            String peticion = new String(b, 0, t);
            System.out.println("t: " + t);

            if (peticion == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("<html><head><title>Servidor WEB\n");
                sb.append("</title><body bgcolor=\"#AACCFF\"<br>Linea Vacia</br>\n");
                sb.append("</body></html>\n");
                dos.write(sb.toString().getBytes());
                dos.flush();
                socket.close();
                return;
            }//if

            System.out.println("\nCliente Conectado desde: " + socket.getInetAddress());
            System.out.println("Por el puerto: " + socket.getPort());
            System.out.println("Datos: " + peticion + "\r\n\r\n");

            StringTokenizer st1 = new StringTokenizer(peticion, "\n");
            String line = st1.nextToken();

            if (line.toUpperCase().startsWith("GET")) {

                if (line.indexOf("?") == -1) {
                    getArch(line);
                    if (FileName.compareTo("") == 0) {
                        SendA("index.html", dos, "text/html");
                    } else {
                        String[] s = FileName.split("\\.");
                        String p1 = s[s.length - 1];
                        String Ctype;
                        switch (p1) {
                            case "html":
                            case "htm": {
                                Ctype = "text/html";
                                break;
                            }
                            case "pdf": {
                                Ctype = "application/pdf";
                                break;
                            }
                            case "jpg":
                            case "jpeg": {
                                Ctype = "image/jpeg";
                                break;
                            }
                            default:
                                Ctype = "text/html";
                        }//switch

                        if (isThereFile(FileName)) {
                            SendA(FileName, dos, Ctype);
                        } else {
                            SendA("NotFound.html", dos, "text/html");
                        }//if 
                    }//if

                } else {
                    StringTokenizer tokens = new StringTokenizer(line, "?");
                    String req_a = tokens.nextToken();
                    String req = tokens.nextToken();
                    System.out.println("Token1: " + req_a);
                    System.out.println("Token2: " + req);
                    String parametros = req.substring(0, req.indexOf(" ")) + "\n";
                    System.out.println("parametros: " + parametros);
                    StringBuffer respuesta = new StringBuffer();
                    respuesta.append("HTTP/1.0 200 Okay \n");
                    String fecha = "Date: " + new Date() + " \n";
                    respuesta.append(fecha);
                    String tipo_mime = "Content-Type: text/html \n\n";
                    respuesta.append(tipo_mime);
                    respuesta.append("<html><head><title>SERVIDOR WEB</title></head>\n");
                    respuesta.append("<body bgcolor=\"#AACCFF\"><center><h1><br>Parametros Obtenidos..</br></h1><h3><b>\n");
                    respuesta.append(parametros);
                    respuesta.append("</b></h3>\n");
                    respuesta.append("</center></body></html>\n\n");
                    System.out.println("Respuesta: " + respuesta);
                    dos.write(respuesta.toString().getBytes());
                    dos.flush();
                    dos.close();
                    socket.close();
                }//if

            } else if (line.toUpperCase().startsWith("POST")) {
                List<String> lista_P = new ArrayList<String>();
                StringTokenizer str = new StringTokenizer(peticion, "\n");

                while (str.hasMoreTokens()) {
                    lista_P.add(str.nextToken());
                }//while                

                StringBuffer respuesta = new StringBuffer();
                respuesta.append("HTTP/1.0 200 Okay \n");
                String fecha = "Date: " + new Date() + " \n";
                respuesta.append(fecha);
                String tipo_mime = "Content-Type: text/html \n\n";
                respuesta.append(tipo_mime);
                respuesta.append("<html><head><title>SERVIDOR WEB</title></head>\n");
                respuesta.append("<body bgcolor=\"#AACCFF\"><center><h1><br>Parametros Obtenidos..</br></h1><h3><b>\n");
                respuesta.append(lista_P.get(lista_P.size() - 1));
                respuesta.append("</b></h3>\n");
                respuesta.append("</center></body></html>\n\n");
                System.out.println("Respuesta: " + respuesta);
                dos.write(respuesta.toString().getBytes());
                dos.flush();
                dos.close();
                socket.close();
            } else if (line.toUpperCase().startsWith("HEAD")) {
                if (line.indexOf("?") == -1) {
                    getArch(line);
                    if (isThereFile(FileName)) {
                        String[] s = FileName.split("\\.");
                        String p1 = s[s.length - 1];
                        String Ctype;
                        switch (p1) {
                            case "html":
                            case "htm": {
                                Ctype = "text/html";
                                break;
                            }
                            case "pdf": {
                                Ctype = "application/pdf";
                                break;
                            }
                            case "jpg":
                            case "jpeg": {
                                Ctype = "image/jpeg";
                                break;
                            }
                            default:
                                Ctype = "text/html";
                        }

                        StringBuffer respuesta = new StringBuffer();
                        respuesta.append("HTTP/1.0 200 Okay \n");
                        String fecha = "Date: " + new Date() + " \n";
                        respuesta.append(fecha);
                        String tipo_mime = "Content-Type: " + Ctype + " \n\n";
                        respuesta.append(tipo_mime);
                        System.out.println("Respuesta: " + respuesta);
                        dos.write(respuesta.toString().getBytes());
                        dos.flush();
                        dos.close();
                        socket.close();

                    } else {
                        String[] s = FileName.split("\\.");
                        String p1 = s[s.length - 1];
                        String Ctype;
                        switch (p1) {
                            case "html":
                            case "htm": {
                                Ctype = "text/html";
                                break;
                            }
                            case "pdf": {
                                Ctype = "application/pdf";
                                break;
                            }
                            case "jpg":
                            case "jpeg": {
                                Ctype = "image/jpeg";
                                break;
                            }
                            default:
                                Ctype = "text/html";
                        }

                        StringBuffer respuesta = new StringBuffer();
                        respuesta.append("HTTP/1.0 404 Not Found \n");
                        String fecha = "Date: " + new Date() + " \n";
                        respuesta.append(fecha);
                        String tipo_mime = "Content-Type: " + Ctype + " \n\n";
                        respuesta.append(tipo_mime);
                        System.out.println("Respuesta: " + respuesta);
                        dos.write(respuesta.toString().getBytes());
                        dos.flush();
                        dos.close();
                        socket.close();

                    }//if

                } else {
                    StringTokenizer tokens = new StringTokenizer(line, "?");
                    String req_a = tokens.nextToken();
                    String req = tokens.nextToken();
                    System.out.println("Token1: " + req_a);
                    System.out.println("Token2: " + req);
                    String parametros = req.substring(0, req.indexOf(" ")) + "\n";
                    System.out.println("parametros: " + parametros);
                    StringBuffer respuesta = new StringBuffer();
                    respuesta.append("HTTP/1.0 200 Okay \n");
                    String fecha = "Date: " + new Date() + " \n";
                    respuesta.append(fecha);
                    String tipo_mime = "Content-Type: text/html \n\n";
                    respuesta.append(tipo_mime);
                    System.out.println("Respuesta: " + respuesta);
                    dos.write(respuesta.toString().getBytes());
                    dos.flush();
                    dos.close();
                    socket.close();
                }//if

            } else if (line.toUpperCase().startsWith("PUT")) {
                System.out.println("PUT");
                getArch(line);
                List<String> lista_P = new ArrayList<String>();
                List<String> lista_C = new ArrayList<String>();
                StringTokenizer str = new StringTokenizer(peticion, "\n");

                while (str.hasMoreTokens()) {
                    lista_P.add(str.nextToken());
                }//while   

                for (int i = 10; i < lista_P.size(); i++) {
                    lista_C.add(lista_P.get(i));
                }//for
                System.out.println(lista_C.size());
                try {
                    if (isThereFile(FileName)) {
                        File f = new File("C:\\Users\\reyma\\Desktop\\Practicas_RED\\Servidor_Ej\\ServidorWEB");
                        File[] L_f = f.listFiles();
                        int x = 0;
                        for (int i = 0; i < L_f.length; i++) {
                            if (L_f[i].getName().compareTo(FileName) == 0) {
                                x = i;
                                break;
                            }//if
                        }//for
                        try (BufferedWriter bw = new BufferedWriter(new FileWriter(L_f[x]))) {
                            L_f[x].setWritable(true);
                            for (String cadena : lista_C) {
                                bw.write(cadena);
                            }//foreach
                        }
                        String[] s = FileName.split("\\.");
                        String p1 = s[s.length - 1];
                        String Ctype;
                        switch (p1) {
                            case "html":
                            case "htm": {
                                Ctype = "text/html";
                                break;
                            }
                            case "pdf": {
                                Ctype = "application/pdf";
                                break;
                            }
                            case "jpg":
                            case "jpeg": {
                                Ctype = "image/jpeg";
                                break;
                            }
                            default:
                                Ctype = "text/html";
                        }

                        StringBuffer respuesta = new StringBuffer();
                        respuesta.append("HTTP/1.0 200 Okay \n");
                        String fecha = "Date: " + new Date() + " \n";
                        respuesta.append(fecha);
                        String tipo_mime = "Content-Type: " + Ctype + " \n\n";
                        respuesta.append(tipo_mime);
                        System.out.println("Respuesta: " + respuesta);
                        dos.write(respuesta.toString().getBytes());
                        dos.flush();
                        dos.close();
                        socket.close();
                    } else {
                        File fichero = new File("C:\\Users\\reyma\\Desktop\\Practicas_RED\\Servidor_Ej\\ServidorWEB", FileName);
                        fichero.setWritable(true);
                        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fichero))) {
                            for (String cadena : lista_C) {
                                bw.write(cadena);
                            }//foreach
                        }
                        String[] s = FileName.split("\\.");
                        String p1 = s[s.length - 1];
                        String Ctype;
                        switch (p1) {
                            case "html":
                            case "htm": {
                                Ctype = "text/html";
                                break;
                            }
                            case "pdf": {
                                Ctype = "application/pdf";
                                break;
                            }
                            case "jpg":
                            case "jpeg": {
                                Ctype = "image/jpeg";
                                break;
                            }
                            default:
                                Ctype = "text/html";
                        }

                        StringBuffer respuesta = new StringBuffer();
                        respuesta.append("HTTP/1.0 204 Created \n");
                        String fecha = "Date: " + new Date() + " \n";
                        respuesta.append(fecha);
                        String tipo_mime = "Content-Type: " + Ctype + " \n\n";
                        respuesta.append(tipo_mime);
                        System.out.println("Respuesta: " + respuesta);
                        dos.write(respuesta.toString().getBytes());
                        dos.flush();
                        dos.close();
                        socket.close();
                    }//if
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }//try-cacth
//          
            } else if (line.toUpperCase().startsWith("DELETE")) {
                getArch(line);
                File f = new File(FileName);
                String[] s = FileName.split("\\.");
                        String p1 = s[s.length - 1];
                        String Ctype;
                        switch (p1) {
                            case "html":
                            case "htm": {
                                Ctype = "text/html";
                                break;
                            }
                            case "pdf": {
                                Ctype = "application/pdf";
                                break;
                            }
                            case "jpg":
                            case "jpeg": {
                                Ctype = "image/jpeg";
                                break;
                            }
                            default:
                                Ctype = "text/html";
                        }
                if (!isThereFile(FileName)) {
                        StringBuffer respuesta = new StringBuffer();
                        respuesta.append("HTTP/1.0 404 Not Found \n");
                        String fecha = "Date: " + new Date() + " \n";
                        respuesta.append(fecha);
                        String tipo_mime = "Content-Type: " + Ctype + " \n\n";
                        respuesta.append(tipo_mime);
                        System.out.println("Respuesta: " + respuesta);
                        dos.write(respuesta.toString().getBytes());
                        dos.flush();
                        dos.close();
                        socket.close();
                    

                }else{
                    if (f.canWrite()) {
                        f.delete();
                        StringBuffer respuesta = new StringBuffer();
                        respuesta.append("HTTP/1.0 200 Okay \n");
                        String fecha = "Date: " + new Date() + " \n";
                        respuesta.append(fecha);
                        String tipo_mime = "Content-Type: " + Ctype + " \n\n";
                        respuesta.append(tipo_mime);
                        System.out.println("Respuesta: " + respuesta);
                        dos.write(respuesta.toString().getBytes());
                        dos.flush();
                        dos.close();
                        socket.close();
                    }else{
                        StringBuffer respuesta = new StringBuffer();
                        respuesta.append("HTTP/1.0 403 Forbidden \n");
                        String fecha = "Date: " + new Date() + " \n";
                        respuesta.append(fecha);
                        String tipo_mime = "Content-Type: " + Ctype + " \n\n";
                        respuesta.append(tipo_mime);
                        System.out.println("Respuesta: " + respuesta);
                        dos.write(respuesta.toString().getBytes());
                        dos.flush();
                        dos.close();
                        socket.close();
                    }
                
                }

            } else {
                dos.write("HTTP/1.0 501 Not Implemented\r\n".getBytes());
                dos.flush();
                dos.close();
                socket.close();
                //pw.println();
            }//if
            //dos.flush();
            //bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }//try - catch
    }//run

    public void getArch(String line) {
        int i;
        int f;
        if (line.toUpperCase().startsWith("GET") || line.toUpperCase().startsWith("HEAD") || line.toUpperCase().startsWith("PUT") || line.toUpperCase().startsWith("DELETE")) {
            i = line.indexOf("/");
            f = line.indexOf(" ", i);
            FileName = line.substring(i + 1, f);
        }// if
    }//getArch

    //returns true if it finds the file
    public boolean isThereFile(String FileName) {
        File ff = new File("C:\\Users\\reyma\\Desktop\\Practicas_RED\\Servidor_Ej\\ServidorWEB");
        File[] archivos = ff.listFiles();
        for (int i = 0; i < archivos.length; i++) {
            if (archivos[i].isFile() && archivos[i].getName().compareTo(FileName) == 0) {
                return true;
            }//if
        }//for
        return false;
    }//isThereFile

    public void SendA(String fileName, Socket sc, DataOutputStream dos) {
        //System.out.println(fileName);
        int fSize = 0;
        byte[] buffer = new byte[4096];
        try {
            //DataOutputStream out =new DataOutputStream(sc.getOutputStream());
            //sendHeader();
            DataInputStream dis1 = new DataInputStream(new FileInputStream(fileName));
            //FileInputStream f = new FileInputStream(fileName);
            int x = 0;
            File ff = new File("fileName");
            long tam, cont = 0;
            tam = ff.length();
            while (cont < tam) {
                x = dis1.read(buffer);
                dos.write(buffer, 0, x);
                cont = cont + x;
                dos.flush();
            }
            //out.flush();
            dis.close();
            dos.close();
        } catch (FileNotFoundException e) {
            //msg.printErr("Transaction::sendResponse():1", "El archivo no existe: " + fileName);
        } catch (IOException e) {
            //			System.out.println(e.getMessage());
            //msg.printErr("Transaction::sendResponse():2", "Error en la lectura del archivo: " + fileName);
        }// try - catch

    }//sendA - 1

    public void SendA(String arg, DataOutputStream dos1, String Ctype) {
        try {
            int b_leidos = 0;
            DataInputStream dis2 = new DataInputStream(new FileInputStream(arg));
            // BufferedInputStream bis2=new BufferedInputStream(new FileInputStream(arg));
            byte[] buf = new byte[1024];
            int x = 0;
            File ff = new File(arg);
            long tam_archivo = ff.length(), cont = 0;
            /**
             * ********************************************
             */
            String sb = "";
            sb = sb + "HTTP/1.0 200 ok\n";
            sb = sb + "Server: Axel Server/1.0 \n";
            sb = sb + "Date: " + new Date() + " \n";
            sb = sb + "Content-Type: " + Ctype + " \n";
            sb = sb + "Content-Length: " + tam_archivo + " \n";
            sb = sb + "\n";
            dos1.write(sb.getBytes());
            dos1.flush();
            /**
             * ********************************************
             */
            while (cont < tam_archivo) {
                x = dis2.read(buf);
                dos1.write(buf, 0, x);
                cont = cont + x;
                dos1.flush();

            }//while

            //bos.flush();
            dis2.close();
            dos1.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }// try - catch

    }//sendA -2

}//class - Manejador

public class mainServer implements Runnable {

    protected int puerto = 9000;
    protected ServerSocket s = null;
    protected boolean detenido = false;
    protected Thread runningThread = null;
    protected ExecutorService pool = Executors.newFixedThreadPool(1);

    public mainServer(int puerto) {
        this.puerto = puerto;
    }//mainServer - 1

    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        iniciaServidor();
        while (!detenido()) {
            Socket cl = null;
            try {
                cl = this.s.accept();
                System.out.println("Conexion aceptada..");

            } catch (IOException e) {
                if (detenido()) {
                    System.out.println("Servidor detenido.");
                    break;
                }
                throw new RuntimeException("Error al aceptar nueva conexion", e);
            }//catch
            this.pool.execute(new Manejador(cl));

        }//while
        this.pool.shutdown();
        System.out.println("Servidor detenido.");
    }//run

    private synchronized boolean detenido() {
        return this.detenido;
    }//detenido

    public synchronized void stop() {
        this.detenido = true;
        try {
            this.s.close();
        } catch (IOException e) {
            throw new RuntimeException("Error al cerrar el socket del servidor", e);
        }
    }//stop

    private void iniciaServidor() {
        try {
            this.s = new ServerSocket(this.puerto);
            System.out.println("Servicio iniciado.. esperando cliente..");
        } catch (IOException e) {
            throw new RuntimeException("No puede iniciar el socket en el puerto: " + s.getLocalPort(), e);
        }//try - catch
    }//iniciaServidor

    public static void main(String[] args) {
        mainServer server = new mainServer(8000);
        new Thread(server).start();
    }//main    
}//mainServer - class
