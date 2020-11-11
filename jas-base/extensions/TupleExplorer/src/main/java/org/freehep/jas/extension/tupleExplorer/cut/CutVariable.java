package org.freehep.jas.extension.tupleExplorer.cut;

import org.freehep.jas.extension.tupleExplorer.cut.CutDataSet;
import org.freehep.jas.extension.tupleExplorer.cut.CutVariableListener;

/**
 * A CutVariable is a variable of a Cut
 * @author The FreeHEP team @ SLAC.
 * @version $Id: CutVariable.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public interface CutVariable {
    
    /**
     * Get the name of the CutVariable
     * @return the name of the CutVariable
     *
     */
    String getName();
    
    /**
     * Get the lower bound of the range
     * @return the lower bound of the range
     *
     */
    double getMin();
    
    /**
     * Get the upper bound of the range
     * @return the upper bound of the range
     *
     */
    double getMax();
    
    /**
     * The CutVariable can be either locked or unlocked.
     * Moreover it can be frozen when it reaches the boundary
     * of the range or when it is synchronized with another
     * CutVariable that is locked or frozen.
     *
     */
    public final int CUTVARIABLE_UNLOCKED = 0;
    public final int CUTVARIABLE_LOCKED   = 1;
    
    /**
     * Get the state of the CutVariable
     * @return the state of the CutVariable
     *
     */
    int getState();
    
    /**
     * Set the state of the CutVariable
     * @param cutVarState the state of the CutVariable
     *
     */
    void setState( int cutVarState );
    
    /**
     * Check if the CutVariable is locked
     * @return <code>true<\code> if the variable is locked
     *         <code>false<\code> otherwise
     *
     */
    boolean isLocked();
    
    /**
     * Get the current value of the CutVariable.
     * @return the current value of the CutVariable
     *
     */
    double getValue();
    
    /**
     * Set the current value of the CutVariable.
     * @param cutVarValue the current value of the CutVariable
     *
     */
    void setValue( double cutVarValue );
    
    /**
     * Get the CutVariable change in its value
     * @return the change in the CutVariable's value
     *
     */
    double getValueChange();
    
    /**
     * Add a CutVariableListener to the CutVariable
     * @param cutVarListener the CutVariableListenere to add
     *
     */
    void addCutVariableListener( CutVariableListener cutVarListener );
    
    /**
     * Remove a CutVariableListener from the CutVariable
     * @param cutVarListener the CutVariableListenere to remove
     *
     */
    void removeCutVariableListener( CutVariableListener cutVarListener );
    
    /**
     * Assign to this CutVariable a CutDataSet. The CutDataSet data set is used
     * to set the CutVariable's range.
     * @param cutDataSet the CutDataSet data set
     *
     */
    void setCutDataSet( CutDataSet cutDataSet );
    
    /**
     * Get the CutDataSet corresponding to this CutVariable
     * @return the CutDataSet
     *
     */
    CutDataSet getCutDataSet();
    
    /**
     * Get the CutVariableGUIListener that listens to the GUI representations
     * of the CutVariable.
     * @return the CutVariableGUIListener
     *
     */
    CutVariableListener getCutVariableGUIListener();
    
    /**
     * Internal class that implements the behavior of CutVariableListener.
     * This class should be registered with the GUI representations of the
     * CutVariable to listen to their CutChangedEvents.
     *
     */
    abstract class CutVariableGUIListener implements CutVariableListener {
    }
}
