package org.freehep.jas.extension.aida.function;

import hep.aida.IFunction;
import hep.aida.IManagedObject;
import hep.aida.IModelFunction;
import hep.aida.ref.function.BaseModelFunction;
import hep.aida.ref.function.FunctionChangedEvent;
import hep.aida.ref.function.FunctionDispatcher;
import hep.aida.ref.plotter.adapter.AIDAFunctionAdapter;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class FunctionWrapper extends AIDAFunctionAdapter implements IModelFunction, IManagedObject {
    
    private IModelFunction function;
    private boolean notify;
    
    public FunctionWrapper( IFunction function ) {
        super( function );
        if ( function instanceof IModelFunction )
            this.function = (IModelFunction) function;
        else {
            String name = function.title();
            if ( function instanceof IManagedObject )
                name = ((IManagedObject)function).name();
            this.function = new BaseModelFunction(name, function.title(), function);
        }
        notify = ! ( function instanceof FunctionDispatcher );
    }
    
    public hep.aida.IAnnotation annotation() {
        return function.annotation();
    }
    
    public String codeletString() {
        return function.codeletString();
    }
    
    public int dimension() {
        return function.dimension();
    }
    
    public double[] gradient(double[] values) {
        return function.gradient(values);
    }
    
    public int indexOfParameter(String str) {
        return function.indexOfParameter(str);
    }
    
    public boolean isEqual(hep.aida.IFunction iFunction) {
        return function.isEqual(iFunction);
    }
    
    public int numberOfParameters() {
        return function.numberOfParameters();
    }
    
    public double parameter(String str) {
        return function.parameter(str);
    }
    
    public String[] parameterNames() {
        return function.parameterNames();
    }
    
    public double[] parameters() {
        return function.parameters();
    }
    
    public boolean providesGradient() {
        return function.providesGradient();
    }
    
    public void setParameter(String str, double param) throws java.lang.IllegalArgumentException {
        function.setParameter(str,  param);
        if ( notify )
            functionChanged( new FunctionChangedEvent( FunctionChangedEvent.PARAMETER_VALUE_CHANGED ) );
    }
    
    public void setParameters(double[] values) throws java.lang.IllegalArgumentException {
        function.setParameters(values);
        if ( notify )
            functionChanged( new FunctionChangedEvent( FunctionChangedEvent.PARAMETER_VALUE_CHANGED ) );
    }
    
    public void setTitle(String str) throws java.lang.IllegalArgumentException {
        function.setTitle(str);
        if ( notify )
            functionChanged( new FunctionChangedEvent( FunctionChangedEvent.TITLE_CHANGED ) );
    }
    
    public String title() {
        return function.title();
    }
    
    public double value(double[] values) {
        return function.value(values);
    }
    
    public String variableName(int param) {
        return function.variableName(param);
    }
    
    public String[] variableNames() {
        return function.variableNames();
    }
    
    public String name() {
        if ( function instanceof IManagedObject  )
            return ( (IManagedObject) function ).name();
        return function.title();
    }
    
    public void excludeNormalizationAll() {
        function.excludeNormalizationAll();
    }
    
    public void includeNormalizationAll() {
        function.includeNormalizationAll();
    }
    
    public boolean isNormalized() {
        return function.isNormalized();
    }
    
    public hep.aida.IRangeSet normalizationRange(int param) {
        return function.normalizationRange(param);
    }
    
    public void normalize(boolean param) {
        function.normalize(param);
    }
    
    public double[] parameterGradient(double[] values) {
        return function.parameterGradient(values);
    }
    
    public boolean providesNormalization() {
        return function.providesNormalization();
    }
    
    public boolean providesParameterGradient() {
        return function.providesParameterGradient();
    }
    
    public String normalizationParameter() {
        return function.normalizationParameter();
    }
    
    public String type() {
        if ( function instanceof IManagedObject  )
            return ( (IManagedObject) function ).type();
        return "IModelFunction";
    }
}

