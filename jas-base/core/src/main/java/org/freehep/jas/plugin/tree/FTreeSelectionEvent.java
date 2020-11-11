package org.freehep.jas.plugin.tree;

import java.util.ArrayList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FTreeSelectionEvent {
    
    private TreeSelectionEvent event;
    private FTreeNode[] addedNodes = null;
    private FTreeNode[] removedNodes = null;
    private ArrayList add;
    private boolean haveNodesBeenChecked = false;
    private FTree tree;
    
    protected FTreeSelectionEvent(TreeSelectionEvent event, FTree tree) {
        this.event = event;
        this.tree = tree;
    }
    
    public FTree tree() {
        return tree;
    }
        
    /**
     * Get the array of the nodes that were removed in the last selection.
     * @return The array of the unselected FTreeNode. If no nodes were
     *         removed, null is returned.
     *
     */
    public FTreeNode[] removedNodes() {
        if ( ! haveNodesBeenChecked )
            checkNodes();
        return removedNodes;
    }
    
    /**
     * Get the array of the nodes that were added in the last selection.
     * @return The array of the FTreeNodes that were added to the selection.
     *         If no nodes were added, null is returned.
     *
     */
    public FTreeNode[] addedNodes() {
        if ( ! haveNodesBeenChecked )
            checkNodes();
        return addedNodes;
    }

    /**
     * Get the array of nodes that are currently selected.
     * @return The array of FTreeNodes that are currently selected.
     *
     */
    public FTreeNode[] selectedNodes() {
        return tree.selectedNodes();
    }
    
    /**
     * Check if a FTreeNode has been added to last selection.
     * @param node The FTreeNode to check.
     * @return     <code>true</code> if the node was added to the selection,
     *             <code>false</code> otherwise.
     *
     */
    boolean isAddedNode(FTreeNode node) {
        if ( ! haveNodesBeenChecked )
            checkNodes();
        return add.contains(node);
    }

    private void checkNodes() {
        haveNodesBeenChecked = true;
        add = new ArrayList();
        TreePath[] paths = event.getPaths();
        for ( int i = 0; i < paths.length; i++ )
            if ( event.isAddedPath(i) )
                add.add( paths[i].getLastPathComponent() );
        
        int added = add.size();
        int removed = paths.length - add.size();
        if ( added > 0 ) 
            addedNodes = new FTreeNode[ added ];
        if ( removed > 0 )
            removedNodes = new FTreeNode[ removed ];
        
        int addCounter = 0;
        int removeCounter = 0;
        for ( int i = 0; i < paths.length; i++ )
            if ( event.isAddedPath(i) )
                addedNodes[ addCounter++ ] = (FTreeNode) paths[i].getLastPathComponent();
            else
                removedNodes[ removeCounter++ ] = (FTreeNode) paths[i].getLastPathComponent();
    }    
}
