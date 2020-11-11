package org.freehep.jas.extension.tupleExplorer.cut;

import hep.aida.ref.tuple.FTupleCursor;

/**
 * A DummyCutDataSet is a collection of data on which the cut is applied
 * @author turri
 * @version $Id: DummyCutDataSet.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public class DummyCutDataSet implements CutDataSet
{

    public double getDataMaxValue()
    {
	return 5;
    }

    public double getDataMinValue()
    {
	return -7;
    }

    public void setDataMaxValue( double maxValue )
    {
    }

    public void setDataMinValue( double minValue )
    {
    }

    public double getDataCurrentValue(FTupleCursor cursor)
    {
	return 0;
    }

}
