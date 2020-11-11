package org.freehep.jas.mac;

import org.freehep.application.studio.Plugin;
import org.freehep.jas.services.PreferencesManager;
import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import org.openide.util.Lookup;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.beans.PropertyChangeListener;
import javax.swing.UIManager;
import org.freehep.jas.services.FileHandler;

/**
 * A plugin to aid in system integration under MacOSX
 * @author tonyj
 */
public class MacPlugin extends Plugin implements PropertyChangeListener
{
   private boolean toolbarUIIsInstalled = false;

   protected void init()
   {
      if (!System.getProperty("os.name").contains("OS X")) {
        return;
      }
     
      Application app = Application.getApplication();
      MacListener ml = new MacListener();
      app.addApplicationListener(ml);
      app.addPreferencesMenuItem();
      app.setEnabledPreferencesMenu(true);
      
      UIManager.addPropertyChangeListener(this);
      propertyChange(null);
      java.awt.Component c = getApplication().getToolBarHolder();
      org.freehep.application.Application.updateComponentTreeUI(c);
   }
   public void propertyChange(java.beans.PropertyChangeEvent evt)
   {
      if (UIManager.getLookAndFeel().getID().equals("Aqua"))
      {
         if (!toolbarUIIsInstalled) UIManager.getDefaults().put("ToolBarUI","org.freehep.jas.mac.plaf.PlainAquaToolbarUI");
         toolbarUIIsInstalled = true;
      }
      else 
      {
         if (toolbarUIIsInstalled) UIManager.getDefaults().remove("ToolBarUI");
         toolbarUIIsInstalled = false;
      }
   }
   private class MacListener extends ApplicationAdapter
   {
      public void handleQuit(com.apple.eawt.ApplicationEvent applicationEvent)
      {
         getApplication().exit();
         applicationEvent.setHandled(false); //if we get here we did not exit
      }
      
      public void handleOpenFile(com.apple.eawt.ApplicationEvent applicationEvent)
      {
         File file = new File(applicationEvent.getFilename());
         try
         {
            Lookup.Template template = new Lookup.Template(FileHandler.class);
            Lookup.Result result = getApplication().getLookup().lookup(template);
            for (Iterator i = result.allInstances().iterator(); i.hasNext();)
            {
               FileHandler s = (FileHandler) i.next();
               if (s.accept(file))
               {
                  s.openFile(file);
                  return;
               }
            }
         }
         catch (IOException x)
         {
            getApplication().error("Unable to open: "+file,x);
         }
         
      }
      
      public void handlePreferences(com.apple.eawt.ApplicationEvent applicationEvent)
      {
         PreferencesManager man = (PreferencesManager) getApplication().getLookup().lookup(PreferencesManager.class);
         if (man != null)
         {
            man.showPreferences();
            applicationEvent.setHandled(true);
         }
      }
      
      public void handleAbout(com.apple.eawt.ApplicationEvent applicationEvent)
      {
         getApplication().about();
         applicationEvent.setHandled(true);
      }
      
   }
}
