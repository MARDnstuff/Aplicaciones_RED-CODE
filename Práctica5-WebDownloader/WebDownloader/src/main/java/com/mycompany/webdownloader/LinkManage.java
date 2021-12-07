/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.webdownloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Mauricio
 */
public class LinkManage {
    private List<String> list = new ArrayList<>();
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    
    public LinkManage(){
    }
 
    public LinkManage(String... initialElements) { //genérico:Clase, Interfaz        
        list.addAll(Arrays.asList(initialElements));
    }
 
    public void add(String link) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock(); 
        try {
            list.add(link);
        } finally {
            writeLock.unlock();
        }
    }
 
    public String get(int index) {
        Lock readLock = rwLock.readLock();
        readLock.lock(); 
        try {
            return list.get(index);
        } finally {
            readLock.unlock();
        }
    }
    
    public boolean exists(String path){
        
        int aux = 0;
        
        Lock readLock = rwLock.readLock();
        readLock.lock(); 
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).equals(path)){
                return true;
            }
        }
        
        list.add(path);
        try {
            return false;
        } finally {
            readLock.unlock();
        }
    }
 
    public int size() {
        Lock readLock = rwLock.readLock();
        readLock.lock(); 
        try {
            return list.size();
        } finally {
            readLock.unlock();
        }
    }
    
}
