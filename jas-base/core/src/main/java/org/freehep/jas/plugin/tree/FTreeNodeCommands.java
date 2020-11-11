package org.freehep.jas.plugin.tree;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.freehep.application.Application;
import org.freehep.application.studio.Studio;
import org.freehep.jas.plugin.tree.FTreePlugin;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.jas.plugin.tree.FTreeSortingChooser;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
class FTreeNodeCommands extends CommandProcessor {
    
    private DefaultJTree jTree;

    public FTreeNodeCommands(DefaultJTree jTree) {
        this.jTree = jTree;
    }
    
    public void onRename() {
        jTree.startEditingAtPath( jTree.getLeadSelectionPath() );
    }   
    
    public void enableRename(CommandState state) {
        boolean enabled = false;
        if ( jTree.getSelectionCount() == 1 ) {
            DefaultFTreeNode node = jTree.selectedNodes()[0];
            enabled = node.isEditable();
        }
        state.setEnabled( enabled );
    }
    
    public void onSorting() {
        DefaultFTreeNode[] nodes = jTree.selectedNodes();
        String sortingString = sortingString = nodes[0].sortingString();
        
        Application app = Studio.getApplication();
        Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, app);
        JDialog dlg = new JDialog(frame,true);
        FTreeSortingChooser sortingChooser = new FTreeSortingChooser((DefaultFTree)jTree.getModel(), sortingString,dlg);
        dlg.getContentPane().add(sortingChooser);
        dlg.setLocationRelativeTo(app);
        dlg.pack();
        dlg.setVisible(true);
    }
    
    public void enableSorting(CommandState state) {
        boolean enabled = false;
        DefaultFTreeNode[] nodes = jTree.selectedNodes();
        for ( int i = 0; i < nodes.length; i++ ) {
            if ( nodes[i].getAllowsChildren() ) {
                enabled = true;
                break;
            }
        }
        state.setEnabled( enabled );
    }    
}
