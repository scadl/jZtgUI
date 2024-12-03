/**
 * 
 */
package jZTgUI;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * 
 */

// https://stackoverflow.com/questions/10111849/how-to-change-style-color-font-of-a-single-jtree-node
// https://stackoverflow.com/questions/20691946/set-icon-to-each-node-in-jtree

public class ZTNodeCellRender extends DefaultTreeCellRenderer {
@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		// TODO Auto-generated method stub
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		ImageIcon imIcnR = new ImageIcon(MainWindow.class.getResource("/bullet_red.png"));
		ImageIcon imIcnG = new ImageIcon(MainWindow.class.getResource("/bullet_green.png"));			
		ImageIcon imIcnY = new ImageIcon(MainWindow.class.getResource("/bullet_yellow.png"));
		ImageIcon imIcnGy = new ImageIcon(MainWindow.class.getResource("/bullet_white.png"));
		ImageIcon imIcnNet = new ImageIcon(MainWindow.class.getResource("/network_adapter.png"));
		ImageIcon imIcnPC = new ImageIcon(MainWindow.class.getResource("/terminal.png"));
	
		/*
		String node = (String) ((DefaultMutableTreeNode) value).getUserObject();
		if(leaf && node.endsWith("[NA]")) {
			setIcon(imIcnY);
			setForeground(new Color(255,0,0));
		} else if(leaf) {
			setIcon(imIcnG);
		} else if(row == 0) {
			setIcon(imIcnPC);
		} else {
			setIcon(imIcnNet);
		}
		*/
		DefaultMutableTreeNode nodeN = (DefaultMutableTreeNode) value;
		if(tree.getModel().getRoot().equals(nodeN)) {
			setIcon(imIcnPC);
		} else if (nodeN.getChildCount()>0) {
			setIcon(imIcnNet);
		} else {
			if(nodeN.getUserObject().toString().endsWith("[E]")) {
				setIcon(imIcnGy);	
			} else if(nodeN.getUserObject().toString().endsWith("[N]")) {
				setIcon(imIcnR);	
			} else {
				setIcon(imIcnG);
			}
		}
		
		return this;
	}
}
