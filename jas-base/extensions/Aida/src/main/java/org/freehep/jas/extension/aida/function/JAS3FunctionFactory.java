package org.freehep.jas.extension.aida.function;

import hep.aida.IFunction;
import hep.aida.ITree;
import hep.aida.ref.function.FunctionFactory;
import org.freehep.jas.extension.aida.function.FunctionWrapper;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class JAS3FunctionFactory extends FunctionFactory {
    
    public JAS3FunctionFactory( ITree tree ) {
        super( tree );
    }
    
    public IFunction createFunctionByName(String path, String model) {
        IFunction function = super.createFunctionByName( path, model );
        return new FunctionWrapper( function );
    }

    public IFunction createFunctionFromScript(String path, int dim, String valexpr, String parameters, String description) {
        IFunction function = super.createFunctionFromScript( path, dim, valexpr, parameters, description);
        return new FunctionWrapper( function );
    }

    public IFunction createFunctionFromScript(String path, int dim, String valexpr, String parameters, String description, String gradexpr) {
        IFunction function = super.createFunctionFromScript( path, dim, valexpr, parameters, description, gradexpr);
        return new FunctionWrapper( function );
    }


    public IFunction cloneFunction(String path, IFunction f) {
        IFunction function = super.cloneFunction( path, f );
        return new FunctionWrapper( function );
    }
}
