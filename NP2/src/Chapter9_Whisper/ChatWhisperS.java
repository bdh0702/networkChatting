package Chapter9_Whisper;

//Step 4
//클라이언트 간의 채팅에서 특정 클라이언트와의 귓속말 구현
//다음의 형식으로 귓속말 전송
///w 상대방아이디 대화말
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;


import java.awt.*;
import java.awt.event.*;

public class ChatWhisperS extends Frame {
   TextArea display;
   Label info;
   List<ServerThread_W> list;
   Hashtable hash;
public ServerThread_W SThread;
	
public ChatWhisperS() {
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
	
public void runServer() {
   ServerSocket server;
   Socket sock;
  // ServerThread_W SThread;
   try {
      server = new ServerSocket(5000, 100);
      hash = new Hashtable();
      list = new ArrayList<ServerThread_W>();
      try {
         while(true) {
            sock = server.accept();
            SThread = new ServerThread_W(this, sock, display, info);
            SThread.start();
            info.setText(sock.getInetAddress().getHostName() + " 서버는 클라이언트와 연결됨");
         }
      } catch(IOException ioe) {
         server.close();
         ioe.printStackTrace();
      }
   } catch(IOException ioe) {
      ioe.printStackTrace();
   }
			
}

public static void main(String args[]) {
   ChatWhisperS s = new ChatWhisperS();
   s.runServer();
}
		
class WinListener extends WindowAdapter {
   public void windowClosing(WindowEvent e) {
      System.exit(0);
   }
}
}

class ServerThread_W extends Thread {
Socket sock;
BufferedWriter output;
BufferedReader input;
TextArea display;
//Label info;
TextField text;
String clientdata;
String serverdata = "";
ChatWhisperS cs;
	
private static final String SEPARATOR = "|";
private static final int REQ_LOGON = 1001;
private static final int REQ_SENDWORDS = 1021;
private static final int REQ_WISPERSEND = 1022;
private static final int REQ_LOGONCON = 1023;
private static final int REQ_LOGOUT = 1010;

public ServerThread_W(ChatWhisperS c, Socket s, TextArea ta, Label l) {
   sock = s;
   display = ta;
  // info = l;
   cs = c;
   try {
      input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
   } catch(IOException ioe) {
      ioe.printStackTrace();
   }
}
public void run() {
   try {
      cs.list.add(this);
      while((clientdata = input.readLine()) != null) {
         StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
         int command = Integer.parseInt(st.nextToken());
         int Lcnt = cs.list.size();
         switch(command) {
            case REQ_LOGON : {
               String ID = st.nextToken(); 
               if(cs.hash.containsKey(ID)) {
            	   display.append("클라이언트가 중복된 이름"+ID+"로 로그인을 시도 하였습니다.\r\n");
            	   for(int i=0; i<Lcnt; i++) {
                  	  ServerThread_W SThread = (ServerThread_W)cs.list.get(i);
                  	  if(this.equals(SThread)) {
                  		  StringBuffer str = new StringBuffer(2048);
                  		  str.setLength(0);
                  		  str.append(REQ_LOGONCON);
                  		  str.append(SEPARATOR);
                  		 SThread.output.write(str.toString()+"\r\n");
                         SThread.output.flush();
                         cs.list.remove(i);
                         break;
                  	  }    
                    }
               }
               else {
            	   display.append("클라이언트가 " + ID + "(으)로 로그인 하였습니다.\r\n");
                   cs.hash.put(ID, this); // 해쉬 테이블에 아이디와 스레드를 저장한다
                   break;
               }
               
            }
            case REQ_SENDWORDS : {
               String ID = st.nextToken();
               String message = st.nextToken();
               display.append(ID + " : " + message + "\r\n");
               for(int i=0; i<Lcnt; i++) {
             	  ServerThread_W SThread = (ServerThread_W)cs.list.get(i);
                  SThread.output.write(ID + " : " + message + "\r\n");
                  SThread.output.flush();
               }
               break;
            }
            case REQ_WISPERSEND : {
               String ID = st.nextToken();
               String WID = st.nextToken();
               String message = st.nextToken();
               display.append(ID + " -> " + WID + " : " + message + "\r\n");
               ServerThread_W SThread = (ServerThread_W)cs.hash.get(ID);
     // 해쉬테이블에서 귓속말 메시지를 전송한 클라이언트의 스레드를 구함
               SThread.output.write(ID + " -> " + WID + " : " + message + "\r\n");
     // 귓속말 메시지를 전송한 클라이언트에 전송함
               SThread.output.flush();
               SThread = (ServerThread_W)cs.hash.get(WID);
     // 해쉬테이블에서 귓속말 메시지를 수신할 클라이언트의 스레드를 구함
               SThread.output.write(ID + " -> " + WID + " : " + message + "\r\n");
     // 귓속말 메시지를 수신할 클라이언트에 전송함
               SThread.output.flush();
               break;
            }
            case REQ_LOGOUT :{
				String str = st.nextToken(); //ID
				display.append(str + " : 로그아웃 하였습니다\r\n");
				if(Lcnt>1) {
					for(int i = 0; i<Lcnt; i++) {
						ServerThread_W SThread = (ServerThread_W)cs.list.get(i);
						//System.out.print(SThread);
						if(this.equals(SThread)) {
							cs.list.remove(i);
							cs.hash.remove(str);					
							break;
						}
					}
				}else break;
			}
         }
      }
   } catch(IOException e) {
      e.printStackTrace();
   }
   cs.list.remove(this);
   
   try{
      sock.close();
   }catch(IOException ea){
      ea.printStackTrace();
   }
}
}