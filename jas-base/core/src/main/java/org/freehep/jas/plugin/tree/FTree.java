package org.freehep.jas.plugin.tree;

/** 
 * Via the FTree interface it is possible to:
 * - modify the existing tree structure
 * - access and modify the tree selection through the {@link FTreeSelectionManager FTreeSelectionManager}
 * - access existing {@link FTreeNode FTreeNodes}
 *
 * @author The FreeHEP team @ SLAC.
 */
public interface FTree {
    
    /**
     * Notify the FTree that something has changed.
     * @param notification The FTreeNotification containing the information regarding
     *                     the FTree's change.
     *
     */
    public void treeChanged( FTreeNotification notification );

    /**
     * Get the FTreeNode corresponding to a given FTreePath.
     * @param path The FTreePath for which the node is being requested.
     * @return     The corresponding FTreeNode. If no node is found for the
     *             give path, null is returned.
     *
     */
    public FTreeNode findNode( FTreePath path );
    
    /**
     * Get the FTree's name.
     * @return The name of the FTree.
     *
     */
    public String name();
    
    /**
     * Get the root node for this tree.
     * @return The tree's root.
     *
     */
    public FTreeNode root();
    
    /**
     * Get the ordered array of the selected nodes for the FTree
     * The nodes are in the order in which they were selected.
     * @return The ordered array of the selected FTreeNode. If no nodes
     *         are selected, null is returned.
     *
     */
    public FTreeNode[] selectedNodes();
    
    /**
     * Get the FTreeNodeAdapter that describes the behavior for a given node's type
     * on this tree. This method returns a single adapter that wraps all the
     * adapters registered with this tree for the given class.
     * @param clazz The type of the nodes for which the adapter is requested.
     * @return      The FTreeNodeAdapter for the given class.
     *
     */
    public FTreeNodeAdapter adapterForClass(Class clazz);
    
    /**
     * Get all the FTreeNodeAdapters registered with this tree for the given class.
     * @param clazz The type of the nodes for which the adapter is requested.
     * @return      The array of the FTreeNodeAdapters registered with this tree for the
     *              given node's type.
     *
     */
    public FTreeNodeAdapter[] adaptersForClass(Class clazz);
    
}