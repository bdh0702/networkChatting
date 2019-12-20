package Game;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

class CreateRoom extends Dialog implements ActionListener
{
   
   Button btn_ok,btn_cancel;
   ClientThread client;
   String roomName="";
   String name="";
   private static final int REQ_CREATEROOM = 2002;
   private static final String SEPARATOR = "|";
   public CreateRoom(Frame parent, String title,ClientThread client,String name){
      super(parent, title, false); // modal Dialog ����
      this.client=client;
      this.name = name;
      setLayout(new BorderLayout());
      Panel panel1 = new Panel();
      Label roomTitle = new Label("�ڽ��� ���̵������ ���� ����ðڽ��ϱ�?");
      panel1.add(roomTitle,BorderLayout.CENTER);
       
      Panel panel2 = new Panel();
      btn_ok = new Button("OK");
      btn_ok.addActionListener(this);
      btn_cancel = new Button("Cancel");
      btn_cancel.addActionListener(this);
      panel2.add(btn_ok,BorderLayout.WEST);
      panel2.add(btn_cancel,BorderLayout.CENTER);
      
      add(panel1,BorderLayout.CENTER);
      add(panel2,BorderLayout.SOUTH);
      
      pack();
   }
   public void actionPerformed(ActionEvent ae){
	   Button b = (Button)ae.getSource();
	   if(b.getLabel().equals("OK")) {		   
		//`client.client.myRoom=tf_roomName.getText().toString();
		   try {
			   client.ct_buffer.setLength(0);
			   client.ct_buffer.append(REQ_CREATEROOM);
			   client.ct_buffer.append(SEPARATOR);
			   client.ct_buffer.append(name);
			   client.ct_buffer.append(SEPARATOR);
			   client.ct_buffer.append(client);
			   client.send(client.ct_buffer.toString());
			   System.out.println("hihi");
			   setVisible(false); // OK ��ư�� ������ �޽��� �ڽ��� �����Ѵ�.
		   }catch(IOException e) {
			   
		   }
		  
		   
	   //else {
		//	MessageBox msg = new MessageBox(client.client,"���� ���Է�","������ �Է��ϼ���");
		//	msg.show();
		//}		   
	   }
	   else if(b.getLabel().equals("Cancel")) {
		   setVisible(false); // OK ��ư�� ������ �޽��� �ڽ��� �����Ѵ�.
	   }
      
   }

}
