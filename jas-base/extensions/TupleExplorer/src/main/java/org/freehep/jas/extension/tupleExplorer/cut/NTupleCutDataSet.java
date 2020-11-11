package org.freehep.jas.extension.tupleExplorer.cut;

import org.freehep.util.Value;
import hep.aida.ref.tuple.FTupleCursor;
import org.freehep.jas.extension.tupleExplorer.jel.JELColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;

/**
 * A NTupleCutDataSet is a collection of data on which the cut is applied
 * @author The FreeHEP team @ SLAC.
 * @version $Id: NTupleCutDataSet.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public class NTupleCutDataSet implements CutDataSet
{
    private MutableTupleColumn ntupleColumn;
    private MutableTuple tuple;
    private int ntupleColumnIndex = -1;
    private Value val = new Value();
    

    protected NTupleCutDataSet(){};
    /**
     * Create a new NTupleCutDataSet
     * @param ntupleColumn the NTupleColumn
     *
     */
    NTupleCutDataSet( MutableTupleColumn ntupleColumn, String name, MutableTuple tuple )
    {
        setNTupleAndColumn(tuple, ntupleColumn);
    }

    /**
     * Get the maximum value of the NTupleCutDataSet
     * @return the maximum value
     *
     */
    public double getDataMaxValue()
    {
	ntupleColumn.maxValue( val );
	return val.getDouble();
    }

    /**
     * Get the minimum value of the NTupleCutDataSet
     * @return the minimum value
     *
     */
    public double getDataMinValue()
    {
	ntupleColumn.minValue( val );
	return val.getDouble();
    }

    /**
     * Set the maximum value of the NTupleCutDataSet
     * @param maxValue the maximum value to be set
     *
     */
    public void setDataMaxValue( double maxValue )
    {
	val.set( maxValue );
	//	ntupleColumn.setMaxValue( val );
    }

    /**
     * Set the minimum value of the NTupleCutDataSet
     * @param minValue the minimum value to be set
     *
     */
    public void setDataMinValue( double minValue )
    {
	val.set( minValue );
	//	ntupleColumn.setMinValue( val );
    }

    /**
     * Get the current value of the NTupleCutDataSet
     * @return the current value
     *
     */
    public double getDataCurrentValue(FTupleCursor cursor)
    {
	if ( ntupleColumnIndex != -1 ) tuple.columnValue( ntupleColumnIndex, cursor, val );
        else ((JELColumn)ntupleColumn).value(cursor,val);
	return val.getDouble();
    }
    
    /**
     * Get the NTupleColumn
     * @return the NTupleColumn
     *
     */
    public MutableTupleColumn getNTupleColumn()
    {
	return ntupleColumn;
    }
    public MutableTuple getNTuple()
    {
	return tuple;
    }
    
    
    protected void setNTupleAndColumn( MutableTuple tuple, MutableTupleColumn ntupleColumn ) {
	this.ntupleColumn = ntupleColumn;
        this.tuple = tuple;
        if ( !(ntupleColumn instanceof JELColumn) )
            this.ntupleColumnIndex = tuple.columnIndexByName(ntupleColumn.name());
    }

}





