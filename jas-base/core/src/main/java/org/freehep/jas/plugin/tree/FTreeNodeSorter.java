package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTreeNode;

/**
 *
 * An interface for sorting FTreeNodes in the FTree.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface FTreeNodeSorter {
    
    /**
     * Sort two FTreeNodes.
     * @param node1 The first node.
     * @param node2 The second node.
     * @return      -1 if node1 comes before node2
     *               0 if their position is interchengable
     *              +1 if node1 comes after node2
     *
     */
    public int sort( FTreeNode node1, FTreeNode node2 );
    
    /**
     * The name of the sorting algorithm.
     * @return The name of the algorithm.
     *
     */
    public String algorithmName();
    
    /**
     * The description of what the algorithm does.
     * @return The description.
     *
     */
    public String description();
    
}
