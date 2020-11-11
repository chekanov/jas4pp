package org.freehep.jas.extension.tupleExplorer.project;

import hep.aida.ref.dataset.DataStatistics;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.jas.plugin.plotter.JAS3DataSource;
import jas.hist.DataSource;
import jas.hist.ExtendedStatistics;
import jas.hist.HasStatistics;
import jas.hist.Statistics;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.Observable;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.freehep.application.Application;
import org.freehep.jas.extension.tupleExplorer.cut.CutDialog;
import org.freehep.jas.extension.tupleExplorer.plot.Plot;
import org.freehep.jas.services.PlotRegion;

/**
 *
 * @author The FreeHEP team @ SLAC
 * @version
 *
 */
public abstract class AbstractProjection extends Observable implements JAS3DataSource, ActionListener, HasStatistics {
    
    private Component component;
    private Plot plot = null;
    private String name;
    private Runnable run;
    private PlotRegion region = null;
    private DataStatistics dataStatistics;
    
    public abstract FTreePath getLeadingPath();
    public abstract void fill(MutableTupleTreeNavigator cursor);
    
    public abstract void start();
    public abstract void end();
        
    public void setRunnable(Runnable run) {
        this.run = run;
    }
    
    protected Runnable run() {
        return run;
    }
    
    public void setPlot(Plot plot) {
        this.plot = plot;
    }
    
    private Plot plot() {
        return plot;
    }
    
    public void setName( String name ) {
        this.name = name;
    }
    
    private String name() {
        return name;
    }
    
    public void destroy() {
        plot.destroy();
    }
    
    
    public void modifyPopupMenu(JPopupMenu jPopupMenu, Component component) {
        this.component = component;
        JMenuItem item = new JMenuItem("Cuts for plot "+name());
        item.setActionCommand("cuts");
        item.addActionListener(this);
        jPopupMenu.add(item);
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();
        if (command.equals("cuts"))
            CutDialog.show(component,plot().getCuts(),plot().getTuple());
    }
    
    public void setRegion( PlotRegion region ) {
        this.region = region;
    }
    
    public JAS3DataSource jas3DataSource() {
        return this;
    }
    
    protected DataStatistics dataStatistics() {
        return dataStatistics;
    }
    
    protected void initStatistics( String[] descriptions ) {
        dataStatistics = new DataStatistics( descriptions );
    }
    
    public Statistics getStatistics() {
        return new ProjectionStatistics(dataStatistics);
    }
    
    public abstract FTreePath path();
    
    public abstract String[] axisLabels();
    
    /** This method was added to fulfill BaBar's needs to have
     * DataPointSets with Dates on the axis.
     *
     *
     */
    public void setAxisType(int type) {
    }
    
    private class ProjectionStatistics implements ExtendedStatistics {
        
        private DataStatistics dataStat;
        private int dim;
        private String[] names;
        private ArrayList list = new ArrayList();
        private boolean showSumOfWeights;
        private boolean showEquivEntries;
        private int globStat;
        
        ProjectionStatistics( DataStatistics dataStat ) {
            this.dataStat = dataStat;
            this.dim = dataStat.dimension();
            reset();
        }
        
        public String[] getStatisticNames() {
            reset();
            int entries = dataStat.entries();
            if ( entries == dataStat.sumOfWeights() ) {
                globStat--;
                showSumOfWeights = false;
            }
            if ( entries == dataStat.equivalentEntries() ) {
                globStat--;
                showEquivEntries = false;
            }
                
            this.names = new String[ globStat + 2*dim ];
            int count = 0;
            names[count++] = "Entries";
            if ( showSumOfWeights ) names[count++] = "SumOfWeights";
            if ( showEquivEntries ) names[count++] = "EquivEntries";
            for ( int i = 0; i < dim; i++ ) {
                String desc = dataStat.description(i);
                names[ globStat + i*dim ] = desc+" Mean";
                names[ globStat + 1 + i*dim ] = desc+" Rms";
            }
            
            for ( int i = 0; i < names.length; i++ )
                list.add( names[i] );
            
            return names;
        }
        
        public double getStatistic(String name) {
            int index = list.indexOf( name );
            if ( index < globStat ) return Double.NaN;
            index -= globStat;
            int coord = index/2;
            int stat  = coord != 0 ? index%(coord*2) : index;
            if ( stat == 0 ) return dataStat.mean(coord);
            if ( stat == 1 ) return dataStat.rms(coord);
            throw new IllegalArgumentException("Unsupported stat "+stat);
        }
        
        public Object getExtendedStatistic(String name) {
            if (name.equals("Entries"))      return new Integer(dataStat.entries());
            if (name.equals("SumOfWeights")) return new Double(dataStat.sumOfWeights());
            if (name.equals("EquivEntries")) return new Double(dataStat.equivalentEntries());
            return null;
        }
        
        public void reset() {
            globStat = 3;
            showSumOfWeights = true;
            showEquivEntries = true;
        }
    }
}