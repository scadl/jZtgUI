/**
 * 
 */
package jZTgUI;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

	public jZTToken[] allTokens;
	public JTree outJTree = null;

	public jZTBridge() {
		// TODO Auto-generated constructor stub
	}

	public String ztAPICurl(String reqAPI, jZTToken currentToken) {

		try {

			String req = "";
			switch (currentToken.tokenType) {
			case localToken:
				req = "curl -H X-ZT1-Auth:" + currentToken.tokenVal + " " + reqAPI;
				break;
			case controlerToken:
				req = "curl " + reqAPI;
				break;
			case ztcentralToken:
				req = "curl -X GET -H \"Authorization: token " + currentToken.tokenVal + "\" -L " + reqAPI;
				break;
			}
			System.out.println(req);

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

	private Boolean checkNodeActive(JSONArray jNodePeers, String NodeID) {

		Boolean activeNode = false;
		for (int j = 0; j < jNodePeers.size(); j++) {
			JSONObject jNodeProps = (JSONObject) jNodePeers.get(j);
			String jNodeID = (String) jNodeProps.get("address");
			if (jNodeID.startsWith(NodeID)) {
				JSONArray jPathsAll = (JSONArray) jNodeProps.get("paths");
				if (jPathsAll.size() > 0) {
					JSONObject jPathOne = (JSONObject) jPathsAll.get(0);
					activeNode = (Boolean) jPathOne.get("active");
				}
			}
		}

		return activeNode;
	}

	private void getNodesByID(String nodeID, String apiURL, DefaultMutableTreeNode nodeNetwork, String ContrID,
			String LocalID, String respNetList, jZTToken currentToken) {

		String req;
		JSONObject jArNetObj = new JSONObject();
		JSONArray jPeers = new JSONArray();

		if (!currentToken.tokenType.equals(tokenTypeEnum.ztcentralToken)) {
			
			req = apiURL + "peer";

			String respJNodeProps = ztAPICurl(req, currentToken);
			if (respJNodeProps.length() > 2) {
				try {
					jPeers = (JSONArray) new JSONParser().parse(respJNodeProps);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		try {

			switch (currentToken.tokenType) {
			case localToken:

				jArNetObj = (JSONObject) new JSONParser().parse(respNetList);

				Map jArNets = (Map) jArNetObj;
				Iterator<Map.Entry> iTr = jArNets.entrySet().iterator();
				while (iTr.hasNext()) {

					Map.Entry<String, Integer> eNt = iTr.next();
					String peerID = eNt.getKey();
					System.out.println(peerID);

					req = apiURL + "controller/network/" + nodeID + "/member/" + peerID;
					String respMembers = ztAPICurl(req, currentToken);
					JSONObject jNodeAsMemb = (JSONObject) new JSONParser().parse(respMembers);

					Boolean activeNode = false;
					Boolean authrized = false;
					String crIP = "NA.NA.NA.NA";
					if (respMembers.length() > 2) {

						activeNode = checkNodeActive(jPeers, peerID);
						
						authrized = (Boolean) jNodeAsMemb.get("authorized");

						JSONArray jNIPs = (JSONArray) jNodeAsMemb.get("ipAssignments");
						if (jNIPs.size() > 0) {
							crIP = (String) jNIPs.get(0);
						}

					} else {

					}

					String authSt = "";
					if (ContrID.startsWith(peerID)) {
						authSt = "[C]";
					} else if (authrized) {
						if (LocalID.startsWith(peerID)) {
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
							crIP + "|" + eNt.getKey() + "|" + authSt);
					nodeNetwork.add(nodeMember);

					// DefaultMutableTreeNode nodeMember = new DefaultMutableTreeNode( crIP + " (" +
					// jONodeAdv.get("name") + ") "+authSt);

				}
				break;
			case controlerToken:

				jArNetObj = (JSONObject) new JSONParser().parse(respNetList);
				
				req = apiURL + "controller/network_members&netID=" + nodeID;
				String respMembers = ztAPICurl(req, currentToken);
				JSONArray jNodeAsMemb = (JSONArray) new JSONParser().parse(respMembers);

				Map jArNets1 = (Map) jArNetObj;
				Iterator<Map.Entry> iTr1 = jArNets1.entrySet().iterator();
				while (iTr1.hasNext()) {

					Map.Entry<String, Integer> eNt = iTr1.next();
					String peerID = eNt.getKey();
					//System.out.println(peerID);				

					String crIP = "NA.NA.NA.NA";
					Boolean activeNode = checkNodeActive(jPeers, peerID);
					Boolean authorized = false;
					String authSt = "";
					String nameNode = "";
					
					if (jNodeAsMemb.size() > 0) {
						for(int j=0; j<jNodeAsMemb.size(); j++) {
							JSONObject jPeerObj = (JSONObject) jNodeAsMemb.get(j);
							if(jPeerObj.get("address").toString().startsWith(peerID)) {
								authorized = (Boolean) jPeerObj.get("authorized");
								JSONArray ipAddr = (JSONArray) jPeerObj.get("ipAssignments");
								if(ipAddr.size()>0) {
									crIP = (String) ipAddr.get(0);
								}
								nameNode = (String) jPeerObj.get("name");
							}
						}
					}
					
					if (ContrID.startsWith(peerID)) {
						authSt = "[C]";
					} else if (authorized) {
						if (LocalID.startsWith(peerID)) {
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
							crIP + " (" + nameNode + ") |"  + peerID + "|" + authSt);
					nodeNetwork.add(nodeMember);

				}

				break;
			case ztcentralToken:
				JSONArray jArrZtc = (JSONArray) new JSONParser().parse(respNetList);
				for (int j = 0; j < jArrZtc.size(); j++) {

					JSONObject jZTCNode = (JSONObject) jArrZtc.get(j);
					JSONObject jZTCConf = (JSONObject) jZTCNode.get("config");
					JSONArray jZTCIps = (JSONArray) jZTCConf.get("ipAssignments");

					long lastTime = (System.currentTimeMillis() - (long) jZTCNode.get("lastSeen")) / 60000;

					String authSt = "";

					if (LocalID.startsWith((String) jZTCNode.get("nodeId"))) {
						authSt = "[L]";
					} else if ((Boolean) jZTCConf.get("authorized")) {
						if (lastTime == 0) {
							authSt = "[A]";
						} else {
							authSt = "[N]";
						}

					} else {
						authSt = "[E]";
					}
					DefaultMutableTreeNode nodeMember = new DefaultMutableTreeNode(jZTCIps.get(0) +  
							" (" + jZTCNode.get("name") + ") |" + jZTCNode.get("nodeId") + "|" + authSt);
					nodeNetwork.add(nodeMember);

					//System.out.println("Node " + jZTCNode.get("nodeId") + " time: "
					//		+ ((System.currentTimeMillis() - (long) jZTCNode.get("lastSeen")) / 60000));
				}
				break;
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
			String req, netIP = null, Local_ID, Contr_ID;
			String localApiURL = "http://localhost:9993/";
			String dataFileName = "settings.ser";
			jZTToken currentToken = new jZTToken();

			FileInputStream fis = new FileInputStream(dataFileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			allTokens = (jZTToken[]) ois.readObject();
			ois.close();

			// https://www.geeksforgeeks.org/parse-json-java/

			// https://docs.zerotier.com/api/service/ref-v1/#tag/Node-Status/operation/node_status_readStatus
			req = localApiURL + "status"; // Local Node Status. 
			currentToken.tokenType = tokenTypeEnum.localToken;
			String statusResp = ztAPICurl(req, currentToken);
			jo = (JSONObject) new JSONParser().parse(ztAPICurl(req, currentToken));
						
			if (statusResp.length() > 2) {
				
				// https://www.codejava.net/java-se/swing/jtree-basic-tutorial-and-examples
				// https://stackoverflow.com/questions/7928839/adding-and-removing-nodes-from-a-jtree
				Local_ID = (String) jo.get("address");
				DefaultMutableTreeNode nr = new DefaultMutableTreeNode("You_ID: " + Local_ID);
				System.out.println("You_ID: " + Local_ID);
				System.out.println("Is online " + jo.get("online"));
				
				
				JSONObject joR = (JSONObject) new JSONParser().parse(statusResp);
				Contr_ID = (String) joR.get("address"); // This node ID.
				System.out.println("Controller ID: " + Contr_ID);
				

				// https://docs.zerotier.com/api/service/ref-v1/#tag/Joined-Networks
				req = localApiURL + "network"; // All the networks that this node is joined to
				jAr = (JSONArray) new JSONParser().parse(ztAPICurl(req, currentToken));

				for (int i = 0; i < jAr.size(); i++) {

					jo = (JSONObject) jAr.get(i);
					JSONArray jArrAd = (JSONArray) jo.get("assignedAddresses");
					String marker = "";
					try {
						netIP = (String) jArrAd.get(0);
						marker = "[NN]";
					} catch (Exception exception) {
						netIP = "<No IP Assigned>";
						marker = "[NAN]";
						jo.replace("name", netIP);
					}
					DefaultMutableTreeNode nodeNetwork = new DefaultMutableTreeNode(jo.get("name") + "|"+jo.get("id")+"|"+marker);
					System.out.println("This NET IP: " + netIP);

					// https://docs.zerotier.com/controller

					// req = jZTRepeaterURL+"controller/network";
					// ztAPICurl(req, false);

					String respNets;
					
					for(jZTToken savedNet : allTokens ) {
						if (savedNet.netID == netIP) {
							currentToken = savedNet;
						}
					}
					
					switch (currentToken.tokenType) {
					case localToken:
						// Try LOCAL Controller
						req = localApiURL + "controller/network/" + jo.get("id") + "/member";
						respNets = ztAPICurl(req, currentToken);
						if (respNets.length() > 0) {
							getNodesByID((String) jo.get("id"), localApiURL, nodeNetwork, Contr_ID, Local_ID, respNets,
									currentToken);
						}
						break;
					case controlerToken:
						// Try Remote Controller
						String jZTRepeaterURL = currentToken.apiURL + "?token=" + currentToken.tokenVal+"&apiRq=";
						req = jZTRepeaterURL + "controller/network/" + jo.get("id") + "/member";
						respNets = ztAPICurl(req, currentToken);
						if (respNets.length() > 0) {
							getNodesByID((String) jo.get("id"), jZTRepeaterURL, nodeNetwork, Contr_ID, Local_ID, respNets,
									currentToken);
						}
						break;
					case ztcentralToken:
						// Try ZTCentral Controller
						// curl -X GET -H "Authorization: token xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" -L
						// https://api.zerotier.com/api/v1/network/565799d8f616c30f/member
						String ZTCentralAPI = "https://api.zerotier.com/api/v1/";
						req = ZTCentralAPI + "network/" + jo.get("id") + "/member";
						respNets = ztAPICurl(req, currentToken);
						Boolean canParse = true;
						try {
							new JSONParser().parse(respNets);
						} catch (ParseException eztc) {
							// TODO: handle exception
							// eztc.printStackTrace();
							System.out.print("Malformed ZTCenral resonse got");
							canParse = false;
						}
						if (respNets.length() > 2 && canParse) {
							// getNodesByID((String) jo.get("id"), jZTRepeaterURL, nodeNetwork, Contr_ID,
							// Local_ID, respNets);
							getNodesByID("", "", nodeNetwork, Contr_ID, Local_ID, respNets, currentToken);

						}
						break;
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
				outJTree.setSelectionRow(0); // Required to make RMB clicks to work
				
				//File f;
				//FileWriter fW;
				
				//f = new File("settings.props");
				//f.createNewFile();
				
				//fW = new FileWriter("settings.props");
				//fW.write(configVals[i]);
				
				FileOutputStream fws = new FileOutputStream("settings.ser");				
				ObjectOutputStream outFSt = new ObjectOutputStream(fws);
				outFSt.writeObject(allTokens);
				outFSt.flush();
				outFSt.close();
				//	fW.close();

				// tm.insertNodeInto(node1, rt, rt.getChildCount());
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
