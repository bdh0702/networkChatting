package Chapter9_Whisper;

//Step 4
//Ŭ���̾�Ʈ ���� ä�ÿ��� Ư�� Ŭ���̾�Ʈ���� �ӼӸ� ����
//������ �������� �ӼӸ� ����
///w ������̵� ��ȭ��
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class ChatWhisperC extends Frame implements ActionListener, KeyListener {
Button btn_login;
TextArea display;
TextField wtext, ltext;
Label mlbl, wlbl, loglbl;
BufferedWriter output;
BufferedReader input;
Socket client;
StringBuffer clientdata;
String serverdata;
String ID=null;
Boolean checkId;
	
private static final String SEPARATOR = "|";
private static final int REQ_LOGON = 1001;
private static final int REQ_SENDWORDS = 1021;
private static final int REQ_WISPERSEND = 1022;
private static final int REQ_LOGONCON = 1023;
private static final int REQ_LOGOUT = 1010;
	
public ChatWhisperC() {
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
   pword.add(wlbl, BorderLayout.WEST);
   pword.add(wtext, BorderLayout.EAST);
   ptotal.add(pword, BorderLayout.CENTER);

   Panel plabel = new Panel(new BorderLayout());
   loglbl = new Label("�α׿�");
   btn_login = new Button("�α���");
   btn_login.addActionListener(this);
   ltext = new TextField(30); //������ �����͸� �Է��ϴ� �ʵ�
   plabel.add(btn_login,BorderLayout.EAST);
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
	  checkId=false;
      client = new Socket(InetAddress.getLocalHost(), 5000);
      mlbl.setText("����� �����̸� : " + client.getInetAddress().getHostName());
      input = new BufferedReader(new InputStreamReader(client.getInputStream()));
      output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
      clientdata = new StringBuffer(2048);
      mlbl.setText("���� �Ϸ� ����� ���̵� �Է��ϼ���.");
      while(true) {
         serverdata = input.readLine();
         StringTokenizer st = new StringTokenizer(serverdata,SEPARATOR.toString());
         if(Integer.parseInt(st.nextToken())!=(REQ_LOGONCON)) {
        	 display.append(serverdata+"\r\n");
             output.flush();
         }else {
        	 checkId=true;
         }
        
      }
   } catch(IOException e) {
      e.printStackTrace();
   }
}
		
public void actionPerformed(ActionEvent ae){
	if(ae.getActionCommand().equals("�α���")) {
		if(ID == null) {
			   
		      ID = ltext.getText();
		      mlbl.setText(ID + "(��)�� �α��� �Ͽ����ϴ�.");
		      try {
		         clientdata.setLength(0);
		         clientdata.append(REQ_LOGON);
		         clientdata.append(SEPARATOR);
		         clientdata.append(ID);
		         output.write(clientdata.toString()+"\r\n");
		         output.flush();
		         
		         Thread.sleep(100);
		         if(checkId) { //���̵� �ߺ�������
		        	 mlbl.setText(ID + "��(��) �ߺ��� ���̵��Դϴ�");
					 ltext.setText("");
					 ID=null;
					 checkId=false;
					 
		         }else {
		        	 wtext.setEditable(true);
					 mlbl.setText(ID + "(��)�� �α��� �Ͽ����ϴ�.");
					 ltext.setVisible(false);
					 btn_login.setLabel("�α׾ƿ�");
		         }
		         
		      } catch(Exception e) {
		         e.printStackTrace();
		      }
		   }
	}else {
		btn_login.setLabel("�α���");
		ltext.setVisible(true);
		wtext.setEditable(false);
		mlbl.setText(ID+"(��)�� �α׾ƿ�  �Ǿ����ϴ�.");
		ID=null;
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
   
}
	
public static void main(String args[]) {
   ChatWhisperC c = new ChatWhisperC();
   c.runClient();
}
		
class WinListener extends WindowAdapter {
   public void windowClosing(WindowEvent e){
      System.exit(0);
   }
}

public void keyPressed(KeyEvent ke) {
   if(ke.getKeyChar() == KeyEvent.VK_ENTER) {
      String message = wtext.getText();
      StringTokenizer st = new StringTokenizer(message, " ");
      if (ID == null) {
         mlbl.setText("�ٽ� �α��� �ϼ���!!!");
         wtext.setText("");
      } else {
         try {
            if(st.nextToken().equals("/w")) {
               message = message.substring(3); // ��/w���� �����Ѵ�.
               String WID = st.nextToken();
               String Wmessage = st.nextToken();
               while(st.hasMoreTokens()) { // ���鹮�� ������ ���� ��ȭ���߰�
                  Wmessage = Wmessage + " " + st.nextToken();
               }
               clientdata.setLength(0);
               clientdata.append(REQ_WISPERSEND);
               clientdata.append(SEPARATOR);
               clientdata.append(ID);
               clientdata.append(SEPARATOR);
               clientdata.append(WID);
               clientdata.append(SEPARATOR);
               clientdata.append(Wmessage);
               output.write(clientdata.toString()+"\r\n");
               output.flush();
               wtext.setText("");
            } else {
               clientdata.setLength(0);
               clientdata.append(REQ_SENDWORDS);
               clientdata.append(SEPARATOR);
               clientdata.append(ID);
               clientdata.append(SEPARATOR);
               clientdata.append(message);
               output.write(clientdata.toString()+"\r\n");
               output.flush();
               wtext.setText("");
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}

public void keyReleased(KeyEvent ke) {
}

public void keyTyped(KeyEvent ke) {
}
}