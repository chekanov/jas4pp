package org.freehep.jas.extension.tupleExplorer.jel;

import gnu.jel.CompilationException;
import gnu.jel.CompiledExpression;
import hep.aida.ref.tuple.FTuple;
import hep.aida.ref.tuple.FTupleCursor;
import java.util.Vector;
import org.freehep.jas.extension.tupleExplorer.cut.AbstractCut;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;
import org.freehep.jas.plugin.tree.FTreePath;

/**
 *
 * @author  tonyj
 * @version $Id: JELCut.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public class JELCut extends AbstractCut {
    
    private String name;
    private CompiledExpression exp;
    private Vector columns;
    private MutableTuple tuple;
    private MutableTupleTree tupleTree;
    private String expression;
    private NTupleColumnEvaluator evaluator;
    private Object[] dyn;
    
    public JELCut(String name, MutableTuple tuple, MutableTupleTree tupleTree, NTupleCompiledExpression ncexp, String expression) {
        super(name);
        this.tuple = tuple;
        this.tupleTree = tupleTree;
        this.exp = ncexp.getCompiledExpression();
        this.columns = ncexp.getColumns();
        this.expression = expression;
        evaluator = new NTupleColumnEvaluator(tupleTree, columns);
        dyn = new Object[] { evaluator };
    }
    void setExpression(NTupleCompiledExpression ncexp, String expression) {
        this.exp = ncexp.getCompiledExpression();
        this.columns = ncexp.getColumns();
        this.expression = expression;
        evaluator = new NTupleColumnEvaluator(tupleTree, columns);
        dyn = new Object[] { evaluator };
        fireCutChanged();
    }

    public FTreePath getLeadingPath() {
        FTreePath leadingPath = null;
        for ( int i = 0; i < columns.size(); i++ ) {
            MutableTupleColumn col = (MutableTupleColumn) columns.get(i);
            FTreePath colPath = col.treePath();
            if ( leadingPath == null )
                leadingPath = colPath;
            else 
                if ( leadingPath.getParentPath().isDescendant( colPath.getParentPath() ) )
                    leadingPath = colPath;
        }
        return leadingPath;
    }

    public boolean accept( FTupleCursor cursor) {
	if ( !isEnabled() ) return true;
        try {
            evaluator.setCursor((MutableTupleTreeNavigator)cursor);
            return exp.evaluate_boolean(dyn);
        }
        catch (Throwable t) {
            return false;
        }
    }
    public String getExpression() {
        return expression;
    }
    
    public MutableTuple getNTuple() {
        return tuple;
    }
    
}
