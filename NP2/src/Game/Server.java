package Game;
import java.io.*;
import java.net.*;


public class Server {
	public static final int port = 2777;
	
	public static void main(String args[]) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(true) {
				Socket socket = null;
				ServerThread client =null;
				try{
		            socket = serverSocket.accept(); // Ŭ���̾�Ʈ�� ������ ��ٸ���.
		            client = new ServerThread(socket);
		            client.start();
		            }catch(IOException e){
		               System.out.println(e);
		               try{
		                  if(socket != null)
		                     socket.close();
		               }catch(IOException e1){	            	
		                  System.out.println(e);
		               }finally{
		                  socket = null;
		               }
		            }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
