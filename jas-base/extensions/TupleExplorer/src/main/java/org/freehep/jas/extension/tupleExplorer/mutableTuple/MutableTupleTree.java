package org.freehep.jas.extension.tupleExplorer.mutableTuple;

import gnu.jel.CompilationException;
import hep.aida.ref.tuple.FTuple;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import org.freehep.jas.extension.tupleExplorer.cut.Cut;
import org.freehep.jas.extension.tupleExplorer.cut.CutColumn;
import org.freehep.jas.extension.tupleExplorer.cut.CutSet;
import org.freehep.jas.extension.tupleExplorer.jel.Compiler;
import org.freehep.jas.extension.tupleExplorer.jel.JELColumn;
import org.freehep.jas.extension.tupleExplorer.jel.NTupleCompiledExpression;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;
import org.freehep.jas.extension.tupleExplorer.plot.Plot;
import org.freehep.jas.extension.tupleExplorer.plot.PlotSet;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection;
import org.freehep.jas.plugin.tree.FTree;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.xml.io.XMLIO;
import org.freehep.xml.io.XMLIOManager;
import org.jdom.Element;


/**
 * A tree model for the MutableTuples.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class MutableTupleTree implements Runnable, XMLIO {
    
    private MutableTuple rootMutableTuple;
    private Hashtable pathMutableTupleHash = new Hashtable();
    private Hashtable mutableTupleTreePathHash = new Hashtable();
    private MutableTupleTreeCutSet allCuts;
    private MutableTupleTreeCutSet defaultCuts;
    private ArrayList addedColumns = new ArrayList();
    private PlotSet plots;
    private boolean valid;
    private FTreeNode node;
    
    public MutableTupleTree(FTuple rootFTuple, FTreePath mutableTuplePath, FTreeNode node) {
        this.node = node;
        this.rootMutableTuple = new MutableTuple(rootFTuple, rootFTuple.name(),this);        
        registerMutableTuple( rootMutableTuple, mutableTuplePath );
        this.allCuts = new MutableTupleTreeCutSet("allCuts");
        this.defaultCuts = new MutableTupleTreeCutSet("defaultCuts");
        this.plots = new PlotSet();
    }
    
    public FTree tree() {
        return node.tree();
    }
    
    public MutableTuple mutableTupleForPath(FTreePath path) {
        MutableTuple mutableTuple = (MutableTuple) pathMutableTupleHash.get(path.toString());
        return mutableTuple;
    }

    public MutableTupleColumn mutableTupleColumnForPath(FTreePath path) {
        MutableTuple mutableTuple = (MutableTuple) pathMutableTupleHash.get(path.getParentPath().toString());
        return (MutableTupleColumn)mutableTuple.columnByName(path.getLastPathComponent());
    }
    
    public FTreePath treePathForMutableTuple( MutableTuple tuple ) {
        return (FTreePath) mutableTupleTreePathHash.get(tuple);
    }
    
    public MutableTuple rootMutableTuple() {
        return rootMutableTuple;
    }
    
    public void removeMutableTupleListener(MutableTupleListener listener) {
        Iterator mutableTupleIter = pathMutableTupleHash.values().iterator();
        while( mutableTupleIter.hasNext() )
            ( (MutableTuple) mutableTupleIter.next() ).removeMutableTupleListener(listener);
    }
    
    public void addMutableTupleListener(MutableTupleListener listener) {
        Iterator mutableTupleIter = pathMutableTupleHash.values().iterator();
        while( mutableTupleIter.hasNext() )
            ( (MutableTuple) mutableTupleIter.next() ).addMutableTupleListener(listener);
    }
    
    private void registerMutableTuple(MutableTuple mutableTuple, FTreePath path) {
        //Add all the MutableTupleListeners to this tuple.
        if ( mutableTuple != rootMutableTuple ) {
            EventListenerList listeners = rootMutableTuple.listeners();
            Object[] l = listeners.getListenerList();
            for ( int i = 0; i < listeners.getListenerCount(); i++ )
                mutableTuple.addMutableTupleListener((MutableTupleListener)l[i]);
        }

        pathMutableTupleHash.put(path.toString(),  mutableTuple);
        mutableTupleTreePathHash.put(mutableTuple, path);
        for ( int i = 0; i < mutableTuple.columns(); i++ ) {
            if ( ((MutableTupleColumn)mutableTuple.columnByIndex(i)).isFolder() ) {
                FTuple tuple = mutableTuple.tuple(i);
                String tupleName = tuple.name();
                registerMutableTuple( new MutableTuple( tuple, tupleName, this), path.pathByAddingChild(tupleName) );
            }
        }
    }
    
    
    public CutSet allCuts() {
        return allCuts;
    }
    
    public CutSet defaultCuts() {
        return defaultCuts;
    }
    
    public PlotSet plots() {
        return plots;
    }
    
    public void addCut( MutableTupleTreeCut cut, boolean addToDefaultCut ) {
        allCuts.addCut(cut);
        if ( addToDefaultCut )
            defaultCuts.addCut(cut);
        addCutColumn(cut);
    }
    public void addCut( MutableTupleTreeCutSet cut ) {
        allCuts.addCut(cut);
        addCutColumn(cut);
    }
    
    private void addCutColumn(Cut cut) {
        rootMutableTuple.addMutableTupleColumn(new CutColumn(cut,rootMutableTuple));
    }
    
    public void runLater() {
        if (valid) javax.swing.SwingUtilities.invokeLater(this);
        valid = false;
    }
    
    public void run() {
        valid = true;
        MutableTupleTreeNavigator cursor = rootMutableTuple.treeCursor();
        cursor.disableAllChild();
        
        Plot[] plots = plots().outOfDatePlots();
        int nPlots = plots.length;
        
        ArrayList pathList = new ArrayList();
        FTreePath[] paths = new FTreePath[nPlots];
        int[] nPlotsForPath = new int[nPlots];
        Plot[][] plotsForPath = new Plot[nPlots][nPlots];
        MutableTupleTreeNavigator[] cursorsForPath = new MutableTupleTreeNavigator[nPlots];
        
        for (int i = 0; i < nPlots ; i++) {
            AbstractProjection projection = plots[i].getProjection();
            FTreePath leadingPath = projection.getLeadingPath();
            if ( ! pathList.contains( leadingPath.toString() ) ) {
                pathList.add(leadingPath.toString());
                paths[ pathList.size()-1 ] = leadingPath;
                nPlotsForPath[ pathList.size()-1 ] = 0;
                cursor.enablePath(leadingPath);
            }
            int pathIndex = pathList.indexOf(leadingPath.toString());
            plotsForPath[ pathIndex ][ nPlotsForPath[pathIndex] ] = plots[i];
            cursorsForPath[pathIndex] = cursor.cursorForPath( paths[pathIndex].toString() );
            nPlotsForPath[ pathIndex ]++;
        }
        
        
        // prepare plots
        for (int i = 0; i < nPlots; i++) plots[i].start();
        
        int nPaths = pathList.size();
        
        //        long startTime = System.currentTimeMillis();
        
        //        System.out.println("** Plots to update : "+nPlots);
        
        // update plots
        cursor.start();
        while (cursor.next()) {
            for ( int i = 0; i < nPaths; i++ )
                if ( cursorsForPath[i].advanced() )
                    for ( int k = 0; k < nPlotsForPath[i]; k++ )
                        plotsForPath[i][k].fill(cursor);
        }        
        //        long endTime = System.currentTimeMillis();
        //        long dt = endTime-startTime;
        //        System.out.println("Filling took "+dt+" milliseconds");
        
        // mark the plots as done
        for (int i=0; i<nPlots; i++) plots[i].end();
    }
    
    public void restore(XMLIOManager xmlioManager, Element nodeEl) {
        
        Element columnsEl = nodeEl.getChild("TupleColumns");
        List columns = columnsEl.getChildren("Column");
        for ( int i = 0; i < columns.size(); i++ ) {
            Element colEl = (Element) columns.get(i);
            String expression = colEl.getAttributeValue("expression");
            NTupleCompiledExpression exp;
            try {
                exp = Compiler.compile(this,expression,null);
            } catch ( CompilationException ce ) {
                throw new IllegalArgumentException("Illegal Expression "+expression);
            }
            String path = colEl.getAttributeValue("path");
            FTreePath treePath = new FTreePath(path);
            MutableTuple tuple = mutableTupleForPath( treePath.getParentPath() );
            JELColumn col = new JELColumn(tuple,this,treePath.getLastPathComponent(),expression,exp);
            tuple.addMutableTupleColumn(col);
        }
        
        Element cutsEl = nodeEl.getChild("Cuts");
        List cutsList = cutsEl.getChildren();
        this.allCuts = (MutableTupleTreeCutSet)xmlioManager.restore( (Element) cutsList.get(0) );
        this.defaultCuts = (MutableTupleTreeCutSet)xmlioManager.restore( (Element) cutsList.get(1) );
        
        for ( int i = 0; i < allCuts.getNCuts(); i++ )
            addCutColumn( allCuts.getCut(i) );
        
        Element plotsEl = nodeEl.getChild("Plots");
        List plotsList = plotsEl.getChildren();
        for ( int i = 0; i < plotsList.size(); i++ ) {
            plots().add( (Plot)xmlioManager.restore(  (Element) plotsList.get(i) ) );
        }
    }
    
    public void save(XMLIOManager xmlioManager, Element nodeEl) {

        //Save the cuts.
        Element cutsEl = new Element("Cuts");
        cutsEl.addContent( xmlioManager.save( allCuts() ) );
        cutsEl.addContent( xmlioManager.save( defaultCuts() ) );
        nodeEl.addContent( cutsEl );
        
        //Save extra columns
        Element tupleColumnsEl = new Element("TupleColumns");
        Enumeration mutableTuples = mutableTupleTreePathHash.keys();
        while( mutableTuples.hasMoreElements() ) {
            MutableTuple tuple = (MutableTuple) mutableTuples.nextElement();
            for ( int i = 0; i < tuple.columns(); i++ ) {
                MutableTupleColumn column = (MutableTupleColumn)tuple.column(i);
                if ( column instanceof JELColumn ) {
                    JELColumn col = (JELColumn) column;
                    Element colEl = new Element("Column");
                    colEl.setAttribute("path", column.treePath().toString() );
                    colEl.setAttribute("expression",col.getExpression());
                    tupleColumnsEl.addContent( colEl );
                }
            }
        }
        nodeEl.addContent( tupleColumnsEl );
        
        //Save the plots.
        PlotSet plots = plots();
        Element plotsEl = new Element("Plots");
        int nPlots = plots.getNPlots();
        for ( int i = 0; i < nPlots; i++ )
            plotsEl.addContent( xmlioManager.save(plots.getPlot(i)) );
        nodeEl.addContent(plotsEl);
        
    }
    
}
