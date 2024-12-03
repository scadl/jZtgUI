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

	public String ztAPICurl(String reqAPI, Boolean localToken) {

		try {

			String req = "";
			if (localToken) {
				req = "curl -H X-ZT1-Auth:" + lToken + " " + reqAPI;
			} else {
				req = "curl " + reqAPI;
			}

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

	public void readZTData() {

		try {

			Object obj;
			JSONObject jo;
			JSONArray jAr;
			String req;
			String netIP;

			// https://www.geeksforgeeks.org/parse-json-java/

			req = "http://localhost:9993/status";
			jo = (JSONObject) new JSONParser().parse(ztAPICurl(req, true));

			// https://www.codejava.net/java-se/swing/jtree-basic-tutorial-and-examples
			// https://stackoverflow.com/questions/7928839/adding-and-removing-nodes-from-a-jtree
			DefaultMutableTreeNode nr = new DefaultMutableTreeNode("You_ID: " + jo.get("address"));
			System.out.println("Is online" + jo.get("online"));

			// https://docs.zerotier.com/api/service/ref-v1/#tag/Joined-Networks
			req = "http://localhost:9993/network";
			jAr = (JSONArray) new JSONParser().parse(ztAPICurl(req, true));

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

				// req = "https://scadsdnd.net/jZTRep/?apiRq=controller/network";
				// ztAPICurl(req, false);

				req = "https://scadsdnd.net/jZTRep/?apiRq=controller/network/" + jo.get("id") + "/member";
				String respNets = ztAPICurl(req, false);
				if (respNets.length() > 0) {

					JSONObject jArNetObj = (JSONObject) new JSONParser().parse(respNets);
					Map jArNets = (Map) jArNetObj;
					Iterator<Map.Entry> iTr = jArNets.entrySet().iterator();
					while (iTr.hasNext()) {

						Map.Entry eNt = iTr.next();
						// System.out.println(eNt.getKey());

						req = "https://scadsdnd.net/jZTRep/?apiRq=controller/network/" + jo.get("id") + "/member/"
								+ eNt.getKey();
						JSONObject jNodeAsMemb = (JSONObject) new JSONParser().parse(ztAPICurl(req, false));

						req = "https://scadsdnd.net/jZTRep/?apiRq=peer/" + eNt.getKey();
						String respJNodeProps = ztAPICurl(req, false);
						Boolean activeNode = false;
						if (respJNodeProps.length() > 2) {
							JSONObject jNodeAsPeer = (JSONObject) new JSONParser().parse(respJNodeProps);
							JSONArray jNodePropArr = (JSONArray) jNodeAsPeer.get("paths");
							if(jNodePropArr.size()>0) {
								JSONObject jNodePeerProps = (JSONObject) jNodePropArr.get(0);
								activeNode = (Boolean) jNodePeerProps.get("active");
							}
						}
						
						JSONArray jNIPs = (JSONArray) jNodeAsMemb.get("ipAssignments");
						String crIP = "";
						if (jNIPs.size() > 0) {
							crIP = (String) jNIPs.get(0);
						} else {
							crIP = "NA.NA.NA.NA";
						}
						String authSt = "";
						if ((Boolean) jNodeAsMemb.get("authorized")) {
							if (activeNode) {
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
