package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTreeNotification;

/**
 * The event to be sent to the FTree when a node is to be repainted.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FTreeNodeRepaintNotification extends FTreeNotification {
        
    private boolean recursive;
    
    public FTreeNodeRepaintNotification(Object source, FTreePath path) {
        this(source, path,false);
    }
    
    public FTreeNodeRepaintNotification(Object source, FTreePath path, boolean recursive) {
        super(source, path);
        this.recursive = recursive;
    }
    
    public FTreeNodeRepaintNotification(Object source, String path) {
        this( source, new FTreePath(path) );
    }

    public FTreeNodeRepaintNotification(Object source, String path, boolean recursive) {
        this( source, new FTreePath(path), recursive );
    }
    
    boolean isRecursive() {
        return recursive;
    }
}
