package org.freehep.jas.extension.tupleExplorer.project;

import jas.hist.DataSource;
import jas.hist.HistogramUpdate;
import jas.hist.ScatterEnumeration;
import jas.hist.Rebinnable2DHistogramData;
import jas.hist.HasScatterPlotData;
import jas.hist.Statistics;
import jas.hist.HasStyle;
import jas.hist.JASHistScatterPlotStyle;
import jas.hist.JASHistStyle;
import org.freehep.util.Value;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection2D;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class Scatter2D extends AbstractProjection2D implements HasScatterPlotData, HasStyle {
    
    private String colxPathName;
    private String colyPathName;
    private double minx = Double.NaN;
    private double maxx = Double.NaN;
    private double miny = Double.NaN;
    private double maxy = Double.NaN;
    
    private double[] x, y;
    private double[] xPoints, yPoints;
    private double[] points;
    private double[] vals = new double[2];
    private int n , nPoints;
    private static final HistogramUpdate hu = new HistogramUpdate(HistogramUpdate.DATA_UPDATE,true);
    private Value value = new Value();

    private MutableTupleTreeNavigator simpleCursorX = null;
    private MutableTupleTreeNavigator simpleCursorY = null;
    private MutableTupleTreeNavigator mainCursor = null;
    
    private JASHistScatterPlotStyle style = new JASHistScatterPlotStyle();

    //For saving/restoring only
    protected Scatter2D() {};
               
    public Scatter2D(MutableTupleColumn x, MutableTupleColumn y) {
        setColumns(x,y);
    }
    
    protected void initProjection2D() {
        style.setDisplayAsScatterPlot(true);
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
        
        //FIX to JAS-215 JAS-216
        if ( Double.isNaN(minx) ) minx = -1;
        if ( Double.isNaN(miny) ) miny = -1;
        if ( Double.isNaN(maxx) ) maxx = 1;
        if ( Double.isNaN(maxy) ) maxy = 1;

        colxPathName = columnX().treePath().getParentPath().toString();
        colyPathName = columnY().treePath().getParentPath().toString();
        initStatistics( new String[] {"x","y"} );
    }
    public DataSource dataSource() {
        return this;
    }
    public void start() {
        //initScatter2D();
        x = new double[1000];
        y = new double[1000];
        n = 0;
        dataStatistics().reset();
    }
    public void fill(MutableTupleTreeNavigator cursor) {
        double tmp;
        if (n == x.length) {
            double[] xNew = new double[n*2];
            double[] yNew = new double[n*2];
            System.arraycopy(x,0,xNew,0,n);
            System.arraycopy(y,0,yNew,0,n);
            x = xNew;
            y = yNew;
        }        

        if ( simpleCursorX == null || mainCursor != cursor ) {
            simpleCursorX =  cursor.cursorForPath( colxPathName );
            simpleCursorY =  cursor.cursorForPath( colyPathName );
            mainCursor = cursor;
        }
        columnX().value( simpleCursorX, value );
        tmp = value.getDouble();
        
        columnY().value( simpleCursorY, value );
        
        vals[0] = x[n] = tmp;
        vals[1] = y[n] = value.getDouble();
        dataStatistics().addEntry(vals);
        n++;
    }
    public void end() {
        xPoints = x;
        yPoints = y;
        nPoints = n;
        setChanged();
        notifyObservers(hu);
    }
    public ScatterEnumeration startEnumeration(double param, double param1, double param2, double param3) {
        return startEnumeration();
    }
    
    public ScatterEnumeration startEnumeration() {
        return new Scatter2DEnumeration(xPoints,yPoints,nPoints);
    }
    public int getXAxisType() {
        return DOUBLE;
    }
    public int getYAxisType() {
        return DOUBLE;
    }
    public java.lang.String getTitle() {
        return columnY().name() + " vs "+columnX().name();
    }
    public double getXMax() {
        return maxx;
    }
    public double getYMax() {
        return maxy;
    }
    public double getXMin() {
        return minx;
    }
    public double getYMin() {
        return miny;
    }
    
    private static class Scatter2DEnumeration implements ScatterEnumeration {
        private double[] x, y;
        private int n;
        private int i;
        Scatter2DEnumeration(double[] x , double[] y, int n) {
            this.x = x;
            this.y = y;
            this.n = n;
        }
        public boolean getNextPoint(double[] point) {
            if (i == n) return false;
            point[0] = x[i];
            point[1] = y[i];
            i++;
            return true;
        }
        public void resetEndPoint() {
        }
        public void restart() {
            i=0;
        }
    }
    
    public boolean hasScatterPlotData() {
        return true;
    }

    public double[][][] rebin(int xbins, double xmin, double xmax,
		                      int ybins, double ymin, double ymax, 
							  boolean wantErrors, boolean hurry, boolean overflow) {
        int l = xPoints.length;
        double[][] data = new double[l][l];
        return new double[][][]{ data };
    }
    
    
    public int getXBins() {
        return nPoints;
    }
    
    public int getYBins() {
        return nPoints;
    }

    public boolean isRebinnable() {
        return false;
    }

    public String[] getXAxisLabels() {
        return new String[] {axisLabels()[0]};
    }

    public String[] getYAxisLabels() {
        return new String[] {axisLabels()[1]};
    }    
    
    public JASHistStyle getStyle() {
        return style;
    }        
    
}

