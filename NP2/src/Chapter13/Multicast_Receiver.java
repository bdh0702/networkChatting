package Chapter13;
// step3
// ���̵�� ��ȭ�� �޽����� ������
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;

public class Multicast_Receiver extends Frame implements ActionListener, KeyListener {
   Button lb;
   TextArea display;
   TextField wtext, ltext;
   Label mlbl, wlbl, loglbl;
   StringBuffer clientdata = new StringBuffer(2048);
   String serverdata;
   String ID="";
   Panel plabel;
   DatagramSocket ds;
   int port;
   String address,message=null;
   MulticastSocket ms;
   DatagramPacket ms_incoming,ms_outgoing;
   
   byte[] buffer = new byte[65508];
   InetAddress group;
   private static final String SEPARATOR = "|";
   private static final int REQ_LOGON = 1001;
   private static final int REQ_SENDWORDS = 1021;
   private static final int LOGOUT =2001;
   
   public Multicast_Receiver() {
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

      plabel = new Panel(new BorderLayout());
      loglbl = new Label("�α׿�");
      lb = new Button("�α׾ƿ�");//�α׾ƿ� ��ư
      lb.setVisible(false);
      lb.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent arg0) {//�α׾ƿ� ��ư�� ��������
         try {
        	   //loglbl = new Label("�α׿�");
        	   //ltext = new TextField(30);
        	   lb.setVisible(false);
        	   validate();
        	   plabel.add(loglbl,BorderLayout.WEST);
        	   plabel.add(ltext,BorderLayout.CENTER);
        	   validate();
        	   clientdata.setLength(0);
               clientdata.append(LOGOUT);
               clientdata.append(SEPARATOR);
               clientdata.append(ID);
               byte[] data = clientdata.toString().getBytes();
               DatagramPacket outcoming = new DatagramPacket(data,data.length,InetAddress.getLocalHost(),5021);
               ds.send(outcoming);              
               ltext.setVisible(true);
               lb.setEnabled(false);
               mlbl.setText(ID + "(��)�� �α׾ƿ� �Ͽ����ϴ�.");
               ID="LOGOUT";
               
         }catch(Exception e) {
             e.printStackTrace();
         }
         
      }
         
      });
      ltext = new TextField(30); //������ �����͸� �Է��ϴ� �ʵ�
      ltext.addActionListener(this); //�Էµ� �����͸� �۽��ϱ� ���� �̺�Ʈ ����
      plabel.add(lb,BorderLayout.SOUTH);
      plabel.add(loglbl, BorderLayout.WEST);
      plabel.add(ltext, BorderLayout.CENTER);
      ptotal.add(plabel, BorderLayout.SOUTH);
      //lb.setEnabled(false);//�α׾ƿ� ��ư�� �α��� �ϱ������� ��� �������ϰ� false

      add(ptotal, BorderLayout.SOUTH);

      addWindowListener(new WinListener());
      setSize(300,250);
      setVisible(true);
   }
   
   public void runClient() {
    	try {
			ds = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	 ms_incoming = new DatagramPacket(buffer,buffer.length);
         mlbl.setText("��Ƽĳ��Ʈ ä�� ������ ���� ��û�մϴ�!");
         while(true) {
        	 try { 
        		 //System.out.println("");
        		Thread.sleep(100);
        		if(ID.equals("") ==false && ltext.isVisible()==false) {//��ĭ�� �ƴϸ�       
        			//Thread.sleep(1000);
        			ms.receive(ms_incoming);
    				serverdata = new String(ms_incoming.getData(),0,ms_incoming.getLength());   
    				StringTokenizer st = new StringTokenizer(serverdata,":");
    				String userID = st.nextToken();
    				String message = st.nextToken();			
    	            ms_incoming.setData(new byte[65508]);
    	            ms_incoming.setLength(65508);
    	            display.append(userID+" : "+ message+"\r\n");
    	            message=null;
        		}
        		
				
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				
			}
            
             
            
            
         }
      
   }
      
   public void actionPerformed(ActionEvent ae){  
     ID = ltext.getText();
      if(ID.equals("") != true) {
         mlbl.setText(ID + "(��)�� �α��� �Ͽ����ϴ�.");
         
         plabel.remove(loglbl);
         plabel.remove(ltext);
         //validate();
         lb.setVisible(true);
         //lb = new Button("�α׾ƿ�");
         //plabel.add(lb,BorderLayout.SOUTH);
         validate();
         //update();
         
         
         try {
            clientdata.setLength(0);
            clientdata.append(REQ_LOGON);
            clientdata.append(SEPARATOR);
            clientdata.append(ID);
            byte[] data = clientdata.toString().getBytes();
            DatagramPacket outcoming = new DatagramPacket(data,data.length,InetAddress.getLocalHost(),5021);
            ds.send(outcoming); 
            try {	
         		DatagramPacket incoming = new DatagramPacket(buffer,buffer.length);
         		ds.receive(incoming);
         		String str = new String(incoming.getData(),0,incoming.getLength());
         		StringTokenizer st = new StringTokenizer(str,":");
         		address = st.nextToken();
         		String str3 = st.nextToken();
         		port = Integer.parseInt(str3);
         		System.out.println(address+":"+port);	
         		group = InetAddress.getByName(address);	
         		ms = new MulticastSocket(port);
         		ms.joinGroup(group);
         		display.append("��Ƽĳ��Ʈ ä�� �׷� �ּҴ� "+address+":"+port+"�Դϴ�.\r\n");
         	} catch (UnknownHostException e1) {
         		// TODO Auto-generated catch block
         		e1.printStackTrace();
         	} catch (SocketException e) {
         		// TODO Auto-generated catch block
         		e.printStackTrace();
         	} catch (IOException e) {
         		// TODO Auto-generated catch block
         		e.printStackTrace();
         	}
            ltext.setVisible(false);
            lb.setEnabled(true);
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
      else {
         mlbl.setText("�ٽ� �α��� �ϼ���!!!");
         ID="";
      }
   }
   
   public static void main(String args[]) {
	   Multicast_Receiver c = new Multicast_Receiver();
      c.runClient();
   }
      
   class WinListener extends WindowAdapter {
      public void windowClosing(WindowEvent e){
         try {
               if(ID.equals("")|| ID=="LOGOUT") {           	   
            	   ds.close();
                   System.exit(0);
               }
               clientdata.setLength(0);
               clientdata.append(LOGOUT);
               clientdata.append(SEPARATOR);
               clientdata.append(ID);
               byte[] data = clientdata.toString().getBytes();
               DatagramPacket outcoming = new DatagramPacket(data,data.length,InetAddress.getLocalHost(),5021);
               ds.send(outcoming);              
               //mlbl.setText(ID + "(��)�� �α׾ƿ� �Ͽ����ϴ�.");
               //ID="LOGOUT";
               ms.close();
               ds.close();
               System.exit(0);
               
         }catch(Exception err) {
             
            
         }
         
      }
   }

   public void keyPressed(KeyEvent ke) {
      if(ke.getKeyChar() == KeyEvent.VK_ENTER) {
         message = new String();
         message = wtext.getText();
         if (ID.equals("") || ID=="LOGOUT") {
            mlbl.setText("�α��� �� �̿��ϼ���!!!");
            wtext.setText("");
            message =null;
         } else {
            try {
               clientdata.setLength(0);
               clientdata.append(REQ_SENDWORDS);
               clientdata.append(SEPARATOR);
               clientdata.append(ID);
               clientdata.append(SEPARATOR);
               clientdata.append(message);
               byte[] data = clientdata.toString().getBytes();
               DatagramPacket outcoming = new DatagramPacket(data,data.length,InetAddress.getLocalHost(),5021);
               ds.send(outcoming);              
               wtext.setText("");
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