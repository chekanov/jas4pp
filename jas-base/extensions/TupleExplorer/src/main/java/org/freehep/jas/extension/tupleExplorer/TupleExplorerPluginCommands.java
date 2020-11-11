package org.freehep.jas.extension.tupleExplorer;

import hep.aida.ref.tuple.Tuple;
import org.freehep.util.commanddispatcher.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import java.util.*;
import hep.aida.ref.tuple.*;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.freehep.jas.plugin.tree.*;
import org.freehep.jas.extension.tupleExplorer.cut.AddCutDialog;
import org.freehep.jas.extension.tupleExplorer.cut.Cut;
import org.freehep.jas.extension.tupleExplorer.cut.CutColumn;
import org.freehep.jas.extension.tupleExplorer.plot.Plot;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection;
import org.freehep.jas.extension.tupleExplorer.jel.NewColumnDialog;
import org.freehep.jas.extension.tupleExplorer.table.MutableTupleTableModel;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.table.MutableTupleTable;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class TupleExplorerPluginCommands extends CommandProcessor {
    
    private TupleExplorerPlugin plugin;
    
    
    private java.util.List selectedColumns = new LinkedList();
    private java.util.List selectedTuples  = new LinkedList();
    private java.util.List selectedOther   = new LinkedList();
    
    private FTreeNode[] selectedNodes;
    
    public TupleExplorerPluginCommands(TupleExplorerPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void selectionChanged(FTreeSelectionEvent e) {
        selectedColumns.clear();
        selectedTuples.clear();
        selectedOther.clear();
        
        selectedNodes = e.selectedNodes();
        
        
        FTreeNode mainNode = null;
        FTreeNode[] tmpNodes = e.addedNodes();
        if ( tmpNodes == null )
            tmpNodes = selectedNodes;
        
        if ( selectedNodes != null ) {
            for ( int i = 0; i < tmpNodes.length; i++ ) {
                if ( tmpNodes[i].objectForClass(MutableTupleTree.class) != null ) {
                    mainNode = tmpNodes[i];
                    while( mainNode.parent().objectForClass(MutableTupleTree.class) != null )
                        mainNode = mainNode.parent();
                    break;
                }
            }
            
            FTree tree = e.tree();
            FTreePath mainPath = mainNode.path();
            for (int i=0; i<selectedNodes.length; i++) {
                FTreePath tmpPath = selectedNodes[i].path();
                if (!mainPath.isDescendant( tmpPath )) tree.treeChanged( new FTreeNodeSelectionNotification( this,tmpPath,FTreeNodeSelectionNotification.NODE_UNSELECTED) );
                else {
                    if ( selectedNodes[i].objectForClass(MutableTuple.class) != null )
                        selectedTuples.add(selectedNodes[i]);
                    else if ( selectedNodes[i].objectForClass(MutableTupleColumn.class) != null )
                        selectedColumns.add(selectedNodes[i]);
                    else
                        selectedOther.add(selectedNodes[i]);
                }
            }
            setChanged();
        }
    }
    
    private boolean areNodesCompatible( FTreeNode node0, FTreeNode node1 ) {
        if ( ! (node0.path().getParentPath()).isDescendant( node1.path() ) &&
        ! (node1.path().getParentPath()).isDescendant( node0.path() ) )
            return false;
        return true;
    }
    
    public AbstractProjection makeProjection( int type ) {
        MutableTupleColumn col0 = (MutableTupleColumn) ((FTreeNode)selectedColumns.get(0)).objectForClass(MutableTupleColumn.class);
        if (selectedColumns.size() == 1) {
            return TupleExplorerPluginProjectionMaker.projection1D( col0 );
        }
        else if ( selectedColumns.size() == 2 ) {
            MutableTupleColumn col1 = (MutableTupleColumn) ((FTreeNode)selectedColumns.get(1)).objectForClass(MutableTupleColumn.class);
            return TupleExplorerPluginProjectionMaker.projection2D( col0, col1, type );
        } else
            throw new RuntimeException("Cannot make a probjection of dimension "+selectedColumns.size());
    }
    
    public Plot makePlot( int plotType ) {
        AbstractProjection projection = makeProjection( plotType );
        MutableTupleTree tupTree = (MutableTupleTree) ((FTreeNode)selectedColumns.get(0)).objectForClass(MutableTupleTree.class);
        return plugin.createPlot( tupTree, projection );
    }
    
    // Profile Plots
    public void onProfile() {
    }
    
    public void onPlotProfileInCurrentRegion() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.PROFILE), false, false, false );
    }
    
    public void onPlotProfileInNewPage() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.PROFILE), true, false, false );
    }
    
    public void onPlotProfileInNewRegion() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.PROFILE), false, false, true );
    }
    
    public void onOverlayProfile() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.PROFILE), false, true, false );
    }
    
    public boolean isEnabledProfile() {
        if ( selectedColumns.size() != 2 || selectedTuples.size() != 0 || selectedOther.size() != 0 )
            return false;
        else {
            return areNodesCompatible( (FTreeNode) selectedColumns.get(0), (FTreeNode) selectedColumns.get(1) );
        }
    }
    
    public void enableProfile(CommandState state) {
        state.setEnabled( isEnabledProfile() );
    }
    
    public void enablePlotProfileInNewPage(CommandState state) {
        enableProfile(state);
    }
    public void enablePlotProfileInNewRegion(CommandState state) {
        enableProfile(state);
    }
    public void enablePlotProfileInCurrentRegion(CommandState state) {
        enableProfile(state);
    }
    public void enableOverlayProfile(CommandState state) {
        enableProfile(state);
    }
    
    // XY Plots
    public void onXYPlot() {
    }
    
    public void onPlotXYPlotInCurrentRegion() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.PROJECT_XY), false, false, false );
    }
    
    public void onPlotXYPlotInNewPage() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.PROJECT_XY), true, false, false );
    }
    
    public void onPlotXYPlotInNewRegion() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.PROJECT_XY), false, false, true );
    }
    
    public void onOverlayXYPlot() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.PROJECT_XY), false, true, false );
    }
    
    public boolean isEnabledXYPlot() {
        if ( selectedColumns.size() != 2 || selectedTuples.size() != 0 || selectedOther.size() != 0 )
            return false;
        else {
            return areNodesCompatible( (FTreeNode) selectedColumns.get(0), (FTreeNode) selectedColumns.get(1) );
        }
    }
    
    public void enableXYPlot(CommandState state) {
        state.setEnabled( isEnabledXYPlot() );
    }
    
    public void enablePlotXYPlotInNewPage(CommandState state) {
        enableXYPlot(state);
    }
    public void enablePlotXYPlotInNewRegion(CommandState state) {
        enableXYPlot(state);
    }
    public void enablePlotXYPlotInCurrentRegion(CommandState state) {
        enableXYPlot(state);
    }
    public void enableOverlayXYPlot(CommandState state) {
        enableXYPlot(state);
    }
    
    // Scatter plots
    public void onScatterPlot() {
    }
    
    public void onPlotScatterPlotInCurrentRegion() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.SCATTER_2D), false, false, false );
    }
    
    public void onPlotScatterPlotInNewPage() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.SCATTER_2D), true, false, false );
    }
    
    public void onPlotScatterPlotInNewRegion() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.SCATTER_2D), false, false, true );
    }
    
    public void onOverlayScatterPlot() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.SCATTER_2D), false, true, false );
    }
    
    public boolean isEnabledScatterPlot() {
        if ( selectedColumns.size() != 2 || selectedTuples.size() != 0 || selectedOther.size() != 0 )
            return false;
        else {
            return areNodesCompatible( (FTreeNode) selectedColumns.get(0), (FTreeNode) selectedColumns.get(1) );
        }
    }
    
    public void enableScatterPlot(CommandState state) {
        state.setEnabled( isEnabledScatterPlot() );
    }
    
    public void enablePlotScatterPlotInNewPage(CommandState state) {
        enableScatterPlot(state);
    }
    public void enablePlotScatterPlotInNewRegion(CommandState state) {
        enableScatterPlot(state);
    }
    public void enablePlotScatterPlotInCurrentRegion(CommandState state) {
        enableScatterPlot(state);
    }
    public void enableOverlayScatterPlot(CommandState state) {
        enableScatterPlot(state);
    }
    
    // Histogram plots
    public void onHistogram() {
    }
    
    public void onPlotHistogramInNewPage() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.HISTOGRAM), true, false, false );
    }
    
    public void onPlotHistogramInNewRegion() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.HISTOGRAM), false, false, true );
    }
    
    public void onOverlayHistogram() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.HISTOGRAM), false, true, false );
    }
    
    public void onPlotHistogramInCurrentRegion() {
        plugin.plotInPage( makePlot(TupleExplorerPluginProjectionMaker.HISTOGRAM), false, false, false );
    }
    
    public boolean isEnabledHistogram() {
        if ( selectedColumns.size() < 1 || selectedColumns.size() > 2 || selectedTuples.size() != 0 || selectedOther.size() != 0 )
            return false;
        else {
            if ( selectedColumns.size() == 1 ) return true;
            else if ( selectedColumns.size() == 2 ) {
                return areNodesCompatible( (FTreeNode) selectedColumns.get(0), (FTreeNode) selectedColumns.get(1) );
            }
            return false;
        }
    }
    public void enableHistogram(CommandState state) {
        state.setEnabled( isEnabledHistogram() );
    }
    public void enablePlotHistogramInNewPage(CommandState state) {
        enableHistogram(state);
    }
    public void enablePlotHistogramInNewRegion(CommandState state) {
        enableHistogram(state);
    }
    public void enablePlotHistogramInCurrentRegion(CommandState state) {
        enableHistogram(state);
    }
    public void enableOverlayHistogram(CommandState state) {
        enableHistogram(state);
    }
    
    // Cuts
    public void onAddCut() {
        Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, plugin.app);
        MutableTupleTree mutableTupleTree = (MutableTupleTree) selectedNodes[0].objectForClass(MutableTupleTree.class);
        AddCutDialog dlg = new AddCutDialog(frame, mutableTupleTree);
        dlg.pack();
        dlg.setLocationRelativeTo(plugin.app);
        dlg.setVisible(true);
        Cut cut = dlg.getCut();
//        if (cut != null) mutableTupleTree.defaultCuts().addCut(cut);
    }
    
    public void enableAddCut(CommandState state) {
        state.setEnabled(selectedNodes != null && selectedNodes.length > 0);
    }
    
    public void onDeleteCut() {
        FTreeNode node = (FTreeNode) selectedColumns.get(0);
        MutableTupleTree mutableTupleTree = (MutableTupleTree)node.objectForClass(MutableTupleTree.class);
        MutableTuple tuple = (MutableTuple) node.parent().objectForClass(MutableTuple.class);
        MutableTupleColumn column = (MutableTupleColumn) node.objectForClass(MutableTupleColumn.class);
        tuple.removeMutableTupleColumn(column);
        Cut cut = ( (CutColumn) column ).getCut();
        mutableTupleTree.defaultCuts().removeCut(cut);
    }
    
    
    public void enableDeleteCut(CommandState state) {
        state.setEnabled(isDeleteCutEnabled());
    }
    
    public boolean isDeleteCutEnabled() {
        if ( selectedColumns.size() != 1 || selectedTuples.size() != 0 || selectedOther.size() != 0 )
            return false;
        return true;
    }
    
    public void onInvertCut() {
        FTreeNode node = (FTreeNode) selectedColumns.get(0);
        MutableTupleColumn column = (MutableTupleColumn) node.objectForClass(MutableTupleColumn.class);        
        Cut cut = ( (CutColumn) column ).getCut();
        cut.invert();
    }
    
    
    public void enableInvertCut(CommandState state) {
        state.setEnabled(isInvertCutEnabled());
    }
    
    public boolean isInvertCutEnabled() {
        if ( selectedColumns.size() != 1 || selectedTuples.size() != 0 || selectedOther.size() != 0 )
            return false;
        return true;
    }
    
    public void onDisableCut() {
        FTreeNode node = (FTreeNode) selectedColumns.get(0);
        MutableTupleColumn column = (MutableTupleColumn) node.objectForClass(MutableTupleColumn.class);
        Cut cut = ( (CutColumn) column ).getCut();
        cut.setDisabled(true);
    }
    
    
    public void enableDisableCut(CommandState state) {
        state.setEnabled(isDisableCutEnabled());
    }
    
    public boolean isDisableCutEnabled() {
        if ( selectedColumns.size() != 1 || selectedTuples.size() != 0 || selectedOther.size() != 0 )
            return false;
        FTreeNode node = (FTreeNode) selectedColumns.get(0);
        MutableTupleColumn column = (MutableTupleColumn) node.objectForClass(MutableTupleColumn.class);
        Cut cut = ( (CutColumn) column ).getCut();
        if ( cut.isEnabled() ) return true;
        return false;
    }
    
    public void onEnableCut() {
        FTreeNode node = (FTreeNode) selectedColumns.get(0);
        MutableTupleColumn column = (MutableTupleColumn) node.objectForClass(MutableTupleColumn.class);
        Cut cut = ( (CutColumn) column ).getCut();
        cut.setDisabled(false);
    }
    
    
    public void enableEnableCut(CommandState state) {
        state.setEnabled(isEnableCutEnabled());
    }
    
    public boolean isEnableCutEnabled() {
        return ! isDisableCutEnabled();
    }
    
    public void onAddColumn() {
        Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, plugin.app);
        FTreeNode node = selectedNodes[0];
        MutableTuple mutableTuple = (MutableTuple) node.objectForClass(MutableTuple.class);
        MutableTupleTree mutableTupleTree = (MutableTupleTree) node.objectForClass(MutableTupleTree.class);
        JDialog dlg = new NewColumnDialog(frame,mutableTuple,mutableTupleTree);
        dlg.pack();
        dlg.setLocationRelativeTo(plugin.app);
        dlg.setVisible(true);
    }
    
    public boolean isAddColumnEnabled() {
        if ( selectedColumns.size() != 0 || selectedTuples.size() != 1 || selectedOther.size() != 0 )
            return false;
        return true;
    }
    
    public void enableAddColumn(CommandState state) {
        state.setEnabled(isAddColumnEnabled());
    }
    
    public void onTabulateTuple() {
        FTreeNode node = selectedNodes[0];
        MutableTuple mutableTuple = (MutableTuple) node.objectForClass(MutableTuple.class);
        MutableTupleTree mutableTupleTree = (MutableTupleTree) node.objectForClass(MutableTupleTree.class);
        JTable table = new MutableTupleTable(mutableTuple,mutableTupleTree,plugin.app);
    }
    
    public boolean isTabulateTupleEnabled() {
        if ( selectedColumns.size() != 0 || selectedTuples.size() != 1 || selectedOther.size() != 0 )
            return false;
        return true;
    }
    
    public void enableTabulateTuple(CommandState state) {
        state.setEnabled( isTabulateTupleEnabled() );
    }
    
    public void onTabulateSelectedColumns() {
        ArrayList cols = new ArrayList(selectedColumns.size());
        for ( int i = 0; i < selectedColumns.size(); i++ )
            cols.add(i, (MutableTupleColumn)((FTreeNode)selectedColumns.get(i)).objectForClass(MutableTupleColumn.class));
        MutableTupleTree mutableTupleTree = (MutableTupleTree) ((FTreeNode)selectedColumns.get(0)).objectForClass(MutableTupleTree.class);
        JTable table = new MutableTupleTable(mutableTupleTree.rootMutableTuple(), mutableTupleTree,cols, plugin.app);
    }
    
    public boolean isEnabledTabulateSelectedColumns() {
        if ( selectedColumns.size() == 0 || selectedTuples.size() != 0 || selectedOther.size() != 0 )
            return false;
        else
            return true;
    }
    
    public void enableTabulateSelectedColumns(CommandState state) {
        state.setEnabled( isEnabledTabulateSelectedColumns() );
    }
    
    public void onLoadTupleInMemory() {
        FTreeNode node = (FTreeNode) selectedTuples.get(0);
        MutableTupleTree mutableTupleTree = (MutableTupleTree) node.objectForClass(MutableTupleTree.class);
        try {
            mutableTupleTree.rootMutableTuple().loadTupleInMemory();
            setChanged();
        } catch (RuntimeException re) {
            plugin.getApplication().error("Failed to load tuple "+mutableTupleTree.rootMutableTuple().name()+" in memory. No more attempts will be made to load it in memory.", re);
        }
    }
    
    public boolean isLoadTupleInMemoryEnabled() {
        if ( selectedColumns.size() != 0 || selectedTuples.size() == 0 || selectedOther.size() != 0 )
            return false;
        FTreeNode node = (FTreeNode) selectedTuples.get(0);
        MutableTupleTree mutableTupleTree = (MutableTupleTree) node.objectForClass(MutableTupleTree.class);
        return ! mutableTupleTree.rootMutableTuple().isInMemory();
    }
    
    public void enableLoadTupleInMemory(CommandState state) {
        state.setEnabled( isLoadTupleInMemoryEnabled() );
    }
    
    /*
     public void onCutProperties() {
        CutSet cs = tuple.defaultCuts();
        CutDialog.show(app,cs,tuple);
    }
     */
    
    /*
    public void onColumnProperties() {
        Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class,app);
        JELColumn column = (JELColumn) selected.get(0);
        JDialog dlg = new JELColumnProperties(frame,column,tuple.getNTuple());
        dlg.pack();
        dlg.setLocationRelativeTo(app);
        dlg.show();
    }
    public void enableColumnProperties(CommandState state) {
        state.setEnabled(selected.size()==1 && selected.get(0) instanceof JELColumn);
    }
     */
    
    
    
}
