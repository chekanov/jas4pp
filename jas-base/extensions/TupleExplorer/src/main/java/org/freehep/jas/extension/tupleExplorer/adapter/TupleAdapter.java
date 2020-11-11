package org.freehep.jas.extension.tupleExplorer.adapter;

import hep.aida.ref.tuple.FTuple;
import hep.aida.ref.tuple.FTupleColumn;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.freehep.application.studio.Studio;
import org.freehep.jas.extension.tupleExplorer.TupleExplorerPlugin;
import org.freehep.jas.extension.tupleExplorer.TupleExplorerPluginCommands;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreeNodeAddedNotification;
import org.freehep.jas.plugin.tree.FTreeNodeObjectProvider;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.jas.plugin.tree.FTreeSelectionEvent;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;
import org.freehep.util.images.ImageHandler;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */

public class TupleAdapter extends DefaultFTreeNodeAdapter {
    
    private static final Icon closedFolderIcon     = ImageHandler.getIcon("images/SmallClosedFolderIcon.gif", TupleExplorerPlugin.class);
    private static final Icon openFolderIcon        = ImageHandler.getIcon("images/SmallOpenFolderIcon.gif", TupleExplorerPlugin.class);
    private static final Icon columnIcon            = ImageHandler.getIcon("images/SmallColumnIcon.gif", TupleExplorerPlugin.class);
    
    private TupleExplorerPlugin plugin;
    private TupleExplorerPluginCommands commands;
    private Studio app;
    private TupleObjectProvider objectProvider;
    
    public TupleAdapter(TupleExplorerPlugin plugin, TupleExplorerPluginCommands commands) {
        super(200);
        this.plugin = plugin;
        this.commands = commands;
        this.app = plugin.app;
        objectProvider = new TupleObjectProvider();
    }
    
    public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) {
        if ( expanded )
            return openFolderIcon;
        else
            return closedFolderIcon;
    }
    
    public void checkForChildren(FTreeNode node) {
        Object obj = node.value("tupleChildrenChecked");
        if ( obj == null ) {
            node.addKey("tupleChildrenChecked", new Boolean(true));
            //This forces to create the structure Should this be changed?
            node.objectForClass(MutableTuple.class);
        }
    }
    
    public boolean allowsChildren(FTreeNode node, boolean allowsChildren) {
        return true;
    }
    public boolean selectionChanged(FTreeSelectionEvent e) {
        FTreeNode[] nodes = e.selectedNodes();
        if ( nodes != null && nodes.length > 0 ) {
            commands.selectionChanged(e);
            return true;
        }
        return false;
    }
    public CommandProcessor commandProcessor(FTreeNode[] selectedNodes) {
        return commands;
    }
    
    public String statusMessage(FTreeNode node, String oldMessage) {
        MutableTupleTree mutableTupleTree = (MutableTupleTree)node.objectForClass(MutableTupleTree.class);
        MutableTuple tuple = mutableTupleTree.rootMutableTuple();
        String status = "Tuple "+tuple.name()+" : "+tuple.columns()+" columns "+tuple.rows()+" rows.";
        return status;
    }
    
    public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
        if ( menu.getSubElements().length != 0 ) menu.addSeparator();
        if ( commands.isTabulateTupleEnabled() ) {
            JMenuItem tabulateTupleMenu = new JMenuItem("Tabulate Tuple");
            menu.add(tabulateTupleMenu);
            app.getCommandTargetManager().add(new CommandSourceAdapter(tabulateTupleMenu));
            menu.addSeparator();
        }
        
        JMenuItem addCutMenu = new JMenuItem("Add Cut ...");
        menu.add(addCutMenu);
        app.getCommandTargetManager().add(new CommandSourceAdapter(addCutMenu));
        
        if ( commands.isAddColumnEnabled() ) {
            JMenuItem addColumnMenu = new JMenuItem("Add Column ...");
            menu.add(addColumnMenu);
            app.getCommandTargetManager().add(new CommandSourceAdapter(addColumnMenu));
        }
        
        if ( commands.isLoadTupleInMemoryEnabled() ) {
            menu.addSeparator();
            JMenuItem loadTupleInMemoryMenu = new JMenuItem("Load Tuple In Memory");
            menu.add(loadTupleInMemoryMenu);
            app.getCommandTargetManager().add(new CommandSourceAdapter(loadTupleInMemoryMenu));
        }
        
        return menu;
    }
    
    public void nodeBeingDeleted(FTreeNode node) {
        objectProvider.resetNode(node);
    }
    
    public FTreeNodeObjectProvider treeNodeObjectProvider(FTreeNode node) {
        return objectProvider;
    }
    
    private class TupleObjectProvider implements FTreeNodeObjectProvider {
        
        public Object objectForNode(FTreeNode node, Class clazz) {
            Object obj;
            if ( clazz == MutableTupleTree.class ) {
                MutableTuple mt = (MutableTuple) node.objectForClass(MutableTuple.class);
                return mt.mutableTupleTree();
            } else if ( clazz == MutableTuple.class ) {
                obj = node.value("MutableTupleObject");
                if ( obj == null ) {
                    FTreeNode parentNode = node.parent();
                    MutableTuple mutableTuple = null;
                    if ( parentNode != null )
                        mutableTuple = (MutableTuple)parentNode.value("MutableTupleObject");
                    if ( mutableTuple == null ) {
                        FTuple tuple = (FTuple)node.objectForClass(FTuple.class);
                        MutableTupleTree mutableTupleTree = new MutableTupleTree(tuple, node.path(), node);
                        plugin.registerMutableTupleTree(mutableTupleTree,node);
                        obj = mutableTupleTree.mutableTupleForPath(node.path());
                    } else {
                        obj = mutableTuple.mutableTupleTree().mutableTupleForPath(node.path());
                    }
                    node.addKey("MutableTupleObject", obj);
                    addTupleToNode((MutableTuple)obj,node);                    
                }
            } else
                obj = null;
            return obj;
        }
        
        public void resetNode(FTreeNode node) {
            node.removeKey("MutableTupleObject");
        }
        
        private void addTupleToNode( FTuple tuple, FTreeNode node ) {
            int columns = tuple.columns();
            for( int i = 0; i < columns; i++ ) {
                FTupleColumn column = tuple.columnByIndex(i);
                FTreePath columnPath = node.path().pathByAddingChild(column.name());
                if ( ((MutableTupleColumn) column).isFolder() )
                    node.tree().treeChanged( new FTreeNodeAddedNotification(this, columnPath, MutableTuple.class ) );
                else
                    node.tree().treeChanged( new FTreeNodeAddedNotification(this, columnPath, column.getClass() ) );
            }
        }        
    }
}
