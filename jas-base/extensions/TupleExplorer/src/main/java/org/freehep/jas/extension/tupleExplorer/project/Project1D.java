package org.freehep.jas.extension.tupleExplorer.project;

import jas.hist.DataSource;
import jas.hist.HasStyle;
import jas.hist.HistogramUpdate;
import jas.hist.JASHist1DHistogramStyle;
import jas.hist.JASHistStyle;
import jas.hist.Rebinnable1DHistogramData;
import java.util.Date;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection1D;
import org.freehep.util.Value;


/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class Project1D extends AbstractProjection1D implements Rebinnable1DHistogramData {
    
    private String colPathName;
    private int bins = 50;
    private double binWidth;
    private double[] data;
    private double[] newData;
    private double min = Double.NaN;
    private double max = Double.NaN;
    private int type;
    private MutableTupleTreeNavigator simpleCursor = null;
    private MutableTupleTreeNavigator mainCursor = null;
    private double[] vals = new double[1];
    
    private static final HistogramUpdate hu = new HistogramUpdate(HistogramUpdate.DATA_UPDATE,true);
    private Value value = new Value();
    
    //For saving/restoring only
    protected Project1D() {};
    
    public Project1D(MutableTupleColumn col) {
        setColumn(col);
    }
    
    protected void initProjection1D() {
        Class colType = column().type();
        if ( colType == Integer.TYPE || colType == Short.TYPE || colType == Byte.TYPE) {
            type = INTEGER;
            column().minValue(value);
            min = value.getDouble();
            column().maxValue(value);
            max = value.getDouble();
            int binWidth = (int) (1 + (max-min)/50);
            bins = (int) (1+(max-min)/binWidth);
            min -= 0.5;
            max += 0.5;
        } else if (Date.class.isAssignableFrom(column().type())) {
            type = DATE;
            column().minValue(value);
            min = value.getDate().getTime()/1000.;
            column().maxValue(value);
            max = value.getDate().getTime()/1000.;
        }
        else {
            type = DOUBLE;
            column().minValue(value);
            min = value.getDouble();
            min -= 0.05*Math.abs(min);
            column().maxValue(value);
            max = value.getDouble();
            max += 0.05*Math.abs(max);
        }
        //FIX to JAS-215 JAS-216
        if ( Double.isNaN(min) ) min = -1;
        if ( Double.isNaN(max) ) max = 1;

        colPathName = column().treePath().getParentPath().toString();
        initStatistics( new String[] {"x"} );
    }
        
    public int getBins() {
        return bins;
    }
    public int getAxisType() {
        return type;
    }
    public double getMax() {
        if (type == INTEGER) return max - 0.5;
        else return max;
    }
    public double getMin() {
        if (type == INTEGER) return min + 0.5;
        else return min;
    }
    public String getTitle() {
        return column().name();
    }
    public java.lang.String[] getAxisLabels() {
        return null;
    }
    public double[][] rebin(int bins, double min, double max, boolean param3, boolean param4) {
        if (bins!=this.bins || min!=this.min || max!=this.max) {
            this.bins = bins;
            this.min = min;
            this.max = max;
            run().run();
        }
        return new double[][]
        { data };
    }
    public boolean isRebinnable() {
        return true;
    }
    public void start() {
        binWidth = (max-min)/bins;
        newData = new double[bins];
        dataStatistics().reset();
    }
    public DataSource dataSource() {
        return this;
    }
    
    public void fill(MutableTupleTreeNavigator cursor) {
        double v;
        double val;
        
        if ( simpleCursor == null || mainCursor != cursor ) {
            simpleCursor =  cursor.cursorForPath( colPathName );
            mainCursor = cursor;
        }
        column().value( simpleCursor, value );
        
        if      (type==DATE   ) {
            Date d = value.getDate();
            if (d == null) return;
            val = d.getTime()/1000.;
        }
        else if (type==INTEGER) val = value.getDouble();
        else                    val = value.getDouble();
        v = val - min;
        if (v<0) return ;
        int bin = (int) Math.floor(v/binWidth);
        if (bin>=bins) return;
        newData[bin]++;
        vals[0] = val;
        dataStatistics().addEntry(vals);        
    }
    
    public void end() {
        data = newData;
        setChanged();
        notifyObservers(hu);
    }
    
}