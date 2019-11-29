package Chapter9;


//STEP 3
//�α׿� �޽����� ��ȭ�� �޽����� ������
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

public class ChatMessageS extends Frame {
	TextArea display;
	Label info;
	List<ServerThread> list;
	HashMap<String,Integer> hm;
	int num=0;
	
public ServerThread SThread;
	
public ChatMessageS() {
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
		//ServerThread SThread;
		try {
			list = new ArrayList<ServerThread>();
			hm = new HashMap<String,Integer>();
			server = new ServerSocket(5000, 100);
			try {
				while(true) {
					sock = server.accept();
//              	SThread = new ServerThread(this, sock, display, info);
					SThread = new ServerThread(this, sock, display);
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
		ChatMessageS s = new ChatMessageS();
		s.runServer();
	}
		
	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			
			System.exit(0);
		}
	}
}

class ServerThread extends Thread {
	Socket sock;
	BufferedWriter output;
	BufferedReader input;
	TextArea display;
	Label info;
	TextField text;
	String clientdata;
	//String serverdata = "";
	ChatMessageS cs;
	
	private static final String SEPARATOR = "|";
	private static final int REQ_LOGON = 1001;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_LOGOUT = 1010;
	private static final int REQ_LOGINCON = 1011;
	
	//public ServerThread(ChatMessageS c, Socket s, TextArea ta, Label l) {
	public ServerThread(ChatMessageS c, Socket s, TextArea ta) {
		sock = s;
		display = ta;
//     	info = l;
		cs = c;
		try {
			input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	public void run() {
		//  cs.list.add(this);
		try {
			while((clientdata = input.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				int cnt = cs.list.size();
				switch(command) {
					case REQ_LOGON : { // ��1001|���̵𡱸� ������ ���
						String ID = st.nextToken();
						if(!cs.hm.containsKey(ID)) {
							cs.list.add(this);
							cs.hm.put(ID, cs.num);
							cs.num++;
							display.append("Ŭ���̾�Ʈ�� " + ID + "(��)�� �α��� �Ͽ����ϴ�.\r\n");
							break;
						}else {
							String serverdata = ""+REQ_LOGINCON;
							output.write(serverdata+"\r\n");
							output.flush();
							display.append("Ŭ���̾�Ʈ�� �ߺ��� ���̵�� �α��� �õ��� �Ͽ����ϴ�\r\n");
							break;
							
						}

					}
					case REQ_SENDWORDS : { // ��1021|���̵�|��ȭ������ ����
						String ID = st.nextToken();
						String message = st.nextToken();
						display.append(ID + " : " + message + "\r\n");
						for(int i=0; i<cnt; i++) { // ��� Ŭ���̾�Ʈ�� ����
							ServerThread SThread = (ServerThread)cs.list.get(i);
							SThread.output.write(ID + " : " + message + "\r\n");
							SThread.output.flush();
						}
						break;
					}
					case REQ_LOGOUT :{
						cnt = cs.list.size();
						String str = st.nextToken(); //ID
						
						display.append(str + " : �α׾ƿ� �Ͽ����ϴ�\r\n");
						if(cnt>1) {
							for(int i = 0; i<cnt; i++) {
								ServerThread SThread = (ServerThread)cs.list.get(i);
								//System.out.print(SThread);
								if(this.equals(SThread)) {
									cs.list.remove(i);
									cs.hm.remove(str);					
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