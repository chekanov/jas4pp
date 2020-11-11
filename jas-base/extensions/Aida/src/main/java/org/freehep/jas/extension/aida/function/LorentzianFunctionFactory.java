package org.freehep.jas.extension.aida.function;

import hep.aida.ref.function.BaseModelFunction;
import hep.aida.ref.function.LorentzianCoreNotNorm;
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
public class LorentzianFunctionFactory extends AbstractFunctionFactory {
    
    public LorentzianFunctionFactory() {
        super("Lorentzian");
    }
    
    public Basic1DFunction createFunction(JASHist h) throws FunctionFactoryError {
        double xMin = h.getXAxis().getMin();
        double xMax = h.getXAxis().getMax();
        double yMin = h.getYAxis().getMin();
        double yMax = h.getYAxis().getMax();
        LorentzianFunction c = new LorentzianFunction(xMin, xMax, yMin, yMax);
        chooseName(c,h);
        return c;
    }
    
    
    private class LorentzianFunction extends AIDAFunctionAdapter {
        public LorentzianFunction(double xMin, double xMax, double yMin, double yMax) {
            super( new BaseModelFunction("Lorentzian","", new LorentzianCoreNotNorm("Lorentzian"),null ) );
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
