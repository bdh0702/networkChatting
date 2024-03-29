package SCP;


import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class ChatClient extends Frame implements ActionListener,KeyListener
{
   public Button cc_enterRoom;
   public Button cc_makeRoom;
   public List cc_lstRoom;
   public TextField cc_tfLogon; // 로그온 입력 텍스트 필드
   private Button cc_btLogon; // 로그온 실행 버튼
   private Button cc_btEnter; // 대화방 개설 및 입장 버튼
   private Button cc_btLogout; // 로그아웃 버튼

   public TextField cc_tfStatus; // 로그온 개설 안내
   public TextField cc_tfDate; // 개설시각
   public List cc_lstMember; // 대화방 참가자

   public static ClientThread cc_thread;
   public static ChatClient client;
   public String msg_logon="";

   public ChatClient(String str){
      super(str);
      setLayout(new BorderLayout());

      // 로그온, 대화방 개설 및 입장 버튼을 설정한다.
      Panel bt_panel = new Panel();
      bt_panel.setLayout(new FlowLayout());
      cc_btLogon = new Button("로그온실행");
      cc_btLogon.addActionListener(this);
      bt_panel.add(cc_btLogon);
      
      cc_tfLogon = new TextField(10);
      cc_tfLogon.addKeyListener(this);
      bt_panel.add(cc_tfLogon);
      
      cc_btEnter = new Button("대화방입장");
      cc_btEnter.addActionListener(this);
      bt_panel.add(cc_btEnter);
      
      cc_btLogout = new Button("로그아웃");
      cc_btLogout.addActionListener(this);
      bt_panel.add(cc_btLogout);
      
      add("Center",bt_panel);
      Panel panel = new Panel();
      panel.setLayout(new FlowLayout());
      
      cc_makeRoom = new Button("대화방개설");
      cc_makeRoom.addActionListener(this);
      panel.add(cc_makeRoom);
      
     
      panel.add(cc_btEnter);
      add("South", panel);

      // 4개의 Panel 객체를 사용하여 대화방 정보를 출력한다.
      Panel roompanel = new Panel(); // 3개의 패널을 담을 패널객체
      roompanel.setLayout(new BorderLayout());

      Panel northpanel = new Panel();
      northpanel.setLayout(new FlowLayout());
      cc_tfStatus = new TextField("하단의 텍스트 필드에  ID를 입력하십시오,",43); 
      													// 대화방의 개설상태 알림
      cc_tfStatus.setEditable(false);
      northpanel.add(cc_tfStatus);
      
      Panel centerpanel = new Panel();
      centerpanel.setLayout(new FlowLayout());
      centerpanel.add(new Label("로그온 시각 : "));
      cc_tfDate = new TextField("로그온 시각",29);
      cc_tfDate.setEditable(false);
      centerpanel.add(cc_tfDate);

      Panel southpanel = new Panel();
      southpanel.setLayout(new BorderLayout());
      
      southpanel.add("West",new Label("로그온 사용자"));
      
      Panel lstpanel = new Panel();
      lstpanel.setLayout(new BorderLayout());
      cc_lstMember = new List(10);
     
      lstpanel.add("West",cc_lstMember);
      cc_lstRoom = new List(10);
      lstpanel.add("East",cc_lstRoom);
      southpanel.add("South",lstpanel);   
      southpanel.add("East",new Label("대화방 목록"));
      

      roompanel.add("North", northpanel);
      roompanel.add("Center", centerpanel);
      roompanel.add("South", southpanel);
      add("North", roompanel);

      // 로그온 텍스트 필드에 포커스를 맞추는 메소드 추가

      addWindowListener(new WinListener());
   }

   class WinListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent we){
    	  if(!msg_logon.equals("")) {
    		  cc_thread.requestLogout(msg_logon);
    	      System.exit(0); // 나중에 로그아웃루틴으로 변경
    	  }
    	  else {
    		  System.exit(0); // 나중에 로그아웃루틴으로 변경
    	  }
    	 //cc_thread.requestLogout(msg_logon);
        
      }
   }

   // 로그온, 대화방 개설 및 입장 버튼 눌림 이벤트를 처리한다.
   public void actionPerformed(ActionEvent ae){
      Button b = (Button)ae.getSource();
      if(b.getLabel().equals("로그온실행")){
         // 로그온 처리 루틴
    	 if(!msg_logon.equals("")) {
    		 MessageBox msgBox = new MessageBox(this, "로그온 되어있습니다", "이미 로그인 되어있습니다");
             msgBox.show();
    	 }else {
    		 msg_logon = cc_tfLogon.getText(); // 로그온 ID를 읽는다.
             if(!msg_logon.equals("")){
                cc_thread.requestLogon(msg_logon); // ClientThread의 메소드를 호출
             }else{
                MessageBox msgBox = new  MessageBox(this, "로그온", "로그온 id를 입력하세요.");
                msgBox.show();
             }
    	 }
         
      }else if(b.getLabel().equals("대화방입장")){

         // 대화방 개설 및 입장 처리 루틴
    	  if(cc_tfLogon.isEditable()) {
    		  msg_logon="";
    	  }else {
    		  msg_logon = cc_tfLogon.getText();
    	  }
         //msg_logon = cc_tfLogon.getText(); // 로그온 ID를 읽는다.
         if(!msg_logon.equals("")){
            cc_thread.requestEnterRoom(msg_logon); // ClientThread의 메소드를 호출
         }else{
            MessageBox msgBox = new MessageBox(this, "로그온", "로그온을 먼저 하십시오.");
            msgBox.show();
         }

      }else if(b.getLabel().equals("로그아웃")){
    	  if(msg_logon.equals("")) {
    		  MessageBox msgBox = new MessageBox(this, "로그온", "로그온을 먼저 하십시오.");
    		  msgBox.show();
    	  }
    	  else {
    		  cc_thread.requestLogout(msg_logon);
    	  }
    	 
      // 로그아웃 처리 루틴
      }else if(b.getLabel().equals("대화방 개설")) {
    	  if(msg_logon.equals("")) {//로그온 안되어 있을 때
    		  MessageBox msgBox = new MessageBox(this, "로그온", "로그온을 먼저 하십시오.");
    		  msgBox.show();
    	  }else {
    		  cc_thread.requestMakeRoom(msg_logon);
    	  }
      }
   }

   public static void main(String args[]){
      client = new ChatClient("대화방 개설 및 입장");
      client.setSize(350, 400);
      client.show();

      // 소켓을 생성하고 서버와 통신할 스레드를 호출한다.
      
      // 서버와 클라이언트를 다른 시스템으로 사용하는 경우
      // 실행 : java ChatClient [호스트이름과 포트번호가 필요하다.]
      // To DO
      
      // 서버와 클라이언트를 같은 시스템으로 사용하는 경우
      // 실행 : java ChatClient [호스트이름과 포트번호가 필요없다.]
      try{
         cc_thread = new ClientThread(client); // 로컬 호스트용 생성자
         cc_thread.start(); // 클라이언트의 스레드를 시작한다.
      }catch(Exception e){
         System.out.println(e);
      }
   }
   
   public void keyPressed(KeyEvent ke) {
	      if(ke.getKeyChar() == KeyEvent.VK_ENTER) {
	    	  msg_logon = cc_tfLogon.getText(); // 로그온 ID를 읽는다.
	          if(!msg_logon.equals("")){
	             cc_thread.requestLogon(msg_logon); // ClientThread의 메소드를 호출
	          }else{
	             MessageBox msgBox = new  MessageBox(this, "로그온", "로그온 id를 입력하세요.");
	             msgBox.show();
	          }
	      }
	   }

   @Override
   public void keyReleased(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
   }

   @Override
   public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
   }
}
