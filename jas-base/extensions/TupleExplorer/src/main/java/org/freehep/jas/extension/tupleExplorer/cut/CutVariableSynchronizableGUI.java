package org.freehep.jas.extension.tupleExplorer.cut;

/**
 * A CutVariableSynchronizableGUI is a variable of a CutGUI
 * @author The FreeHEP team @ SLAC.
 * @version $Id: CutVariableSynchronizableGUI.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public interface CutVariableSynchronizableGUI extends CutVariableGUI
{

    /**
     * Synchronize this CutVariableSynchronizableGUI with another CutVariableSynchronizableGUI.
     * When synchronized two or more CutVariableSynchronizableGUIs will increase
     * or decrease their current value by the same amount.
     * @param cutVariableSynchronizableGUI the CutVariableSynchronizableGUI to synchronize with.
     *
     */
    void synchronizeWith( CutVariableSynchronizableGUI cutVariableSynchronizableGUI );

    /**
     * Remove the synchronization from this CutVariableSynchronizableGUI.
     * @param cutVariableSynchronizableGUI the CutVariableSynchronizableGUI to unlock from.
     *
     */
    void unSynchronizeFrom( CutVariableSynchronizableGUI cutVariableSynchronizableGUI );

    /**
     * Check if the CutVariableSynchronizableGUI is synchronized with a given CutVariableSynchronizableGUI
     * @param cutVariableSynchronizableGUI the CutVariableSynchronizableGUI to check
     * @return <code>true<\code> if the variable is synchronized with the <code>cutVariableSynchronizableGUI<\code>
     *         <code>false<\code> otherwise
     *
     */
    boolean isSynchronizedWith( CutVariableSynchronizableGUI cutVariableSynchronizableGUI );

}
