package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTreeNotification;
import org.freehep.jas.plugin.tree.FTreeNode;

/**
 * The event to be sent to the FTree when a node is changed.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
class FTreeNodeStructureChangedNotification extends FTreeNotification {
    
    private FTreeNode node;
    
    public FTreeNodeStructureChangedNotification(Object source, FTreeNode node) {
        super(source, node.path());
        this.node = node;
    }
    
    public FTreeNode node() {
        return node;
    }
    
}
