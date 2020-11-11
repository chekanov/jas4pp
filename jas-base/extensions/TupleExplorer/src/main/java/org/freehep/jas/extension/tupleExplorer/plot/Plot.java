package org.freehep.jas.extension.tupleExplorer.plot;

import org.freehep.jas.extension.tupleExplorer.cut.CutChangedEvent;
import org.freehep.jas.extension.tupleExplorer.cut.CutListenerAdapter;
import org.freehep.jas.extension.tupleExplorer.cut.CutSet;
import org.freehep.jas.extension.tupleExplorer.cut.Cut;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeCutSet;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;

/**
 *
 * @author  tonyj
 * @version $Id: Plot.java 13893 2011-09-28 23:42:34Z tonyj $
 */
// Note: Serializable just to work around bug id: 4473062
//public class Plot implements java.io.Serializable, XMLIO
public class Plot {
    
    private AbstractProjection project;
    private MutableTupleTreeCutSet cuts;
    private String name;
    private MutableTupleTree tuple;
    private boolean isOutOfDate = true;
    
    private CutListenerAdapter cutListener;
    
    // Just for saving and restoring
    protected Plot() {
    }
    
   public Plot(String name, AbstractProjection project, MutableTupleTreeCutSet cuts, MutableTupleTree tuple) {
        setName(name);
        setProjection(project);
        setTupleAndCuts(cuts, tuple);
    }
   
    protected void setName( String name ) {
        this.name = name;
        if ( project != null )
            project.setName(name);
    }
    
    protected void setProjection( AbstractProjection projection ) {
        this.project = projection;
        project.setPlot(this);
        if ( name != null )
            project.setName(name);
        
        Runnable run = new Runnable() {
            public void run() {
                Plot.this.invalidate();
                tuple.run();
            }
        };
        project.setRunnable(run);
    }
    
   protected void setTupleAndCuts( MutableTupleTreeCutSet cuts, MutableTupleTree tuple ) {
        this.cuts = cuts;
        this.tuple = tuple;
        initCutListener();
        cuts.addCutListener( cutListener );
    }
    
    public void destroy() {
        cuts.removeCutListener(cutListener);
        tuple.plots().remove(this);
    }
    private void initCutListener() {
        cutListener = new CutListenerAdapter() {
            public void cutChanged( CutChangedEvent e ) {
                invalidate();
                Plot.this.tuple.runLater();
            }
        };
    }
    public String getName() {
        return name;
    }
    public void start() {
        isOutOfDate = false;
        project.start();
    }
    public void end() {
        project.end();
    }
    public void fill(MutableTupleTreeNavigator cursor) {
      if (cuts.accept(cursor,project.getLeadingPath())) project.fill(cursor);
    }
    public boolean isOutOfDate() {
        return isOutOfDate;
    }
    public void invalidate() {
        isOutOfDate = true;
    }
   public MutableTupleTreeCutSet getCuts() {
        return cuts;
    }
    public MutableTupleTree getTuple() {
        return tuple;
    }
    public AbstractProjection getProjection() {
        return project;
    }
}
