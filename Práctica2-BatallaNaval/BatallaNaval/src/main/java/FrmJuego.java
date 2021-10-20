
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mauricio
 */
public class FrmJuego extends javax.swing.JFrame {
    String nombreJugador;
    Casilla targetCl = new Casilla();
    int numFilas=10;
    int numColumnas=10;
    int numMinas=10;
    int ganador = -1;
    boolean ordenaBarco = false;
    private int numCasillasSeleccionadas = 0;
                                            
    private static final String[] coordY = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    
    
    Barco[] barcos = new Barco[7];
    /*
    barcos[0] -> destructor 1;
    barcos[1] -> destructor 2;
    barcos[2] -> destructor 3;
    barcos[3] -> crucero 1;
    barcos[4] -> crucero 2;
    barcos[5] -> acorazado;
    barcos[6] -> submarino;
    
    */
    JButton[][] botonesTablero1 = new JButton[numFilas][numColumnas];
    JButton[][] botonesTablero2 = new JButton[numFilas][numColumnas];
    JLabel[] coordenadasX1;
    JLabel[] coordenadasY1;
    JLabel[] coordenadasX2;
    JLabel[] coordenadasY2;
    
    Tablero tablero;
  
    /**
     * Creates new form FrmJuego
     */
    public FrmJuego() {
        initComponents();
        cargarControles();
    }
    
    private void cargarControles(){
        
        int posXReferencia=28;
        int posYReferencia=35;
        int anchoControl=30;
        int altoControl=30;
        int i;
        
        coordenadasX1 = new JLabel[numFilas];
        coordenadasY1 = new JLabel[numFilas];
        coordenadasX2 = new JLabel[numFilas];
        coordenadasY2 = new JLabel[numFilas];
        
        
        for (i = 0; i < coordenadasX2.length; i++) {
            coordenadasX1[i] = new JLabel(""+(i+1));
            coordenadasX1[i].setForeground(Color.white);
            coordenadasY1[i] = new JLabel(coordY[i]);
            coordenadasY1[i].setForeground(Color.white);
            coordenadasX2[i] = new JLabel(""+(i+1));
            coordenadasX2[i].setForeground(Color.white);
            coordenadasY2[i] = new JLabel(coordY[i]);
            coordenadasY2[i].setForeground(Color.white);
            
            if(i == 0){
                coordenadasX1[i].setBounds(posXReferencia+10, posYReferencia-20, anchoControl, altoControl);
                coordenadasY1[i].setBounds(posXReferencia-10, posYReferencia-2, anchoControl, altoControl);
                coordenadasX2[i].setBounds(posXReferencia+10, posYReferencia-20, anchoControl, altoControl);
                coordenadasY2[i].setBounds(posXReferencia-10, posYReferencia-2, anchoControl, altoControl);
            }
            else{
                coordenadasX1[i].setBounds(
                            coordenadasX1[i-1].getX()+coordenadasX1[i-1].getWidth(),
                            posYReferencia-20, 
                            anchoControl, altoControl);
                coordenadasY1[i].setBounds(
                            posXReferencia-10, 
                            coordenadasY1[i-1].getY()+coordenadasY1[i-1].getHeight(), 
                            anchoControl, altoControl);
                coordenadasX2[i].setBounds(
                            coordenadasX1[i-1].getX()+coordenadasX1[i-1].getWidth(),
                            posYReferencia-20, 
                            anchoControl, altoControl);
                coordenadasY2[i].setBounds(
                            posXReferencia-10, 
                            coordenadasY1[i-1].getY()+coordenadasY1[i-1].getHeight(), 
                            anchoControl, altoControl);
            }
            PlayerPanel.add(coordenadasX1[i]); 
            PlayerPanel.add(coordenadasY1[i]);
            PCPanel.add(coordenadasX2[i]); 
            PCPanel.add(coordenadasY2[i]);
        }
        
        for (i = 0; i < botonesTablero1.length; i++) {
            for (int j = 0; j < botonesTablero1[i].length; j++) {
                botonesTablero1[i][j]=new JButton();
                botonesTablero1[i][j].setName(i+","+j);
                botonesTablero1[i][j].setBorder(null);
                botonesTablero1[i][j].setBackground(Color.black);
                botonesTablero2[i][j]=new JButton();
                botonesTablero2[i][j].setName(i+","+j);
                botonesTablero2[i][j].setBorder(null);
                botonesTablero2[i][j].setBackground(Color.black);
                if (i==0 && j==0){
                    botonesTablero1[i][j].setBounds(posXReferencia, 
                            posYReferencia, anchoControl, altoControl);
                    botonesTablero2[i][j].setBounds(posXReferencia, 
                            posYReferencia, anchoControl, altoControl);
                    
                }else if (i==0 && j!=0){
                    botonesTablero1[i][j].setBounds(
                            botonesTablero1[i][j-1].getX()+botonesTablero1[i][j-1].getWidth(), 
                            posYReferencia, anchoControl, altoControl);
                    botonesTablero2[i][j].setBounds(
                            botonesTablero1[i][j-1].getX()+botonesTablero1[i][j-1].getWidth(), 
                            posYReferencia, anchoControl, altoControl);
                }else{
                    botonesTablero1[i][j].setBounds(
                            botonesTablero1[i-1][j].getX(), 
                            botonesTablero1[i-1][j].getY()+botonesTablero1[i-1][j].getHeight(), 
                            anchoControl, altoControl);
                    botonesTablero2[i][j].setBounds(
                            botonesTablero2[i-1][j].getX(), 
                            botonesTablero2[i-1][j].getY()+botonesTablero2[i-1][j].getHeight(), 
                            anchoControl, altoControl);
                }
                PlayerPanel.add(botonesTablero1[i][j]);
                PCPanel.add(botonesTablero2[i][j]);
            }
        }
        this.setSize(botonesTablero1[numFilas-1][numColumnas-1].getX()+
                botonesTablero1[numFilas-1][numColumnas-1].getWidth()+30,
                botonesTablero1[numFilas-1][numColumnas-1].getY()+
                botonesTablero1[numFilas-1][numColumnas-1].getHeight()+70
                );
        this.setSize(botonesTablero2[numFilas-1][numColumnas-1].getX()+
                botonesTablero2[numFilas-1][numColumnas-1].getWidth()+30,
                botonesTablero2[numFilas-1][numColumnas-1].getY()+
                botonesTablero2[numFilas-1][numColumnas-1].getHeight()+70
                );
    }
    private void colocarCasillas(String nombreBarco, int tipo, int nBarco, int validBarco, Casilla casillaInicio , Casilla casillaFinal){
        int i = 0;
        //botonesTablero1[4][5].setBackground(Color.green);
        //JOptionPane.showMessageDialog(rootPane, "Valid Barco: " + validBarco);
        JOptionPane.showMessageDialog(rootPane, "Colocando " + nombreBarco + " " + nBarco + "...");
        switch(validBarco){
            //Barco horizontal
            case 1:
                switch(tipo){
                    //Destructor (2 casillas)
                    case 1:
                        barcos[nBarco-1].rellenarCasillas(casillaInicio.getX(), (casillaInicio.getY()));
                        botonesTablero1[casillaInicio.getX()][(casillaInicio.getY())].setBackground(Color.gray);
                        break;
                    //Crucero (3 casillas)
                    case 2:
                        for(i = 0; i < 2; i++){
                            barcos[2+nBarco].rellenarCasillas(casillaInicio.getX(), (casillaInicio.getY())+ i);
                            botonesTablero1[casillaInicio.getX()][(casillaInicio.getY())+i].setBackground(Color.gray);
                        }
                        break;
                    //Acorazado(4 casillas)
                    case 3:
                        for(i = 0; i < 3; i++){
                            barcos[5].rellenarCasillas(casillaInicio.getX(), (casillaInicio.getY())+ i);
                            botonesTablero1[casillaInicio.getX()][(casillaInicio.getY())+i].setBackground(Color.gray);
                        }
                        break;
                    //Submarino(5 casillas)
                    case 4:
                        for(i = 0; i < 4; i++){
                            barcos[6].rellenarCasillas(casillaInicio.getX(), (casillaInicio.getY())+ i);
                            botonesTablero1[casillaInicio.getX()][(casillaInicio.getY())+i].setBackground(Color.gray);
                        }
                        break;
                
                }
                
                break;
            //Barco vertical
            case 2:
                switch(tipo){
                    //Destructor (2 casillas)
                    case 1:
                        barcos[nBarco-1].rellenarCasillas(casillaInicio.getX()+1, (casillaInicio.getY())-1);
                        botonesTablero1[casillaInicio.getX()+1][(casillaInicio.getY())-1].setBackground(Color.gray);
                        break;
                    //Crucero (3 casillas)
                    case 2:
                        for(i = 0; i < 3; i++){
                            barcos[2+nBarco].rellenarCasillas(casillaInicio.getX()+i, (casillaInicio.getY())-1);
                            botonesTablero1[casillaInicio.getX()+i][(casillaInicio.getY())-1].setBackground(Color.gray);
                        }
                        break;
                    //Acorazado(4 casillas)
                    case 3:
                        for(i = 0; i < 4; i++){
                            barcos[5].rellenarCasillas(casillaInicio.getX()+i, (casillaInicio.getY())-1);
                            botonesTablero1[casillaInicio.getX()+i][(casillaInicio.getY())-1].setBackground(Color.gray);
                        }
                        break;
                    //Submarino(5 casillas)
                    case 4:
                        for(i = 0; i < 5; i++){
                            barcos[6].rellenarCasillas(casillaInicio.getX()+i, (casillaInicio.getY())-1);
                            botonesTablero1[casillaInicio.getX()+i][(casillaInicio.getY())-1].setBackground(Color.gray);
                        }
                        break;
                
                }
                break;
        }
    }
    
    /*
    private void ordenarBarcos(int tipo, int nBarco) {
        int i = 0, j = 0, contador = 0;
        numCasillasSeleccionadas = 0;
        int numCasillasSelect = 0;
        Casilla[] casillasSeleccionadas = new Casilla[2];
        
        Barco barco = new Barco();
        for (i = 0; i < botonesTablero1.length; i++) {
            for (j = 0; j < botonesTablero1[i].length; j++) {
                switch(tipo){
                    //Destructor (2 casillas) - 3 barcos
                    case 1: 
                        botonesTablero1[i][j].addActionListener(new ActionListener(){
                            @Override
                            public void actionPerformed(ActionEvent e){
                                numCasillasSeleccionadas++;
                                JButton btn=(JButton)e.getSource();
                                String[] coordenada=btn.getName().split(",");
                                int posFila=Integer.parseInt(coordenada[0]);
                                int posColumna=Integer.parseInt(coordenada[1]);
                                if(numCasillasSeleccionadas == 1){
                                    switch(nBarco){
                                        //Destructor 1
                                        case 1:
                                            barcos[0] = new Barco();
                                            barcos[0].rellenarCasillas(posFila, posColumna);
                                            break;
                                        //Destructor 2
                                        case 2:
                                            barcos[1].rellenarCasillas(posFila, posColumna);
                                            break;
                                        //Destructor 3
                                        case 3:
                                            barcos[2].rellenarCasillas(posFila, posColumna);
                                            break;
                                    }
                                    btn.setBackground(Color.green);
                                    //JOptionPane.showMessageDialog(rootPane, "Selecciona 2 casillas unicamente");
                                }
                                //La primera casilla ha sido colocada
                                else if(numCasillasSeleccionadas > 1){
                                    int validBarco = 0;
                                    validBarco = validarBarco(barcos[nBarco-1].getCasillaInicio(),new Casilla(posFila, posColumna));
                                    colocarCasillas(tipo, nBarco, validBarco, barcos[0].getCasillaInicio(),new Casilla(posFila, posColumna));
                                    if(validBarco != -1 || validBarco != 0){
                                        JOptionPane.showMessageDialog(rootPane, "Barco colocado correctamente");
                                        return;
                                    }
                                }
                                
                            }
                        });
                        break;
                       
                
                }
                
            }
        }  
    }*/
    
    private int validarDestructor(Casilla casillaInicio, Casilla casillaFinal){
        //Se ha seleccionado 2 veces la misma casilla
        if(casillaInicio.getX() == casillaFinal.getX() && casillaInicio.getY() == casillaFinal.getY()){
            return 0;
        }
        //Barco vertical
        else if(casillaInicio.getX() == casillaFinal.getX() && casillaInicio.getY() != casillaFinal.getY()){
            if(casillaFinal.getY() - casillaInicio.getY() > 1){
                return -1;
            }
            return 1;
        }
        //Barco horizontal
        else if(casillaInicio.getX() != casillaFinal.getX() && casillaInicio.getY() == casillaFinal.getY()){
            if(casillaFinal.getX() - casillaInicio.getX() > 1){
                return -1;
            }
            return 2;
        }
        //Casillas colocadas indebidamente
        return -1;
    
    }
    private int validarCrucero(Casilla casillaInicio, Casilla casillaFinal){
        //Se ha seleccionado 2 veces la misma casilla
        if(casillaInicio.getX() == casillaFinal.getX() && casillaInicio.getY() == casillaFinal.getY()){
            return 0;
        }
        //Barco vertical
        else if(casillaInicio.getX() == casillaFinal.getX() && casillaInicio.getY() != casillaFinal.getY()){
            if(casillaFinal.getY() - casillaInicio.getY() != 2){
                return -1;
            }
            return 1;
        }
        //Barco horizontal
        else if(casillaInicio.getX() != casillaFinal.getX() && casillaInicio.getY() == casillaFinal.getY()){
            if(casillaFinal.getX() - casillaInicio.getX() != 2){
                return -1;
            }
            return 2;
        }
        //Casillas colocadas indebidamente
        return -1;
    
    }
    private int validarAcorazado(Casilla casillaInicio, Casilla casillaFinal){
        //Se ha seleccionado 2 veces la misma casilla
        if(casillaInicio.getX() == casillaFinal.getX() && casillaInicio.getY() == casillaFinal.getY()){
            return 0;
        }
        //Barco vertical
        else if(casillaInicio.getX() == casillaFinal.getX() && casillaInicio.getY() != casillaFinal.getY()){
            if(casillaFinal.getY() - casillaInicio.getY() != 3){
                return -1;
            }
            return 1;
        }
        //Barco horizontal
        else if(casillaInicio.getX() != casillaFinal.getX() && casillaInicio.getY() == casillaFinal.getY()){
            if(casillaFinal.getX() - casillaInicio.getX() != 3){
                return -1;
            }
            return 2;
        }
        //Casillas colocadas indebidamente
        return -1;
    
    }
    private int validarSubmarino(Casilla casillaInicio, Casilla casillaFinal){
        //Se ha seleccionado 2 veces la misma casilla
        if(casillaInicio.getX() == casillaFinal.getX() && casillaInicio.getY() == casillaFinal.getY()){
            return 0;
        }
        //Barco vertical
        else if(casillaInicio.getX() == casillaFinal.getX() && casillaInicio.getY() != casillaFinal.getY()){
            if(casillaFinal.getY() - casillaInicio.getY() != 4){
                return -1;
            }
            return 1;
        }
        //Barco horizontal
        else if(casillaInicio.getX() != casillaFinal.getX() && casillaInicio.getY() == casillaFinal.getY()){
            if(casillaFinal.getX() - casillaInicio.getX() != 4){
                return -1;
            }
            return 2;
        }
        //Casillas colocadas indebidamente
        return -1;
    
    }
    
    private boolean validCoord(String coord){
        if(!coord.matches("^[a-jA-J],([0-9]||10)$")){
            return false;
        }
        String[] coordenada=coord.split(",");
        int i = 0, posFila = 0, posColumna = 0;
        posColumna=Integer.parseInt(coordenada[1]);
        
        if(posColumna < 1 || posColumna > 10){
            return false;
        }
        while(!coordY[i].equalsIgnoreCase(coordenada[0]) && i < coordY.length){
            i++;
        }
        if(i < coordY.length){
             return true;
        }
        return false;
    }
    
    
    private void ordenarBarco(int tipo, int nBarco){
        String nombreBarco = "";
        switch(tipo){
            //Destructores
            case 1: 
                nombreBarco = "destructor";
                break;
            //Crucero
            case 2:
                nombreBarco = "crucero";
                break;
            //Acorazado    
            case 3:
                nombreBarco = "acorazado";
                break;
            //Submarino
            case 4: 
                nombreBarco = "Submarino";
        }
        
        //Casilla inicial
        String coordInicial = JOptionPane.showInputDialog(ContainerPanel, "Ingresa la coordenada de la casilla inicial del " + nombreBarco + " " + nBarco);
        while(!validCoord(coordInicial)){
            coordInicial = JOptionPane.showInputDialog(ContainerPanel, "Ingresa una coordenada válida:");
        }
        String[] coordenada1=coordInicial.split(",");
        int i = 0, posFilaInicial = 0, posColumnaInicial = 0;
        posColumnaInicial=Integer.parseInt(coordenada1[1]);
        
        while(!coordY[i].equalsIgnoreCase(coordenada1[0]) && i < coordY.length){
            i++;
        }
        if(i < coordY.length){
             posFilaInicial = i;
        }
        JOptionPane.showMessageDialog(ContainerPanel, "posFila: " + posFilaInicial + " posColumna: " + posColumnaInicial);
        botonesTablero1[posFilaInicial][posColumnaInicial-1].setBackground(Color.gray);
        
        //Casilla final
        String coordFinal = JOptionPane.showInputDialog(ContainerPanel, "Ingresa la coordenada de la casilla final del " + nombreBarco + " " + nBarco);
        while(!validCoord(coordFinal) ){
            coordFinal = JOptionPane.showInputDialog(ContainerPanel, "Ingresa una coordenada válida:");
        }
        String[] coordenada2=coordFinal.split(",");
        int posFilaFinal = 0, posColumnaFinal = 0;
        i = 0;
        posColumnaFinal=Integer.parseInt(coordenada2[1]);
        while(!coordY[i].equalsIgnoreCase(coordenada2[0]) && i < coordY.length){
            i++;
        }
        if(i < coordY.length){
             posFilaFinal = i;
        }
        JOptionPane.showMessageDialog(ContainerPanel, "posFila: " + posFilaFinal + " posColumna: " + posColumnaFinal);
        //botonesTablero1[posFilaFinal][posColumnaFinal-1].setBackground(Color.green);
        
        switch(tipo){
            //Destructores
            case 1: 
                barcos[nBarco-1] = new Barco();
                barcos[nBarco-1].rellenarCasillas(posFilaInicial, posColumnaInicial);
                int validDestructor = validarDestructor(barcos[nBarco-1].getCasillaInicio(),new Casilla(posFilaFinal, posColumnaFinal));  
                while(validDestructor == -1 || validDestructor == 0){
                    coordFinal = JOptionPane.showInputDialog(ContainerPanel, "Casilla no válida, ingrese una distinta: ");
                    while(!validCoord(coordFinal) ){
                        coordFinal = JOptionPane.showInputDialog(ContainerPanel, "Ingresa una coordenada válida:");
                    }
                    coordenada2=coordFinal.split(",");
                    posFilaFinal = 0;
                    posColumnaFinal = 0;
                    i = 0;
                    posColumnaFinal=Integer.parseInt(coordenada2[1]);
                    while(!coordY[i].equalsIgnoreCase(coordenada2[0]) && i < coordY.length){
                        i++;
                    }
                    if(i < coordY.length){
                         posFilaFinal = i;
                    }
                    validDestructor = validarDestructor(barcos[nBarco-1].getCasillaInicio(),new Casilla(posFilaFinal, posColumnaFinal));
                }
                colocarCasillas(nombreBarco,1, nBarco, validDestructor, barcos[nBarco-1].getCasillaInicio(),new Casilla(posFilaFinal, posColumnaFinal));
                barcos[nBarco - 1].getCasillaInicio().setY(posColumnaInicial - 1);
                break;
            //Crucero
            case 2:
                barcos[2+nBarco] = new Barco();
                barcos[2+nBarco].rellenarCasillas(posFilaInicial, posColumnaInicial);
                int validCrucero = validarCrucero(barcos[2+nBarco].getCasillaInicio(),new Casilla(posFilaFinal, posColumnaFinal));  
                while(validCrucero == -1 || validCrucero == 0){
                    coordFinal = JOptionPane.showInputDialog(ContainerPanel, "Casilla no válida, ingrese una distinta: ");
                    while(!validCoord(coordFinal) ){
                        coordFinal = JOptionPane.showInputDialog(ContainerPanel, "Ingresa una coordenada válida:");
                    }
                    coordenada2=coordFinal.split(",");
                    posFilaFinal = 0;
                    posColumnaFinal = 0;
                    i = 0;
                    posColumnaFinal=Integer.parseInt(coordenada2[1]);
                    while(!coordY[i].equalsIgnoreCase(coordenada2[0]) && i < coordY.length){
                        i++;
                    }
                    if(i < coordY.length){
                         posFilaFinal = i;
                    }
                    validCrucero = validarCrucero(barcos[2+nBarco].getCasillaInicio(),new Casilla(posFilaFinal, posColumnaFinal));
                }
                colocarCasillas(nombreBarco,2, nBarco, validCrucero, barcos[2+nBarco].getCasillaInicio(),new Casilla(posFilaFinal, posColumnaFinal));
                barcos[2+nBarco].getCasillaInicio().setY(posColumnaInicial - 1);
                break;
            //Acorazado    
            case 3:
                barcos[5] = new Barco();
                barcos[5].rellenarCasillas(posFilaInicial, posColumnaInicial);
                int validAcorazado = validarAcorazado(barcos[5].getCasillaInicio(),new Casilla(posFilaFinal, posColumnaFinal));  
                while(validAcorazado == -1 || validAcorazado == 0){
                    coordFinal = JOptionPane.showInputDialog(ContainerPanel, "Casilla no válida, ingrese una distinta: ");
                    while(!validCoord(coordFinal) ){
                        coordFinal = JOptionPane.showInputDialog(ContainerPanel, "Ingresa una coordenada válida:");
                    }
                    coordenada2=coordFinal.split(",");
                    posFilaFinal = 0;
                    posColumnaFinal = 0;
                    i = 0;
                    posColumnaFinal=Integer.parseInt(coordenada2[1]);
                    while(!coordY[i].equalsIgnoreCase(coordenada2[0]) && i < coordY.length){
                        i++;
                    }
                    if(i < coordY.length){
                         posFilaFinal = i;
                    }
                    validAcorazado = validarAcorazado(barcos[5].getCasillaInicio(),new Casilla(posFilaFinal, posColumnaFinal));
                }
                colocarCasillas(nombreBarco,3, nBarco, validAcorazado, barcos[5].getCasillaInicio(),new Casilla(posFilaFinal, posColumnaFinal));
                barcos[5].getCasillaInicio().setY(posColumnaInicial - 1);
                break;
            //Submarino
            case 4: 
                barcos[6] = new Barco();
                barcos[6].rellenarCasillas(posFilaInicial, posColumnaInicial);
                int validSubmarino = validarSubmarino(barcos[6].getCasillaInicio(),new Casilla(posFilaFinal, posColumnaFinal));  
                while(validSubmarino == -1 || validSubmarino == 0){
                    coordFinal = JOptionPane.showInputDialog(ContainerPanel, "Casilla no válida, ingrese una distinta: ");
                    while(!validCoord(coordFinal) ){
                        coordFinal = JOptionPane.showInputDialog(ContainerPanel, "Ingresa una coordenada válida:");
                    }
                    coordenada2=coordFinal.split(",");
                    posFilaFinal = 0;
                    posColumnaFinal = 0;
                    i = 0;
                    posColumnaFinal=Integer.parseInt(coordenada2[1]);
                    while(!coordY[i].equalsIgnoreCase(coordenada2[0]) && i < coordY.length){
                        i++;
                    }
                    if(i < coordY.length){
                         posFilaFinal = i;
                    }
                    validSubmarino = validarSubmarino(barcos[6].getCasillaInicio(),new Casilla(posFilaFinal, posColumnaFinal));
                }
                colocarCasillas(nombreBarco,4, nBarco, validSubmarino, barcos[6].getCasillaInicio(),new Casilla(posFilaFinal, posColumnaFinal));
                barcos[6].getCasillaInicio().setY(posColumnaInicial - 1);
                break;
        }
        
    }
    /*
    private boolean ordenarDestructor(int nBarco){
        numCasillasSeleccionadas = 0;
        int i,j;
        for (i = 0; i < botonesTablero1.length; i++) {
            for (j = 0; j < botonesTablero1[i].length; j++) {
                botonesTablero1[i][j].addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        JButton btn=(JButton)e.getSource();
                        String[] coordenada=btn.getName().split(",");
                        int posFila=Integer.parseInt(coordenada[0]);
                        int posColumna=Integer.parseInt(coordenada[1]);
                        if(numCasillasSeleccionadas == 0){
                            numCasillasSeleccionadas++;
                            barcos[nBarco-1] = new Barco();
                            barcos[nBarco-1].rellenarCasillas(posFila, posColumna);
                            btn.setBackground(Color.green);
                        }
                        else{
                            if(numCasillasSeleccionadas == 2){
                                JOptionPane.showMessageDialog(rootPane, "El destructor " + nBarco + "ha sido colocado correctamente");
                                ordenaBarco = true;
                            }
                            else{
                                int validDestructor = validarDestructor(barcos[nBarco-1].getCasillaInicio(),new Casilla(posFila, posColumna));
                                if(validDestructor == -1){
                                    JOptionPane.showMessageDialog(rootPane, "Casilla no válida. Selecciones otra");
                                }
                                else if(validDestructor == 0){
                                    JOptionPane.showMessageDialog(rootPane, "Seleccione una casilla diferente");
                                }
                                else{
                                    colocarCasillas(1, nBarco, validDestructor, barcos[nBarco-1].getCasillaInicio(),new Casilla(posFila, posColumna));
                                    numCasillasSeleccionadas++;
                                }

                            }
                        }     
                    }

                });
            }
        }
        return ordenaBarco;
    }*/

    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ContainerPanel = new javax.swing.JPanel();
        PlayerPanel = new javax.swing.JPanel();
        PCPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        PlayBTN = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        PlayerNameLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ContainerPanel.setBackground(new java.awt.Color(0, 0, 0));

        PlayerPanel.setBackground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout PlayerPanelLayout = new javax.swing.GroupLayout(PlayerPanel);
        PlayerPanel.setLayout(PlayerPanelLayout);
        PlayerPanelLayout.setHorizontalGroup(
            PlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );
        PlayerPanelLayout.setVerticalGroup(
            PlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 363, Short.MAX_VALUE)
        );

        PCPanel.setBackground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout PCPanelLayout = new javax.swing.GroupLayout(PCPanel);
        PCPanel.setLayout(PCPanelLayout);
        PCPanelLayout.setHorizontalGroup(
            PCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );
        PCPanelLayout.setVerticalGroup(
            PCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 363, Short.MAX_VALUE)
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Base Naval");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Mapa Contrincante");

        PlayBTN.setBackground(new java.awt.Color(255, 0, 0));
        PlayBTN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PlayBTN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PlayBTNMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                PlayBTNMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                PlayBTNMouseExited(evt);
            }
        });

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("JUGAR");

        javax.swing.GroupLayout PlayBTNLayout = new javax.swing.GroupLayout(PlayBTN);
        PlayBTN.setLayout(PlayBTNLayout);
        PlayBTNLayout.setHorizontalGroup(
            PlayBTNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PlayBTNLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel3)
                .addContainerGap(63, Short.MAX_VALUE))
        );
        PlayBTNLayout.setVerticalGroup(
            PlayBTNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PlayBTNLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PlayerNameLabel.setBackground(new java.awt.Color(51, 102, 255));
        PlayerNameLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        PlayerNameLabel.setForeground(new java.awt.Color(255, 0, 0));
        PlayerNameLabel.setText("Nombre del Jugador:  ");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 0, 0));
        jLabel4.setText("BATALLA NAVAL");

        javax.swing.GroupLayout ContainerPanelLayout = new javax.swing.GroupLayout(ContainerPanel);
        ContainerPanel.setLayout(ContainerPanelLayout);
        ContainerPanelLayout.setHorizontalGroup(
            ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContainerPanelLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ContainerPanelLayout.createSequentialGroup()
                        .addComponent(PlayBTN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(ContainerPanelLayout.createSequentialGroup()
                        .addComponent(PlayerNameLabel)
                        .addContainerGap(660, Short.MAX_VALUE))
                    .addGroup(ContainerPanelLayout.createSequentialGroup()
                        .addComponent(PlayerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(PCPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37))))
            .addGroup(ContainerPanelLayout.createSequentialGroup()
                .addGap(168, 168, 168)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(147, 147, 147))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ContainerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(354, 354, 354))
        );
        ContainerPanelLayout.setVerticalGroup(
            ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContainerPanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(PlayerNameLabel)
                .addGap(26, 26, 26)
                .addGroup(ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PlayerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PCPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(PlayBTN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ContainerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ContainerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void PlayBTNMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PlayBTNMouseEntered
        PlayBTN.setBackground(new Color(204,0,0));
    }//GEN-LAST:event_PlayBTNMouseEntered

    private void PlayBTNMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PlayBTNMouseExited
        PlayBTN.setBackground(new Color(255,0,0));
    }//GEN-LAST:event_PlayBTNMouseExited

    private void PlayBTNMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PlayBTNMouseClicked
        nombreJugador = JOptionPane.showInputDialog(ContainerPanel, "Ingresa un nombre de jugador para iniciar el juego:");
        //TODO: Send the name to the server to start the game
        PlayerNameLabel.setText("Nombre del Jugador: "+ nombreJugador);
        JOptionPane.showMessageDialog(ContainerPanel, "El juego está a punto de iniciar. Ordena tus barcos seleccionando "
                + "la primera casilla y la última de cada barco.\n Recuerda"
                + " puedes ordenarlos vertical u horizontalmente.");
        
        JOptionPane.showMessageDialog(ContainerPanel, "Ordena tu barco submarino(longitud: 5 casillas)");
        ordenarBarco(4,1);
        
        JOptionPane.showMessageDialog(ContainerPanel, "Ordena tu barco acorazado(longitud: 4 casillas)");
        ordenarBarco(3,1);
        
        JOptionPane.showMessageDialog(ContainerPanel, "Ordena tu barco crucero 1(longitud: 3 casillas)");
        ordenarBarco(2, 1);
        
        JOptionPane.showMessageDialog(ContainerPanel, "Ordena tu barco crucero 2(longitud: 3 casillas)");
        ordenarBarco(2,2);
        
        JOptionPane.showMessageDialog(ContainerPanel, "Ordena tu barco destructor 1(longitud: 2 casillas)");
        ordenarBarco(1, 1);
        
        JOptionPane.showMessageDialog(ContainerPanel, "Ordena tu barco destructor 2(longitud: 2 casillas)");
        ordenarBarco(1, 2);
        
        JOptionPane.showMessageDialog(ContainerPanel, "Ordena tu barco destructor 3(longitud: 2 casillas)");
        ordenarBarco(1, 3); 
        System.out.println("-->CLIENTE FINALIZO ACOMODO<--");
        //--> COMUNICACION CON SERVIDOR <--
        try{
            String host="127.0.0.1";
            int port=1700;           
            // 1) Creatting DatagramSocket object 
            InetAddress dst = InetAddress.getByName(host);
            DatagramSocket client = new DatagramSocket();
            System.out.println("Destino-->" + dst.toString());
            
            //2) Creating the outgoing datagram
            System.out.println("Cliente iniciado, generando objeto a ser enviado a " + host + ":" + port);
            System.out.println("--> COMUNICANDO CON SERVIDOR <--");
            //Creacion de paquete casilla para empezar el juego
            Casilla target = new Casilla(); //Inicializamos con -1
            target.setFlag(1);            
            ByteArrayOutputStream baos= new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(target);
            oos.flush();
            byte[] b = baos.toByteArray();
            DatagramPacket pkt = new DatagramPacket(b,b.length,dst,port);
            
            //3) Send the datagram message
            client.send(pkt);
            System.out.println("envio FLAG:" +target.getFlag());
            int X= -1, Y=-1;
             System.out.println("-->EMPIEZA JUEGO<--");
            while(true){
                int exFlag = -1;
                //4) Create a DatagramPacket object for incoming  datagrams
                System.out.println("Esperando recibir objeto..");
                DatagramPacket pkt_r = new DatagramPacket(new byte[65535],65535);
                
                //5) Accept an incoming datagram
                client.receive(pkt_r);
                
                //6) Retrieve the data from the buffer
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(pkt_r.getData()));
                Casilla target_back = (Casilla)ois.readObject();   
                System.out.println("FLAG:"+target_back.getFlag());
                
                Casilla target2 = new Casilla(); //Inicializamos con -1
                switch(target_back.getFlag()){
                    case 0:{
                        JOptionPane.showMessageDialog(ContainerPanel, "FELICIDADES, JUGADOR: " + nombreJugador + " GANA");
                        //7) Close the DatagramSocket
                        exFlag = 10;
                        break;
                    }
                    case 1:{//SERVIDOR LISTO
                       
                        System.out.println("-->TURNO CLIENTE<--");
                        String coordInicial = JOptionPane.showInputDialog(ContainerPanel, "Ingresa la coordenada de para atacar a tu contrincante: ");
                        while(!validCoord(coordInicial)){
                            coordInicial = JOptionPane.showInputDialog(ContainerPanel, "Ingresa una coordenada válida:");
                        }
                        String[] coordenada1=coordInicial.split(",");
                        int i = 0, posFilaInicial = 0, posColumnaInicial = 0;
                        posColumnaInicial=Integer.parseInt(coordenada1[1]);

                        while(!coordY[i].equalsIgnoreCase(coordenada1[0]) && i < coordY.length){
                            i++;
                        }
                        if(i < coordY.length){
                             posFilaInicial = i;
                        }
                        JOptionPane.showMessageDialog(ContainerPanel, "posFila: " + posFilaInicial + " posColumna: " + posColumnaInicial);
                        System.out.println(posFilaInicial + "-->" + posColumnaInicial);
                        target2.setFlag(2);
                        target2.setX(posFilaInicial);
                        target2.setY(posColumnaInicial - 1);
                        X = posFilaInicial;
                        Y = posColumnaInicial;
                        
                        break;
                    }
                    case 2:{//RECEPCION DE VERFICACION DE TIRO - TIRO ACERTADO
                        System.out.println("--> VERIFICACION DE TIRO <--");
                        botonesTablero2[X][Y-1].setBackground(Color.green);
                        JOptionPane.showMessageDialog(ContainerPanel, "TIRO ACERTADO");
                        target2.setFlag(3);//CAMBIO DE TURNO
                        
                        break;
                    }
                    case 3:{//RECEPCION DE VERIFICACION DE TIRO - TIRO FALLADO
                        System.out.println("--> VERIFICACION DE TIRO 2 <--");
                        botonesTablero2[X][Y-1].setBackground(Color.RED);
                        JOptionPane.showMessageDialog(ContainerPanel, "TIRO FALLADO");
                        target2.setFlag(3);
                        break;
                    }
                    case 4:{//VERIFICACION DE TIRO DEL SERVIDOR
                        System.out.println("--> VERIFICACION DE ATAQUE A CLIENTE <--");
                        int ex = 0;
                        for(int i=0; i< this.barcos.length ; i++){
                            if(this.barcos[i].isBoat_Destroy(target_back.getX(),target_back.getY())){
                                //SERVIDOR DESTRUYE UN COORDENADA
                                botonesTablero1[target_back.getX()][target_back.getY()].setBackground(Color.RED);
                                JOptionPane.showMessageDialog(ContainerPanel, "TIRO ACERTADO POR PARTE DEL SERVIDOR");
                                target2.setFlag(4); // INFORME DE TIRO A SERVIDOR ACERTADO
                                ex++;
                                break;
                            }//if
                        }//for
                        if(ex >0){
                            break;
                        }
                        //SERVIDOR DESTRUYE UN COORDENADA
                        JOptionPane.showMessageDialog(ContainerPanel, "TIRO FALLADO POR PARTE DEL SERVIDOR: X:" + target_back.getX() + " Y:"+ target_back.getY());
                        target2.setFlag(4); // INFORME DE TIRO A SERVIDOR FALLADO
                        break;
                    }
                }//switch
                
                
                
                    Barco b3 = new Barco();
                    if(b3.isAllBoatsDestroy(barcos)){
                        target2.setFlag(5);
                        JOptionPane.showMessageDialog(ContainerPanel, "SUERTE PARA LA PROXIMA,SERVIDOR GANA :C ");
                        //7) Close the DatagramSocket
                        ByteArrayOutputStream baos2= new ByteArrayOutputStream();
                        ObjectOutputStream oos2 = new ObjectOutputStream(baos2);
                        oos2.writeObject(target2);
                        oos2.flush();
                        byte[] b_ = baos2.toByteArray();
                        DatagramPacket pkt2 = new DatagramPacket(b_,b_.length,dst,port);
                        client.send(pkt2);
                        client.close();
                        System.exit(0);
                    }

                
                ByteArrayOutputStream baos2= new ByteArrayOutputStream();
                ObjectOutputStream oos2 = new ObjectOutputStream(baos2);
                oos2.writeObject(target2);
                oos2.flush();
                byte[] b2 = baos2.toByteArray();
                DatagramPacket pkt2 = new DatagramPacket(b2,b2.length,dst,port);
                client.send(pkt2);
                if(exFlag == 10){
                    client.close();
                    System.exit(0);
                }
            
            
            
            }//while
            
            
            
            //NOTA:
            //1)Se tiene que revisar la implementacion del tablero de Mau, ya que él
            //implemento con una lista de casillas
            // --> barcos[6].showMyBoat(barcos[6]); con este metodo podemos entender el como los tiene implementado
            
            //2)Meter en un for infinito y hacer un switch para las flags
            
            //3)Los turnos van a ser uno a uno
            
            //4)Creo que no se compartira el tablero, solo sera que si 
            //uno le atina le otro confirma que si se dio y se cambia el color en el 
            //tablero del cliente 
            
            //5)GUI anunciara a que casilla le dio y si le da a un barco se confirmara 
            //en el mensaje de dialogo
            
           
//            barcos[0] -> destructor 1;
//            barcos[1] -> destructor 2;
//            barcos[2] -> destructor 3;
//            barcos[3] -> crucero 1;
//            barcos[4] -> crucero 2;
//            barcos[5] -> acorazado;
//            barcos[6] -> submarino;

            
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
        
    }//GEN-LAST:event_PlayBTNMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmJuego().setVisible(true);
            }
        });
     
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ContainerPanel;
    private javax.swing.JPanel PCPanel;
    private javax.swing.JPanel PlayBTN;
    private javax.swing.JLabel PlayerNameLabel;
    private javax.swing.JPanel PlayerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}
