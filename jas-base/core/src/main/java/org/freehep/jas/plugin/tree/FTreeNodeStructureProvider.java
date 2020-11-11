package org.freehep.jas.plugin.tree;

import java.util.List;
import org.freehep.jas.plugin.tree.FTreeNode;

/**
 * Provides the structure for an FTreeNode.
 * Children are added and removed via this interface that also provides the
 * list the sorted children.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface FTreeNodeStructureProvider {
    
    /**
     * Add an FTreeNode to the node for which the structure is being provided.
     * @param node       The FTreeNode to be added.
     * @return           <code>true</code> if the node was succesfully added.
     *
     */
    public boolean addNode( FTreeNode node );
    
    /**
     * Remove an FTreeNode from the node for which the structure is being provided.
     * @param node The node to be removed.
     * @return     <code>true</code> if the node was succesfully removed.
     *
     */
    public boolean removeNode( FTreeNode node );
    
    /**
     * Get the list of the children for this node.
     * The children's order in the list is the way they will be displayed on the
     * tree (unless other sorting is applyed).
     * @return The list of the node's children.
     *
     */
    public List nodes();
    
}
