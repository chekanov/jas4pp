package org.freehep.jas.extension.tupleExplorer.mutableTuple;

import java.util.EventListener;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface MutableTupleListener extends EventListener {
    public void columnsAdded(MutableTupleEvent e);
    public void columnsRemoved(MutableTupleEvent e);
    public void columnsChanged(MutableTupleEvent e);
}