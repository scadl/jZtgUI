package jZTgUI;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JFrame;
import org.json.*;

public class MainWindow {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// https://docs.zerotier.com/api/service/ref-v1/
				String token = "";
				String req = "curl -H X-ZT1-Auth:"+token+" http://localhost:9993/status";
				String[] ReqS = req.split(" ");
				try {
					
					
					// https://www.baeldung.com/java-curl
					// https://stackoverflow.com/questions/2586975/how-to-use-curl-in-java
					Process proc = Runtime.getRuntime().exec(ReqS);
					InputStreamReader isr = new InputStreamReader(proc.getInputStream(), "UTF-8");
					BufferedReader br = new BufferedReader(isr);
					String line, resp = "";
					while((line=br.readLine())!=null) {
						resp += line;
					}
					System.out.print("Res "+ resp);
					
					// https://www.geeksforgeeks.org/parse-json-java/
					//JSONObject jo = new JSONObject("");
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
