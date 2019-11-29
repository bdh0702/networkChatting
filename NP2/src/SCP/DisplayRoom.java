package SCP;


import java.awt.*;
import java.awt.event.*;

public class DisplayRoom extends Frame implements ActionListener, KeyListener
{
   String ID;
   private Button dr_btClear; // ��ȭ�� â ȭ�� �����
   private Button dr_btLogout; // �α׾ƿ� ���� ��ư
   
   public Label lb_logon;
   public TextField tf_logon;
   public TextArea dr_taContents; // ��ȭ�� ���� ����Ʈâ
   public List dr_lstMember; // ��ȭ�� ������

   public TextField dr_tfInput; // ��ȭ�� �Է��ʵ�

   public static ClientThread dr_thread;

   public DisplayRoom(ClientThread client, String title,String ID){
      super(title);
      setLayout(new BorderLayout());

      // ��ȭ�濡�� ����ϴ� ������Ʈ�� ��ġ�Ѵ�.
      Panel northpanel = new Panel();
      northpanel.setLayout(new FlowLayout());
      lb_logon = new Label("�α��� ID :");
      tf_logon = new TextField(ID,15);
      tf_logon.setEditable(false);
      northpanel.add(lb_logon);
      northpanel.add(tf_logon);
      dr_btClear = new Button("ȭ�������"); 
      dr_btClear.addActionListener(this);
      northpanel.add(dr_btClear);
   
      dr_btLogout = new Button("����ϱ�");
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

      dr_thread = client; // ClientThread Ŭ������ �����Ѵ�.

      // �Է� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���ߴ� �޼ҵ� �߰�

      addWindowListener(new WinListener());

   }

   class WinListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent we){
         System.exit(0); // �α׾ƿ� ��ƾ���� �ٲ۴�.
      }
   }

   // ȭ�������, �α׾ƿ� �̺�Ʈ�� ó���Ѵ�.
   public void actionPerformed(ActionEvent ae){
      Button b = (Button)ae.getSource();
      if(b.getLabel().equals("ȭ�������")){
    	  dr_taContents.setText("");
      // ȭ������� ó�� ��ƾ

      }else if(b.getLabel().equals("����ϱ�")){
    	  dr_thread.requestQuitRoom(dr_thread.ID);
    	  dispose();
    	  dr_thread.ct_client.pack();
    	  dr_thread.ct_client.show();
      // �α׾ƿ� ó�� ��ƾ
      }
   }

   // �Է��ʵ忡 �Է��� ��ȭ���� ������ �����Ѵ�.
   public void keyPressed(KeyEvent ke){
      if(ke.getKeyChar() == KeyEvent.VK_ENTER){
         String words = dr_tfInput.getText(); // ��ȭ���� ���Ѵ�.
         dr_thread.requestSendWords(words); // ��ȭ���� ������ ����ڿ� �����Ѵ�.
      }
   }

   public void keyReleased(KeyEvent ke){}
   public void keyTyped(KeyEvent ke){}

}
