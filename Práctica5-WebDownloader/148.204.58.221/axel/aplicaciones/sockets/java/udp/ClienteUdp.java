/**
 * Javier Abellán, 16 Mayo 2006
 * Ejemplo de uso de socket udp en java
 */
package chuidiang.ejemplos;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Abre un socket udp y envía por él 10 mensajes consistentes en 10 clases
 * DatoUdp.
 * @author Chuidiang
 *
 */
public class ClienteUdp
{

    /**
     * Programa de prueba. Instancia esta clase
     * @param args
     */
    public static void main(String[] args)
    {
        new ClienteUdp();
    }

    /**
     * Crea una instancia de esta clase y envía los 10 mensajes
     *
     */
    public ClienteUdp()
    {
        try
        {

            // La IP es la local, el puerto es en el que este cliente esté
            // escuchando.
            DatagramSocket socket = new DatagramSocket(
                    Constantes.PUERTO_DEL_CLIENTE, InetAddress
                            .getByName("localhost"));

            // Se instancia un DatoUdp y se convierte a bytes[]
            DatoUdp elDato = new DatoUdp("hola");
            byte[] elDatoEnBytes = elDato.toByteArray();

            // Se meten los bytes en el DatagramPacket, que es lo que se
            // va a enviar por el socket.
            // El destinatario es el servidor.
            // El puerto es por el que esté escuchando el servidor.
            DatagramPacket dato = new DatagramPacket(elDatoEnBytes,
                    elDatoEnBytes.length, InetAddress
                            .getByName(Constantes.HOST_SERVIDOR),
                    Constantes.PUERTO_DEL_SERVIDOR);
            
            // Se envía el DatagramPacket 10 veces, esperando 1 segundo entre
            // envío y envío.
            for (int i = 0; i < 10; i++)
            {
                System.out.println("Envio dato " + i);
                socket.send(dato);
                Thread.sleep(1000);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
