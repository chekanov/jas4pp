package org.freehep.jas.util.waitcursor;

import java.awt.*;
import java.awt.event.InputEvent;

import java.util.*;


class CursorManager
{
   private final DelayTimer waitTimer;
   private final Stack dispatchedEvents;
   private boolean needsCleanup;

   public CursorManager(DelayTimer waitTimer)
   {
      this.dispatchedEvents = new Stack();
      this.waitTimer = waitTimer;
   }

   public void setCursor()
   {
      ((DispatchedEvent) dispatchedEvents.peek()).setCursor();
   }

   public void pop()
   {
      cleanUp();
      dispatchedEvents.pop();
      if (!dispatchedEvents.isEmpty())
      {
         //this will be stopped if getNextEvent() is called - 
         //used to watch for modal dialogs closing
         waitTimer.startTimer();
      }
      else
      {
         needsCleanup = false;
      }
   }

   public void push(Object source)
   {
      if (needsCleanup)
      {
         waitTimer.stopTimer();
         cleanUp(); //this corrects the state when a modal dialog 

         //opened last time round
      }
      dispatchedEvents.push(new DispatchedEvent(source));
      needsCleanup = true;
   }

   private void cleanUp()
   {
      if (((DispatchedEvent) dispatchedEvents.peek()).resetCursor())
      {
         clearQueueOfInputEvents();
      }
   }

   private void clearQueueOfInputEvents()
   {
      EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
      ArrayList nonInputEvents;
      synchronized (q)
      {
         nonInputEvents = gatherNonInputEvents(q);
      }
      for (Iterator it = nonInputEvents.iterator(); it.hasNext();)
         q.postEvent((AWTEvent) it.next());
   }

   private ArrayList gatherNonInputEvents(EventQueue systemQueue)
   {
      ArrayList events = new ArrayList();
      while (systemQueue.peekEvent() != null)
      {
         try
         {
            AWTEvent nextEvent = systemQueue.getNextEvent();
            if (!(nextEvent instanceof InputEvent))
            {
               events.add(nextEvent);
            }
         }
         catch (InterruptedException ie)
         {
            Thread.currentThread().interrupt();
         }
      }

      return events;
   }
}
