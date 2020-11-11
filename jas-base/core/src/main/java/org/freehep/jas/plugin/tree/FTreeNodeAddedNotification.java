package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTreeNotification;

/**
 * The event to be sent to the FTree when a node is added.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FTreeNodeAddedNotification extends FTreeNotification {
    
    private Class clazz;
    private Object obj;
    
    public FTreeNodeAddedNotification(Object source, FTreePath path, Class clazz) {
        this(source,path,clazz,null);
    }

    public FTreeNodeAddedNotification(Object source, String path, Class clazz) {
        this(source,new FTreePath(path),clazz,null);
    }
    
    public FTreeNodeAddedNotification(Object source, FTreePath path, Object obj) {
        this(source,path,obj.getClass(),obj);
    }

    public FTreeNodeAddedNotification(Object source, String path, Object obj) {
        this(source,new FTreePath(path),obj.getClass(),obj);
    }
        
    FTreeNodeAddedNotification(Object source, FTreePath path, Class clazz, Object obj) {
        super(source, path);
        this.clazz = clazz;
        this.obj = obj;
    }
    
    Class nodeClass() {
        return clazz;
    }
    
    Object nodeObject() {
        return obj;
    }
}
