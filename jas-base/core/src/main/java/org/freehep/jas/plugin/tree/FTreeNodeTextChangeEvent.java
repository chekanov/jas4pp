package org.freehep.jas.plugin.tree;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class FTreeNodeTextChangeEvent {
    
    private FTreeNode node;
    private String name;
    private String text;
    private String newText;
    private boolean isShowingName;
    private boolean isConsumed = false;
    
    FTreeNodeTextChangeEvent(FTreeNode node, String text, String newText, boolean isShowingName) {
        this.node = node;
        this.text = text;
        this.newText = newText;
        this.isShowingName = isShowingName;
    }
    
    public FTreeNode node() {
        return node;
    }
    
    public String text() {
        return text;
    }
    
    public String newText() {
        return newText;
    }
    
    public boolean isShowingName() {
        return isShowingName;
    }
    
    public void consume() {
        isConsumed = true;
    }
    
    boolean isConsumed() {
        return isConsumed;
    }
}
