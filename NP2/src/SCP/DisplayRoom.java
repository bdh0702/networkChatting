package SCP;


import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class DisplayRoom extends Frame implements ActionListener, KeyListener ,MouseListener
{
   String ID,msg,receiver;
   private Button dr_btClear; // ��ȭ�� â ȭ�� �����
   private Button dr_btLogout; // �α׾ƿ� ���� ��ư
   private Button dr_btWhisper;
   private Button dr_btnFile; // ���� ���� ��ư
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
      dr_lstMember.addMouseListener(this);
      centerpanel.add(dr_lstMember);

      Panel southpanel = new Panel();
      southpanel.setLayout(new FlowLayout());
      dr_tfInput = new TextField(41);
      dr_tfInput.addKeyListener(this);
      dr_btnFile = new Button("���� ����");
      dr_btnFile.addActionListener(this);
      
      dr_btWhisper = new Button("���� ������");
      dr_btWhisper.addActionListener(this);
      
      southpanel.add(dr_tfInput);
      southpanel.add(dr_btWhisper);
      southpanel.add(dr_btnFile);

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
    	 dr_thread.requestLogout(dr_thread.ID);
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
      }else if(b.getLabel().equals("���� ������")) {
    	  if(receiver==null) {
    		  dr_taContents.append("�ӼӸ� ���ϴ� ��븦 Ŭ�����ּ���.\r\n");
    	  }
    	  else{
    		  msg=dr_tfInput.getText();
    		  if(!msg.equals("")){       		  
            	  dr_taContents.append(dr_thread.ID + " -> " + receiver +" : "+ msg +"\r\n" );
            	  dr_tfInput.setText("");
            	  dr_thread.requestwhisper(receiver, msg);
            	  msg=null;
        	  }else {
        		  dr_taContents.append(receiver+"�Կ��� ���� �޼����� �Է��ϼ���\r\n");
        	  }
    	  }	  
      }else if(b.getLabel().equals("���� ����")) {   	  
    	  if(receiver  != null) {
    		  dr_thread.requestSendFile(receiver);
    	  }
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

   @Override
   public void mouseClicked(MouseEvent arg0) {
	// TODO Auto-generated method stub
	   receiver = dr_lstMember.getSelectedItem();
	   
	   if(dr_thread.ID.equals(receiver)) {
		   dr_taContents.append("�ڱ� �ڽ��Դϴ�.\r\n");
		   receiver=null;
	   }
	   else{
		   dr_taContents.append(receiver+"���� �����ϼ̽��ϴ�.\r\n");
	   }
	   
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
