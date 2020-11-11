package org.freehep.jas.extension.aida.adapter;

import hep.aida.IFunction;
import hep.aida.IManagedObject;
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
import org.freehep.util.images.ImageHandler;

/**
 * The FTreeNodeAdapter for IFunction.
 *
 * @author The FreeHEP team @ SLAC
 *
 */

public class FunctionAdapter extends DefaultFTreeNodeAdapter {

    private static final Icon funcionIcon = ImageHandler.getIcon("images/FunctionIcon.gif", AIDAPlugin.class);
    private Studio app;
    private AIDAPlugin thePlugin;
    private Commands commands = new Commands();
    
    public FunctionAdapter(AIDAPlugin thePlugin, Studio app) {
        super(100);
        this.app = app;
        this.thePlugin = thePlugin;
    }
    
    public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) {
        return funcionIcon;
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
        commands.setSelectedNodes(selectedNodes);
        return commands;
    }
    
    public boolean doubleClick(FTreeNode node) {
        commands.setSelectedNodes( new FTreeNode[] {node} );
        commands.onShow();
        return true;
    }
    
    public String statusMessage(FTreeNode node, String oldMessage) {
        IFunction func = (IFunction) node.objectForClass(IFunction.class);
        return "Function : dimension " + func.dimension() +", parameters "+func.numberOfParameters()+")";
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
            PlotRegion region = null;
            try {
                FTreeNode[] nodes = selectedNodes;
                IFunction func = (IFunction) nodes[0].objectForClass(IFunction.class);
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
                    plotter = pf.createPlotterFor(func.getClass());
                }
                plotter.plot(func,overlay ? plotter.OVERLAY : plotter.NORMAL);
                region.showPlot(plotter);
            }
            catch (IllegalArgumentException x) {
                // Currently can not plot function in empty region
                /*
                if (!newPage && !newPlot) {
                     region.clear();
                     show(newPage, overlay, newPlot); 
                } else app.error("Error showing plot",x);
                 */
                app.error("Error showing plot",x);
            }
        }
        
        public void onDelete() {
            ITree masterTree = (ITree) thePlugin.aidaMasterTree();
            FTreeNode[] nodes = selectedNodes;
            masterTree.rm( masterTree.findPath( (IManagedObject) nodes[0].objectForClass(IManagedObject.class) ));
        }
    }
}
