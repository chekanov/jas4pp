package org.freehep.jas.extension.tupleExplorer.mutableTuple;

import hep.aida.ref.tuple.FTupleCursor;
import javax.swing.event.EventListenerList;
import org.freehep.jas.extension.tupleExplorer.cut.*;
import org.freehep.jas.extension.tupleExplorer.jel.JELCut;
import org.freehep.jas.plugin.tree.FTreePath;
import hep.aida.ref.tuple.FTupleCursor;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */

public class MutableTupleTreeCut extends AbstractCut {
    
    
    private Cut cut;
    private FTreePath deepestPath;
    private FTreePath tmpPath;
    
    
    public MutableTupleTreeCut() {
        super("");
    }
    
    public MutableTupleTreeCut(Cut cut, FTreePath path) {
        super("");
        setCut(cut);
        setDeepestPath(path.getParentPath());
    }
    
    public void setDeepestPath(FTreePath deepestPath) {
        this.deepestPath = deepestPath;
    }
    
    public void setCut(Cut cut) {
        this.cut = cut;
        setName(cut.getName());
    }
    
    public Cut cut() {
        return cut;
    }
    
    void setPath(FTreePath path) {
        this.tmpPath = path;
    }
    
    public boolean accept( FTupleCursor cutDataCursor ) {
        if ( cutDeepestPath().isDescendant(tmpPath) )
            return cut.accept(cutDataCursor);
        return true;
    }
    
    /**
     * Get the cut's deepest path.
     *
     */
    public FTreePath cutDeepestPath() {
        if ( cut instanceof JELCut )
            return ( (JELCut) cut ).getLeadingPath().getParentPath();
        return deepestPath;
    }
        
    public String getName() {
        return cut.getName();
    }

    public void setName( String cutName ) {
        cut.setName(cutName);
    }

    public void invert() {
        cut.invert();
    }
    
    public void setDisabled(boolean isDisabled) {
        cut.setDisabled(isDisabled);
    }
    
    public int getState() {
        return cut.getState();
    }

    public void setState( int cutState ) {
        cut.setState(cutState);
    }

    public boolean isEnabled() {
        return cut.isEnabled();
    }

    public boolean isInverted() {
        return cut.isInverted();
    }

    public CutDataSet getCutDataSet() {
        return cut.getCutDataSet();
    }

    public void setCutDataSet( CutDataSet cutDataSet ) {
        cut.setCutDataSet(cutDataSet);
    }

    public void addCutListener( CutListener cutListener ) {
        cut.addCutListener(cutListener);
    }

    public void removeCutListener( CutListener cutListener ) {
        cut.removeCutListener(cutListener);
    }

    public EventListenerList getCutListeners() {
        return cut.getCutListeners();
    }

    public CutListener getCutGUIListener() {
        return cut.getCutGUIListener();
    }
    
}


