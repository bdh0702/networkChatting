package Chapter13;
import java.net.*;
import java.io.*;
public class TestMulticast_Sender extends Thread{
	public static final int PORT=5021;
	public static final int BUFFER_SIZE=8192;
	protected DatagramSocket ds;
	public TestMulticast_Sender() throws SocketException{
		ds = new DatagramSocket(PORT);
	}
	public void run() {
		byte[] buffer = new byte[BUFFER_SIZE];
		
		while(true) {
			DatagramPacket incoming = new DatagramPacket(buffer,buffer.length);
			try {
				ds.receive(incoming);
				//String data = new String(incoming.getData(),0,incoming.getLength());
				//incoming.setLength(buffer.length);
				String address = "239.255.10.10";
				byte[] data = address.getBytes();
				DatagramPacket outgoing = new DatagramPacket(data,data.length,incoming.getAddress(),incoming.getPort());
				ds.send(outgoing);
				String sendmsg = "This is a multicast data";
				byte[] data2 = sendmsg.getBytes();
				InetAddress group = InetAddress.getByName(address);
				MulticastSocket ms = new MulticastSocket();
				DatagramPacket packet = new DatagramPacket(data2,data2.length,group,5020);
				System.out.println("멀티캐스트 메세지를 전송중입니다 : "+sendmsg);
				ms.send(packet);
				packet.setData(new byte[BUFFER_SIZE]);
				packet.setLength(BUFFER_SIZE);
			}catch(IOException e) {
				System.out.println("error");
			}
		}
	}
	
	public static void main(String args[]) {
		try {
			TestMulticast_Sender ts = new TestMulticast_Sender();
			ts.start();
		}catch(SocketException se) {
			System.out.println(se);
		}
	}
}
