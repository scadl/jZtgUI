package jZTgUI;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

import javax.swing.JFrame;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.Box;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridLayout;
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
import javax.swing.JMenuItem;

public class MainWindow {

	private JFrame mainFrameWindow;
	private JTextField textFieldToken;
	private JTextField textFieldReptURL;
	private JTextField textFieldControlerToken;
	private JTextField textFieldZTCToken;

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

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		jZTBridge jztBr = new jZTBridge();

		mainFrameWindow = new JFrame();
		mainFrameWindow.setTitle("jZTgUI\r\n");
		mainFrameWindow.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		mainFrameWindow.setBounds(100, 100, 351, 450);
		mainFrameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrameWindow.getContentPane().setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
						FormSpecs.GROWING_BUTTON_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("14px"), FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("301px:grow"), FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.LINE_GAP_ROWSPEC, }));

		JLabel lblLabelNetworks = new JLabel("ZT Netwoks");
		mainFrameWindow.getContentPane().add(lblLabelNetworks, "3, 2");

		JScrollPane scrollPane = new JScrollPane();
		mainFrameWindow.getContentPane().add(scrollPane, "3, 4, fill, fill");

		JTree jTree = new JTree();
		jTree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("-LocalPC-") {
				{
					DefaultMutableTreeNode node_1;
					node_1 = new DefaultMutableTreeNode("-Network_1-");
						node_1.add(new DefaultMutableTreeNode("-Node_1[PC]-"));
						node_1.add(new DefaultMutableTreeNode("-Node_2[PC]-"));
					add(node_1);
				}
			}
		));
		scrollPane.setViewportView(jTree);
		jTree.setEditable(true);
		ToolTipManager.sharedInstance().registerComponent(jTree);
		
		JPopupMenu popupMenu = new JPopupMenu();
		//addPopup(jTree, popupMenu);
		popupMenu.setLabel("");
		
		JMenuItem mntmNewMenuItem = new JMenuItem("<NO ID>");
		popupMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Set Type");
		popupMenu.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_1a = new JMenuItem("Controller");
		JMenuItem mntmNewMenuItem_1b = new JMenuItem("ZT Central");
		JMenuItem mntmNewMenuItem_1c = new JMenuItem("Local");
		mntmNewMenuItem_1.add(mntmNewMenuItem_1a);
		mntmNewMenuItem_1.add(mntmNewMenuItem_1b);
		mntmNewMenuItem_1.add(mntmNewMenuItem_1c);
		
		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// https://docs.oracle.com/javase/tutorial/uiswing/components/tooltip.html
		// https://docs.oracle.com/javase/7/docs/api/javax/swing/JTree.html#getToolTipText(java.awt.event.MouseEvent)
		// https://stackoverflow.com/questions/517704/right-click-context-menu-for-java-jtree
		
		
		MouseAdapter ma  = new MouseAdapter() {			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
					if (node == null) {
						return;
					}
					int row = jTree.getClosestRowForLocation(e.getX(), e.getY());
					jTree.setSelectionRow(row);
					String[] nodeInfo = node.getUserObject().toString().split("\\|");
					try {
						mntmNewMenuItem.setText("ID: "+nodeInfo[1]);
						//JMenuItem nMI = new JMenuItem("ID:"+nodeInfo[1]);
						//popupMenu.add(nMI);
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					} catch (Exception e2) {
						// TODO: handle exception
						//mntmNewMenuItem.setText("<NO ID>");
						e2.printStackTrace();
					}
					
					System.out.println(nodeInfo);
					
				}
			}
		};
		jTree.addMouseListener(ma);
		

		Box horizontalBox1 = Box.createHorizontalBox();
		mainFrameWindow.getContentPane().add(horizontalBox1, "3, 6");

		JLabel lblLabelToken = new JLabel("You Token");
		horizontalBox1.add(lblLabelToken);

		JPanel panel = new JPanel();
		horizontalBox1.add(panel);

		textFieldToken = new JTextField();
		horizontalBox1.add(textFieldToken);
		textFieldToken.setColumns(10);
		textFieldToken.setText(jztBr.readTxtFile("C:\\ProgramData\\ZeroTier\\One\\authtoken.secret"));

		Box horizontalBox2 = Box.createHorizontalBox();
		mainFrameWindow.getContentPane().add(horizontalBox2, "3, 9");

		JLabel lblLabelRept = new JLabel("jZT Repeater");
		horizontalBox2.add(lblLabelRept);

		JPanel panel_1 = new JPanel();
		horizontalBox2.add(panel_1);

		textFieldReptURL = new JTextField();
		textFieldReptURL.setText(jztBr.readTxtFile("controller.api"));
		textFieldReptURL.setColumns(10);
		horizontalBox2.add(textFieldReptURL);

		Box horizontalBox3 = Box.createHorizontalBox();
		mainFrameWindow.getContentPane().add(horizontalBox3, "3, 11");

		JLabel lblControllerToken = new JLabel("Controller Token");
		horizontalBox3.add(lblControllerToken);

		JPanel panel_2 = new JPanel();
		horizontalBox3.add(panel_2);

		textFieldControlerToken = new JTextField();
		textFieldControlerToken.setColumns(10);
		textFieldControlerToken.setText(jztBr.readTxtFile("controller.token"));
		horizontalBox3.add(textFieldControlerToken);

		Box horizontalBox3_1 = Box.createHorizontalBox();
		mainFrameWindow.getContentPane().add(horizontalBox3_1, "3, 13");

		JLabel lblZtcentralToken = new JLabel("ZTCentral Token");
		horizontalBox3_1.add(lblZtcentralToken);

		JPanel panel_2_1 = new JPanel();
		horizontalBox3_1.add(panel_2_1);

		textFieldZTCToken = new JTextField();
		textFieldZTCToken.setColumns(10);
		textFieldZTCToken.setText(jztBr.readTxtFile("ztcentral.token"));
		horizontalBox3_1.add(textFieldZTCToken);

		JButton btnReadButton = new JButton("Read");
		mainFrameWindow.getContentPane().add(btnReadButton, "3, 15");
		btnReadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				File f;
				FileWriter fW;
				String[] configFiles = {"controller.token","ztcentral.token","controller.api"};
				String[] configVals = {textFieldControlerToken.getText(),textFieldZTCToken.getText(),textFieldReptURL.getText()};
				try {
					
					for(int i=0; i< configFiles.length; i++) {
						
						f = new File(configFiles[i]);
						f.createNewFile();
						
						fW = new FileWriter(configFiles[i]);
						fW.write(configVals[i]);
						fW.close();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
				
				jztBr.allTokens = new jZTTokens();
				jztBr.allTokens.localToken = textFieldToken.getText();
				jztBr.allTokens.controllerToken = textFieldControlerToken.getText();
				jztBr.allTokens.ztCentralToken = textFieldZTCToken.getText();
				jztBr.allTokens.controllerApi = textFieldReptURL.getText();
				jztBr.outJTree = jTree;
				jztBr.readZTData();

			}
		});
		btnReadButton.doClick();

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
