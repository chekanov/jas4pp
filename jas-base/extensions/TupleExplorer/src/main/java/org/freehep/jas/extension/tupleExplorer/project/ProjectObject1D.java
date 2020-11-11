package org.freehep.jas.extension.tupleExplorer.project;

import jas.hist.DataSource;
import jas.hist.HasStyle;
import jas.hist.HistogramUpdate;
import jas.hist.JASHist1DHistogramStyle;
import jas.hist.JASHistStyle;
import jas.hist.Rebinnable1DHistogramData;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection1D;
import org.freehep.util.Value;


/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class ProjectObject1D extends AbstractProjection1D implements Rebinnable1DHistogramData {
    
    private String colPathName;
    private String[] labels;
    private double[] counts;
    private HashMap map;
    
    private static final HistogramUpdate hu = new HistogramUpdate(HistogramUpdate.DATA_UPDATE,true);
    private Value value = new Value();
    private double[] vals = new double[1];
    
    private MutableTupleTreeNavigator simpleCursor = null;
    private MutableTupleTreeNavigator mainCursor = null;

    //For saving/restoring only
    protected ProjectObject1D() {};
        
    public ProjectObject1D(MutableTupleColumn col) {
        setColumn(col);
    }
    
    protected void initProjection1D() {
        colPathName = column().treePath().getParentPath().toString();
        initStatistics( new String[] {"x"} );
    }
    
    public int getBins() {
        return labels.length;
    }
    public int getAxisType() {
        return STRING;
    }
    public double getMax() {
        return labels.length;
    }
    public double getMin() {
        return 0;
    }
    public String getTitle() {
        return column().name();
    }
    public String[] getAxisLabels() {
        return labels;
    }
    public double[][] rebin(int bins, double min, double max, boolean param3, boolean param4) {
        return new double[][] { counts };
    }
    public boolean isRebinnable() {
        return false;
    }
    public DataSource dataSource() {
        return this;
    }
    public void start() {
        map = new HashMap();
        dataStatistics().reset();
    }
    public void fill(MutableTupleTreeNavigator cursor) {
        
        if ( simpleCursor == null || mainCursor != cursor ) {
            simpleCursor =  cursor.cursorForPath( colPathName );
            mainCursor = cursor;
        }
        column().value( simpleCursor, value );
        
        Object o = value.getObject();
        Count count = (Count) map.get(o);
        if (count != null) count.increment();
        else map.put(o,new Count(1));
        dataStatistics().addEntry(vals);        
    }
    public void end() {
        Comparator c = new Comparator() {
            public int compare(Object o1, Object o2) {
                Count c1 = (Count) map.get(o1);
                Count c2 = (Count) map.get(o2);
                return c2.getValue() - c1.getValue();
            }
        };
        List keys = new LinkedList(map.keySet());
        Collections.sort(keys,c);
        
        int n = keys.size();
        if (n>20) n = 20;
        labels = new String[n];
        counts = new double[n];
        int i = 0;
        for (Iterator it=keys.iterator(); it.hasNext(); i++) {
            Object o = it.next();
            if (i < n) {
                labels[i] = o == null ? "null" : o.toString();
                //FIX to JAS-210
//                if (labels[i].length()>10) labels[i] = labels[i].substring(0,6)+"...";
                counts[i] = ((Count) map.get(o)).getValue();
            }
            else {
                labels[n-1] = "Other";
                counts[n-1] += ((Count) map.get(o)).getValue();
            }
        }
        map = null;
        setChanged();
        notifyObservers(hu);
    }
    
    private class Count {
        Count(int i) {
            this.i = i;
        }
        void increment() {
            i++;
        }
        int getValue() {
            return i;
        }
        private int i;
    }
    
}
