package SCP;


import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread 
{
   private Socket st_sock;
   private DataInputStream st_in;
   private DataOutputStream st_out;
   private StringBuffer st_buffer;
   /* 로그온된 사용자 저장 */
   private static Hashtable<String,ServerThread> logonHash; 
   private static Vector<String> logonVector;
   /* 대화방 참여자 저장 */
   private static Hashtable<String,ServerThread> roomHash; 
   private static Vector<String> roomVector;
   
   private static Hashtable<String,ServerThread> lstroomHash;
   private static Vector<String> lstroomVector;

   private static int isOpenRoom = 0; // 대화방이 개설안됨(초기값)

   private static final String SEPARATOR = "|"; // 메시지간 구분자
   private static final String DELIMETER = "`"; // 소메시지간 구분자
  
   private static Date starttime;  	// 로그온 시각

   public String st_ID; 			// ID 저장

   // 메시지 패킷 코드 및 데이터 정의

   // 클라이언트로부터 전달되는 메시지 코드
   private static final int REQ_LOGON = 1001;
   private static final int REQ_ENTERROOM = 1011;
   private static final int REQ_SENDWORDS = 1021;
   private static final int REQ_LOGOUT = 1031;
   private static final int REQ_QUITROOM = 1041;
   private static final int REQ_WHISPER = 1042;
   private static final int REQ_SENDFILE = 1043;
   private static final int REQ_CREATEROOM = 1044;
   // 클라이언트에 전송하는 메시지 코드
   private static final int YES_LOGON = 2001;
   private static final int NO_LOGON = 2002;
   private static final int YES_ENTERROOM = 2011;
   private static final int NO_ENTERROOM = 2012;
   private static final int MDY_USERIDS = 2013;
   private static final int MDY_ROOMUSERIDS = 2014;
   private static final int YES_SENDWORDS = 2021;
   private static final int NO_SENDWORDS = 2022;
   private static final int YES_LOGOUT = 2031;
   private static final int NO_LOGOUT = 2032;
   private static final int YES_QUITROOM = 2041;
   private static final int YES_WHISPER = 2042;
   private static final int NO_SENDFILE = 2043;
   private static final int YES_SENDFILE = 2044;

   // 에러 메시지 코드
   private static final int MSG_ALREADYUSER = 3001;
   private static final int MSG_SERVERFULL = 3002;
   private static final int MSG_CANNOTOPEN = 3011;

   private static final int ERR_REJECTION = 3012;

   static{	
      logonHash = new Hashtable<String,ServerThread>(ChatServer.cs_maxclient);
      logonVector = new Vector<String>(ChatServer.cs_maxclient); 
      roomHash = new Hashtable<String,ServerThread>(ChatServer.cs_maxclient);
      roomVector = new Vector<String>(ChatServer.cs_maxclient); 
      lstroomHash = new Hashtable<String,ServerThread>(ChatServer.cs_maxclient);
      lstroomVector = new Vector<String>(ChatServer.cs_maxclient);
   }

   public ServerThread(Socket sock){
      try{
         st_sock = sock;
         st_in = new DataInputStream(sock.getInputStream()); 
         st_out = new DataOutputStream(sock.getOutputStream());
         st_buffer = new StringBuffer(2048);
      }catch(IOException e){
         System.out.println(e);
      }
   }

   public void run(){
      try{
         while(true){
        	//System.out.println("here");
            String recvData = st_in.readUTF();
            StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
            int command = Integer.parseInt(st.nextToken());
            switch(command){

               // 로그온 시도 메시지 PACKET : REQ_LOGON|ID
               case REQ_LOGON:{
                  int result;
                  String id = st.nextToken(); // 클라이언트의 ID를 얻는다.
                  result = addUser(id, this);
                  st_buffer.setLength(0);
                  if(result ==0){  // 접속을 허용한 상태          	
                	 starttime = new Date();
                     st_buffer.append(YES_LOGON); 
                     					// YES_LOGON|개설시각|ID1`ID2`..
                     st_buffer.append(SEPARATOR);
                     st_buffer.append(starttime);
                     st_buffer.append(SEPARATOR);
                     String userIDs = getUsers(); //대화방 참여 사용자ID를 구한다
                     st_buffer.append(userIDs);
                     send(st_buffer.toString());
                     
                     st_buffer.setLength(0);
                     st_buffer.append(MDY_USERIDS);
                     st_buffer.append(SEPARATOR);
                     String userIds = getUsers();
                     st_buffer.append(userIds);
                     user_broadcast(st_buffer.toString());
                     
                  }else{  // 접속불가 상태
                     st_buffer.append(NO_LOGON);  // NO_LOGON|errCode
                     st_buffer.append(SEPARATOR);
                     st_buffer.append(result); // 접속불가 원인코드 전송
                     send(st_buffer.toString());
                  }
                  break;
               }

               // 대화방 개설 시도 메시지  PACKET : REQ_ENTERROOM|ID
               case REQ_ENTERROOM:{
                  st_buffer.setLength(0);
                  String id = st.nextToken(); // 클라이언트의 ID를 얻는다.
                  if(checkUserID(id) == null){

                  // NO_ENTERROOM PACKET : NO_ENTERROOM|errCode
                     st_buffer.append(NO_ENTERROOM);
                     st_buffer.append(SEPARATOR);
                     st_buffer.append(MSG_CANNOTOPEN);
                     send(st_buffer.toString());  // NO_ENTERROOM 패킷을 전송한다.
                     break;
                  }

                  roomVector.addElement(id);  // 사용자 ID 추가
                  roomHash.put(id, this); //사용자 ID 및 클라이언트와 통신할  스레드 저장

                  if(isOpenRoom == 0){  // 대화방 개설시간 설정
                     isOpenRoom = 1;
                     //starttime = new Date();
                  }

                  // YES_ENTERROOM PACKET : YES_ENTERROOM
                  st_buffer.append(YES_ENTERROOM); 
                  st_buffer.append(SEPARATOR);
                  st_buffer.append(st_ID);
                  send(st_buffer.toString()); // YES_ENTERROOM 패킷을 전송한다.

                  //MDY_USERIDS PACKET : MDY_USERIDS|id1'id2' ....
                  st_buffer.setLength(0);
                  st_buffer.append(MDY_ROOMUSERIDS);
                  st_buffer.append(SEPARATOR);
                  String userIDs = getRoomUsers(); // 대화방 참여 사용자 ID를 구한다
                  st_buffer.append(userIDs);
                  st_buffer.append(SEPARATOR);
                  broadcast(st_buffer.toString()); // MDY_USERIDS 패킷을 전송한다.
                  break;
               }

               // 대화말 전송 시도 메시지 PACKET : REQ_SENDWORDS|ID|대화말
               case REQ_SENDWORDS:{
                  st_buffer.setLength(0);
                  st_buffer.append(YES_SENDWORDS);
                  st_buffer.append(SEPARATOR);
                  String id = st.nextToken(); // 전송한 사용자의 ID를 구한다.
                  st_buffer.append(id);
                  st_buffer.append(SEPARATOR);
                  try{
                     String data = st.nextToken(); // 대화말을 구한다.
                     st_buffer.append(data);
                  }catch(NoSuchElementException e){}
                  broadcast(st_buffer.toString()); // YES_SENDWORDS 패킷  전송
                  break;
               }

               // LOGOUT 전송 시도 메시지  
               // PACKET : YES_LOGOUT|탈퇴자ID
               case REQ_LOGOUT:{
            	  String id = st.nextToken();
            	  logonVector.remove(id);
            	  logonHash.remove(id);
            	  if(roomVector.contains(id)) {
            		  roomVector.remove(id);
                	  roomHash.remove(id);
                	  
                	  st_buffer.setLength(0);
                	  st_buffer.append(MDY_ROOMUSERIDS);
                      st_buffer.append(SEPARATOR);
                      String roomIDs = getRoomUsers(); // 대화방 참여 사용자 ID를 구한다
                      st_buffer.append(roomIDs);
                      st_buffer.append(SEPARATOR);
                      broadcast(st_buffer.toString());
            	  }         	  
            	
            	  st_buffer.setLength(0);
            	  st_buffer.append(MDY_USERIDS);
                  st_buffer.append(SEPARATOR);
                  String userIDs = getUsers(); // 대화방 참여 사용자 ID를 구한다
                  st_buffer.append(userIDs);
                  st_buffer.append(SEPARATOR);
                  user_broadcast(st_buffer.toString());
                  
            	  st_buffer.setLength(0);
            	  st_buffer.append(YES_LOGOUT);
            	  st_buffer.append(SEPARATOR);
            	  st_buffer.append(id);
            	  st_buffer.append(SEPARATOR);
            	  send(st_buffer.toString());
            	  
            	  
                            	  
                  break;
               } 
               case REQ_SENDFILE :{
            	   String send_id = st.nextToken();
            	   String receive_id = st.nextToken();
            	   //System.out.println("send_id ="+send_id);
            	   //System.out.println("receive_id ="+receive_id);
            	   st_buffer.setLength(0);
            	   st_buffer.append(REQ_SENDFILE);
            	   st_buffer.append(SEPARATOR);
            	   st_buffer.append(send_id);
            	   st_buffer.append(SEPARATOR);
            	   ServerThread client;
            	   client=(ServerThread)roomHash.get(receive_id);
            	   client.send(st_buffer.toString());
            	   break;
            	               	   
               }
               //보내는 사람 + 받는사람 + 메세지 
               case REQ_WHISPER :{ 
            	   String sender = st.nextToken();
            	   String receiver = st.nextToken();
            	   String msg = st.nextToken();
            	   
            	   st_buffer.setLength(0);
            	   st_buffer.append(YES_WHISPER);
            	   st_buffer.append(SEPARATOR);
            	   st_buffer.append(sender);
            	   st_buffer.append(SEPARATOR);
            	   st_buffer.append(receiver);
            	   st_buffer.append(SEPARATOR);
            	   st_buffer.append(msg);
            	   st_buffer.append(SEPARATOR);
            	   
            	   
            	   ServerThread client;
            	   client=(ServerThread)roomHash.get(receiver);
            	   client.send(st_buffer.toString());
            	   break;
               }

               // 방 입장전의 LOGOUT 전송 시도 메시지 PACKET : YES_QUITROOM
               case REQ_QUITROOM:{
            	  String id = st.nextToken();
            	  roomHash.remove(id);
            	  roomVector.remove(id);
            	  st_buffer.setLength(0);
            	  st_buffer.append(YES_QUITROOM);
            	  st_buffer.append(SEPARATOR);
            	  st_buffer.append(id);
            	  st_buffer.append(SEPARATOR);
            	  send(st_buffer.toString());
            	  
            	  st_buffer.setLength(0);
            	  st_buffer.append(MDY_ROOMUSERIDS);
            	  st_buffer.append(SEPARATOR);
            	  String ids= getRoomUsers();
            	  st_buffer.append(ids);
            	  st_buffer.append(SEPARATOR);
                  broadcast(st_buffer.toString());
     	  
                  break;
               }
               case NO_SENDFILE :{//요청보냈던 상대 id + 요청 보낸 사람 id 전송됌
            	   String id= st.nextToken();//요청 보냈던 상대id
            	   String idTo = st.nextToken();
            	   
            	   ServerThread client;
            	   client=(ServerThread)roomHash.get(idTo);
            	   
            	   st_buffer.setLength(0); //요청 거절메세지 + 수락거부메세지 + 요청보냈던 사람 id를 보냄
            	   st_buffer.append(NO_SENDFILE);
            	   st_buffer.append(SEPARATOR);
            	   st_buffer.append(ERR_REJECTION);
            	   st_buffer.append(SEPARATOR);
            	   st_buffer.append(id);
            	   
            	   client.send(st_buffer.toString());
            	   break;
            	   
               }
               
               case YES_SENDFILE :{//상대방 아이디 + 나의 id +포트번호를 받음  //보냈던 사람이 수락이니깐 포트번호를 알려줘서 연결할수 있게 해줌
            	   String id = st.nextToken();
            	   String idTo = st.nextToken();
            	   String hostaddr = st.nextToken();
            	   String port = st.nextToken();
            	   
            	   ServerThread client;
            	   client=(ServerThread)roomHash.get(idTo);
            	   
            	   st_buffer.setLength(0);
            	   st_buffer.append(YES_SENDFILE);
            	   st_buffer.append(SEPARATOR);
            	   st_buffer.append(id);
            	   st_buffer.append(SEPARATOR);
            	   st_buffer.append(hostaddr);
            	   st_buffer.append(SEPARATOR);
            	   st_buffer.append(port);
            	   
            	   client.send(st_buffer.toString());
            	   break;
            	   
            	   
            	   
               }

            } // switch 종료

            Thread.sleep(100);
         } //while 종료

      }catch(NullPointerException e){ // 로그아웃시 st_in이 이 예외를 발생하므로
      }catch(InterruptedException e){
      }catch(IOException e){
      }
   }

   // 자원을 해제한다.

   public void release(){}

   /* 해쉬 테이블에 접속을 요청한 클라이언트의 ID 및 전송을 담당하는 스레드를 등록.
          즉, 해쉬 테이블은 대화를 하는 클라이언트의 리스트를 포함. */
    private static synchronized int addUser(String id, ServerThread client){
      if(checkUserID(id) != null){
         return MSG_ALREADYUSER;
      }  
      if(logonHash.size() >= ChatServer.cs_maxclient){
         return MSG_SERVERFULL;
      }
      logonVector.addElement(id);  // 사용자 ID 추가
      logonHash.put(id, client); // 사용자 ID 및 클라이언트와 통신할 스레드를 저장한다.
      client.st_ID = id;
      return 0; // 클라이언트와 성공적으로 접속하고, 대화방이 이미 개설된 상태.
   }

   /* 접속을 요청한 사용자의 ID와 일치하는 ID가 이미 사용되는 지를 조사한다.
           반환값이 null이라면 요구한 ID로 대화방 입장이 가능함. */
   private static ServerThread checkUserID(String id){
      ServerThread alreadyClient = null;
      alreadyClient = (ServerThread) logonHash.get(id);
      return alreadyClient;
   }

   // 로그온에 참여한 사용자 ID를 구한다.
   private String getUsers(){
      StringBuffer id = new StringBuffer();
      String ids;
      Enumeration<String> enu = logonVector.elements();
      while(enu.hasMoreElements()){
         id.append(enu.nextElement());
         id.append(DELIMETER); 
      }
      try{
         ids = new String(id);  // 문자열로 변환한다.
         ids = ids.substring(0, ids.length()-1); // 마지막 "`"를 삭제한다.
      }catch(StringIndexOutOfBoundsException e){
         return "";
      }
      return ids;
   }

   // 대화방에 참여한 사용자 ID를 구한다.

   private String getRoomUsers(){
      StringBuffer id = new StringBuffer();
      String ids;
      Enumeration<String> enu = roomVector.elements();
      while(enu.hasMoreElements()){
         id.append(enu.nextElement());
         id.append(DELIMETER); 
      }
      try{
         ids = new String(id);
         ids = ids.substring(0, ids.length()-1); // 마지막 "`"를 삭제한다.
      }catch(StringIndexOutOfBoundsException e){
         return "";
      }
      return ids;
   }

   // 대화방에 참여한 모든 사용자(브로드케스팅)에게 데이터를 전송한다.
   public synchronized void broadcast(String sendData) throws IOException{
      ServerThread client;
      Enumeration<String> enu = roomVector.elements();
      while(enu.hasMoreElements()){
         client = (ServerThread) roomHash.get(enu.nextElement());
         client.send(sendData);
      }
   }
   
   public synchronized void user_broadcast(String sendData) throws IOException{
	      ServerThread client;
	      Enumeration<String> enu = logonVector.elements();
	      while(enu.hasMoreElements()){
	         client = (ServerThread) logonHash.get(enu.nextElement());
	         client.send(sendData);
	      }
	   }

   // 데이터를 전송한다.
   public void send(String sendData) throws IOException{
      synchronized(st_out){
         st_out.writeUTF(sendData);
         st_out.flush();
      }
   }
}   

