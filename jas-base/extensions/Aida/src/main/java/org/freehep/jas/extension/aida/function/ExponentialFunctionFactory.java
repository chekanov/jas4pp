package org.freehep.jas.extension.aida.function;

import hep.aida.ref.function.BaseModelFunction;
import hep.aida.ref.function.ExponentialCoreNotNorm;
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
public class ExponentialFunctionFactory extends AbstractFunctionFactory {
    
    public ExponentialFunctionFactory() {
        super("Exponential");
    }
    
    public Basic1DFunction createFunction(JASHist h) throws FunctionFactoryError {
        double xMin = h.getXAxis().getMin();
        double xMax = h.getXAxis().getMax();
        double yMin = h.getYAxis().getMin();
        double yMax = h.getYAxis().getMax();
        ExponentialFunction c = new ExponentialFunction(xMin, xMax, yMin, yMax);
        chooseName(c,h);
        return c;
    }
    
    
    private class ExponentialFunction extends AIDAFunctionAdapter {
        public ExponentialFunction(double xMin, double xMax, double yMin, double yMax) {
            super( new BaseModelFunction("exp","", new ExponentialCoreNotNorm("exp"),null ) );
            try {
                double a = yMin + (yMax - yMin)*.8;
                double b = xMin + (xMax - xMin)*.8;
                setParameter(0, a);
                setParameter(1,(1./b)*Math.log(Math.abs(yMin/a)+2));
            } catch ( InvalidFunctionParameter ifp ) {
                throw new RuntimeException("Invalid parameter");
            }
        }
    }
}
