// Clase dedicada al acomodo de barcos
import java.util.*;
/**
 *
 * @author reyma
 */
public class ServerBarcos {
    private int [][] myGameBoard = new int[10][10];
    private int ErrorFlag;
    
    //Constructor
    public ServerBarcos() {
        ErrorFlag = 0;
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                myGameBoard[i][j]=-1;
            }//for
        }//for
    }
    
    //Regrese la matriz (GameBoard)
    public int[][] get_myGameBoard(){
        return myGameBoard;
    }
    
    //Estblece el valor de la coordenada [X,Y]
    public void set_myBoat_XY(int x, int y,int value){
        myGameBoard[x][y] = value;
        return;
    }
    
    //Muestra en terminal el tablero de juego del servidor
    public void show_myGameBoard(){
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                System.out.print("["+myGameBoard[i][j]+"]");
            }//for
            System.out.println();
        }//for
        return;
    }
    //Verifica que el expacio este libre para colocar el barco
    //Rango de 0 a n-1
    public boolean isThereEspace_R(int X, int Y, int tamBoat){//Derecha
        for(int i=0; i<tamBoat; i++){
            if((X+i)>9 || this.myGameBoard[X+i][Y] != -1  )
                return false; //no hay espacio disponible
        }
        return true;
    }
    
    public void setBoat_R(int X, int Y, int tamBoat,int ID_BOAT){
        for(int i=0; i<tamBoat; i++){
            this.myGameBoard[X+i][Y] = ID_BOAT;
        }
        return;
    }
    
    public boolean isThereEspace_L(int X, int Y, int tamBoat){//Izquierda
        for(int i=0; i<tamBoat; i++){
            if((X-i)<0 ||this.myGameBoard[X-i][Y] != -1 )
                return false; //no hay espacio disponible
        }
        return true;
    }
    
    public void setBoat_L(int X, int Y, int tamBoat,int ID_BOAT){
        for(int i=0; i<tamBoat; i++){
            this.myGameBoard[X-i][Y] = ID_BOAT;
        }
    }
    
    public boolean isThereEspace_U(int X, int Y, int tamBoat){//Arriba
        for(int i=0; i<tamBoat; i++){
            if( (Y-i)<0 || this.myGameBoard[X][Y-i] != -1 )
                return false; //no hay espacio disponible
        }
        return true;
    }
    
    public void setBoat_U(int X, int Y, int tamBoat,int ID_BOAT){
        for(int i=0; i<tamBoat; i++){
            this.myGameBoard[X][Y-i] = ID_BOAT;
        }
    }
    
    public boolean isThereEspace_D(int X, int Y, int tamBoat){//Abajo
        for(int i=0; i<tamBoat; i++){
            if((Y+i)>9 ||this.myGameBoard[X][Y+i] != -1 )
                return false; //no hay espacio disponible
        }
        return true;
    }
    
    public void setBoat_D(int X, int Y, int tamBoat,int ID_BOAT){
        for(int i=0; i<tamBoat; i++){
            this.myGameBoard[X][Y+i] = ID_BOAT;
        }
    }
    
    //Construye los barcos, en función de la coordenada inicio del barco
    public void set_Boat(int typeBoat){
        int numBarcos=0, tamBoat=0, ID_Boat = 0;
        switch(typeBoat){
            case 1:{ //Acorazado
                numBarcos = 1; //Barcos disponibles
                tamBoat = 4; //numero de casillas
                ID_Boat = typeBoat;
                break;
            }
            case 2:{//Crucero
                numBarcos = 2; //Barcos disponibles
                tamBoat = 3; //numero de casillas
                ID_Boat = typeBoat;
                break;
            }
            case 3:{//Destructores
                numBarcos = 3; //Barcos disponibles
                tamBoat = 2; //numero de casillas
                ID_Boat = typeBoat;
                break;
            }
            case 4:{//Submarino
                numBarcos = 1; //Barcos disponibles
                tamBoat = 5; //numero de casillas
                ID_Boat = typeBoat;
                break;
            }
            default:
                System.out.println("ERROR: Numero no valido");
                ErrorFlag = -404;
        }
        System.out.println("IDENTIFICADOR-->" + ID_Boat);
        //ciclo
        int count = 0;
        while(count < numBarcos){
            int X = this.get_RandomNumber(); //X
            int Y = this.get_RandomNumber(); //Y
            System.out.println("-->X:" + X +"-->Y:" + Y);
            if(this.myGameBoard[X][Y] == -1){//Casilla libre             
                if(this.isThereEspace_R(X, Y, tamBoat)){ //Movimiento derecha
                    this.setBoat_R(X, Y, tamBoat, ID_Boat);
                }else if(this.isThereEspace_L(X, Y, tamBoat)){//Movimiento izquierda
                    this.setBoat_L(X, Y, tamBoat, ID_Boat);
                }else if(this.isThereEspace_U(X, Y, tamBoat)){//Movimiento ascendente 
                    this.setBoat_U(X, Y, tamBoat, ID_Boat);
                }else if(this.isThereEspace_D(X, Y, tamBoat)){//Movimiento descendente
                    this.setBoat_D(X, Y, tamBoat, ID_Boat);
                }else{
                    continue;
                }
                count++;
            }//if            
        }//while
        System.out.println("FIN DE COLOCACION");        
    }
    
    
    //Regresa un numero aleatorio entre el 0 y 9
    //posible cambio --> recepcion de limites
    public int get_RandomNumber(){
        Random num = new Random();
        int n = num.nextInt(9-0+1) + 0;
        return n;
    }
    //Verifica si la posicion que mando el cliente es un
    //barco
    public boolean isBoat(int x, int y){
        return this.myGameBoard[x][y] != -1;
    }
    
    //Barco destruido
    public void setDestroyBoat(int x, int y){
        this.myGameBoard[x][y] = -1;
    }
    public boolean isAllBoatsDesroyed(){
        for(int i=0; i<10;i++ ){
            for(int j=0; j<10;j++){
                if(this.myGameBoard[i][j] != -1){
                    return false;
                }//if  
           }//for
        }//for
        return true;
    }
    
}//class
