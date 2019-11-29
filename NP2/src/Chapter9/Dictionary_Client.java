package Chapter9;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;


public class Dictionary_Client extends Frame {
   Label mlbl,wlbl;
   TextField wtext;
   TextArea display;
   BufferedWriter output;
   BufferedReader input;
   Socket client;
   String serverdata;
   StringBuffer clientdata = new StringBuffer(2048);
   public Dictionary_Client() {
	   	 super("클라이언트");
	   	 mlbl = new Label("영어사전");
         add(mlbl, BorderLayout.NORTH);
         display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
         display.setEditable(false);
         add(display, BorderLayout.CENTER);
        
         Panel pword = new Panel(new BorderLayout());
         wlbl = new Label("찾을 단어");
         wtext = new TextField(25); //전송할 데이터를 입력하는 필드
         wtext.addActionListener(new ActionListener() {
        	 public void actionPerformed(ActionEvent e) {
        		 try {
        			 String temp =wtext.getText();
        			 clientdata.setLength(0);        
        			 clientdata.append(temp);
        			 output.write(clientdata.toString()+"\r\n");
        			 output.flush();
        		 }catch(Exception e1) {
        			 e1.printStackTrace();
        		 }
            
        	 } 
         });
         pword.add(wlbl, BorderLayout.WEST);
         pword.add(wtext, BorderLayout.EAST);
         add(pword,BorderLayout.SOUTH);
         addWindowListener(new WinListener());
         setSize(300,250);
         setVisible(true);
   }
   public void runClient() {
         try {
            client = new Socket(InetAddress.getLocalHost(), 5002);
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            while(true) {
                  serverdata = input.readLine();
                  display.append(serverdata+"\r\n"); 
            }
         }catch(IOException e) {
            e.printStackTrace();
         }
      }
   class WinListener extends WindowAdapter {
         public void windowClosing(WindowEvent e){           
                  System.exit(0);                                                          
         }
      }
    public static void main(String args[]) {
    	 Dictionary_Client c = new Dictionary_Client();
         c.runClient();
       
      }
   
}