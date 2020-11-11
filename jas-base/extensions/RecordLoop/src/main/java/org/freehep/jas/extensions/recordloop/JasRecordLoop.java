package org.freehep.jas.extensions.recordloop;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.freehep.application.studio.Studio;
import org.freehep.record.loop.ConcurrentRecordLoop;
import org.freehep.swing.ErrorDialog;

/**
 * Customized RecordLoop that overrides default handling of user's code errors and 
 * provides thread factories for ConcurrentLoop executors.
 *
 * @author onoprien
 * @version $Id$
 */
class JasRecordLoop extends ConcurrentRecordLoop {

// -- Private parts : ----------------------------------------------------------
  
  private final Studio _app;
  private final ThreadFactory _factory;

  
// -- Construction and initialization : ----------------------------------------
  
  JasRecordLoop(Studio application) {
    
    _app = application;
    _factory = Executors.defaultThreadFactory();

    _isInteractive = true;
    _loopExecutor = Executors.newSingleThreadExecutor(new LoopThreadFactory(false));
    
    _nThreads = 0;
    _threadFactory = new LoopThreadFactory(true);
  }
  
// -- Handle client and source errors : ----------------------------------------

  @Override
  protected void handleClientError(final Throwable x) {
    int response = _app.error("Error in user's code while processing records", x, new Object[]{"   OK   ", " Pause ", "Quit Jas3"});
    if (response == 1) {
      execute(Command.PAUSE);
    } else if (response == 2) {
      System.exit(1);
    }
  }

  @Override
  protected void handleSourceError(final Throwable x) {
    try {
      Runnable task = new Runnable() {
        public void run() {
          _app.error("Error while retrieving records from the source", x);
          System.exit(1);
        }
      };
      if (SwingUtilities.isEventDispatchThread()) {
        SwingUtilities.invokeLater(task);
      } else {
        SwingUtilities.invokeAndWait(task);
      }
    } catch (InterruptedException xx) {
      System.exit(1);
    } catch (InvocationTargetException xx) {
      System.exit(1);
    }
  }
  
// -- Thread factory class : ---------------------------------------------------
  
  /**
   * Thread factory class used by the executor that supplies events to {@link RecordListener}s.
   * Threads created by this factory will have their priority set to <tt>current-1</tt>
   * and their output redirected to "Record Loop" console.
   */
  private class LoopThreadFactory implements ThreadFactory {
    private final boolean _reducePriority;
    LoopThreadFactory(boolean reducePriority) {
      _reducePriority = reducePriority;
    }
    public Thread newThread(Runnable r) {
      Thread thread = _factory.newThread(r);
      if (_reducePriority) thread.setPriority(Thread.currentThread().getPriority()-1);
//      ConsoleService cs = (ConsoleService) _app.getLookup().lookup(ConsoleService.class);
//      if (cs != null) {
//        try {
//          ConsoleOutputStream out = cs.getConsoleOutputStream("Record Loop", null);
//          cs.redirectStandardOutputOnThreadToConsole(thread, out);
//        } catch (IOException x) {}
//      }
      return thread;
    }
  }
  
}
