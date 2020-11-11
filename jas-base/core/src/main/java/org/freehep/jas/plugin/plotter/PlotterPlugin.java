package org.freehep.jas.plugin.plotter;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.jas.plugin.xmlio.XMLPluginIO;
import org.freehep.jas.services.PlotterAdapter;
import org.freehep.jas.services.PlotFactory;
import org.freehep.jas.services.PlotPage;
import org.freehep.jas.services.PlotRegion;
import org.freehep.jas.services.Plotter;
import org.freehep.jas.services.PlotterProvider;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.xml.io.XMLIOFactory;
import org.freehep.xml.io.XMLIOManager;
import org.freehep.xml.io.XMLIOProxy;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.jdom.Element;
import org.openide.util.Lookup;
import org.xml.sax.SAXException;

/**
 * Plugin that implements {@link PlotFactory} and provides plotting service in Jas3.
 * <p>
 * When the {@link PlotFactory} implemented by this plugin is asked to create a {@link Plotter},
 * it looks through all {@link PlotterProvider}s registered with the Jas3 lookup until it 
 * finds a compatible one. This plugin itself registers a provider that can create plotters 
 * (instances of {@link DefaultPlotter} for objects of type {@link JAS3DataSource}.
 * <p>
 * This plugin also creates an instance of {@link PlotterAdapterLookup} and adds it to the 
 * Jas3 lookup. Registering an adapter with this {@link PlotFactory} adds it to that adapter
 * lookup. The adapter will not be used directly by this {@link PlotFactory}. 
 * However, it will be accessible to instances of {@link DefaultPlotter} created by it.
 *
 * @author tonyj
 */
public class PlotterPlugin extends Plugin implements PlotFactory, PlotterProvider, XMLPluginIO {

  private int nPage = 0;
  private PlotterAdapterLookup plotterLookup;
  
// -- Plugin life cycle : ------------------------------------------------------

  @Override
  public void init() throws SAXException, IOException {
    Studio app = getApplication();
    app.getLookup().add(this);

    XMLMenuBuilder builder = app.getXMLMenuBuilder();
    java.net.URL xml = getClass().getResource("Plotter.menus");
    builder.build(xml);

    app.getCommandTargetManager().add(new PlotterCommands());

    plotterLookup = new PlotterAdapterLookup();
    app.getLookup().add(plotterLookup);

    app.getLookup().add(new PlotPageFactoryAndProxy(this));
    app.getLookup().add(new JAS3PlotFactoryAndProxy(this));
    app.getLookup().add(new DefaultPlotterFactory(this));
  }
  
  
// -- Getters : ----------------------------------------------------------------

  protected PlotterAdapterLookup plotterAdapterLookup() {
    return plotterLookup;
  }
  
  
// -- Implementing PlotFactory : -----------------------------------------------

  @Override
  public PlotPage createPage(String name) {
    if (name == null) {
      name = "Page " + (++nPage);
    }
    return new DefaultPage(getApplication(), this, name);
  }

  @Override
  public Plotter createPlotterFor(Class dataType) {
    PlotterProvider pp = findPlotProvider(dataType);
    return (pp == null) ? null : pp.create();
  }

  @Override
  public Plotter createPlotterFor(Class[] dataType) {
    PlotterProvider pp = findPlotProvider(dataType);
    return (pp == null) ? null : pp.create();
  }

  @Override
  public boolean canCreatePlotterFor(Class dataType) {
    return findPlotProvider(dataType) != null;
  }

  @Override
  public boolean canCreatePlotterFor(Class[] dataType) {
    return findPlotProvider(dataType) != null;
  }

  private PlotterProvider findPlotProvider(Class dataType) {
    Lookup.Template template = new Lookup.Template(PlotterProvider.class);
    Lookup.Result result = getApplication().getLookup().lookup(template);
    for (Iterator i = result.allInstances().iterator(); i.hasNext();) {
      PlotterProvider pp = (PlotterProvider) i.next();
      if (pp.supports(dataType)) {
        return pp;
      }
    }
    return null;
  }

  private PlotterProvider findPlotProvider(Class[] dataType) {
    Lookup.Template template = new Lookup.Template(PlotterProvider.class);
    Lookup.Result result = getApplication().getLookup().lookup(template);
    outer:
    for (Iterator i = result.allInstances().iterator(); i.hasNext();) {
      PlotterProvider pp = (PlotterProvider) i.next();
      for (Class type : dataType) {
        if (!pp.supports(type)) {
          continue outer;
        }
      }
      return pp;
    }
    return null;
  }

  @Override
  public PlotPage currentPage() {
    PageContext context = getApplication().getPageManager().getSelectedPage();
    if (context == null) return null;
    Component page = context.getPage();
    return (page instanceof PlotPage) ? (PlotPage) page : null;
  }

  /**
   * Registers the specified adapter with the {@link PlotterAdapterLookup} maintained
   * by this plugin. The adapter will not be used directly by the {@link PlotFactory}
   * implemented by this plugin. However, it will be accessible to instances of 
   * {@link DefaultPlotter} created by it.
   */
  @Override
  public void registerAdapter(PlotterAdapter adapter, Class from, Class to) {
    plotterLookup.registerAdapter(adapter, from, to);
  }

  @Override
  public List<PlotPage> pages() {
    List pages = getApplication().getPageManager().pages();
    List<PlotPage> result = new ArrayList(pages.size());
    for (Object page : pages) {
      if (page instanceof PlotPage) {
        result.add((PlotPage)page);
      }
    }
    return result;
  }
  
  
// -- Implementing PlotterProvider : -------------------------------------------

  @Override
  public Plotter create() {
    return new DefaultPlotter(this);
  }

  @Override
  public boolean supports(Class klass) {
    return JAS3DataSource.class.isAssignableFrom(klass);
  }
  
  
// -- Implementing XMLPluginIO : -----------------------------------------------

  @Override
  public int restore(int level, XMLIOManager manager, Element el) {
    switch (level) {
      case RESTORE_DATA:
        return RESTORE_PAGES;
      case RESTORE_PAGES:
        List pages = el.getChildren("PlotPage");
        for (int i = 0; i < pages.size(); i++) {
          manager.restore((Element) pages.get(i));
        }
        nPage = Integer.parseInt(el.getAttributeValue("nPages"));
        return RESTORE_DONE;
      default:
        throw new IllegalArgumentException("Level " + level + " is not supported");
    }
  }

  @Override
  public void save(XMLIOManager manager, Element el) {
    el.setAttribute("nPages", String.valueOf(nPage));
    Studio app = getApplication();
    List pages = app.getPageManager().pages();
    for (int i = 0; i < pages.size(); i++) {
      PageContext pageContext = (PageContext) pages.get(i);
      if (pageContext.getPage() instanceof PlotPage) {
        PlotPage page = (PlotPage) pageContext.getPage();
        el.addContent(manager.saveAs(page, PlotPage.class));

      }
    }
  }
  
  
// -- Command processor : ------------------------------------------------------

  public class PlotterCommands extends CommandProcessor {

    public void onNewPlotPage() {
      PlotPage page = createPage(null);
      page.createRegions(1, 1);
      page.showPage();
    }
    
  }
  
  
// -- XMLIO Factory classes : --------------------------------------------------

  private class PlotPageFactoryAndProxy implements XMLIOFactory, XMLIOProxy {

    private PlotterPlugin plugin;
    private Class[] classes = {PlotPage.class};

    public PlotPageFactoryAndProxy(PlotterPlugin plugin) {
      this.plugin = plugin;
    }

    public Class[] XMLIOFactoryClasses() {
      return classes;
    }

    public Class[] XMLIOProxyClasses() {
      return classes;
    }

    public Object createObject(Class objClass) throws IllegalArgumentException {
      return plugin.createPage(null);
    }

    public void restore(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
      DefaultPage page = (DefaultPage) obj;
      page.setTitle(nodeEl.getAttributeValue("title"));
      String colString = nodeEl.getAttributeValue("columns");
      if (colString != null) {
        int columns = Integer.valueOf(colString).intValue();
        int rows = Integer.valueOf(nodeEl.getAttributeValue("rows")).intValue();
        page.createRegions(columns, rows);
      }
      String addString = nodeEl.getAttributeValue("addedRegions");
      if (addString != null) {
        int addedRegions = Integer.valueOf(addString).intValue();
        for (int i = 0; i < addedRegions; i++) {
          page.addRegion();
        }
      }

      int currentRegion = Integer.parseInt(nodeEl.getAttributeValue("currentRegion"));
      page.showPage();
      page.setCurrentRegion(page.region(currentRegion));

      List plotRegions = nodeEl.getChildren("PlotRegion");
      for (int i = 0; i < plotRegions.size(); i++) {
        Element plotRegionEl = (Element) plotRegions.get(i);

        List children = plotRegionEl.getChildren();
        Plotter plotter = (Plotter) xmlioManager.restore((Element) children.get(0));

        int n = Integer.valueOf(plotRegionEl.getAttributeValue("n")).intValue();
        page.region(n).showPlot(plotter);
      }
    }

    public void save(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
      DefaultPage page = (DefaultPage) obj;
      nodeEl.setAttribute("title", page.title());
      nodeEl.setAttribute("nRegions", String.valueOf(page.numberOfRegions()));
      int cols = page.columns();
      if (cols > 0) {
        nodeEl.setAttribute("columns", String.valueOf(cols));
        nodeEl.setAttribute("rows", String.valueOf(page.rows()));
      }
      int addRows = page.addedRegions();
      if (addRows > 0) {
        nodeEl.setAttribute("addedRegions", String.valueOf(addRows));
      }
      for (int i = 0; i < page.numberOfRegions(); i++) {
        PlotRegion region = page.region(i);
        Plotter plotter = region.currentPlot();
        if (plotter != null) {
          Element regEl = new Element("PlotRegion");
          regEl.setAttribute("n", String.valueOf(i));
          regEl.addContent(xmlioManager.save(plotter));
          nodeEl.addContent(regEl);
        }
        if (page.region(i) == page.currentRegion()) {
          nodeEl.setAttribute("currentRegion", String.valueOf(i));
          break;
        }
      }
    }
  }

  private class JAS3PlotFactoryAndProxy implements XMLIOFactory, XMLIOProxy {

    private PlotterPlugin plugin;
    private Class[] classes = {JAS3Plot.class};

    public JAS3PlotFactoryAndProxy(PlotterPlugin plugin) {
      this.plugin = plugin;
    }

    public Class[] XMLIOFactoryClasses() {
      return classes;
    }

    public Class[] XMLIOProxyClasses() {
      return classes;
    }

    public Object createObject(Class objClass) throws IllegalArgumentException {
      if (objClass == JAS3Plot.class) {
        return new JAS3Plot();
      }
      throw new IllegalArgumentException("Cannot create class " + objClass);
    }

    public void restore(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
      JAS3Plot plot = (JAS3Plot) obj;
      List children = nodeEl.getChildren();
      for (int i = 0; i < children.size(); i++) {
        Element childEl = (Element) children.get(i);
        JAS3DataSource data = (JAS3DataSource) xmlioManager.restore(childEl);
        if (data.dataSource() != null) {
          plot.addJAS3Data(data);
          plot.addData(data.dataSource());
        }
      }
    }

    public void save(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
      List data = ((JAS3Plot) obj).data();
      for (int i = 0; i < data.size(); i++) {
        nodeEl.addContent(xmlioManager.save(data.get(i)));
      }
    }
  }

  private class DefaultPlotterFactory implements XMLIOFactory {

    private PlotterPlugin thePlugin;

    DefaultPlotterFactory(PlotterPlugin thePlugin) {
      this.thePlugin = thePlugin;
    }

    private Class[] classes = {DefaultPlotter.class};

    public Class[] XMLIOFactoryClasses() {
      return classes;
    }

    public Object createObject(Class objClass) throws IllegalArgumentException {
      if (objClass == DefaultPlotter.class) {
        return thePlugin.create();
      }
      throw new IllegalArgumentException("Cannot create class " + objClass);
    }
  }

}
