
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author escom
 */
public class Ejemplowalk {
    public static void main(String[] args){
        //Usage: snmpwalk [-c communityName -p portNumber -v snmpVersion(1 or 2)] targetAddr oid
        try{
            HashMap<String, String> hmap = new HashMap<String, String>();

      /*Agregamos datos al hashmap*/
      hmap.put("ipaddr", "127.0.0.1");
      //hmap.put("oid", ".1.3.6.1.4.1.8072.3.2.10");  //1.3.6.1.2.1.1  //.1.3.6.1.2.1.1.1.0
      hmap.put("oid", "1.3.6.1.2.1");
      hmap.put("communityString", "public");
      hmap.put("version", "1");
      hmap.put("portNum","161");
      

           // Snmpwalk s = new Snmpwalk();
            Snmpwalk s = new Snmpwalk(hmap);
            s.doSnmpwalk();
            System.out.println("Termina programa");
        }catch(Exception e){
            e.printStackTrace();
        }
            
    }
}
