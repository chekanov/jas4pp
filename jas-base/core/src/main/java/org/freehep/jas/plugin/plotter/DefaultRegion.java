package org.freehep.jas.plugin.plotter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.AbstractBorder;
import org.freehep.jas.services.PlotFactory;
import org.freehep.jas.services.PlotRegion;
import org.freehep.jas.services.PlotRegionDropHandler;
import org.freehep.jas.services.Plotter;
import org.freehep.swing.popup.HasPopupItems;

public class DefaultRegion extends JPanel implements PlotRegion, DropTargetListener, HasPopupItems {

  private final RegionBorder border = new RegionBorder();
  private final PlotFactory factory;
  private final PlotRegionDropHandler defaultDropHandler;
  static private final Color activeBorderColor = Color.red;
  static private final Color defaultBorderColor = Color.green;
  static private final Color selectedBorderColor = Color.blue;
  
  private Plotter plotter;
  private boolean selected = false;
  
  private ArrayList<HasPopupItems> popup;
  
  
// -- Construction : -----------------------------------------------------------

  DefaultRegion(PlotFactory factory) {
    super(new BorderLayout());
    this.factory = factory;
    setBorder(border);
    border.setColor(defaultBorderColor);
    defaultDropHandler = new DefaultPlotRegionDropHandler(this, factory);
    setOpaque(false);
    DropTarget dt = new DropTarget(this, this);
  }
  
  
// -- Operations on region : ---------------------------------------------------

  void setSelected(boolean selected) {
    this.selected = selected;
    border.setColor(selected ? selectedBorderColor : defaultBorderColor);
    repaint();
  }

  
// -- Implementing PlotRegion : ------------------------------------------------

  @Override
  public void showPlot(Plotter plotter) {
    removeAll();
    this.plotter = plotter;
    add(plotter.viewable(), BorderLayout.CENTER);
    revalidate();
    repaint();
  }

  @Override
  public void clear() {
    if (plotter != null) {
      plotter.clear();
      plotter = null;
    }
    removeAll();
    revalidate();
    repaint();
  }

  @Override
  public Plotter currentPlot() {
    return plotter;
  }


// -- Implementing DropTargetListener : ----------------------------------------

  @Override
  public void dragEnter(DropTargetDragEvent dtde) {
    if (acceptOrReject(dtde)) {
      border.setColor(activeBorderColor);
      repaint();
    }
  }

  @Override
  public void dragExit(DropTargetEvent dte) {
    border.setColor(selected ? selectedBorderColor : defaultBorderColor);
    repaint();
  }

  @Override
  public void dragOver(DropTargetDragEvent dtde) {
  }

  @Override
  public void drop(DropTargetDropEvent dtde) {
    border.setColor(defaultBorderColor);
    repaint();
    factory.currentPage().setCurrentRegion(this);
    try {
      PlotRegionDropHandler dh = getPlotRegionDropHandler(dtde);
      if (dh == null) {
        defaultDropHandler.drop(dtde);
      } else {
        dh.setPlotRegion(this);
        dh.drop(dtde);
      }
    } catch (Throwable x) {
      x.printStackTrace();
      dtde.dropComplete(false);
    }
  }

  @Override
  public void dropActionChanged(DropTargetDragEvent dtde) {
  }

  public PlotRegionDropHandler getPlotRegionDropHandler(DropTargetDropEvent dtde) {
    PlotRegionDropHandler dh = null;
    try {
      Transferable t = dtde.getTransferable();
      DataFlavor[] flavors = t.getTransferDataFlavors();
      for (int i = 0; i < flavors.length; i++) {
        Class k = flavors[i].getRepresentationClass();
        if (PlotRegionDropHandler.class.isAssignableFrom(k)) {
          dh = (PlotRegionDropHandler) t.getTransferData(flavors[i]);
          break;
        }
      }
    } catch (Throwable x) {
      x.printStackTrace();
      dtde.dropComplete(false);
    }
    return dh;
  }

  boolean acceptOrReject(DropTargetDragEvent e) {
    boolean accept = false;
    DataFlavor[] flavors = e.getCurrentDataFlavors();
    for (int i = 0; i < flavors.length; i++) {
      Class k = flavors[i].getRepresentationClass();
      // Test if a plotter is available for this class
      accept = Plotter.class.isAssignableFrom(k) || factory.canCreatePlotterFor(k);
      if (accept) {
        break;
      }
    }

    if (accept) {
      e.acceptDrag(DnDConstants.ACTION_LINK);
    } else {
      e.rejectDrag();
    }
    return accept;
  }
  
  
// -- Handling popup : ---------------------------------------------------------

  @Override
  public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {
    JMenuItem item = new JMenuItem("Clear Region");
    item.setActionCommand("clear");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        if ("clear".equals(actionEvent.getActionCommand())) {
          clear();
        }
      }
    });
    menu.add(item);
    if (popup != null) {
      for (HasPopupItems it : popup) {
        menu = it.modifyPopupMenu(menu, source, p);
      }
    }
    return menu;
  }
  
  /**
   * Adds an additional popup items set to this region.
   * @param items Item set to be added.
   * @return True.
   */
  public boolean addPopupItems(HasPopupItems items) {
    if (popup == null) popup = new ArrayList<>(1);
    return popup.add(items);
  }
  
  /**
   * Removes additional popup item sets from this region.
   * 
   * @param items The item set to be removed. If <tt>null</tt>, all sets satisfying the filter are removed.
   * @param filter Only item sets satisfying this filter are removed. If <tt>null</tt>, all sets are removed.
   * @return True if any item sets were removed.
   */
  public boolean removePopupItems(HasPopupItems items, Predicate<HasPopupItems> filter) {
    if (popup == null || popup.isEmpty()) return false;
    if (items == null) {
      if (filter == null) {
        popup.clear();
        return true;
      } else {
//        return popup.removeIf(filter); // need to support 1.7 for now
        Iterator<HasPopupItems> it = popup.iterator();
        boolean out = false;
        while (it.hasNext()) {
          if (filter.test(it.next())) {
            it.remove() ;
            out = true;
          }
        }
        return out;
      }
    } else {
      if (filter == null || filter.test(items) ) {
        return popup.remove(items);
      } else {
        return false;
      }
    }
  }
  
  /** To be removed and replaced with java.util.function.Predicate when JRE 1.7 support is dropped. */
  public interface Predicate<T> {
    boolean test(T t);
  }
  
  
// -- Overriding JComponent : --------------------------------------------------
  
  @Override
  protected void printBorder(Graphics g) {
    // We dont want to print the border, which is just used to show the current selection
  }

}


// -- Border class : -----------------------------------------------------------

class RegionBorder extends AbstractBorder {

  private static final Insets insets = new Insets(1, 1, 1, 1);
  private Color color;

  void setColor(Color color) {
    this.color = color;
  }

  @Override
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    g.setColor(color != null ? color : c.getBackground());
    g.drawRect(x, y, width - 1, height - 1);
  }

  @Override
  public boolean isBorderOpaque() {
    return true;
  }

  @Override
  public Insets getBorderInsets(Component c) {
    return insets;
  }
  
}

