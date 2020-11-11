package org.freehep.jas.plugin.plotter;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.freehep.application.PrintHelper;
import org.freehep.application.mdi.ManagedPage;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.studio.Studio;
import org.freehep.graphicsbase.util.export.ExportDialog;
import org.freehep.graphicsbase.util.export.VectorGraphicsTransferable;
import org.freehep.jas.services.PlotFactory;
import org.freehep.jas.services.PlotPage;
import org.freehep.jas.services.PlotRegion;
import org.freehep.swing.layout.PercentLayout;
import org.freehep.swing.layout.PercentLayout.Constraint;
import org.freehep.swing.popup.HasPopupItems;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.images.ImageHandler;

/**
 * Plot page.
 * 
 * @author tonyj
 */
class DefaultPage extends JPanel implements PlotPage, HasPopupItems, ManagedPage {

  private static final Icon histogramIcon = ImageHandler.getIcon("images/Histogram", DefaultPage.class);
  private final Studio studio;
  private final PlotFactory factory;
  private final PercentLayout layout;
  private final Commands commands = new Commands();
  private final MouseListener ml = new MouseAdapter() {
    public void mouseClicked(MouseEvent e) {
      DefaultRegion reg = (DefaultRegion) e.getSource();
      setCurrentRegion(reg);
    }    
  };
  
  private PageContext context;
  private String title;
  
  private int nColumns = -1;
  private int nRows = -1;
  private int nAddedRegions = 0;
  private DefaultRegion currentRegion;
  
// -- Construction : -----------------------------------------------------------

  public DefaultPage(Studio studio, PlotFactory factory, String title) {
    this.studio = studio;
    this.factory = factory;
    this.title = title;
    setLayout(layout = new PercentLayout());
    setBackground(Color.white);
    setPreferredSize(new Dimension(600, 600));
//    createRegion(0, 0, 1, 1);
  }
  
  
// -- Extra getters and setters : ----------------------------------------------

  protected void setTitle(String title) {
    this.title = title;
    if (context != null) {
      context.setTitle(title);
    }
  }

  protected String title() {
    return title;
  }

  protected int columns() {
    return nColumns;
  }

  protected int rows() {
    return nRows;
  }

  protected int addedRegions() {
    return nAddedRegions;
  }

  
// -- Implementing PlotPage : --------------------------------------------------
  
  @Override
  public void clearRegions() {
    removeAll();
    currentRegion = null;
    nColumns = -1;
    nRows = -1;
    nAddedRegions = 0;
  }

  @Override
  public PlotRegion createRegion(double x, double y, double w, double h) {
    DefaultRegion result = new DefaultRegion(factory);
    result.addMouseListener(ml);
    add(result, new Constraint(x * 100, y * 100, w * 100, h * 100));
    setCurrentRegion(result);
    return result;
  }

  @Override
  public void createRegions(int columns, int rows) {
    clearRegions();
    nColumns = columns;
    nRows = rows;
    if (rows * columns != 0) {
      double pcWidth = 100 / columns;
      double pcHeight = 100 / rows;

      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < columns; c++) {
          DefaultRegion region = new DefaultRegion(factory);
          region.addMouseListener(ml);
          add(region, new Constraint(c * pcWidth, r * pcHeight, pcWidth, pcHeight));
          if (r == 0 && c == 0) {
            setCurrentRegion(region);
          }
        }
      }
    }
  }

  @Override
  public PlotRegion currentRegion() {
    return currentRegion;
  }

  @Override
  public PlotRegion next() {
    Component[] c = getComponents();
    for (int i = 0; i < c.length; i++) {
      if (c[i] == currentRegion) {
        int index = (i + 1) % c.length;
        setCurrentRegion((PlotRegion) c[index]);
        return currentRegion;
      }
    }
    return null;
  }

  @Override
  public int numberOfRegions() {
    return getComponentCount();
  }

  @Override
  public PlotRegion region(int index) {
    return (PlotRegion) getComponent(index);
  }

  @Override
  public void setCurrentRegion(PlotRegion reg) {
    if (reg != currentRegion) {
      if (!(reg instanceof DefaultRegion)) throw new IllegalArgumentException();
      if (currentRegion != null) {
        currentRegion.setSelected(false);
      }
      currentRegion = (DefaultRegion) reg;
      if (currentRegion != null) {
        currentRegion.setSelected(true);
      }
    }
  }

  @Override
  public void showPage() {
    if (context == null) {
      context = studio.getPageManager().openPage(this, title, histogramIcon, "Plot");
    } else {
      context.requestShow();
    }
  }

  @Override
  public void hidePage() {
    context.close();
  }

  @Override
  public Component viewable() {
    return this;
  }

  
// -- Implementing HasPopupItems : ---------------------------------------------
  
  @Override
  public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {
    studio.getXMLMenuBuilder().mergePopupMenu("PlotPagePopupMenu", menu);
    return menu;
  }
  
  
// -- Implementing ManagedPage : -----------------------------------------------

  @Override
  public boolean close() {
    context = null;
    return true;
  }

  @Override
  public void pageClosed() {
  }

  @Override
  public void pageDeiconized() {
  }

  @Override
  public void pageIconized() {
  }

  @Override
  public void pageSelected() {
    studio.getCommandTargetManager().add(commands);
  }

  @Override
  public void pageDeselected() {
    studio.getCommandTargetManager().remove(commands);
  }

  @Override
  public void setPageContext(PageContext context) {
  }

  @Override
  public PlotRegion addRegion() {
    nAddedRegions++;
    int n = getComponentCount();
    DefaultRegion result = new DefaultRegion(factory);
    result.addMouseListener(ml);
    add(result, new Constraint(0, 0, 100, 100));
    setCurrentRegion(result);
    if (n < 2) {
      setLayout(2, 1);
    } else if (n < 3) {
      setLayout(3, 1);
    } else if (n < 4) {
      setLayout(2, 2);
    } else if (n < 6) {
      setLayout(3, 2);
    } else if (n < 9) {
      setLayout(3, 3);
    } else if (n < 12) {
      setLayout(4, 3);
    } else if (n < 16) {
      setLayout(4, 4);
    } else if (n < 20) {
      setLayout(5, 4);
    } else if (n < 25) {
      setLayout(5, 5);
    } else if (n < 30) {
      setLayout(6, 5);
    } else if (n < 36) {
      setLayout(6, 6);
    } else {
      int nn = 1 + (int) Math.floor(Math.sqrt(n - 1));
      setLayout(nn, nn);
    }
    revalidate();
    repaint();
    return result;
  }
  
  
// -- Local methods : ----------------------------------------------------------

  private void setLayout(int columns, int rows) {
    if (rows * columns != 0) {
      double pcWidth = 100 / columns;
      double pcHeight = 100 / rows;

      Component[] comps = getComponents();
      int i = 0;
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < columns; c++) {
          if (i >= comps.length) {
            return;
          }
          Constraint constraint = layout.getConstraintFor(comps[i++]);
          constraint.setConstraints(c * pcWidth, r * pcHeight, pcWidth, pcHeight);
        }
      }
    }
  }
  

// -- Command processor class : ------------------------------------------------
  
  private class Commands extends CommandProcessor {

    public void onRegions_1_1() {
      createRegions(1, 1);
    }

    public void onRegions_2_1() {
      createRegions(2, 1);
    }

    public void onRegions_1_2() {
      createRegions(1, 2);
    }

    public void onRegions_2_2() {
      createRegions(2, 2);
    }

    public void onRegions_3_1() {
      createRegions(3, 1);
    }

    public void onRegions_1_3() {
      createRegions(1, 3);
    }

    public void onRegions_3_2() {
      createRegions(3, 2);
    }

    public void onRegions_2_3() {
      createRegions(2, 3);
    }

    public void onRegions_3_3() {
      createRegions(3, 3);
    }

    public void onRegions_4_4() {
      createRegions(4, 4);
    }

    public void onCopy() {
      Clipboard cb = DefaultPage.this.getToolkit().getSystemClipboard();
      VectorGraphicsTransferable t = new VectorGraphicsTransferable(DefaultPage.this);
      cb.setContents(t, t);
    }

    public void onPrintSetup() {
      PrintHelper ph = new PrintHelper(DefaultPage.this, studio);
      ph.showOptionsDialog(studio);
    }

    public void onPrint() throws Exception {
      PrintHelper ph = new PrintHelper(DefaultPage.this, studio);
      //Temporary fix to JAS-410
      jas.plot.PrintHelper.instance().setPrintingThread(Thread.currentThread());
      ph.print();
      jas.plot.PrintHelper.instance().setPrintingThread(null);
    }

    public void onPrintPreview() throws Exception {
      PrintHelper ph = new PrintHelper(DefaultPage.this, studio);
      //Temporary fix to JAS-410
      jas.plot.PrintHelper.instance().setPrintingThread(Thread.currentThread());
      ph.printPreview(studio);
      jas.plot.PrintHelper.instance().setPrintingThread(null);
    }

    public void onSaveAs() {
      Properties user = studio.getUserProperties();
      String creator = user.getProperty("fullVersion");
      ExportDialog dlg = new ExportDialog(creator, true);
      dlg.setUserProperties(user);
      //Temporary fix to JAS-410
      jas.plot.PrintHelper.instance().setPrintingThread(Thread.currentThread());
      dlg.showExportDialog(studio, "Save As...", DefaultPage.this, "plotpage");
      jas.plot.PrintHelper.instance().setPrintingThread(null);
    }

    public void onClearAllRegions() {
      for (int i = 0; i < numberOfRegions(); i++) {
        region(i).clear();
      }
    }
  }
  
}

