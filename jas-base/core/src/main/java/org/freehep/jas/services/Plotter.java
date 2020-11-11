package org.freehep.jas.services;

import java.awt.Component;
import java.util.List;

/**
 * A plot that can display compatible objects.
 */
public interface Plotter {

  public final int NORMAL = 0;
  public final int OVERLAY = 1;
  public final int ADD = 2;
  public final int STACK = 3;

  /**
   * Displays the specified object in this plot.
   *
   * @param data The data to be plotted.
   * @param mode One of (NORMAL, OVERLAY, ADD, STACK).
   * 
   * @throws UnsupportedOperationException if the specified data object is not of a compatible type.
   */
  void plot(Object data, int mode);

  /**
   * Displays the specified object in this plot.
   * Interpretation of style and options is implementation dependent.
   *
   * @param data The data to be plotted.
   * @param mode One of (NORMAL, OVERLAY, ADD, STACK).
   * @param style Style that affects the display of the data. 
   * @param options Additional options that affect the display of the data.
   * 
   * @throws UnsupportedOperationException if the specified data object is not of a compatible type.
   */
  void plot(Object data, int mode, Object style, String options);

  /**
   * Removes a data item from this plot.
   * Removing a non-existing item has no effect.
   */
  void remove(Object data);

  /**
   * Removes all data from this plot.
   */
  void clear();

  /**
   * Tests if the plotter can plot the given datatype
   */
   //boolean canPlot(Object data, int options);

  /**
   * Returns a component that can be used to display this plot in some other swing component.
   */
  Component viewable();

  /**
   * Returns the list of data objects added to this plot.
   */
  List<Object> getData();
}
