/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatmulticast.backend;

import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author Mauricio
 */
public class Message implements Serializable{
    private static final long serialVersionUID = 6529685098267757690L;
    /*
    1 : join message
    2 : multicast message
    3 : private message
    4 : list of users
    5 : leave message
    6 : Emoji for multicast
    7 : Emoji for private
    */
    private int type;
    private String message;
    private String sender;
    private String addressee;
    public Vector<String> usersList = new Vector<String>();

    public Message(int type, String sender){
        this.type = type;
        this.message = message;
        this.sender = sender;
    }
    
    public Message(int type, Vector<String> usersList){
        this.type = type;
        this.usersList = usersList;
    }
    
    public Message(int type, String message, String sender) {
        this.type = type;
        this.message = message;
        this.sender = sender;
    }
    
    public Message(int type, String message, String sender, String addressee) {
        this.type = type;
        this.message = message;
        this.sender = sender;
        this.addressee = addressee;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getAddressee() {
        return addressee;
    }

    public Vector<String> getUsersList() {
        return usersList;
    }
    

    public void setUsersList(Vector<String> usersList) {
        this.usersList = usersList;
    }

    public void setType(int type) {
        this.type = type;
    }
    
}
