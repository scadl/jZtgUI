/**
 * 
 */
package jZTgUI;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

	private String dataFileName = "settings.ser";
	private String localApiURL = "http://localhost:9993/";
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
			System.out.println(">>> RQ: " + req);

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
			System.out.println(">>> RESP: " + resp);
			return resp;

		} catch (Exception ep) {
			// TODO Auto-generated catch block
			ep.printStackTrace();
			return "";
		}

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
					// System.out.println(peerID);

					String crIP = "NA.NA.NA.NA";
					Boolean activeNode = checkNodeActive(jPeers, peerID);
					Boolean authorized = false;
					String authSt = "";
					String nameNode = "";

					if (jNodeAsMemb.size() > 0) {
						for (int j = 0; j < jNodeAsMemb.size(); j++) {
							JSONObject jPeerObj = (JSONObject) jNodeAsMemb.get(j);
							if (jPeerObj.get("address").toString().startsWith(peerID)) {
								authorized = (Boolean) jPeerObj.get("authorized");
								JSONArray ipAddr = (JSONArray) jPeerObj.get("ipAssignments");
								if (ipAddr.size() > 0) {
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
							crIP + " (" + nameNode + ") |" + peerID + "|" + authSt);
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
					DefaultMutableTreeNode nodeMember = new DefaultMutableTreeNode(jZTCIps.get(0) + " ("
							+ jZTCNode.get("name") + ") |" + jZTCNode.get("nodeId") + "|" + authSt);
					nodeNetwork.add(nodeMember);

					// System.out.println("Node " + jZTCNode.get("nodeId") + " time: "
					// + ((System.currentTimeMillis() - (long) jZTCNode.get("lastSeen")) / 60000));
				}
				break;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadTokens() {
		try {
			FileInputStream fis = new FileInputStream(dataFileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			allTokens = (jZTToken[]) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e2) {
			// TODO: handle exception
			File f;
			f = new File(dataFileName);
			try {
				f.createNewFile();
			} catch (IOException e21) {
				// TODO Auto-generated catch block
				e21.printStackTrace();
			}
			loadTokens();
		} catch (Exception e3) {
			// TODO: handle exception
			e3.printStackTrace();
		}

	}

	private void saveTokens() {
		try {
			FileOutputStream fws = new FileOutputStream(dataFileName);
			ObjectOutputStream outFSt = new ObjectOutputStream(fws);
			outFSt.writeObject(allTokens);
			outFSt.flush();
			outFSt.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public String readRemoteInfo(jZTToken remToken) {

		try {
			String req = remToken.apiURL + "?token=" + remToken.tokenVal + "&apiRq=" + "status"; // Remote Node Status.
			String statusResp = ztAPICurl(req, remToken);
			JSONObject jo = (JSONObject) new JSONParser().parse(statusResp);

			System.out.println("This Controller ID: " + jo.get("address"));

			return (String) jo.get("address");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "";
		}

	}

	public String readLocalInfo(JTree jTree, String localToken) {

		String netIP;
		try {

			// https://www.geeksforgeeks.org/parse-json-java/
			// https://docs.zerotier.com/api/service/ref-v1/#tag/Node-Status/operation/node_status_readStatus
			String req = localApiURL + "status"; // Local Node Status.
			jZTToken currentToken = new jZTToken("", tokenTypeEnum.localToken, localToken, "");
			String statusResp = ztAPICurl(req, currentToken);
			JSONObject jo = (JSONObject) new JSONParser().parse(statusResp);
			
			String LocalID = (String) jo.get("address");

			System.out.println("This PC ID: " + LocalID);

			if (statusResp.length() > 2) {
				// https://docs.zerotier.com/api/service/ref-v1/#tag/Joined-Networks
				req = localApiURL + "network"; // All the networks that this node is joined to
				JSONArray jAr = (JSONArray) new JSONParser().parse(ztAPICurl(req, currentToken));

				for (int i = 0; i < jAr.size(); i++) {
					jo = (JSONObject) jAr.get(i);
					JSONArray jArrAd = (JSONArray) jo.get("assignedAddresses");
					String marker = "";
					try {
						netIP = (String) jArrAd.get(0);
						marker = "[NN]";
					} catch (Exception exception) {
						netIP = "<No Name and IP>";
						marker = "[NAN]";
						jo.replace("name", netIP);
					}
					DefaultMutableTreeNode nodeNetwork = new DefaultMutableTreeNode(
							jo.get("name") + "|" + jo.get("id") + "|" + marker);

					// Try to read network members from local controller
					jZTToken currentTokenNN = new jZTToken(LocalID, tokenTypeEnum.localToken, localToken,"");
					readZTData(nodeNetwork, currentTokenNN, LocalID, LocalID);

					DefaultTreeModel tmr = (DefaultTreeModel) jTree.getModel();
					DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tmr.getRoot();
					tmr.insertNodeInto(nodeNetwork, rootNode, rootNode.getChildCount());
					tmr.reload();

					System.out.println("This NET IP: " + netIP);
				}
			}
			return LocalID;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "";
		}

	}

	public void readZTData(DefaultMutableTreeNode rootNode, jZTToken currentToken, String Local_ID,
			String Contr_ID) {

		String req, respNets;
		String ID = currentToken.netID;
		try {

			switch (currentToken.tokenType) {
			case localToken:
				// Try LOCAL Controller
				req = localApiURL + "controller/network/" + ID + "/member";
				respNets = ztAPICurl(req, currentToken);
				if (respNets.length() > 0) {
					getNodesByID((String) ID, localApiURL, rootNode, Contr_ID, Local_ID, respNets, currentToken);
				}
				break;
			case controlerToken:
				// Try Remote Controller
				// https://scadsdnd.net/jZTRep/?token=xxxxxxxxxxxxxxxx&apiRq=status
				String jZTRepeaterURL = currentToken.apiURL + "?token=" + currentToken.tokenVal + "&apiRq=";
				req = jZTRepeaterURL + "controller/network/" + ID + "/member";
				respNets = ztAPICurl(req, currentToken);
				if (respNets.length() > 0) {
					getNodesByID((String) ID, jZTRepeaterURL, rootNode, Contr_ID, Local_ID, respNets, currentToken);
				}
				break;
			case ztcentralToken:
				// Try ZTCentral Controller
				// curl -X GET -H "Authorization: token xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" -L
				// https://api.zerotier.com/api/v1/network/565799d8f616c30f/member
				String ZTCentralAPI = "https://api.zerotier.com/api/v1/";
				req = ZTCentralAPI + "network/" + ID + "/member";
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
					getNodesByID("", "", rootNode, Contr_ID, Local_ID, respNets, currentToken);

				}
				break;
			}
			// System.out.println(respNets.length());

			// nr.add(nodeNetwork);
			// jo.get("id")

			//DefaultTreeModel tmr = (DefaultTreeModel) jTree.getModel();
			//tmr.reload();

			// File f;
			// FileWriter fW;

			// f = new File("settings.props");
			// f.createNewFile();

			// fW = new FileWriter("settings.props");
			// fW.write(configVals[i]);

			// fW.close();

			saveTokens();

			// tm.insertNodeInto(node1, rt, rt.getChildCount());

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
