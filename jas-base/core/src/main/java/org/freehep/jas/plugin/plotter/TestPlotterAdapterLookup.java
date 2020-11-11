/*
 * TestPlotterAdapterLookup.java
 *
 * Created on April 24, 2003, 6:34 PM
 */

package org.freehep.jas.plugin.plotter;

import org.freehep.jas.plugin.plotter.PlotterAdapterLookup;
import org.freehep.jas.services.PlotterAdapter;

/**
 *
 * @author  turri
 */
public class TestPlotterAdapterLookup {
    
        
    private PlotterAdapter intAdapter = new IntToDoubleAdapter();
    private PlotterAdapter doubleAdapter = new DoubleToStringAdapter();
    private PlotterAdapter stringAdapter = new StringToIntegerAdapter();
 
    public TestPlotterAdapterLookup() {
    }
    
    public PlotterAdapter intToDoubleAdapter() {
        return intAdapter;
    }
    public PlotterAdapter doubleToStringAdapter() {
        return doubleAdapter;
    }
    public PlotterAdapter stringToIntegerAdapter() {
        return stringAdapter;
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestPlotterAdapterLookup test = new TestPlotterAdapterLookup();
        PlotterAdapterLookup lookup = new PlotterAdapterLookup();
        lookup.registerAdapter( test.intToDoubleAdapter(), Integer.class, Double.class );
        lookup.registerAdapter( test.doubleToStringAdapter(), Double.class, String.class );
        lookup.registerAdapter( test.doubleToStringAdapter(), String.class, Integer.class );
        
        System.out.println("*** Conversion Integer to Double");
        Integer two = new Integer(2);
        System.out.println("Before adapting "+two);
        PlotterAdapter adapter = lookup.adapter(two.getClass(), Double.class);
        Double d = (Double) adapter.adapt(two);
        System.out.println("After adapting "+d);
        

        System.out.println("\n*** Conversion Number to Double");
        adapter = lookup.adapter(Number.class, Double.class);
        if ( adapter != null ) 
            System.out.println("The conversion should have FAILED!!!!");
        else
            System.out.println("Conversion failed as espected");
        
        System.out.println("\n*** Conversion Integer to Number");
        adapter = lookup.adapter(two.getClass(), Number.class);
        if ( adapter == null ) 
            System.out.println("The conversion should have SUCCEEDED!!!!");
        else {
            Number n = (Number) adapter.adapt(two);
            System.out.println("After adapting "+n);
        }
        
        System.out.println("\n*** Conversion Number to Number");
        adapter = lookup.adapter(Number.class, Number.class);
        if ( adapter != null ) 
            System.out.println("The conversion should have FAILED!!!!");
        else
            System.out.println("Conversion failed as espected");
        
        System.out.println("\n*** Conversion Integer to String");
        adapter = lookup.adapter(two.getClass(), String.class);
        if ( adapter == null ) 
            System.out.println("The conversion should have SUCCEEDED!!!!");
        else {
            String s = (String) adapter.adapt(two);
            System.out.println("After adapting "+s);
        }
        
        System.out.println("\n*** Conversion Integer to Integer");
        adapter = lookup.adapter(two.getClass(), Integer.class);
        System.out.println("New Integer "+adapter.adapt(two));
        
    }
    
    private class IntToDoubleAdapter implements PlotterAdapter {
        
        public Object adapt(Object obj) {
            if ( ! ( obj instanceof Integer ) )
                throw new UnsupportedOperationException("Cannot convert "+obj.getClass()+" to Integer");
            return new Double( (double) ((Integer)obj).intValue());
        }
        
    }
    
    private class DoubleToStringAdapter implements PlotterAdapter {
        
        public Object adapt(Object obj) {
            if ( ! ( obj instanceof Double ) )
                throw new UnsupportedOperationException("Cannot convert "+obj.getClass()+" to Double");
            return new String( "s-"+String.valueOf( ((Double)obj).doubleValue()) );
        }
        
    }
    
    private class StringToIntegerAdapter implements PlotterAdapter {
        
        public Object adapt(Object obj) {
            if ( ! ( obj instanceof String ) )
                throw new UnsupportedOperationException("Cannot convert "+obj.getClass()+" to String");
            return new Integer(5);
        }
        
    }
    
}
