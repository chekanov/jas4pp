package org.freehep.jas.extension.aida.function;

import hep.aida.ref.plotter.adapter.AIDAFunctionAdapter;
import jas.hist.FunctionFactory;
import jas.hist.JASHist;
import jas.hist.JASHistData;
import jas.util.JASIcon;
import java.util.Enumeration;
import javax.swing.Icon;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
abstract class AbstractFunctionFactory implements FunctionFactory {
    
    private String functionName;
    private Icon icon = JASIcon.create(this,"function.gif");

    AbstractFunctionFactory( String functionName ) {
        this.functionName = functionName;
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public Icon getFunctionIcon() {
        return icon;
    }
    
    void chooseName( AIDAFunctionAdapter f, JASHist hist ) {
        String name = f.getTitle();
        boolean changeName = false;
        Enumeration e = hist.get1DFunctions();
        int count = 0;
        while ( e.hasMoreElements() ) {
            JASHistData data = (JASHistData) e.nextElement();
            String n = ( (AIDAFunctionAdapter) data.getDataSource() ).getTitle();
            if ( name.equals(n) )
                changeName = true;
            String tmpName = name+"_";
            int index = n.indexOf(tmpName);
            if ( index != -1 ) {
                try {
                    int c = Integer.valueOf( n.substring(tmpName.length()) ).intValue();
                    if ( count < c )
                        count = c;
                } catch ( NumberFormatException nfe ) {}
            }
        }
        if ( changeName ) {
            count++;
            f.setTitle( name+"_"+count );
        }
    }
    
}
