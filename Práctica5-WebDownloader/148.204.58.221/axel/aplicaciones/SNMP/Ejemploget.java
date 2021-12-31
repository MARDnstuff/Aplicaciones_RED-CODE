
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
public class Ejemploget {
   public static void main(String[] args){
        try{
            HashMap<String, String> hmap = new HashMap<String, String>();

      /*Agregamos datos al hashmap*/
      hmap.put("ipaddr", "127.0.0.1");
      //hmap.put("oid", ".318.1.3.7");
      hmap.put("oid", ".1.3.6.1.2.1.1.1.0");  //1.3.6.1.2.1.1
      hmap.put("communityString", "public");
      hmap.put("version", "1");
      

           // Snmpwalk s = new Snmpwalk();
            SnmpGet s = new SnmpGet();
            s.snmpGet("127.0.0.1","public",".1.3.6.1.2.1.1.1.0",1); //String strAddress, String community, String strOID, int snmpVersion
            
            System.out.println("Termina programa");
        }catch(Exception e){
            e.printStackTrace();
        }
            
    } 
}
