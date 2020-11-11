package org.freehep.jas.plugin.pluginmanager;

import java.awt.Component;
import java.io.IOException;
import java.net.MalformedURLException;
import org.freehep.application.PropertyUtilities;
import org.freehep.application.studio.Studio;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.xml.menus.XMLMenuBuilder;
import java.net.URL;
import java.util.Collection;
import java.util.Properties;
import javax.swing.JComponent;
import org.freehep.application.studio.PluginInfo;
import org.freehep.application.studio.pluginmanager.PluginPreferences;
import org.xml.sax.SAXException;

/**
 * Jas3 customized plugin manager.
 *
 * @author tonyj
 * @version $Id: PluginManager.java 15681 2013-10-07 23:57:03Z onoprien $
 */
public class PluginManager extends org.freehep.application.studio.pluginmanager.PluginManager implements PreferencesTopic {
  
// -- Customizing PluginManager : ----------------------------------------------

    @Override
    protected void init() throws SAXException, IOException {
        super.init();
      
        final Studio app = getApplication();

        XMLMenuBuilder builder = app.getXMLMenuBuilder();
        java.net.URL xml = PluginManager.class.getResource("PluginManager.menus");
        builder.build(xml);

        app.getLookup().add(this);
        app.getCommandTargetManager().add(new Commands());
    }

    @Override
    /**
     * Override to always return true since some Jas3 plugins require init() to
     * have been run on all other plugins before their own postInit() runs.
     */
    public boolean install(Component parent, Collection<PluginInfo> plugins) {
      super.install(parent, plugins);
      return true;
    }
    

// -- Implementing PreferencesTopic : ------------------------------------------
  
  @Override
  public String[] path() {
    return new String[] {"Plugin Manager"};
  }

  @Override
  public JComponent component() {
    return preferences.getPreferencesPanel();
  }

  @Override
  public boolean apply(JComponent panel) {
    return preferences.apply(panel);
  }

  @Override
  protected PluginPreferences makePreferences() {
    return new Preferences(this);
  }

  /** Customized plugin manager preferences. */
  class Preferences extends PluginPreferences {

    public Preferences(PluginManager manager) {
      super(manager, false);

      // Customize PluginPreferences constants :
      try {
        urlDefault = new URL("http://jas.freehep.org/jas3-plugins/Plugins");
      } catch (MalformedURLException x) {
        throw new RuntimeException(x);
      }

      // restore settings from saved user properties :
      restore();
    }

  }
    
// -- Command processor class : ------------------------------------------------
    
    class Commands extends CommandProcessor {
        public void onPluginManager() {
            showPluginManager();
        }
    }
    
}