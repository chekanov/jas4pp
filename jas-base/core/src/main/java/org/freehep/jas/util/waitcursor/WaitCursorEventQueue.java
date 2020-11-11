package org.freehep.jas.util.waitcursor;

import java.awt.*;


/**
 * Suggested serving size:
 * Toolkit.getDefaultToolkit().getSystemEventQueue().push(new WaitCursorEventQueue(70));
 */
// Based on code from: http://www.javaspecialists.co.za/archive/Issue075.html 
public class WaitCursorEventQueue extends EventQueue implements DelayTimerCallback
{
   private final CursorManager cursorManager;
   private final DelayTimer waitTimer;

   public WaitCursorEventQueue(int delay)
   {
      this.waitTimer = new DelayTimer(this, delay);
      this.cursorManager = new CursorManager(waitTimer);
   }

   public AWTEvent getNextEvent() throws InterruptedException
   {
      waitTimer.stopTimer(); //started by pop(), this catches modal dialogs

      //closing that do work afterwards
      return super.getNextEvent();
   }

   public void close()
   {
      waitTimer.quit();
      pop();
   }

   public void trigger()
   {
      cursorManager.setCursor();
   }

   protected void dispatchEvent(AWTEvent event)
   {
      cursorManager.push(event.getSource());
      waitTimer.startTimer();
      try
      {
         super.dispatchEvent(event);
      }
      finally
      {
         waitTimer.stopTimer();
         cursorManager.pop();
      }
   }
}
