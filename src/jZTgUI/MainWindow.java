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
import java.awt.Panel;

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
		mainFrameWindow.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.GROWING_BUTTON_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("301px:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.LINE_GAP_ROWSPEC,}));
		
		Box horizontalBox = Box.createHorizontalBox();
		mainFrameWindow.getContentPane().add(horizontalBox, "3, 2");
		
		JLabel lblLabelNetworks = new JLabel("ZT Netwoks");
		horizontalBox.add(lblLabelNetworks);
		
		Panel panel_3 = new Panel();
		horizontalBox.add(panel_3);
		
		JScrollPane scrollPane = new JScrollPane();
		mainFrameWindow.getContentPane().add(scrollPane, "3, 4, fill, fill");

		JTree jTree = new JTree();
		jTree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("-LocalPC-|N0N0N0N0N0|[L]") {
				{
					
				}
			}
		));
		scrollPane.setViewportView(jTree);
		jTree.setEditable(true);
		ToolTipManager.sharedInstance().registerComponent(jTree);
		
		JButton btnNewButton = new JButton("LL");
		btnNewButton.addActionListener(new ActionListener() {
			Shell sh = new Shell();
			public void actionPerformed(ActionEvent e) {
				try {
					// https://www.demo2s.com/java/eclipse-swt-messagebox-tutorial-with-examples.html
					String lToken = jztBr.readTxtFile("C:\\ProgramData\\ZeroTier\\One\\authtoken.secret");
					MessageBox mb = new MessageBox(sh, SWT.ICON_INFORMATION | SWT.OK);
					mb.setText("Load OK");
					mb.setMessage("Your local token:\n"+ lToken);
					int retCode = mb.open();
					if(retCode == SWT.OK) {
						jztBr.readZTData(jTree, tokenTypeEnum.localToken);
					}
				} catch (Exception e2) {
					// TODO: handle exception
					MessageBox mb = new MessageBox(sh, SWT.ICON_WARNING | SWT.OK);
					mb.setText("Load ERROR");
					mb.setMessage("It looks, you don\\'t have ZT on this device.\n"+e2.toString());
					e2.printStackTrace();
					int retCode = mb.open();
				} 
			}
		});
		btnNewButton.setToolTipText("Try to load local token");
		horizontalBox.add(btnNewButton);
		
		// https://www.demo2s.com/java/java-swing-jmenu-menu-component.html
		JPopupMenu popupMenu = new JPopupMenu();
		//addPopup(jTree, popupMenu);
		popupMenu.setLabel("");
		
		JMenuItem mntmIDMenuItem = new JMenuItem("<NO ID>");
		popupMenu.add(mntmIDMenuItem);
		popupMenu.addSeparator();
		
		JMenu mntmTypeMenu = new JMenu("Set Type");
		popupMenu.add(mntmTypeMenu);
		
		JRadioButtonMenuItem[] mntmTypeItems = {
				new JRadioButtonMenuItem("Controller"),
				new JRadioButtonMenuItem("ZT Central"),
				new JRadioButtonMenuItem("Local")
		};
		for(int i=0; i<mntmTypeItems.length; i++) {
			mntmTypeMenu.add(mntmTypeItems[i]);
			mntmTypeItems[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JRadioButtonMenuItem jrm = (JRadioButtonMenuItem) e.getSource();
					for(int j=0; j<mntmTypeItems.length; j++) {
						mntmTypeItems[j].setSelected(false);
					}
					jrm.setSelected(true);
				}
			});
		}
		
		

		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// https://docs.oracle.com/javase/tutorial/uiswing/components/tooltip.html
		// https://docs.oracle.com/javase/7/docs/api/javax/swing/JTree.html#getToolTipText(java.awt.event.MouseEvent)
		// https://stackoverflow.com/questions/517704/right-click-context-menu-for-java-jtree
		// https://docs.oracle.com/javase/tutorial/uiswing/components/tree.html
		
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
						mntmIDMenuItem.setText("ID: "+nodeInfo[1]);
						//JMenuItem nMI = new JMenuItem("ID:"+nodeInfo[1]);
						//popupMenu.add(nMI);
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					} catch (Exception e2) {
						// TODO: handle exception
						//mntmNewMenuItem.setText("<NO ID>");
						e2.printStackTrace();
					}
					
					System.out.println(nodeInfo.toString());
					
				}
			}
		};
		jTree.addMouseListener(ma);
		
		ZTNodeCellRender ztcr = new ZTNodeCellRender();
		jTree.setCellRenderer(ztcr);
		jTree.setSelectionRow(0); // Required to make RMB clicks to work
		

		Box horizontalBox1 = Box.createHorizontalBox();
		mainFrameWindow.getContentPane().add(horizontalBox1, "3, 6");

		JLabel lblLabelToken = new JLabel("You Token");
		horizontalBox1.add(lblLabelToken);

		JPanel panel = new JPanel();
		horizontalBox1.add(panel);

		textFieldToken = new JTextField();
		horizontalBox1.add(textFieldToken);
		textFieldToken.setColumns(10);
		textFieldToken.setText("");

		Box horizontalBox2 = Box.createHorizontalBox();
		mainFrameWindow.getContentPane().add(horizontalBox2, "3, 9");

		JLabel lblLabelRept = new JLabel("jZT Repeater");
		horizontalBox2.add(lblLabelRept);

		JPanel panel_1 = new JPanel();
		horizontalBox2.add(panel_1);

		textFieldReptURL = new JTextField();
		horizontalBox2.add(textFieldReptURL);

		Box horizontalBox3 = Box.createHorizontalBox();
		mainFrameWindow.getContentPane().add(horizontalBox3, "3, 11");

		JLabel lblControllerToken = new JLabel("Controller Token");
		horizontalBox3.add(lblControllerToken);

		JPanel panel_2 = new JPanel();
		horizontalBox3.add(panel_2);

		textFieldControlerToken = new JTextField();
		horizontalBox3.add(textFieldControlerToken);

		Box horizontalBox3_1 = Box.createHorizontalBox();
		mainFrameWindow.getContentPane().add(horizontalBox3_1, "3, 13");

		JLabel lblZtcentralToken = new JLabel("ZTCentral Token");
		horizontalBox3_1.add(lblZtcentralToken);

		JPanel panel_2_1 = new JPanel();
		horizontalBox3_1.add(panel_2_1);

		textFieldZTCToken = new JTextField();
		horizontalBox3_1.add(textFieldZTCToken);

		JButton btnReadButton = new JButton("Read");
		mainFrameWindow.getContentPane().add(btnReadButton, "3, 15");
		btnReadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				
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
