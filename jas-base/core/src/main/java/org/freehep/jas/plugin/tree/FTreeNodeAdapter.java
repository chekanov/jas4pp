package org.freehep.jas.plugin.tree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import org.freehep.util.commanddispatcher.CommandProcessor;

/**
 * An FTreeNodeAdapter defines the appearance and the behavior of FTreeNodes through its methods. 
 * This interface is to be implemented by each node adapter. A user, interested in define or changing
 * the appearance or behavior of a node can either implement this interface directly or
 * extend its default implementation {@link DefaultFTreeNodeAdapter DefaultFTreeNodeAdapter},
 * overriding the desired methods.
 *
 * FTreeNodeAdapters need to be registered with the {@link FTreeNodeAdapterRegistry FTreeNodeAdapterRegistry}, and this can be
 * done in two ways: 
 * - FTreeNodeAdapters can be registered directly with the FTreeNodeAdapterRegistry through
 * its {@link FTreeNodeAdapterRegistry#registerNodeAdapter(FTreeNodeAdapter, Class) registerNodeAdapter(FTreeNodeAdapter, Class)} method. In this
 * case, for a given FTreeNode, the FTreeNodeAdapters are selected based on the node's type, i.e. the FTreeNodeAdapter is
 * assigned to any node whose type inherits from the class for which the adapter has been registered.
 * - register an {@link FTreeNodeAdapterProvider FTreeNodeAdapterProvider} by using the FTreeNodeAdapterRegistry's method
 * {@link FTreeNodeAdapterRegistry#registerNodeAdapterProvider(FTreeNodeAdapterProvider) registerNodeAdapterProvider(FTreeNodeAdapterProvider)}. In
 * this case the adapters for a given FTreeNode are selected by asking the adapter provider for the adapters compatible with 
 * the node's type.
 *
 * If multiple adapters are present for a given FTreeNode, they are ordered by priority.
 * Depending on the specific method this ordering can either ascending or descending and the behavior can be either provided
 * by only one adapter or by the cumulative contribution of all of them (see each method's documentation for further details). 
 * When multiple adapters contribute to a given method (like for popup menus), the
 * adapters are ordered by lower to higher priority, giving the higher priority adapters
 * the possibility to override the behavior of lower priority ones.
 * If only one adapter is to provide the behavior (like for double click), the adapters
 * are ordered from the higher priority to the lower ones and the first node with non-trivial
 * behavior is used (see the method's documentation for further information on the
 * expected default behaviors and what is considered trivial).
 *
 * @author The FreeHEP team @ SLAC.
 *
 * 
 */
public interface FTreeNodeAdapter {
        
    /**
     * Returns the icon of a given node in a given selected/expanded state.
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
    public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded);
    
    /**
     * This method defines the double click behavior of the given node.
     * The trivial behavior is to do nothing and return false to specify that no behavior is provided.
     * The double click action will be provided by the adapter with the highest priority that
     * returns true.
     * This method is called only for leaves on the FTree. The behavior of folders,
     * when double clicking, cannot be changed.
     * @param node  The FTreeNode for which the doubleClick action is to be provided.
     * @return      <code>true</code> if a doubleClick behavior is provided. <code>false</code> otherwise.
     *
     */
    public boolean doubleClick(FTreeNode node);
    
    /**
     * This method is invoked when the status message is to be displayed for a given node.
     * This method allows the adapter to control the status message that applications like
     * JAS3 or WIRED display.
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
    public String statusMessage(FTreeNode node, String oldMessage);

    /**
     * This method is invoked when the tooltip is to be displayed for a given node.
     * This method allows the adapter to control the message that will be tooltip.
     * It is invoked for all the adapters contributing to the given node from the low priority one
     * to the higher priority ones.
     * Each adapter can either modify the passed message (as provided by lower priority adapters), return
     * a new message (overwriting the lower priority adapters) or return null to specify that no message
     * should be displayed (unless a higher priority node will change this).
     * The default is to return the {link #statusMessage(String) statusMessage}.
     * To avoid slowing down the rendering of the FTree the tooltip message information is cached after the
     * first time in the corresponding node. So the tooltip message cannot change unless the node's information is reset.
     * 
     * @param node       The fTreeNode for which the status message is to be provided.
     * @param oldMessage The current message as modified by previous adapters. It is never null.
     * @return           The message to be displayed on the tooltip. null if no tooltip is to be displayed.
     *
     */
    public String toolTipMessage(FTreeNode node, String oldMessage);

    /**
     * The text to be displayed next to the icon for a given FTreeNode.
     * By default the node's name will be displayed (i.e. the last part of the node's path in the FTree); 
     * alternatively the user can choose to display the text as provided by the adapter. This could be the 
     * title of the node's object or any other text.
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
    public String text(FTreeNode node, String oldText);

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
    public boolean allowsChildren(FTreeNode node, boolean allowsChildren);
    
    /**
     * Invoked when the given node is being removed from then FTree. 
     * It is invoked on all the compatible adapters going from the higher priority to the lower priority one. 
     * The adapter specific underlying substructure should be removed. 
     * @param node The FTreeNode being deleted.
     *
     */
    public void nodeBeingDeleted(FTreeNode node);
    
    /**
     * This method is invoked when a popup menu is to be displayed for a given set of nodes.
     * It is invoked on all the adapters contributing to the given nodes going from the lower priority
     * one to the higher priority ones. This way the higher priority adapters can overwrite the
     * behavior of the lower priority adapters.
     * Each adapter can contribute to the popup menu by adding a menu/menuItem to the passed menu,
     * by returning a new menu or by returning null to specify that no popup menu should be displayed.
     * The menu passed in is never null.
     * The default is to return the passed menu unchanged.
     * @param nodes The array of FTreeNode for which the popup menu is to be provided. The nodes are in
     *              the order in which they were selected.
     * @param menu  The current menu as it will be popped up if unchanged. It is never null.
     * @return      The menu as it should be popped up. If null no popup menu will be displayed.
     *
     */
    public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu);
    
    /**
     * This method is invoked when a Drag and Drop action is initiated on a set of nodes.
     * It is invoked on all the compatible adapters from the lower priority to the higher priority one.
     * Each node can add data to the FTreeNodeTransferable.
     * The default behavior is to return the passed transferable unchanged.
     * @param nodes        The set of FTreeNode being dragged. The nodes are ordered as they were selected.
     * @param transferable The FTreeNodeTransferable carrying the data of the DnD action.
     *
     */
    public FTreeNodeTransferable modifyTransferable( FTreeNode[] nodes, FTreeNodeTransferable transferable );

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
    public CommandProcessor commandProcessor(FTreeNode[] selectedNodes);
        
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
    public boolean canTextBeChanged(FTreeNodeTextChangeEvent evt, boolean oldCanBeRenamed);
    
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
    public boolean acceptNewText(FTreeNodeTextChangeEvent evt, boolean oldAcceptNewName);

    /**
     * The FTree does not change the text of a node. The change of the text has to be performed by
     * the adapter responsible for the text being changed.
     * This method is invoked from the higher priority adapter to the lower ones. The adapter responsible
     * for changing the node's text should consume the event by invoking the {@link FTreeNodeTextChangeEvent#consume() consume()} method.
     * This will end the propagation of the event to lower priority adapters.
     * @param evt The FTreeNodeTextChangeEvent containing the information of the text change.
     * 
     */
    public void nodeTextChanged(FTreeNodeTextChangeEvent evt);
    
    /**
     * Get the FTreeNodeObjectProvider for a given node.
     * The FTreeNodeObjectProvider is used to provide the objects when the FTreeNode's
     * method {@link FTreeNode#objectForClass(Class) objectForClass()} is invoked.
     * @param node The FTreeNode for which the object provider is to be returned.
     * @return     The FTreeNodeObjectProvider for this adapter.
     *             If the adapter does not provide objects, null is to be returned (the default).
     *
     */
    public FTreeNodeObjectProvider treeNodeObjectProvider(FTreeNode node);
    
    /**
     * The FTreeNodeAdapter's priority for a given tree.
     * This method is used by the FTreeLookupAdapter create a sorted list of FTreeNodeAdapters.
     * @param tree  The FTree on which the adapter will act.
     * @return The adapter priority.
     *
     */
    int priority(FTree tree);
    
    /**
     * This method is invoked when the FTree needs to know about a given FTreeNode's structure.
     * This method is invoked only once, unless the FTreeNode's information is reset.
     * This is the place where an adapter should add the node's structure by
     * notifying the FTree.
     * @param node The FTreeNode for which the sub-structure is being requested.
     *
     */
    public void checkForChildren(FTreeNode node);
    
    
    /**
     * This method is invoked on a node's adapters starting from the highest priority
     * one when the selected nodes have changed. 
     * The default selection behavior is to cancel any existing selection,
     * effectively reducing the tree to a SINGLE_SELECTION_MODEL. This method is to be
     * overwritten if a more sophisticated selection handling is required, in which case
     * true should be returned.
     * @param e     The FTreeSelectionEvent containing the information of the selection change.
     * @return      <code>true</code> if selection behavior, other than the default one, is provided. 
     *              <code>false</code> otherwise.
     *
     */
    public boolean selectionChanged(FTreeSelectionEvent e);


    /**
     * This method is invoked on all adapters, starting from the lower priority one
     * each time the corresponding FTreeNode is to be rendered in the FTree. 
     * This method should be used to modify the way the FTreeNode appears in the tree.
     * Consider that the icon and the text already have dedicated methods.
     * @param component The FTreeNode's renderer as provided by lower priority adapters.
     * @param node      The FTreeNode for which the rendering is taking place.
     * @param sel       <code>true</code> if the node is selected.
     * @param expanded  <code>true</code> if the node is expanded.
     * @param leaf      <code>true</code> if the node is a leaf.
     * @param hasFocus  <code>true</code> if the node has focus.
     * @return          The Component that will render the FTreeNode in the FTree.
     *
     */
    public Component treeCellRendererComponent(Component component, FTreeNode node, boolean sel, boolean expanded, boolean leaf, boolean hasFocus);
    
    /**
     * This method is invoked every time a mouse click event occurs on the 
     * representation of the FTreeNode. It is invoked starting from the highest priority
     * adapter, stopping when the first one with a non-trivial behavior is found, i.e.
     * the first one returning true.
     * @param node       The FTreeNode on which the mouse has been clicked.
     * @param mouseEvent The MouseEvent describing the click.
     * @param dimension  The location of the click in the FTreeNode's internal representation.
     * @return           <code>true</code> if a non trivial behavior is provided.
     *
     */
    public boolean mouseClicked(FTreeNode node, MouseEvent mouseEvent, Dimension dimension);

    /**
     * Get the FTreeNodeStructureProvider for a given node.
     * This method is to be overwritten by adapters that provide a non-default
     * node structure.
     * This method is invoked from the highest priority adapter to the lower ones.
     * The first non-null structure provider is used.
     * @param node The FTreeNode for which the FTreeNodeStructureProvider is requested.
     * @return     The FTreeNodeStructureProvider for the given node.
     *
     */
    public FTreeNodeStructureProvider nodeStructureProvider(FTreeNode node);
    
    
}
