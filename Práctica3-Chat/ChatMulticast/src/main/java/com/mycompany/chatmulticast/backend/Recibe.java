/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatmulticast.backend;

import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 *
 * @author Mauricio
 */
class Recibe extends Thread{
    MulticastSocket socket;
    public Recibe(MulticastSocket m){
        this.socket=m;
    }
    public void run(){
       try{
           
        for(;;){
           DatagramPacket p = new DatagramPacket(new byte[65535],65535);
            System.out.println("Listo para recibir mensajes...");
           socket.receive(p);
           String msj = new String(p.getData(),0,p.getLength());
            System.out.println("Mensaje recibido: "+msj);
       } //for
       }catch(Exception e){
           e.printStackTrace();
       }//catch
    }//run
}//class
