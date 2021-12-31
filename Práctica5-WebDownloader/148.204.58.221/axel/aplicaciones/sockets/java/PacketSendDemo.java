import java.net.*;
import java.io.*;
public class PacketSendDemo
{
public static void main (String args[])
{
int argc = args.length;
// Check for valid number of parameters
if (argc != 1)
{
System.out.println ("Syntax :");
System.out.println ("java PacketSendDemo
hostname");
return;
}
String hostname = args[0];
try
{
System.out.println ("Binding to a local port");
// Create a datagram socket, bound to any
available
// local port
DatagramSocket socket = new
DatagramSocket();
System.out.println ("Bound to local port "
+ socket.getLocalPort());
// Create a message to send using a UDP packet
ByteArrayOutputStream bout = new
ByteArrayOutputStream();
PrintStream pout = new PrintStream (bout);
pout.print ("Greetings!");
// Get the contents of our message as an array
// of bytes
byte[] barray = bout.toByteArray();
// Create a datagram packet, containing our byte
// array
DatagramPacket packet = new
DatagramPacket( barray, barray.length );
System.out.println ("Looking up hostname "
+ hostname );
// Lookup the specified hostname, and get an
// InetAddress
InetAddress remote_addr =
InetAddress.getByName(hostname);
System.out.println ("Hostname resolved as " +
remote_addr.getHostAddress());
// Address packet to sender
packet.setAddress (remote_addr);
// Set port number to 2000
packet.setPort (2000);
// Send the packet - remember no guarantee of
delivery
socket.send(packet);
System.out.println ("Packet sent!");
}
catch (UnknownHostException uhe)
{
System.err.println ("Can't find host " +
hostname);
}
catch (IOException ioe)
{
System.err.println ("Error - " + ioe);
}
}
}