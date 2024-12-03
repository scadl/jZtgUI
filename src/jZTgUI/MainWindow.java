package jZTgUI;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import java.awt.Dialog.ModalExclusionType;
import com.jgoodies.forms.layout.FormSpecs;

import jZTgUI.jZTBridge;

public class MainWindow {

	private JFrame mainFrameWindow;
	private JTextField textFieldToken;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		mainFrameWindow.getContentPane()
				.setLayout(new FormLayout(
						new ColumnSpec[] { FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
								FormSpecs.GROWING_BUTTON_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
								FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, },
						new RowSpec[] { RowSpec.decode("2px"), FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("14px"), FormSpecs.RELATED_GAP_ROWSPEC,
								RowSpec.decode("301px:grow"), FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC, FormSpecs.LINE_GAP_ROWSPEC, }));

		JLabel lblLabelNetworks = new JLabel("ZT Netwoks");
		mainFrameWindow.getContentPane().add(lblLabelNetworks, "3, 4");

		Box horizontalBox = Box.createHorizontalBox();
		mainFrameWindow.getContentPane().add(horizontalBox, "3, 8");

		JLabel lblLabelToken = new JLabel("You Token");
		horizontalBox.add(lblLabelToken);

		JTree jTree = new JTree();
		jTree.setEditable(true);
		mainFrameWindow.getContentPane().add(jTree, "3, 6, fill, fill");
		
		textFieldToken = new JTextField();
		horizontalBox.add(textFieldToken);
		textFieldToken.setColumns(10);
		textFieldToken.setText(jztBr.readTxtFile("C:\\ProgramData\\ZeroTier\\One\\authtoken.secret"));
		
				JButton btnReadButton = new JButton("Read");
				horizontalBox.add(btnReadButton);
				btnReadButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						jztBr.setLocalToken(textFieldToken.getText());
						jztBr.setOutJTree(jTree);
						jztBr.readZTData();

					}
				});
		btnReadButton.doClick();

	}

}
