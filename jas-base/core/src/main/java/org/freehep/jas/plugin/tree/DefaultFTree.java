package org.freehep.jas.plugin.tree;

import javax.swing.tree.*;
import javax.swing.SwingUtilities;
import java.util.*;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JTree;
import org.freehep.jas.plugin.tree.FTreeFolderNode;
import org.freehep.jas.plugin.tree.FTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreeNodeRemovedNotification;
import org.freehep.jas.plugin.tree.FTreeNodeRepaintNotification;
import org.freehep.jas.plugin.tree.FTreeNodeSelectionNotification;
import org.freehep.jas.plugin.tree.FTreeSelectionManager;
import org.jdom.Element;

/**
 * The default implementation of FTree.
 * @author The FreeHEP team @ SLAC.
 * @see FTree
 *
 */
class DefaultFTree extends DefaultTreeModel implements Runnable, FTree {
    
    private List queue;
    private DefaultJTree jTree;
    private String name;
    private DefaultFTreeNodeAdapterManager adapterManager;
    
    DefaultFTree(DefaultFTreeNode root) {
        super( root, true );
        root.setDefaultTree(this);
        this.name = root.realName();
        adapterManager = new DefaultFTreeNodeAdapterManager(this);            
        FTreePlugin thePlugin = FTreePlugin.plugin();
        root.setSorting( thePlugin.treeSortingAlgorithm(name), thePlugin.isTreeSortingRecursive(name) );
    }
    
    protected void setJTree( DefaultJTree jTree ) {
        this.jTree = jTree;
    }
    
    DefaultJTree jTree() {
        return jTree;
    }
    
    public FTreeNode root() {
        return (FTreeNode) getRoot();
    }
    
    public String name() {
        return name;
    }
    
    /**
     * This sets the user object of the TreeNode identified by path
     * and posts a node changed.  If you use custom user objects in
     * the TreeModel you're going to need to subclass this and
     * set the user object of the changed node to something meaningful.
     * In our implementation we do nothing.
     * This method belongs to the DefaultTreeModel.
     * What is this used for?
     * Why is this needed?
     *
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
    }
    
    public synchronized void treeChanged( FTreeNotification notification ) {
        if ( SwingUtilities.isEventDispatchThread() ) {
            if ( queue != null )
                run();
            processTreeNotification(notification);
        }
        else {
            boolean wasEmpty = queue == null;
            if (wasEmpty)
                queue = new ArrayList();
            queue.add(notification);
            if (wasEmpty)
                SwingUtilities.invokeLater(this);
        }
    }
    
    public void nodeStructureChanged(TreeNode node) {
        Element el = new Element("root");
        saveNodeStructure((DefaultFTreeNode)node, el);
        super.nodeStructureChanged(node);
        restoreNodeStructure((DefaultFTreeNode)node, el.getChild("node"), true, new ArrayList());
    }
    
    /**
     * Save the structure of a node in a JDom Element.
     *
     */
    private void saveNodeStructure(DefaultFTreeNode node, Element el) {
        if ( node.childrenChecked() && isNodeExpanded(node) ) {
            Element e = new Element("node");
            e.setAttribute("name",node.realName());
            if ( isNodeSelected( node ) )
                e.setAttribute("isSelected", "true");

            int nChild = node.getChildCount();
            for ( int i = 0; i < nChild; i++ ) {
                DefaultFTreeNode n = (DefaultFTreeNode)node.getChildAt(i);
                if ( isNodeExpanded( n ) )
                    saveNodeStructure(n, e);
            }
            el.addContent(e);
        }
    }
    /**
     * Restore the structure of the a node.
     *
     */
    private void restoreNodeStructure(DefaultFTreeNode node, Element el, boolean finalize, List selNodes) {
        if ( el == null ) return;
        if ( finalize && selNodes == null )
            selNodes = new ArrayList();
        List nodes = el.getChildren("node");
        String isSelected = el.getAttributeValue("isSelected");            
        if ( isSelected != null )
            selNodes.add(node);
        for ( int i = 0; i < nodes.size(); i++ ) {
            Element childEl = (Element) nodes.get(i);
            String childName = childEl.getAttributeValue("name");            
            DefaultFTreeNode childNode = node.find(childName);
            if ( childNode != null ) {
                expandNode( childNode );
                restoreNodeStructure( childNode, childEl, false, selNodes );
            }
        }

        if ( finalize ) {
            DefaultFTreeNode[] selectedNodes = new DefaultFTreeNode[ selNodes.size() ];
            for ( int i = 0; i < selectedNodes.length; i++ )
                selectedNodes[i] = (DefaultFTreeNode) selNodes.get(i);
            selectNodes( selectedNodes );
        }
    }
    
    /**
     * All the FTreeNotifications received by the FTree are queued and run
     * later, on a different thread than the SwingThread, so that the GUI
     * is not affected by what happens on the shell.
     */
    public void run() {
        Iterator iter;
        synchronized (this) {
            iter = queue.iterator();
            queue = null;
        }
        for (; iter.hasNext();) {
            FTreeNotification notification = (FTreeNotification) iter.next();
            processTreeNotification( notification );
        }
    }
    
    private boolean addNodeToParent(FTreeNodeAddedNotification event) {
        DefaultFTreeNode parent = (DefaultFTreeNode)findParent(event);
        if ( parent == null ) {
            if ( ! addNodeToParent( new FTreeNodeAddedNotification(event.getSource(), event.nodePath().getParentPath(), FTreeFolderNode.class) ) )
                return false;
            parent = (DefaultFTreeNode)findParent(event);
        } 
        if ( ! parent.getAllowsChildren() ) throw new IllegalArgumentException("Cannot add nodes to "+parent+"! It doesn't allow children");
        DefaultFTreeNode node = new DefaultFTreeNode(event, this);
        if ( ! parent.addNode(node) )
            return false;
        int indexOfChild = parent.getIndex(node);
        nodesWereInserted(parent, new int[] { indexOfChild });
        return true;        
    }
    
    private void processTreeNotification( FTreeNotification notification ) {
        Class eventClass = notification.getClass();
        if ( eventClass == FTreeNodeAddedNotification.class ) {
            FTreeNodeAddedNotification event = (FTreeNodeAddedNotification) notification;
            if ( findNode(event) != null ) throw new IllegalArgumentException("Node "+event.nodePath()+" already exists!");
            addNodeToParent(event);
            jTree.repaint();
        }
        else if ( eventClass == FTreeNodeRemovedNotification.class ) {
            FTreeNodeRemovedNotification event = (FTreeNodeRemovedNotification) notification;
            DefaultFTreeNode node = (DefaultFTreeNode)findNode(event);
            adapterManager().closeNode( node );
            DefaultFTreeNode parent = (DefaultFTreeNode) node.getParent();
            int n = parent.getIndex(node);
            if ( parent.removeNode(node) ) {
                nodesWereRemoved(parent, new int[]
                { n }, new Object[]
                { node });
            }
        }
        else if ( eventClass == FTreeNodeRenamedNotification.class ) {
            FTreeNodeRenamedNotification event = (FTreeNodeRenamedNotification) notification;
            DefaultFTreeNode node = (DefaultFTreeNode)findNode(event);
            node.setName( event.nodeNewName() );
            nodeChanged( node );
            node.fireFTreeNodeEvent( new FTreeNodeEvent( node, node, FTreeNodeEvent.NODE_CHANGED ) );
        }
        else if ( eventClass == FTreeNodeRepaintNotification.class ) {
            //This reloads each node in the tree. Fix to JAS-212
            boolean recursive = ( (FTreeNodeRepaintNotification) notification ).isRecursive();
            DefaultFTreeNode node = (DefaultFTreeNode) findNode(notification);
            updateNode( node, recursive );
            if ( ! recursive )
                node.fireFTreeNodeEvent( new FTreeNodeEvent( node, node, FTreeNodeEvent.NODE_CHANGED ) );
            else
                node.fireFTreeNodeEvent( new FTreeNodeEvent( node, node, FTreeNodeEvent.NODE_STRUCTURE_CHANGED ) );
        }
        else if ( eventClass == FTreeNodeStructureChangedNotification.class ) {
            FTreeNodeStructureChangedNotification event = (FTreeNodeStructureChangedNotification) notification;
            DefaultFTreeNode node = (DefaultFTreeNode) event.node();
            nodeStructureChanged( node );
            node.fireFTreeNodeEvent( new FTreeNodeEvent( node, node, FTreeNodeEvent.NODE_STRUCTURE_CHANGED ) );
        }
        else if ( eventClass == FTreeNodeExpandedNotification.class ) {
            DefaultFTreeNode node = (DefaultFTreeNode)findNode(notification);
            expandNode(node);
            node.fireFTreeNodeEvent( new FTreeNodeEvent( node, node, FTreeNodeEvent.NODE_STRUCTURE_CHANGED ) );
        }
        else if ( eventClass == FTreeNodeMovedNotification.class ) {
            FTreeNodeMovedNotification event = (FTreeNodeMovedNotification) notification;
            DefaultFTreeNode node = (DefaultFTreeNode)findNode(event);
            DefaultFTreeNode parent = (DefaultFTreeNode) node.parent();
            DefaultFTreeNode newParent = (DefaultFTreeNode)findNode( event.nodeNewPath().getParentPath() );
            String newName = (String)event.nodeNewPath().getLastPathComponent();
            
            if ( parent != newParent ) {
                if ( ! newParent.getAllowsChildren() ) throw new IllegalArgumentException("Cannot move node to "+newParent+"! It doesn't allow children");
                parent.removeNode(node);
                newParent.addNode(node);
                node.setName(newName);
                nodeStructureChanged( parent );
                nodeStructureChanged( newParent );
            } else {
                node.setName(newName);
                nodeChanged(node);
                node.fireFTreeNodeEvent( new FTreeNodeEvent( node, node, FTreeNodeEvent.NODE_CHANGED ) );
            }
        }
        else if ( eventClass == FTreeNodeSorterNotification.class ) {
            DefaultFTreeNode node = (DefaultFTreeNode)findNode(notification);
            String sortingString = ((FTreeNodeSorterNotification) notification).sortingString();
            boolean recursive = ((FTreeNodeSorterNotification) notification).recursive();
            node.applySorting(sortingString, recursive);
            node.fireFTreeNodeEvent( new FTreeNodeEvent( node, node, FTreeNodeEvent.NODE_STRUCTURE_CHANGED ) );
        }
        else if ( eventClass == FTreeNodeSelectionNotification.class )
            selectionManager().selectionChange( (FTreeNodeSelectionNotification) notification );
        else throw new IllegalArgumentException("Unsupported FTreeNotification class "+eventClass);
    }
    
    private void updateNode( DefaultFTreeNode node, boolean recursive ) {
        nodeChanged(node);                
        // Update the substructure ONLY if the node is expanded.
        if ( isNodeExpanded(node) )
            for ( int i = 0; i < node.getChildCount(); i++ ) {
                if ( recursive )
                    updateNode( (DefaultFTreeNode)node.getChildAt(i), recursive );
                else
                    nodeChanged( node.getChildAt(i) );
            }
    }

    void selectNodes( DefaultFTreeNode[] selNodes ) {
        TreePath[] selPath = new TreePath[ selNodes.length ];
        for ( int i = 0; i < selPath.length; i++ )
            selPath[i] = FTreeUtils.treePathForNode( selNodes[i] );        
        jTree.setSelectionPaths( selPath );
    }
    
    void selectNode( DefaultFTreeNode node ) {
        jTree.setSelectionPath( FTreeUtils.treePathForNode(node) );
    }
    
    void expandNode( DefaultFTreeNode node ) {
        jTree.expandPath(FTreeUtils.treePathForNode(node));
    }
    
    boolean isNodeExpanded( DefaultFTreeNode node ) {
        TreePath path = FTreeUtils.treePathForNode(node);
        return jTree.isExpanded( path );
    }
    
    boolean isNodeSelected( DefaultFTreeNode node ) {
        TreePath path = FTreeUtils.treePathForNode(node);
        return jTree.isPathSelected( path );
    }

    private FTreeNode findNode(FTreeNotification e) {
        return findNode(e.nodePath());
    }
    
    public FTreeNode findNode(FTreePath path) {
        if ( path == null )
            return findNode( path, 0 );
        return findNode( path, path.getPathCount() );
    }
    
    private FTreeNode findNode(FTreePath path, int depth) {
        DefaultFTreeNode node = (DefaultFTreeNode) root();
        FTreePath treePath = null;
        for (int i = 0; i < depth; i++) {
            String name = path.getPathComponent(i);
            if ( treePath == null )
                treePath = new FTreePath(name);
            else
                treePath = treePath.pathByAddingChild(name);
            
            if ( node == null ) return null;
            DefaultFTreeNode child = (DefaultFTreeNode) node.find(name);
            node = child;
        }
        return node;
    }
    
    private FTreeNode findParent(FTreeNotification e) {
        return findNode(e.nodePath(), e.nodePath().getPathCount() - 1);
    }
    
    
    public FTreeSelectionManager selectionManager() {
        return jTree.selectionManager();
    }
    
    public FTreeNode[] selectedNodes() {
        return selectionManager().selectedNodes();
    }
    
    DefaultFTreeNodeAdapterManager adapterManager() {
        return adapterManager;
    }
    
    public FTreeNodeAdapter adapterForClass(Class clazz) {
        return adapterManager().adapterForClass(clazz);
    }
    
    public FTreeNodeAdapter[] adaptersForClass(Class clazz) {
        return adapterManager().adaptersForClass(clazz);
    }
    
}