
import java.io.Serializable;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mauricio
 */
public class Casilla implements Serializable{
    private static final long serialVersionUID = 6529685098267757690L;
    private int flag;
    private int x;
    private int y;
    private boolean ship;
    private boolean bombarded;
    
    public Casilla(){
        this.x = -1;
        this.y = -1;
    }
    
    public Casilla(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public int getFlag (){
        return this.flag;
    }
    
    public void setFlag (int Flag_value){
        this.flag = Flag_value;
    }
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isShip() {
        return ship;
    }

    public void setShip(boolean ship) {
        this.ship = ship;
    }

    public boolean isBombarded() {
        return bombarded;
    }

    public void setBombarded(boolean bombarded) {
        this.bombarded = bombarded;
    }
    
    public int getRandom(){
        Random num = new Random();
        int n = num.nextInt(9-0+1) + 0;
        return n;
    }
}

