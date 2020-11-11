package org.freehep.jas.services;

/**
 * A region in a plot page, where a plot can be displayed.
 */
public interface PlotRegion {

  /**
   * Show a plot in this region.
   * The specified plot replace any previous plot in this region.
   *
   * @param plotter The plot to show. 
   */
  void showPlot(Plotter plotter);

  /**
   * Clears the region
   */
  void clear();
  
  /**
   * Returns the current plot, or <tt>null</tt> if there is no plot in this region.
   */
  Plotter currentPlot();
  
}
