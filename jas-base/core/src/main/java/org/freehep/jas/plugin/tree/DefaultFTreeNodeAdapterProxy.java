package org.freehep.jas.plugin.tree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import org.freehep.util.commanddispatcher.CommandProcessor;

/**
 * A FTreeNodeAdapter define the behavior of FTreeNodes through its methods. This class is
 * the base class for each node adapter; each node adapter must extend this base class. The methods below 
 * provide the default behavior for nodes on FTree and have to be overwritten if a different behavior is desired.
 *
 * A FTreeNodeAdapter has to be registered with the FTree via its method
 * {link FTree#registerFTreeNodeAdapter(FTreeNodeAdapter,Class) registerFTreeNodeAdapter()} by providing a Class.
 * This Class is used to assign the given FTreeNodeAdapter to FTreeNodes by inheritance. So, if an adapter is registered
 * for class A and on the FTree there is a node of type B that inherits from A, the behavior of the node B will be handled 
 * by the adapter registered for class A.
 *
 * At the constructor leven the adapter priority is specified. This priority is used internally by each method to order
 * the adapters in the case when there are multiple adapters contributing to the behavior of a given node. Depending on the method
 * this ordering can either be from the lower to the higher priority or from the higher to the lower priority.
 * Depending on the method the node's behavior will either be provided by all the adapters in the priority-sorted list or by
 * only one of such adapters. In the first case (like for popup menus) the ordering will be from low to high priority, giving
 * the higher priority adapters a chance to overwrite the lower priority adapters. In the second case (like for double click)
 * the ordering will be from high to low priority and the behavior will be given by only one adapter: the first one in
 * such list that overwrites the default behavior (see the method's documentation for more details).
 *
 * @author The FreeHEP team @ SLAC.
 *
 * 
 */
public class DefaultFTreeNodeAdapterProxy implements FTreeNodeAdapter {
    
    private Class clazz;
    
    public DefaultFTreeNodeAdapterProxy(Class clazz) {
        this.clazz = clazz;
    }

    public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) {
        return node.tree().adapterForClass(clazz).icon(node, oldIcon, selected, expanded);
    }
    
    public boolean doubleClick(FTreeNode node) {
        return node.tree().adapterForClass(clazz).doubleClick(node);
    }
    public String statusMessage(FTreeNode node, String oldMessage) {
        return node.tree().adapterForClass(clazz).statusMessage(node, oldMessage);
    }

    public String toolTipMessage(FTreeNode node, String oldMessage) {
        return node.tree().adapterForClass(clazz).toolTipMessage(node, oldMessage);
    }

    public String text(FTreeNode node, String oldText) {
        return node.tree().adapterForClass(clazz).text(node, oldText);
    }

    public boolean allowsChildren(FTreeNode node, boolean allowsChildren) {
        return node.tree().adapterForClass(clazz).allowsChildren(node, allowsChildren);
    }    
        
    public void nodeBeingDeleted(FTreeNode node) {
        node.tree().adapterForClass(clazz).nodeBeingDeleted(node);
    }

    public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
        return menu;
    }
    
    public FTreeNodeTransferable modifyTransferable( FTreeNode[] nodes, FTreeNodeTransferable transferable ) {
        return transferable;
    }

    public CommandProcessor commandProcessor(FTreeNode[] selectedNodes) {
        return selectedNodes[0].tree().adapterForClass(clazz).commandProcessor(selectedNodes);
    }
    
    public boolean canTextBeChanged(FTreeNodeTextChangeEvent evt, boolean oldCanBeRenamed) {
        return evt.node().tree().adapterForClass(clazz).canTextBeChanged(evt, oldCanBeRenamed);
    }
    
    public boolean acceptNewText(FTreeNodeTextChangeEvent evt, boolean oldAcceptNewName) {
        return evt.node().tree().adapterForClass(clazz).acceptNewText(evt, oldAcceptNewName);
    }

    public void nodeTextChanged(FTreeNodeTextChangeEvent evt) {
        evt.node().tree().adapterForClass(clazz).nodeTextChanged(evt);
    }
    
    public int priority(FTree tree) {
        return tree.adapterForClass(clazz).priority(tree);
    }
    
    public void checkForChildren(FTreeNode node) {
        node.tree().adapterForClass(clazz).checkForChildren(node);
    }
    
    public boolean selectionChanged(FTreeSelectionEvent e) {
        return e.tree().adapterForClass(clazz).selectionChanged(e);
    }

    public Component treeCellRendererComponent(Component component, FTreeNode node, boolean sel, boolean expanded, boolean leaf, boolean hasFocus) {
        return node.tree().adapterForClass(clazz).treeCellRendererComponent(component, node, sel, expanded, leaf, hasFocus);
    }
    
    public boolean mouseClicked(FTreeNode node, MouseEvent mouseEvent, Dimension dimension) {
        return node.tree().adapterForClass(clazz).mouseClicked(node, mouseEvent, dimension);
    }

    public FTreeNodeObjectProvider treeNodeObjectProvider(FTreeNode node) {
        return node.tree().adapterForClass(clazz).treeNodeObjectProvider(node);
    }    
    
    public FTreeNodeStructureProvider nodeStructureProvider(FTreeNode node) {
        return node.tree().adapterForClass(clazz).nodeStructureProvider(node);
    }
    
}
