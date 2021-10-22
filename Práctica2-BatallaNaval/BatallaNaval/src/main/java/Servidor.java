//Servidor para el juego:  BATALLA NAVAL
import java.io.*;
import java.util.*;
import java.net.*;


public class Servidor {
    
    public static boolean yaLaOcupe(int x, int y, List<Casilla> l){
        for(int i=0; i<l.size();i++){
            if(l.get(i).getX()== x && l.get(i).getY() == y){
                return true;
            }//ir
        }//for
        return false;
    }
    
    
    public static void main(String[] args){
            
                try{
                    //COMUNICACION CON CLIENTE
                    List<Casilla> casillaUsadas =new ArrayList<>();
                    //1) Create a DatagramSocket object
                    DatagramSocket server = new DatagramSocket(1700);
                    server.setReuseAddress(true);
                    System.out.println("Servidor iniciado, esperando cliente..");
                    ServerBarcos p = new ServerBarcos();
                    while(true){
                        
                        //2) Create a DatagramPacket object for the incoming datagrams
                        DatagramPacket pkt = new DatagramPacket(new byte[65535],65535);
                        
                        //3) Accept an incoming datagrama
                        server.receive(pkt);
                        
                        //4) Retrieve the data from the buffer
                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(pkt.getData()));
                        Casilla objeto = (Casilla)ois.readObject();
                        System.out.println("Esperando objeto...");
                        System.out.println("FLAG:" + objeto.getFlag());
                        
                        Casilla target = new Casilla(); //Inicializamos con -1                                           
                        switch(objeto.getFlag()){
                            case 0:{
                                System.out.println("--> CLIENTE GANA <--");
                                server.close();
                                System.exit(0);
                            }
                            case 1:{//CLIENTE LISTO
                                  //5) Connecting wiith the one who send a Datagram first
                                    System.out.println("--> CONNECT method <--");
                                    InetAddress clientAddress = pkt.getAddress(); 
                                    int clientPort = pkt.getPort();
                                    server.connect(clientAddress, clientPort);  
                                
                                  //ACOMODO AUTOMATICO DE BARCOS
                                    
                                    System.out.println("--> INICIO DE CONTRUCCION <--");
                                    p.set_Boat(1);
                                    p.set_Boat(2);
                                    p.set_Boat(3);
                                    p.set_Boat(4);
                                    p.show_myGameBoard();
                                 //6) Create the response datagram
                                    target.setFlag(1);   
                                break;
                            }
                            case 2:{// VERFICACION DE TIRO
                                System.out.println("--> VERIFICANDO TIRO DE CLIENTE <--");
                                if(p.isBoat(objeto.getX(),objeto.getY())){
                                    target.setFlag(2);//Cliente le dio
                                    p.setDestroyBoat(objeto.getX(), objeto.getY());
                                }else{
                                    target.setFlag(3);//Cliente fallo
                                }//if
                                break;
                            }
                            
                            case 3:{// TURNO DE TIRO DEL SERVIDOR
                                //posible cambio de logica por pila de tiros realizados
                                System.out.println("FLAG: 3");
                                Casilla x = new Casilla();
                                   int tX = x.getRandom();
                                   int tY = x.getRandom();
                                if(casillaUsadas.isEmpty()){
                                    x.setX(tX);
                                    x.setY(tY);
                                    casillaUsadas.add(x);
                                }else{
                                    while(yaLaOcupe(tX,tY,casillaUsadas)){
                                       tX = x.getRandom();
                                       tY = x.getRandom();
                                    }//while
                                    x.setX(tX);
                                    x.setY(tY);
                                    casillaUsadas.add(x);
                                }//if
                                target.setX(x.getX());
                                target.setY(x.getY());
                                target.setFlag(4); //Verificacion de tiro
                                break;
                            }
                            case 4:{ //RECEPECION DE TIRO ACERTADO
                                //cambio de tiro
                                target.setFlag(1);
                                break;
                            }
                            case 5:{//SERVIDOR GANA
                                System.out.println("--> GANA SERVIDOR <--");
                                System.out.println("Cerrando servidor...");
                                server.close();
                                System.exit(0);
                                break;
                            }
                            
                        }
                        if(p.isAllBoatsDesroyed()){
                            target.setFlag(0);
                        }
                        ByteArrayOutputStream baos= new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(target);
                        oos.flush();
                        byte[] b = baos.toByteArray();
                        DatagramPacket p1 = new DatagramPacket(b,b.length,pkt.getAddress(),pkt.getPort());
                        
                        //7) Send the response datagram
                        server.send(p1);
                        System.out.println("FLAG:" +target.getFlag());
                        
                        
                    }//while
                
                
                }catch(Exception e){
                    e.printStackTrace();
                }//catch
    }//main
}//Servidor


