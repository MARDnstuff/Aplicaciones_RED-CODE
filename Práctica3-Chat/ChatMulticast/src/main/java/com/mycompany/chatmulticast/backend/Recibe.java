/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatmulticast.backend;

import java.awt.Color;
import java.awt.List;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author Mauricio
 */
public class Recibe extends Thread{
    MulticastSocket socket;
    JEditorPane chatPane; 
    DefaultListModel usersModel;
    JComboBox userSelected;
    Vector<String> usersList;
    String msg;
    String nickName;
    public Recibe(MulticastSocket m, String nickName, JEditorPane chatPane, DefaultListModel usersModel, JComboBox userSelected){
        this.socket=m;
        this.chatPane = chatPane;
        this.usersModel = usersModel;
        this.userSelected = userSelected;
        this.nickName = nickName;
    }
    public void run(){
       try{
            for(;;){
                DatagramPacket pkt = new DatagramPacket(new byte[65535],65535);
                System.out.println("Listo para recibir mensajes...");
                //3) Accept an incoming datagrama
                socket.receive(pkt);
                //4) Retrieve the data from the buffer
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(pkt.getData()));
                Message message = (Message)ois.readObject();
                System.out.println("Message: " + message.getMessage());
                String textInChat = this.chatPane.getText();
                
                
                switch(message.getType()){
                    //Join Message
                    case 1:
                        if(!this.nickName.equalsIgnoreCase(message.getSender())){
                            this.chatPane.setText(textInChat+ "\n\n\n" + message.getSender() + " se ha unido al grupo");
                            this.chatPane.setForeground(new Color(0,204,0));
                        }
                        break;
                    //Multicast message
                    case 2:
                        if(!this.nickName.equalsIgnoreCase(message.getSender())){
                            this.chatPane.setText(textInChat+ "\n\n\n[" + message.getSender() + "] :       " + message.getMessage());
                            this.chatPane.setForeground(new Color(0,204,0));
                        }
                        break;
                    case 3:
                        if(this.nickName.equals(message.getAddressee())){
                            this.chatPane.setText(textInChat+ "\n\n\n[" + message.getSender() + "]        " + message.getMessage());
                        }
                        break;
                    case 4:
                        this.usersList = message.getUsersList();
                        for(int i = 0; i < this.usersList.size(); i++){
                            //Users online
                            this.usersModel.addElement(this.usersList.get(i));
                            //Selected user
                            this.userSelected.addItem(this.usersList.get(i));
                        }
                        break;
                }
                /*DatagramPacket p = new DatagramPacket(new byte[65535],65535);
                socket.receive(p);
                msg = new String(p.getData(),0,p.getLength());
                System.out.println("Mensaje recibido en Recibe: " + msg);
                String textInChat = this.chatPane.getText();
                this.chatPane.setText(textInChat+ "\n\n\n" +msg);*/
            } //for
       }catch(Exception e){
           e.printStackTrace();
       }//catch
    }//run

    public String getMsg() {
        return msg;
    }
    
}//class
