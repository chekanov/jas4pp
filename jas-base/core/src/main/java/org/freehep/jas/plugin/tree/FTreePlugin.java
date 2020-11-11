package org.freehep.jas.plugin.tree;

import java.awt.*;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.DefaultCellEditor;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.TreePath;

import org.freehep.application.*;
import org.freehep.application.mdi.ManagedPage;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.studio.*;
import org.freehep.jas.plugin.basic.FileOpener;
import org.freehep.jas.plugin.xmlio.XMLPluginIO;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.util.FreeHEPLookup;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandTargetManager;
import org.freehep.util.images.ImageHandler;
import org.freehep.util.template.Template;
import org.freehep.xml.io.XMLIOManager;
import org.jdom.Element;


/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class FTreePlugin extends Plugin implements FTreeProvider, XMLPluginIO, PreferencesTopic {
    
    private static Studio app;
    
    private Set oldCommandProcessors = Collections.EMPTY_SET;
    private List eventList;
    
    private Hashtable trees  = new Hashtable();
    private Hashtable defaultJTrees = new Hashtable();
    
    private static FTreePlugin thePlugin;
    private Properties userProperties;
    private static FTreeNodeSorter defaultSorter = new SorterCreationOrder();
    
    private static final Icon openNonSelectedFolderIcon = ImageHandler.getIcon("images/SelectedOpenFolder.gif", FTreePlugin.class);
    private static final Icon closedNonSelectedFolderIcon = ImageHandler.getIcon("images/SelectedClosedFolder.gif", FTreePlugin.class);
    private static final Icon openSelectedFolderIcon = ImageHandler.getIcon("images/OpenFolder.gif", FTreePlugin.class);
    private static final Icon closedSelectedFolderIcon = ImageHandler.getIcon("images/ClosedFolder.gif", FTreePlugin.class);
    
    private static final Icon nonSelectedLeafIcon = ImageHandler.getIcon("images/NonSelectedLeafNodeIcon.gif", FTreePlugin.class);
    private static final Icon selectedLeafIcon = ImageHandler.getIcon("images/SelectedLeafIcon.gif", FTreePlugin.class);
    
    private FTreeNodeAdapterRegistry adapterRegistry;
    
    private FileOpener fileOpener;

    protected void init() throws org.xml.sax.SAXException, java.io.IOException {
        
        thePlugin = this;
        
        app = getApplication();
        userProperties = app.getUserProperties();
        
        // Add the Tree Plugin to the lookup table.
        FreeHEPLookup lookup = app.getLookup();
        lookup.add(this);
        
        lookup.add( defaultSorter );
        lookup.add( new SorterFolderFirst() );
        lookup.add( new SorterAlphabetical() );
        lookup.add( new SorterAlphaNumerical() );
        
        adapterRegistry = new DefaultFTreeNodeAdapterRegistry(this);
        
        adapterRegistry.registerNodeAdapter( new FTreeFolderNodeAdapter(), FTreeFolderNode.class );
        adapterRegistry.registerNodeAdapter( new FTreeLeafNodeAdapter(), FTreeLeafNode.class );
        
        // Add the web documentation
        Template map = new Template();
        map.set("title", "FTree");
        map.set("url", "classpath:/org/freehep/jas/plugin/tree/web/index.html");
        map.set("description", "The JAS3 navigation tree. Different plugins can add/remove/change nodes on the "+
                "FTree. The FTree also provide a common base through which different plugins can interact with each other");
        lookup.add(map, "built-in-plugins");
        
        fileOpener = (FileOpener) lookup.lookup(FileOpener.class);
        
        FTreeNodeSorterManager.startListerning();        
    }
    
    ArrayList trees() {
        return new ArrayList(trees.values());
    }
    
    
    //*****************************************//
    // Methods for the FTreeProvider interface //
    //*****************************************//
    
    public FTreeNodeAdapterRegistry treeNodeAdapterRegistry() {
        return adapterRegistry;
    }
    
    public String[] namesOfExistingTrees() {
        Set s = trees.keySet();
        String[] tNames = new String[s.size()];
        Iterator iter = s.iterator();
        int count = 0;
        while ( iter.hasNext() )
            tNames[count++] = (String) iter.next();
        return tNames;
    }
    
    public FTree tree() {
        return tree("JAS3Tree");
    }
    
    public FTree tree(String name) {
        DefaultFTree tree = (DefaultFTree)trees.get(name);
        if ( tree != null ) return tree;
        
        //Create a new FTree
        tree = new DefaultFTree(new DefaultFTreeNode(name, FTreeFolderNode.class, null));
        
        // Update the tree with the list of all the available adapers.
        ( (DefaultFTreeNodeAdapterRegistry)adapterRegistry ).addCommonAdaptersToTree(tree);
        
        trees.put(name,tree);
        DefaultJTree jTree = new DefaultJTree(tree);
        defaultJTrees.put(tree,jTree);
        ((JTree)jTree).setRootVisible(false);
        
        if (fileOpener != null) fileOpener.addDropTargetFileOpener(jTree);
        
        FTreeCellRenderer cellRenderer = new FTreeCellRenderer();
        jTree.setCellRenderer(cellRenderer);
        jTree.setCellEditor( new FTreeTreeCellEditor(jTree, cellRenderer, new FTreeCellEditor(new FTreeNodeTextField(jTree),jTree)) );
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        jTree.getSelectionModel().addTreeSelectionListener(new FTreeSelectionListener(jTree));
        FTreePane scroll = new FTreePane(jTree);
        scroll.setMinimumSize(new java.awt.Dimension(100, 100));
        app.getControlManager().openPage(scroll, name, null);
        
        jTree.addMouseListener( new FTreeMouseListener(jTree));
        
        //Drag Listener
        DragSource dragSource = DragSource.getDefaultDragSource();
        DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(jTree, //DragSource
                DnDConstants.ACTION_LINK, //specifies valid actions
                new FTreeDragGestureListener(jTree));
        
        /* Eliminates right mouse clicks as valid actions - useful especially
         * if you implement a JPopupMenu for the JTree
         */
        dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);
        
        /**
         * By default the Tree look and feel does not allow multiple selections
         * to be dragged. Calling setDragEnabled fixes this, as long as a non-null
         * transfer handler is installed, but then the TransferHandler takes over
         * the drag and drop from our custom DragGestureRecognizer. We install
         * BrokenTransferHandler, which satisfies the non-null requirement, but
         * which doesnt do anything, thus leaving our own DragGestureRecognizer to
         * handle the transfers.
         */
        jTree.setDragEnabled(true);
        jTree.setTransferHandler(new BrokenTransferHandler());
        return tree;
    }
    
    //***********************************************************************//
    
    
    static FTreePlugin plugin() {
        return thePlugin;
    }
    
    private void destroyFTree(String name) {
        throw new UnsupportedOperationException("This method has not been implemented yet; please report this to the JAS mailing list");
    }
    
    public int restore(int level, XMLIOManager manager, Element el) {
        switch (level) {
            case RESTORE_DATA:
                return RESTORE_TREE_STRUCTURE;
            case RESTORE_TREE_STRUCTURE:
                DefaultFTree tree = (DefaultFTree)tree();
                DefaultJTree jTree = (DefaultJTree) defaultJTrees.get(tree);
                List mainNodes = el.getChildren("mainNode");
                for ( int i = 0; i < mainNodes.size(); i++ ) {
                    Element mainNodeEl = (Element) mainNodes.get(i);
                    if ( mainNodeEl.getAttributeValue("isExpanded") != null )
                        jTree.setExpandedState( mainNodeEl.getAttributeValue("path") );
                    List openNodes =  mainNodeEl.getChildren("openNode");
                    for ( int j = 0; j < openNodes.size(); j++ ) {
                        Element openNodeEl = (Element) openNodes.get(j);
                        String path = openNodeEl.getAttributeValue("path");
                        jTree.setExpandedState(path);
                    }
                }
                return RESTORE_DONE;
            default :
                throw new IllegalArgumentException("Level "+level+" is not supported. Please report this problem!!");
        }
    }
    
    public void save(XMLIOManager manager, Element el) {
        DefaultFTree tree = (DefaultFTree)tree();
        Object root = tree.getRoot();
        TreePath rootPath = new TreePath( root );
        int nMainChild = tree.getChildCount( root );
        for ( int i = 0; i < nMainChild; i++ ) {
            TreePath childPath = rootPath.pathByAddingChild( tree.getChild( root, i ));
            Element mainNodeEl = new Element("mainNode");
            mainNodeEl.setAttribute("path", FTreeUtils.createTreePath(childPath).toString());
            DefaultJTree jTree = (DefaultJTree) defaultJTrees.get(tree);
            if ( jTree.isExpanded( childPath ) )  {
                mainNodeEl.setAttribute("isExpanded", "true");
                saveExpandedChild( tree, jTree, childPath, mainNodeEl );
            }
            el.addContent(mainNodeEl);
        }
    }
    
    private void saveExpandedChild( DefaultFTree tree, DefaultJTree jTree, TreePath path, Element nodeEl ) {
        Object parent = path.getLastPathComponent();
        int nChild = tree.getChildCount( parent );
        for ( int i = 0; i < nChild; i++ ) {
            TreePath childPath = path.pathByAddingChild( tree.getChild( parent, i ));
            if ( jTree.isExpanded( childPath ) ) {
                Element childEl = new Element("openNode");
                childEl.setAttribute("path", FTreeUtils.createTreePath(childPath).toString());
                nodeEl.addContent(childEl);
                saveExpandedChild( tree, jTree, childPath, nodeEl );
            }
        }
    }
    
    public boolean apply(JComponent panel) {
        ((FTreePreferencesDialog) panel).apply();
        return true;
    }
    
    public JComponent component() {
        return new FTreePreferencesDialog();
    }
    
    public String[] path() {
        return new String[] {"Navigation Tree"};
    }
    
    private class FTreePane extends JScrollPane implements ManagedPage {
        
        FTreePane( JTree jTree ) {
            super(jTree);
        }
        
        public boolean close() {
            return false;
        }
        public void pageClosed() {
        }
        public void pageDeiconized() {
        }
        public void pageDeselected() {
        }
        public void pageIconized() {
        }
        public void pageSelected() {
        }
        public void setPageContext(PageContext context) {
        }
    }
    
    
    private class FTreeMouseListener extends MouseAdapter {
        private DefaultJTree jTree;
        
        FTreeMouseListener( DefaultJTree jTree ) {
            this.jTree = jTree;
        }
        
        public void mouseClicked(MouseEvent e) {
            
            DefaultFTreeNode node = null;
            TreePath path = jTree.getSelectionPath();
            Rectangle pathBounds = jTree.getPathBounds(path);
            if ( pathBounds != null )
                if (pathBounds.contains(e.getPoint()))
                    node = (DefaultFTreeNode)path.getLastPathComponent();
            
            if (e.getClickCount() == 2) {
                if ( jTree.getSelectionCount() == 0 ) return;
                if ( jTree.getSelectionCount() != 1 )
                    throw new RuntimeException("Unexpected number of selected nodes. Please report this problem!");
                if ( node != null )
                    jTree.model().adapterManager().doubleClick(node);
            } else if ( e.getClickCount() == 1 ) {
                if ( e.getButton() == e.BUTTON1 )
                    if ( node != null ) {
                    e.translatePoint(-pathBounds.x, -pathBounds.y);
                    jTree.model().adapterManager().mouseClicked(node,e,pathBounds.getSize());
                    }
            }
        }
    }
    
    private class FTreeSelectionListener implements TreeSelectionListener {
        private DefaultJTree jTree;
        
        FTreeSelectionListener( DefaultJTree jTree ) {
            this.jTree = jTree;
        }
        
        public void valueChanged(TreeSelectionEvent e) {
            
            TreeSelectionModel model = jTree.getSelectionModel();
            
            // Create the selection-ordered list of the selected FTreeNode
            DefaultFTreeNode[] selectedNodes = null;
            if ( jTree.getSelectionCount() > 0 ) {
                TreePath[] p = model.getSelectionPaths();
                DefaultFTreeNode[] nodes = new DefaultFTreeNode[p.length];
                // In the case of a range selection (holding the SHIFT key) we have to
                // figure out if the selection is upward or downward. This is done by comparing
                // the newLeadSelectionPath to the oldLeadSelectionPath. If the are the same the
                // selection is upward.
                int start = 0;
                int end = p.length;
                if ( e.getNewLeadSelectionPath().equals( e.getOldLeadSelectionPath() ) ) {
                    start = 1 - end ;
                    end = 1;
                }
                for (int i=start; i<end; i++) {
                    int index = Math.abs(i);
                    nodes[index] = (DefaultFTreeNode) p[index].getLastPathComponent();
                }
                selectedNodes = nodes;
            }
            jTree.setSelectedNodes( selectedNodes );
            
            
            DefaultFTreeNode node = (DefaultFTreeNode) e.getPath().getLastPathComponent();
            jTree.model().adapterManager().selectionChanged(node, e, model);
            
            
            if (e.isAddedPath()) {
                //                DefaultFTreeNode node = (DefaultFTreeNode) e.getPath().getLastPathComponent();
                String message = jTree.model().adapterManager().statusMessage(node);
                if (message != null) app.setStatusMessage(message);
            } else {
            }
            // This is a bit tricky, the command processor for any adapter which has at least
            // one node in the selection path should be installed, others should be removed.
            Set newCommandProcessors = new HashSet();
            newCommandProcessors.add(jTree.commandProcessor());
            DefaultFTreeNode[] nodeSel = jTree.selectedNodes();
            if ( nodeSel != null && nodeSel.length > 0 )
                jTree.model().adapterManager().commandProcessors(nodeSel,newCommandProcessors);
            CommandTargetManager ctm = app.getCommandTargetManager();
            for (Iterator i = newCommandProcessors.iterator(); i.hasNext(); ) {
                CommandProcessor cp = (CommandProcessor) i.next();
                if (!oldCommandProcessors.contains(cp)) ctm.add(cp);
            }
            for (Iterator i = oldCommandProcessors.iterator(); i.hasNext(); ) {
                CommandProcessor cp = (CommandProcessor) i.next();
                if (!newCommandProcessors.contains(cp)) ctm.remove(cp);
            }
            oldCommandProcessors = newCommandProcessors;
        }
    }
    
    
    private class FTreeDragGestureListener implements DragGestureListener {
        private DefaultJTree jTree;
        
        FTreeDragGestureListener(DefaultJTree jTree) {
            this.jTree = jTree;
        }
        
        public void dragGestureRecognized(DragGestureEvent dge) {
            DefaultFTreeNode[] nodes = jTree.selectedNodes();
            if ( nodes != null )
                jTree.model().adapterManager().initiateDrag(nodes, dge);
        }
    }
    
    private class FTreeTreeCellEditor extends DefaultTreeCellEditor {
        
        private DefaultJTree jTree;
        
        FTreeTreeCellEditor(DefaultJTree tree, FTreeCellRenderer renderer, FTreeCellEditor editor) {
            super(tree, renderer, editor);
            this.jTree = tree;
        }
        
        public Component getTreeCellEditorComponent(JTree tree, Object value,boolean isSelected,boolean expanded,boolean leaf, int row) {
            renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true);
            return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
        }
        
        protected void startEditingTimer() {
            if(timer == null) {
                timer = new Timer(400, this);
                timer.setRepeats(false);
            }
            timer.start();
        }
        
        protected boolean canEditImmediately(EventObject event) {
            return (event == null);
        }
        
        protected boolean inHitRegion(int x, int y) {
            // Make sure that the Icon is not part of the "HitRegion"
            TreePath path = jTree.getSelectionPath();
            Rectangle pathBounds = jTree.getPathBounds(path);
            x -= pathBounds.x;
            y -= pathBounds.y;
            DefaultFTreeNode node = (DefaultFTreeNode)path.getLastPathComponent();
            return x > node.icon(jTree.isPathSelected(path), jTree.isExpanded(path)).getIconWidth();
        }
        
        public boolean isCellEditable( EventObject evtObj ) {
            boolean isEditable = super.isCellEditable(evtObj);
            TreePath path = jTree.getLeadSelectionPath();
            if ( isEditable && path != null )
                isEditable = ( (DefaultFTreeNode) path.getLastPathComponent() ).isEditable();
            return isEditable;
        }
    }
    
    private class FTreeCellEditor extends DefaultCellEditor {
        
        FTreeCellEditor(FTreeNodeTextField textField, DefaultJTree jTree) {
            super(textField);
            setClickCountToStart(1);
        }
    }
    
    private class FTreeNodeTextField extends JTextField implements ActionListener {
        
        DefaultJTree jTree;
        FTreeNodeTextField(DefaultJTree jTree) {
            addActionListener(this);
            this.jTree = jTree;
            
            // Add a focus listener to interrupt the editing when JTextField loses the focus.
            addFocusListener( new FocusAdapter() {
                public void focusLost( FocusEvent e ) {
                    FTreeNodeTextField.this.jTree.stopEditing();
                }
            });
        }
        
        public void actionPerformed(ActionEvent e) {
            String newText = getText();
            
            // Check if the node is showing the name or the text
            TreePath nodePath = jTree.getLeadSelectionPath();
            DefaultFTreeNode node = (DefaultFTreeNode) nodePath.getLastPathComponent();
            boolean isNodeShowingName = node.isShowingName();
            String oldText = node.name();
            
            FTreeNodeTextChangeEvent evt = new FTreeNodeTextChangeEvent(node, oldText, newText, isNodeShowingName);
            
            if ( isNodeShowingName ) {
                // An empty name cannot be accepted
                if ( newText.equals("") ) {
                    return;
                } else {
                    if ( ! newText.equals( oldText ) ) {
                        // Check if there is another node with a similar name.
                        DefaultFTreeNode parentNode = (DefaultFTreeNode) nodePath.getParentPath().getLastPathComponent();
                        DefaultFTreeNode newNode = parentNode.find(newText);
                        
                        if ( newNode != null )
                            return;
                    }
                }
            }
            boolean retValue = jTree.model().adapterManager().acceptNewText(evt);
            
            if ( ! retValue ) {
                getToolkit().beep();
                return;
            }
            if ( ! jTree.model().adapterManager().nodeTextChanged(evt) )
                ((DefaultFTree) jTree.getModel()).treeChanged( new FTreeNodeRenamedNotification(this, node.path(), newText) );
        }
    }
    
    
    private class FTreeCellRenderer extends DefaultTreeCellRenderer {
        FTreeCellRenderer() {
        }
        
        public java.awt.Component getTreeCellRendererComponent(JTree jTree, Object obj, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(jTree, obj, sel, expanded, leaf, row, hasFocus);
            DefaultFTreeNode node = (DefaultFTreeNode) obj;
            String text = node.name();
            setText(text);
            Icon icon = node.icon(sel,expanded);
            if ( leaf ) setLeafIcon(icon);
            else if(expanded) setOpenIcon(icon);
            else setClosedIcon(icon);
            setIcon(icon);
            return node.treeCellRendererComponent(this, sel, expanded, leaf, hasFocus);
        }
    }
    
    private class BrokenTransferHandler extends TransferHandler {
        public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        }
        protected Transferable createTransferable(JComponent c) {
            return null;
        }
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }
    
    
    private class FTreeFolderNodeAdapter extends DefaultFTreeNodeAdapter {
        
        FTreeFolderNodeAdapter() {
            super(10);
        }
        
        public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) {
            if (selected)
                if (expanded)
                    oldIcon = openSelectedFolderIcon;
                else
                    oldIcon = closedSelectedFolderIcon;
            else
                if (expanded)
                    oldIcon = openNonSelectedFolderIcon;
                else
                    oldIcon = closedNonSelectedFolderIcon;
            return oldIcon;
        }
        
        public boolean allowsChildren(FTreeNode node, boolean allowsChildren) {
            return true;
        }
    }
    
    private class FTreeLeafNodeAdapter extends DefaultFTreeNodeAdapter {
        
        FTreeLeafNodeAdapter() {
            super(10);
        }
        
        public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) {
            if ( selected )
                return selectedLeafIcon;
            return nonSelectedLeafIcon;
        }
        
        public boolean allowsChildren(FTreeNode node, boolean allowsChildren) {
            return false;
        }
    }
    
    // Sorting preferences
    private final static String FTreePLUGIN_TREE_SORTING = "org.freehep.jas.plugin.tree.treeSortingAlgorithm.";
    private final static String FTreePLUGIN_IS_SORTING_RECURSIVE = "org.freehep.jas.plugin.tree.isSortingRecursive.";
    
    String treeSortingAlgorithm(String treeName) {
        String prop = FTreePLUGIN_TREE_SORTING+treeName;
        return PropertyUtilities.getString(userProperties, prop, defaultSorter.algorithmName() );
    }
    
    void setTreeSortingAlgorithm(String treeName, String sortingAlgorithm) {
        String prop = FTreePLUGIN_TREE_SORTING+treeName;
        userProperties.setProperty(prop, sortingAlgorithm);
    }
    
    boolean isTreeSortingRecursive(String treeName) {
        String prop = FTreePLUGIN_IS_SORTING_RECURSIVE+treeName;
        return PropertyUtilities.getBoolean(userProperties, prop, true );
    }
    
    void setIsTreeSortingRecursive(String treeName, boolean isRecursive) {
        String prop = FTreePLUGIN_IS_SORTING_RECURSIVE+treeName;
        PropertyUtilities.setBoolean(userProperties,prop, isRecursive);
    }
    
}