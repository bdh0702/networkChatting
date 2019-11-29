package Chapter12;
import java.io.*;
import java.net.*;
public class Practice_2 {
	public static void main(String args[]) {
		System.out.print("URL을 입력해주세요");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String url = br.readLine();
			URL u = new URL(url);
			URLConnection uc = u.openConnection();
			uc.connect();
			InputStream is = uc.getInputStream();
			BufferedReader br2 = new BufferedReader(new InputStreamReader(is));
			String type = uc.getContentType();
			String line ="헤더타입 : "+type+"\n";
			StringBuffer buffer = new StringBuffer();
			buffer.append(line);
			File f = new File("받은파일\\");
			f.mkdir();

			while((line = br2.readLine()) != null) {
				File f2 = new File(uc.getContentType());
				FileWriter fw = new FileWriter("C:\\Users\\COMSE\\eclipse-workspace\\NP2\\받은파일\\"+f2);
				fw.write(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
