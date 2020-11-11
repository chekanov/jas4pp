package org.freehep.jas.extension.tupleExplorer.cut;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: CutVariableListenerAdapter.java 13893 2011-09-28 23:42:34Z tonyj $
 */

public class CutVariableListenerAdapter implements CutVariableListener
{
    private CutVariableGUI cutVarGUI;

    /**
     * Create a new CutVariableListenereAdapter
     * @param cutVarGUI the CutVariableGUI that is listening
     *
     */
    public CutVariableListenerAdapter( CutVariableGUI cutVarGUI )
    {
	this.cutVarGUI = cutVarGUI;
    }

    /**
     * Invoked when the CutVariable has changed the current value
     * @param cutChangedEvent the event describing the change
     *
     */
    public void cutVarValueChanged( CutChangedEvent cutChangedEvent )
    {
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

}
