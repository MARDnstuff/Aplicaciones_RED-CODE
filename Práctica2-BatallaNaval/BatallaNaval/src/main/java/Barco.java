
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mauricio
 */
public class Barco {
    /*
    1 - Destructor (2 casillas) - 3 barcos
    2 - Crucero (3 casillas) - 2 barcos
    3 - Acorazado (4 casillas) - 1 barco
    4 - Submarino (5 casillas) - 1 barco
    */
    int tipo;
    List<Casilla> casillas=new ArrayList<Casilla>();
    List<Casilla> casillasBombardeadas=new ArrayList<Casilla>();
    public boolean barcoHundido;

    public Barco() {
        barcoHundido = false;
    }
    
    public void rellenarCasillas(int x, int y){
        Casilla casilla = new Casilla(x,y);
        casillas.add(casilla);
    }
    public void bombardearCasilla(int x, int y){
        int i = 0;
        while(i < casillas.size() && (casillas.get(i).getX() != x && casillas.get(i).getX() != y)){
            
        }
    }

   
    
    /*public Casilla getCasilla(){
        
    }*/
    public Casilla getCasillaInicio(){
        return casillas.get(0);
    }
}
