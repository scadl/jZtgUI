package jZTgUI;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import javax.swing.JFrame;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.Box;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow {

	private JFrame frame;
	private JTextField textField;

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
								
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}
	
	public String ztAPICurl(String req) {
		
		try {
						
			// https://www.baeldung.com/java-curl
			// https://stackoverflow.com/questions/2586975/how-to-use-curl-in-java
			String[] ReqS = req.split(" ");
			Process proc = Runtime.getRuntime().exec(ReqS);
			InputStreamReader isr = new InputStreamReader(proc.getInputStream(), "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line, resp = "";
			while((line=br.readLine())!=null) {
				resp += line;
			}
			System.out.println(resp);			
			return resp;
			
		} catch (IOException ep) {
			// TODO Auto-generated catch block
			ep.printStackTrace();
			return "";
		} 
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 250, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("234px"),},
			new RowSpec[] {
				RowSpec.decode("14px"),
				RowSpec.decode("26px"),
				RowSpec.decode("23px"),
				RowSpec.decode("2px"),
				RowSpec.decode("14px"),
				RowSpec.decode("122px"),}));
		
		JLabel lblNewLabel = new JLabel("You local token");
		frame.getContentPane().add(lblNewLabel, "1, 1, left, center");
		
		textField = new JTextField();
		frame.getContentPane().add(textField, "1, 2, fill, center");
		textField.setColumns(10);		
		
		JTree jTree = new JTree();
		jTree.setEditable(true);
		frame.getContentPane().add(jTree, "1, 6, fill, fill");
		
		JButton btnNewButton = new JButton("Read the data");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
						
				
				// https://www.geeksforgeeks.org/parse-json-java/
				Object obj;
				try {
					
					JSONObject jo;
					JSONArray jAr;
					String req;
					
					req = "curl -H X-ZT1-Auth:"+textField.getText()+" http://localhost:9993/status";
					obj = new JSONParser().parse(ztAPICurl(req));
					jo = (JSONObject) obj;						
				
				
					// https://www.codejava.net/java-se/swing/jtree-basic-tutorial-and-examples
					// https://stackoverflow.com/questions/7928839/adding-and-removing-nodes-from-a-jtree
				
					DefaultMutableTreeNode nr = new DefaultMutableTreeNode("ID:" + jo.get("address"));
					System.out.println("Is online" + jo.get("online"));
					
					// https://docs.zerotier.com/api/service/ref-v1/#tag/Joined-Networks
					req = "curl -H X-ZT1-Auth:"+textField.getText()+" http://localhost:9993/network";
					obj = new JSONParser().parse(ztAPICurl(req));
					jAr = (JSONArray) obj;
					
					//Iterator iTr = jAr.iterator();
					for(int i=0; i<jAr.size(); i++) {
						jo = (JSONObject) jAr.get(i);
						DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(jo.get("name")+" - "+jo.get("nwid"));
						nr.add(node1);
					}
					
					DefaultTreeModel tm = (DefaultTreeModel) jTree.getModel();					
					DefaultMutableTreeNode rt = (DefaultMutableTreeNode) tm.getRoot();
					tm.setRoot(nr);
				
					//tm.insertNodeInto(node1, rt, rt.getChildCount());
				
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
				
			}
		});
		frame.getContentPane().add(btnNewButton, "1, 3, fill, center");

		
		JSeparator separator = new JSeparator();
		frame.getContentPane().add(separator, "1, 4, center, center");
		
		JLabel lblNewLabel_1 = new JLabel("Yor netwoks");
		frame.getContentPane().add(lblNewLabel_1, "1, 5, left, center");
		

	}

}
