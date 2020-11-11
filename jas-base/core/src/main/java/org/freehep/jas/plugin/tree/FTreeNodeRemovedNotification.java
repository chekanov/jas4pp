package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTreeNotification;

/**
 * The event to be sent to the FTree when a node is removed.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FTreeNodeRemovedNotification extends FTreeNotification {
        
    public FTreeNodeRemovedNotification(Object source, FTreePath path) {
        super(source, path);
    }

    public FTreeNodeRemovedNotification(Object source, String path) {
        super(source, new FTreePath(path));
    }
}
