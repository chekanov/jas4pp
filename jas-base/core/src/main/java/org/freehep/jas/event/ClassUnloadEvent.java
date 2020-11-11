package org.freehep.jas.event;

import java.util.EventObject;

/**
 * Informs listeners that all loaded classes have been cleared.
 * Listeners should release any references to classes that they previously
 * obtained from a ClasLoadedEvent
 * @author tonyj
 */
public class ClassUnloadEvent extends ClassLoadEvent
{
   public ClassUnloadEvent(Object source)
   {
      super(source);
   }   
}