package org.freehep.jas.extension.tupleExplorer.cut;

import hep.aida.ref.tuple.FTupleCursor;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version
 */

public class Numeric1DCut extends AbstractCutWithVariables 
{
    private int cutType;
    private int origCutType;
    private CutVariableSynchronization cutVarSync = new CutVariableSynchronization();
    private boolean isSynchronized = false;
    
    /** 
     * Create a new Numeric1DCut object
     * @param cutName the name of the cut
     *
     */
    public Numeric1DCut( String cutName )
    {
	super( cutName );
    }

    /** 
     * Create a new Numeric1DCut object
     * @param cutName the name of the cut
     * @param cutDataSet the CutDataSet on which the cut is applied
     * @param cutType the type of cut
     *
     */
    public Numeric1DCut( String cutName, CutDataSet cutDataSet, int cutType )
    {
	super( cutName );
	setCutDataSet( cutDataSet );
	setCutType( cutType );
	setOriginalCutType( cutType );
	if ( cutType == NUMERIC1DCUT_GREATER_THAN || cutType == NUMERIC1DCUT_LESS_THAN ) {
	    Numeric1DCutVariable minVar = new Numeric1DCutVariable( "const", cutDataSet );

            minVar.setValue( minVar.getMin() + 0.5 * ( minVar.getMax() - minVar.getMin() ) );	    
	    addCutVariable( minVar );
	} else if ( cutType == NUMERIC1DCUT_INCLUSIVE || cutType == NUMERIC1DCUT_EXCLUSIVE ) {
	    Numeric1DCutVariable minVar = new Numeric1DCutVariable( "min", cutDataSet );
	    minVar.setValue( minVar.getMin() + 0.25 * ( minVar.getMax() - minVar.getMin() ) );
	    Numeric1DCutVariable maxVar = new Numeric1DCutVariable( "max", cutDataSet );
	    maxVar.setValue( maxVar.getMin() + 0.75 * ( maxVar.getMax() - maxVar.getMin() ) );
	    addCutVariable( minVar );
	    addCutVariable( maxVar );
            resetSynchronization();
	}
   }
    
    /** 
     * Create a new Numeric1DCut object
     * @param cutName the name of the cut
     * @param cutDataSet the CutDataSet on which the cut is applied
     * @param cutType the type of cut
     * @param numeric1DCutVariable the Numeric1DCutVariable for this Numeric1DCut
     *
     */
    public Numeric1DCut( String cutName, CutDataSet cutDataSet, int cutType, Numeric1DCutVariable numeric1DCutVariable )
    {
	super( cutName );
	setCutDataSet( cutDataSet );
	setCutType( cutType );
	setOriginalCutType( cutType );
	addCutVariable( numeric1DCutVariable );
    }
    
    /** 
     * Create a new Numeric1DCut object
     * @param cutName the name of the cut
     * @param cutDataSet the CutDataSet on which the cut is applied
     * @param cutType the type of cut
     * @param minNumeric1DCutVariable the lower Numeric1DCutVariable for this Numeric1DCut
     * @param maxNumeric1DCutVariable the upper Numeric1DCutVariable for this Numeric1DCut
     *
     */
    public Numeric1DCut( String cutName, CutDataSet cutDataSet, int cutType, Numeric1DCutVariable minNumeric1DCutVariable, Numeric1DCutVariable maxNumeric1DCutVariable )
    {
	super( cutName );
	setCutDataSet( cutDataSet );
	setCutType( cutType );
	setOriginalCutType( cutType );
	addCutVariable( minNumeric1DCutVariable );
	addCutVariable( maxNumeric1DCutVariable );
        resetSynchronization();
    }
    
    /**
     * The four kind of Numeric1DCut.
     *
     */
    public static final int NUMERIC1DCUT_GREATER_THAN = 0; // x >
    public static final int NUMERIC1DCUT_LESS_THAN    = 1; // x <
    public static final int NUMERIC1DCUT_INCLUSIVE    = 2; // < x <
    public static final int NUMERIC1DCUT_EXCLUSIVE    = 3; // x < || x >
    
    /**
     * Apply the cut to the current value of CutDataSet
     * @param cutDataCursor the CutDataCursor to access the CutDataSet current value
     * @return <code>true<\code> if the current value of the CutDataSet is accepted by the cut
     *         <code>false<\code> otherwise
     *
     */
    public boolean accept( FTupleCursor cutDataCursor )
    {
	if ( !isEnabled() ) return true;

        double value = getCutDataSet().getDataCurrentValue( cutDataCursor );

	switch ( cutType )
	    {
	    case NUMERIC1DCUT_GREATER_THAN: 
		return value > getCutVariable(0).getValue();
	    case NUMERIC1DCUT_LESS_THAN:    
		return value < getCutVariable(0).getValue();
	    case NUMERIC1DCUT_INCLUSIVE:   
		return value > getCutVariable(0).getValue() && value < getCutVariable(1).getValue();
	    case NUMERIC1DCUT_EXCLUSIVE:    
		return value < getCutVariable(0).getValue() || value > getCutVariable(1).getValue();
	    }
	return false;
    }

    /**
     * Get the type of the Numeric1DCut
     * @return the cut type
     *
     */
    public int getCutType()
    {
	return cutType;
    }

    /**
     * Set the type of the Numeric1DCut
     * @param cutType the cut type
     *
     */
    public void setCutType( int cutType )
    {
	this.cutType = cutType;
    }

    protected int getOriginalCutType() {
	return origCutType;
    }
    protected void setOriginalCutType( int origCutType ) {
	this.origCutType = origCutType;
    }

    /**
     * Set the state of the Numeric1DCut.
     * @param cutState the state of the Numeric1DCut
     *
     */
    public void setState( int cutState )
    {
	boolean invert = ( cutState == CUT_INVERTED || cutState == CUT_DISABLED_INVERTED );
	switch ( getOriginalCutType() )
	    {
	    case NUMERIC1DCUT_GREATER_THAN: 
		cutType = !invert ? NUMERIC1DCUT_GREATER_THAN : NUMERIC1DCUT_LESS_THAN;
		break;
	    case NUMERIC1DCUT_LESS_THAN:    
		cutType = !invert ? NUMERIC1DCUT_LESS_THAN : NUMERIC1DCUT_GREATER_THAN;
		break;
	    case NUMERIC1DCUT_INCLUSIVE:    
		cutType = !invert ? NUMERIC1DCUT_INCLUSIVE : NUMERIC1DCUT_EXCLUSIVE;
		break;
	    case NUMERIC1DCUT_EXCLUSIVE: 
		cutType = !invert ? NUMERIC1DCUT_EXCLUSIVE : NUMERIC1DCUT_INCLUSIVE;
		break;
	    }
	super.setState( cutState );
    }
    
    protected void resetSynchronization() {
        isSynchronized = false;
        if ( getNCutVariables() != 2 ) throw new IllegalArgumentException("This method can only be called for 2-d cuts");
        cutVarSync.setSyncDiff(0.);
        cutVarSync.setSyncType( CutVariableSynchronization.SYNCHRONIZED_MINIMUM );        
        cutVarSync.synchronize( getCutVariable(1), getCutVariable(0) );
    }

    protected void synchronizeSliders() {
        isSynchronized = true;        
        if ( getNCutVariables() != 2 ) throw new IllegalArgumentException("This method can only be called for 2-d cuts");
        double diff = getCutVariable(0).getValue() - getCutVariable(1).getValue();
        cutVarSync.setSyncDiff(diff);
        cutVarSync.setSyncType( CutVariableSynchronization.SYNCHRONIZED_FIXED );
        cutVarSync.synchronize( getCutVariable(0), getCutVariable(1) );
    }

    protected boolean isSynchronized() {
        return isSynchronized;
    }
    
    protected CutVariableSynchronization getCutVariableSynchronization() {
        return cutVarSync;
    }
    protected void setCutVariableSynchronization(CutVariableSynchronization cutVarSync) {
        this.cutVarSync = cutVarSync;
    }
}

