package org.freehep.jas.extension.tupleExplorer.cut;

import java.util.EventObject;
import org.freehep.jas.extension.tupleExplorer.cut.Cut;
import org.freehep.jas.extension.tupleExplorer.cut.CutVariable;

/**
 * A CutChangedEvent describes the change in a Cut.
 * @author The FreeHEP team @ SLAC.
 * @version  $Id: CutChangedEvent.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public class CutChangedEvent extends EventObject {
    
    private Cut cut;
    private CutVariable cutVar;
    
    /**
     * Constract a CutChangedEvent object from a Cut
     * @param cut the parent Cut that is firing the event
     *
     */
    public CutChangedEvent( Cut cut ) {
        super( cut );
        this.cut = cut;
    }
    
    /**
     * Construct a CutChangedEvent object from a CutVariable
     * @param cutVar the parent CutVariable that is firing the event
     *
     */
    public CutChangedEvent( CutVariable cutVar ) {
        super( cutVar );
        this.cutVar = cutVar;
    }
    
    /**
     * Construct a CutChangedEvent object from a CutGUI
     * @param cutGUI the parent CutGUI that is firing the event
     *
     */
    public CutChangedEvent( CutGUI cutGUI ) {
        super( cutGUI );
        this.cut = cutGUI.getCut();
    }
    
    /**
     * Construct a CutChangedEvent object from a CutVariableGUI
     * @param cutVariableGUI the parent CutGUI that is firing the event
     *
     */
    public CutChangedEvent( CutVariableGUI cutVariableGUI ) {
        super( cutVariableGUI );
        this.cutVar = cutVariableGUI.getCutVariable();
    }
    
    /**
     * Get the cut name
     * @return the name of the cut
     *
     */
    public String getCutName() {
        return cut.getName();
    }
    
    /**
     * Get the cut state
     * @return the state of the cut
     * @see Cut#getState()
     *
     */
    public int getCutState() {
        return cut.getState();
    }
    
    /**
     * Get the CutVariable's lower bound
     * @return the Cut Variable's lower bound
     *
     */
    public double getVarMin() {
        return cutVar.getMin();
    }
    
    /**
     * Get the CutVariable's upper bound
     * @return the CutVariable's upper bound
     *
     */
    public double getVarMax() {
        return cutVar.getMax();
    }
    
    /**
     * Get the CutVariable's current value
     * @return the CutVariable current value
     *
     */
    public double getVarValue() {
        return cutVar.getValue();
    }
    
    /**
     * Get the state the CutVariable is in
     * @return the CutVariable's state
     *
     */
    public int getVarState() {
        return cutVar.getState();
    }
    
    /**
     * Get the change in the CutVariable's value
     * @return the change in the CutVariable's value
     *
     */
    public double getVarValueChange() {
        return cutVar.getValueChange();
    }
}
