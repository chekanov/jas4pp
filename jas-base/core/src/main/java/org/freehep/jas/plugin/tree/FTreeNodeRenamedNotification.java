package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTreeNotification;

/**
 * The event to be sent to the FTree when a node is renamed.
 * Renaming a node only changes its name, not its parent. To change
 * the parent of a node use a FTreeNodeMovedEvent.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FTreeNodeRenamedNotification extends FTreeNotification {
    
    String newName;
    
    public FTreeNodeRenamedNotification(Object source, FTreePath oldPath, String newName) {
        super(source, oldPath);
        this.newName = newName;
    }
    
    protected String nodeNewName() {
        return newName;
    }
    
}
