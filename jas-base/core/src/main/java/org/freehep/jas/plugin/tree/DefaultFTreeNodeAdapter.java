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
 * At the constructor level the adapter priority is specified. This priority is used internally by each method to order
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
public class DefaultFTreeNodeAdapter implements FTreeNodeAdapter {
    
    private DefaultFTreeNode node;
    private int priority;
    private FTreeNodeObjectProvider objectProvider;
    
    /**
     * The constructor of a FTreeNodeAdapter. At construction level the adapter priority is
     * provided. The adapter priority is used by the methods to order the adapters for a given
     * node by priority, either ascending or descending.
     * @param priority The adapter priority.
     *
     */
    public DefaultFTreeNodeAdapter(int priority) {
        this(priority,null);
    }

    public DefaultFTreeNodeAdapter(int priority, FTreeNodeObjectProvider objectProvider) {
        this.priority = priority;
        this.objectProvider = objectProvider;
    }

    /**
     * Returns the icon of a given node in a given selected/expanded state.
     * This method is to be overwritten by any adapter that wants to contribute to the icon
     * for the given node.
     * This method will be invoked on all the adapters contributing to the given node from the lower
     * priority one to the higher priority. Each adapter can contribute by either modifying the icon
     * provided by the lower priority adapters, by returning a new icon or by returning null to specify that
     * the defaultIcon should be used.
     * The default is to return the passed icon unchanged.
     * To avoid slowing down the rendering of the FTree the icon for a given node's status is cached after the
     * first time in the corresponding node. So the icon for a given node's status cannot change unless the node's information is reset.
     * @param node     The FTreeNode for which the icon is to be provided.
     * @param oldIcon  The icon as provided by the lower priority adapters. It can never be null.
     * @param selected true if the node is currently selected.
     * @param expanded true if the node is currently expanded.
     * @return         The Icon for the node in the current selected/expanded state.
     *
     */
    public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) {
        return oldIcon;
    }
    
    /**
     * This method defines the double click behavior of the given node.
     * The default is to do nothing and return false to specify that no behavior is provided.
     * The double click action will be provided by the adapter with the highest priority that
     * returns true.
     * This method is called only for leaves on the FTree. The behavior of folders cannot
     * be overwritten.
     * @param node  The FTreeNode for which the doubleClick action is to be provided.
     * @return true if a doubleClick behavior is provided. false otherwise.
     *
     */
    public boolean doubleClick(FTreeNode node) {
        return false;
    }
    
    /**
     * This method is invoked when the status message is to be displayed for a given node.
     * This method allows the adapter to control the message that will be displayed on the bottom  bar of JAS3.
     * It is invoked for all the adapters contributing to the given node from the low priority one
     * to the higher priority ones.
     * Each adapter can either modify the passed message (as provided by lower priority adapters), return
     * a new message (overwriting the lower priority adapters) or return null to specify that no message
     * should be displayed (unless a higher priority node will change this).
     * The default is to return the passed message unchanged.

     * @param node       The FTreeNode for which the status message is to be provided.
     * @param oldMessage The current message as modified by previous adapters. It is never null.
     * @return           The message to be displayed on the status bar. null if no status is to be displayed.
     *
     */
    public String statusMessage(FTreeNode node, String oldMessage) {
        return oldMessage;
    }

    /**
     * This method is invoked when the tooltip is to be displayed for a given node.
     * This method allows the adapter to control the message that will be tooltip.
     * It is invoked for all the adapters contributing to the given node from the low priority one
     * to the higher priority ones.
     * Each adapter can either modify the passed message (as provided by lower priority adapters), return
     * a new message (overwriting the lower priority adapters) or return null to specify that no message
     * should be displayed (unless a higher priority node will change this).
     * The default is to return the {link FTreeAdapter#statusMessage(String) statusMessage}.
     * To avoid slowing down the rendering of the FTree the tooltip message information is cached after the
     * first time in the corresponding node. So the tooltip message cannot change unless the node's information is reset.
     * 
     * @param node       The FTreeNode for which the status message is to be provided.
     * @param oldMessage The current message as modified by previous adapters. It is never null.
     * @return           The message to be displayed on the tooltip. null if no tooltip is to be displayed.
     *
     */
    public String toolTipMessage(FTreeNode node, String oldMessage) {
        return statusMessage(node, oldMessage);
    }

    /**
     * The text to be displayed next to the icon for a given FTreeNode.
     * By default the node's name will be displayed; alternatively the user can choose to display the
     * text as provided by the adapter. This could be the title of the node's object.
     * This method allows the adapter to control the text that will be shown.
     * It is invoked for all the adapters contributing to the given node from the low priority one
     * to the higher priority ones.
     * Each adapter can either modify the passed text (as provided by lower priority adapters), return
     * a new text (overwriting the lower priority adapters) or return null to specify that no text
     * is provided.
     * The default is to return the passed text.
     * To avoid slowing down the rendering of the FTree this text information is cached after the
     * first time in the corresponding node. So the text cannot change unless the node's information is reset.
     * @param node    The FTreeNode for which the text should be provided.
     * @param oldText The current text as provided by previous adapters. It can be null.
     * @return        The text to be displayed next to the object. null if the name is to be displayed.
     *
     */
    public String text(FTreeNode node, String oldText) {
        return oldText;
    }

    /**
     * This method is invoked to check if the adapter allows the node to have a substructure.
     * It is invoked on all the compatible adapters from the lower to the higher priority.
     * Each adapter has the opportunity to change the behavior of the previous adapter by changing
     * the passed boolean.
     * The default is to returned the passed boolean unchanged.
     * @param node           The FTreeNode for which allowsChildren is invoked.
     * @param allowsChildren The returned boolean by the lower priority adapters.
     * @return               <code>true</code> if the node has a substructure.
     *
     */
    public boolean allowsChildren(FTreeNode node, boolean allowsChildren) {
        return allowsChildren;
    }    
    
    /**
     * Invoked when the given node is being removed from then FTree.
     * It is invoked on all the compatible adapters going from the higher priority to the lower priority one. 
     * It should be overwritten by each adapter in order to clean up the node.
     * The adapter specific underlying substructure should be removed. 
     * @param node The FTreeNode being deleted.
     *
     */
    public void nodeBeingDeleted(FTreeNode node) {
    }

    /**
     * This method is invoked when a popup menu is to be displayed for a given set of nodes.
     * It is invoked on all the adapters contributing to the given nodes going from the lower priority
     * one to the higher priority ones. This way the higher priority adapters can overwrite the
     * behavior of the lower priority adapters.
     * Each adapter can contribute to the popup menu by adding a menu/menuItem to the passed menu,
     * by returning a new menu or by returning null to specify that no popup menu should be displayed.
     * The menu passed in is NEVER null.
     * The default is to returned the passed menu unchanged.
     * When modifying the popup menu make sure to use the methods to access the selected nodes.
     * @param nodes The array of FTreeNode for which the popup menu is to be provided. The nodes are in
     *              the order in which they got selected.
     * @param menu  The current menu as it will be popped up if unchanged. It is NEVER null.
     * @return      The menu as it should be popped up. If null no popup menu will be displayed.
     *
     */
    public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
        return menu;
    }
    
    /**
     * This method is invoked when a DnD action is initiated on a set of nodes.
     * It is invoked on all the compatible adapters from the lower priority to the higher priority one.
     * Each node can add data to the FTreeNodeTransferable.
     * The default behavior is to return the passed transferable unchanged.
     * @param nodes        The set of FTreeNode being dragged. The nodes are ordered as they were selected.
     * @param transferable The FTreeNodeTransferable carrying the data of the DnD action.
     *
     */
    public FTreeNodeTransferable modifyTransferable( FTreeNode[] nodes, FTreeNodeTransferable transferable ) {
        return transferable;
    }

    /**
     * Invoked after each selection change to get the CommandProcessor for the selected nodes.
     * The default is to return null to specify that no CommandProcessor is provided.
     * All the compatible adapters will be invoked and each non null CommandProcessor will be installed.
     * The state of the commands provided by the CommandProcessor usually depends on the selected nodes.
     * The utility method {@link FTreeSelectionManager#selectedNodes() selectedNodes()} should be used
     * within this method to get the list of the selected FTreeNodes. Such list is not provided as
     * an argument to this method to avoid each adapter having to store such list.
     * @return The corresponding CommandProcessor.
     *
     */
    public CommandProcessor commandProcessor(FTreeNode[] selectedNodes) {
        return null;
    }
        
    /**
     * Check if the given node's text can be changed. The showing text can either be the node's name
     * or the text provided by a node adapter.
     * This method is invoked from the lower priority to the higher priority nodes. The higher
     * priority nodes can overwrite the behavior of the lower priority nodes by changing the given
     * result of earlier invocations.
     * The default is to return the given result unchanged. By default it is assumed that the 
     * node's text cannot be changed.
     * @param evt             The FTreeNodeTextChangeEvent containing the information for this change of text.
     * @param oldCanBeRenamed The result as given by lower priority adapters.
     * @return                <code>true</code> if the name of this node can be changed.
     *
     */
    public boolean canTextBeChanged(FTreeNodeTextChangeEvent evt, boolean oldCanBeRenamed) {
        return oldCanBeRenamed;
    }
    
    /**
     * Invoked when a node's text is being changed to check if the corresponding adapters accept the
     * new text. This method is invoked from the lower priority to the higher priority adapters.
     * The result from the invocation of lower priority adapters is provided. The default is to
     * return the provided result unchanged.
     * @param evt              The FTreeNodeTextChangeEvent containing the information for this change of text.
     * @param oldAcceptNewName The result of lower priority adapters.
     * @return                 <code>true</code> if the new name has been accepted.
     *
     */
    public boolean acceptNewText(FTreeNodeTextChangeEvent evt, boolean oldAcceptNewName) {
        return oldAcceptNewName;
    }

    /**
     * The FTree does not change the text of a node. The change of the text has to be performed by
     * the adapter responsible for the text being changed.
     * This method is invoked from the higher priority adapter to the lower ones. The adapter responsible
     * for changing the node's text should consume the event by invoking the FTreeNodeTextChangeEvent#consume() method.
     * This will end the propagation of the event to lower priority adapters.
     * @param evt The FTreeNodeTextChangeEvent containing the information of the text change.
     * 
     */
    public void nodeTextChanged(FTreeNodeTextChangeEvent evt) {
    }
    
    /**
     * Get the FTreeNodeObjectProvider for this adapter.
     * @return The FTreeNodeObjectProvider for this adapter.
     *         If the adapter does not provide objects, null is to be returned (the default).
     *
     */
    public FTreeNodeObjectProvider treeNodeObjectProvider(FTreeNode node) {
        return objectProvider;
    }

    
    
    
    
    
    /**
     * This method is used by the FTreeLookupAdapter create a sorted list of FTreeNodeAdapters.
     * It returns the priority of this adapter.
     * @return The adapter priority.
     *
     */
    public int priority(FTree tree) {
        return priority;
    }
    
    
    
    
    
    public void checkForChildren(FTreeNode node) {
    }
    
    
    
    /**
     * The default selection behavior is to cancel any existing selection,
     * effectively reducing the tree to a SINGLE_SELECTION_MODEL. This method is to be
     * overwritten if a more sophisticated selection handling is required, in which case
     * true should be returned.
     * @return true if selection behavior, other than the default one, is provided. false otherwise.
     */
    public boolean selectionChanged(FTreeSelectionEvent e) {
        return false;
    }



    public Component treeCellRendererComponent(Component component, FTreeNode node, boolean sel, boolean expanded, boolean leaf, boolean hasFocus) {
        return component;
    }
    
    public boolean mouseClicked(FTreeNode node, MouseEvent mouseEvent, Dimension dimension) {
        return false;
    }
    
    public FTreeNodeStructureProvider nodeStructureProvider(FTreeNode node) {
        Object obj = node.value("objectProvider");
        if ( obj == null ) {
            obj = new DefaultFTreeNodeStructureProvider();
            node.addKey("objectProvider", obj);
        }
        return (FTreeNodeStructureProvider)obj;
    }
    
}
