package org.freehep.jas.services;

import java.awt.dnd.DropTargetListener;

/**
 * Drop target listener for Jas3 plots.
 * 
 * @author serbo
 */
public interface PlotRegionDropHandler extends DropTargetListener {

  public void setPlotRegion(PlotRegion pr);
  
}
