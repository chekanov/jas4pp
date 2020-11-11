package org.freehep.jas.extension.tupleExplorer.cut;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version
 */

public class Numeric1DCutVariable extends AbstractCutVariable
{
    
    /** 
     * Create a new Numeric1DCutVariable object
     * @param cutName the name of the CutVariable
     *
     */
    public Numeric1DCutVariable( String cutName )
    {
	super( cutName );
    }
    
    /** 
     * Create a new Numeric1DCutVariable object
     * @param cutName the name of the cut
     * @param cutData the CutData on which the cut is applied
     *
     */
    public Numeric1DCutVariable( String cutName, CutDataSet cutDataSet )
    {
	super( cutName, cutDataSet );
    }
}
