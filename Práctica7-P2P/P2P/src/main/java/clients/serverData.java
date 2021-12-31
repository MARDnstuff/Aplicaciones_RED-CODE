/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clients;
import java.io.Serializable;
/**
 *
 * @author Mauricio
 */
public class serverData implements Serializable{
    String address;
    int port;
    int temp;
    
    public serverData(String address, int port, int temp) {
        this.address = address;
        this.port = port;
        this.temp = temp;
    }
    
    public serverData() {}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    
}
