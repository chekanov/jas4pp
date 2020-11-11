package org.freehep.jas.extension.aida.function;

import hep.aida.ref.function.BaseModelFunction;
import hep.aida.ref.function.GaussianCoreNotNorm;
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
public class GaussianFunctionFactory extends AbstractFunctionFactory {
    
    public GaussianFunctionFactory() {
        super("Gaussian");
    }
    
    public Basic1DFunction createFunction(JASHist h) throws FunctionFactoryError {
        double xMin = h.getXAxis().getMin();
        double xMax = h.getXAxis().getMax();
        double yMin = h.getYAxis().getMin();
        double yMax = h.getYAxis().getMax();
        GaussianFunction c = new GaussianFunction(xMin, xMax, yMin, yMax);
        chooseName(c,h);
        return c;
    }
    
    
    private class GaussianFunction extends AIDAFunctionAdapter {
        public GaussianFunction(double xMin, double xMax, double yMin, double yMax) {
            super( new BaseModelFunction("gauss","", new GaussianCoreNotNorm("gauss"),null ) );
            try {
                setParameter(0, yMin + (yMax - yMin)*.8);
                setParameter(1, (xMin + xMax)/2);
                setParameter(2, (xMax - xMin)/6);
            } catch ( InvalidFunctionParameter ifp ) {
                throw new RuntimeException("Invalid parameter");
            }
        }
    }
}
