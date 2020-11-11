package org.freehep.jas.extension.tupleExplorer.cut;

import hep.aida.ref.tuple.FTupleCursor;
import javax.swing.event.EventListenerList;
import org.freehep.jas.extension.tupleExplorer.cut.CutDataSet;

/**
 * A Cut either accepts or rejects an CutData data set
 * @author The FreeHEP team @ SLAC.
 * @version $Id: Cut.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */

public interface Cut
{

    /**
     * Get the name of the Cut
     * @return the name of the Cut
     *
     */
    String getName();

    /**
     * Set the name of the Cut
     * @param cutName the name of the Cut
     *
     */
    void setName( String cutName );

    /**
     * The Cut can be in any of the following
     * three states: enabled, disabled or iverted.
     * The "inverted" state corresponds to the
     * logical NOT (!).
     *
     */
    public static final int CUT_ENABLED  = 0;
    public static final int CUT_INVERTED = 1;
    public static final int CUT_DISABLED = 2;
    public static final int CUT_DISABLED_INVERTED = 3;

    /**
     * Invert the cut.
     *
     */
    public void invert();
    
    /**
     * Disable the cut.
     *
     */
    public void setDisabled(boolean isDisabled);
    
    /**
     * Get the state of the Cut.
     * @return the state of the Cut
     *
     */
    int getState();

    /**
     * Set the state of the Cut.
     * @param cutState the state of the Cut
     *
     */
    void setState( int cutState );

    /**
     * Check if the Cut is enabled
     * @return <code>true<\code> if the cut is enabled
     *         <code>false<\code> otherwise
     *
     */
    boolean isEnabled();

    /**
     * Check if the Cut is inverted
     * @return <code>true<\code> if the cut is inverted
     *         <code>false<\code> otherwise
     *
     */
    boolean isInverted();

    /**
     * Get the CutDataSet on which the cut is applied
     * @return the CutDataSet.
     *
     */
    CutDataSet getCutDataSet();

    /**
     * Set the CutDataSet on which the cut is applied
     * @param cutDataSet the CutDataSet.
     *
     */
    void setCutDataSet( CutDataSet cutDataSet );

    /**
     * Apply the cut to the CutDataSet.
     * @param cutDataCursor the CutDataCursor to access the CutDataSet current value
     * @return <code>true<\code> if the current value of the CutDataSet is accepted by the cut
     *         <code>false<\code> otherwise
     *
     */
    boolean accept( FTupleCursor cutDataCursor );

    /**
     * Add a CutListener to the Cut
     * @param cutListener the CutListener to add
     *
     */
    void addCutListener( CutListener cutListener );

    /**
     * Remove a CutListener from the Cut
     * @param cutListener the CutListenere to remove
     *
     */
    void removeCutListener( CutListener cutListener );
    
    /**
     * Get the list of CutListeners listening to this Cut
     * @return the list of CutListeners
     *
     */
    EventListenerList getCutListeners();
    
    /**
     * Get the CutGUIListener that listens to the GUI representations
     * of the Cut. 
     * @return the CutGUIListener
     *
     */
    CutListener getCutGUIListener();

    /**
     * Internal class that implements the behavior of CutListener.
     * This class should be registered with the GUI representations of the
     * Cut to listen to their CutChangedEvents.
     * 
     */
    abstract class CutGUIListener implements CutListener 
    {
    }
}

