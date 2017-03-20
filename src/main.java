
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class main {

	public static void main(String[] args) {
		DatagramSocket socket=null;
		DatagramPacket inputPacket=null; 	//receiving packet
		byte[] inbuf;                //input buffer output buffer
		int port=3000;
		   System.out.println("server is now runing");

		  try {
			socket =new DatagramSocket(3000);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while(true){
				try {
					   inbuf =new byte[1024];
					   inputPacket=new DatagramPacket(inbuf,inbuf.length);
					   socket.receive(inputPacket);
					   // get client port and address
					   int source_port=inputPacket.getPort();
					   InetAddress source_address=inputPacket.getAddress();
					   
					   System.out.println("client:"+source_address+":"+source_port);

									   
					   // begin server thread
					   try {
							port++;
						      Thread t = new server(port,source_port,source_address);
						      t.start();
						    } catch (Exception e) {
						      e.printStackTrace();
						    }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			   }
		
		  }
		}