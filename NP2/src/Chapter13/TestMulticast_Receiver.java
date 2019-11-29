package Chapter13;
import java.io.*;
import java.net.*;
public class TestMulticast_Receiver {
	protected DatagramSocket ds;
	byte[] buffer = new byte[65508];
	public TestMulticast_Receiver(){
		try {
			ds = new DatagramSocket();
			InetAddress inetAddress = InetAddress.getLocalHost();
			String str = "hi";
			byte[] data = str.getBytes();
			DatagramPacket incoming = new DatagramPacket(data,data.length,InetAddress.getLocalHost(),5021);
			ds.send(incoming);
			DatagramPacket outcoming = new DatagramPacket(buffer,buffer.length);
			ds.receive(outcoming);
			String str2 = new String(outcoming.getData(),0,outcoming.getLength());
			InetAddress group = InetAddress.getByName(str2);
			MulticastSocket ms= new MulticastSocket(5020);
			DatagramPacket datas = new DatagramPacket(buffer,buffer.length);
			ms.setSoTimeout(10000);
			ms.setTimeToLive(1);
			ms.joinGroup(group);
			//DatagramPacket incoming2 = new DatagramPacket(buffer,buffer.length);
			ms.receive(datas);
			//ms.receive(incoming2);
			String msg = new String(datas.getData(),0,datas.getLength());
			System.out.println("수신된 메세지는 : "+msg);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(UnknownHostException e) {
			
		}catch(IOException e1) {
			
		}
		
	}
	
	public static void main(String args[]) {
		TestMulticast_Receiver tr = new TestMulticast_Receiver();
	}
}
