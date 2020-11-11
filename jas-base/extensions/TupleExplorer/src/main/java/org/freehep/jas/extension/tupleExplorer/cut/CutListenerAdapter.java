package org.freehep.jas.extension.tupleExplorer.cut;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: CutListenerAdapter.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public class CutListenerAdapter implements CutListener
{
    /**
     * Invoked when the Cut name has changed
     * @param cutChangedEvent the event describing the change
     *
     */
    public void cutNameChanged( CutChangedEvent cutChangedEvent )
    {
    }

    /**
     * Invoked when the Cut state has changed
     * @param cutChangedEvent the event describing the change
     *
     */
    public void cutStateChanged( CutChangedEvent cutChangedEvent )
    {
    }

    /**
     * Invoked when the condition in the <code>accept<\code> method has changed
     * @param cutChangedEvent the event describing the change
     *
     */
    public void cutChanged( CutChangedEvent cutChangedEvent )
    {
    }
}


