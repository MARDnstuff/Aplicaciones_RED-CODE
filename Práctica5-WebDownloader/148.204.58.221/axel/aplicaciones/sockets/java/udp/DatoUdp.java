/**
 * Javier Abellán, 16 Mayo 2006
 */
package chuidiang.ejemplos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DatoUdp implements Serializable
{
    /**
     * serial uid
     */
    private static final long serialVersionUID = 3258698714674442547L;

    /**
     * Crea una instancia de la clase, guardando la cadena que se le pasa.
     * @param cadena
     */
    public DatoUdp (String cadena)
    {
        this.cadenaTexto=cadena;
    }
    public String cadenaTexto;
    
    /**
     * Se autoconvierte esta clase a array de bytes.
     * @return La clase convertida a array de bytes.
     */
    public byte [] toByteArray()
    {
        try
        {
             // Se hace la conversión usando un ByteArrayOutputStream y un
             // ObjetOutputStream.
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream (bytes);
            os.writeObject(this);
            os.close();
            return bytes.toByteArray();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Se convierte el array de bytes que recibe en un objeto DatoUdp.
     * @param bytes El array de bytes
     * @return Un DatoUdp.
     */
    public static DatoUdp fromByteArray (byte [] bytes)
    {
        try
        {
            // Se realiza la conversión usando un ByteArrayInputStream y un
            // ObjectInputStream
            ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
            ObjectInputStream is = new ObjectInputStream(byteArray);
            DatoUdp aux = (DatoUdp)is.readObject();
            is.close();
            return aux;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
