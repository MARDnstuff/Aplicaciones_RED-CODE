/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servers;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 *
 * @author Mauricio
 */
public interface busquedaRMI extends Remote{
       searchResult buscar(String file) throws RemoteException;
}
