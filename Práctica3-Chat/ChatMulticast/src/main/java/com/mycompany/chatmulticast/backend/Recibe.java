/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatmulticast.backend;

import java.awt.Color;
import java.awt.List;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.URL;
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
    int[] usersIndexes;
    Vector<String> usersList;
    String msg, startMessage, middleMessage="", endMessage;
    String nickName;
    String path;
    
    public Recibe(
        MulticastSocket m, 
        String nickName, 
        JEditorPane chatPane, 
        DefaultListModel 
        usersModel,int[] usersIndexes,
        JComboBox userSelected,
        String startMessage,
        String endMessage
    ){
        this.socket=m;
        this.chatPane = chatPane;
        this.usersModel = usersModel;
        this.usersIndexes = usersIndexes;
        this.userSelected = userSelected;
        this.nickName = nickName;
        this.startMessage = startMessage;
        this.endMessage = endMessage;
        File f = new File("");
        String path = f.getAbsolutePath()+"\\src\\img\\emoji";
        this.path=path;
        System.out.println("Path: " + this.path);
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
                String textInChat = this.chatPane.getText();
                System.out.println("Message recibe: " + textInChat);
                
                
                switch(message.getType()){
                    //Join Message
                    case 1:
                        if(!this.nickName.equalsIgnoreCase(message.getSender())){
                            //this.chatPane.setText(textInChat+ "\n\n\n" + message.getSender() + " se ha unido al grupo");
                            /*this.middleMessage = this.middleMessage +"  <tr>\n" +
                            "    <td>" + message.getSender() + " se ha unido al grupo </td>\n" + "<td></td>\n" +
                            "    </tr>";*/
                            this.middleMessage = this.middleMessage +"<p style='color:blue'> " + message.getSender() + " se ha unido al grupo " + "</p>\n";
                            this.chatPane.setText(startMessage+middleMessage);
                            //this.chatPane.setText(startMessage+middleMessage+endMessage);
                            //this.chatPane.setForeground(Color.BLUE);
                        }
                        else{
                            /*this.middleMessage = this.middleMessage +"  <tr>\n" +
                            "    <td> Bienvenido " + message.getSender() + ". A partir de ahora puesdes enviar "
                                    + "y recibir mensajes </td>\n" + "<td></td>\n" +
                            "    </tr>";
                            this.chatPane.setText(startMessage+middleMessage+endMessage);*/
                            this.middleMessage = this.middleMessage +"<p style='color:#00e676'> Bienvenido " + message.getSender() + ". A partir de ahora puedes enviar y recibir mensajes" + "</p>";
                            this.chatPane.setText(startMessage+middleMessage);
                            this.chatPane.setForeground(Color.BLUE); 
                        }
                        break;
                    //Multicast message
                    case 2:
                        /*this.middleMessage = this.middleMessage +"  <tr>\n" +
                        "    <td>" + "[ " + message.getSender() + " ] : </td>\n" +
                        "    <td>" + message.getMessage() +"</td>\n" +
                        "  </tr>";
                        this.chatPane.setText(startMessage+middleMessage+endMessage);*/
                        //this.chatPane.setText(textInChat+ "\n\n\n[" + message.getSender() + "] :       " + message.getMessage());
                        if(this.nickName.equals(message.getSender())){
                            this.middleMessage = this.middleMessage + "<p style='color:#00e676'> [Tú] :     "  + message.getMessage() + "</p>\n";
                            this.chatPane.setText(startMessage+middleMessage);
                        }
                        else{
                            this.middleMessage = this.middleMessage + "<p style='color:blue'> [" + message.getSender() + "]  :              " + message.getMessage() + "</p>\n";
                            this.chatPane.setText(startMessage+middleMessage);
                        }
                        //this.chatPane.setForeground(Color.BLUE);
                       
                        break;
                    //Private Message
                    case 3:
                        if(this.nickName.equals(message.getAddressee())){
                            /*this.middleMessage = this.middleMessage + "  <tr>\n" +
                            "    <td>" + "[ " + message.getSender() + " ] : </td>" +
                            "    <td>" + message.getMessage() +"</td>\n" +
                            "  </tr>";
                            this.chatPane.setText(startMessage+middleMessage+endMessage);*/
                            this.middleMessage = this.middleMessage + "<p style='color:blue'> [Privado] [" + message.getSender() + "] :     " + message.getMessage() + "</p>\n";
                            this.chatPane.setText(startMessage+middleMessage);
                            //this.chatPane.setText(textInChat+ "\n\n\n[" + message.getSender() + "]        " + message.getMessage());
                        }
                        else if(this.nickName.equals(message.getSender())){
                            /*this.middleMessage = this.middleMessage + "  <tr>\n" +
                            "    <td>" + "[ " + message.getSender() + " ] : </td>" +
                            "    <td>" + message.getMessage() +"</td>\n" +
                            "  </tr>";
                            this.chatPane.setText(startMessage+middleMessage+endMessage);*/
                            this.middleMessage = this.middleMessage + "<p style='color:#00e676'> [Privado] [Tú] [" + message.getAddressee() + "] :    "  + message.getMessage() + "</p>\n";
                            this.chatPane.setText(startMessage+middleMessage);
                            //this.chatPane.setText(textInChat+ "\n\n\n[" + message.getSender() + "]        " + message.getMessage());
                        }
                        break;
                    //online users' List
                    case 4:
                        //this.usersList = message.getUsersList();
                        this.usersModel.clear();
                        this.userSelected.removeAllItems();
                        this.userSelected.addItem("Todos");
                        for(int i = 0; i < message.getUsersList().size(); i++){
                            //Users online
                            if(message.getUsersList().get(i).equalsIgnoreCase(this.nickName)){
                                this.usersModel.addElement(message.getUsersList().get(i) + "(Tú)");
                                this.userSelected.addItem(message.getUsersList().get(i) + "(Tú)");
                            }
                            else{
                                this.usersModel.addElement(message.getUsersList().get(i));
                                this.userSelected.addItem(message.getUsersList().get(i));
                            } 
                        }
                        break;
                    case 5:
                        if(!this.nickName.equalsIgnoreCase(message.getSender())){
                            //this.chatPane.setText(textInChat+ "\n\n\n" + message.getSender() + " ha abandonado el grupo");
                            /*this.middleMessage = this.middleMessage +"  <tr>\n" +
                            "    <td>" + message.getSender() + " ha abandonado el grupo </td>\n" + "<td></td>\n" +
                            "    </tr>";
                            this.chatPane.setText(startMessage+middleMessage+endMessage);*/
                            this.middleMessage = this.middleMessage +"<p style='color:red'> " + message.getSender() + " ha salido de la reunión " + "</p>\n";
                            this.chatPane.setText(startMessage+middleMessage);
                            //this.chatPane.setForeground(Color.BLUE);
                        }
                        break;
                    //Emoji multicast
                    case 6:
                        int nEmoji = Integer.parseInt(message.getMessage());
                        String path = this.path + (nEmoji+1) + ".png";
                        File emojiFile = new File(path);
                        URL emojiURL = emojiFile.toURL();
                        System.out.println("Emoji Path: " + path);
                        if(this.nickName.equals(message.getSender())){
                            this.middleMessage = this.middleMessage + "<p style='color:#00e676'> [Tú] :     "  + "</p><br>\n" + "<img src=\'"+emojiURL+"\'width=\'50\' height=\'50\'></img>\n";
                            this.chatPane.setText(startMessage+middleMessage);
 
                        }
                        else{
                            this.middleMessage = this.middleMessage + "<p style='color:blue'> [" + message.getSender() + "]  :              "+ "</p><br>\n" + "<img src=\'"+emojiURL+"\'width=\'50\' height=\'50\'></img>\n";
                            this.chatPane.setText(startMessage+middleMessage);
                        }
                        break;
                    //Emoji private
                    case 7:
                        int nEmoji1 = Integer.parseInt(message.getMessage());
                        String path1 = this.path + (nEmoji1+1) + ".png";
                        File emojiFile1 = new File(path1);
                        URL emojiURL1 = emojiFile1.toURL();
                        System.out.println("Emoji Path: " + path1);
                        if(this.nickName.equals(message.getAddressee())){
                            this.middleMessage = this.middleMessage + "<p style='color:#00e676'> [Privado] [" + message.getSender() + "]  :              "+ "</p><br>\n" + "<img src=\'"+emojiURL1+"\'width=\'50\' height=\'50\'></img>\n";
                            this.chatPane.setText(startMessage+middleMessage);
 
                        }
                        else if(this.nickName.equals(message.getSender())){
                            this.middleMessage = this.middleMessage + "<p style='color:blue'> [Privado] [Tú] [" + message.getAddressee() + "] :    "+ "</p><br>\n" + "<img src=\'"+emojiURL1+"\'width=\'50\' height=\'50\'></img>\n";
                            this.chatPane.setText(startMessage+middleMessage);
                        }
                        break;
                        
                        //this.chatPane.setForeground(Color.BLUE);
                        
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
