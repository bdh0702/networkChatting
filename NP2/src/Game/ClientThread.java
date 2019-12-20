package Game;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;


import javax.swing.border.*;

public class ClientThread extends Thread{
	
	public String roomName="";
	public Client client;
	private Socket ct_sock;
	public StringBuffer ct_buffer; // ����
	private Thread thisThread;
	public DataInputStream ct_in; // �Է� ��Ʈ��
	public DataOutputStream ct_out; // ��� ��Ʈ��
	public CreateRoom createRoom;
	public GameRoom gameRoom;
	public MessageBox fileTransBox;
	private static final String SEPARATOR = "|";
	private static final String DELIMETER = "`";
	private static final int PORT = 2777;
	
	private static final int YES_LOGIN = 1000;
	private static final int NO_LOGIN = 1001;
	private static final int YES_LOGOUT = 1002;
	private static final int NO_CREATEROOM = 1003;
	private static final int YES_CREATEROOM = 1004;
	private static final int YES_ENTERROOM = 1005;
	private static final int NO_ENTERROOM = 1006;
	private static final int YES_SEND = 1007;
	private static final int NO_SEND = 1008;
	private static final int YES_WINNER = 1009;
	private static final int YES_DRAW = 1010;
	private static final int YES_LOSE = 1011;
	
	private static final int REQ_LOGIN = 2000;
	private static final int REQ_LOGOUT = 2001;
	private static final int REQ_CREATEROOM = 2002;
	private static final int REQ_ENTERROOM = 2003;
	private static final int REQ_SEND = 2004;
	private static final int REQ_RESULT = 2005;
	private static final int MDY_ROOMS = 4000;
	private static final int MSG_ALREADYUSER = 3000;
	private static final int MDY_ROOMPEOPLE = 4001;
	
	private static final int ERR_REJECTION = 5000;
	
	public ClientThread(Client client) {
		this.client = client;
		
		try {
			ct_sock= new Socket(InetAddress.getLocalHost(),PORT);
			ct_in = new DataInputStream(ct_sock.getInputStream());
	        ct_out = new DataOutputStream(ct_sock.getOutputStream());
	        ct_buffer = new StringBuffer(4096);
	        thisThread = this;
	        
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void run() {
		Thread currentThread = Thread.currentThread();
		try {
			while(thisThread==currentThread) {
				String recvData = ct_in.readUTF();
	            StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
	            System.out.println(recvData);     
	            int command = Integer.parseInt(st.nextToken());
	            switch(command) {
	            	case YES_LOGIN:{
	            		client.tf_login.setEditable(false);
	            		client.btn_login.setLabel("�α׾ƿ�");
	            		client.lb_login.setText("���� �������� ID : "+client.loginId);
	            		break;
	            	}
	            	case NO_LOGIN:{
	            		String id = st.nextToken();
	            		MessageBox msg = new MessageBox(client,"���� �Ұ�",id+"��(��) �̹� �α��� �Ǿ��ֽ��ϴ� ");
	            		msg.show();
	            		client.loginId=null;
	            		
	            		break;
	            	}
	            	case YES_LOGOUT:{           		
	            		client.tf_login.setEditable(true);
	            		client.tf_login.setText("");
	            		client.btn_login.setLabel("�α���");
	            		client.loginId=null;
	            		client.lb_login.setText("���� �������� ID : "+client.loginId);
	            		client.lst_room.clear();
	            		break;
	            	}
	            	case MDY_ROOMS : {
	            		System.out.println("ggggg");
	            		client.lst_room.clear();
	            		String roomNames = st.nextToken();
	            		StringTokenizer stt = new StringTokenizer(roomNames,DELIMETER);
	            		while(stt.hasMoreTokens()){
	                        client.lst_room.add(stt.nextToken());
	                     }	            		
	              	   	break;
	            	}
	            	case NO_CREATEROOM : {
	            		MessageBox msg = new MessageBox(client,"�� ���� ����","�̹� �����ϴ� �������Դϴ�");
	            		msg.show();
	            		roomName="";
	            		break;
	            	}
	            	case YES_CREATEROOM :{
	            		 //�� ������
	            		gameRoom= new GameRoom(this,client.loginId);
	            		gameRoom.setLPeople(client.loginId);
	            		gameRoom.show();
	            		client.hide();
	            		break;
	            	}
	            	case YES_ENTERROOM : {
	            		String Id = st.nextToken();
	            		String roomName = st.nextToken();
	            		gameRoom= new GameRoom(this,roomName);
	            		gameRoom.r_people = Id;
	            		gameRoom.lst_user.add(Id);
	            		client.hide();
	            		break;
	            	}
	            	case NO_ENTERROOM : {
	            		
	            		break;
	            	}
	            	
	            	case MDY_ROOMPEOPLE : {
	            		 gameRoom.lst_user.clear(); // ��� ID�� �����Ѵ�.
	                     String ids = st.nextToken(); // ��ȭ�� ������ ����Ʈ
	                     StringTokenizer roomusers = new StringTokenizer(ids, DELIMETER);
	                     while(roomusers.hasMoreTokens()){
	                        gameRoom.lst_user.add(roomusers.nextToken());
	                     }
	                     break;
	            	}
	            	 case REQ_SEND : {
	              	   String id = st.nextToken(); //���� ��� ���̵�
	              	   String my_id = st.nextToken();
	              	   String message = id +"�� ���� ���� ��û�� �����Ͻðڽ��ϱ�?";
	              	   String roomName = st.nextToken();
	              	   int value = JOptionPane.showConfirmDialog(gameRoom, message,"���ϼ���",JOptionPane.YES_NO_OPTION);
	              	   if(value == 1) { //���� ������
	              		   //���� ���̵� + ���´� ��� id 
	              		   try {
	              			   ct_buffer.setLength(0);
	              			   ct_buffer.append(NO_SEND);
	              			   ct_buffer.append(SEPARATOR);
	              			   ct_buffer.append(client.loginId);
	              			   ct_buffer.append(SEPARATOR);
	              			   ct_buffer.append(id);
	              			   ct_buffer.append(SEPARATOR);
	              			   ct_buffer.append(roomName);
	              			   send(ct_buffer.toString());
	              		   }catch(IOException e) {
	              			   System.out.println(e);
	              		   }
	              			   
	              		   
	              	   }else {//��û ���� ��
	              		   StringTokenizer addr = new StringTokenizer(InetAddress.getLocalHost().toString(),"/");
	              		   //ȣ��Ʈ�� ��Ʈ��ȣ�� ������ ���ؼ� �����ּ�/��Ʈ��ȣ �� ����������
	              		   gameRoom.btn_rock.setEnabled(true);
	              		   gameRoom.btn_cizer.setEnabled(true);
	              		   gameRoom.btn_paper.setEnabled(true);
	              		   gameRoom.ta_chat.append(id + " vs " + my_id +"�԰��� ����� �����մϴ�.\r\n");
	              		   gameRoom.ta_chat.append("���� ���� �� �߿��� �����Ͽ� �ּ���.\r\n");
	              		   try {
	              			   ct_buffer.setLength(0);
	              			   ct_buffer.append(YES_SEND);
	              			   ct_buffer.append(SEPARATOR);
	              			   ct_buffer.append(client.loginId); //���� ���̵� + ���� ��û���´� id +��Ʈ��ȣ�� ����
	              			   ct_buffer.append(SEPARATOR);
	              			   ct_buffer.append(id);	              
	              			   ct_buffer.append(SEPARATOR);
	              			   ct_buffer.append(roomName);
	              			   send(ct_buffer.toString());
	              		   }catch(IOException e) {
	              			   System.out.println(e);
	              		   }
	              		   //���� ���� ���� ����
	              		   //new Receive();
	              	   }
	              	   break;
	                 }
	            	 
	            	 case YES_SEND:{
	              	   String id = st.nextToken();
	              	   String my_id = st.nextToken();
	              	   String roomName = st.nextToken();
	              	   //System.out.println(addr);
	              	   
	              	   fileTransBox.dispose();
	              	   //new Send(addr,port);
	              	   gameRoom.ta_chat.append(id + " vs " + my_id +"�԰��� ����� �����մϴ�.\r\n");
	              	   gameRoom.ta_chat.append("���� ���� �� �߿��� �����Ͽ� �ּ���.\r\n");
	              	   break;
	                 }
	            	 case NO_SEND : {
	              	   int code = Integer.parseInt(st.nextToken());
	              	   String id = st.nextToken();
	              	   fileTransBox.dispose();
	              	   
	              	   if(code==ERR_REJECTION) {
	              		   String message =id+"���� ��û�� �ź��Ͽ����ϴ�.";
	              		   JOptionPane.showMessageDialog(gameRoom, message,"���� ��û",JOptionPane.ERROR_MESSAGE);;
	              		   break;
	              	   }
	                 }
	            	 case REQ_RESULT : {
	            		 String idTo = st.nextToken();
	            		 String my_id = st.nextToken();
	            		 String roomName = st.nextToken();
	            		 int scoreTo = Integer.parseInt(st.nextToken());
	            		 
	            		 switch(gameRoom.my_score) {
	            		 	case 1 : { //�����ΰ��
	            		 		if(scoreTo == 2) {//�ָ�
	            		 			  try {
	       	              			   ct_buffer.setLength(0);
	       	              			   ct_buffer.append(YES_WINNER);// �ʰ� �̰��
	       	              			   ct_buffer.append(SEPARATOR);	
	       	              			   ct_buffer.append(idTo);
	       	              			   ct_buffer.append(SEPARATOR);
	       	              			   ct_buffer.append(my_id);
	       	              			   ct_buffer.append(SEPARATOR);	       	              			   
	       	              			   ct_buffer.append(roomName);
	       	              			   send(ct_buffer.toString());
	       	              			   
	       	              			   gameRoom.ta_chat.append("����� �й��Ͽ����ϴ�.. �ٽõ����ϼ���!\r\n");
	       	              			   break;
	       	              		   }catch(IOException e) {
	       	              			   System.out.println(e);
	       	              		   }
	            		 			  
	            		 		}else if(scoreTo == 1) { //����
	            		 			try {
		       	              			   ct_buffer.setLength(0);
		       	              			   ct_buffer.append(YES_DRAW);// ����
		       	              			   ct_buffer.append(SEPARATOR);	
		       	              			   ct_buffer.append(idTo);
		       	              			   ct_buffer.append(SEPARATOR);
		       	              			   ct_buffer.append(my_id);
		       	              			   ct_buffer.append(SEPARATOR);	       	              			   
		       	              			   ct_buffer.append(roomName);
		       	              			   send(ct_buffer.toString());
		       	              			   
		       	              			   gameRoom.ta_chat.append("�����ϴ�. �ƽ��׿�!!\r\n");
		       	              			   break;
		       	              		   }catch(IOException e) {
		       	              			   System.out.println(e);
		       	              		   }
	            		 		}else { //���ΰ��
	            		 			try {
		       	              			   ct_buffer.setLength(0);
		       	              			   ct_buffer.append(YES_LOSE);// ����
		       	              			   ct_buffer.append(SEPARATOR);	
		       	              			   ct_buffer.append(idTo);
		       	              			   ct_buffer.append(SEPARATOR);
		       	              			   ct_buffer.append(my_id);	
		       	              			   ct_buffer.append(SEPARATOR);	       	              			   
		       	              			   ct_buffer.append(roomName);
		       	              			   send(ct_buffer.toString());
		       	              			   
		       	              			   gameRoom.ta_chat.append("����� �¸��Ͽ����ϴ�! ���ϵ帳�ϴ�!\r\n");
		       	              			   break;
		       	              		   }catch(IOException e) {
		       	              			   System.out.println(e);
		       	              		   }
	            		 		}
	            		 		break;
	            		 	}
	            		 	
	            		 	case 2:{//�ָ�
	            		 		if(scoreTo == 2) {
	            		 			  try {
	       	              			   ct_buffer.setLength(0);
	       	              			   ct_buffer.append(YES_DRAW);// �ʰ� �̰��
	       	              			   ct_buffer.append(SEPARATOR);	
	       	              			   ct_buffer.append(idTo);
	       	              			   ct_buffer.append(SEPARATOR);
	       	              			   ct_buffer.append(my_id);
	       	              			   ct_buffer.append(SEPARATOR);	       	              			   
	       	              			   ct_buffer.append(roomName);
	       	              			   send(ct_buffer.toString());
	       	              			   gameRoom.ta_chat.append("�����ϴ�. �ƽ��׿�!!\r\n");
	       	              			   break;
	       	              		   }catch(IOException e) {
	       	              			   System.out.println(e);
	       	              		   }
	            		 		}else if(scoreTo == 1) {
	            		 			try {
		       	              			   ct_buffer.setLength(0);
		       	              			   ct_buffer.append(YES_LOSE);// ����
		       	              			   ct_buffer.append(SEPARATOR);	
		       	              			   ct_buffer.append(idTo);
		       	              			   ct_buffer.append(SEPARATOR);
		       	              			   ct_buffer.append(my_id);
		       	              			   ct_buffer.append(SEPARATOR);	       	              			   
		       	              			   ct_buffer.append(roomName);
		       	              			   send(ct_buffer.toString());
		       	              			   gameRoom.ta_chat.append("����� �¸��Ͽ����ϴ�! ���ϵ帳�ϴ�!\r\n");
		       	              			   break;
		       	              		   }catch(IOException e) {
		       	              			   System.out.println(e);
		       	              		   }
	            		 		}else {
	            		 			try {
		       	              			   ct_buffer.setLength(0);
		       	              			   ct_buffer.append(YES_WINNER);// ����
		       	              			   ct_buffer.append(SEPARATOR);	
		       	              			   ct_buffer.append(idTo);
		       	              			   ct_buffer.append(SEPARATOR);
		       	              			   ct_buffer.append(my_id);
		       	              			   ct_buffer.append(SEPARATOR);	       	              			   
		       	              			   ct_buffer.append(roomName);
		       	              			   send(ct_buffer.toString());
		       	              			   gameRoom.ta_chat.append("����� �й��Ͽ����ϴ�.. �ٽõ����ϼ���!\r\n");
		       	              			   break;
		       	              		   }catch(IOException e) {
		       	              			   System.out.println(e);
		       	              		   }
	            		 		}
	            		 		break;
	            		 	}
	            		 	
	            		 	case 3:{
	            		 		if(scoreTo == 2) {
	            		 			  try {
	       	              			   ct_buffer.setLength(0);
	       	              			   ct_buffer.append(YES_LOSE);// �ʰ� ����
	       	              			   ct_buffer.append(SEPARATOR);	
	       	              			   ct_buffer.append(idTo);
	       	              			   ct_buffer.append(SEPARATOR);
	       	              			   ct_buffer.append(my_id);
	       	              			   ct_buffer.append(SEPARATOR);	       	              			   
	       	              			   ct_buffer.append(roomName);
	       	              			   send(ct_buffer.toString());
	       	              			   
	       	              			   gameRoom.ta_chat.append("����� �¸��Ͽ����ϴ�! ���ϵ帳�ϴ�!\r\n");
	       	              			   break;
	       	              		   }catch(IOException e) {
	       	              			   System.out.println(e);
	       	              		   }
	            		 		}else if(scoreTo == 1) {
	            		 			try {
		       	              			   ct_buffer.setLength(0);
		       	              			   ct_buffer.append(YES_WINNER);// ����
		       	              			   ct_buffer.append(SEPARATOR);	
		       	              			   ct_buffer.append(idTo);
		       	              			   ct_buffer.append(SEPARATOR);
		       	              			   ct_buffer.append(my_id);
		       	              			   ct_buffer.append(SEPARATOR);	       	              			   
		       	              			   ct_buffer.append(roomName);
		       	              			   send(ct_buffer.toString());
		       	              			   gameRoom.ta_chat.append("����� �й��Ͽ����ϴ�.. �ٽõ����ϼ���!\r\n");
		       	              			   break;
		       	              		   }catch(IOException e) {
		       	              			   System.out.println(e);
		       	              		   }
	            		 		}else {
	            		 			try {
		       	              			   ct_buffer.setLength(0);
		       	              			   ct_buffer.append(YES_DRAW);// ����
		       	              			   ct_buffer.append(SEPARATOR);	
		       	              			   ct_buffer.append(idTo);
		       	              			   ct_buffer.append(SEPARATOR);
		       	              			   ct_buffer.append(my_id);
		       	              			   ct_buffer.append(SEPARATOR);	       	              			   
		       	              			   ct_buffer.append(roomName);
		       	              			   send(ct_buffer.toString());
		       	              			   gameRoom.ta_chat.append("�����ϴ�. �ƽ��׿�!!\r\n");
		       	              			   break;
		       	              		   }catch(IOException e) {
		       	              			   System.out.println(e);
		       	              		   }
	            		 		}
	            		 		break;
	            		 		
	            		 	}
	            		 	
	            		 }
	            	 }
	            	 case YES_WINNER :{
	            		 
	            		 //fileTransBox.dispose();
	            		 gameRoom.ta_chat.append("����� �¸��Ͽ����ϴ� �����մϴ�.\r\n");
	            		 break;
	            	 }
	            	 case YES_DRAW : {
	            		 //fileTransBox.dispose();
	            		 gameRoom.ta_chat.append("�����ϴ�. �ƽ��׿�.\r\n");
	            		 break;
	            	 }
	            	 case YES_LOSE : {
	            		 //fileTransBox.dispose();
	            		 gameRoom.ta_chat.append("����� �й��Ͽ����ϴ�. �ٽ� �����غ�����!\r\n");
	            		 break;
	            	 }
  
	            }
	            Thread.sleep(200);
			}
			
		}catch(IOException e){
	         System.out.println(e);	         
	    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void requestLogin(String id) {
		try {
			ct_buffer.setLength(0);
			ct_buffer.append(REQ_LOGIN);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString());
			//System.out.println(ct_buffer.toString());
		}catch(IOException e) {
			
		}		
	}
	public void requestLogout(String id) {
		try {
			ct_buffer.setLength(0);
			ct_buffer.append(REQ_LOGOUT);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString());
			//System.out.println(ct_buffer.toString());
		}catch(IOException e) {
			
		}		
	}
	public void requestEnterRoom(String name,String roomName) {
		try {
			ct_buffer.setLength(0);
			ct_buffer.append(REQ_ENTERROOM);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(name); //id 
			ct_buffer.append(SEPARATOR);
			//ct_buffer.append(gameRoom.people); // �ο� 
			//ct_buffer.append(SEPARATOR);
			ct_buffer.append(roomName);
			send(ct_buffer.toString());
			//System.out.println(ct_buffer.toString());
		}catch(IOException e) {
			
		}		
	}
	public void requesCreateRoom(String name) {	

		CreateRoom createRoom = new CreateRoom(client,"�� �����ϱ�",this,name);
		createRoom.show();
	   
		
			
	}
	public void requestSend(String receive_ID,String roomName) {   //��û�ϴ� ���̵� // �޴¾��̵� ������ ����
		   fileTransBox = new MessageBox(gameRoom,"���� ��û","������ ������ ��ٸ��ϴ�");
		   fileTransBox.show();
		   try {
			   ct_buffer.setLength(0);
			   ct_buffer.append(REQ_SEND);
			   ct_buffer.append(SEPARATOR);
			   ct_buffer.append(client.loginId);
			   ct_buffer.append(SEPARATOR);
			   ct_buffer.append(receive_ID);
			   ct_buffer.append(SEPARATOR);
			   ct_buffer.append(roomName);
			   send(ct_buffer.toString());		   
		   }catch(IOException e) {
			   System.out.println(e);
		   }
		   
	   }
	
	public void requestResult(String receive_ID,String roomName,int score) {   //���� ���̵� + �޴� ��� id + ���̸� + ������ ���� 
		   //fileTransBox = new MessageBox(gameRoom,"���� ��� Ȯ��","������ ����� ��ٸ��ϴ�");
		  // fileTransBox.show();
		   try {
			   ct_buffer.setLength(0);
			   ct_buffer.append(REQ_RESULT);
			   ct_buffer.append(SEPARATOR);
			   ct_buffer.append(client.loginId);
			   ct_buffer.append(SEPARATOR);
			   ct_buffer.append(receive_ID);
			   ct_buffer.append(SEPARATOR);
			   ct_buffer.append(roomName);
			   ct_buffer.append(SEPARATOR);
			   ct_buffer.append(score);
			   send(ct_buffer.toString());		   
		   }catch(IOException e) {
			   System.out.println(e);
		   }
		   
	   }
	public void send(String sendData) throws IOException {
	      ct_out.writeUTF(sendData);
	      ct_out.flush();
	}
}
