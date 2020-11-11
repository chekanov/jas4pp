package org.freehep.jas.plugin.tree;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class FTreeNodeEvent {
    
    public static final int NODE_ADDED   = 0;
    public static final int NODE_REMOVED = 1;
    public static final int NODE_CHANGED = 2;
    public static final int NODE_STRUCTURE_CHANGED = 3;
    
    private int eventId;
    private FTreeNode node;
    private FTreeNode source;
    
    FTreeNodeEvent(FTreeNode source, FTreeNode node, int eventId) {
        this.eventId = eventId;
        this.source = source;
        this.node = node;
    }
    
    public int eventId() {
        return eventId;
    }
    
    public FTreeNode source() {
        return source;
    }
    
    public FTreeNode node() {
        return node;
    }
    
    public String toString() {
        return " id "+eventId()+" source "+source()+" node "+node();
    }
    
}
