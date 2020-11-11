package org.freehep.jas.extension.tupleExplorer.jel;

import gnu.jel.CompilationException;
import gnu.jel.CompiledExpression;
import hep.aida.ref.tuple.FTuple;
import hep.aida.ref.tuple.FTupleColumn;
import hep.aida.ref.tuple.FTupleCursor;
import java.util.Vector;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.util.Value;

/**
 *
 * @author  tonyj
 * @version
 */
public class JELColumn extends MutableTupleColumn {

    private MutableTuple tuple;
    private MutableTupleTree tupleTree;
    private String orig;
    private CompiledExpression expression;
    private Vector columns;
    private NTupleColumnEvaluator evaluator;
    private Object[] dyn;
    private Value v;
    
    private Class[] types =
    { Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Object.class };
    
    public JELColumn(MutableTuple tuple, MutableTupleTree tupleTree, String columnName, String orig, NTupleCompiledExpression ncexp) {
        super(columnName, tuple);
        this.tuple = tuple;
        this.tupleTree = tupleTree;
        this.orig = orig;
        this.expression = ncexp.getCompiledExpression();
        this.columns = ncexp.getColumns();
        this.v = new Value();
        initJELColumn();
    }

    private void initJELColumn() {
        
        this.evaluator = new NTupleColumnEvaluator(tupleTree, columns);
        this.dyn = new Object[]{ evaluator };
        
        Vector metaColumns = new Vector();
        for (int i=0; i<columns.size(); i++) {
            FTupleColumn nc = (FTupleColumn) columns.elementAt(i);
                for (int ii=0; ii<metaColumns.size(); ii++)
                    if ( !((MutableTupleColumn) nc).isCompatible((MutableTupleColumn) metaColumns.elementAt(ii)))
                        throw new RuntimeException("Problem making column \""+name()+
                        "\" :\nColumns \""+((MutableTupleColumn) nc).name()+"\", and \""+
                        ((MutableTupleColumn) metaColumns.elementAt(ii)).name()+"\" are Incompatible!");
                metaColumns.add(nc);
        }
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

    public boolean isFolder() {
        return false;
    }

    public void value(FTupleCursor cursor, Value value) {
        evaluator.setCursor((MutableTupleTreeNavigator)cursor);
        getValue(value);
    }
    
    private Value getValue(Value result) {
        try {
            switch (expression.getType()) {
                case 0: return result.set(expression.evaluate_boolean(dyn));
                case 1: return result.set(expression.evaluate_byte(dyn));
                case 2: return result.set(expression.evaluate_char(dyn));
                case 3: return result.set(expression.evaluate_short(dyn));
                case 4: return result.set(expression.evaluate_int(dyn));
                case 5: return result.set(expression.evaluate_long(dyn));
                case 6: return result.set(expression.evaluate_float(dyn));
                case 7: return result.set(expression.evaluate_double(dyn));
                default: return result.set(expression.evaluate(dyn));
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
            result.set(Double.NaN);
            return result;
        }
    }
    public Class type() {
        return types[expression.getType()];
    }

    public String getExpression() {
        return orig;
    }
    
    public void enableColumnsInCursor( MutableTupleTreeNavigator cursor ) {
        for ( int i = 0; i < columns.size(); i++ ) {
            MutableTupleColumn nc = (MutableTupleColumn) columns.get(i);
            cursor.enablePath( nc.parent().treePath() );
        }
    }
    
    public boolean cursorAdvanced( MutableTupleTreeNavigator cursor ) {
        for ( int i = 0; i < columns.size(); i++ ) {
            MutableTupleColumn nc = (MutableTupleColumn) columns.get(i);
            if ( cursor.advanced( nc.parent().treePath() ) ) {
                return true;
            }
        }
        return false;
    }
}
