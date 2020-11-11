package org.freehep.jas.extension.tupleExplorer.cut;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * CutVariableSynchronization manages the syncronization of variables.
 * @author turri
 * @version $Id: CutVariableSynchronization.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public class CutVariableSynchronization implements CutVariableListener
{
    private CutVariable cutVar1;
    private CutVariable cutVar2;

    private boolean isValueChanging = false;

    /** 
     * The synchronization triggers when the difference
     * between the two cut variables is <code>syncDiff<\code>
     *
     */
    private double syncDiff = 0.;
    private double oldSyncDiff;

    /**
     * When synchronized if one variable changes its value
     * the other changes with scale factor <code>syncScale<\code>
     *
     */
    private double syncScale = 1.;

    /**
     * There are three ways in which two variables can be
     * synchronized
     *
     */
    public static final int SYNCHRONIZED_FIXED   = 0;
    public static final int SYNCHRONIZED_MAXIMUM = 1;
    public static final int SYNCHRONIZED_MINIMUM = 2;
    private int syncType = SYNCHRONIZED_MINIMUM;

    private boolean syncState;

    /**
     * Synchronize the following CutVariables.
     * @param cutVar1 the first CutVariable to synchronize
     * @param cutVar2 the second CutVariable to synchronize
     * @param syncType  the type of synchronization
     * @param syncScale the scale factor of the synchronization
     * @param syncDiff  the difference at which the two variables are synchronized
     *
     */
    public void synchronize( CutVariable cutVar1, CutVariable cutVar2 )
    {
	this.oldSyncDiff = syncDiff;
	this.cutVar1 = cutVar1;
	this.cutVar2 = cutVar2;
	cutVar1.addCutVariableListener( this );
	cutVar2.addCutVariableListener( this );
	if ( syncType == SYNCHRONIZED_FIXED ) cutVar1.setValue( cutVar2.getValue() + getSyncDiff() );
	if ( getSyncState() ) cutVar1.setValue( cutVar2.getValue() + getSyncDiff() );
    }

    /**
     * Get the synchronization state of the two variables
     * @return <code>true<\code> if the two variables need to synchronize
     *         <code>false<\code> otherwise
     *
     */
    private boolean getSyncState()
    {
	double diff = cutVar1.getValue() - cutVar2.getValue();
	syncState = false;

	switch ( syncType )
	    {
	    case 0: 
		if ( diff != syncDiff )
		    syncState = true;
		break;
	    case 1: 
		if ( diff >  syncDiff ) 
		    syncState = true;
		break;
	    case 2: 
		if ( diff <  syncDiff ) 
		    syncState = true;
		break;
	    }
	return syncState;
    }

    /**
     * Get the synchronization distance at which the synchronization switches on
     * @return the synchronization distance
     *
     */
    private double getSyncDiff()
    {
	double varDiff = cutVar1.getValue() - cutVar2.getValue();
	double newDiff = varDiff - syncScale * ( varDiff - oldSyncDiff );
	switch ( syncType )
	    {
	    case 0: 
		if ( newDiff != syncDiff ) newDiff = syncDiff;
		break;
	    case 1: 
		if ( newDiff <  syncDiff ) newDiff = syncDiff;
		break;
	    case 2: 
		if ( newDiff >  syncDiff ) newDiff = syncDiff;
		break;
	    }
	oldSyncDiff = newDiff;
	return newDiff;
    }

    /**
     * Check if the two variables are synchronized
     * @return <code>true<\code> if the two variables are synchronized
     *         <code>false<\code> otherwise
     *
     */
    private boolean isSynchronized()
    {
	return syncState;
    }

    /**
     * Invoked when the CutVariable has changed the current value
     * @param cutChangedEvent the event describing the change
     *
     */
    public void cutVarValueChanged( CutChangedEvent cutChangedEvent )
    {
	if ( isValueChanging ) return;
	isValueChanging = true;

	if ( getSyncState() ) {
	    if ( cutChangedEvent.getSource() == cutVar1 ) {
		double delta = getSyncDiff();
		cutVar2.setValue( cutVar1.getValue() - delta );
		if ( cutVar2.getValue() != cutVar1.getValue() - delta ) {
		    double newDelta = getSyncDiff();
		    double val = cutVar2.getValue() + newDelta;
		    cutVar1.setValue( val );
		}
	    } else {
		double delta = getSyncDiff();
		cutVar1.setValue( cutVar2.getValue() + delta );
		if ( cutVar1.getValue() != cutVar2.getValue() + delta ) {
		    double newDelta = getSyncDiff();
		    double val = cutVar1.getValue() - newDelta;
		    cutVar2.setValue( val );
		}
	    }
	}
	isValueChanging = false;
    }

    /**
     * Invoked when the CutVariable range has changed
     * @param cutChangedEvent the event describing the change
     *
     */
    public void cutVarRangeChanged( CutChangedEvent cutChangedEvent )
    {
    }

    /**
     * Invoked when the CutVariable state has changed
     * @param cutChangedEvent the event describing the change
     *
     */
    public void cutVarStateChanged( CutChangedEvent cutChangedEvent )
    {
    }
    
    protected double syncDiff() {
        return syncDiff;
    }
    protected void setSyncDiff(double syncDiff) {
        this.syncDiff = syncDiff;
    }
    
    protected double syncScale() {
        return syncScale;
    }
    protected void setSyncScale(double syncScale) {
        this.syncScale = syncScale;
    }

    protected int syncType() {
        return syncType;
    }
    protected void setSyncType(int syncType) {
        this.syncType = syncType;
    }
}

