package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTreeNode;

/**
 * Interface to be implemented by classes that can provide objects for
 * FTreeNode.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface FTreeNodeObjectProvider {
    
    /**
     * Get the object for a given FTreeNode.
     * @param node  The FTreeNode for which the object is to be returned.
     * @param clazz The class of the object being requested.
     * @return      The corresponding object. If no object is to be provided, null
     *              is to be returned.
     *
     */
    Object objectForNode( FTreeNode node, Class clazz );
        
}
