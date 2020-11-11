package org.freehep.jas.extension.tupleExplorer.cut;

import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: CutPanel.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */

public class CutPanel extends JPanel
{
    /**
     * Create a new CutPanel object.
     *
     */
    public CutPanel()
    {
	super( new BorderLayout() );
    }

    /**
     * Create a new CutPanel object.
     * @param cut the cut this panel is representing
     *
     */
    public CutPanel( Cut cut )
    {
	super( new BorderLayout() );
	setCut( cut );
    }

    /**
     * Set the Cut the Panel is representing
     * @param cut the Cut
     *
     */
    public void setCut( Cut cut )
    {
	JPanel panel = new JPanel( new BorderLayout() );
	panel.add( new CutNamePanel( cut ), BorderLayout.WEST );
	panel.add( new CutStatePanel( cut ), BorderLayout.EAST );
	
	add( panel, BorderLayout.NORTH );
    }

}





