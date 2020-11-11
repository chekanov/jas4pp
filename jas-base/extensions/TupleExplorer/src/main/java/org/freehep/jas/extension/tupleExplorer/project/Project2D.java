package org.freehep.jas.extension.tupleExplorer.project;

import jas.hist.DataSource;
import jas.hist.HasStyle;
import jas.hist.HistogramUpdate;
import jas.hist.JASHist1DHistogramStyle;
import jas.hist.JASHistStyle;
import jas.hist.Rebinnable2DHistogramData;
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
public class Project2D extends AbstractProjection2D implements Rebinnable2DHistogramData {
    
    private String colxPathName;
    private String colyPathName;
    private int binsx = 40;
    private int binsy = 40;
    private double binWidthX;
    private double binWidthY;
    private double minx = Double.NaN;
    private double maxx = Double.NaN;
    private double miny = Double.NaN;
    private double maxy = Double.NaN;
    private double[][] data;
    private double[][] newData;
    private static final HistogramUpdate hu = new HistogramUpdate(HistogramUpdate.DATA_UPDATE,true);
    private Value value = new Value();
    private double[] vals = new double[2];

    private MutableTupleTreeNavigator simpleCursorX = null;
    private MutableTupleTreeNavigator simpleCursorY = null;
    private MutableTupleTreeNavigator mainCursor = null;

    //For saving/restoring only
    protected Project2D() {}
    
    public Project2D(MutableTupleColumn x, MutableTupleColumn y) {
        setColumns(x,y);
    }
    
    protected void initProjection2D() {
        columnX().minValue(value);
        minx = value.getDouble();
        columnX().maxValue(value);
        maxx = value.getDouble();
        columnY().minValue(value);
        miny = value.getDouble();
        columnY().maxValue(value);
        maxy = value.getDouble();

        minx = minx - 0.05*Math.abs(minx);
        miny = miny - 0.05*Math.abs(miny);
        maxx = maxx + 0.05*Math.abs(maxx);
        maxy = maxy + 0.05*Math.abs(maxy);
                
        //FIX to JAS-215 JAS-216
        if ( Double.isNaN(minx) ) minx = -1;
        if ( Double.isNaN(miny) ) miny = -1;
        if ( Double.isNaN(maxx) ) maxx = 1;
        if ( Double.isNaN(maxy) ) maxy = 1;

        colxPathName = columnX().treePath().getParentPath().toString();

        //FIX to JAS-215 JAS-216
        if ( Double.isNaN(minx) ) minx = -1;
        if ( Double.isNaN(miny) ) miny = -1;
        if ( Double.isNaN(maxx) ) maxx = 1;
        if ( Double.isNaN(maxy) ) maxy = 1;
        
        colyPathName = columnY().treePath().getParentPath().toString();
        initStatistics( new String[] {"x","y"} );
    }
    
    
    public java.lang.String[] getXAxisLabels() {
        return null;
    }
    public java.lang.String[] getYAxisLabels() {
        return null;
    }
    
    public int getXAxisType() {
        return DOUBLE;
    }
    public int getYAxisType() {
        return DOUBLE;
    }
    public int getYBins() {
        return binsx;
    }
    public int getXBins() {
        return binsy;
    }
    public double getYMin() {
        return miny;
    }
    public double getXMin() {
        return minx;
    }
    public double getYMax() {
        return maxy;
    }
    public double getXMax() {
        return maxx;
    }
    public java.lang.String getTitle() {
        return columnY().name() + " vs "+columnX().name();
    }
    public double[][][] rebin(int xBins, double xMin, double xMax, int yBins, double yMin, double yMax, boolean param6, boolean param7, boolean param8) {
        return new double[][][]{ data };
    }
    
    public boolean isRebinnable() {
        return false;
    }
    public DataSource dataSource() {
        return this;
    }
    public void start() {
        //initProject2D();
        binWidthX = (maxx-minx)/binsx;
        binWidthY = (maxy-miny)/binsy;
        newData = new double[binsx][binsy];
        dataStatistics().reset();
    }
    public void fill(MutableTupleTreeNavigator cursor) {
        
        if ( simpleCursorX == null || mainCursor != cursor ) {
            simpleCursorX =  cursor.cursorForPath( colxPathName );
            simpleCursorY =  cursor.cursorForPath( colyPathName );
            mainCursor = cursor;
        }
        columnX().value( simpleCursorX, value );
        
        double xval = value.getDouble();
        double x = xval - minx;
        if (x<0) return;
        int xBin = (int) Math.floor(x/binWidthX);
        if (xBin >= binsx) return;
        
        columnY().value( simpleCursorY, value );
        
        double yval = value.getDouble();
        double y = yval - miny;
        if (y<0) return;
        int yBin = (int) Math.floor(y/binWidthY);
        if (yBin >= binsy) return;
        
        newData[xBin][yBin]++;
        
        vals[0] = xval;
        vals[1] = yval;
        dataStatistics().addEntry(vals);
    }
    
    public void end() {
        data = newData;
        setChanged();
        notifyObservers(hu);
    }
}
