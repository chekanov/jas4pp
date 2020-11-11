package org.freehep.jas.extension.tupleExplorer.cut;

import hep.aida.ref.tuple.FTuple;
import hep.aida.ref.tuple.FTupleColumn;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author tonyj
 * @version $Id: NTupleListModel.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public class NTupleListModel extends DefaultComboBoxModel {
    
    private FTuple tuple;
    private Class filter;
    
    /** Creates new NTupleListModel
     * @param tuple The tuple to base the list on
     * @param filter If non-null only columns of this type (or subtype) will be included
     */
    public NTupleListModel(FTuple tuple, Class filter) {
        this.tuple = tuple;
        if (filter == null) filter = Object.class;
        this.filter = filter;
        addCols(tuple);
    }
    
    private void addCols(FTuple tuple) {
        int n = tuple.columns();
        for (int i=0; i<n; i++) {
            FTupleColumn col = tuple.columnByIndex(i);
            Class type = col.type();
            if ( FTuple.class.isAssignableFrom( col.type()) ) {
//                addCols( tuple.tuple(i) );
                continue;
            }
            if (type.isPrimitive()) type = (Class) wrap.get(type);
            if (!filter.isAssignableFrom(type)) continue;
            addElement(col);
        }
    }
    
    private static HashMap wrap = new HashMap();
    static {
        wrap.put(Integer.TYPE,Integer.class);
        wrap.put(Long.TYPE,Long.class);
        wrap.put(Short.TYPE,Short.class);
        wrap.put(Byte.TYPE,Byte.class);
        wrap.put(Double.TYPE,Double.class);
        wrap.put(Float.TYPE,Float.class);
        wrap.put(Character.TYPE,Character.class);
        wrap.put(Boolean.TYPE,Boolean.class);    }
}
