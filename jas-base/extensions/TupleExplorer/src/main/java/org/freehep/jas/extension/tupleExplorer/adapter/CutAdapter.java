package org.freehep.jas.extension.tupleExplorer.adapter;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.freehep.jas.extension.tupleExplorer.cut.Cut;
import org.freehep.jas.extension.tupleExplorer.cut.CutDialog;
import org.freehep.jas.extension.tupleExplorer.cut.CutColumn;
import org.freehep.jas.extension.tupleExplorer.TupleExplorerPlugin;
import org.freehep.jas.extension.tupleExplorer.TupleExplorerPluginCommands;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreeNodeObjectProvider;
import org.freehep.jas.plugin.tree.FTreeSelectionEvent;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;
import org.freehep.util.images.ImageHandler;


/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */

public class CutAdapter extends DefaultFTreeNodeAdapter {
    
    private static final Icon cutFolderIcon     = ImageHandler.getIcon("images/SmallCutIcon.gif", TupleExplorerPlugin.class);
    private TupleExplorerPlugin plugin;
    private TupleExplorerPluginCommands commands;
    private CutColumnObjectProvider objectProvider;
    
    public CutAdapter(TupleExplorerPlugin plugin, TupleExplorerPluginCommands commands) 
    {
       super(250);
       this.plugin = plugin;
       this.commands = commands;
    }
    
    public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) 
    {
        return cutFolderIcon;
    }
        
    public boolean doubleClick(FTreeNode node) {
        Cut cut = ((CutColumn) node.objectForClass( CutColumn.class )).getCut();
        CutDialog.show(plugin.app,cut,(MutableTupleTree)node.objectForClass(MutableTupleTree.class) );
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
    
    public String statusMessage(FTreeNode node, String oldMessage)
    {
        Cut cut = ((CutColumn) node.objectForClass(CutColumn.class)).getCut();
        String status = "Cut "+cut.getName()+" : ";
        if ( cut.isEnabled() ) status += "enabled";
        else status += "disabled";
        if ( cut.isInverted() ) status += ", inverted";
        status += ".";
        return status;
   }

    public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
        if ( menu.getSubElements().length != 0 ) menu.addSeparator();
        if ( commands.isEnableCutEnabled() ) {
            JMenuItem enableCutItem = new JMenuItem("Enable Cut");
            menu.add(enableCutItem);
            plugin.getApplication().getCommandTargetManager().add(new CommandSourceAdapter(enableCutItem));
        }
        if ( commands.isDisableCutEnabled() ) {
            JMenuItem disableCutItem = new JMenuItem("Disable Cut");
            menu.add(disableCutItem);
            plugin.getApplication().getCommandTargetManager().add(new CommandSourceAdapter(disableCutItem));
        }
        if ( commands.isInvertCutEnabled() ) {
            JMenuItem invertCutItem = new JMenuItem("Invert Cut");
            menu.add(invertCutItem);
            plugin.getApplication().getCommandTargetManager().add(new CommandSourceAdapter(invertCutItem));
        }
        if ( commands.isDeleteCutEnabled() ) {
            JMenuItem deleteCutItem = new JMenuItem("Delete Cut");
            menu.add(deleteCutItem);
            plugin.getApplication().getCommandTargetManager().add(new CommandSourceAdapter(deleteCutItem));
        }
        menu.addSeparator();
        return menu;
    }

    public FTreeNodeObjectProvider treeNodeObjectProvider(FTreeNode node) {
        return objectProvider;
    }
    
    private class CutColumnObjectProvider implements FTreeNodeObjectProvider {
        
        public Object objectForNode(FTreeNode node, Class clazz) {
            Object obj;
            if ( clazz == MutableTupleTree.class ) {
                MutableTuple mt = ((MutableTupleColumn) node.objectForClass(MutableTupleColumn.class)).parent();
                return mt.mutableTupleTree();
            } else if ( clazz == MutableTupleColumn.class || clazz == CutColumn.class ) {
                obj = node.value("CutColumnObject");
                if ( obj == null ) {
                    FTreeNode parentNode = node.parent();
                    MutableTuple mt = (MutableTuple)parentNode.objectForClass(MutableTuple.class);
                    obj = mt.columnByName(node.path().getLastPathComponent());
                    node.addKey("CutColumnObject", obj);
                }
            } else
                obj = null;
            return obj;
        }
        
        public void resetNode(FTreeNode node) {
            node.removeKey("CutColumnObject");
        }
    }    

}
