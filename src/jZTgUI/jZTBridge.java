/**
 * 
 */
package jZTgUI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 */
public class jZTBridge {
	
	private String lToken="";
	private JTree outJTree = null;
	
	public jZTBridge() {
		// TODO Auto-generated constructor stub
	}
	
	public void setLocalToken(String token) {
		lToken = token;
	}
	
	public void setOutJTree(JTree jTrCont) {
		outJTree = jTrCont;
	}

	public String ztAPICurl(String reqAPI) {	

		try {
			
			String req = "curl -H X-ZT1-Auth:" + lToken + " "+reqAPI;

			// https://www.baeldung.com/java-curl
			// https://stackoverflow.com/questions/2586975/how-to-use-curl-in-java
			String[] ReqS = req.split(" ");
			Process proc = Runtime.getRuntime().exec(ReqS);
			InputStreamReader isr = new InputStreamReader(proc.getInputStream(), "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line, resp = "";
			while ((line = br.readLine()) != null) {
				resp += line;
			}
			System.out.println(resp);
			return resp;

		} catch (Exception ep) {
			// TODO Auto-generated catch block
			ep.printStackTrace();
			return "";
		}

	}

	public String readTxtFile(String path) {
		BufferedReader br = null;
		String everything = "";
		try {
			br = new BufferedReader(new FileReader(path));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				//sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return everything;
	}
	
	public void readZTData() {
		
		try {

			Object obj;
			JSONObject jo;
			JSONArray jAr;
			String req;
			String netIP;

			// https://www.geeksforgeeks.org/parse-json-java/
			
			req = "http://localhost:9993/status";
			jo = (JSONObject) new JSONParser().parse(ztAPICurl(req));
			
			// https://www.codejava.net/java-se/swing/jtree-basic-tutorial-and-examples
			// https://stackoverflow.com/questions/7928839/adding-and-removing-nodes-from-a-jtree
			DefaultMutableTreeNode nr = new DefaultMutableTreeNode("You_ID: " + jo.get("address"));
			System.out.println("Is online" + jo.get("online"));

			// https://docs.zerotier.com/api/service/ref-v1/#tag/Joined-Networks
			req = "http://localhost:9993/network";
			jAr = (JSONArray) new JSONParser().parse(ztAPICurl(req));

			for (int i = 0; i < jAr.size(); i++) {				
				
				jo = (JSONObject) jAr.get(i);
				JSONArray jArrAd = (JSONArray) jo.get("assignedAddresses");
				try {
					netIP = (String) jArrAd.get(0);
				} catch (Exception exception) {
					netIP = "No IP Assigned";
				}
				DefaultMutableTreeNode nodeNetwork = new DefaultMutableTreeNode(netIP + " (" + jo.get("name") + ")");
				
				// https://docs.zerotier.com/controller
				
				req = "http://localhost:9993/controller/network";
				ztAPICurl(req);
				
				req = "http://localhost:9993/controller/network/"+jo.get("id")+"/member";
				String respNets = ztAPICurl(req);				
				if (respNets.length() > 0) {
					
					JSONObject jArNetObj = (JSONObject) new JSONParser().parse(respNets);
					Map jArNets = (Map) jArNetObj;
					Iterator<Map.Entry> iTr = jArNets.entrySet().iterator();
					while (iTr.hasNext()) {
						
						Map.Entry eNt = iTr.next();						
						System.out.println(eNt.getKey());
						
						req = "http://localhost:9993/controller/network/"+jo.get("id")+"/member/"+eNt.getKey();
						JSONObject jONodeAdv = (JSONObject) new JSONParser().parse(ztAPICurl(req));
						
						DefaultMutableTreeNode nodeMember = new DefaultMutableTreeNode(eNt.getKey() + " "+jONodeAdv.get("authorized"));
						
						nodeNetwork.add(nodeMember);
						
					}
					
					
				}
				System.out.println(respNets.length());				
				
				nr.add(nodeNetwork);
				
				// jo.get("id")
			}

			DefaultTreeModel tm = (DefaultTreeModel) outJTree.getModel();
			DefaultMutableTreeNode rt = (DefaultMutableTreeNode) tm.getRoot();
			tm.setRoot(nr);

			// tm.insertNodeInto(node1, rt, rt.getChildCount());

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}

