/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectofinalacr;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author setjafet
 */
public interface RMI_SN_N extends Remote {
    
    String SolicitarConexion(String id) throws RemoteException;
    void ActualizarListaArchivos (HashMap<String, String> listaArchivos, String idNodo) throws RemoteException;
    HashMap<String, List<String>> BuscarArchivo(String nombre) throws RemoteException;
    HashMap<String, List<String>> BuscarArchivoSN(String nombre) throws RemoteException;
    
}
