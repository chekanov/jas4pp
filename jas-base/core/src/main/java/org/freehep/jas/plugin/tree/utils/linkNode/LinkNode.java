package org.freehep.jas.plugin.tree.utils.linkNode;

import org.freehep.jas.plugin.tree.FTree;
import org.freehep.jas.plugin.tree.FTreePath;

/**
 * A link in the FTree.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface LinkNode {
    

    /**
     * The FTreePath being linked.
     * @return The linked path.
     *
     */
    public FTreePath linkedPath();

    /**
     * The FTree to which the linked path belongs to.
     * @return The FTree of the linked path.
     *
     */
    public FTree tree();

}
