/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatmulticast.backend;

import java.io.BufferedReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author Mauricio
 */
public class Send extends Thread{
    MulticastSocket socket;
    BufferedReader br;
    String message;
    public Send(MulticastSocket m, BufferedReader br, String message){
        this.socket=m;
        this.br=br;
        this.message = message;
    }
    public void run(){
        try{
           //BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
           String dir= "230.1.1.1";
           int pto=1234;
           InetAddress gpo = InetAddress.getByName(dir);
            for(;;){
                System.out.println("Escribe un mensaje para ser enviado:");
                String mensaje= br.readLine();
                byte[] b = mensaje.getBytes();
                DatagramPacket p = new DatagramPacket(b,b.length,gpo,pto);
                socket.send(p);
            }//for
        }catch(Exception e){
            e.printStackTrace();
        }//catch
    }//run
}
