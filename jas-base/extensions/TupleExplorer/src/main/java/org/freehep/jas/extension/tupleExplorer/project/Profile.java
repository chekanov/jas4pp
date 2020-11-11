package org.freehep.jas.extension.tupleExplorer.project;

import hep.aida.ref.dataset.DataStatistics;
import jas.hist.DataSource;
import jas.hist.HasStyle;
import jas.hist.HistogramUpdate;
import jas.hist.JASHist1DHistogramStyle;
import jas.hist.JASHistStyle;
import jas.hist.Rebinnable1DHistogramData;
import java.util.Date;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection2D;
import org.freehep.util.Value;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class Profile extends AbstractProjection2D implements Rebinnable1DHistogramData, HasStyle {
    
    private String colPathName;
    private String colWPathName;
    private double min = Double.NaN;
    private double max = Double.NaN;
    private int bins = 50;
    private double binWidth;
    private double[] data, dataErrors;
    private double[] n, hist, error;
    private JASHist1DHistogramStyle style = new JASHist1DHistogramStyle();
    private Value value = new Value();
    
    private int type;
    private static final HistogramUpdate hu = new HistogramUpdate(HistogramUpdate.DATA_UPDATE,true);
    private double[] vals = new double[2];

    private MutableTupleTreeNavigator simpleCursor = null;
    private MutableTupleTreeNavigator simpleCursorW = null;
    private MutableTupleTreeNavigator mainCursor = null;

    //For saving/restoring only
    protected Profile() {}
    
    public Profile(MutableTupleColumn col, MutableTupleColumn colW) {
        setColumns(col,colW);
    }
        
    protected void initProjection2D() {
        style.setShowHistogramBars(false);
        style.setShowDataPoints(true);
        
        if (columnX().type() == Integer.TYPE) {
            type = INTEGER;
            columnX().minValue(value);
            min = value.getDouble();
            columnX().maxValue(value);
            max = value.getDouble();
            int binWidth = (int) (1 + (max-min)/50);
            bins = (int) (1+(max-min)/binWidth);
            min -= 0.5;
            max += 0.5;
        }
        else if (Date.class.isAssignableFrom(columnX().type())) {
            type = DATE;
            columnX().minValue(value);
            min = value.getDate().getTime()/1000.;
            columnX().maxValue(value);
            max = value.getDate().getTime()/1000.;
        }
        else {
            type = DOUBLE;
            columnX().minValue(value);
            min = value.getDouble();
            columnX().maxValue(value);
            max = value.getDouble();
            min = min - 0.05*Math.abs(min);
            max = max + 0.05*Math.abs(max);
        }
        
        //FIX to JAS-215 JAS-216
        if ( Double.isNaN(min) ) min = -1;
        if ( Double.isNaN(max) ) max = 1;

        colPathName = columnX().treePath().getParentPath().toString();
        colWPathName = columnY().treePath().getParentPath().toString();
        initStatistics( new String[] {"x","y"} );
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
        return columnY().name() + " vs "+columnX().name();
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
        return new double[][] { data, dataErrors };
    }
    public boolean isRebinnable() {
        return true;
    }
    public void start() {
        //initProfile();
        binWidth = (max-min)/bins;
        n = new double[bins];
        hist = new double[bins];
        error = new double[bins];
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
            simpleCursorW =  cursor.cursorForPath( colWPathName );
            mainCursor = cursor;
        }
        columnX().value( simpleCursor, value );
        
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
        n[bin]++;

        columnY().value( simpleCursorW, value );

        double w = value.getDouble();
        hist[bin] += w;
        error[bin] += w*w;
        
        vals[0] = val;
        vals[1] = w;
        dataStatistics().addEntry(vals);
        
    }
    public void end() {
        for (int i=0; i<error.length; i++) {
            hist[i] /= n[i];
            error[i] = Math.sqrt(error[i]/n[i] - hist[i]*hist[i]);
        }
        data = hist;
        dataErrors = error;
        setChanged();
        notifyObservers(hu);
    }
    
    public JASHistStyle getStyle() {
        return style;
    }    
}
