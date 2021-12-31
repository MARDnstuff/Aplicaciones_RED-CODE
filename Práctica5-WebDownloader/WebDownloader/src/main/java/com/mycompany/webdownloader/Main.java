/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.webdownloader;

import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*; 

/**
 *
 * @author Mauricio
 */
public class Main {
    static LinkManage downloadFilesList = new LinkManage();
    static final int MAX_T = 10; 
    
    
    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in); //System.in is a standard input stream
        System.out.println("Ingresa el link del sitio web a descargar: ");
        String url= sc.nextLine(); //reads string.
       
        try {
            URL webUrl = new URL(url);
            ExecutorService pool = Executors.newFixedThreadPool(MAX_T);
            pool.execute(new FileManage(webUrl, downloadFilesList));            
        } catch (Exception e) {
           e.printStackTrace();
        }
        
    }
}
