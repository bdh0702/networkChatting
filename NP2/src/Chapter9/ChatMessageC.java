package Chapter9;



//step3
//아이디와 대화말 메시지를 전송함
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;

public class ChatMessageC extends Frame implements ActionListener, KeyListener {
	Button btn_logout;
	TextArea display;
	TextField wtext, ltext;
	Label mlbl, wlbl, loglbl;
	BufferedWriter output;
	BufferedReader input;
	Socket client;
	StringBuffer clientdata = new StringBuffer(2048);
	String serverdata;
	String ID;
	int data;
	
	private static final String SEPARATOR = "|";
	private static final int REQ_LOGON = 1001;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_LOGOUT = 1010;
	private static final int REQ_LOGINCON = 1011;
	
	public ChatMessageC() {
		super("클라이언트");

		mlbl = new Label("채팅 상태를 보여줍니다.");
		add(mlbl, BorderLayout.NORTH);

		display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
		display.setEditable(false);
		add(display, BorderLayout.CENTER);

		Panel ptotal = new Panel(new BorderLayout());

		Panel pword = new Panel(new BorderLayout());
		wlbl = new Label("대화말");
		wtext = new TextField(30); //전송할 데이터를 입력하는 필드
		wtext.addKeyListener(this); //입력된 데이터를 송신하기 위한 이벤트 연결
		wtext.setEditable(false);
		pword.add(wlbl, BorderLayout.WEST);
		pword.add(wtext, BorderLayout.EAST);
		ptotal.add(pword, BorderLayout.CENTER);

		Panel plabel = new Panel(new BorderLayout());
		loglbl = new Label("로그온");
		ltext = new TextField(30); //전송할 데이터를 입력하는 필드
		ltext.addActionListener(this); //입력된 데이터를 송신하기 위한 이벤트 연결
		btn_logout = new Button("로그아웃");
		btn_logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {   
				ltext.setVisible(true);
				wtext.setEditable(false);
				mlbl.setText(ID+"(은)는 로그아웃  되었습니다.");
				try {
					clientdata.setLength(0);
					clientdata.append(REQ_LOGOUT);
					clientdata.append(SEPARATOR);
					clientdata.append(ID);
					//String ID=null;
					output.write(clientdata.toString()+"\r\n");
					output.flush();
				}catch(IOException e1) {
			   
				}
			}
		});
		plabel.add(btn_logout,BorderLayout.EAST);
		plabel.add(loglbl, BorderLayout.WEST);
		plabel.add(ltext, BorderLayout.CENTER);
		ptotal.add(plabel, BorderLayout.SOUTH);

		add(ptotal, BorderLayout.SOUTH);

		addWindowListener(new WinListener());
		setSize(300,250);
		setVisible(true);
	}
	
	public void runClient() {
		try {
			client = new Socket(InetAddress.getLocalHost(), 5000);
			mlbl.setText("연결된 서버이름 : " + client.getInetAddress().getHostName());
			input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			//clientdata = new StringBuffer(2048);
			mlbl.setText("접속 완료 사용할 아이디를 입력하세요.");
			while(true) {
				serverdata = input.readLine();
				String str = Integer.toString(REQ_LOGINCON);
				if(serverdata.contains(str)) {
					data = REQ_LOGINCON;
				}
				else {			
					display.append(serverdata+"\r\n");
				}
				//String str2 = serverdata;
				//StringTokenizer st = new StringTokenizer(str2, SEPARATOR);
				//if(Integer.parseInt(st.nextToken())!=REQ_LOGINCON) {
					
				//}
				//else {
				//	data = REQ_LOGINCON;
				//}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
		
	public void actionPerformed(ActionEvent ae){
		ID = ltext.getText();
		String serverdata;
		
		if(ID.equals("") != true) {
			
			try {
				clientdata.setLength(0);
				clientdata.append(REQ_LOGON);
				clientdata.append(SEPARATOR);
				clientdata.append(ID);
				output.write(clientdata.toString()+"\r\n");
				output.flush();
				
				Thread.sleep(100);
				if(data==REQ_LOGINCON) {
					mlbl.setText(ID + "은(는) 중복된 아이디입니다");
					ltext.setText("");
					ID="";
					data=0;
				}
				else {
					wtext.setEditable(true);
					mlbl.setText(ID + "(으)로 로그인 하였습니다.");
					ltext.setVisible(false);
				}
				
			} catch(Exception e) {
				//e.printStackTrace();
			}
		}
		else {
			mlbl.setText("다시 로그인 하세요!!!");
		}
	}
	
	public static void main(String args[]) {
		ChatMessageC c = new ChatMessageC();
		c.runClient();
	}
		
	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent e){
			try {
				
				clientdata.setLength(0);
				clientdata.append(REQ_LOGOUT);
				clientdata.append(SEPARATOR);
				clientdata.append(ID);
					//String ID=null;
				output.write(clientdata.toString()+"\r\n");
				output.flush();
				
				client.close();
				System.exit(0);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			
		}
	}

	public void keyPressed(KeyEvent ke) {
		if(ke.getKeyChar() == KeyEvent.VK_ENTER) {
			String message = new String();
			message = wtext.getText();
			if (ID == null) {
				mlbl.setText("다시 로그인 하세요!!!");
				wtext.setText("");
			} else {
				try {
					clientdata.setLength(0);
					clientdata.append(REQ_SENDWORDS);
					clientdata.append(SEPARATOR);
					clientdata.append(ID);
					clientdata.append(SEPARATOR);
					clientdata.append(message);
					output.write(clientdata.toString()+"\r\n");
					output.flush();
					wtext.setText("");
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}
	}

	public void keyReleased(KeyEvent ke) {
		
	}

	public void keyTyped(KeyEvent ke) {
		
	}
}