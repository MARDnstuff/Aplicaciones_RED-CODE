import java.net.*;
import java.io.*;
// Chapter 5, Listing 1
public class PacketReceiveDemo
{
public static void main (String args[])
{
try
{
System.out.println ("Binding to local port
2000");
// Create a datagram socket, bound to the
// specific port 2000
DatagramSocket socket = new
DatagramSocket(2000);
System.out.println ("Bound to local port "
+ socket.getLocalPort());
// Create a datagram packet, containing a
// maximum buffer of 256 bytes
DatagramPacket packet = new
DatagramPacket( new byte[256], 256 );
// Receive a packet - remember by default
//this is a blocking operation
socket.receive(packet);
System.out.println ("Packet received!");
// Display packet information
InetAddress remote_addr = packet.getAddress();
System.out.println ("Sent by : " +
remote_addr.getHostAddress() );
System.out.println ("Sent from: " +
packet.getPort());
// Display packet contents, by reading
// from byte array
ByteArrayInputStream bin = new
ByteArrayInputStream (packet.getData());
// Display only up to the length of the
// original UDP packet
for (int i=0; i < packet.getLength(); i++)
{
int data = bin.read();
if (data == -1)
break;
else
System.out.print ( (char)
data) ;
}
socket.close();
}
catch (IOException ioe)
{
System.err.println ("Error - " + ioe);
}
}
}