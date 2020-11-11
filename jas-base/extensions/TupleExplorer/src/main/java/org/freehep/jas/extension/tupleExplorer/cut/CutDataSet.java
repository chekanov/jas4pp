package org.freehep.jas.extension.tupleExplorer.cut;

import hep.aida.ref.tuple.FTupleCursor;

/**
 * A CutDataSet is a collection of data on which the cut is applied
 * @author The FreeHEP team @ SLAC.
 * @version $Id: CutDataSet.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public interface CutDataSet {
    
    /**
     * Get the maximum value of the CutDataSet
     * @return the maximum value
     *
     */
    double getDataMaxValue();
    
    /**
     * Get the minimum value of the CutDataSet
     * @return the minimum value
     *
     */
    double getDataMinValue();
    
    /**
     * Set the maximum value of the CutDataSet
     * @param maxValue the maximum value to be set
     *
     */
    void setDataMaxValue( double maxValue );
    
    /**
     * Set the minimum value of the CutDataSet
     * @param minValue the minimum value to be set
     *
     */
    void setDataMinValue( double minValue );
    
    /**
     * Get the current value of the CutDataSet
     * @return the current value
     *
     */
    double getDataCurrentValue( FTupleCursor cursor );
    
}
