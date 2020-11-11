package org.freehep.jas.extension.aida.adapter;

import hep.aida.IAnalysisFactory;
import hep.aida.IManagedObject;
import hep.aida.IPlotter;
import hep.aida.IPlottable;
import hep.aida.IProfile;
import hep.aida.ITree;
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
 * The FTreeNodeAdapter for IBaseHistogram.
 *
 * @author The FreeHEP team @ SLAC
 *
 */

public class PlottableAdapter extends DefaultFTreeNodeAdapter {

    private Studio app;
    private AIDAPlugin thePlugin;
    private Commands commands = new Commands();
    
    public PlottableAdapter(AIDAPlugin thePlugin, Studio app) {
        super(150);
        this.app = app;
        this.thePlugin = thePlugin;
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
        IPlottable hist = (IPlottable) node.objectForClass(IPlottable.class);
        return "IPlottable object: "+node.path().toString();
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
                IPlottable hist = getObject();
                PlotFactory pf = (PlotFactory) app.getLookup().lookup(PlotFactory.class);
                PlotPage page =  newPage ? null : pf.currentPage();
                if (page == null) {
                    page = pf.createPage(null);
                    page.createRegions(1,1);
                    page.showPage();
                }
                region = page.currentRegion();
                if (region == null) region = page.createRegion(0,0,1,1);
                else if (newPlot) region = page.addRegion();
                Plotter plotter = region.currentPlot();
                if (plotter == null) {
                    plotter = pf.createPlotterFor(hist.getClass());
                }
                
                plotter.plot(hist,overlay ? plotter.OVERLAY : plotter.NORMAL);
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
            ITree masterTree = thePlugin.aidaMasterTree();
            masterTree.rm( masterTree.findPath( (IManagedObject) getObject()));
        }
        
        private IPlottable getObject() {
            FTreeNode[] nodes = selectedNodes;
            return (IPlottable) nodes[0].objectForClass(IPlottable.class);
        }
    }
}
