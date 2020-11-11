package org.freehep.jas.extensions.jconsole;

import java.awt.Component;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.mdi.PageEvent;
import org.freehep.application.mdi.PageListener;
import org.freehep.application.studio.Plugin;
import org.freehep.jas.plugin.console.Console;
import org.freehep.swing.popup.HasPopupItems;

/**
 *
 * @author onoprien
 */
public class JConsolePlugin extends Plugin implements JConsoleService, PageListener {

// -- Private parts : ----------------------------------------------------------
  
  private final Map<Class,JConsoleProvider> _providers = Collections.synchronizedMap(new HashMap<>(3));
  private final ConcurrentHashMap<String,JConsole> _name2console = new ConcurrentHashMap<>();
  private final Map<PageContext,JConsole> _context2console = Collections.synchronizedMap(new IdentityHashMap<>(4));

// -- Construction and Plugin hooks : ------------------------------------------

  @Override
  protected void init() throws Throwable {
    getApplication().getLookup().add(this);
    getApplication().getConsoleManager().addPageListener(this);
  }

  @Override
  protected void postInit() {
    addConsoleProvider(SwingConsole.class, new SwingConsoleProvider());
    addConsoleProvider(JLineConsole.class, new JLineConsoleProvider());
  }

//  @Override
//  protected void applicationVisible() {
//    createConsole(JLineConsole.class, "JLine Console", null, null);
//    createConsole(SwingConsole.class, "Swing Console", null, null);
//  }
  
  
// -- Implementing JConsoleService: --------------------------------------------
  
  /**
   * Adds a provider that will be used to create consoles of the specified type.
   * 
   * @param consoleType Type of consoles the specified provider will be used to create.
   * @param provider The provider.
   */
  @Override
  public void addConsoleProvider(Class consoleType, JConsoleProvider provider) {
    _providers.put(consoleType, provider);
  }

  /**
   * Returns an existing console with the specified name. If there are multiple
   * consoles with the given name, this method will return one of them.
   *
   * @param name The name of the console.
   * @return The console, or <tt>null</tt> if there is no console by this name.
   */
  @Override
  public JConsole getConsole(String name) {
    return _name2console.get(name);
  }

  @Override
  public JConsole createConsole(Class type, String name, Icon icon, HasPopupItems popupItems) {
    JConsoleProvider provider = _providers.get(type);
    if (provider == null) {
      throw new IllegalArgumentException("No console provider for type "+ type.getName());
    }
    JConsole console = provider.createConsole(name, popupItems);
    _name2console.putIfAbsent(name, console);
    int vPolicy = console.getVerticalScrollBarPolicy();
    int hPolicy = console.getHorizontalScrollBarPolicy();
    Component panel;
    if (vPolicy == ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER && hPolicy == ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER) {
      panel = console.getView();
    } else {
      panel = new JScrollPane(console.getView(), vPolicy, hPolicy);
    }
    PageContext context = getApplication().getConsoleManager().openPage(panel, name, icon, name, false);
    _context2console.put(context, console);
    return console;
  }

  @Override
  public void showConsole(Console console) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public PageContext getPageContextForConsole(JConsole console) {
    synchronized (_context2console) {
      for (Map.Entry<PageContext, JConsole> e : _context2console.entrySet()) {
        if (console == e.getValue()) {
          return e.getKey();
        }
      }
    }
    return null;
  }
  
  
// -- Lestening to console manager : -------------------------------------------

  @Override
  public void pageChanged(PageEvent e) {
    if (e.getID() == PageEvent.PAGECLOSED) {
      JConsole console = _context2console.remove(e.getPageContext());
      if (console != null) {
        _name2console.remove(console.getName(), console);
        console.dispose();
      }
    }
  }
  
  
// -- Local methods : ----------------------------------------------------------


}
