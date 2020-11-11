package org.freehep.jas.event;

import java.util.EventObject;

/**
 * An event fired to tell the world that a new class has been loaded.
 * @author tonyj
 * @version $Id: ClassLoadedEvent.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public class ClassLoadedEvent extends ClassLoadEvent
{
   private Class x;
   public ClassLoadedEvent(Object source, Class x)
   {
      super(source);
      this.x = x;
   }
   public Class getLoadedClass()
   {
      return x;
   }
}
