package org.freehep.jas.extension.tupleExplorer.jel;

import gnu.jel.CompilationException;
import gnu.jel.CompiledExpression;
import gnu.jel.Evaluator;
import gnu.jel.Library;
import java.util.Vector;
import org.freehep.jas.extension.tupleExplorer.jel.NTupleCompiledExpression;
import org.freehep.jas.extension.tupleExplorer.jel.NTupleNameResolver;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;

/**
 *
 * @author  tonyj
 * @version
 */
public class Compiler {
    
    public static NTupleCompiledExpression compile(MutableTupleTree tupleTree, String expression, Class resultType) throws CompilationException {
        Vector columns =  new Vector();
        // Set up the library
        Class[] staticLib = { java.lang.Math.class };
        Class[] dynamicLib = { NTupleColumnEvaluator.class };
        Class[] dotLib = { Object.class, String.class, java.util.Date.class };
        Library lib=new Library(staticLib, dynamicLib, dotLib, new NTupleNameResolver(tupleTree, columns), null);
        lib.markStateDependent("random",null);
        CompiledExpression cexpr = Evaluator.compile(expression,lib,resultType);
        return new NTupleCompiledExpression(cexpr, columns);
    }
}
