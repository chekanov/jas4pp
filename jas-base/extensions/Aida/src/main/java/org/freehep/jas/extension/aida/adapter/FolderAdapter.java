package org.freehep.jas.extension.aida.adapter;

import java.util.List;

import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.ref.event.Connectable;
import hep.aida.ref.tree.Folder;
import hep.aida.ref.tree.Tree;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.freehep.application.studio.Studio;
import org.freehep.jas.extension.aida.AIDAPlugin;
import org.freehep.jas.plugin.tree.FTreeNode;

import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreePlugin;
import org.freehep.util.images.ImageHandler;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.util.commanddispatcher.CommandProcessor;


/**
 * The FTreeNodeAdapter for AIDA folders.
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class FolderAdapter extends DefaultFTreeNodeAdapter {

    private static final Icon nonSelectedLeafIcon = ImageHandler.getIcon("images/NonSelectedLeafNodeIcon.gif", FTreePlugin.class);
    private static final Icon selectedLeafIcon = ImageHandler.getIcon("images/SelectedLeafIcon.gif", FTreePlugin.class);
    protected AIDAPlugin plugin;
    protected Studio app;
    protected Commands commands;
    protected boolean showEmptyFolderAsLeaf;
    protected Tree aidaMasterTree;
    
    public FolderAdapter(AIDAPlugin plugin, Studio app) {
        this(100, plugin, app);
    }
    public FolderAdapter(int priority, AIDAPlugin plugin, Studio app) {
        super(priority);
        this.plugin = plugin;
        this.app = app;
        commands = new Commands();
        showEmptyFolderAsLeaf = false;
        aidaMasterTree = ( (Tree) app.getLookup().lookup(ITree.class) );
    }
    
    public void setShowEmptyFolderAsLeaf(boolean b) { showEmptyFolderAsLeaf = b; }
    public boolean getShowEmptyFolderAsLeaf() { return showEmptyFolderAsLeaf; }
    
    public void checkForChildren(FTreeNode node) {
        Object obj = node.value("folderChildrenChecked");
        if ( obj == null ) {
            node.addKey("folderChildrenChecked", new Boolean(true));
            plugin.checkForChildrenForNode( node.path() );
        }
    }
    
    public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
        commands.setPath(plugin.fullPath(nodes[0].path()));
        if ( menu.getSubElements().length != 0 ) menu.addSeparator();
        JMenuItem item = new JMenuItem("Make Current Directory");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        item = new JMenuItem("Clear All");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        item = new JMenuItem("Delete");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        return menu;
    }
    public boolean allowsChildren(FTreeNode node, boolean allowsChildren) {
        return true;
    }
    
    public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) {
        /*
        Icon icon = oldIcon;
        if (showEmptyFolderAsLeaf) {
            try {
                String objPath = plugin.fullPath(node.path());
                Object obj = aidaMasterTree.findObject(objPath);
                if (!(obj instanceof Folder) || !((Folder) obj).isFilled()) return icon;
            } catch (Exception e) {
                e.printStackTrace();
                return icon;
            }
            List list = node.childNodes();
            if (list == null || list.isEmpty()) {
                if ( selected ) return selectedLeafIcon;
                return nonSelectedLeafIcon;
            }
        }
         */
        return oldIcon;
    }
        
    public CommandProcessor commandProcessor(FTreeNode[] selectedNodes) {
        FTreeNode[] nodes = selectedNodes;
        commands.setPath(plugin.fullPath( nodes[0].path() ));
        return commands;
    }
    public class Commands extends CommandProcessor {
        private String path;
        ITree masterTree = (ITree) app.getLookup().lookup(ITree.class);
        
        public void setPath( String path ) {
            this.path = path;
        }
        
        public void onMakeCurrentDirectory() {
            masterTree.cd(path);
        }
        public void enableMakeCurrentDirectory(CommandState state) {
            state.setEnabled(!masterTree.pwd().equals(path));
        }
        public void onDelete() {
            masterTree.rmdir(path);
        }
    }
}