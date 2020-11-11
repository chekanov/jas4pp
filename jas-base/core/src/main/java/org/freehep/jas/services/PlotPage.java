package org.freehep.jas.services;

import java.awt.Component;

/**
 * Plot page.
 */
public interface PlotPage {

  /**
   * Splits this page into a grid of equally sized regions.
   * 
   * @param columns Number of columns.
   * @param rows Number of rows.
   */
  void createRegions(int columns, int rows);

  /**
   * Creates a region of the specified size at the given position.
   * All parameters are specified as fractions of the page size.
   * The newly created region is selected as current.
   * 
   * @param x X-coordinate of the bottom left corner of the region.
   * @param y Y-coordinate of the bottom left corner of the region.
   * @param w Width of the region.
   * @param h Height of the region.
   * @return The newly created region.
   */
  PlotRegion createRegion(double x, double y, double w, double h);

  /**
   * Destroys all regions.
   */
  void clearRegions();

  /**
   * Return the region specified by the index.
   * @throws IndexOutOfBoundsException exception if the specified index is invalid.
   */
  PlotRegion region(int index);

  /**
   * Returns the currently selected region on this page.
   */
  PlotRegion currentRegion();

  /**
   * Selects the next region on this page and sets it as current.
   * If the current region is the last on the page, moves to the first one.
   * 
   * @return The newly selected current region.
   */
  PlotRegion next();

  /**
   * Adds a region to this page.
   * 
   * @return The newly created region.
   */
  PlotRegion addRegion();

  /**
   * Selects the specified region as current.
   */
  void setCurrentRegion(PlotRegion reg);

  /**
   * Returns the number of regions on this page.
   */
  int numberOfRegions();

  /**
   * Method to be called by clients to request that this page is displayed in Jas3.
   */
  void showPage();

  /**
   * Method to be called by clients to request that this page is removed from Jas3.
   */
  void hidePage();

  /**
   * Returns a Component that can be used to display a PlotPage in some other
   * swing component.
   */
  Component viewable();
}
