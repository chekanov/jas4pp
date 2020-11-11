package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTreePath;

/** The base class for all the events that can modify a FTreeNode.
 * @author The FreeHEP team @ SLAC.
 */
class FTreeNotification extends java.util.EventObject {
    
    private FTreePath path;

    /** The default constructor.
     * @param source The Object from which the event originated
     * @param path The FTreePath fo the FTreeNode to which the
     * event is referring to
     */    
    FTreeNotification(Object source, FTreePath path) {
        super(source);
        this.path = path;
    }
    
    /** Get the FTreePath of the FTreeNode to which this
     * event is referring to.
     * @return The FTreePath of the corresponding node
     */    
    protected FTreePath nodePath() {
        return path;
    }

    /** Get the name of the FTreeNode to which this event is
     * referring to.
     * @return The name of the node.
     */    
    protected String nodeName() {
        return (String) path.getLastPathComponent().toString();
    }
    
}
