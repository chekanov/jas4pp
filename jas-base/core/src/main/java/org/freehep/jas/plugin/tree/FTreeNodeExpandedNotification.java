package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTreeNotification;

/**
 * The event to be sent to the FTree when a node is to be expanded.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FTreeNodeExpandedNotification extends FTreeNotification {
        
    public FTreeNodeExpandedNotification(Object source, FTreePath path) {
        super(source, path);
    }

}
