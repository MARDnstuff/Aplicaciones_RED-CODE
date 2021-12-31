/**
 * Javier Abellán, 16 Mayo 2006
 */
package chuidiang.ejemplos;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Servidor de udp que se pone a la escucha de DatagramPacket que contengan
 * dentro DatoUdp y los escribe en pantalla.
 * 
 * @author Chuidiang
 */
public class ServidorUdp
{

    /**
     * Prueba del prorama ServidorUdp
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        new ServidorUdp();
    }

    /**
     * Crea una instancia de esta clase, poniendose a la escucha del puerto
     * definido en Constantes y escribe en pantalla todos los mensajes que le
     * lleguen.
     */
    public ServidorUdp()
    {
        try
        {

            // La IP es la local, el puerto es en el que el servidor esté 
            // escuchando.
            DatagramSocket socket = new DatagramSocket(
                    Constantes.PUERTO_DEL_SERVIDOR, InetAddress
                            .getByName("localhost"));

            // Un DatagramPacket para recibir los mensajes.
            DatagramPacket dato = new DatagramPacket(new byte[100], 100);

            // Bucle infinito.
            while (true)
            {
                // Se recibe un dato y se escribe en pantalla.
                socket.receive(dato);
                System.out.print("Recibido dato de "
                        + dato.getAddress().getHostName() + " : ");
                
                // Conversion de los bytes a DatoUdp
                DatoUdp datoRecibido = DatoUdp.fromByteArray(dato.getData());
                System.out.println(datoRecibido.cadenaTexto);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
