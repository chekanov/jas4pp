package org.freehep.jas.extension.aida;

import hep.aida.ITuple;
import hep.aida.ref.tuple.FTupleAdapter;
import hep.aida.ref.tuple.FTuple;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.FTreeNodeObjectProvider;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class ITupleFTupleObjectProvider implements FTreeNodeObjectProvider {
        
    public Object objectForNode(FTreeNode node, Class clazz) {
        if (! (FTuple.class.isAssignableFrom(clazz))) return null;
        Object obj = node.value("FTupleAdapter");
        if ( obj == null ) {
            obj = (ITuple) node.objectForClass( ITuple.class );
            if ( ! ( obj instanceof FTuple ) ) 
                obj = new FTupleAdapter( (ITuple) obj );
            node.addKey("FTupleAdapter", obj);
        }
        return obj;
    }    
}
