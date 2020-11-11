package org.freehep.jas.extension.aida.fitter;

import hep.aida.IFitter;
import hep.aida.ref.fitter.FitFactory;
import org.freehep.jas.extension.aida.fitter.FitterAdapter;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class JAS3FitterFactory extends FitFactory {
    
    public IFitter createFitter() throws java.lang.IllegalArgumentException {
        return createFitter(null);
    }
    
    public IFitter createFitter(String fitterType) throws java.lang.IllegalArgumentException {
        return createFitter(fitterType,null);
    }
    
    public IFitter createFitter(String fitterType, String engineType) throws java.lang.IllegalArgumentException {
        return createFitter(fitterType, engineType, null);
    }
    
    public IFitter createFitter(String fitterType, String engineType, String options) throws java.lang.IllegalArgumentException {
        IFitter fitter = super.createFitter(fitterType,engineType,options);
        return new FitterAdapter(fitter);
    }
}
