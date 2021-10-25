/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatmulticast.backend;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
//import java.io.*;

/**
 *
 * @author axel
 */
public class Server {
    public static void main(String[] args){
        Vector<String> usersList = new Vector<String>();
        String nickName;
        try{
            int pto= 1234;
            //NetworkInterface ni = NetworkInterface.getByName("eth2");
            NetworkInterface ni = NetworkInterface.getByIndex(1);
            System.out.println("\nElegiste "+ni.getDisplayName());
            MulticastSocket s = new MulticastSocket(pto);
            s.setReuseAddress(true);
            s.setTimeToLive(255);
            InetAddress gpo = InetAddress.getByName("230.1.1.1");
            //InetAddress gpo = InetAddress.getByName("ff3e:40:2001::1");
            SocketAddress dir;
            try{
                 dir = new InetSocketAddress(gpo,pto);
            }catch(Exception e){
              e.printStackTrace();
               return;
            }//catch
            s.joinGroup(dir, ni);
            for(;;){
                DatagramPacket pkt = new DatagramPacket(new byte[65535],65535);
                System.out.println("Listo para recibir mensajes...");
                //3) Accept an incoming datagrama
                s.receive(pkt);
                //4) Retrieve the data from the buffer
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(pkt.getData()));
                Message message = (Message)ois.readObject();
                nickName = message.getSender();
                
                //join message
                if(message.getType() == 1){
                    usersList.add(message.getSender());
                    message.setUsersList(usersList);
                    message.setType(4);
                    ByteArrayOutputStream baos= new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(message);
                    oos.flush();
                    byte[] b = baos.toByteArray();
                    DatagramPacket pktList = new DatagramPacket(b,b.length,gpo,pto);
                    s.send(pkt);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }//main
}
