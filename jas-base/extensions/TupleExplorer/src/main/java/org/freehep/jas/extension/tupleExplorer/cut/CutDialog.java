package org.freehep.jas.extension.tupleExplorer.cut;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.freehep.jas.extension.tupleExplorer.cut.Cut;
import org.freehep.jas.extension.tupleExplorer.cut.CutSetPanel;
import org.freehep.jas.extension.tupleExplorer.cut.CutSet;
import org.freehep.jas.extension.tupleExplorer.jel.JELCut;
import org.freehep.jas.extension.tupleExplorer.jel.JELCutPanel;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeCut;

/**
 *
 * @author tonyj
 * @version $Id: CutDialog.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public class CutDialog
{

    /**
     * The CutDialog creates a JComponent for each type of Cut
     *
     */

    /**
     * Show a Cut in its own JPanel
     * @param parent the Component from which the JPanel is originating
     * @param cut the Cut that need to be showed
     * @param tuple the TreeTuple the cut is applied to
     *
     */
    public static void show( Component parent, Cut cut, MutableTupleTree tuple )
    {
	JComponent cp = createPanel( cut, tuple );

	JPanel p = new JPanel( new BorderLayout() );
	p.add( cp, BorderLayout.CENTER );

	Frame f = (Frame) SwingUtilities.getAncestorOfClass( Frame.class,parent );
	JDialog dlg = new JDialog( f, "Cut: "+cut.getName(), false );
	dlg.getContentPane().add( p, BorderLayout.CENTER );
	dlg.pack();
	dlg.setLocationRelativeTo(f);
	dlg.setVisible(true);
    }

    /**
     * Create a panel for a given Cut
     * @param cut the Cut
     * @param tuple the TreeTuple the cut is applied to
     * @return the JComponent for this Cut
     *
     */
    public static JComponent createPanel( Cut cut, MutableTupleTree tupleTree )
    {
        if ( cut instanceof MutableTupleTreeCut )
            cut = ( (MutableTupleTreeCut) cut ).cut();
	if ( cut instanceof Numeric1DCut ) { 
	    return new Numeric1DCutPanel( (Numeric1DCut) cut );
	}
	else if (cut instanceof CutSet) {
	    return new CutSetPanel( (CutSet) cut, tupleTree );
	}
	else {
	    return new JELCutPanel( (JELCut) cut, ((JELCut) cut).getNTuple(), tupleTree );
	}
    }
}
