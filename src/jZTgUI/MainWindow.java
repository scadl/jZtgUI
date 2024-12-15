package jZTgUI;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

import javax.swing.JFrame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.Box;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.JobAttributes;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.awt.Dialog.ModalExclusionType;
import com.jgoodies.forms.layout.FormSpecs;

import jZTgUI.jZTBridge;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow {

	private JFrame mainFrameWindow;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		// https://stackoverflow.com/questions/13139283/jframe-theme-and-appearance
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.mainFrameWindow.setVisible(true);
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

	// https://www.w3schools.com/java/java_arraylist.asp
	private String LocalID = "", LocalToken = "";
	private ArrayList<jZTToken> myZTNetworks = new ArrayList<jZTToken>();
	private jZTBridge jztBr = new jZTBridge();
	private String dataFileName = "jZtgUI.ser";

	private void LoadLocalInfo(JTree jTree) {
		try {
			// https://www.demo2s.com/java/eclipse-swt-messagebox-tutorial-with-examples.html
			// https://www.theserverside.com/blog/Coffee-Talk-Java-News-Stories-and-Opinions/Java-user-input-with-a-Swing-JOptionPane-example
			// https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/JOptionPane.html#showInputDialog(java.lang.Object)
			
			String lToken = readTxtFile("C:\\ProgramData\\ZeroTier\\One\\authtoken.secret");
			LocalToken = lToken;
			LocalID = jztBr.readLocalInfo(jTree, lToken);
			//JOptionPane.showMessageDialog(null, "Your local token:\n" + lToken, "Load OK", JOptionPane.INFORMATION_MESSAGE);
			jTree.setSelectionRow(0);
			ArrayList<jZTToken> loadedTokens = loadLastState();
			
			if (loadedTokens!=null) {
				
				// https://stackoverflow.com/questions/30011227/looping-all-jtree-nodes-and-get-the-objects-in-each-node
				// https://docs.oracle.com/javase/8/docs/api/javax/swing/tree/DefaultMutableTreeNode.html
				DefaultMutableTreeNode jRoot = (DefaultMutableTreeNode) jTree.getModel().getRoot();
				//DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode("Your ID: "+LocalID+"|"+LocalID+"|[L]");
				Enumeration jTrEnum = jRoot.children();
				while (jTrEnum.hasMoreElements()) {

					DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) jTrEnum.nextElement();
					String nodeID = thisNode.toString().split("\\|")[1];

					for (jZTToken thisToken : loadedTokens) {
						System.out.println(nodeID + " " + thisToken.netID);
						if (thisToken.netID.startsWith(nodeID)) {
							System.out.println("Got Net Match");
							//DefaultMutableTreeNode nnn  = new DefaultMutableTreeNode(thisNode.toString());
							loadNetworkMembersByID(thisToken.tokenType, thisNode, nodeID, thisToken.tokenVal,
									thisToken.apiURL);
							//newRoot.add(nnn);

						}
					}
				}
				
				DefaultTreeModel tm = (DefaultTreeModel) jTree.getModel();
				//tm.setRoot(newRoot);
				tm.reload();
				
				//myZTNetworks = loadedTokens;

			}

		} catch (Exception e2) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(null, "It looks, you do not have ZT on this device.\n" + e2.toString(),
					"Load ERROR", JOptionPane.ERROR_MESSAGE);
			e2.printStackTrace();
		}
	}

	private void addCustomNetwork(JTree jTree) {
		DefaultTreeModel tm = (DefaultTreeModel) jTree.getModel();
		DefaultMutableTreeNode tmr = (DefaultMutableTreeNode) tm.getRoot();
		String nnID = JOptionPane.showInputDialog("Please, set your new network ID (ZT fromat)");
		if (nnID != "" && nnID != null) {
			DefaultMutableTreeNode tmn = new DefaultMutableTreeNode("New Network|" + nnID + "|[NN]");
			tm.insertNodeInto(tmn, tmr, tmr.getChildCount());
			tm.reload();
		}
	}

	private void loadNetworkMembersByID(tokenTypeEnum tokenType, DefaultMutableTreeNode rootNode,
			String... networkProps) {

		// https://stackoverflow.com/questions/965690/how-do-i-use-optional-parameters-in-java
		
		String networkID = networkProps[0];
		switch (tokenType) {

		case controlerToken:
			String controlerURL = "";
			String controlerToken = "";
			if (networkProps.length < 2) {
				controlerToken = JOptionPane.showInputDialog("Plaese, set the controller token");
			} else {
				controlerToken = networkProps[1];
			}
			System.out.print(controlerToken);

			if (networkProps.length < 3) {
				if (controlerToken != "" && controlerToken != null) {
					controlerURL = JOptionPane.showInputDialog("A controller also requires API URL");
				}
			} else {
				controlerURL = networkProps[2];
			}
			if (controlerToken != "" && controlerURL != "" && controlerToken != null && controlerURL != null) {
				jZTToken currentTokenNN1 = new jZTToken(networkID, tokenTypeEnum.controlerToken, controlerToken,
						controlerURL);
				String remoteID = jztBr.readRemoteInfo(currentTokenNN1);
				jztBr.readZTData(rootNode, currentTokenNN1, LocalID, remoteID);
				if (!myZTNetworks.contains(currentTokenNN1)) {
					myZTNetworks.add(currentTokenNN1);
				}
			}
			break;

		case localToken:
			if (LocalToken != "" && LocalToken != null) {
				jZTToken currentTokenNN3 = new jZTToken(networkID, tokenTypeEnum.localToken, LocalToken, "");
				jztBr.readZTData(rootNode, currentTokenNN3, LocalID, LocalID);
				if (!myZTNetworks.contains(currentTokenNN3)) {
					myZTNetworks.add(currentTokenNN3);
				}
			}
			break;

		case ztcentralToken:
			String ztcToken = "";
			if(networkProps.length < 2) {
				ztcToken = JOptionPane.showInputDialog("Plaese, set your ZeroTier Central API Key");
			} else {
				ztcToken = networkProps[1];
			}
			if (ztcToken != "") {
				jZTToken currentTokenNN2 = new jZTToken(networkID, tokenTypeEnum.ztcentralToken, ztcToken, "");
				jztBr.readZTData(rootNode, currentTokenNN2, LocalID, "");
				if (myZTNetworks.contains(currentTokenNN2)) {
					myZTNetworks.add(currentTokenNN2);
				}
			}
			break;

		default:
			System.out.println("Token Type not recognized!");
			break;
		}
	};

	private void networkTypeSelcted(JTree jTree, JRadioButtonMenuItem[] mntmTypeItems, JRadioButtonMenuItem jrm) {

		for (int j = 0; j < mntmTypeItems.length; j++) {
			mntmTypeItems[j].setSelected(false);
		}

		jrm.setSelected(true);

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
		String[] nodeInfo = node.getUserObject().toString().split("\\|");
		String networkID = nodeInfo[1];

		if (jrm.equals(mntmTypeItems[0])) {
			loadNetworkMembersByID(tokenTypeEnum.controlerToken, node, networkID);
		} else if (jrm.equals(mntmTypeItems[1])) {
			loadNetworkMembersByID(tokenTypeEnum.ztcentralToken, node, networkID);
		} else if (jrm.equals(mntmTypeItems[2])) {
			loadNetworkMembersByID(tokenTypeEnum.localToken, node, networkID);
		}
	}

	private ArrayList<jZTToken> loadLastState() {
		File f = new File(dataFileName);
		ArrayList<jZTToken> lastZTnetworks;
		try {

			FileInputStream fis = new FileInputStream(dataFileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			lastZTnetworks = (ArrayList<jZTToken>) ois.readObject();
			ois.close();

			f.delete();
			f.createNewFile();

			return lastZTnetworks;
		} catch (FileNotFoundException e2) {
			// TODO: handle exception
			try {
				f.createNewFile();
			} catch (IOException e21) {
				// TODO Auto-generated catch block
				e21.printStackTrace();
			}
			return null;
		} catch (Exception e3) {
			// TODO: handle exception
			e3.printStackTrace();
			return null;
		}
	}

	private void saveCurrentState() {
		try {
			FileOutputStream fws = new FileOutputStream(dataFileName);
			ObjectOutputStream outFSt = new ObjectOutputStream(fws);
			outFSt.writeObject(myZTNetworks);
			outFSt.flush();
			outFSt.close();
		} catch (Exception e1) {
			// TODO: handle exception
			e1.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		mainFrameWindow = new JFrame();

		mainFrameWindow.setTitle("jZTgUI\r\n");
		mainFrameWindow.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		mainFrameWindow.setBounds(100, 100, 351, 450);
		mainFrameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrameWindow.getContentPane()
				.setLayout(new FormLayout(
						new ColumnSpec[] { FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
								FormSpecs.GROWING_BUTTON_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
								FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, },
						new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("301px:grow"),
								FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC, FormSpecs.LINE_GAP_ROWSPEC, }));

		Box horizontalBox = Box.createHorizontalBox();
		mainFrameWindow.getContentPane().add(horizontalBox, "3, 2");

		JLabel lblLabelNetworks = new JLabel("ZT Netwoks");
		horizontalBox.add(lblLabelNetworks);

		Panel panel_3 = new Panel();
		horizontalBox.add(panel_3);

		JScrollPane scrollPane = new JScrollPane();
		mainFrameWindow.getContentPane().add(scrollPane, "3, 4, fill, fill");

		JTree jTree = new JTree();
		jTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("-LocalPC-|N0N0N0N0N0|[L]") {
		}));
		scrollPane.setViewportView(jTree);
		jTree.setEditable(true);
		ToolTipManager.sharedInstance().registerComponent(jTree);

		JButton btnNewButton = new JButton("LL");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoadLocalInfo(jTree);
			}
		});
		btnNewButton.setToolTipText("Try to load local token");
		horizontalBox.add(btnNewButton);

		JButton btnReadButton = new JButton("+");
		horizontalBox.add(btnReadButton);
		btnReadButton.setToolTipText("Add network manualy");
		btnReadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addCustomNetwork(jTree);
			}
		});

		// https://www.demo2s.com/java/java-swing-jmenu-menu-component.html
		JPopupMenu popupMenu = new JPopupMenu();
		// addPopup(jTree, popupMenu);
		popupMenu.setLabel("");

		JMenuItem mntmIDMenuItem = new JMenuItem("<NO ID>");
		popupMenu.add(mntmIDMenuItem);
		popupMenu.addSeparator();

		JMenu mntmTypeMenu = new JMenu("Set Type");
		JRadioButtonMenuItem[] mntmTypeItems = { new JRadioButtonMenuItem("Controller"),
				new JRadioButtonMenuItem("ZT Central"), new JRadioButtonMenuItem("Local") };
		for (int i = 0; i < mntmTypeItems.length; i++) {
			mntmTypeMenu.add(mntmTypeItems[i]);
			mntmTypeItems[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JRadioButtonMenuItem jrm = (JRadioButtonMenuItem) e.getSource();
					networkTypeSelcted(jTree, mntmTypeItems, jrm);
					DefaultTreeModel tmr = (DefaultTreeModel) jTree.getModel();
					tmr.reload();
				}
			});
		}
		popupMenu.add(mntmTypeMenu);

		JMenuItem mntmDelMenuItem = new JMenuItem("Delete");
		popupMenu.add(mntmDelMenuItem);
		mntmDelMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DefaultTreeModel tm = (DefaultTreeModel) jTree.getModel();
				tm.removeNodeFromParent((DefaultMutableTreeNode) jTree.getLastSelectedPathComponent());
			}
		});

		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// https://docs.oracle.com/javase/tutorial/uiswing/components/tooltip.html
		// https://docs.oracle.com/javase/7/docs/api/javax/swing/JTree.html#getToolTipText(java.awt.event.MouseEvent)
		// https://stackoverflow.com/questions/517704/right-click-context-menu-for-java-jtree
		// https://docs.oracle.com/javase/tutorial/uiswing/components/tree.html

		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int row = jTree.getClosestRowForLocation(e.getX(), e.getY());
					jTree.setSelectionRow(row);
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
					if (node == null) {
						return;
					}
					String[] nodeInfo = node.getUserObject().toString().split("\\|");
					try {
						mntmIDMenuItem.setText("ID: " + nodeInfo[1]);
						// JMenuItem nMI = new JMenuItem("ID:"+nodeInfo[1]);
						// popupMenu.add(nMI);
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					} catch (Exception e2) {
						// TODO: handle exception
						// mntmNewMenuItem.setText("<NO ID>");
						e2.printStackTrace();
					}

					System.out.println(nodeInfo[1]);

				}
			}
		};
		jTree.addMouseListener(ma);

		ZTNodeCellRender ztcr = new ZTNodeCellRender();
		jTree.setCellRenderer(ztcr);
		jTree.setSelectionRow(0);

		mainFrameWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveCurrentState();
			}
		});

	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
