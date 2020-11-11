package org.freehep.jas.extension.tupleExplorer.plot;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author tonyj
 * @version $Id: PlotSet.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public class PlotSet {
    
    protected EventListenerList listeners = new EventListenerList();
    public List plots = new ArrayList();
    
    /** Creates new PlotSet */
    public PlotSet() {
    }
    
    public void add(Plot plot) {
        int index = plots.size();
        plots.add(plot);
        firePlotAdded(index);
    }
    public void remove(Plot plot) {
        int index = plots.indexOf(plot);
        plots.remove(plot);
        firePlotRemoved(index);
    }
    public int getNPlots() {
        return plots.size();
    }
    public Plot getPlot(int index) {
        return (Plot) plots.get(index);
    }
    public int indexOf(Plot plot) {
        return plots.indexOf(plot);
    }
    public void addListDataListener(ListDataListener l) {
        listeners.add(ListDataListener.class,l);
    }
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(ListDataListener.class,l);
    }
    private void firePlotAdded(int index) {
        if (listeners.getListenerCount(ListDataListener.class) == 0) return;
        ListDataEvent e = new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,index,index);
        ListDataListener[] l = (ListDataListener[]) listeners.getListeners(ListDataListener.class);
        for (int i=0; i<l.length; i++) {
            l[i].intervalAdded(e);
        }
    }
    private void firePlotRemoved(int index) {
        if (listeners.getListenerCount(ListDataListener.class) == 0) return;
        ListDataEvent e = new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,index,index);
        ListDataListener[] l = (ListDataListener[]) listeners.getListeners(ListDataListener.class);
        for (int i=0; i<l.length; i++) {
            l[i].intervalRemoved(e);
        }
    }
    
    public Plot[] outOfDatePlots() {
        int n = getNPlots();
        Vector tmpPlots = new Vector();
        for (int i=0; i<n; i++) {
            Plot p = getPlot(i);
            if (p.isOutOfDate()) 
                tmpPlots.add(p);
        }

        tmpPlots.trimToSize();
        int nOutOfDatePlots = tmpPlots.size();

        Plot[] outOfDatePlots = new Plot[nOutOfDatePlots];
        for (int i=0; i<nOutOfDatePlots; i++) 
            outOfDatePlots[i] = (Plot) tmpPlots.elementAt(i);
        return outOfDatePlots;
    }
}