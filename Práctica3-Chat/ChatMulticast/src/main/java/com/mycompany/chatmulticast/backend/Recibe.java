/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatmulticast.backend;

import java.net.DatagramPacket;
import java.net.MulticastSocket;
import javax.swing.JEditorPane;

/**
 *
 * @author Mauricio
 */
public class Recibe extends Thread{
    MulticastSocket socket;
    JEditorPane chatPane; 
    String msg;
    public Recibe(MulticastSocket m, JEditorPane chatPane){
        this.socket=m;
        this.chatPane = chatPane;
    }
    public void run(){
       try{
            for(;;){
                DatagramPacket p = new DatagramPacket(new byte[65535],65535);
                System.out.println("Listo para recibir mensajes...");
                socket.receive(p);
                msg = new String(p.getData(),0,p.getLength());
                System.out.println("Mensaje recibido en Recibe: " + msg);
                String textInChat = this.chatPane.getText();
                this.chatPane.setText(textInChat+ "\n\n\n" +msg);
            } //for
       }catch(Exception e){
           e.printStackTrace();
       }//catch
    }//run

    public String getMsg() {
        return msg;
    }
    
}//class
