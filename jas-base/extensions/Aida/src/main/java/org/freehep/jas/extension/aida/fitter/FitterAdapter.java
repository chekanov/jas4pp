package org.freehep.jas.extension.aida.fitter;

import hep.aida.IBaseHistogram;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IFitData;
import hep.aida.IFitParameterSettings;
import hep.aida.IFitResult;
import hep.aida.IFitter;
import hep.aida.IFunction;
import hep.aida.ext.IExtFitter;
import hep.aida.ref.fitter.FitFactory;
import hep.aida.ref.fitter.FitResult;
import hep.aida.ref.fitter.Fitter;
import hep.aida.ref.function.AbstractIFunction;
import hep.aida.ref.histogram.DataPointSet;
import jas.hist.Fittable1DFunction;
import jas.hist.InvalidFunctionParameter;
import org.freehep.jas.extension.aida.function.FunctionWrapper;

public class FitterAdapter extends jas.hist.Fitter implements IExtFitter {
    
    private static FitFactory fitFactory = new FitFactory();
    private Fitter fitter;
    private IFitResult result = null;
    
    FitterAdapter( IFitter fitter ) {
        this.fitter = (Fitter) fitter;
    }
    
    public FitterAdapter(String fitterName) {
        this( fitterName, "chi2" );
    }
    
    FitterAdapter(String fitterName, String fitterType) {
        fitter = (Fitter) fitFactory.createFitter(fitterType, fitterName,"noClone=true");
    }
    
    private FitterAdapter(IFitter fitter, IFitResult result) {
        this.fitter = (Fitter) fitter;
        this.result = result;
    }
    
    protected void fit(Fittable1DFunction fittable1DFunction, double[] x, double[] y, double[] ey) throws jas.hist.FitFailed {
        IFunction fitFunction;
        if ( fittable1DFunction instanceof IFunction )
            fitFunction = (IFunction) fittable1DFunction;
        else {
            String[] parNames = fittable1DFunction.getParameterNames();
            boolean[] inFitPars = fittable1DFunction.getIncludeParametersInFit();
              
            int freePars = fittable1DFunction.getParameterValues().length;
            String[] funcParNames = new String[freePars];
            int count = 0;
            for ( int i = 0; i < inFitPars.length; i++ ) 
                if ( inFitPars[i] ) 
                    funcParNames[count++] = parNames[i];
            fitFunction = new Fittable1DFunctionAdapter(fittable1DFunction,funcParNames);
        }
        
        DataPointSet dps = new DataPointSet("pds", "", 2);
        
        for ( int i = 0; i < x.length; i++ ) {
            IDataPoint point = dps.addPoint();
            point.coordinate(0).setValue(x[i]);
            point.coordinate(1).setValue(y[i]);
            point.coordinate(1).setErrorPlus(ey[i]);
            point.coordinate(1).setErrorMinus(ey[i]);
        }
        
        //***********************************//

        result = fitter.fit((IDataPointSet)dps,fitFunction);
        
        try {
            double[] pars = result.fittedFunction().parameters();
            fittable1DFunction.setFit(new FitterAdapter(fitter,result), result.fittedFunction().parameters());
        } catch ( InvalidFunctionParameter ifp ) {
            throw new RuntimeException();
        }

    }
    
    public double getChiSquared() {
        if ( result!= null )
            return result.quality();
        return 0.;
    }
    
    public double[] getParameterSigmas() {
        if ( result != null ) {
            double[] errors = new double[result.fittedFunction().numberOfParameters()];
            for ( int i = 0; i < errors.length; i++ )
                errors[i] = Math.sqrt( Math.abs(result.covMatrixElement(i,i)) );
            return errors;
        }
        return null;
    }
    
    public String[] constraints() {
        return fitter.constraints();
    }
    
    public IDataPointSet createContour( IFitData iFitData, IFitResult iFitResult, String str, String str3, int param, double param5) {
        IDataPointSet dps = fitter.createContour( iFitData, iFitResult, str, str3, param, param5 );        
        if ( iFitResult.fittedFunction() instanceof Fittable1DFunction ) {
            Fittable1DFunction fittable1DFunction = (Fittable1DFunction) iFitResult.fittedFunction();
            try {
                double[] pars = iFitResult.fittedFunction().parameters();
                fittable1DFunction.setFit(new FitterAdapter(fitter,result), iFitResult.fittedFunction().parameters());
            } catch ( InvalidFunctionParameter ifp ) {
                throw new RuntimeException();
            }
        }
        return dps;
    }
    
    public IDataPointSet createScan1D(IFitData iFitData, IFunction iFunction, String str, int param, double param4, double param5) {
        IDataPointSet dps = fitter.createScan1D( iFitData, iFunction, str, param, param4, param5);
        if ( iFunction instanceof Fittable1DFunction ) {
            Fittable1DFunction fittable1DFunction = (Fittable1DFunction) iFunction;
            try {
                double[] pars = iFunction.parameters();
                fittable1DFunction.setFit(new FitterAdapter(fitter,result), iFunction.parameters());
            } catch ( InvalidFunctionParameter ifp ) {
                throw new RuntimeException();
            }
        }
        return dps;
    }
    
    public String engineName() {
        return fitter.engineName();
    }
    
    private void updateFunction() {
        if ( result != null ) {
            IFunction func = result.fittedFunction();
            Fittable1DFunction f;
            if ( func instanceof Fittable1DFunction )
                f = (Fittable1DFunction) func;
            else {
                f = new FunctionWrapper(func);
                ((FitResult) result).setFittedFunction( (IFunction) f );
            }
            try {
                double[] pars = result.fittedFunction().parameters();
                ( (Fittable1DFunction) f ).setFit(new FitterAdapter(fitter,result), result.fittedFunction().parameters());
            } catch ( InvalidFunctionParameter ifp ) {
                throw new RuntimeException();
            }
        }
    }
    
    public IFitResult fit(IFitData d, IFunction originalFunction) {
        result = fitter.fit(d, originalFunction);
        updateFunction();
        return result;
    }
    
    public IFitResult fit(IFitData d, IFunction originalFunction, String range) {
        result = fitter.fit(d, originalFunction, range);
        updateFunction();
        return result;
    }
    
    public IFitResult fit(IDataPointSet iDataPointSet, IFunction iFunction) {
        result = fitter.fit(iDataPointSet, iFunction);
        updateFunction();
        return result;
    }

    public IFitResult fit(IDataPointSet iDataPointSet, IFunction iFunction, String range) {
        result = fitter.fit(iDataPointSet, iFunction, range);
        updateFunction();
        return result;
    }

    public IFitResult fit(IDataPointSet iDataPointSet, IFunction iFunction, double[] initialParameters) {
        result = fitter.fit(iDataPointSet, iFunction, initialParameters);
        updateFunction();
        return result;
    }

    public IFitResult fit(IDataPointSet iDataPointSet, String str) {
        result = fitter.fit(iDataPointSet, str);
        updateFunction();
        return result;
    }

    public IFitResult fit(IDataPointSet iDataPointSet, String str, String range) {
        result = fitter.fit(iDataPointSet, str, range);
        updateFunction();
        return result;
    }

    public IFitResult fit(IBaseHistogram iBaseHistogram, String str) {
        result = fitter.fit(iBaseHistogram, str);
        updateFunction();
        return result;
    }
    
    public IFitResult fit(IBaseHistogram iBaseHistogram, String str, String range) {
        result = fitter.fit(iBaseHistogram, str, range);
        updateFunction();
        return result;
    }
    
    public IFitResult fit(IBaseHistogram iBaseHistogram, IFunction iFunction) {
        result = fitter.fit(iBaseHistogram, iFunction);
        updateFunction();
        return result;
    }
    
    public IFitResult fit(IBaseHistogram iBaseHistogram, IFunction iFunction, String range) {
        result = fitter.fit(iBaseHistogram, iFunction, range);
        updateFunction();
        return result;
    }
    
    public IFitResult fit(IBaseHistogram iBaseHistogram, String str, double[] values) {
        result = fitter.fit(iBaseHistogram, str, values);
        updateFunction();
        return result;
    }
    
    public IFitResult fit(IBaseHistogram iBaseHistogram, String str, double[] values, String range) {
        result = fitter.fit(iBaseHistogram, str, values, range);
        updateFunction();
        return result;
    }
    
    public IFitResult fit(IFitData iFitData, String str, double[] values) {
        result = fitter.fit(iFitData, str, values);
        updateFunction();
        return result;
    }

    public IFitResult fit(IFitData iFitData, String str, double[] values, String range) {
        result = fitter.fit(iFitData, str, values, range);
        updateFunction();
        return result;
    }

    public IFitResult fit(IDataPointSet iDataPointSet, String str, double[] values) {
        result = fitter.fit(iDataPointSet, str, values);
        updateFunction();
        return result;
    }

    public IFitResult fit(IDataPointSet iDataPointSet, String str, double[] values, String range) {
        result = fitter.fit(iDataPointSet, str, values, range);
        updateFunction();
        return result;
    }

    public IFitResult fit(IFitData iFitData, String str) {
        result = fitter.fit(iFitData, str);
        updateFunction();
        return result;
    }

    public IFitResult fit(IFitData iFitData, String str, String range) {
        result = fitter.fit(iFitData, str, range);
        updateFunction();
        return result;
    }

    public IFitResult fit(IFitData d, IFunction originalFunction, String range, Object correlationObject) {
        result = fitter.fit(d, originalFunction, range, correlationObject);
        updateFunction();
        return result;
    }

    public IFitResult fit(IDataPointSet iDataPointSet, IFunction iFunction, String range, Object correlationObject) {
        result = fitter.fit(iDataPointSet, iFunction, range, correlationObject);
        updateFunction();
        return result;
    }

    public IFitResult fit(IDataPointSet iDataPointSet, IFunction iFunction, double[] initialParameters, String range, Object correlationObject) {
        result = fitter.fit(iDataPointSet, iFunction, initialParameters, range, correlationObject);
        updateFunction();
        return result;
    }
    
    public IFitResult fit(IDataPointSet iDataPointSet, String str, String range, Object correlationObject) {
        result = fitter.fit(iDataPointSet, str, range, correlationObject);
        updateFunction();
        return result;
    }
    
    public IFitResult fit(IFitData iFitData, String str, String range, Object correlationObject) {
        result = fitter.fit(iFitData, str, range, correlationObject);
        updateFunction();
        return result;
    }
    
    public IFitResult fit(IDataPointSet iDataPointSet, String str, double[] values, String range, Object correlationObject) {
        result = fitter.fit(iDataPointSet, str, values, range, correlationObject);
        updateFunction();
        return result;
    }
    
    public IFitResult fit(IFitData iFitData, String str, double[] values, String range, Object correlationObject) {
        result = fitter.fit(iFitData, str, values, range, correlationObject);
        updateFunction();
        return result;
    }
        
    public String fitMethodName() {
        return fitter.fitMethodName();
    }
    
    public IFitParameterSettings fitParameterSettings(String str) {
        return fitter.fitParameterSettings(str);
    }
    
    public String[] listParameterSettings() {
        return fitter.listParameterSettings();
    }
    
    public void resetConstraints() {
        fitter.resetConstraints();
    }
    
    public void resetParameterSettings() {
        fitter.resetParameterSettings();
    }
    
    public void setConstraint(String str) throws java.lang.IllegalArgumentException {
        fitter.setConstraint(str);
    }
    
    public void setEngine(String str) throws java.lang.IllegalArgumentException {
        fitter.setEngine(str);
    }
    
    public void setFitMethod(String str) throws java.lang.IllegalArgumentException {
        fitter.setFitMethod(str);
    }
    
    public void setUseFunctionGradient(boolean param) {
        fitter.setUseFunctionGradient(param);
    }
    
    public boolean useFunctionGradient() {
        return fitter.useFunctionGradient();
    }
    
    private class Fittable1DFunctionAdapter extends AbstractIFunction {
        
        private double[] pars;
        private Fittable1DFunction function;
        
        Fittable1DFunctionAdapter( Fittable1DFunction function, String[] funcParNames ) {
            super("func",new String[]{"xVar"},funcParNames);
            this.function = function;
        }
        
        public double value(double[] values) {
            double val = 0;
            try {
                val = function.valueAt(values[0],pars);
            } catch ( jas.hist.FunctionValueUndefined fvu ) {
                throw new RuntimeException(fvu);
            }
            return val;
        }
        
        public double[] parameters() {
            return function.getParameterValues();
        }
                
        public double parameter(String str) {
            int index = indexOfParameter(str);
            return parameters()[index];
        }
        
        public void setParameter(String str, double param) throws java.lang.IllegalArgumentException {
            int index = indexOfParameter(str);
            pars[index] = param;
        }
        
        public void setParameters(double[] pars) throws java.lang.IllegalArgumentException {
            this.pars = pars;
        }
        
        public String normalizationParameter() {
            return null;
        }
    }
    
}
