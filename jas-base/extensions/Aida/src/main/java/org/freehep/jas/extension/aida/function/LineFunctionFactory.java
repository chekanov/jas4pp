package org.freehep.jas.extension.aida.function;

import hep.aida.ref.function.BaseModelFunction;
import hep.aida.ref.function.PolynomialCoreNotNorm;
import hep.aida.ref.plotter.adapter.AIDAFunctionAdapter;
import jas.hist.Basic1DFunction;
import jas.hist.FunctionFactoryError;
import jas.hist.InvalidFunctionParameter;
import jas.hist.JASHist;
import jas.hist.JASHistAxis;
import org.freehep.jas.extension.aida.function.AbstractFunctionFactory;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class LineFunctionFactory extends AbstractFunctionFactory {
    
    public LineFunctionFactory() {
        super("1st order polynomial");
    }
    
    public Basic1DFunction createFunction(JASHist h) throws FunctionFactoryError {
        double xMin = h.getXAxis().getMin();
        double xMax = h.getXAxis().getMax();
        double yMin = h.getYAxis().getMin();
        double yMax = h.getYAxis().getMax();
        /*
        if ( h.getXAxis().getAxisType() == JASHistAxis.DATE ) {
            xMin *= 1000.;
            xMax *= 1000.;
        }
        if ( h.getYAxis().getAxisType() == JASHistAxis.DATE ) {
            yMin *= 1000.;
            yMax *= 1000.;
        }
        System.out.println("Creating line for plot : "+xMin+" "+xMax+" "+yMin+" "+yMax);
         */
        LineFunction c = new LineFunction(xMin, xMax, yMin, yMax);
        chooseName(c,h);
        return c;
    }
    
    
    private class LineFunction extends AIDAFunctionAdapter {
        public LineFunction(double xMin, double xMax, double yMin, double yMax) {
            super( new BaseModelFunction("p1","", new PolynomialCoreNotNorm(1,2),null ) );
            try {
                double a = yMin + (yMax - yMin)*.8;
                double b = xMin + (xMax - xMin)*.8;
                setParameter(0, (yMin*xMin - a*b)/(xMin-b) );
                setParameter(1, (a-yMin)/(xMin-b) );
            } catch ( InvalidFunctionParameter ifp ) {
                throw new RuntimeException("Invalid parameter");
            }
        }
    }
}
