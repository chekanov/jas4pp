package org.freehep.jas;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.freehep.application.studio.Studio;
import org.freehep.jas.plugin.xmlio.XMLIOPlugin;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.jas.services.WebBrowser;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.commandline.CommandLine;
import org.openide.util.Lookup.Item;
import org.openide.util.Lookup.Template;
import org.freehep.jas.util.waitcursor.WaitCursorEventQueue;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.freehep.application.PropertyUtilities;
import org.freehep.application.studio.PluginDir;
import org.freehep.graphicsbase.util.export.ExportFileType;

/**
 * Jas3 application.
 *
 * @author tonyj
 * @version $Id: JAS3.java 16271 2015-03-27 21:13:29Z onoprien $
 */
public class JAS3 extends Studio {

  private XMLIOPlugin xmlioPlugin;
  public static final File BUILTIN = new File("");

  public JAS3() {
    this("JAS3");
  }
  
  public JAS3(String applicationName) {
    super(applicationName);
    setPluginManagerName("Plugin Manager");

    // This is required so that unsigned plugins can be run when JAS is running as a (signed) web-start app.
    Policy.setPolicy(new Policy() {

      @Override
      public PermissionCollection getPermissions(CodeSource codesource) {
        Permissions perms = new Permissions();
        perms.add(new AllPermission());
        return (perms);
      }

      @Override
      public void refresh() {
      }
    });    
  }

  @Override
  protected CommandLine createCommandLine() {
    CommandLine cl = super.createCommandLine();
        // register standard options. There should be a more modular way to do
    // this!
    cl.addOption("startPage", "s", "url", "URL for the welcome page");
    cl.addOption("title", "t", "window title", "Title for the window");
    cl.addOption("open", null, "file", "File to open");
    cl.addParameter("file...", "Files to be read in");
    return cl;
  }

  @Override
  protected void init() {

      // Make sure snapshots are treated the same way as corresponding releases in version comparisons:
    Properties prop = getAppProperties();
    String version = prop.getProperty("version");
    if (version != null && version.endsWith("-SNAPSHOT")) {
      prop.setProperty("version", version.substring(0, version.length() - 9));
    }

      // Register a general purpose URLStreamHandlerFactory:
    URL.setURLStreamHandlerFactory(new JAS3StreamHandlerFactory());

      // Register a general purpose Authenticator:
    Authenticator.setDefault(new JAS3Authenticator());
    URLConnection.setDefaultAllowUserInteraction(true);

      // Load built-in plugins:
    getLookup().add(new Preferences());
//            setStatusMessage("Loading built-in modules...");
//            InputStream in = JAS3.class.getClassLoader().getResourceAsStream("PLUGIN-inf/plugins.xml");
//            if (in != null) {
//                List<PluginInfo> plugins = buildPluginList(in);
//                for (PluginInfo plugin : plugins) {
//                  plugin.setDirectory(PluginDir.BUILTIN);
//                }
//                loadPlugins(plugins, JAS3.class.getClassLoader());
//            }

      // Blacklist undesirable plugins:
    blacklistPlugin("LCIO", null, "1.4.0");
    blacklistPlugin("HepRep Plugin", null, "1.4.3");

      // Studio initialization:
    super.init();

    int cursorDelay = PropertyUtilities.getInteger(getUserProperties(), "waitCursorDelay", 250);
    if (cursorDelay > 0) {
      EventQueue waitQueue = new WaitCursorEventQueue(cursorDelay);
      Toolkit.getDefaultToolkit().getSystemEventQueue().push(waitQueue);
    }

    // Fix for JAS-85
    ExportFileType.setClassLoader(getExtensionLoader());
    // Fix for WIRED-408
    XMLMenuBuilder.setClassLoader(getExtensionLoader());

    String title = getCommandLine().getOption("title");
    if (title != null) {
      getAppProperties().setProperty("title", title);
    }

    xmlioPlugin = (XMLIOPlugin) getLookup().lookup(XMLIOPlugin.class);
    if (xmlioPlugin != null) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          xmlioPlugin.restoreAtStart();
        }
      });
    }
  }

  @Override
  public String getExtensionsDir(PluginDir dir) {
    String out = null;
    switch (dir) {
      case BUILTIN:
        String path = System.getProperty("application.home");
        if (path != null) {
          try {
            return (new File(path, "lib")).getCanonicalPath();
          } catch (IOException x) {
          }
        }
        return null;
      case SYSTEM:
        return System.getProperty("application.home") == null ? null : super.getExtensionsDir(dir);
      default:
    }
    if (out == null) {
      return super.getExtensionsDir(dir);
    } else {
      try {
        out = (new File(out)).getCanonicalPath();
      } catch (IOException x) {
        out = null;
      }
    }
    return out;
  }

  /** Override Application to strip "jnlp." prefix from properties names. */
  @Override
  protected Properties createAppProperties() throws InitializationException {
    Properties p = new Properties(super.createAppProperties());
    Enumeration<?> en = p.propertyNames();
    while (en.hasMoreElements()) {
      String name = en.nextElement().toString();
      if (name.startsWith("jnlp.")) {
        p.setProperty(name.substring(5), p.getProperty(name));
      }
    }
    return p;
  }

  @Override
  protected void saveUserProperties() {
    super.saveUserProperties();
    if (xmlioPlugin != null) {
      boolean success = xmlioPlugin.saveAtExit();
    }
  }

  public void onViewDocumentation() throws MalformedURLException {
    showWebPage("Documentation.URL");
  }

  public void enableViewDocumentation(CommandState state) {
    hasWebBrowser(state);
  }

  public void onViewForum() throws MalformedURLException {
    showWebPage("Forum.URL");
  }

  public void enableViewForum(CommandState state) {
    hasWebBrowser(state);
  }

  public void onReportBug() throws MalformedURLException {
    showWebPage("BugReport.URL");
  }

  public void enableReportBug(CommandState state) {
    hasWebBrowser(state);
  }

  public void onExamples() throws MalformedURLException {
    WebBrowser webBrowser = ((WebBrowser) getLookup().lookup(WebBrowser.class));
    webBrowser.showURL(new URL("classpath:/org/freehep/jas/web/examples.html"));
  }

  public void enableExamples(CommandState state) {
    hasWebBrowser(state);
  }

  private void hasWebBrowser(CommandState state) {
    state.setEnabled(getLookup().lookup(WebBrowser.class) != null);
  }

  private void showWebPage(String property) throws MalformedURLException {
    WebBrowser webBrowser = ((WebBrowser) getLookup().lookup(WebBrowser.class));
    webBrowser.showURL(new URL(getUserProperties().getProperty(property)), true);
  }

  public static void main(final String[] args) {

//    JOptionPane.showMessageDialog(null, "Jas3 main "); // uncomment to enable debugger attachement
    
    String title = "JAS3";
    for (String s : args) {
      String[] tokens = s.split("=");
      if (tokens.length == 2 && "-Dtitle".equals(tokens[0].trim())) {
        title = tokens[1].trim();
      }
    }

    try {
      new JAS3(title).createFrame(args).setVisible(true);
    } catch (Throwable t) {
      error(null, "Jas3 Fatal Error", t);
      System.exit(1);
    }

  }

  @Override
  public void addToolBar(JToolBar toolBar, String name, int mode) {
    toolBar.setRollover(true);
    super.addToolBar(toolBar, name, mode);
  }

  private class Preferences implements PreferencesTopic {

    @Override
    public boolean apply(JComponent panel) {
      return ((GeneralPreferences) panel).apply();
    }

    @Override
    public JComponent component() {
      return new GeneralPreferences(JAS3.this);
    }

    @Override
    public String[] path() {
      return new String[]{"General"};
    }
  }

  private class JAS3StreamHandlerFactory implements URLStreamHandlerFactory {

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
            // The special cases are required for efficiency, and to avoid circular
      // dependencies when running in WebStart
      if ("http".equals(protocol)) {
        return null;
      }
      if ("jar".equals(protocol)) {
        return null;
      }
      if ("file".equals(protocol)) {
        return null;
      }
      if ("ftp".equals(protocol)) {
        return null;
      }

      Template template = new Template(URLStreamHandler.class, protocol, null);
      Item item = getLookup().lookupItem(template);
      return item == null ? null : (URLStreamHandler) item.getInstance();
    }
  }

  private class JAS3Authenticator extends Authenticator {

    private String[] fields = {"requestingHost",
      "requestingSite",
      "requestingPort",
      "requestingProtocol",
      "requestingPrompt",
      "requestingScheme"
    };

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
      try {
        String protocol = getRequestingProtocol();
        Template template = new Template(Authenticator.class, protocol, null);
        Item item = getLookup().lookupItem(template);
        if (item == null) {
          return null;
        }
        Authenticator auth = (Authenticator) item.getInstance();

        // This is really ugly, but there is no other way to deal with Authenticator's stupid design.
        for (int i = 0; i < fields.length; i++) {
          Field f = Authenticator.class.getDeclaredField(fields[i]);
          f.setAccessible(true);
          f.set(auth, f.get(this));
        }
        Method method = auth.getClass().getDeclaredMethod("getPasswordAuthentication", (Class[]) null);
        method.setAccessible(true);
        return (PasswordAuthentication) method.invoke(auth, (Object[]) null);
      } catch (Throwable t) {
        t.printStackTrace();
        return null;
      }
    }
  }
}
