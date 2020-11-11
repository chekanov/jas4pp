package org.freehep.jas.extension.tupleExplorer.cut;

/**
 * A CutGUI is a variable of a CutGUI
 * @author turri
 * @version $Id: CutGUI.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public interface CutGUI
{

    /**
     * Get the Cut that this CutGUI
     * is a representation of.
     * @return the Cut
     *
     */
    Cut getCut();

    /**
     * When closing the GUI it is important to
     * properly remove all the listeners associated
     * with the Cut.
     *
     */
    void removeCutListeners();

}
