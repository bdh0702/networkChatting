package Chapter9;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

public class Dictionary_Server extends Frame {
      TextArea display;
      Label info;
      List<ServerThread5> list;
      public  ServerThread5 SThread;
      public Dictionary_Server() {
            super("서버");
            info = new Label();
            add(info, BorderLayout.CENTER);
            display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
            display.setEditable(false);
            add(display, BorderLayout.SOUTH);
            addWindowListener(new WinListener());
            setSize(300,250);
            setVisible(true);
         }
      public void runServer() {
            ServerSocket server;
            Socket sock;
            //ServerThread SThread;
            try {
               list = new ArrayList<ServerThread5>();
               server = new ServerSocket(5002, 100);
               try {
                  while(true) {
                     sock = server.accept();             
                     SThread = new ServerThread5(this, sock, display);
                     SThread.start();
                  }
               } catch(IOException ioe) {
                  server.close();
                  ioe.printStackTrace();
               }
            } catch(IOException ioe) {
               ioe.printStackTrace();
            }
         }
      public static void main(String args[]) {
    	  	Dictionary_Server s = new Dictionary_Server();
    	  	s.runServer();
         }
      class WinListener extends WindowAdapter {
            public void windowClosing(WindowEvent e) {
               System.exit(0);
            }
         }
}
class ServerThread5 extends Thread{
      Socket sock;
      BufferedWriter output;
      BufferedReader input;
      TextArea display;
      Dictionary_Server cs;
      String clientdata;
      String serverdata;
    public ServerThread5( Dictionary_Server c, Socket s, TextArea ta) {
         sock = s;
         display = ta;
         cs = c;
         try {
            input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
         } catch(IOException ioe) {
            ioe.printStackTrace();
         }
      }
    public void run() {
            cs.list.add(this);
            try {
               while((clientdata = input.readLine()) != null) {
            	  FileInputStream f = new FileInputStream("English.txt");
                  BufferedReader br = new BufferedReader(new InputStreamReader(f,"UTF-8"));
                  while((serverdata = br.readLine()) != null) {
                     StringTokenizer st = new StringTokenizer(serverdata,":");
                     String s1 =st.nextToken();
                     String s2 = st.nextToken();                
                     if(clientdata.equals(s1)){                       
                          output.write(s1 + " : " +s2 + "\r\n");
                          output.flush();  
                          display.append(s1 + ":" + s2 +"  전송완료\r\n");
                          break;
                     }
                  }
                } 
              }catch(IOException e) {
            	  e.printStackTrace();
              }
            try{
               sock.close();
            }catch(IOException ea){
               ea.printStackTrace();
            }
           
    }
}