package org.freehep.jas.extension.aida.function;

import hep.aida.ref.plotter.adapter.AIDAFunctionAdapter;
import jas.hist.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import jas.util.*;
import org.freehep.jas.extension.aida.function.AbstractFunctionFactory;
import hep.aida.IFunction;
import hep.aida.ref.plotter.adapter.AIDAFunctionAdapter;
import java.util.ArrayList;
import javax.swing.*;

public class SumFunctionFactory extends AbstractFunctionFactory {
    
    public SumFunctionFactory() {
        super("Sum of existing functions");
    }
    
    public Basic1DFunction createFunction(JASHist h) throws FunctionFactoryError {
        
        SumOfFunctions sum = new SumOfFunctions(h);
        chooseName(sum, h);
        int n=0;
        
        // By default add all the other functions!
        
        Enumeration e = h.get1DFunctions();
        while (e.hasMoreElements()) {
            JASHistData data = (JASHistData) e.nextElement();
            Basic1DFunction func = (Basic1DFunction) data.getDataSource();
            sum.addFunction(func);
            n++;
        }
        
        if (n<2) throw new FunctionFactoryError("Need at least two functions to sum!");
        
        return (Basic1DFunction)sum;
    }
    
    public Icon getFunctionIcon() {
        return icon;
    }
    private Icon icon = JASIcon.create(this,"sum.gif");
    
    
    
    
    
    
    private class SumOfFunctions extends AIDAFunctionAdapter implements Observer, FunctionAdvancedOptions {
        
        private hep.aida.ref.function.SumOfFunctions sum;
        private JASHist h;
        
        public SumOfFunctions(JASHist h) {
            super( new hep.aida.ref.function.SumOfFunctions("sum", new ArrayList() ) );
            sum = (hep.aida.ref.function.SumOfFunctions) function();
            this.h = h;
        }
        
        void addFunction(Basic1DFunction f) {
            sum.addFunction( ((AIDAFunctionAdapter)f).function() );
            f.addObserver(this);
            mapParams();
        }
        
        boolean containsFunction(Basic1DFunction f) {
            return sum.containsFunction(((AIDAFunctionAdapter)f).function());
        }
        
        void removeFunction(Basic1DFunction f) {
            f.deleteObserver(this);
            sum.removeFunction(((AIDAFunctionAdapter)f).function());
            mapParams();
        }
        
        public void update(Observable obs, Object arg) {
            //        System.out.println("Updating "+arg);
            if ( ("remove").equals(arg) )
                removeFunction( (Basic1DFunction) obs);
            // if one of our child functions is updated, then that invalidates any fit we
            // currently have
            mapParams();
            clearFit();
            setChanged();
        }
        
        private void mapParams() {
            int j = 0, i = 0;
            m_nParams = 0;
            
            int nFuncs = 0;
            Enumeration e = jasHist().get1DFunctions();
            while (e.hasMoreElements()) {
                JASHistData data = (JASHistData) e.nextElement();
                Basic1DFunction func = (Basic1DFunction) data.getDataSource();
                if ( func != this && containsFunction(func) )
                    nFuncs++;
            }
            
            m_param = new int[nFuncs];
            
            Enumeration en = jasHist().get1DFunctions();
            while (en.hasMoreElements()) {
                JASHistData data = (JASHistData) en.nextElement();
                Basic1DFunction func = (Basic1DFunction) data.getDataSource();
                if ( containsFunction(func) ) {
                    int n = func.getStatisticNames().length;
                    m_param[j++] = n;
                    m_nParams += n;
                }
            }
            initIncludeParametersInFit(m_nParams);
        }
        
        JASHist jasHist() {
            return h;
        }
        
        private class SumOfFunctionsAdvancedDialog extends JASDialog {
            SumOfFunctionsAdvancedDialog(Frame f) {
                super(f, "Advanced...", true, JASDialog.OK_BUTTON);
                
                int n = 0;
                Enumeration e = SumOfFunctions.this.jasHist().get1DFunctions();
                while (e.hasMoreElements()) {
                    JASHistData data = (JASHistData) e.nextElement();
                    Basic1DFunction func = (Basic1DFunction) data.getDataSource();
                    if ( func != SumOfFunctions.this )
                        n++;
                }

                Container c = getContentPane();
                JCheckBox[] cb = new JCheckBox[n];
                c.setLayout(new GridLayout(n + 1, 1));
                c.add(new JLabel("Select functions to include:"));
                
                Enumeration en = SumOfFunctions.this.jasHist().get1DFunctions();
                int count = 0;
                while (en.hasMoreElements()) {
                    JASHistData data = (JASHistData) en.nextElement();
                    Basic1DFunction func = (Basic1DFunction) data.getDataSource();
                    if ( func != SumOfFunctions.this ) {
                        cb[count] = new JCheckBox(String.valueOf(count + 1) +" "+ func.getTitle(), SumOfFunctions.this.containsFunction(func));
                        cb[count].addActionListener(new EnumeratedJCheckBoxListener(count,func));
                        cb[count].setMnemonic('0' + (char) (count + 1));
                        c.add(cb[count]);
                        count++;
                    }
                }
                pack();
                setResizable(false);
                show();
            }
            
            private class EnumeratedJCheckBoxListener implements ActionListener {
                private int i;
                private Basic1DFunction func;
                
                EnumeratedJCheckBoxListener(int i, Basic1DFunction func) {
                    this.i = i;
                    this.func = func;
                }
                
                public void actionPerformed(ActionEvent e) {
                    if ( ! SumOfFunctions.this.containsFunction(func) )
                        SumOfFunctions.this.addFunction(func);
                    else
                        SumOfFunctions.this.removeFunction(func);
                    mapParams();
                    setChanged();
                    clearFit();
                }
            }
        }
        
        public void openAdvancedDialog(Frame f, JASHist hist) {
            new SumOfFunctionsAdvancedDialog(f);
        }
        private int m_nParams;
        private int[] m_param;
    }
    
}
