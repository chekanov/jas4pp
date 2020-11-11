package org.freehep.jas.extension.compiler;

import java.lang.ClassLoader;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author tonyj
 * @version $Id: JASClassLoader.java 13884 2011-09-20 23:10:19Z tonyj $
 */
class JASClassLoader extends URLClassLoader
{
   private JASClassManager manager;
   private final static URL[] empty = {};
   JASClassLoader(JASClassManager manager, ClassLoader parent)
   {
      super(empty,parent);
      this.manager = manager;
   }
   protected void addURL(URL url)
   {
      super.addURL(url);
   }
   protected Class findClass(String name) throws ClassNotFoundException
   {
      Class x = super.findClass(name);
      manager.classLoaded(x);
      return x;
   }    
}