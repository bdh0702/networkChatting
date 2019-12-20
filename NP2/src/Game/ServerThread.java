package Game;

import java.io.*;
import java.net.*;
import java.util.*;








public class ServerThread extends Thread{
	private Socket st_sock;
	private DataInputStream st_in;
	private DataOutputStream st_out;
	private StringBuffer st_buffer;
	public String st_ID;
	private ClientThread client;
	public GameRoom gr_room;
	public String st_id;
	private static Hashtable<String,ServerThread> logonHash; 
	private static Vector<String> logonVector;
	
	private static Hashtable<String,ServerThread> roomUserHash;
	private static Vector<String> roomUserVector;
	private static Hashtable roomNameHash;
	private static Vector roomNameVector;
	
	private static final String SEPARATOR = "|";
	private static final String DELIMETER = "`";
	private static final String DELIMETER1 = "=";
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
	static {
		logonHash = new Hashtable<String,ServerThread>();
	    logonVector = new Vector<String>(); 
	    roomNameHash = new Hashtable();
	    roomNameVector = new Vector();
	    roomUserHash = new Hashtable();
	    roomUserVector = new Vector<String>();
	    
	}
	
	public ServerThread(Socket sock) {
		try{			 
	         st_sock = sock;
	         st_in = new DataInputStream(sock.getInputStream()); 
	         st_out = new DataOutputStream(sock.getOutputStream());
	         st_buffer = new StringBuffer(2048);
	      }catch(IOException e){
	         System.out.println(e);
	      }
	}
	
	public void run() {
		try {
			while(true){				
	            String recvData = st_in.readUTF();
	            System.out.println(recvData);
	            StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
	            int command = Integer.parseInt(st.nextToken());
	            switch(command) {
	            	case REQ_LOGIN:{
	            		int result;
	            		//System.out.println("gigi2");
	            		st_id = st.nextToken();
	            		result = addUser(st_id,this);
	            		st_buffer.setLength(0);
	            		if(result ==0) { //���� ���
	            			//System.out.println("gigi2");
	            			st_buffer.append(YES_LOGIN);
	            			st_buffer.append(SEPARATOR);
	            			st_buffer.append(st_id);
	            			send(st_buffer.toString());
	            			
	            			String roomNames = getRoomNames();
	            			if(!roomNames.equals("")) {
	            				st_buffer.setLength(0);
		            			st_buffer.append(MDY_ROOMS);
		            			st_buffer.append(SEPARATOR);
		            			st_buffer.append(roomNames);
		            			System.out.println(roomNames);
		            			user_broadcast(st_buffer.toString());	 	            		
	            			}
	            			
	            		}
	            		else {//���� �Ұ�
	            			st_buffer.append(NO_LOGIN);
	            			st_buffer.append(SEPARATOR);
	            			st_buffer.append(st_id);
	            			send(st_buffer.toString());
	            		}
	            		break;
	            	}
	            	case REQ_LOGOUT:{
	            		String id = st.nextToken();
	              	  	logonVector.remove(id);
	              	  	logonHash.remove(id);
	              	  	st_buffer.setLength(0);
	              	  	st_buffer.append(YES_LOGOUT);
	              	  	st_buffer.append(SEPARATOR);
	              	  	st_buffer.append(id);
	              	  	send(st_buffer.toString());
	              	  	System.out.println("good");
	              	  	break;
	            	}
	            	case REQ_CREATEROOM : {
	            		String roomName = st.nextToken();
	            		//System.out.println(roomName);
	            		if(roomNameVector.contains(roomName)) {	 
	            			System.out.println("1");
	            			st_buffer.setLength(0);
	            			st_buffer.append(NO_CREATEROOM);
	            			st_buffer.append(SEPARATOR);
	            			send(st_buffer.toString());
	            		}
	            		else {
	            			System.out.println("2");
	            			
	            			
	            			gr_room = new GameRoom();
	            			gr_room.roomName=roomName;
	            			roomNameVector.addElement(gr_room);
	            			roomNameHash.put(roomName,gr_room);
	            			Boolean temp = gr_room.addUser(st_id, this);
	            			//roomUserVector.addElement(roomName);
	            			//roomUserHash.put(roomName,gr_room);
	            			
	            			//GameRoom gameRoom = new GameRoom();	            			
	            			String roomNames = getRoomNames();
	            			st_buffer.setLength(0);
	            			st_buffer.append(YES_CREATEROOM);
	            			st_buffer.append(SEPARATOR);
	            			send(st_buffer.toString());
	            			System.out.println("ggggg2");
	            			
	            			st_buffer.setLength(0);
	            			st_buffer.append(MDY_ROOMS);
	            			st_buffer.append(SEPARATOR);
	            			st_buffer.append(roomNames);
        			//System.out.println(roomNames);
	            			user_broadcast(st_buffer.toString());	 
	            			//System.out.println("ggggg");
	            			
	            		}
	            			
	            		
      		
	            		break;
	            	}
	            	case REQ_ENTERROOM : {
	            			String ID = st.nextToken();
	            			//int people = Integer.parseInt(st.nextToken());
	            			String roomName = st.nextToken();
	            			//ServerThread sh = (ServerThread)roomNameHash.get(roomName);
	            			//gr_room = new GameRoom();
	            			
	            			GameRoom tempRoom = (GameRoom) roomNameHash.get(roomName);		           	    	  		            	
	            			Boolean temp = tempRoom.addUser(ID, this);
	            			/*if(tempRoom.people.) {
	            				System.out.println("2");
	            				st_buffer.setLength(0);
		            			st_buffer.append(NO_ENTERROOM);
		            			st_buffer.append(SEPARATOR);
		            			send(st_buffer.toString());
	            			}else {*/
	            				System.out.println("1");
	            				st_buffer.setLength(0);
		            			st_buffer.append(YES_ENTERROOM);
		            			st_buffer.append(SEPARATOR);
		            			st_buffer.append(ID);
		            			st_buffer.append(SEPARATOR);
		            			st_buffer.append(roomName);
		            			send(st_buffer.toString());
		            			
		            			//GameRoom tempRoom = (GameRoom) roomNameHash.get(roomName);		           	    	  	
		            			String ids = tempRoom.getUsers();
		            			st_buffer.setLength(0);
		            			st_buffer.append(MDY_ROOMPEOPLE);
		            			st_buffer.append(SEPARATOR);
		            			st_buffer.append(ids);
		            			roomUser_broadcast(st_buffer.toString(),roomName);
	            			//}
	            			//roomUserVector.addElement(ID);
	            			//roomUserHash.put(ID,this);
	            			//if(people == 2) {
	            				//����
	            				/*st_buffer.setLength(0);
		            			st_buffer.append(NO_ENTERROOM);
		            			st_buffer.append(SEPARATOR);
		            			send(st_buffer.toString());*/
	            			//}
	            			//else { //���� ����
	            				/*st_buffer.setLength(0);
		            			st_buffer.append(YES_ENTERROOM);
		            			st_buffer.append(SEPARATOR);
		            			st_buffer.append(ID);
		            			send(st_buffer.toString());
		            			
		            			
		            			String ids = getRoomUsers();
		            			st_buffer.setLength(0);
		            			st_buffer.append(MDY_ROOMPEOPLE);
		            			st_buffer.append(SEPARATOR);
		            			st_buffer.append(ids);
		            			roomUser_broadcast(st_buffer.toString(),roomName);*/
	            			//}
	            			break;
	            	}
	            	case REQ_SEND :{
	             	   String send_id = st.nextToken();
	             	   String receive_id = st.nextToken();
	             	   String roomName = st.nextToken();
	             	   GameRoom tempRoom = (GameRoom) roomNameHash.get(roomName);
	             	   
	             	   //System.out.println("send_id ="+send_id);
	             	   //System.out.println("receive_id ="+receive_id);
	             	   st_buffer.setLength(0);
	             	   st_buffer.append(REQ_SEND);             	   
	             	   st_buffer.append(SEPARATOR);
	             	   st_buffer.append(send_id);
	             	   st_buffer.append(SEPARATOR);
	             	  st_buffer.append(receive_id);
	             	   st_buffer.append(SEPARATOR);
	             	   st_buffer.append(roomName);
	             	   st_buffer.append(SEPARATOR);
	             	   System.out.println(st_buffer.toString());
	             	   ServerThread client;
	             	   client=(ServerThread)tempRoom.userHash.get(receive_id);
	             	   client.send(st_buffer.toString());
	             	   
	             	  
	             	  
	             	   break;
	             	               	   
	                }
	            	 case YES_SEND :{//���� ���̵� + ���� id +��Ʈ��ȣ�� ����  //���´� ����� �����̴ϱ� ��Ʈ��ȣ�� �˷��༭ �����Ҽ� �ְ� ����
	             	   String id = st.nextToken();
	             	   String idTo = st.nextToken();	             	   
	             	   String roomName = st.nextToken();
	             	   
	             	   GameRoom tempRoom = (GameRoom) roomNameHash.get(roomName);
	             	   ServerThread client;
	             	   client=(ServerThread)tempRoom.userHash.get(idTo);
	             	   
	             	   st_buffer.setLength(0);
	             	   st_buffer.append(YES_SEND);
	             	   st_buffer.append(SEPARATOR);
	             	   st_buffer.append(id);
	             	   st_buffer.append(SEPARATOR);
	             	   st_buffer.append(idTo);
	             	   st_buffer.append(SEPARATOR);
	             	   st_buffer.append(roomName);
	             	   
	             	   client.send(st_buffer.toString());
	             	   break;
           	   
	                }
	            	 case NO_SEND :{//��û���´� ��� id + ��û ���� ��� id ���ۉ�
	              	   String id= st.nextToken();//��û ���´� ���id
	              	   String idTo = st.nextToken();
	              	   String roomName = st.nextToken();
	              	   GameRoom tempRoom = (GameRoom) roomNameHash.get(roomName);
	              	   ServerThread client;
	              	   client=(ServerThread)tempRoom.userHash.get(idTo);
	              	   
	              	   st_buffer.setLength(0); //��û �����޼��� + �����źθ޼��� + ��û���´� ��� id�� ����
	              	   st_buffer.append(NO_SEND);
	              	   st_buffer.append(SEPARATOR);
	              	   st_buffer.append(ERR_REJECTION);
	              	   st_buffer.append(SEPARATOR);
	              	   st_buffer.append(id);
	              	   
	              	   client.send(st_buffer.toString());
	              	   break;
	            	 }
	            	 case REQ_RESULT : { //���� ���̵� + �����»�� id + ���̸� + ������
	            		 String my_id = st.nextToken();
	            		 String idTo = st.nextToken();
	            		 String roomName = st.nextToken();
	            		 String myScore = st.nextToken();
	            		 GameRoom tempRoom = (GameRoom) roomNameHash.get(roomName);
		              	 ServerThread client;
		              	 client=(ServerThread)tempRoom.userHash.get(idTo);
		              	 
	            		 st_buffer.setLength(0); //��û �����޼��� + �����źθ޼��� + ��û���´� ��� id�� ����
		              	 st_buffer.append(REQ_RESULT);
		              	 st_buffer.append(SEPARATOR);
		              	 st_buffer.append(my_id);
		              	 st_buffer.append(SEPARATOR);
		              	 st_buffer.append(idTo);
		              	 st_buffer.append(SEPARATOR);
		              	 st_buffer.append(roomName);
		              	 st_buffer.append(SEPARATOR);
		              	 st_buffer.append(myScore);
		              	 client.send(st_buffer.toString());
		              	 break;
	            	 }
	            	 case YES_WINNER : {
	            		 String idTo = st.nextToken();
	            		 String my_id = st.nextToken();
	            		 String roomName = st.nextToken();
	            		
	            		 GameRoom tempRoom = (GameRoom) roomNameHash.get(roomName);
		              	 ServerThread client;
		              	 client=(ServerThread)tempRoom.userHash.get(idTo);
		              	 st_buffer.setLength(0); //��û �����޼��� + �����źθ޼��� + ��û���´� ��� id�� ����
		              	 st_buffer.append(YES_WINNER);
		              	 st_buffer.append(SEPARATOR);	              	
		              	 client.send(st_buffer.toString());
		              	 
		              	 /*client=(ServerThread)tempRoom.userHash.get(my_id);
		              	 st_buffer.setLength(0); //��û �����޼��� + �����źθ޼��� + ��û���´� ��� id�� ����
		              	 st_buffer.append(YES_LOSE);
		              	 st_buffer.append(SEPARATOR);	              	
		              	 client.send(st_buffer.toString());*/
	            		 break;
	            	 }
	            	 case YES_DRAW : {
	            		 String idTo = st.nextToken();
	            		 String my_id = st.nextToken();
	            		 String roomName = st.nextToken();
	            		 GameRoom tempRoom = (GameRoom) roomNameHash.get(roomName);
		              	 ServerThread client;
		              	 client=(ServerThread)tempRoom.userHash.get(idTo);
		              	 st_buffer.setLength(0); //��û �����޼��� + �����źθ޼��� + ��û���´� ��� id�� ����
		              	 st_buffer.append(YES_DRAW);
		              	 st_buffer.append(SEPARATOR);
		          
		              	 client.send(st_buffer.toString());
		              	 
		              	 /*client=(ServerThread)tempRoom.userHash.get(my_id);
		              	 st_buffer.setLength(0); //��û �����޼��� + �����źθ޼��� + ��û���´� ��� id�� ����
		              	 st_buffer.append(YES_DRAW);
		              	 st_buffer.append(SEPARATOR);	              	
		              	 client.send(st_buffer.toString());*/
	            		 break;
	            	 }
	            	 case YES_LOSE : {
	            		 String idTo = st.nextToken();
	            		 String my_id = st.nextToken();
	            		 String roomName = st.nextToken();
	            		 GameRoom tempRoom = (GameRoom) roomNameHash.get(roomName);
		              	 ServerThread client;
		              	 client=(ServerThread)tempRoom.userHash.get(idTo);
		              	 st_buffer.setLength(0); //��û �����޼��� + �����źθ޼��� + ��û���´� ��� id�� ����
		              	 st_buffer.append(YES_LOSE);
		              	 st_buffer.append(SEPARATOR);
		              	
		              	 client.send(st_buffer.toString());
		              	 
		              	 /*client=(ServerThread)tempRoom.userHash.get(my_id);
		              	 st_buffer.setLength(0); //��û �����޼��� + �����źθ޼��� + ��û���´� ��� id�� ����
		              	 st_buffer.append(YES_WINNER);
		              	 st_buffer.append(SEPARATOR);	              	
		              	 client.send(st_buffer.toString());*/
	            		 break;
	            	 }
           	
	            }            
	            Thread.sleep(100);
			}
		}catch(NullPointerException e){
			// �α׾ƿ��� st_in�� �� ���ܸ� �߻��ϹǷ�
	    }catch(InterruptedException e){
	    	
	    }catch(IOException e){
	    	
	    }
	}
	private static ServerThread checkUserID(String id){
		ServerThread alreadyClient = null;
	    alreadyClient = (ServerThread) logonHash.get(id);
	    return alreadyClient;
	}	
	
	private static synchronized int addUser(String id, ServerThread client){
		if(checkUserID(id) != null){
			return MSG_ALREADYUSER;
		}  		
		logonVector.addElement(id);  // ����� ID �߰�
		logonHash.put(id, client); // ����� ID �� Ŭ���̾�Ʈ�� ����� �����带 �����Ѵ�.
		client.st_ID = id;
		return 0; // Ŭ���̾�Ʈ�� ���������� �����ϰ�, ��ȭ���� �̹� ������ ����.
	}
	public void send(String sendData) throws IOException{
	   synchronized(st_out){
	      st_out.writeUTF(sendData);
	      st_out.flush();
	   }
	}
	public synchronized void roomUser_broadcast(String sendData,String roomName) throws IOException{ //�� ���!!
		   ServerThread client;
		   GameRoom tempRoom = (GameRoom) roomNameHash.get(roomName);
		   Hashtable clients = getClients(roomName);
		   Enumeration<String> enu = tempRoom.userVector.elements();
		   while(enu.hasMoreElements()){
		      client = (ServerThread) clients.get(enu.nextElement());
		      client.send(sendData);
		   }
	}
	public synchronized void room_broadcast(String sendData) throws IOException{ //�� ���!!
		   ServerThread client;
		   Enumeration<String> enu = roomNameVector.elements();
		   while(enu.hasMoreElements()){
		      client = (ServerThread) roomNameHash.get(enu.nextElement());
		      client.send(sendData);
		   }
	}
	public synchronized String getRoomUsers(String roomName){
	      StringBuffer id = new StringBuffer();
	      String ids;
	      String name;
	      Enumeration<String> enu = roomNameHash.keys();
	      while(enu.hasMoreElements()){
	    	  name= enu.nextElement();
	    	  //GameRoom tempRoom = (GameRoom) roomNameHash.get(roomName);
	    	  //tempRoom.getUsers();
	    	  
	         id.append(enu.nextElement());
	         id.append(DELIMETER); 
	      }
	      try{
	         ids = new String(id);
	         ids = ids.substring(0, ids.length()-1); // ������ "`"�� �����Ѵ�.
	      }catch(StringIndexOutOfBoundsException e){
	         return "";
	      }
	      return ids;
	   }
	/*public String getRoomName(){
	      StringBuffer id = new StringBuffer();
	      String ids;
	      Enumeration<String> enu = roomNameVector.elements();
	      while(enu.hasMoreElements()){
	         id.append(enu.nextElement());
	         id.append(DELIMETER); 
	      }
	      try{
	         ids = new String(id);
	         ids = ids.substring(0, ids.length()-1); // ������ "`"�� �����Ѵ�.
	      }catch(StringIndexOutOfBoundsException e){
	         return "";
	      }
	      return ids;
	   }*/
	public synchronized void user_broadcast(String sendData) throws IOException{
	   ServerThread client;
	   Enumeration<String> enu = logonVector.elements();
	   while(enu.hasMoreElements()){
	      client = (ServerThread) logonHash.get(enu.nextElement());
	      client.send(sendData);
	   }
	}
	
	public synchronized Hashtable getClients(String roomName) {
		GameRoom room = (GameRoom)roomNameHash.get(roomName);
		return room.getClients();
	}
	private String getRoomNames(){
	   StringBuffer id = new StringBuffer();
	   String names;
	   String roomName;
	   Enumeration<String> enu = roomNameHash.keys();
	   while(enu.hasMoreElements()){
		   roomName = (String) enu.nextElement();
		   GameRoom tempRoom = (GameRoom) roomNameHash.get(roomName);
		   id.append(roomName);
		   //id.append(DELIMETER1);
		   //id.append(tempRoom.toString());
		   id.append(DELIMETER); 
	   }
	   try{
	      names = new String(id);
	      names = names.substring(0, names.length()-1); // ������ "`"�� �����Ѵ�.
	   }catch(StringIndexOutOfBoundsException e){
	      return "";
	   }
	   return names;
	}
}
