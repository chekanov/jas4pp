package org.freehep.jas.extension.tupleExplorer.cut;

import hep.aida.ref.tuple.FTuple;
import hep.aida.ref.tuple.FTupleCursor;
import org.freehep.jas.extension.tupleExplorer.cut.Cut;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.util.Value;

/**
 *
 * @author tonyj
 * @version $Id: CutColumn.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public class CutColumn extends MutableTupleColumn {

    private Cut cut;
    private int state;
    private int oldRow = FTuple.ROWS_UNKNOWN;
    private int newRow = FTuple.ROWS_UNKNOWN;
    
    
    public CutColumn( Cut cut, MutableTuple tuple) {
        super(cut.getName(), tuple);
        this.cut = cut;
    }

    public void value(FTupleCursor cursor, Value value) {
        value.set(cut.accept(cursor));
        newRow = cursor.row();
    }
    
    public void maxValue(Value value) {
        throw new UnsupportedOperationException("Not Supported");
    }
    public void minValue(Value value) {
        throw new UnsupportedOperationException("Not Supported");
    }
    public void meanValue(Value value)  {
        throw new UnsupportedOperationException("Not Supported");
    }
    public void defaultValue(Value value)  {
        throw new UnsupportedOperationException("Not Supported");
    }
    public void rmsValue(Value value)  {
        throw new UnsupportedOperationException("Not Supported");
    }
    public Class type() {
        return Boolean.TYPE;
    }

    public boolean isFolder() {
        return false;
    }

    public Cut getCut() {
        return cut;
    }
    
    public int getState() {
        return state;
    }
    
    public void setState(int state) {
        this.state = state;
    }
    
}
