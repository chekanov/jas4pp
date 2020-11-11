package org.freehep.jas.plugin.tree;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragGestureEvent;
import java.awt.event.MouseEvent;
import org.freehep.util.FreeHEPLookup;
import java.util.*;
import java.util.Hashtable;
import java.util.List;
import javax.swing.*;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.freehep.application.studio.Studio;
import org.freehep.jas.plugin.tree.FTreeNodeStructureProvider;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.jas.plugin.tree.FTreeSelectionEvent;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;

/**
 * The manager of FTreeNodeAdapters.
 * Each tree has a DefaultFTreeNodeAdapterManager as different trees can have
 * different adapters.
 *
 * @author The FreeHEP team @ SLAC
 *
 */
class DefaultFTreeNodeAdapterManager {
    
    private DefaultFTreeNodeAdapterLookup lookup;
    private final Object NOOBJECT = null;
    private final String EMPTY_STRING = new String();
    private Hashtable adapters = new Hashtable();
    private FTree tree;

    DefaultFTreeNodeAdapterManager(FTree tree) {
        this.tree = tree;
        lookup = new DefaultFTreeNodeAdapterLookup(tree);
    }
    
    DefaultFTreeNodeAdapterLookup lookup() {
        return lookup;
    }
    
    private DragSourceAdapter dragAdapter = new DragSourceAdapter() {
        public void dragEnter(DragSourceDragEvent dsde) {
            dsde.getDragSourceContext().setCursor(DragSource.DefaultLinkDrop);
        }
        
        public void dragExit(DragSourceEvent dse) {
            dse.getDragSourceContext().setCursor(DragSource.DefaultLinkNoDrop);
        }
    };
    
    void registerNodeAdapterProvider(FTreeNodeAdapterProvider provider) {
        lookup.registerFTreeNodeAdapterProvider( provider );
    }
    
    void registerNodeAdapter( FTreeNodeAdapter adapter, Class clazz ) {
        registerNodeAdapterProvider( new DefaultFTreeNodeAdapterProvider( clazz, adapter ) );
    }
    
    void registerObjectProvider( FTreeNodeObjectProvider provider, Class clazz, int priority ) {
        registerNodeAdapter( new DefaultFTreeNodeAdapter(priority, provider), clazz );
    }
        
    FTreeNodeAdapter[] adaptersForClass(Class clazz) {
        List adapters = lookup.getFTreeNodeAdaptersForClass(clazz);
        FTreeNodeAdapter[] result = new FTreeNodeAdapter[ adapters.size() ];
        for ( int i = 0; i < result.length; i++ )
            result[i] = (FTreeNodeAdapter) adapters.get(i);
        return result;
    }
    
    FTreeNodeAdapter adapterForClass(Class clazz) {
        FTreeNodeAdapter adapter = (FTreeNodeAdapter) adapters.get(clazz);
        if ( adapter == null ) {
            adapter = new DefaultFTreeNodeAdapterWrapper(clazz,this);
            adapters.put(clazz,adapter);
        }
        return adapter;
    }
    
    JPopupMenu modifyPopupMenu( DefaultFTreeNode[] nodes, JPopupMenu menu ) {
        FTreeNodeAdapter[] adapters = adaptersForClass( nodes[0].type() );
        for ( int i = adapters.length - 1; i > -1; i-- ) {
            menu = adapters[i].modifyPopupMenu(nodes, menu);
        }
        
        if ( menu == null ) menu = new JPopupMenu();
        if ( menu.getComponentCount() > 0 && ! ( menu.getComponent( menu.getComponentCount()-1 ) instanceof JSeparator ) )
            menu.addSeparator();
        
        JMenuItem renameMenuItem = new JMenuItem("Rename");
        menu.add(renameMenuItem);
        Studio.getApplication().getCommandTargetManager().add(new CommandSourceAdapter(renameMenuItem));
        for ( int i = 0; i < nodes.length; i++ ) {
            if ( nodes[i].getAllowsChildren() ) {
                JMenuItem sortingMenuItem = new JMenuItem("Sorting ...");
                menu.add(sortingMenuItem);
                Studio.getApplication().getCommandTargetManager().add(new CommandSourceAdapter(sortingMenuItem));
                break;
            }
        }
        return menu;
    }
    
    Object userObjectForNode(DefaultFTreeNode node, Class clazz ) {
        if ( node.isRoot() ) return NOOBJECT;
        FTreeNodeObjectProvider provider = adapterForClass(node.type()).treeNodeObjectProvider(node);
        return provider.objectForNode(node,clazz);
    }
    
    void checkForChildren( DefaultFTreeNode node ) {
        Object obj = node.value("managerChildrenChecked");
        if ( obj == null ) {
            node.addKey("managerChildrenChecked", new Boolean(true));
            adapterForClass( node.type() ).checkForChildren(node);
        }
    }
    
    boolean allowsChildren( DefaultFTreeNode node ) {
        return adapterForClass( node.type() ).allowsChildren(node, false);
    }
    
    void initiateDrag(DefaultFTreeNode[] nodes, DragGestureEvent dge) {
        FTreeNodeTransferable transferable = new FTreeNodeTransferable(nodes);
        Cursor cursor = DragSource.DefaultLinkNoDrop;
        transferable = adapterForClass(nodes[0].type()).modifyTransferable(nodes, transferable);
        dge.getDragSource().startDrag(dge, cursor, transferable, dragAdapter);
    }
    
    boolean doubleClick(DefaultFTreeNode node) {
        if ( ! node.isLeaf() ) return false;
        return adapterForClass(node.type()).doubleClick(node);
    }
    
    boolean mouseClicked(DefaultFTreeNode node, MouseEvent mouseEvent, Dimension dimension) {
        return adapterForClass(node.type()).mouseClicked(node, mouseEvent, dimension);
    }
    
    String statusMessage(DefaultFTreeNode node) {
        String message = adapterForClass(node.type()).statusMessage(node, EMPTY_STRING);
        if ( message.equals(EMPTY_STRING) )
            return null;
        return message;
    }
    
    String toolTipMessage(DefaultFTreeNode node) {
        String message = adapterForClass(node.type()).toolTipMessage(node, EMPTY_STRING);
        if ( message.equals(EMPTY_STRING) )
            return null;
        return message;
    }
    
    String text(DefaultFTreeNode node) {
        if ( node.isRoot() ) return null;
        return adapterForClass(node.type()).text(node,null);
    }
    
    Icon icon(DefaultFTreeNode node, Icon defaultIcon, boolean selected, boolean expanded) {
        return adapterForClass(node.type()).icon(node, defaultIcon, selected, expanded);
    }
    
    FTreeNodeStructureProvider nodeStructureProvider(FTreeNode node) {
        FTreeNodeStructureProvider structureProvider = (FTreeNodeStructureProvider) node.value("structureProvider");
        if ( structureProvider == null ) {
            structureProvider = adapterForClass(node.type()).nodeStructureProvider(node);
            node.addKey("structureProvider", structureProvider);
        }
        return structureProvider;
    }
    
    void commandProcessors(FTreeNode[] nodes, Set set) {
        FTreeNodeAdapter[] adapters = adaptersForClass( nodes[0].type() );
        for ( int i = 0; i < adapters.length; i++ ) {
            CommandProcessor cp = adapters[i].commandProcessor(nodes);
        if ( cp != null )
            set.add( cp );
        }
    }
    
    void selectionChanged(DefaultFTreeNode node, TreeSelectionEvent e, TreeSelectionModel model) {
        FTreeSelectionEvent selEvent = new FTreeSelectionEvent(e, node.tree());
        
        if ( ! adapterForClass(node.type()).selectionChanged(selEvent) ) {
            if (e.isAddedPath()) {
                int n = model.getSelectionCount();
                if (n > 0) {
                    TreePath[] paths = model.getSelectionPaths();
                    for (int i=0; i<paths.length; i++) {
                        if (!paths[i].equals( e.getNewLeadSelectionPath() ))
                            model.removeSelectionPath(paths[i]);
                    }
                }
            }
        }
    }
    
    
    void closeNode( DefaultFTreeNode node ) {
        List children = node.structureProvider().nodes();
        for ( int i = 0; i < children.size(); i++ ) {
            DefaultFTreeNode child = (DefaultFTreeNode) children.get(i);
            closeNode( child );
        }
        adapterForClass(node.type()).nodeBeingDeleted(node);
    }
    
    boolean isNodeEditable(FTreeNodeTextChangeEvent evt) {
        return adapterForClass( evt.node().type() ).canTextBeChanged(evt, false);
    }
    
    boolean acceptNewText(FTreeNodeTextChangeEvent evt) {
        return adapterForClass( evt.node().type() ).acceptNewText( evt, true );
    }
    
    boolean nodeTextChanged(FTreeNodeTextChangeEvent evt) {
        adapterForClass( evt.node().type() ).nodeTextChanged(evt);
        return evt.isConsumed();
    }
    
    Component treeCellRendererComponent(Component component, FTreeNode node, boolean sel, boolean expanded, boolean leaf, boolean hasFocus) {
        return adapterForClass( node.type() ).treeCellRendererComponent(component, node, sel, expanded, leaf, hasFocus);
    }
    
    
}