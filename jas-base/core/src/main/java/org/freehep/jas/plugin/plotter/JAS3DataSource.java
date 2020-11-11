package org.freehep.jas.plugin.plotter;

import jas.hist.DataSource;
import java.awt.Component;
import javax.swing.JPopupMenu;
import org.freehep.jas.plugin.tree.FTreePath;

/**
 * Defines an object that can be plotted by the Jas3 default plotter.
 */
public interface JAS3DataSource {
    
    public void destroy();
 
    public void modifyPopupMenu(JPopupMenu jPopupMenu, Component component);
    
    public DataSource dataSource();
    
    /** Returns the path to the data, if available. */
    public FTreePath path();
    
    public String[] axisLabels();
    
    /**
     * This method was added to fulfill BaBar's needs to have
     * DataPointSets with Dates on the axis.
     * @param type The axis type
     * 1 = Double
     * 2 = String
     * 3 = Date
     * 4 = Integer
     * 5 = DeltaTime
     */
    public void setAxisType(int type);
}
