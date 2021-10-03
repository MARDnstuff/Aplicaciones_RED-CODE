/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mauricio
 */
public class Casilla {
    private int x;
    private int y;
    private boolean ship;
    private boolean bombarded;

    public Casilla(int x, int y){
        this.x = x;
        this.y = y;
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
}
