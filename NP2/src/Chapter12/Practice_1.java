package Chapter12;

import java.awt.*;
import java.io.*;
import java.net.*;


import java.awt.event.*;

public class Practice_1 extends Frame{
	Label lb_1,lb_2,lb_3;
	Button btn_ok;
	TextField tf_1;
	TextArea ta_1,ta_2;
	public Practice_1() {
		super("예제1");
		lb_1 = new Label("URL");
		tf_1 = new TextField(60);
		Panel p = new Panel(new BorderLayout());
		p.add(lb_1,BorderLayout.WEST);
		p.add(tf_1,BorderLayout.EAST);
		
		lb_2 = new Label("헤더");
		ta_1 = new TextArea();
		Panel p2 = new Panel(new BorderLayout());
		p2.add(lb_2,BorderLayout.NORTH);
		p2.add(ta_1,BorderLayout.CENTER);
		
		lb_3 = new Label("바디");
		ta_2 = new TextArea();
		btn_ok = new Button("확인");
		btn_ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String str = tf_1.getText();
				try {
					URL u = new URL(str);
					HttpURLConnection uc = (HttpURLConnection)u.openConnection();
					uc.connect();
					int code;
					String msg;
					code = uc.getResponseCode();
					msg = uc.getResponseMessage();
					ta_1.append("응답 라인: HTTP/1.1 "+code+" "+msg+"\r\n");
					int i=0;
					while(uc.getHeaderField(i)!=null) {
						ta_1.append(uc.getHeaderFieldKey(i)+"\r\n");		
						i++;
					}
					InputStream is = uc.getInputStream();
					BufferedReader isr = new BufferedReader(new InputStreamReader(is));
					String str2;
					while((str2=isr.readLine()) != null) {
						ta_2.append(str2+"\r\n");
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		Panel p3 = new Panel(new BorderLayout());
		p3.add(lb_3,BorderLayout.NORTH);
		p3.add(ta_2,BorderLayout.CENTER);
		p3.add(btn_ok,BorderLayout.SOUTH);
		add(p,BorderLayout.NORTH);
		add(p2,BorderLayout.CENTER);
		add(p3,BorderLayout.SOUTH);
		addWindowListener(new WinListener());
	    setSize(600,600);
	    setVisible(true);
	}
	

	class WinListener extends WindowAdapter {
	      public void windowClosing(WindowEvent e){        
	               System.exit(0);
	         
	      }
	   }
 
	public static void main(String args[]) {
		Practice_1 p = new Practice_1();
	}
}
