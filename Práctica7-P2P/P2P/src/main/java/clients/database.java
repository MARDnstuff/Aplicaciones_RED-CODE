/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clients;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mauricio
 */
public class database {
   
    private List<serverData> ServersList = new ArrayList<>();
    private searchResult fileFound = new searchResult(); 
    private String serverFileFound = new String();

    public String getServerFileFound() {
        return serverFileFound;
    }

    public void setServerFileFound(String serverFileFound) {
        this.serverFileFound = serverFileFound;
    }

    public searchResult getFileFound() {
        return fileFound;
    }

    public void setFileFound(searchResult fileFound) {
        this.fileFound = fileFound;
    }

    public List<serverData> getServersList() {
        return ServersList;
    }
    
    public void addServer(serverData e) {
        ServersList.add(e);
    }

    public void setServersList(List<serverData> ServersList) {
        this.ServersList = ServersList;
    }

}
