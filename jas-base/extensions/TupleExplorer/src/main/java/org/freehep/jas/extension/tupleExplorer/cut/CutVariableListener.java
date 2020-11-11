package org.freehep.jas.extension.tupleExplorer.cut;

import java.util.EventListener;
import org.freehep.jas.extension.tupleExplorer.cut.CutChangedEvent;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: CutVariableListener.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public interface CutVariableListener extends EventListener {
    
    /**
     * Invoked when the CutVariable has changed the current value
     * @param cutChangedEvent the event describing the change
     *
     */
    void cutVarValueChanged( CutChangedEvent cutChangedEvent );
    
    /**
     * Invoked when the CutVariable range has changed
     * @param cutChangedEvent the event describing the change
     *
     */
    void cutVarRangeChanged( CutChangedEvent cutChangedEvent );
    
    /**
     * Invoked when the CutVariable state has changed
     * @param cutChangedEvent the event describing the change
     *
     */
    void cutVarStateChanged( CutChangedEvent cutChangedEvent );
    
}
