package org.freehep.jas.extension.tupleExplorer.adapter;

import javax.swing.Icon;
import org.freehep.jas.extension.tupleExplorer.TupleExplorerPlugin;
import org.freehep.jas.extension.tupleExplorer.TupleExplorerPluginCommands;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreeSelectionEvent;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.images.ImageHandler;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */

public class CutFolderAdapter extends DefaultFTreeNodeAdapter {
    
    private static final Icon cutFolderIcon = ImageHandler.getIcon("images/SmallCutFolderIcon.gif", TupleExplorerPlugin.class);
    
    private TupleExplorerPlugin plugin;
    private TupleExplorerPluginCommands commands;
    
    public CutFolderAdapter(TupleExplorerPlugin plugin, TupleExplorerPluginCommands commands) {
       super(200);
       this.plugin = plugin;
       this.commands = commands;
    }
    
    public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) {
        return cutFolderIcon;
    }  
    public boolean allowsChildren(FTreeNode node, boolean allowsChildren)
    {
       return true;
    }
    public CommandProcessor commandProcessor(FTreeNode[] selectedNodes)
    {
       return commands;
    }    

    public boolean selectionChanged(FTreeSelectionEvent e)
    {
       commands.selectionChanged(e);
       return true;
    }   
}
