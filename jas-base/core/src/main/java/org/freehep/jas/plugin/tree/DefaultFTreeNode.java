package org.freehep.jas.plugin.tree;

import java.awt.Component;
import javax.swing.tree.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import javax.swing.Icon;
import javax.swing.tree.MutableTreeNode;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapterManager;
import org.freehep.jas.plugin.tree.FTreeLeafNode;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.FTreeNodeEvent;
import org.freehep.jas.plugin.tree.FTreeNodeListener;
import org.freehep.jas.plugin.tree.FTreeNodeSorterManager;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.util.images.ImageHandler;

/**
 * The default implementation of a node. A FTreeNode is what appears on a
 * tree. It only has a name and a class. Its behaviour is determined by a set of
 * corresponding FTreeNodeAdapter that are assigned to the node through
 * inheritance via its class.
 * @author The FreeHEP team @ SLAC.
 */
class DefaultFTreeNode extends DefaultMutableTreeNode implements FTreeNode, FTreeNodeListener {
    
    private boolean allowsChildren = false;
    private boolean allowsChildrenChecked = false;
    private String name;
    private Class type;
    private FTree tree;
    private ArrayList listeners = new ArrayList();
    private String sortingString = "Default order";
    private boolean recursiveSorting = false;
    private final static Icon NOICON = ImageHandler.brokenIcon;
    private List sortedChildren;
    public boolean childrenChecked = false;
    
    private Hashtable keyHash = null;
    
    private Object nodeObject = null;
    
    /** Create a new FTreeNode via a FTreeNodeAddedNotification.
     * @param e The FTreeNodeAddedNotification that creates this node.
     */
    DefaultFTreeNode(FTreeNodeAddedNotification e, FTree tree) {
        this( e.nodeName(), e.nodeClass(), e.nodeObject(), tree );
    }
    
    /** Create a new FTreeNodeEvent by specifying its name and class.
     * @param name The name of the node
     * @param type The class of the node
     */
    DefaultFTreeNode(String name, Class type, FTree tree) {
        this( name, type, null, tree );
    }
    
    DefaultFTreeNode(String name, Class type, Object obj, FTree tree) {
        this.name = name;
        this.type = type;
        this.nodeObject = obj;
        this.tree = tree;
    }
    
    //********************************//
    // Methods to implement FTreeNode //
    //********************************//
    
    public Object objectForClass(Class clazz) {
        if ( nodeObject != null && clazz.isAssignableFrom( nodeObject.getClass() ) )
            return nodeObject;
        return adapterManager().userObjectForNode(this, clazz);
    }
    
    public Class type() {
        return type;
    }
    
    public FTree tree() {
        return tree;
    }
    
    public FTreePath path() {
        if ( isRoot() ) return null;
        if ( ( (DefaultFTreeNode)parent() ).isRoot() ) return new FTreePath( realName() );
        return parent().path().pathByAddingChild(realName());
    }
    
    public void addKey( Object key, Object value ) {
        if ( keyHash == null )
            keyHash = new Hashtable();
        keyHash.put( key, value );
    }
    
    public void removeKey( Object key ) {
        if ( keyHash != null )
            keyHash.remove(key);
    }
    
    public Object value( Object key ) {
        if ( keyHash != null )
            return keyHash.get( key );
        return null;
    }
    
    public void addFTreeNodeListener(FTreeNodeListener listener) {
        listeners.add(listener);
    }
    
    public void removeFTreeNodeListener(FTreeNodeListener listener) {
        listeners.remove(listener);
    }
    
    public List childNodes() {
        checkForChildren();
        return Collections.unmodifiableList( sortedChildren() );
    }

    //***************************************//
    // Method to implement FTreeNodeListener //
    //***************************************//
    
    public void nodeChanged(FTreeNodeEvent event) {
        // Fire the same event to all the listeners for this node
        // This will garantee that the event is cascaded down to the root
        fireFTreeNodeEvent(event);

        if ( event.eventId() == FTreeNodeEvent.NODE_CHANGED ) {
            FTreePath parentPath = event.node().path().getParentPath();
            if ( (parentPath == null && isRoot()) || ( parentPath != null && parentPath.equals( path() ) ) )
                applySorting( sortingString(), isRecursiveSorting() );
        }
    }
    
    
    
    FTreeNodeStructureProvider structureProvider() {
        return adapterManager().nodeStructureProvider(this);
    }
    
    public String realName() {
        return name;
    }
    
    public String name() {
        String text = adapterManager().text(this);
        if ( text != null )
            return text;
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    boolean isShowingName() {
        return realName().equals(name());
    }
    
    public Icon icon(boolean selected, boolean expanded) {
        Icon icon = NOICON;
        if ( getAllowsChildren() )
            icon = adapterManager().adapterForClass(FTreeFolderNode.class).icon(this,icon,selected,expanded);
        else
            icon = adapterManager().adapterForClass(FTreeLeafNode.class).icon(this,icon,selected,expanded);
        return adapterManager().icon(this, icon, selected, expanded);
    }
    
    Component treeCellRendererComponent(Component component, boolean sel, boolean expanded, boolean leaf, boolean hasFocus) {
        return adapterManager().treeCellRendererComponent(component, this, sel, expanded, leaf, hasFocus);
    }
    
    String toolTipMessage() {
        return adapterManager().toolTipMessage(this);
    }
    
    DefaultFTreeNode find(String childName) {
        for ( int i = 0; i < getChildCount(); i++ ) {
            DefaultFTreeNode node = (DefaultFTreeNode) getChildAt(i);
            if ( node.realName().equals(childName) ) return node;
        }
        return null;
    }
    
    public String toString() {
        return realName();
    }
    
    boolean isEditable() {
        FTreeNodeTextChangeEvent evt = new FTreeNodeTextChangeEvent(this, name(), null, isShowingName());
        return adapterManager().isNodeEditable(evt);
    }
    
    public FTreeNode parent() {
        return (FTreeNode) super.getParent();
    }
    
    public DefaultFTreeNode root() {
        return (DefaultFTreeNode) super.getRoot();
    }
    
    public boolean getAllowsChildren() {
        if ( isRoot() ) return true;
        if ( type == Object.class) return true;
        if ( ! allowsChildrenChecked ) {
            allowsChildren = adapterManager().allowsChildren(this);
            allowsChildrenChecked = true;
        }
        return allowsChildren;
    }
    
    boolean childrenChecked() {
        if ( isRoot() ) return true;
        return childrenChecked;
    }
    
    protected void checkForChildren() {
        if ( childrenChecked() ) return;
        childrenChecked = true;
        if ( getAllowsChildren() )
            if ( sortedChildren().size() == 0 )
                adapterManager().checkForChildren(this);
    }
    
    public int getChildCount() {
        checkForChildren();
        return sortedChildren().size();
    }
    
    public Enumeration children() {
        checkForChildren();
        return new MyEnumeration( sortedChildren().iterator() );
    }
    
    // Are the two methods below really needed?
    public TreeNode getFirstChild() {
        checkForChildren();
        return (TreeNode) sortedChildren().get(0);
    }
    
    public TreeNode getLastChild() {
        checkForChildren();
        return (TreeNode) sortedChildren().get( sortedChildren().size() - 1 );
    }
    
    public boolean addNode(MutableTreeNode newChild) {
        DefaultFTreeNode node = (DefaultFTreeNode) newChild;
        if ( structureProvider().addNode( (FTreeNode) newChild ) ) {
            newChild.setParent(this);
            if ( ! sortedChildren().contains(node) ) {
                int index = sortedChildren().size();
                Comparator comparator = FTreeNodeSorterManager.sortingComparator(sortingString);
                if ( index > 0 && comparator != null ) {
                    index = Collections.binarySearch(sortedChildren(),node,comparator);
                    index = -index - 1;
                }
                sortedChildren().add(index,node);
                if ( isRecursiveSorting() ) {
                    node.setSorting( sortingString(), true );
                }
                node.addFTreeNodeListener( (FTreeNodeListener) this );
            }
            fireFTreeNodeEvent( new FTreeNodeEvent( this, node, FTreeNodeEvent.NODE_ADDED ) );
            return true;
        }
        return false;
    }
    
    public boolean removeNode(MutableTreeNode newChild) {
        if ( structureProvider().removeNode( (FTreeNode) newChild ) ) {
            sortedChildren().remove(newChild);
            DefaultFTreeNode node = (DefaultFTreeNode) newChild;
            fireFTreeNodeEvent( new FTreeNodeEvent( this, node, FTreeNodeEvent.NODE_REMOVED ) );
            node.removeFTreeNodeListener( (FTreeNodeListener) this );
            return true;
        }
        return false;
    }
    
    
    public TreeNode getChildAt(int index)	{
        checkForChildren();
        return (TreeNode) sortedChildren().get(index);
    }
    
    public int getIndex(TreeNode child) {
        checkForChildren();
        return sortedChildren().indexOf(child);
    }
    
    /**
     * Sorting methods.
     */
    
    /**
     * Set the type of sorting sorting.
     * @param sorting An internal representation of the sorting algorithm.
     *
     */
    void setSorting(String sortingString, boolean isRecursive) {
        this.sortingString = sortingString;
        this.recursiveSorting = isRecursive;
    }
    
    String sortingString() {
        return sortingString;
    }
    
    boolean isRecursiveSorting() {
        return recursiveSorting;
    }
    
    /**
     * Apply the sorting if necessary.
     * @param recursive True if the sorting has to be applied recursively.
     *
     */
    private List sortedChildren() {
        if ( sortedChildren == null ) {
            // Create a new list containing the same elements in the structure provider's list
            sortedChildren = new ArrayList( structureProvider().nodes() );
            if ( sortedChildren.size() != 0 )
                Collections.sort(sortedChildren, FTreeNodeSorterManager.sortingComparator(sortingString()));
        }
        return sortedChildren;
    }
    
    void applySorting( String sortingString, boolean recursive ) {
        applySorting( sortingString, recursive, true );
    }
    
    void applySorting( String sortingString, boolean recursive, boolean finalize ) {
        if ( getAllowsChildren() ) {
            setSorting(sortingString,recursive);
            // Apply sorting only the children have been checked
            if ( childrenChecked() ) {
                // Sort the nodes in the right order.
                Collections.sort(sortedChildren(), FTreeNodeSorterManager.sortingComparator(sortingString));

                Iterator childIter = sortedChildren().iterator();
                while ( childIter.hasNext() ) {
                    DefaultFTreeNode node = (DefaultFTreeNode) childIter.next();
                    if ( isRecursiveSorting() )
                        node.applySorting( sortingString, true, false );
                }            
                // To finalize make sure the selection is correct.
                if ( finalize ) {
                    DefaultFTree tree = (DefaultFTree) tree();
                    if ( tree.isNodeExpanded(this) )
                        tree.treeChanged( new FTreeNodeStructureChangedNotification(this, this) );                
                }
            }
        }
    }
        
    void setDefaultTree(FTree tree) {
        this.tree = tree;
    }
    
    void fireFTreeNodeEvent( FTreeNodeEvent event ) {
        for ( int i = 0; i < listeners.size(); i++ )
            ( (FTreeNodeListener) listeners.get(i) ).nodeChanged( event );
    }
        
    private class MyEnumeration implements Enumeration {
        
        private Iterator iter;
        
        MyEnumeration( Iterator iter ) {
            this.iter = iter;
        }
        
        public boolean hasMoreElements() {
            return iter.hasNext();
        }
        
        public Object nextElement() {
            return iter.next();
        }
        
    }
    
    private DefaultFTreeNodeAdapterManager adapterManager() {
        return ( (DefaultFTree) tree() ).adapterManager();
    }
}