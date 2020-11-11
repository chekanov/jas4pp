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
public class ParabolaFunctionFactory extends AbstractFunctionFactory {
    
    public ParabolaFunctionFactory() {
        super("2nd order polynomial");
    }
    
    public Basic1DFunction createFunction(JASHist h) throws FunctionFactoryError {
        double xMin = h.getXAxis().getMin();
        double xMax = h.getXAxis().getMax();
        double yMin = h.getYAxis().getMin();
        double yMax = h.getYAxis().getMax();
        ParabolaFunction c = new ParabolaFunction(xMin, xMax, yMin, yMax);
        chooseName(c,h);
        return c;
    }
    
    
    private class ParabolaFunction extends AIDAFunctionAdapter {
        public ParabolaFunction(double xMin, double xMax, double yMin, double yMax) {
            super( new BaseModelFunction("p2","", new PolynomialCoreNotNorm(1,3),null ) );
            try {
                double x0 = (xMin + xMax)*.5;
                double x1 = xMin + (xMax - xMin)*.1;
                double y0 = yMin + (yMax - yMin)*.1;
                double y1 = yMax;
                double a = (y1-y0)/((x1-x0)*(x1-x0));
                double b = -2.*a*x0;
                double c = y0 + a*x0*x0;
                setParameter(0,c);
                setParameter(1,b);
                setParameter(2,a);
            } catch ( InvalidFunctionParameter ifp ) {
                throw new RuntimeException("Invalid parameter");
            }
        }
    }
}
