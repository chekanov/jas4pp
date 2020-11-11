package org.freehep.jas.extension.tupleExplorer.cut;

import org.freehep.jas.extension.tupleExplorer.cut.CutVariable;

/**
 * A CutVariableGUI is a variable of a CutGUI
 * @author The FreeHEP team @ SLAC
 * @version $Id: CutVariableGUI.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public interface CutVariableGUI
{

    /**
     * Get the CutVariable that this CutVariableGUI
     * is a representation of.
     * @return the CutVariable
     *
     */
    CutVariable getCutVariable();

    /**
     * When closing the GUI it is important to
     * properly remove all the listeners associated
     * with the CutVariable.
     *
     */
    void removeCutVariableListeners();

}
