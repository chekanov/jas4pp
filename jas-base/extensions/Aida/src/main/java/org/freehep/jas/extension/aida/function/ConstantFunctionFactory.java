package org.freehep.jas.extension.aida.function;

import hep.aida.ref.function.BaseModelFunction;
import hep.aida.ref.function.PolynomialCoreNotNorm;
import hep.aida.ref.plotter.adapter.AIDAFunctionAdapter;
import jas.hist.Basic1DFunction;
import jas.hist.FunctionFactoryError;
import jas.hist.InvalidFunctionParameter;
import jas.hist.JASHist;
import org.freehep.jas.extension.aida.function.AbstractFunctionFactory;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class ConstantFunctionFactory extends AbstractFunctionFactory {
    
    public ConstantFunctionFactory() {
        super("0th order polynomial");
    }
    
    public Basic1DFunction createFunction(JASHist h) throws FunctionFactoryError {
        double xMin = h.getXAxis().getMin();
        double xMax = h.getXAxis().getMax();
        double yMin = h.getYAxis().getMin();
        double yMax = h.getYAxis().getMax();
        ConstantFunction c = new ConstantFunction(xMin, xMax, yMin, yMax);
        chooseName(c,h);
        return c;
    }
    
    
    private class ConstantFunction extends AIDAFunctionAdapter {
        
        public ConstantFunction(double xMin, double xMax, double yMin, double yMax) {
            super( new BaseModelFunction("p0","", new PolynomialCoreNotNorm(1,1),null ) );
            try {
                double a = yMin + (yMax - yMin)*.35;
                setParameter(0, a);
            } catch ( InvalidFunctionParameter ifp ) {
                throw new RuntimeException("Invalid parameter");
            }
        }
    }
}
