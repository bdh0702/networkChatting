package Chapter9_Whisper;

//Step 4
//Ŭ���̾�Ʈ ���� ä�ÿ��� Ư�� Ŭ���̾�Ʈ���� �ӼӸ� ����
//������ �������� �ӼӸ� ����
///w ������̵� ��ȭ��
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
   super("����");
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
            info.setText(sock.getInetAddress().getHostName() + " ������ Ŭ���̾�Ʈ�� �����");
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
            	   display.append("Ŭ���̾�Ʈ�� �ߺ��� �̸�"+ID+"�� �α����� �õ� �Ͽ����ϴ�.\r\n");
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
            	   display.append("Ŭ���̾�Ʈ�� " + ID + "(��)�� �α��� �Ͽ����ϴ�.\r\n");
                   cs.hash.put(ID, this); // �ؽ� ���̺� ���̵�� �����带 �����Ѵ�
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
     // �ؽ����̺��� �ӼӸ� �޽����� ������ Ŭ���̾�Ʈ�� �����带 ����
               SThread.output.write(ID + " -> " + WID + " : " + message + "\r\n");
     // �ӼӸ� �޽����� ������ Ŭ���̾�Ʈ�� ������
               SThread.output.flush();
               SThread = (ServerThread_W)cs.hash.get(WID);
     // �ؽ����̺��� �ӼӸ� �޽����� ������ Ŭ���̾�Ʈ�� �����带 ����
               SThread.output.write(ID + " -> " + WID + " : " + message + "\r\n");
     // �ӼӸ� �޽����� ������ Ŭ���̾�Ʈ�� ������
               SThread.output.flush();
               break;
            }
            case REQ_LOGOUT :{
				String str = st.nextToken(); //ID
				display.append(str + " : �α׾ƿ� �Ͽ����ϴ�\r\n");
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