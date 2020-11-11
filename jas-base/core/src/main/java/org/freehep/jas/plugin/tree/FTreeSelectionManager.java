package org.freehep.jas.plugin.tree;

import javax.swing.tree.TreePath;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.FTreeNodeSelectionNotification;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
class FTreeSelectionManager {
    
    private DefaultJTree jTree;
    
    FTreeSelectionManager(DefaultJTree jTree) {
        this.jTree = jTree;
    }
    
    
    /**
     * Get the ordered array of the selected nodes for the FTree
     * The nodes are in the order in which they were selected.
     * @return The ordered array of the selected FTreeNode. If no nodes
     *         are selected, null is returned.
     *
     */
    FTreeNode[] selectedNodes() {
        return jTree.selectedNodes();
    }
    
    /**
     * Notify the FTreeSelectionManager that the selection is to be changed.
     * @param event The FTreeNodeSelectionNotification carrying the information of
     *              the selection change.
     *
     */
    public void selectionChange( FTreeNodeSelectionNotification event ) {
        TreePath path = FTreeUtils.treePathForNode( jTree.model().findNode( event.nodePath() ) );
        switch( event.state() ) {
            case FTreeNodeSelectionNotification.NODE_UNSELECTED :
                jTree.getSelectionModel().removeSelectionPath( path );
                break;
            case FTreeNodeSelectionNotification.NODE_SELECTED :
                jTree.getSelectionModel().addSelectionPath( path );
                break;
            default:
                throw new IllegalArgumentException("Unknown state "+event.state());
        }
    }
    
}
