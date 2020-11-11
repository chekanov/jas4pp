/*
 * DefaultDropHandler.java
 *
 * Created on June 23, 2004, 1:25 PM
 */

package org.freehep.jas.plugin.plotter;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import org.freehep.jas.services.Plotter;
import org.freehep.jas.services.PlotFactory;
import org.freehep.jas.services.PlotRegion;
import org.freehep.jas.services.PlotRegionDropHandler;

/**
 *
 * @author  serbo
 */
public class DefaultPlotRegionDropHandler implements PlotRegionDropHandler {
    
    protected PlotRegion region;
    protected PlotFactory factory;
    
    /** Creates a new instance of DefaultDropHandler */
    public DefaultPlotRegionDropHandler() {
    }
    
    public DefaultPlotRegionDropHandler(PlotRegion pr, PlotFactory pf) {
        this.region = pr;
        this.factory = pf;
    }
    
    public void setPlotRegion(PlotRegion pr) {
        this.region = pr;
    }
    
    
    // DropTargetListener methods
    
    public void dragEnter(DropTargetDragEvent dtde) {
    }
    
    public void dragExit(DropTargetEvent dte) {
    }
    
    public void dragOver(DropTargetDragEvent dtde) {
    }
    
    public void drop(DropTargetDropEvent dtde) {
        try {
            Transferable t = dtde.getTransferable();
            DataFlavor[] flavors = t.getTransferDataFlavors();
            for (int i=0; i<flavors.length; i++) {
                Class k = flavors[i].getRepresentationClass();
                // Test if a plotter is available for this class
                if (Plotter.class.isAssignableFrom(k)) {
                    dtde.acceptDrop(DnDConstants.ACTION_LINK);
                    Plotter plotter = (Plotter) t.getTransferData(flavors[i]);
                    region.showPlot(plotter);
                    dtde.dropComplete(true);
                    return;
                }
            }
            for (int i=0; i<flavors.length; i++) {
                Class k = flavors[i].getRepresentationClass();
                // Test if a plotter is available for this class
                region.clear();
                Plotter plotter = factory.createPlotterFor(k);
                if (plotter != null) {
                    dtde.acceptDrop(DnDConstants.ACTION_LINK);
                    
                    //Fix to JAS-245
                    //TO-DO figure out why showPlot must be invoked before "plot"
                    region.showPlot(plotter);
                    plotter.plot(t.getTransferData(flavors[i]),plotter.NORMAL);
                    dtde.dropComplete(true);
                    return;
                }
            }
            dtde.rejectDrop();
        }
        catch (Throwable x) {
            x.printStackTrace();
            dtde.dropComplete(false);
        }
    }
    
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }
    
}
