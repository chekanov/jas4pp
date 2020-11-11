package org.freehep.jas.extension.tupleExplorer.project;

import org.freehep.util.Value;
import jas.hist.DataSource;
import jas.hist.HasStyle;
import jas.hist.HistogramUpdate;
import jas.hist.JASHist1DHistogramStyle;
import jas.hist.JASHistStyle;
import jas.hist.XYDataSource;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection2D;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class ProjectXY extends AbstractProjection2D implements XYDataSource, HasStyle {
    
    private String colxPathName;
    private String colyPathName;
    protected int nBins = 100;
    protected int Counter = 0;
    protected double minx = Double.NaN;
    protected double maxx = Double.NaN;
    protected double miny = Double.NaN;
    protected double maxy = Double.NaN;
    protected int typeX;
    protected double f;
    protected double[][] data;
    protected double[][] newData;
    protected static final HistogramUpdate hu = new HistogramUpdate(HistogramUpdate.DATA_UPDATE,true);
    protected Value value = new Value();
    protected JASHist1DHistogramStyle style;
    private double[] vals = new double[2];
    
    private MutableTupleTreeNavigator simpleCursorX = null;
    private MutableTupleTreeNavigator simpleCursorY = null;
    private MutableTupleTreeNavigator mainCursor = null;

    //For saving/restoring only
    protected ProjectXY() {};
    
    public ProjectXY(MutableTupleColumn x, MutableTupleColumn y) {
        setColumns(x,y);
        setNPoints(100);
    }
    
    public ProjectXY(MutableTupleColumn x, MutableTupleColumn y, int n) {
        setColumns(x,y);
        setNPoints(n);
    }
            
    protected void initProjection2D() {
        typeX = DOUBLE;
        f = 1.;
        columnX().minValue(value);
        minx = (value.getDouble())/f;
        columnX().maxValue(value);
        maxx = (value.getDouble())/f;
        columnY().minValue(value);
        miny = value.getDouble();
        columnY().maxValue(value);
        maxy = value.getDouble();

        //FIX to JAS-215 JAS-216
        if ( Double.isNaN(minx) ) minx = -1;
        if ( Double.isNaN(miny) ) miny = -1;
        if ( Double.isNaN(maxx) ) maxx = 1;
        if ( Double.isNaN(maxy) ) maxy = 1;
                
        colxPathName = columnX().treePath().getParentPath().toString();
        colyPathName = columnY().treePath().getParentPath().toString();
        
        style = new JASHist1DHistogramStyle();
        style.setShowHistogramBars(false);
        style.setShowDataPoints(true);
        style.setShowLinesBetweenPoints(true);
        style.setShowErrorBars(false);
        style.setDataPointStyle(JASHist1DHistogramStyle.SYMBOL_DOT);
        style.setHistogramFill(false);
        initStatistics( new String[] {"x","y"} );
    }
    
    public void setAxisType(int t) { typeX = t; }
    public void setNPoints(int n) { nBins = n; }
    
    // Sometimes need to convert X-axis data into seconds (for typeX=DATE)
    public void setXAxisFactor(double factor) { f = factor; }
        
    // --- Implementation of XYDataSource interface
    public int getNPoints() { return nBins; }
    public double getX(int index) { return data[index][0]; }
    public double getY(int index) { return data[index][1]; }
    public double getPlusError(int index) { return Math.sqrt(Math.abs(data[index][1])); }
    public double getMinusError(int index) { return Math.sqrt(Math.abs(data[index][1])); }
    public int getAxisType() { return typeX; }
    
    public java.lang.String getTitle() // This function implements DataSource interface
    {
        return columnY().name() + " vs "+columnX().name();
    }
    // --- end XYDataSource interface
    
    // --- Implementation of Projection interface
    public DataSource dataSource() { return this; }
    public void start() { 
        newData = new double[nBins][2]; 
        Counter = 0; 
        dataStatistics().reset();
    }
    public void fill(MutableTupleTreeNavigator cursor) {
        
        if ( simpleCursorX == null || mainCursor != cursor ) {
            simpleCursorX =  cursor.cursorForPath( colxPathName );
            simpleCursorY =  cursor.cursorForPath( colyPathName );
            mainCursor = cursor;
        }
        columnX().value( simpleCursorX, value );

        double x = value.getDouble();
        
        columnY().value( simpleCursorY, value );
        double y = value.getDouble();
        
        if (Counter >= newData.length) {
            int oldSize = newData.length;
            int newSize = Counter+500;
            double[][] newData2 = new double[newSize][2];
            System.arraycopy(newData,0,newData2,0,oldSize);
            newData = newData2;
        }
        vals[0] = newData[Counter][0] = x/f;
        vals[1] = newData[Counter][1] = y;
        Counter++;
        dataStatistics().addEntry(vals);
    }
    public void end() {
        data = newData;
        nBins = Counter;
        setChanged();
        notifyObservers(hu);
    }
    // --- end Projection interface
    
    // --- Implementation of HasStyle interface
    public JASHistStyle getStyle() {
        return style;
    }
    // --- end HasStyle interface
    
}
