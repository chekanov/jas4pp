package org.freehep.jas.plugin.plotter;

import jas.hist.JASHistData;
import java.awt.Component;
import java.util.*;
import org.freehep.jas.services.PlotFactory;
import org.freehep.jas.services.Plotter;
import org.freehep.jas.services.PlotterAdapter;
import org.freehep.xml.io.XMLIO;
import org.freehep.xml.io.XMLIOManager;
import org.jdom.Element;

/**
 * {@link Plotter} that can display objects of type {@link JAS3DataSource}.
 * Objects of other types can be plotted if a {@link PlotterAdapter} capable of
 * converting those objects into instances of {@link JAS3DataSource} is registered 
 * with {@link PlotterPlugin} adapter lookup.
 *
 * @author tonyj
 */
public class DefaultPlotter implements Plotter, XMLIO {

  private final JAS3Plot plot = new JAS3Plot();
  private final PlotterAdapterLookup plotterAdapterLookup;
  
  protected List<Object> dataList = new ArrayList<>(1);
  
// -- Construction : -----------------------------------------------------------

  public DefaultPlotter(PlotFactory factory) {
    plotterAdapterLookup = ((PlotterPlugin) factory).plotterAdapterLookup();
  }

// -- Extra getters : ----------------------------------------------------------

  /** Returns the viewable component of this plot. */
  public JAS3Plot getPlot() {
    return plot;
  }
  
  
// -- Implementing Plotter : ---------------------------------------------------

  @Override
  public void plot(Object data, int mode) {
    plot(data, mode, null, null);
  }

  @Override
  public void plot(Object data, int mode, Object style, String options) {
    if (mode != NORMAL && mode != OVERLAY) {
      throw new UnsupportedOperationException();
    }
    if (mode != OVERLAY) {
      clear();
            //TODO - This is needed to clear the data list. In the future, when we
      //define the new plotter and the new data source, mergine the code in FreeHEP and
      //the one in JAS2 should take care of this problem.
      //plot.clearDataList();
      //dataList.clear();
    }
    JASHistData jasHistData;

    JAS3DataSource jas3DataSource;

    if (data instanceof JAS3DataSource) {
      jas3DataSource = (JAS3DataSource) data;
    } else {
      PlotterAdapter adapter = plotterAdapterLookup.adapter(data.getClass(), JAS3DataSource.class);
      if (adapter != null) {
        jas3DataSource = (JAS3DataSource) adapter.adapt(data);
      } else {
        throw new UnsupportedOperationException();
      }
    }

    plot.addJAS3Data(jas3DataSource);
    jasHistData = plot.addData(jas3DataSource.dataSource());

    jasHistData.show(true);
    dataList.add(data);
  }

  @Override
  public void clear() {
    /*
     JASHistAxis[] axs = plot.getYAxes();
     JASHistAxis[] axises = new JASHistAxis[2];
     axises[0] = plot.getYAxis(0);
     axises[1] = plot.getYAxis(1);
     System.out.println("Clear plot: axs="+axs.length+", Y0="+axises[0]+", Y1="+axises[1]);
     if (axises != null && axises.length > 1 && axises[1] != null) axises[1].setShowing(false);
     */
    plot.removeAllData();
    plot.clearDataList();
    dataList.clear();
    plot.getXAxis().setLabel("");
    plot.getYAxis().setLabel("");
  }

  /** Currently unimplemented. */
  @Override
  public void remove(Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Component viewable() {
    return plot;
  }

  @Override
  public List<Object> getData() {
    return dataList;
  }
  
  
// -- Implementing XMLIO : -----------------------------------------------------

  @Override
  public void restore(XMLIOManager xmlioManager, Element nodeEl) {
    List children = nodeEl.getChildren();
    if (children.size() == 1) {
      JAS3Plot plot = (JAS3Plot) xmlioManager.restore((Element) children.get(0));
      List data = plot.data();
      plot(data.get(0), Plotter.NORMAL);
      for (int i = 1; i < data.size(); i++) {
        plot(data.get(i), Plotter.OVERLAY);
      }
    }
  }

  @Override
  public void save(XMLIOManager xmlioManager, Element nodeEl) {
    Component c = getPlot();
    if (c != null) {
      nodeEl.addContent(xmlioManager.save(c));
    }
  }

}
