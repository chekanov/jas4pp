package org.freehep.jas.plugin.preferences;

import java.io.IOException;
import java.net.URL;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.xml.sax.SAXException;
import org.freehep.jas.services.PreferencesManager;

/**
 *
 * @author tonyj
 * @version $Id: PreferencesPlugin.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public class PreferencesPlugin extends Plugin implements PreferencesManager
{
   private Studio app;
   protected void init() throws SAXException, IOException
   {
      app = getApplication();

      XMLMenuBuilder builder = app.getXMLMenuBuilder();
      URL xml = getClass().getResource("Preferences.menus");
      builder.build(xml);
      
      app.getCommandTargetManager().add(new GlobalCommands());
      app.getLookup().add(this);
   } 
   public void showPreferences()
   {
      showPreferences(null);
   }
   public void showPreferences(String[] topic)
   {
      PreferencesDialog dlg = new PreferencesDialog(app);
      if (topic != null) dlg.selectTopic(topic);
      dlg.showDialog(); 
   }
   public class GlobalCommands extends CommandProcessor
   {
      public void onShowPreferences()
      {
         showPreferences();
      }
   }
}