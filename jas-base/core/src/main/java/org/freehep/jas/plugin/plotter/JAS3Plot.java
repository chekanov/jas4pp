package org.freehep.jas.plugin.plotter;

import jas.hist.DataSource;
import jas.hist.JASHist;
import jas.hist.JASHistData;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.freehep.application.Application;
import org.freehep.application.PrintHelper;
import org.freehep.application.studio.Studio;
import org.freehep.graphicsbase.util.export.ExportDialog;
import org.freehep.graphicsbase.util.export.VectorGraphicsTransferable;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.jas.services.PlotFactory;
import org.freehep.jas.services.PlotPage;
import org.freehep.jas.services.PlotRegion;
import org.freehep.swing.popup.HasPopupItems;

import org.freehep.util.FreeHEPLookup;

/**
 * Viewable component of the Jas3 default plotter.
 */
public class JAS3Plot extends JASHist implements HasPopupItems, ActionListener {
  
    private ArrayList<JAS3DataSource> dataList = new ArrayList<>(1);
    
// -- Construction : -----------------------------------------------------------
    
    protected JAS3Plot() {
        setShowStatistics(true);
        setBackground(java.awt.Color.white);
        setAllowPopupMenus(false);
        
        /**
         * This piece of code is to propagate a mouse clicked event to the underlying
         * plot page to make the region on which the plot is the current one.
         */
        addMouseListener( new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Component region = getParent();
                if ( region instanceof PlotRegion ) {
                    Component page = region.getParent();
                    if ( page instanceof PlotPage )
                        ( (PlotPage) page ).setCurrentRegion( (PlotRegion)region );
                }
            }
        });
        //*********************************//
    }
    

// -- Operations and getters : -------------------------------------------------
    
    protected List<JAS3DataSource> data() {
        return dataList;
    }
    
    public void addJAS3Data(JAS3DataSource ds) {
        if ( dataList.contains(ds) ) return;
        dataList.add(ds);
    }
    
    public void removeJAS3Data( DataSource ds ) {
      for (int i = 0; i < dataList.size(); i++) {
        if ((dataList.get(i)).dataSource() == ds) {
          dataList.remove(i);
          break;
        }
      }
    }
    
    void clearDataList() {
        for (JAS3DataSource ds : dataList) {
            ds.destroy();
        }
        dataList.clear();
    }
    
    private void addPlotMenu(JPopupMenu menu, Point p) {
        try {
            Object plot = ((PlotFactory) FreeHEPLookup.instance().lookup(PlotFactory.class)).currentPage().currentRegion().currentPlot();
            if (plot instanceof org.freehep.swing.popup.HasPopupItems)
                ((org.freehep.swing.popup.HasPopupItems) plot).modifyPopupMenu(menu, this, p);
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public String getSuperTitle() {
        return super.getTitle();
    }
    
    
// -- Overriding JASHist : -----------------------------------------------------
    
    @Override
    public String getTitle() {
        setTitleAndAxisLabels(this);
        return super.getTitle();
    }
    
    @Override
    public JASHistData addData(DataSource ds) {
        JASHistData jasHistData = super.addData(ds);
        setTitleAndAxisLabels(this);
        return jasHistData;
    }
    
    @Override
    public void removeAllData() {
        super.removeAllData();
        clearDataList();
    }
    
    @Override
    public void destroy() {
        java.awt.Container c = getParent();
        if (c != null) {
            c.remove(this);
            c.repaint();
        }
        super.destroy();
        clearDataList();
    }
    
    
// -- Handling pop-up menu commands : ------------------------------------------
    
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();
        if (command.equals("save")) {
            Application studio = Application.getApplication();
            Properties user = studio.getUserProperties();
            String creator = user.getProperty("fullVersion");
            ExportDialog dlg = new ExportDialog(creator,true);
            dlg.addExportFileType(new PlotMLExportFileType());
            dlg.setUserProperties(user);
            //Temporary fix to JAS-410
            jas.plot.PrintHelper.instance().setPrintingThread(Thread.currentThread());
            dlg.showExportDialog(studio, "Save As...",  JAS3Plot.this, "plot");
            jas.plot.PrintHelper.instance().setPrintingThread(null);
        } else if (command.equals("copy")) {
            Clipboard cb = JAS3Plot.this.getToolkit().getSystemClipboard();
            VectorGraphicsTransferable t = new VectorGraphicsTransferable(JAS3Plot.this);
            cb.setContents(t,t);
        } else if (command.equals("print")) {
            Studio studio = (Studio) Application.getApplication();
            try {
                PrintHelper ph = new PrintHelper(JAS3Plot.this, studio);
                //Temporary fix to JAS-410
                jas.plot.PrintHelper.instance().setPrintingThread(Thread.currentThread());
                ph.print();
                jas.plot.PrintHelper.instance().setPrintingThread(null);
            } catch (PrinterException x) {
                studio.error("Error printing plot",x);
            }
        }
    }

    @Override
    public JPopupMenu modifyPopupMenu(JPopupMenu menu,Component source,Point p) {
        for (Component c=source; c != null; c = c.getParent()) {
            if (c instanceof jas.plot.HasPopupItems) ((jas.plot.HasPopupItems) c).modifyPopupMenu(menu, source);
        }
        int data = dataList.size();
        for( int i = 0; i < data; i++ )
            ((JAS3DataSource) dataList.get(i)).modifyPopupMenu(menu, source);
        
        // Do some work on the menu!
        for (int i=0; i<menu.getComponentCount();i++) {
            Object item = menu.getComponent(i);
            if (item instanceof JMenuItem) {
                JMenuItem mItem = (JMenuItem) item;
                String name = mItem.getText();
                //                if      (name.indexOf("function") >= 0) menu.remove(i--);
                //                else if (name.equals("Fit"))            menu.remove(i--);
                if (name.indexOf("Advanced") >= 0) menu.remove(i--);
                else if (name.equals("Save Plot As...")) menu.remove(i--);
                //                else if (name.equals("Save Plot As...")) menu.remove(i--);
                else if (name.equals("Print")) menu.remove(i--);
                else if (name.equals("Copy Plot to Clipboard...")) menu.remove(i--);
            }
        }
        
        addPlotMenu(menu, p);
        
        menu.addSeparator();
        JMenuItem item = new JMenuItem("Copy Plot...");
        item.setActionCommand("copy");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem("Save Plot As...");
        item.setActionCommand("save");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem("Print");
        item.setActionCommand("print");
        item.addActionListener(this);
        menu.add(item);
        
        return menu;
    }

    
// -- Local methods and classes : ----------------------------------------------
    
    private JAS3DataSource jas3DataSource( DataSource ds, ArrayList list ) {
        for ( int i = 0; i < list.size(); i++ )
            if ( ( (JAS3DataSource) list.get(i) ).dataSource() ==  ds )
                return (JAS3DataSource) list.get(i);
        return new J3Ds( ds );
    }
    
    private void setTitleAndAxisLabels(JAS3Plot plot) {
        setTitle("");
        
        ArrayList data = new ArrayList();
        
        // This is needed to keep track of which functions are added or removed
        // via the GUI.
        Enumeration en = plot.get1DFunctions();
        if ( en != null ) {
            while ( en.hasMoreElements() )
                data.add( en.nextElement() );
        }
        
        Enumeration en1 = plot.getDataSources();
        while ( en1.hasMoreElements() )
            data.add( en1.nextElement() );
        
        ArrayList tmpDataList = (ArrayList)( (ArrayList)plot.data() ).clone();
        
        getYAxis().setLabel("");
        getXAxis().setLabel("");
        int size = plot.data().size();
        FTreePath[] paths = new FTreePath[size];
        
        String[] xLabels = new String[size];
        String[] yLabels = new String[size];
        dataList = new ArrayList();
        
        for ( int i = 0; i < size; i++ ) {
            JAS3DataSource ds = jas3DataSource( ((JASHistData)data.get(i)).getDataSource(), tmpDataList );
            plot.data().add(ds);
            paths[i] = ds.path();
            String[] axisLabels = ds.axisLabels();
            if ( axisLabels != null ) {
                xLabels[i] = axisLabels[0];
                if ( axisLabels.length > 1 )
                    yLabels[i] = axisLabels[1];
            }
        }
        
        if ( size == 1 ) {
            DataSource ds = ( (JAS3DataSource) plot.data().get(0) ).dataSource();
            setTitle( ds.getTitle() );
            if ( xLabels[0] != null )
                getXAxis().setLabel( xLabels[0] );
            if ( yLabels[0] != null )
                getYAxis().setLabel( yLabels[0] );
        } else if ( size > 1 ) {
            int length = paths[0].getPathCount();
            for ( int i = 1; i < size; i++ )
                if ( length != paths[i].getPathCount() ) length = -1;
            
            if ( length > 0 ) {
                String[] commonString = new String[length];
                int commonItems = length;
                for ( int j = 0; j < length; j++ ) {
                    commonString[j] = paths[0].getPathComponent(j);
                    for ( int k = 1; k < size; k++ ) {
                        if ( ! paths[k].getPathComponent(j).equals( commonString[j] ) ) {
                            commonString[j] = null;
                            commonItems--;
                            break;
                        }
                    }
                }
                
                if ( commonItems > 0 ) {
                    String title = null;
                    for ( int i = 0; i < length; i++ )
                        if ( commonString[i] != null )
                            if ( title == null )
                                title = commonString[i];
                            else
                                title += " - "+commonString[i];
                    setTitle(title);
                    
                    // Set the legend
                    for (int k=0; k<data.size(); k++) {
                        JASHistData d = (JASHistData) data.get(k);
                        StringBuffer legend = new StringBuffer();                                                
                        for ( int i = 0; i < length; i++ ) {
                            if ( commonString[i] == null ) {
                                if (legend.length() > 0) legend.append(" - ");
                                legend.append(paths[k].getPathComponent(i));
                            }
                        }
                        d.setLegendText(legend.toString());
                    }
                    return;
                }
            }
            
            String title = paths[0].getLastPathComponent();
            //                String xLabel = xLabels[0] != null ? xLabels[0] : "";
            //                String yLabel = yLabels[0] != null ? yLabels[0] : "";
            for ( int i = 1; i < size; i++ ) {
                title += " - "+paths[i].getLastPathComponent();
                //                    xLabel += xLabels[i] != null ? " - "+xLabels[i] : " - ";
                //                    yLabel += yLabels[i] != null ? " - "+yLabels[i] : " - ";
            }
            setTitle( title );
            //                getXAxis().setLabel( xLabel );
            //                getYAxis().setLabel( yLabel );
        }
        getStats().repaint();
    }
    
    private class J3Ds implements JAS3DataSource {
        
        private DataSource ds;
        
        J3Ds( DataSource ds ) {
            this.ds = ds;
        }
        
        public String[] axisLabels() {
            return null;
        }
        
        public DataSource dataSource() {
            return ds;
        }
        
        public void destroy() {
        }
        
        public void modifyPopupMenu(JPopupMenu jPopupMenu, Component component) {
        }
        
        public FTreePath path() {
            return new FTreePath(ds.getTitle());
        }
        
        public void setAxisType(int type) {
        }
        
    }
}