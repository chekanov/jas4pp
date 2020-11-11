package org.freehep.jas.extension.tupleExplorer.cut;
import java.util.EventListener;
/**
 *
 * @author turri
 * @version $Id: CutListener.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public interface CutListener extends EventListener
{

    /**
     * Invoked when the Cut name has changed
     * @param cutChangedEvent the event describing the change
     *
     */
    void cutNameChanged( CutChangedEvent cutChangedEvent );

    /**
     * Invoked when the Cut state has changed
     * @param cutChangedEvent the event describing the change
     *
     */
    void cutStateChanged( CutChangedEvent cutChangedEvent );

    /**
     * Invoked when the condition in the <code>accept<\code> method has changed
     * @param cutChangedEvent the event describing the change
     *
     */
    void cutChanged( CutChangedEvent cutChangedEvent );

}

