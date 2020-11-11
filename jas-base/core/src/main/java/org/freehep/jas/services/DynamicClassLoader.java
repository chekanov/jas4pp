package org.freehep.jas.services;

/**
 * Provides access to the ClassLoader used to dynamically load (analysis)
 * classes into JAS3.
 * @author  tonyj
 */
public interface DynamicClassLoader
{
   ClassLoader getClassLoader();
}
