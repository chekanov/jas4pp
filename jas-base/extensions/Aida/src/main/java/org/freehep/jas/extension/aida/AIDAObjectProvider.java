package org.freehep.jas.extension.aida;

import hep.aida.IBaseHistogram;
import hep.aida.IDataPointSet;
import hep.aida.IFunction;
import hep.aida.IManagedObject;
import hep.aida.ITuple;
import hep.aida.ref.tree.Folder;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.FTreeNodeObjectProvider;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class AIDAObjectProvider implements FTreeNodeObjectProvider {
    
    private AIDAPlugin thePlugin;
    
    /** Creates a new instance of AIDAObjectProvider */
    public AIDAObjectProvider(AIDAPlugin thePlugin) {
        this.thePlugin = thePlugin;
    }
    
    public Object objectForNode(FTreeNode node, Class clazz) {
        //The following line is needed because Folder implements IManagedObject.
        if ( Folder.class.isAssignableFrom(node.type()) ) return null;
        Object obj = node.value("AidaObject");
        if ( obj == null ) {
            try {
                obj = thePlugin.aidaMasterTree().find( thePlugin.fullPath(node.path()) );
                node.addKey("AidaObject", obj);
            } catch ( IllegalArgumentException iae ) {
                obj = null;
            }
        }
        if (obj instanceof IManagedObject) {
            if (((IManagedObject) obj).type().equalsIgnoreCase("RemoteUnavailableObject")) return null;
        }
        return obj;
    }    
}
