package org.freehep.jas.services;

/**
 * An interface to be implemented by classes that create {@link Plotter} instances.
 *
 * @author tonyj
 */
public interface PlotterProvider {

  /**
   * Returns <tt>true</tt> if the specified type is supported by plots created by this provider.
   */
  boolean supports(Class klass);

  /**
   * Creates a plot.
   */
  Plotter create();
  
}
