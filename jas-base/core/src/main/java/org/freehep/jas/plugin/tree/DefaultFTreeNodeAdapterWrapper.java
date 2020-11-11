package org.freehep.jas.plugin.tree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.freehep.jas.plugin.tree.FTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreeNodeTransferable;
import org.freehep.jas.plugin.tree.FTreeSelectionEvent;
import org.freehep.util.commanddispatcher.CommandProcessor;

/**
 * The manager of FTreeNodeAdapters.
 *
 * @author The FreeHEP team @ SLAC
 *
 */
class DefaultFTreeNodeAdapterWrapper implements FTreeNodeAdapter {
    
    private Class clazz;
    private List result = null;
    private FTreeNodeObjectProvider objectProvider;
    private DefaultFTreeNodeAdapterManager adapterManager;
    
    DefaultFTreeNodeAdapterWrapper(Class clazz, DefaultFTreeNodeAdapterManager adapterManager) {
        this.clazz = clazz;
        objectProvider = new ObjectProvider(this);
        this.adapterManager = adapterManager;
    }
    
    List result() {
        
        // FIXME Temporarily the line below is commented out.
        // It creates problems because the result is cached and if new adapters are
        // added they don't show up. There should be a notification process put in place.
//        if ( result == null )
            result = adapterManager.lookup().getFTreeNodeAdaptersForClass(clazz);
        return result;
    }
    
    public JPopupMenu modifyPopupMenu( FTreeNode[] nodes, JPopupMenu menu ) {
        // start from lowest priority item
        for (ListIterator i = result().listIterator(result().size()); i.hasPrevious(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.previous();
            if ( menu == null ) menu = new JPopupMenu();
            menu = adapter.modifyPopupMenu(nodes,menu);
        }
        return menu;
    }
    
    public FTreeNodeObjectProvider treeNodeObjectProvider(FTreeNode node) {
        return objectProvider;
    }
    
    public void checkForChildren( FTreeNode node ) {
        Object obj = node.value("wrapperChildrenChecked");
        if ( obj == null ) {
            for (Iterator i = result().iterator(); i.hasNext(); ) {
                FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.next();
                adapter.checkForChildren(node);
            }
            node.addKey("wrapperChildrenChecked",new Boolean(true));
        }
    }
    
    public boolean allowsChildren( FTreeNode node, boolean allowsChildren ) {
        // start from lowest priority item
        for (ListIterator i = result().listIterator(result().size()); i.hasPrevious(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.previous();
            allowsChildren = adapter.allowsChildren(node, allowsChildren);
        }
        return allowsChildren;
    }
    
    public FTreeNodeTransferable modifyTransferable(FTreeNode[] nodes, FTreeNodeTransferable transferable) {
        for (ListIterator i = result().listIterator(result().size()); i.hasPrevious(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.previous();
            transferable = adapter.modifyTransferable(nodes,transferable);
        }
        return transferable;
    }
    
    public boolean doubleClick(FTreeNode node) {
        for (Iterator i = result().iterator(); i.hasNext(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.next();
            if (adapter.doubleClick(node)) return true;
        }
        return false;
    }
    
    public boolean mouseClicked(FTreeNode node, MouseEvent mouseEvent, Dimension dimension) {
        for (Iterator i = result().iterator(); i.hasNext(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.next();
            if (adapter.mouseClicked(node, mouseEvent, dimension)) return true;
        }
        return false;
    }
    
    public String statusMessage(FTreeNode node, String message) {
        String originalMessage = message;
        // start from lowest priority item
        for (ListIterator i = result().listIterator(result().size()); i.hasPrevious(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.previous();
            if (message == null) message = originalMessage;
            message = adapter.statusMessage(node,message);
        }
        return message;
    }
    
    public String toolTipMessage(FTreeNode node, String message) {
        String originalMessage = message;
        // start from lowest priority item
        for (ListIterator i = result().listIterator(result().size()); i.hasPrevious(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.previous();
            if (message == null) message = originalMessage;
            message = adapter.toolTipMessage(node,message);
        }
        return message;
    }
    
    public String text(FTreeNode node, String text) {
        // start from lowest priority item
        for (ListIterator i = result().listIterator(result().size()); i.hasPrevious(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.previous();
            text = adapter.text(node,text);
        }
        return text;
    }
    
    public Icon icon(FTreeNode node, Icon defaultIcon, boolean selected, boolean expanded) {
        Icon icon = defaultIcon;
        // start from lowest priority item
        for (ListIterator i = result().listIterator(result().size()); i.hasPrevious(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.previous();
            if ( icon == null ) icon = defaultIcon;
            icon = adapter.icon(node, icon, selected, expanded);
        }
        return icon;
    }
    
    public CommandProcessor commandProcessor(FTreeNode[] selectedNodes) {
        for (Iterator i = result().iterator(); i.hasNext(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.next();
            CommandProcessor cp = adapter.commandProcessor(selectedNodes);
//            if (cp != null) set.add(cp);
            if ( cp != null )
                return cp;
        }
        return null;
    }
    
    public boolean selectionChanged(FTreeSelectionEvent e) {
        // The selection is behaviour is left to the node with the heighest priority that provides a
        // behaviour. If no adapter is found, the default behaviour is to cancel any existing selection,
        // effectively reducing the tree to a SINGLE_SELECTION_MODEL.
        boolean foundAdapter = false;
        Iterator iter  = result().iterator();
        while ( iter.hasNext() ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) iter.next();
            if ( adapter.selectionChanged(e) ) {
                foundAdapter = true;
                break;
            }
        }
        return foundAdapter;
    }
    
    
    public void nodeBeingDeleted( FTreeNode node ) {
        for (Iterator i = result().iterator(); i.hasNext(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.next();
            adapter.nodeBeingDeleted(node);
        }
    }
    
    public boolean canTextBeChanged(FTreeNodeTextChangeEvent evt, boolean isEditable) {
        // start from lowest priority item
        for (ListIterator i = result().listIterator(result().size()); i.hasPrevious(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.previous();
            isEditable = adapter.canTextBeChanged(evt, isEditable);
        }
        return isEditable;
    }
    
    public boolean acceptNewText(FTreeNodeTextChangeEvent evt, boolean acceptNewText) {
        // start from lowest priority item
        for (ListIterator i = result().listIterator(result().size()); i.hasPrevious(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.previous();
            acceptNewText = adapter.acceptNewText(evt, acceptNewText);
        }
        return acceptNewText;
    }
    
    public void nodeTextChanged(FTreeNodeTextChangeEvent evt) {
        // start from highest priority item
        for (Iterator i = result().iterator(); i.hasNext(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.next();
            adapter.nodeTextChanged(evt);
            if ( evt.isConsumed() ) break;
        }
    }
    
    public Component treeCellRendererComponent(Component component, FTreeNode node, boolean sel, boolean expanded, boolean leaf, boolean hasFocus) {
        // start from lowest priority item
        for (ListIterator i = result().listIterator(result().size()); i.hasPrevious(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.previous();
//        for (Iterator i = result().iterator(); i.hasNext(); ) {
//            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.next();
            component = adapter.treeCellRendererComponent(component, node, sel, expanded, leaf, hasFocus);
        }
        return component;
    }
    
    public int priority(FTree tree) {
        Iterator i = result().iterator();
        FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.next();
        return adapter.priority(tree);
    }

    public FTreeNodeStructureProvider nodeStructureProvider(FTreeNode node) {
        // start from highest priority item
        for (Iterator i = result().iterator(); i.hasNext(); ) {
            FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.next();
            FTreeNodeStructureProvider structureProvider = adapter.nodeStructureProvider(node);
            if ( structureProvider != null ) return structureProvider;
        }
        throw new RuntimeException("No structure provider available!! Please report this problem.");
    }
    
    private class ObjectProvider implements FTreeNodeObjectProvider {
        
        private DefaultFTreeNodeAdapterWrapper adapter;
        
        ObjectProvider( DefaultFTreeNodeAdapterWrapper adapter  ) {
            this.adapter = adapter;
        }
        
        public Object objectForNode( FTreeNode node, Class clazz ) {
            for (Iterator i = adapter.result().iterator(); i.hasNext(); ) {
                FTreeNodeAdapter adapter = (FTreeNodeAdapter) i.next();
                FTreeNodeObjectProvider provider = adapter.treeNodeObjectProvider(node);
                if ( provider != null ) {
                    Object object = provider.objectForNode(node,clazz);
                    if (object != null && clazz.isAssignableFrom( object.getClass() ) )
                        return object;
                }
            }
            return null;            
        }
    }    
}