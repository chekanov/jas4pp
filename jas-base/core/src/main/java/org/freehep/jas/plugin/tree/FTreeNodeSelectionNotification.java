package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTreeNotification;
import org.freehep.jas.plugin.tree.FTreeNode;

/**
 * The event to be sent to the FTree when a node is selected.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FTreeNodeSelectionNotification extends FTreeNotification {
    
    public static final int NODE_SELECTED = 0;
    public static final int NODE_UNSELECTED = 1;
    
    private int state;
        
    public FTreeNodeSelectionNotification(Object source, FTreePath path, int state) {
        super(source, path);
        this.state = state;
    }
    
    int state() {
        return state;
    }
    
}
