package org.freehep.jas.plugin.tree.utils.linkNode;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import org.freehep.application.studio.Plugin;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTree;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.FTreeNodeAddedNotification;
import org.freehep.jas.plugin.tree.FTreeNodeEvent;
import org.freehep.jas.plugin.tree.FTreeNodeListener;
import org.freehep.jas.plugin.tree.FTreeNodeRemovedNotification;
import org.freehep.jas.plugin.tree.FTreeNodeRepaintNotification;
import org.freehep.jas.plugin.tree.FTreeNodeStructureProvider;
import org.freehep.jas.plugin.tree.FTreeNodeTransferable;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.jas.plugin.tree.FTreeProvider;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.images.ImageHandler;

public class LinkNodePlugin extends Plugin {
    
    private static final Icon brokenLinkIcon = ImageHandler.getIcon("images/BrokenLink.gif", LinkNode.class);
    
    @Override
    protected void postInit() {
        FTreeProvider treeProvider = (FTreeProvider) getApplication().getLookup().lookup(FTreeProvider.class);
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter(new LinkNodeAdapter(), LinkNode.class);
    }
    
    
    class LinkNodeAdapter extends DefaultFTreeNodeAdapter {
        
        public LinkNodeAdapter() {
            super(1000);
        }
        
        public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) {
            FTreeNode linkedNode = linkedNode( node );
            Icon icon;
            if ( linkedNode != null )
                icon = node.tree().adapterForClass( linkedNode.type() ).icon( linkedNode, oldIcon, selected, expanded );
            else
                return brokenLinkIcon;
            if ( node.objectForClass(LinkNode.class) instanceof DefaultLink )
                return icon;
            return new LinkNodeIcon(icon);
        }
        
        public boolean doubleClick(FTreeNode node) {
            FTreeNode linkedNode = linkedNode( node );
            if ( linkedNode != null )
                return node.tree().adapterForClass( linkedNode.type() ).doubleClick( linkedNode );
            return false;
        }
        
        public String statusMessage(FTreeNode node, String oldMessage) {
            FTreeNode linkedNode = linkedNode( node );
            if ( linkedNode != null )
                return node.tree().adapterForClass( linkedNode.type() ).statusMessage( linkedNode, oldMessage );
            FTreePath path = ((LinkNode) node.objectForClass(LinkNode.class) ).linkedPath();
            return "Broken Link ["+path+"]";
        }
        
        public String toolTipMessage(FTreeNode node, String oldMessage) {
            FTreeNode linkedNode = linkedNode( node );
            if ( linkedNode != null )
                return node.tree().adapterForClass( linkedNode.type() ).toolTipMessage( linkedNode, oldMessage );
            FTreePath path = ((LinkNode) node.objectForClass(LinkNode.class) ).linkedPath();
            return "Broken Link ["+path+"]";
        }
        
        public String text(FTreeNode node, String oldText) {
            FTreeNode linkedNode = linkedNode( node );
            if ( linkedNode != null )
                return node.tree().adapterForClass( linkedNode.type() ).text( linkedNode, oldText );
            return null;
        }
        
        public boolean allowsChildren(FTreeNode node, boolean allowsChildren) {
            FTreeNode linkedNode = linkedNode( node );
            if ( linkedNode != null )
                return node.tree().adapterForClass( linkedNode.type() ).allowsChildren( linkedNode, allowsChildren );
            return false;
        }
        
        public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
            FTreeNode[] linkedNodes = new FTreeNode[nodes.length];
            for ( int i = 0; i < linkedNodes.length; i++ )
                linkedNodes[i] = linkedNode( nodes[0] );
            if ( linkedNodes[0] != null )
                menu = nodes[0].tree().adapterForClass( linkedNodes[0].type() ).modifyPopupMenu(linkedNodes,menu);
            return menu;
        }
        
        
        public FTreeNodeTransferable modifyTransferable( FTreeNode[] nodes, FTreeNodeTransferable transferable ) {
            FTreeNode[] linkedNodes = new FTreeNode[nodes.length];
            for ( int i = 0; i < linkedNodes.length; i++ )
                linkedNodes[i] = linkedNode( nodes[0] );
            if ( linkedNodes[0] != null )
                return nodes[0].tree().adapterForClass( linkedNodes[0].type() ).modifyTransferable(linkedNodes,transferable);
            return null;
        }
        
        public Component treeCellRendererComponent(Component component, FTreeNode node, boolean sel, boolean expanded, boolean leaf, boolean hasFocus) {
            FTreeNode linkedNode = linkedNode( node );
            if ( linkedNode != null )
                return node.tree().adapterForClass( linkedNode.type() ).treeCellRendererComponent( component, linkedNode, sel, expanded, leaf, hasFocus );
            return component;
        }
        
        public boolean mouseClicked(FTreeNode node, MouseEvent mouseEvent, Dimension dimension) {
            FTreeNode linkedNode = linkedNode( node );
            if ( linkedNode != null )
                return node.tree().adapterForClass( linkedNode.type() ).mouseClicked( linkedNode, mouseEvent, dimension );
            return false;
        }
        
        public CommandProcessor commandProcessor(FTreeNode[] selectedNodes) {
            FTreeNode[] linkedNodes = new FTreeNode[selectedNodes.length];
            for ( int i = 0; i < linkedNodes.length; i++ )
                linkedNodes[i] = linkedNode( selectedNodes[0] );
            if ( linkedNodes[0] != null )
                return selectedNodes[0].tree().adapterForClass( linkedNodes[0].type() ).commandProcessor(linkedNodes);
            return null;
        }

        /*
        public FTreeNodeStructureProvider nodeStructureProvider(FTreeNode node) {
            // Before getting the structure provider from the linked make sure to
            // invoke checkForChildren()
            checkForChildren(node);
            FTreeNode linkedNode = linkedNode( node );
            if ( linkedNode != null )
                return node.tree().adapterForClass( linkedNode.type() ).nodeStructureProvider( linkedNode );
            return null;
        }
        */
        public void checkForChildren(FTreeNode node) {
            Object obj = node.value("linkChildrenChecked");
            if ( obj == null ) {
                FTreeNode linkedNode = linkedNode( node );
                if ( linkedNode != null ) {
                    node.addKey( "linkChildrenChecked", new Boolean(true) );
                    FTreeNodeStructureProvider provider = node.tree().adapterForClass( linkedNode.type() ).nodeStructureProvider( linkedNode );
                    List child = provider.nodes();
                    int nChild = child.size();
                    if ( nChild == 0 )
                        node.tree().adapterForClass( linkedNode.type() ).checkForChildren( linkedNode );
                    else {
                        for( int i = 0; i < nChild; i++ ) {
                            FTreeNode c = (FTreeNode) child.get(i);
                            node.tree().treeChanged( new FTreeNodeAddedNotification(this, node.path().pathByAddingChild(c.path().getLastPathComponent()), new DefaultLink( c.path(), node.tree() ) ) );
                        }
                    }
                }
            }
        }
        
        // This is to be overwritten by the plugin providing the specific link
        // public boolean canTextBeChanged(FTreeNodeTextChangeEvent evt, boolean oldCanBeRenamed);
        // public boolean acceptNewText(FTreeNodeTextChangeEvent evt, boolean oldAcceptNewName);
        // public void nodeTextChanged(FTreeNodeTextChangeEvent evt);
        //public FTreeNodeObjectProvider treeNodeObjectProvider(FTreeNode node);
        
        // This cannot be overritten
        
        private FTreeNode linkedNode(FTreeNode node) {
            LinkNode link = (LinkNode) node.objectForClass( LinkNode.class );
            FTreeNode linkedNode;
            Object obj = node.value("linkedNode");
            if ( obj == null ) {
                try {
                    FTreePath path = link.linkedPath();
                    FTree tree = link.tree();
                    linkedNode = (FTreeNode) tree.findNode( path );
                    node.addKey("linkedNode", linkedNode);
                    linkedNode.parent().addFTreeNodeListener( new LinkedNodeListener( node ) );
                } catch (RuntimeException re) {
                    linkedNode = null;
                }
            } else
                linkedNode = (FTreeNode)obj;
            return linkedNode;
        }
        
        private class DefaultLink implements LinkNode {
            
            private FTreePath linkedPath;
            private FTree tree;
            
            DefaultLink( FTreePath linkedPath, FTree tree ) {
                this.linkedPath = linkedPath;
                this.tree = tree;
            }
            
            public FTreePath linkedPath() {
                return linkedPath;
            }            
            
            public FTree tree() {
                return tree;
            }
            
        }
        
        private class LinkedNodeListener implements FTreeNodeListener {
            
            private FTreeNode node;
            
            LinkedNodeListener( FTreeNode node ) {
                this.node = node;
            }
            
            public void nodeChanged(FTreeNodeEvent event) {
                FTreeNode linkedNode = (FTreeNode) node.value("linkedNode");
                if ( event.eventId() == FTreeNodeEvent.NODE_REMOVED ) {
                    if ( event.node() == linkedNode ) {
                        if ( node.objectForClass(LinkNode.class) instanceof DefaultLink ) {
                            FTreePath removedPath = node.path();
                            node.tree().treeChanged( new FTreeNodeRemovedNotification(this, removedPath ) );
                        } else {
                            node.removeKey("linkedNode");
//                            node.tree().treeChanged( new FTreeNodeRepaintNotification(this, node.path() ) );
                        }
                    }
                } else if ( event.eventId() == FTreeNodeEvent.NODE_ADDED ) {
                    if ( event.source() == linkedNode ) {
                        FTreeNode addedNode   = event.node();
                        FTreePath newNodePath = node.path();
                        newNodePath = newNodePath.pathByAddingChild( addedNode.path().getLastPathComponent() );
                        node.tree().treeChanged( new FTreeNodeAddedNotification(this, newNodePath, new DefaultLink( addedNode.path(), node.tree() ) ) );
                    }
                } else {
                }
            }
        }
    }
}
