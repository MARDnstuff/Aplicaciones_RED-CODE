/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mauricio
 */
public class Tablero {
    Casilla[][] casillas;
    
    int numRows;
    int numColumns;
    
    public Tablero(){
        this.numRows = 10;
        this.numColumns = 10;
    }
    
    public void initializeCasillas(){
       casillas = new Casilla[this.numRows][this.numColumns];
        for (int i = 0; i < casillas.length; i++) {
            for (int j = 0; j < casillas[i].length; j++) {
                casillas[i][j] = new Casilla(i,j);
            }
        }
    }
    
    //TODO: Implement the method for generate ships randomly
}
