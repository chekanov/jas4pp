package org.freehep.jas.services;

/**
 * An adapter that converts objects from source to target type for the purpose of plotting.
 * <p>
 * Registering an adapter with a {@link PlotFactory} allows that factory to plot objects
 * of the source type as long as it is capable of handling objects of the target type.
 */
public interface PlotterAdapter<T,S> {

  /**
   * Converts the specified object to the target type.
   *
   * @param source The object to be converted.
   * @return The result of the conversion.
   */
  public T adapt(S source);

}
