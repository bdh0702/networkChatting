package Chapter13;
// STEP 3
// 로그온 메시지와 대화말 메시지를 전송함
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

public class Multicast_Server extends Frame {
   TextArea display;
   Label info;
   DatagramPacket incoming;
   MulticastSocket ms;
   
   public ServerThread SThread;
   
   public Multicast_Server() {
      super("서버");
      info = new Label();
      add(info, BorderLayout.CENTER);
      display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
      display.setEditable(false);
      add(display, BorderLayout.SOUTH);
      addWindowListener(new WinListener());
      setSize(300,250);
      setVisible(true);
   }
   
   public void runServer() throws SocketException {
     DatagramSocket ds = new DatagramSocket(5021);
     try {
		 ms = new MulticastSocket();
		 info.setText("멀티캐스트 채팅 그룹 주소 : 239.255.10.10");
		 SThread = new ServerThread(this, ds,ms, display);
		 SThread.start();
		 
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
      
   public static void main(String args[]) {
      Multicast_Server s = new Multicast_Server();
      try {
		s.runServer();
	} catch (SocketException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
      
   class WinListener extends WindowAdapter {
      public void windowClosing(WindowEvent e) {
         System.exit(0);
      }
   }
}

class ServerThread extends Thread {
   DatagramSocket sock;
   TextArea display;
   Label info;
   TextField text;
   String clientdata=null;
   Multicast_Server cs;
   DatagramPacket incoming;
   DatagramPacket outcoming;
   MulticastSocket ms;
   InetAddress group;
   byte[] buffer = new byte[5000];
   private static final String SEPARATOR = "|";
   private static final int REQ_LOGON = 1001;
   private static final int REQ_SENDWORDS = 1021;
   private static final int LOGOUT =2001;
   

   public ServerThread(Multicast_Server c, DatagramSocket s, MulticastSocket m,TextArea ta) {
      sock = s;
      display = ta;
      cs = c;
      ms = m;
      incoming = new DatagramPacket(buffer,buffer.length);
		try {
			group = InetAddress.getByName("239.255.10.10");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
   public void run() {
      try {
         while(true)  {
       	  	sock.receive(incoming);
       	  	clientdata = new String(incoming.getData(),0,incoming.getLength());
       	  	System.out.println(clientdata);
            StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
            int command = Integer.parseInt(st.nextToken());
            switch(command) {
               case REQ_LOGON : { // “1001|아이디”를 수신한 경우
                  String ID= st.nextToken();              
                  display.append("클라이언트가 " + ID + "(으)로 로그인 하였습니다.\r\n");
                  try {
              		String address = "239.255.10.10:"+"5020";
              		byte[] data = address.getBytes();
              		outcoming = new DatagramPacket(data,data.length,incoming.getAddress(),incoming.getPort());
              		sock.send(outcoming);
              	} catch (IOException e1) {
              		// TODO Auto-generated catch block
              		e1.printStackTrace();
              	}
                  break;
               }
               case LOGOUT :{
                  String ID=st.nextToken();
                  display.append("클라이언트가 " + ID + "(이)가 로그아웃 하였습니다.\r\n");
                   break;
               }
               case REQ_SENDWORDS : { // “1021|아이디|대화말”를 수신
                   String ID = st.nextToken();
                   String message = st.nextToken();
                   display.append(ID + " : " + message + "\r\n");
                   String str =ID+":"+message;
                   System.out.println(str);
                   byte[] data = str.getBytes();
                   DatagramPacket outcoming = new DatagramPacket(data,data.length,group,5020);
                   ms.send(outcoming);
                   break;
               }
            }
            //incoming.setData(new byte[5000]);
            
           
         }
      } catch(IOException e) {
         e.printStackTrace();
      }    
      sock.close();
      ms.close();
   }
}