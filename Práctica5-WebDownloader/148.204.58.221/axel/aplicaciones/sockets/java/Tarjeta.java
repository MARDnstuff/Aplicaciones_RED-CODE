/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.*;
import java.util.*;
/**
 *
 * @author admin
 */
public class Tarjeta {

    public static void main(String[] args)throws Exception{
        //for(NetworkInterface ni :  NetworkInterface.getNetworkInterfaces())
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();   
        while (interfaces.hasMoreElements())   
        {   
            NetworkInterface nif = interfaces.nextElement();   
            System.out.println("Tarjeta: "+nif.getDisplayName());   
            System.out.println("MTU: "+nif.getMTU());

        Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();   
        while (inetAddresses.hasMoreElements()){   
            InetAddress inetAddr = inetAddresses.nextElement();   
            System.out.println("Host: "+inetAddr.getCanonicalHostName());   
            System.out.println("IP: "+inetAddr.getHostAddress());   
        }//while   
    }//while

        }//main   
    }//class
