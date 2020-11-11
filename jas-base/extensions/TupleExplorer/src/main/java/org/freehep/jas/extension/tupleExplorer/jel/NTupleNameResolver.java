package org.freehep.jas.extension.tupleExplorer.jel;

import gnu.jel.DVMap;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.util.Value;

/**
 *
 * @author tonyj
 * @version $Id: NTupleNameResolver.java 13893 2011-09-28 23:42:34Z tonyj $
 */
class NTupleNameResolver extends DVMap {

    private MutableTupleTree tupleTree;
    private Vector columns;
    private Hashtable hash;
    
    NTupleNameResolver(MutableTupleTree tupleTree, Vector cols) {
        this.tupleTree = tupleTree;
        this.columns = cols;
        this.hash = new Hashtable();
    }
    
    public Object translate(String name) {
        return (Integer) hash.get(name);
    }
    
    public String getTypeName(String name) {
        MutableTupleColumn col = null;
        int index = 0;
        if (name.equals("null")) return "Object";
        if (hash.get(name) == null) {
            col = getColumn(name);
            if (col == null) return null;
            if ( col.type() != MutableTuple.class ) {
            columns.add(col);
            index = columns.indexOf(col);
            hash.put(name, new Integer(index));
            }
        }
        else col = (MutableTupleColumn) columns.elementAt(((Integer) hash.get(name)).intValue());
        return nameForType(col.type());
    }
    
    
    
    static String nameForType(Class type) {
        if (type.isPrimitive()) {
            String typeName = type.getName();
            return typeName.substring(0,1).toUpperCase()+typeName.substring(1);
        }
        else if (String.class.isAssignableFrom(type)) return "String";
        else if (Date.class.isAssignableFrom(type)) return "Date";
        else return "Object";
    }
    

    
    private MutableTupleColumn getColumn(String columnName) {
        StringTokenizer st = new StringTokenizer(columnName,".");
	int nTokens = 0;
	int i = 0;
        FTreePath treePath = tupleTree.rootMutableTuple().treePath();
        
	if ( (nTokens = st.countTokens()) > 1) {
            while (st.hasMoreTokens()) {
		String colName = st.nextToken();
                treePath = treePath.pathByAddingChild(colName);
            }
        } else 
            treePath = treePath.pathByAddingChild( columnName );

        MutableTupleColumn col = null;
        try {
            MutableTuple tuple = tupleTree.mutableTupleForPath(treePath.getParentPath());
            if ( tuple != null ) col = (MutableTupleColumn)tuple.columnByName(treePath.getLastPathComponent());
        } catch ( IllegalArgumentException iae ) {}
        return col;
    }
}
