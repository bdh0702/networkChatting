package Game;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import Game.Client.WinListener;



public class GameRoom extends Frame implements ActionListener,KeyListener,MouseListener{
	private ClientThread gr_thread;
	public String ClickID="";
	static String roomName;
	public List lst_winner,lst_user;
	private Label lb_status;
	//public static final int MAX_PERSON = 2;
	public String l_people="",r_people="";
	public Button btn_start,btn_logout,btn_rock,btn_cizer,btn_paper,btn_result;
	public TextArea ta_chat;
	private TextField tf_chat;
	public int people = 0;
	private static final String DELIMETER = "`";
	public Vector userVector = new Vector(); //유저
	public Hashtable userHash = new Hashtable();//각유저와 서버스레드
	public int my_score;
	public String result;
	//private Vector userVector=new Vector(2);
	String userId="";
	public GameRoom() {
		
	}
	public GameRoom(ClientThread thread,String roomName) {
		super("게임방");
		this.roomName= roomName;
		this.gr_thread = thread;
		//this.l_people = ID;
		setLayout(new BorderLayout());
		people++;
		
	
		lb_status = new Label("가위 바위 보 게임장에 오신걸 환영합니다");
		lb_status.setBackground(Color.CYAN);
		add(lb_status,BorderLayout.NORTH);
		
		Panel panel1 = new Panel();
		panel1.setLayout(new FlowLayout());
		lst_winner = new List();
		lst_user = new List();
		lst_user.addMouseListener(this);
		//lst_user.add(l_people);
		Panel panel4 = new Panel();
		panel4.setLayout(new BorderLayout());
		panel4.add(new Label("모든 방 전체 순위"),BorderLayout.CENTER);
		panel4.add(lst_winner,BorderLayout.SOUTH);
		
		Panel panel5 = new Panel();
		panel5.setLayout(new BorderLayout());
		panel5.setBackground(Color.BLUE);
		panel5.add(new Label("사용자 목록"),BorderLayout.CENTER);
		panel5.add(lst_user,BorderLayout.SOUTH);
		//panel1.add(panel4);
		panel1.add(panel5);
		
	
		Panel panel2 = new Panel();
		panel2.setLayout(new FlowLayout());
		btn_start = new Button("게임시작");
		btn_start.addActionListener(this);
		btn_logout = new Button("방 나가기");
		btn_logout.addActionListener(this);
		btn_result = new Button("결과보기");
		btn_result.addActionListener(this);
		btn_rock = new Button("주먹");
		btn_rock.addActionListener(this);
		btn_cizer = new Button("가위");
		btn_cizer.addActionListener(this);
		btn_paper = new Button("보");
		btn_paper.addActionListener(this);
		btn_rock.setEnabled(false);
		btn_paper.setEnabled(false);
		btn_cizer.setEnabled(false);
		Panel panel6 = new Panel();
		panel6.setLayout(new BorderLayout());
		panel6.add(btn_start,BorderLayout.NORTH);
		panel6.add(btn_logout,BorderLayout.CENTER);
		panel6.add(btn_result,BorderLayout.SOUTH);
		
		Panel panel7 = new Panel();
		panel7.setLayout(new BorderLayout());
		panel7.add(btn_rock,BorderLayout.WEST);
		panel7.add(btn_cizer,BorderLayout.CENTER);
		panel7.add(btn_paper,BorderLayout.EAST);
		
		
		panel2.add(panel6);
		panel2.add(panel7);

		
		Panel panel3 = new Panel();
		panel3.setLayout(new BorderLayout());
		ta_chat = new TextArea();
		tf_chat = new TextField(30);
		tf_chat.addKeyListener(this);

		panel3.add(ta_chat,BorderLayout.CENTER);
		panel3.add(tf_chat,BorderLayout.SOUTH);
		//Panel panel4 = new Panel();
		
		add(panel1,BorderLayout.CENTER);
		add(panel2,BorderLayout.EAST);
		add(panel3,BorderLayout.SOUTH);
		addWindowListener(new WinListener());
		setVisible(true);
		pack();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Button b = (Button)e.getSource();
		if(b.getLabel().equals("게임시작")) {
			if(!ClickID.equals("")) {//요청수락해야함
				if(!l_people.equals(ClickID)) {
					btn_rock.setEnabled(true);
					btn_paper.setEnabled(true);
					btn_cizer.setEnabled(true);
					
					System.out.println(ClickID);
					gr_thread.requestSend(ClickID, roomName);
				}
				else {
					ta_chat.append("자기 자신과는 대결할 수 없습니다.\r\n");
				}
				
			}else {
				ta_chat.append("대결할 상대방을 먼저 클릭해주세요.\r\n");
			}

			
		}
		else if(b.getLabel().equals("방 나가기")) {
			setVisible(false);
			if(userId.equals(gr_thread.client.loginId)) {//상대방이 나가려는거면
				
			}else {//방장이 나가려는 거면 //나가기 요청 
				
			}
		}
		else if(b.getLabel().equals("주먹")) {
			my_score = 2;
			btn_paper.setEnabled(false);
			btn_cizer.setEnabled(false);
		}
		else if(b.getLabel().equals("가위")) {
			my_score = 1;
			btn_paper.setEnabled(false);
			btn_rock.setEnabled(false);
		}
		else if(b.getLabel().equals("보")) {
			my_score = 3;
			btn_rock.setEnabled(false);
			btn_cizer.setEnabled(false);
		}
		else if(b.getLabel().equals("결과보기")) {
			gr_thread.requestResult(ClickID, roomName, my_score);
		}
		
	}
	public void setLPeople(String s) {
		roomName = s;
		lst_user.add(s);
		l_people = s;
		lst_user.validate();
	}
	
	class WinListener extends WindowAdapter
	{
	    public void windowClosing(WindowEvent we){
	    	System.exit(0);
	    }
	}
	public boolean addUser(String id,ServerThread client) {
		/*if(people == MAX_PERSON) {
			return false;
		}*/
		userVector.addElement(id);
		userHash.put(id, client);
		people++;
		return true;
	}
	
	public synchronized String getUsers() {
		StringBuffer id = new StringBuffer();
		String ids;
		Enumeration enu = userVector.elements();
		while(enu.hasMoreElements()) {
			id.append(enu.nextElement());
			id.append(DELIMETER);
		}
		try {
			ids = new String(id);
			ids = ids.substring(0,ids.length()-1);
			
		}catch(StringIndexOutOfBoundsException e) {
			return "";
		}
		return ids;
	}
	
	public ServerThread getUser(String id) {
		ServerThread client = null;
		client = (ServerThread)userHash.get(id);
		return client;
		
	}
	public static synchronized String getRoomNumber() {
		return roomName;
	}
	
	public Hashtable getClients() {
		return userHash;
	}

	@Override
	public void keyPressed(KeyEvent e) { //여기다가 채팅시 
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub		
		 ClickID = lst_user.getSelectedItem();
		 if(gr_thread.client.loginId.equals(ClickID)) {
			 ta_chat.append("자기 자신은 선택 할 수 없습니다.\r\n");
		 }
		 else ta_chat.append(ClickID+"님을 선택하였습니다.\r\n");
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
