
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.Scanner;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Client {

	public static void main(String[] args) {
		Checksum checksum = new CRC32();

		DatagramSocket socket = null;
		DatagramPacket inputPacket = null; // receiving packet
		DatagramPacket outputPacket = null; // sending packet
		byte[] inBuf, outBuf; // input buffer output buffer
		ack ackn = null;
		Data datap = null;
		String message;
		int port = 3000;
		int seqno = 0;
		BufferedWriter bufferwrite = null;
		Scanner src = new Scanner(System.in);
		try {
			InetAddress address = InetAddress.getByName("127.0.0.1");
			socket = new DatagramSocket();

			message = "";

			outBuf = message.getBytes();
			outputPacket = new DatagramPacket(outBuf, 0, outBuf.length, address, port);
			socket.send(outputPacket);

			inBuf = new byte[1024];
			inputPacket = new DatagramPacket(inBuf, inBuf.length);
			socket.receive(inputPacket);
			port = inputPacket.getPort();
			// ack
			checksum.update( (seqno+"").getBytes(), 0, (seqno+"").getBytes().length);
			long lngChecksum = checksum.getValue();
			ackn = new ack(8, seqno,lngChecksum);
			outBuf = serialize(ackn);
			outputPacket = new DatagramPacket(outBuf, 0, outBuf.length, address, port);
			socket.send(outputPacket);
			seqno++;

			datap = (Data) deserialize(inputPacket.getData());
			String data = new String(datap.getData(), 0, (datap.getData()).length);

			// print file list

			System.out.println(data);

			// send file name

			String filename = src.nextLine();
			outBuf = filename.getBytes();
			outputPacket = new DatagramPacket(outBuf, 0, outBuf.length, address, port);
			socket.send(outputPacket);

			// Receive file
			StringBuilder stringbuild = new StringBuilder();
			while (true) {

				inBuf = new byte[1024];
				inputPacket = new DatagramPacket(inBuf, inBuf.length);
				socket.receive(inputPacket);
				datap = (Data) deserialize(inputPacket.getData());
				checksum.update( datap.getData(), 0, datap.getData().length);
			    lngChecksum = checksum.getValue();
                if(lngChecksum==datap.getChecksum())
                	continue;
				if (seqno == datap.getSeqno()) {
					Random random = new Random();
					int chance = random.nextInt(100);
					if (chance < 90) {
						System.out.println("send ack");
						checksum.update( (datap.getSeqno()+"").getBytes(), 0, (datap.getSeqno()+"").getBytes().length);
					    lngChecksum = checksum.getValue();
						ackn = new ack(8, datap.getSeqno(),lngChecksum);
						outBuf = serialize(ackn);
						outputPacket = new DatagramPacket(outBuf, 0, outBuf.length, inputPacket.getAddress(),
								inputPacket.getPort());
						socket.send(outputPacket);
					} else {
						System.out.println("lost ack");
					}
					seqno++;
					data = new String(datap.getData(), 0, (datap.getData()).length);
					if (data.endsWith("Error")) {
						break;
					}
					if (!data.equalsIgnoreCase("finsh")) {
						stringbuild.append(data);
						if (stringbuild.length() > 1000) {
							try {
								bufferwrite = new BufferedWriter(new FileWriter(filename + port, true));
								bufferwrite.write(stringbuild.toString().replace("\0", ""));
								bufferwrite.flush();
								stringbuild = new StringBuilder();
							} catch (IOException ioe) {
								ioe.printStackTrace();
							} finally { // always close the file
								if (bufferwrite != null)
									try {
										bufferwrite.close();
									} catch (IOException ioe2) {
										
									}
							} 
						}
					}
					System.out.println(seqno);

				} else {
					System.out.println("resending the missing ack");
					checksum.update( (seqno - 1+"").getBytes(), 0, (seqno - 1+"").getBytes().length);
				    lngChecksum = checksum.getValue();
					ackn = new ack(8, seqno - 1,lngChecksum);
					outBuf = serialize(ackn);
					outputPacket = new DatagramPacket(outBuf, 0, outBuf.length, inputPacket.getAddress(),
							inputPacket.getPort());
					socket.send(outputPacket);
				}
				if (data.equalsIgnoreCase("finsh")) {
					System.out.println("finshed");
					break;
				}

			}
			if (seqno != datap.getSeqno()) {
				checksum.update( (datap.getSeqno()+"").getBytes(), 0, (datap.getSeqno()+"").getBytes().length);
			    lngChecksum = checksum.getValue();
				ackn = new ack(8, datap.getSeqno(),lngChecksum);				outBuf = serialize(ackn);
				outputPacket = new DatagramPacket(outBuf, 0, outBuf.length, inputPacket.getAddress(),
						inputPacket.getPort());
				socket.send(outputPacket);
			}
			if (data.endsWith("Error")) {

				System.out.println("file not exist");
				socket.close();
			}

			else {
				    if(stringbuild.length()>0){
				    	try {
							bufferwrite = new BufferedWriter(new FileWriter(filename + port, true));
							bufferwrite.write(stringbuild.toString().replace("\0", ""));
							bufferwrite.flush();
							stringbuild = new StringBuilder();
						} catch (IOException ioe) {
							ioe.printStackTrace();
						} finally { // always close the file
							if (bufferwrite != null)
								try {
									bufferwrite.close();
								} catch (IOException ioe2) {
									
								}
						} 
					}
					System.out.println("file sent successfully");
					socket.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error");
		}
	}

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}
}
