package org.freehep.jas.extension.aida.adapter;

import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.ref.histogram.FixedAxis;
import hep.aida.ref.histogram.Histogram1D;
import hep.aida.ref.tree.Tree;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.freehep.application.studio.Studio;
import org.freehep.jas.extension.aida.AIDAPlugin;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapter;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;

/**
 * The FTreeNodeAdapter for an AIDA mount Point.
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class MountPointAdapter extends DefaultFTreeNodeAdapter {

    private AIDAPlugin plugin;
    private Studio app;
    private Commands commands;
    
    public MountPointAdapter(AIDAPlugin plugin, Studio app) {
        super(100);
        this.plugin = plugin;
        this.app = app;
        commands = new Commands();
    }
    
    @Override
    public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
        commands.setPath(plugin.fullPath(nodes[0].path()));
        menu.removeAll(); 
        // Override menu created by FolderAdapter
        JMenuItem item = new JMenuItem("Save");
        item.setActionCommand("commit");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        item = new JMenuItem("Save As...");
        item.setActionCommand("commitAs");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        item = new JMenuItem("Close");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        item = new JMenuItem("Description");
        item.setActionCommand("description");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        return menu;
    }
    
    @Override
    public String toolTipMessage(FTreeNode node, String oldMessage) {
        try {
            Tree tree = (Tree) plugin.aidaMasterTree().findTree(plugin.fullPath(node.path()));
            String description = getDescription(tree);
            return description == null ? oldMessage : description;
        } catch (Exception x) {
          return oldMessage;
        }
      
    }
    
    public CommandProcessor commandProcessor(FTreeNode[] selectedNodes) {
        FTreeNode[] nodes = selectedNodes;
        commands.setPath(plugin.fullPath(nodes[0].path()));
        return commands;
    }
    
    public class Commands extends CommandProcessor {
        private String path;
        ITree masterTree = plugin.aidaMasterTree();
        
        public void setPath( String path ) {
            this.path = path;
        }
        
        public void onClose() throws Exception {
            //This is invoked later to avoid the node being closed undernith
            //the popup menu that causes a NPE to be thrown in the CommandProcessor.
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    masterTree.unmount(path);
                }
            } );
        }
        
        public void onCommit() throws IOException {
            ITree tree = masterTree.findTree( path );
            if ( ((Tree)tree).hasStore() )
                tree.commit();
            else
                commitAs(tree);
        }

        public void onCommitAs() throws IOException {
            ITree tree = masterTree.findTree( path );
            commitAs(tree);
        }
        
        private void commitAs( ITree tree )throws IOException {
            JFileChooser chooser = new JFileChooser(plugin.getLastDir());
            chooser.setDialogTitle("Save As...");
            File f = null;
            int returnVal = chooser.showSaveDialog(app);
            if (returnVal != JFileChooser.APPROVE_OPTION)
                return;
            else {
                f = chooser.getSelectedFile();
                plugin.setLastDir(f);
                if (f.exists()) {
                    int rc = JOptionPane.showConfirmDialog(app, "Replace existing file?", null, JOptionPane.OK_CANCEL_OPTION);
                    if (rc != JOptionPane.OK_OPTION)
                        return;
                }
            }            
            
            ((Tree)tree).init(f.getAbsolutePath(), false, true, "xml", "",false);
            tree.commit();
            
            //Check if this tree is at root level. If so rename it.
            /*
            try {
                if ( path.endsWith("/") ) path = path.substring(0, path.length()-1);
                String treePath = "/"+path.substring( path.lastIndexOf("/"));
                masterTree.findTree(treePath);
                String mountPointName = plugin.getMountPointName(f.getName());   
                masterTree.mv(treePath, "/"+mountPointName);
            } catch (Exception e) {}
             */
        }

        public void onDescription() throws IOException {
          try {
            final Tree tree = (Tree) masterTree.findTree( path );
            if (tree.isReadOnly()) {
              app.error("This is read-only tree.");
              return;
            }
            String description = getDescription(tree);
            final JTextArea area = new JTextArea();
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            if (description != null) area.setText(description);
            final JScrollPane scrollPane = new JScrollPane(area);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setPreferredSize(new Dimension(350, 350));
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                int out = JOptionPane.showConfirmDialog(app, scrollPane, "Tree description", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (out == JOptionPane.OK_OPTION) {
                  try {
                      setDescription(tree, area.getText());
                  } catch (Exception x) {
                    app.error("This tree does not support descriptions", x);
                  }
                }
              }
            });
          } catch (Exception x) {
            app.error("This tree does not support descriptions", x);
          }
        }
        
    }

  private void setDescription(Tree tree, String description) {
    IManagedObject mo;
    try {
      mo = tree.find("/Tree description");
    } catch (IllegalArgumentException x) {
      mo = new Histogram1D("Tree description", "Tree description", new FixedAxis(1, 0., 1.));
      tree.add("/", mo);
    }
    ((Histogram1D) mo).annotation().addItem("Tree description", description);
  }

  private String getDescription(Tree tree) {
    try {
      IManagedObject mo = tree.find("/Tree description");
      return ((Histogram1D) mo).annotation().value("Tree description");
    } catch (IllegalArgumentException x) {
      return null;
    }
  }
  
}