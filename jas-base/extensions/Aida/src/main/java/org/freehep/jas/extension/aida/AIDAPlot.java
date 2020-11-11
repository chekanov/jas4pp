package org.freehep.jas.extension.aida;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import hep.aida.IPlotterStyle;
import hep.aida.ref.plotter.style.editor.IStyleEditor;
import jas.hist.Basic1DFunction;
import org.freehep.application.studio.Studio;
import org.freehep.jas.plugin.plotter.DefaultPlotter;
import org.freehep.jas.plugin.plotter.JAS3DataSource;
import org.freehep.jas.services.PlotFactory;
import org.freehep.jas.services.Plotter;

import org.freehep.swing.popup.HasPopupItems;
import org.freehep.util.FreeHEPLookup;

/**
 * {@link Plotter} for AIDA objects.
 *
 * @author  tonyj
 */
class AIDAPlot extends DefaultPlotter implements HasPopupItems, ActionListener {
    
    static private final String separator = ". ";
    private final AIDAPlotAdapter adapter;
    private final PlotFactory factory;
    
    private AIDARegion region;
    
    AIDAPlot( PlotFactory factory, AIDAPlugin thePlugin, Studio studio ) {
        super( factory );
        adapter = new AIDAPlotAdapter(thePlugin, studio);
        this.factory = factory;
    }
    
    void setPlotterRegion( AIDARegion region ) {
        this.region = region;
    }
    
    @Override
    public void plot(Object data, int mode, Object style, String options) {
        
        if ( region == null ) {
            region = new AIDARegion(factory, factory.currentPage().currentRegion());
            region.setPlot( getPlot() );
        }
        
        if (mode == Plotter.NORMAL) {
            clear();
        }
        
        //TODO
        //The if statement below is so that we can also plot tuple columns from the
        //TupleExplorer. The code should not be here. The Plotter interface should
        //be changed.
        JAS3DataSource jas3DataSource;
        if ( data instanceof JAS3DataSource ) {
            jas3DataSource = (JAS3DataSource) data;
        } else {
            jas3DataSource = (JAS3DataSource)adapter.adapt(data);
        }
        
        //Make sure it is possible to plot a function
        if ( jas3DataSource.dataSource() instanceof Basic1DFunction && getPlot().getNumberOfDataSources() == 0 ) {
            throw new IllegalArgumentException("Currently it is not possible to plot a function on an empty plot.");
        }
        
        //The following line is repeated twice to solve JAS-425.
        //When data is plotted in NORMAL mode on a plot containing other data the plotter region
        //will clear the content of the jas3Plot and the two will be offset by one plot. This is
        //why it is necessary to add the data to the jas3Plot after it is added to the region. On the
        //other hand it must be added before the region to evaluate correctely the title. 
        //This uglyness must be fixed at some point.
        getPlot().addJAS3Data(jas3DataSource);
        region.add(jas3DataSource.dataSource(), (IPlotterStyle)style, mode, data, options);
        getPlot().addJAS3Data(jas3DataSource);
        
        getData().add(data);
    }
    
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();
        
        if (command.equals("editAllAIDAStyles")) {
            String[] names = region.getAllDataNames();
            String title = "AIDA Region Styles";
            if (names == null) return;
            IPlotterStyle[] styles = new IPlotterStyle[names.length];
            for (int i=0; i<names.length; i++) {
                styles[i] = region.getStyleForName(names[i]);
            }
            IStyleEditor styleEditor = (IStyleEditor) FreeHEPLookup.instance().lookup(IStyleEditor.class);
            styleEditor.edit(styles, title, false);
            
        } else if (command.equals("editAIDAStyle")) {
            Object obj = actionEvent.getSource();
            String dataTitle = obj.toString();
            if (obj instanceof JMenuItem) dataTitle = ((JMenuItem) obj).getText();
            
            int index = dataTitle.indexOf(separator);
            if (index >= 0) dataTitle = dataTitle.substring(index + separator.length());
            IPlotterStyle sty = region.getStyleForName(dataTitle);
            IStyleEditor styleEditor = (IStyleEditor) FreeHEPLookup.instance().lookup(IStyleEditor.class);
            styleEditor.edit(sty, dataTitle, false);
        }
    }

    @Override
    public JPopupMenu modifyPopupMenu(JPopupMenu menu,Component source,Point p) {
        IStyleEditor styleEditor = (IStyleEditor) FreeHEPLookup.instance().lookup(IStyleEditor.class);
        if (styleEditor == null) return menu;
        
        JMenu styleMenu = new JMenu("Edit AIDA Style for");        
        String[] names = region.getAllDataNames();
        if (names == null) return menu;
        
        JMenuItem item = item = new JMenuItem("Whole Region");
        item.setActionCommand("editAllAIDAStyles");
        item.addActionListener(this);
        styleMenu.add(item);
            
        for (int i=0; i<names.length; i++) {
            item = new JMenuItem(i+separator+names[i]);
            item.setActionCommand("editAIDAStyle");
            item.addActionListener(this);
            styleMenu.add(item);
        }
        menu.add(styleMenu);
        return menu;
    }
    
}
