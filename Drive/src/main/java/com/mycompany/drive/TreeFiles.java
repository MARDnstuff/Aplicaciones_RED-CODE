package com.mycompany.drive;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author reyma
 */
class TreeFiles extends JFrame {
    
    public TreeFiles (DefaultMutableTreeNode arbol){
        //setTitle("MyFiles");
        //setBounds(350,300,600,600);
        JTree obj = new JTree (arbol);
        //TreeView arbolView = new TreeView(arbol);
        //add(arbolView);
    Container arbolView = getContentPane();
       arbolView.add(new JScrollPane(obj));
        
    }
    
}

class TreeView extends JPanel {
    public TreeView ( JTree obj){
        setLayout(new BorderLayout());
        add(obj, BorderLayout.NORTH);
    }
}
