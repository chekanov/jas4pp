package org.freehep.jas.plugin.tree;

import java.util.List;
import org.freehep.jas.plugin.tree.FTreeNodeListener;
import org.freehep.jas.plugin.tree.FTreePath;

/**
 * An FTreeNode is a node of an FTree.
 * It has a type and it can contain multiple objects.
 * Its appearence and behavior are defined by {@link FTreeNodeAdapter FTreeNodeAdapters} 
 * that are selected based on the FTreeNode's type.
 * Each FTreeNode contains an Hashtable that can be used to store objects in the node.
 * This Hashtable should be primarily used by {@link FTreeNodeObjectProvider FTreeNodeObjectProviders}
 * to store objects that can be then accesed via the {@link FTreeNode#objectForClass(Class) objectForClass()}
 * method. Objects are stored in key-value pairs.
 * Changes to the FTreeNode are propagated to the node's {@link FTreeNodeListener liteners}
 * via {@link FTreeNodeEvent FTreeNodeEvents}.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface FTreeNode {
    
    /**
     * Get the object for a given class contained in this node. 
     * @param clazz The type of object to be returned.
     * @return      The node's object for the given class. If no object for the
     *              given class is present, null is returned.
     *
     */
    public Object objectForClass(Class clazz);
    
    /**
     * Get the node's type.
     * This is the type of the node as it was assigned when the node was added to the tree.
     * The type is used to select the FTreeNodeAdapters that provide the appearence
     * and behavior for the given node.
     * @return The node's type
     * 
     */
    public Class type();
    
    /**
     * Get the FTreePath that leads to this node.
     * @return The node's FTreePath.
     *
     */
    public FTreePath path();
    
    /**
     * Get the FTree to which this node belongs.
     * @return The FTree to which this node belongs.
     *
     */
    public FTree tree();
    
    /**
     * Get the list of children in this node.
     * The list is ordered as the nodes are currently displayed in the tree.
     * If the node has no children an empty list is returned.
     * @return The ordered list of this node's children.
     *
     */
    public List childNodes();
    
    /**
     * Get the parent for this node.
     * @return The node's parent.
     *
     */
    public FTreeNode parent();
    
    /**
     * Add a key-value pair to this node.
     * If the key already exists, its value is overritten.
     * @param key   The key for the given value.
     * @param value The corresponding value.
     *
     */
    public void addKey( Object key, Object value );
    
    /**
     * Remove a key, and the corresponding value, from this node.
     * @param key The key to be removed.
     *
     */
    public void removeKey( Object key );
    
    /**
     * Get the value contained in the node for a given key.
     * @param key The key.
     * @return    The corresponding object.
     *
     */  
    public Object value( Object key );
    
    /**
     * Add an FTreeNodeListener to this node. All the listeners registered with
     * this node will be notified of any changed to the FTreeNode.
     * @param listener The FTreeNodeListener to be added.
     *
     */
    public void addFTreeNodeListener( FTreeNodeListener listener );

    /**
     * Remove an FTreeNodeListener from this node.
     * @param listener The FTreeNodeListener to be removed.
     *
     */
    public void removeFTreeNodeListener( FTreeNodeListener listener );
    
}
