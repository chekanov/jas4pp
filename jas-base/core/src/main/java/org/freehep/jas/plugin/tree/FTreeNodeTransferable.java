package org.freehep.jas.plugin.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import org.freehep.jas.flavors.ObjectFlavor;
import org.freehep.jas.plugin.tree.FTreeNode;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FTreeNodeTransferable implements Transferable {
    
    private Hashtable flavorDataHash = new Hashtable();
    
    protected FTreeNodeTransferable(DefaultFTreeNode[] nodes) {
        for ( int i = 0; i < nodes.length; i++ ) {
            FTreeNode n = nodes[i];
            Class type = n.type();
            addDataForClass(FTreeNode.class, n);
            addDataForClass(type, n.objectForClass(type));
        }
    }
    
    public Object getTransferData(DataFlavor f) throws UnsupportedFlavorException, IOException {
        if ( flavorDataHash.containsKey(f) )
            return flavorDataHash.get(f);
        else throw new UnsupportedFlavorException(f);
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] flavors = new DataFlavor[flavorDataHash.size()];
        Iterator iter = flavorDataHash.keySet().iterator();
        int count = 0;
        while( iter.hasNext() )
            flavors[ count++ ] = (DataFlavor) iter.next();
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor f) {
        return flavorDataHash.containsKey(f);
    }
    
    public void clear() {
        flavorDataHash.clear();
    }
    
    public void addDataForClass( Class clazz, Object data ) {
        flavorDataHash.put( new ObjectFlavor(clazz), data);
    }    
}