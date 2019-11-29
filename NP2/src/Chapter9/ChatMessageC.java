package Chapter9;



//step3
//���̵�� ��ȭ�� �޽����� ������
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
		super("Ŭ���̾�Ʈ");

		mlbl = new Label("ä�� ���¸� �����ݴϴ�.");
		add(mlbl, BorderLayout.NORTH);

		display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
		display.setEditable(false);
		add(display, BorderLayout.CENTER);

		Panel ptotal = new Panel(new BorderLayout());

		Panel pword = new Panel(new BorderLayout());
		wlbl = new Label("��ȭ��");
		wtext = new TextField(30); //������ �����͸� �Է��ϴ� �ʵ�
		wtext.addKeyListener(this); //�Էµ� �����͸� �۽��ϱ� ���� �̺�Ʈ ����
		wtext.setEditable(false);
		pword.add(wlbl, BorderLayout.WEST);
		pword.add(wtext, BorderLayout.EAST);
		ptotal.add(pword, BorderLayout.CENTER);

		Panel plabel = new Panel(new BorderLayout());
		loglbl = new Label("�α׿�");
		ltext = new TextField(30); //������ �����͸� �Է��ϴ� �ʵ�
		ltext.addActionListener(this); //�Էµ� �����͸� �۽��ϱ� ���� �̺�Ʈ ����
		btn_logout = new Button("�α׾ƿ�");
		btn_logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {   
				ltext.setVisible(true);
				wtext.setEditable(false);
				mlbl.setText(ID+"(��)�� �α׾ƿ�  �Ǿ����ϴ�.");
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
			mlbl.setText("����� �����̸� : " + client.getInetAddress().getHostName());
			input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			//clientdata = new StringBuffer(2048);
			mlbl.setText("���� �Ϸ� ����� ���̵� �Է��ϼ���.");
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
					mlbl.setText(ID + "��(��) �ߺ��� ���̵��Դϴ�");
					ltext.setText("");
					ID="";
					data=0;
				}
				else {
					wtext.setEditable(true);
					mlbl.setText(ID + "(��)�� �α��� �Ͽ����ϴ�.");
					ltext.setVisible(false);
				}
				
			} catch(Exception e) {
				//e.printStackTrace();
			}
		}
		else {
			mlbl.setText("�ٽ� �α��� �ϼ���!!!");
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
				mlbl.setText("�ٽ� �α��� �ϼ���!!!");
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