package SCP;


import java.awt.*;
import java.awt.event.*;

public class DisplayRoom extends Frame implements ActionListener, KeyListener
{
   String ID;
   private Button dr_btClear; // 대화말 창 화면 지우기
   private Button dr_btLogout; // 로그아웃 실행 버튼
   
   public Label lb_logon;
   public TextField tf_logon;
   public TextArea dr_taContents; // 대화말 내용 리스트창
   public List dr_lstMember; // 대화방 참가자

   public TextField dr_tfInput; // 대화말 입력필드

   public static ClientThread dr_thread;

   public DisplayRoom(ClientThread client, String title,String ID){
      super(title);
      setLayout(new BorderLayout());

      // 대화방에서 사용하는 컴포넌트를 배치한다.
      Panel northpanel = new Panel();
      northpanel.setLayout(new FlowLayout());
      lb_logon = new Label("로그인 ID :");
      tf_logon = new TextField(ID,15);
      tf_logon.setEditable(false);
      northpanel.add(lb_logon);
      northpanel.add(tf_logon);
      dr_btClear = new Button("화면지우기"); 
      dr_btClear.addActionListener(this);
      northpanel.add(dr_btClear);
   
      dr_btLogout = new Button("퇴실하기");
      dr_btLogout.addActionListener(this);
      northpanel.add(dr_btLogout);

      Panel centerpanel = new Panel();
      centerpanel.setLayout(new FlowLayout());
      dr_taContents = new TextArea(10, 27);
      dr_taContents.setEditable(false);
      centerpanel.add(dr_taContents);
     
      dr_lstMember = new List(10);
      centerpanel.add(dr_lstMember);

      Panel southpanel = new Panel();
      southpanel.setLayout(new FlowLayout());
      dr_tfInput = new TextField(41);
      dr_tfInput.addKeyListener(this);
      southpanel.add(dr_tfInput);

      add("North", northpanel);
      add("Center", centerpanel);
      add("South", southpanel);

      dr_thread = client; // ClientThread 클래스와 연결한다.

      // 입력 텍스트 필드에 포커스를 맞추는 메소드 추가

      addWindowListener(new WinListener());

   }

   class WinListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent we){
         System.exit(0); // 로그아웃 루틴으로 바꾼다.
      }
   }

   // 화면지우기, 로그아웃 이벤트를 처리한다.
   public void actionPerformed(ActionEvent ae){
      Button b = (Button)ae.getSource();
      if(b.getLabel().equals("화면지우기")){
    	  dr_taContents.setText("");
      // 화면지우기 처리 루틴

      }else if(b.getLabel().equals("퇴실하기")){
    	  dr_thread.requestQuitRoom(dr_thread.ID);
    	  dispose();
    	  dr_thread.ct_client.pack();
    	  dr_thread.ct_client.show();
      // 로그아웃 처리 루틴
      }
   }

   // 입력필드에 입력한 대화말을 서버에 전송한다.
   public void keyPressed(KeyEvent ke){
      if(ke.getKeyChar() == KeyEvent.VK_ENTER){
         String words = dr_tfInput.getText(); // 대화말을 구한다.
         dr_thread.requestSendWords(words); // 대화말을 참여한 사용자에 전송한다.
      }
   }

   public void keyReleased(KeyEvent ke){}
   public void keyTyped(KeyEvent ke){}

}
