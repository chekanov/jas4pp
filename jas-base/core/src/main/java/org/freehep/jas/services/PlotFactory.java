package org.freehep.jas.services;

import java.util.List;

/**
 * This interface aims to provide a simple way for plugins to produce plots,
 * without depending on any particular plotter implementation.
 * <p>
 * This interface defines a service that creates plotters and plot pages, and keeps track
 * of existing pages. The factory can only plot objects of compatible types. Additional types
 * can be handled through adapters registered with the factory.
 * <p>
 * The default implementation of the service is provided by {@link org.freehep.jas.plugin.plotter.PlotterPlugin}.
 *
 * @author tonyj
 */
public interface PlotFactory {

  /** 
   * Returns a plotter which is able to plot the given class of data.
   * Returns <tt>null</tt> if this factory is unable to create a plotter for the specified data type.
   */
  Plotter createPlotterFor(Class dataType);

  /** 
   * Returns a plotter which is able to plot all of the given classes of data.
   * Returns <tt>null</tt> if this factory is unable to create a plotter for the specified data types.
   */
  Plotter createPlotterFor(Class[] dataTypes);

  /** Tests if can create a plotter for the given data type. */
  boolean canCreatePlotterFor(Class dataType);

  /** Tests if can create a plotter for the given data types. */
  boolean canCreatePlotterFor(Class[] dataTypes);

  /**
   * Create a page which can be used to display plots.
   *
   * @param name The name of the page, or <tt>null</tt> for a default name.
   */
  PlotPage createPage(String name);

  /** Returns the currently selected plot page, or null if no plot page is currently selected. */
  PlotPage currentPage();

  /** Returns a list of all plot pages that are showing. */
  List<PlotPage> pages();

  /**
   * Register a Plotter adapter that can convert an object of class "from" to an object of class "to".
   *
   * @param adapter The PlotterAdapter.
   * @param from The class of the objects that can be converted.
   * @param to The class to which the objects can be converted.
   *
   */
  void registerAdapter(PlotterAdapter adapter, Class from, Class to);
}
