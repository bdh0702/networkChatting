package Game;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Client extends Frame implements ActionListener,KeyListener,MouseListener{
	List lst_room;
	String myRoom="";
	Label lb_login;
	Button btn_login,btn_enterRoom,btn_createRoom;
	TextField tf_login;
	String loginId= null;
	String clickRoom="";
	
	public static ClientThread c_thread;
	public static Client client;
	public Client() {
		super("가위바위보 게임");
		setLayout(new BorderLayout());
		
		Panel panel1 = new Panel();
		lb_login = new Label("현재 접속중인 ID : "+loginId);
		panel1.setLayout(new BorderLayout());
		panel1.setBackground(Color.CYAN);
		panel1.add(new Label("눈치싸움의 끝판왕은 누구? 가위바위보 게임의 우승자가 되어보세요!"),BorderLayout.NORTH);
		panel1.add(lb_login,BorderLayout.SOUTH);
		
		Panel panel2 = new Panel();
		panel2.setLayout(new BorderLayout());
		btn_login = new Button("로그인");
		btn_login.addActionListener(this);
		btn_login.addKeyListener(this);
		btn_enterRoom = new Button("방 입장");
		btn_enterRoom.addActionListener(this);
		btn_createRoom = new Button("방 생성");
		btn_createRoom.addActionListener(this);
		panel2.add(btn_login,BorderLayout.NORTH);
		panel2.add(btn_enterRoom,BorderLayout.CENTER);
		panel2.add(btn_createRoom,BorderLayout.SOUTH);
		
		Panel panel3 = new Panel();
		panel3.setLayout(new BorderLayout());
		lst_room = new List(10);
		lst_room.addMouseListener(this);
		tf_login = new TextField("로그인할 ID를 입력하세요",10);
		panel3.add(lst_room,BorderLayout.CENTER);
		
		panel3.add(tf_login,BorderLayout.NORTH);
			
		add(panel1,BorderLayout.NORTH);
		add(panel2,BorderLayout.EAST);
		add(panel3,BorderLayout.CENTER);
		
		setVisible(true);
		addWindowListener(new WinListener());
	}
	
	class WinListener extends WindowAdapter
	{
	    public void windowClosing(WindowEvent we){
	    	System.exit(0);
	    }
	}
	
	
	public static void main(String args[]) {
		 client = new Client();
	     client.setSize(400, 300);
	     client.show();

	     c_thread = new ClientThread(client);
	     c_thread.start();
	}
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button)ae.getSource();
		if(b.getLabel().equals("로그인")) {
			if(loginId == null) {
				loginId = tf_login.getText();
				c_thread.requestLogin(loginId);
			}
			else{
				MessageBox msg = new MessageBox(this,"로그인 에러","이미 로그인 되어있습니다.");
				msg.show();
			}
		}
		else if(b.getLabel().equals("방 생성")) {
			if(loginId != null) {
				c_thread.requesCreateRoom(loginId);
				//CreateRoom createRoom = new CreateRoom(this,"방 생성하기",c_thread);
				//createRoom.show();
				
				if(!myRoom.equals("")) {
					//c_thread.requesCreateRoom(myRoom);
				//}
				}
				
				
			}else {
				MessageBox msg = new MessageBox(this,"방 생성에러","로그인을 먼저 하세요.");
				msg.show();
			}
			
		}
		else if(b.getLabel().equals("방 입장")) {
			if(clickRoom.equals("")) {
				MessageBox msg = new MessageBox(this,"방 입장에러","방을 먼저 클릭하세요.");
				msg.show();
			}
			else {
				c_thread.requestEnterRoom(loginId, clickRoom);
			}
		}
		else if(b.getLabel().equals("로그아웃")) {
			if(loginId != null) {
				c_thread.requestLogout(loginId);
			}
		}
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		 clickRoom = lst_room.getSelectedItem();
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
