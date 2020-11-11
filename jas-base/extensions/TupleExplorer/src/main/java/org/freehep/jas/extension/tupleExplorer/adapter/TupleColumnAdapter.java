package org.freehep.jas.extension.tupleExplorer.adapter;

import hep.aida.ref.tuple.FTupleColumn;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.freehep.jas.extension.tupleExplorer.TupleExplorerPlugin;
import org.freehep.jas.extension.tupleExplorer.TupleExplorerPluginCommands;
import org.freehep.jas.extension.tupleExplorer.TupleExplorerPluginProjectionMaker;
import org.freehep.jas.extension.tupleExplorer.plot.Plot;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.jas.plugin.tree.FTreeSelectionEvent;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;
import org.freehep.util.images.ImageHandler;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.application.studio.Studio;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.FTreeNodeObjectProvider;
import org.freehep.jas.plugin.tree.FTreeNodeTextChangeEvent;
import org.freehep.jas.plugin.tree.FTreeNodeTransferable;
import org.freehep.util.Value;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */

public class TupleColumnAdapter extends DefaultFTreeNodeAdapter {
    
    private static final Icon columnIcon = ImageHandler.getIcon("images/SmallColumnIcon.gif", TupleExplorerPlugin.class);
    
    private TupleExplorerPlugin plugin;
    private static TupleExplorerPluginCommands commands;
    private Studio app;
    private TupleColumnObjectProvider objectProvider;
    
    private Value value = new Value();
    
    public TupleColumnAdapter(TupleExplorerPlugin plugin, TupleExplorerPluginCommands commands) {
        super(200);
        this.plugin = plugin;
        this.commands = commands;
        app = plugin.app;
        objectProvider = new TupleColumnObjectProvider();
    }
    
    public String statusMessage(FTreeNode node, String oldMessage) {
        MutableTuple tuple = (MutableTuple)node.parent().objectForClass(MutableTuple.class);
        MutableTupleColumn column = (MutableTupleColumn) tuple.columnByName( node.path().getLastPathComponent() );
        String status = "Column "+column.name()+" : type "+column.type();
        Class type = column.type();
        if ( column.limitsCalculated() ) {
            if ( type.isPrimitive() && type != Boolean.TYPE && type != Character.TYPE ) {
                column.minValue(value);
                status += ", min "+value.getString()+", max ";
                column.maxValue(value);
                status += value.getString();
            }
        }
        return status;
    }
    
    public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) {
        return columnIcon;
    }
    
    public boolean doubleClick(FTreeNode node) {
        int doubleClick = plugin.getDoubleClick();
        
        switch (doubleClick) {
            case 0:
                commands.onPlotHistogramInCurrentRegion();
                break;
            case 1:
                commands.onPlotHistogramInNewRegion();
                break;
            case 2:
                commands.onPlotHistogramInNewPage();
                break;
            case 3:
                commands.onOverlayHistogram();
                break;
            default:
                throw new IllegalArgumentException("Illegal selection on double click. Please report this problem!");
        }
        return true;
    }
    
    public boolean allowsChildren(FTreeNode node, boolean allowsChildren) {
        return false;
    }
    
    public boolean selectionChanged(FTreeSelectionEvent e) {
        commands.selectionChanged(e);
        return true;
    }
    
    public CommandProcessor commandProcessor(FTreeNode[] selectedNodes) {
        return commands;
    }
    
    public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
        if ( menu.getSubElements().length != 0 ) menu.addSeparator();
        if ( commands.isEnabledHistogram() ) {
            JMenu histMenu = new JMenu("Histogram");
            menu.add(histMenu);
            JMenuItem item0 = new JMenuItem("Plot Histogram In Current Region");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item0));
            histMenu.add(item0);
            JMenuItem item1 = new JMenuItem("Plot Histogram In New Region");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item1));
            histMenu.add(item1);
            JMenuItem item2 = new JMenuItem("Plot Histogram In New Page");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item2));
            histMenu.add(item2);
            JMenuItem item3 = new JMenuItem("Overlay Histogram");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item3));
            histMenu.add(item3);
        }
        if ( commands.isEnabledProfile() ) {
            JMenu profMenu = new JMenu("Profile");
            menu.add(profMenu);
            JMenuItem item0 = new JMenuItem("Plot Profile In Current Region");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item0));
            profMenu.add(item0);
            JMenuItem item1 = new JMenuItem("Plot Profile In New Region");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item1));
            profMenu.add(item1);
            JMenuItem item2 = new JMenuItem("Plot Profile In New Page");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item2));
            profMenu.add(item2);
            JMenuItem item3 = new JMenuItem("Overlay Profile");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item3));
            profMenu.add(item3);
        }
        if ( commands.isEnabledScatterPlot() ) {
            JMenu scatterMenu = new JMenu("ScatterPlot");
            menu.add(scatterMenu);
            JMenuItem item0 = new JMenuItem("Plot ScatterPlot In Current Region");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item0));
            scatterMenu.add(item0);
            JMenuItem item1 = new JMenuItem("Plot ScatterPlot In New Region");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item1));
            scatterMenu.add(item1);
            JMenuItem item2 = new JMenuItem("Plot ScatterPlot In New Page");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item2));
            scatterMenu.add(item2);
            JMenuItem item3 = new JMenuItem("Overlay ScatterPlot");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item3));
            scatterMenu.add(item3);
        }
        if ( commands.isEnabledXYPlot() ) {
            JMenu scatterMenu = new JMenu("XYPlot");
            menu.add(scatterMenu);
            JMenuItem item0 = new JMenuItem("Plot XYPlot In Current Region");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item0));
            scatterMenu.add(item0);
            JMenuItem item1 = new JMenuItem("Plot XYPlot In New Region");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item1));
            scatterMenu.add(item1);
            JMenuItem item2 = new JMenuItem("Plot XYPlot In New Page");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item2));
            scatterMenu.add(item2);
            JMenuItem item3 = new JMenuItem("Overlay XYPlot");
            app.getCommandTargetManager().add(new CommandSourceAdapter(item3));
            scatterMenu.add(item3);
        }
        if ( commands.isTabulateTupleEnabled() ) {
            JMenuItem item = new JMenuItem("Tabulate Tuple");
            menu.add(item);
            app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        }
        if ( commands.isEnabledTabulateSelectedColumns() ) {
            if ( menu.getSubElements().length != 0 ) menu.addSeparator();
            JMenuItem item = new JMenuItem("Tabulate Selected Columns");
            menu.add(item);
            app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        }
        
        return menu;
    }
    
    public FTreeNodeTransferable modifyTransferable(FTreeNode[] nodes, FTreeNodeTransferable transferable) {
        if ( commands.isEnabledHistogram() ) {
            Plot plot = commands.makePlot( TupleExplorerPluginProjectionMaker.HISTOGRAM );
            transferable.addDataForClass( plot.getProjection().jas3DataSource().getClass(), plot.getProjection().dataSource() );
        }
        return transferable;
    }
    
    public void nodeBeingDeleted(FTreeNode node) {
        objectProvider.resetNode(node);
    }
    
    public FTreeNodeObjectProvider treeNodeObjectProvider(FTreeNode node) {
        return objectProvider;
    }
    
    private class TupleColumnObjectProvider implements FTreeNodeObjectProvider {
        
        public Object objectForNode(FTreeNode node, Class clazz) {
            Object obj;
            if ( clazz == MutableTupleTree.class ) {
                MutableTuple mt = ((MutableTupleColumn) node.objectForClass(MutableTupleColumn.class)).parent();
                return mt.mutableTupleTree();
            } else if ( clazz == MutableTupleColumn.class ) {
                obj = node.value("MutableTupleColumnObject");
                if ( obj == null ) {
                    FTreeNode parentNode = node.parent();
                    MutableTuple mt = (MutableTuple)parentNode.objectForClass(MutableTuple.class);
                    obj = mt.columnByName(node.path().getLastPathComponent());
                    node.addKey("MutableTupleColumnObject", obj);
                }
            } else
                obj = null;
            return obj;
        }
        
        public void resetNode(FTreeNode node) {
            node.removeKey("MutableTupleColumnObject");
        }
    }
}
