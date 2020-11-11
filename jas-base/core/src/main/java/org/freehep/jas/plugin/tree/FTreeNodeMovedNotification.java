package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTreeNotification;

/**
 * The event to be sent to the FTree when a node is 
 * moved to a different FTreePath.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FTreeNodeMovedNotification extends FTreeNotification {
    
    private FTreePath newPath;
    
    public FTreeNodeMovedNotification(Object source, FTreePath oldPath, FTreePath newPath) {
        super(source, oldPath);
        this.newPath = newPath;
    }
    
    protected FTreePath nodeNewPath() {
        return newPath;
    }

}
