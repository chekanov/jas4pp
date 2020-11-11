package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTreeNotification;
import org.freehep.jas.plugin.tree.FTreePath;

/**
 * The event to be sent to the FTree when a node is changed.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FTreeNodeSorterNotification extends FTreeNotification {
    
    private String sortingString;
    private boolean recursive = true;
    
    public FTreeNodeSorterNotification(Object source, FTreePath path, String sortingString) {
        this(source, path, sortingString, true);
    }
    
    public FTreeNodeSorterNotification(Object source, FTreePath path, String sortingString, boolean recursive) {
        super(source, path);
        this.sortingString = sortingString;
        this.recursive = recursive;
    }
    
    public String sortingString() {
        return sortingString;
    }
    
    public boolean recursive() {
        return recursive;
    }
    
}
