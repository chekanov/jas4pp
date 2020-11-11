package org.freehep.jas.event;

import java.util.EventObject;

/**
 * This is a base class for ClassLoadedEvent and ClassUnloadEvent to make
 * it easy to listen for both events.
 * @author tonyj
 * @version $Id: ClassLoadEvent.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public abstract class ClassLoadEvent extends EventObject
{
   public ClassLoadEvent(Object source)
   {
      super(source);
   }  
}