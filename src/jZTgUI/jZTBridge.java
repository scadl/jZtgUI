/**
 * 
 */
package jZTgUI;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 */
public class jZTBridge {

	private String lToken = "";
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

	public String ztAPICurl(String reqAPI, Integer typeToken) {

		try {

			String ctrToken = "";
			String ztToken = "";
			
			String req = "";
			if (typeToken == 1) {
				req = "curl -H X-ZT1-Auth:" + lToken + " " + reqAPI;
			} else if (typeToken == 2) {
				req = "curl " + reqAPI + "&token="+ctrToken;
			} else if (typeToken == 3) {
				req = "curl -X GET -H \"Authorization: token "+ztToken+"\" -L " + reqAPI;
			}
			System.out.println(reqAPI);

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
				// sb.append(System.lineSeparator());
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

	private void getNodesByID(String nodeID, String apiURL, DefaultMutableTreeNode nodeNetwork, String ContrID,
			String LocalID, String respNetList) {

		String req;

		JSONObject jArNetObj;
		try {
			jArNetObj = (JSONObject) new JSONParser().parse(respNetList);

			Map jArNets = (Map) jArNetObj;
			Iterator<Map.Entry> iTr = jArNets.entrySet().iterator();
			while (iTr.hasNext()) {

				Map.Entry<String, Integer> eNt = iTr.next();
				System.out.println(eNt.getKey());

				req = apiURL + "controller/network/" + nodeID + "/member/" + eNt.getKey();
				JSONObject jNodeAsMemb = (JSONObject) new JSONParser().parse(ztAPICurl(req, 2));

				req = apiURL + "peer/" + eNt.getKey();
				String respJNodeProps = ztAPICurl(req, 2);
				Boolean activeNode = false;
				String crIP = "NA.NA.NA.NA";
				if (respJNodeProps.length() > 2) {
					JSONObject jNodeAsPeer = (JSONObject) new JSONParser().parse(respJNodeProps);
					JSONArray jNodePropArr = (JSONArray) jNodeAsPeer.get("paths");
					if (jNodePropArr.size() > 0) {
						JSONObject jNodePeerProps = (JSONObject) jNodePropArr.get(0);
						activeNode = (Boolean) jNodePeerProps.get("active");
					}
					
					JSONArray jNIPs = (JSONArray) jNodeAsMemb.get("ipAssignments");
					if (jNIPs.size() > 0) {
						crIP = (String) jNIPs.get(0);
					} 
				} else {
					
				}
				
				
				String authSt = "";
				if (ContrID.startsWith(eNt.getKey())) {
					authSt = "[C]";
				} else if (activeNode) {
					if (LocalID.startsWith(eNt.getKey())) {
						authSt = "[L]";
					} else if (activeNode) {
						authSt = "[A]";
					} else {
						authSt = "[N]";
					}
				} else {
					authSt = "[E]";
				}
				DefaultMutableTreeNode nodeMember = new DefaultMutableTreeNode(
						crIP + " (" + eNt.getKey() + ") " + authSt);
				nodeNetwork.add(nodeMember);

				

				// DefaultMutableTreeNode nodeMember = new DefaultMutableTreeNode( crIP + " (" +
				// jONodeAdv.get("name") + ") "+authSt);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readZTData() {

		try {

			Object obj;
			JSONObject jo;
			JSONArray jAr;
			String req, netIP, Local_ID, Contr_ID;
			String localApiURL = "http://localhost:9993/";
			String jZTRepeaterURL = "https://scadsdnd.net/jZTRep/" + "?apiRq=";
			String ZTCentralAPI = "https://api.zerotier.com/api/v1/";

			// https://www.geeksforgeeks.org/parse-json-java/

			req = localApiURL+"status";
			jo = (JSONObject) new JSONParser().parse(ztAPICurl(req, 1));

			// https://www.codejava.net/java-se/swing/jtree-basic-tutorial-and-examples
			// https://stackoverflow.com/questions/7928839/adding-and-removing-nodes-from-a-jtree
			Local_ID = (String) jo.get("address");
			DefaultMutableTreeNode nr = new DefaultMutableTreeNode("You_ID: " + Local_ID);
			System.out.println("You_ID: " + Local_ID);
			System.out.println("Is online " + jo.get("online"));

			req = jZTRepeaterURL + "status";
			JSONObject joR = (JSONObject) new JSONParser().parse(ztAPICurl(req, 1));
			Contr_ID = (String) joR.get("address");
			System.out.println("Controller ID: " + Contr_ID);

			// https://docs.zerotier.com/api/service/ref-v1/#tag/Joined-Networks
			req = localApiURL+"network";
			jAr = (JSONArray) new JSONParser().parse(ztAPICurl(req, 1));

			for (int i = 0; i < jAr.size(); i++) {

				jo = (JSONObject) jAr.get(i);
				JSONArray jArrAd = (JSONArray) jo.get("assignedAddresses");
				try {
					netIP = (String) jArrAd.get(0);
				} catch (Exception exception) {
					netIP = "(No IP Assigned)[NAN]";
					jo.replace("name", netIP);
				}
				DefaultMutableTreeNode nodeNetwork = new DefaultMutableTreeNode(jo.get("name"));
				System.out.println("This NET IP: " + netIP);

				// https://docs.zerotier.com/controller

				// req = jZTRepeaterURL+"controller/network";
				// ztAPICurl(req, false);

				String respNets;
				
				// Try LOCAL Controller
				req = localApiURL + "controller/network/" + jo.get("id") + "/member";
				respNets = ztAPICurl(req, 1);
				if (respNets.length() > 0) {	
					getNodesByID((String) jo.get("id"), localApiURL, nodeNetwork, Contr_ID, Local_ID, respNets);
				}
				// Try Remote Controller
				req = jZTRepeaterURL + "controller/network/" + jo.get("id") + "/member";
				respNets = ztAPICurl(req, 2);
				if (respNets.length() > 0) {					
					getNodesByID((String) jo.get("id"), jZTRepeaterURL, nodeNetwork, Contr_ID, Local_ID, respNets);
				} 
				// Try ZTCentral Controller
				// curl -X GET -H "Authorization: token xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" -L https://api.zerotier.com/api/v1/network/565799d8f616c30f/member
				req = jZTRepeaterURL + "controller/network/" + jo.get("id") + "/member";
				//respNets = ztAPICurl(req, false);
				if (respNets.length() > 0) {					
					//getNodesByID((String) jo.get("id"), jZTRepeaterURL, nodeNetwork, Contr_ID, Local_ID, respNets);
				} 
				// System.out.println(respNets.length());

				nr.add(nodeNetwork);

				// jo.get("id")
			}

			DefaultTreeModel tm = (DefaultTreeModel) outJTree.getModel();
			DefaultMutableTreeNode rt = (DefaultMutableTreeNode) tm.getRoot();
			tm.setRoot(nr);

			ZTNodeCellRender ztcr = new ZTNodeCellRender();
			outJTree.setCellRenderer(ztcr);

			// tm.insertNodeInto(node1, rt, rt.getChildCount());

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
