package org.freehep.jas.plugin.tree;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public interface FTreeNodeListener {
    
    /**
     * Invoked when the node being listened has changed.
     * @param event The FTreeNodeEvent describing the change in the node.
     *
     */
    public void nodeChanged( FTreeNodeEvent event );
    
}
