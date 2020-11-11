package org.freehep.jas.extension.aida.adapter;

import hep.aida.IAnalysisFactory;
import hep.aida.IBaseHistogram;
import hep.aida.ICloud;
import hep.aida.IDataPointSet;
import hep.aida.IFunction;
import hep.aida.IHistogram;
import hep.aida.IManagedObject;
import hep.aida.IPlotter;
import hep.aida.IPlotterStyle;
import hep.aida.ITree;
import hep.aida.ref.plotter.PlotterStyle;
import org.freehep.jas.services.PlotFactory;
import org.freehep.jas.services.PlotPage;
import org.freehep.jas.services.Plotter;
import org.freehep.jas.services.PlotRegion;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.freehep.application.studio.Studio;
import org.freehep.jas.extension.aida.AIDAPlugin;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapter;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.images.ImageHandler;

/**
 * The FTreeNodeAdapter for IDataPointSet.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */

public class DataPointSetAdapter extends DefaultFTreeNodeAdapter {
    
    private static final Icon dataPointSetIcon = ImageHandler.getIcon("images/DataPointSetIcon.gif", AIDAPlugin.class);
    private Studio app;
    private AIDAPlugin thePlugin;
    private Commands commands = new Commands();
    
    public DataPointSetAdapter(AIDAPlugin thePlugin, Studio app) {
        super(100);
        this.thePlugin = thePlugin;
        this.app = app;
    }
    
    public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) {
        return dataPointSetIcon;
    }
    
    public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
        commands.setSelectedNodes( nodes );
        if ( menu.getSubElements().length != 0 ) menu.addSeparator();
        JMenuItem item = new JMenuItem("Show");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        item = new JMenuItem("Overlay on Current Plot");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        item = new JMenuItem("Add to Current Plot");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        item = new JMenuItem("Show in New Plot");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        item = new JMenuItem("Show on New Page");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        menu.addSeparator();
        item = new JMenuItem("Delete");
        app.getCommandTargetManager().add(new CommandSourceAdapter(item));
        menu.add(item);
        return menu;
    }
    public CommandProcessor commandProcessor(FTreeNode[] selectedNodes) {
        commands.setSelectedNodes( selectedNodes );
        return commands;
    }
    
    public boolean doubleClick(FTreeNode node) {
        commands.setSelectedNodes( new FTreeNode[] { node } );
        commands.onShow();
        return true;
    }
    
    public String statusMessage(FTreeNode node, String oldMessage) {
        IDataPointSet dps = (IDataPointSet) node.objectForClass(IDataPointSet.class);
        if (dps instanceof IManagedObject) {
            String tmp = "Name: "+((IManagedObject) dps).name() +", type: " + ((IManagedObject) dps).type();
            return tmp;
        }
        return "IDataPointSet "+ dps.title();
    }
    
    public class Commands extends CommandProcessor {
        
        private FTreeNode[] selectedNodes;
        
        void setSelectedNodes( FTreeNode[] selectedNodes ) {
            this.selectedNodes = selectedNodes;
        }
        
        public void onShow() {
            show(false, false, false);
        }
        public void onOverlayonCurrentPlot() {
            show(false, true, false);
        }
        public void onShowinNewPlot() {
            show(false, false, true);
        }
        public void onShowonNewPage() {
            show(true, false, false);
        }
        private void show(boolean newPage, boolean overlay, boolean newPlot) {
            show(newPage, overlay, newPlot, true);
        }
        private void show(boolean newPage, boolean overlay, boolean newPlot, boolean firstTry) {
            PlotRegion region = null;
            try {
                IDataPointSet dps = (IDataPointSet) selectedNodes[0].objectForClass(IDataPointSet.class);
                PlotFactory pf = (PlotFactory) app.getLookup().lookup(PlotFactory.class);
                PlotPage page =  newPage ? null : pf.currentPage();
                if (page == null) {
                    page = pf.createPage(null);
                    page.createRegions(1, 1);
                    page.showPage();
                }
                region = page.currentRegion();
                if (region == null) region = page.createRegion(0,0,1,1);
                else if (newPlot) region = page.addRegion();
                Plotter plotter = region.currentPlot();
                if (plotter == null) {
                    plotter = pf.createPlotterFor(dps.getClass());
                }
                
                // TODO this is temporary, until we find a way to load default styles.
                plotter.plot(dps,overlay ? plotter.OVERLAY : plotter.NORMAL);
                region.showPlot(plotter);
            }
            catch (IllegalArgumentException x) {
                 if (firstTry && !newPage && !newPlot) {
                     region.clear();
                     show(newPage, overlay, newPlot, false); 
                } else app.error("Error showing plot",x);
            }
        }
        
        public void onDelete() {
            FTreeNode[] selectedNodes = thePlugin.tree().selectedNodes();
            IManagedObject obj = (IManagedObject)selectedNodes[0].objectForClass(IManagedObject.class);
            ITree masterTree = thePlugin.aidaMasterTree();
            masterTree.rm( masterTree.findPath( obj ));
        }
    }
}
