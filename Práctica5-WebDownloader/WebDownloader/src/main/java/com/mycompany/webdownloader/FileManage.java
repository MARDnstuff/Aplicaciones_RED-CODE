/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.webdownloader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author Mauricio
 */
public class FileManage extends Thread {
    private LinkManage downloadFilesList;
    private URL url;
    static String path = "C:\\Users\\Mauricio\\Documents\\ESCOM\\5semestre\\RedesII\\Prácticas\\";
    
    public FileManage(URL url, LinkManage downloadFilesList){
        this.url = url;
        this.downloadFilesList = downloadFilesList;
    }
    
    public void run(){
        try {
            download(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    public void download(URL url) throws IOException{
        
        String line;
        String name;
        String fileName;
        String newLink;
        String auxNewLink;
        String newName;
        int index;
        //boolean flag = false;
        
        try{
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            fileName = conn.getURL().getFile();
            name = conn.getURL().getHost() + fileName;
            if(conn.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN || conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                return;
            
            
            
            if(!downloadFilesList.exists(name)){
                
                newName = name;
                //Algorithm to know if the root path is a file or a 
                //System.out.println("Downloading: " + name);
                
                //File - Creating a folder for a File
                if(fileName.contains(".")){
                    System.out.println("Downloading the file: "+ newName);
                    newName = name.substring(0, name.lastIndexOf("/"));
                    File f = new File(path + newName);
                    f.mkdirs();
                    f.setWritable(true);
                }
                //Folder
                else{
                    System.out.println("Downloading the folder: " + name);
                    File f = new File(path + name);
                    f.mkdirs();
                    f.setWritable(true);
                }
                                 
                if(conn.getContentType().contains("text/html")){                    
                    
                    //Folder
                    if(!name.contains(".html")){
                        /*if(name.endsWith("/")){
                            name = name.substring(0, name.length() - 1);
                        }*/
                        name = name + "index.html";
                        //System.out.println("Folder in html: " + name);
                    }
                    
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //DataOutputStream dos = new DataOutputStream(new FileOutputStream(path + name));
                    BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path + name)));

                    while((line = br.readLine()) != null) {
                        line = line + "\n";

                        if(line.contains("href=\"")){
                            //System.out.println("");
                            //System.out.print(line);
                            
                            index = line.indexOf("href=") + 6;
                            //getting the link from href
                            newLink = line.substring(index, line.indexOf("\"", index));
                            
                            auxNewLink = newLink;
          
                            //System.out.println(newLink);                      
                            
                            if(!newLink.contains("?") && !newLink.contains("@")){
                                if(newLink.startsWith("/")){
                                    newLink = url.getProtocol() + "://" + url.getHost() + newLink;
                                } else {
                                    newLink = url.getProtocol() + "://" + url.getHost() + url.getFile() + newLink;
                                }
                                System.out.println(newLink);
                                
                                download(new URL(newLink));
                            }
                            
                            line  = line.replace("https://", path);
                            line  = line.replace("http://", path);
                            
                            if(!auxNewLink.startsWith("/")){
                                line  = line.replace("href=\"", "href=\"" + path + url.getHost() + url.getFile() + "/");
                            }
                            
                        }
                        
                        if(line.contains("src=\"")){
                            index = line.indexOf("src") + 5;
                            newLink = line.substring(index, line.indexOf("\"", index));
                        
                            if(!newLink.contains("?")){
                                if(newLink.startsWith("/")){
                                    newLink = url.getProtocol() + "://" + url.getHost() + newLink;
                                } else {
                                    newLink = url.getProtocol() + "://" + url.getHost() + url.getFile() + newLink;
                                }
                                //System.out.println(newLink);
                                
                                download(new URL(newLink));
                            }
                            
                            line  = line.replace("https://", path);
                            line  = line.replace("http://", path);
                            line  = line.replace("/icons/", "../icons/");
                            
                        }
                                                
                        bw.write(line);
                        bw.newLine();
                        //dos.flush();
                    }
                    
                    //dos.close();
                    br.close();
                    bw.close();
                
                } else {
                                        
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    DataOutputStream dos = new DataOutputStream(new FileOutputStream(path + name));

                    long recibidos = 0;
                    int n;

                    while(recibidos < conn.getContentLengthLong()){
                        byte[] b = new byte[2000];
                        n = dis.read(b);
                        recibidos = recibidos + n;

                        dos.write(b, 0, n);
                        dos.flush();
                    }

                    dis.close();
                    dos.close();

                }
            
            }
        } catch(Exception e){
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            System.out.println("connection.getResponseCode(): " + conn.getResponseCode());
            //e.printStackTrace();
        }
        
    }
    
}
