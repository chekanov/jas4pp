package org.freehep.jas.extension.tupleExplorer.cut;
import java.util.ArrayList;

/**
 * A CutWithVariables is a Cut with one or more CutVariable
 * @author turri
 * @version $Id: CutWithVariables.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public interface CutWithVariables extends Cut, CutVariableListener
{

    /**
     * Add a CutVariable to the CutWithVariables
     * @param cutVariable the CutVariable to add
     *
     */
    void addCutVariable( CutVariable cutVariable );

    /**
     * Remove a CutVariable from the CutWithVariables
     * @param cutVariable the CutVariable to remove
     *
     */
    void removeCutVariable( CutVariable cutVariable );

    /**
     * Get the list of CutVariables
     * @return the list of CutVariables
     *
     */
    ArrayList getCutVariables();

    /**
     * Get the nth CutVariable
     * @param  nCutVar the index of the desired CutVariable
     * @return the nth CutVariables
     *
     */
    CutVariable getCutVariable( int nCutVar );

    /**
     * Get the number of CutVariables
     * @return the number of CutVariables
     *
     */
    int getNCutVariables();

}



