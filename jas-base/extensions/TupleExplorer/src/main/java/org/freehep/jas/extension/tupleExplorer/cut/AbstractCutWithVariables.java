package org.freehep.jas.extension.tupleExplorer.cut;

import java.util.ArrayList;

/**
 * AbstractCutWithVariables abstract implementation of CutWithVariables
 * @author The FreeHEP team @ SLAC.
 * @version $Id: AbstractCutWithVariables.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public abstract class AbstractCutWithVariables extends AbstractCut implements CutWithVariables {
    
    private ArrayList cutVarList = new ArrayList();
    
    /**
     * The default constructor
     * @param cutName the name of the cut
     *
     */
    public AbstractCutWithVariables( String cutName ) {
        super( cutName );
    }
    
    /**
     * Add a CutVariable to the AbstractCutWithVariables
     * @param cutVariable the CutVariable to add
     *
     */
    public void addCutVariable( CutVariable cutVariable ) {
        cutVarList.add( cutVariable );
        cutVariable.addCutVariableListener( this );
    }
    
    /**
     * Remove a CutVariable from the AbstractCutWithVariables
     * @param cutVariable the CutVariable to remove
     *
     */
    public void removeCutVariable( CutVariable cutVariable ) {
        cutVarList.remove( cutVariable );
        cutVariable.removeCutVariableListener( this );
    }
    
    /**
     * Get the list of CutVariables
     * @return the list of CutVariables
     *
     */
    public ArrayList getCutVariables() {
        return cutVarList;
    }
    
    /**
     * Get the nth CutVariable
     * @param  nCutVar the index of the desired CutVariable
     * @return the nth CutVariables
     *
     */
    public CutVariable getCutVariable( int nCutVar ) {
        return (CutVariable) cutVarList.get( nCutVar );
    }
    
    /**
     * Get the number of CutVariables
     * @return the number of CutVariables
     *
     */
    public int getNCutVariables() {
        return cutVarList.size();
    }
    
    /**
     * Invoked when the CutVariable has changed the current value
     * @param cutChangedEvent the event describing the change
     * @see cutVariableListener#cutVarValueChanged( CutChangedEvent )
     *
     */
    public void cutVarValueChanged( CutChangedEvent cutChangedEvent ) {
        if ( isEnabled() )
            fireCutChanged();
    }
    
    /**
     * Invoked when the CutVariable range has changed
     * @param cutChangedEvent the event describing the change
     * @see cutVariableListener#cutVarRangeChanged( CutChangedEvent )
     *
     */
    public void cutVarRangeChanged( CutChangedEvent cutChangedEvent ) {
        fireCutChanged();
    }
    
    /**
     * Invoked when the CutVariable state has changed
     * @param cutChangedEvent the event describing the change
     * @see cutVariableListener#cutVarStateChanged( CutChangedEvent )
     */
    public void cutVarStateChanged( CutChangedEvent cutChangedEvent ) {
        fireCutChanged();
    }
}

