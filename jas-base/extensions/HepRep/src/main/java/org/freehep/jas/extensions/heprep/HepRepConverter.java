package org.freehep.jas.extensions.heprep;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HasHepRep;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepProvider;
import hep.graphics.heprep.HepRepConversionException;
import hep.graphics.heprep.ref.DefaultHepRepAttValue;
import hep.graphics.heprep.util.HepRepUtil;
import hep.graphics.heprep1.adapter.NumericalComparator;
import java.util.*;
import org.freehep.application.Application;
import org.freehep.application.studio.Studio;

/**
 * Converts HepRep, HasHepRep and HepRep1.
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepConverter.java 14555 2013-04-19 22:53:58Z onoprien $
 */ 
class HepRepConverter implements HepRepProvider, /* for compatibility reasons */ hep.graphics.heprep.HepRepConverter {

    private static boolean converterNotFoundSignalled = false;

    public boolean canConvert(Object object) {
        return (object instanceof HepRep) || 
               (object instanceof HasHepRep) ||
               (object instanceof hep.graphics.heprep1.HepRep);
    }

    /* for compatibility reasons */
    public boolean canHandle(Class cls) {
        return HepRep.class.isAssignableFrom(cls) ||
               HasHepRep.class.isAssignableFrom(cls) ||
               hep.graphics.heprep1.HepRep.class.isAssignableFrom(cls);
    }
    
    public HepRep convert(Object object) {
        // is a HepRep
        if (object instanceof HepRep) return (HepRep)object;
        
        // has a HepRep
        if (object instanceof HasHepRep) return ((HasHepRep)object).getHepRep();
        
        // is a HepRep Version 1
        if (object instanceof hep.graphics.heprep1.HepRep) {
            hep.graphics.heprep1.adapter.HepRepAdapterFactory factory = hep.graphics.heprep1.adapter.HepRepAdapterFactory.getFactory();
            HepRep heprep = factory.createHepRep();
            List layers = new ArrayList();

            // get event
            hep.graphics.heprep1.HepRep heprep1 = (hep.graphics.heprep1.HepRep)object;

            // check if this is a babar file, by looking for type HepRep1/DchHitUnassigned
            boolean babar = false;
            for (Enumeration e = heprep1.getTypes(); e.hasMoreElements(); ) {
                hep.graphics.heprep1.HepRepType type = (hep.graphics.heprep1.HepRepType)e.nextElement();
                if (type.getName().equals("DchHitUnassigned")) {
                    babar = true;
                    break;
                }
            }

            DefaultHepRepAttValue.addGuessedType("ViewScale", "double");
            DefaultHepRepAttValue.addGuessedType("Generator", "String");
            heprep1.addDefinition("ViewScale", "Scale of initial suggested viewpoint", "Draw", "");
            heprep1.addDefinition("Generator", "Generator of the file", "General", "");
            heprep1.addValue("ViewScale", 0.01, hep.graphics.heprep1.HepRepAttValue.SHOW_NONE);
            heprep1.addValue("Generator", "HepRepPlugin - HepRep1-HepRep2 Adapter");
                                   
            if (babar) {
                // Add BaBar specifics             
                // alphabetical
                DefaultHepRepAttValue.addGuessedType("BestH", "int");
                DefaultHepRepAttValue.addGuessedType("BestHStatus", "int");
                DefaultHepRepAttValue.addGuessedType("charge", "double");
                DefaultHepRepAttValue.addGuessedType("decayTree", "String");
                DefaultHepRepAttValue.addGuessedType("drift", "double");
                DefaultHepRepAttValue.addGuessedType("d0", "double");
                DefaultHepRepAttValue.addGuessedType("E", "double");
                DefaultHepRepAttValue.addGuessedType("entrBar", "int");
                DefaultHepRepAttValue.addGuessedType("exitBar", "int");
                DefaultHepRepAttValue.addGuessedType("hypo", "int");
                DefaultHepRepAttValue.addGuessedType("lay", "int");
                DefaultHepRepAttValue.addGuessedType("mom", "double");
                DefaultHepRepAttValue.addGuessedType("nAssoc", "int");
                DefaultHepRepAttValue.addGuessedType("nDch", "int");
                DefaultHepRepAttValue.addGuessedType("nexp", "double");
                DefaultHepRepAttValue.addGuessedType("nSvt", "int");
                DefaultHepRepAttValue.addGuessedType("P", "double");
                DefaultHepRepAttValue.addGuessedType("path", "String");
                DefaultHepRepAttValue.addGuessedType("phi0", "double");
                DefaultHepRepAttValue.addGuessedType("phi(CM)", "double");
                DefaultHepRepAttValue.addGuessedType("phi(lab)", "double");
                DefaultHepRepAttValue.addGuessedType("PID", "String");
                DefaultHepRepAttValue.addGuessedType("PT", "double");
                DefaultHepRepAttValue.addGuessedType("pulse", "double");
                DefaultHepRepAttValue.addGuessedType("p_T", "double");
                DefaultHepRepAttValue.addGuessedType("p(CM)", "double");
                DefaultHepRepAttValue.addGuessedType("p(lab)", "double");
                DefaultHepRepAttValue.addGuessedType("sector", "int");
                DefaultHepRepAttValue.addGuessedType("tanDip", "double");
                DefaultHepRepAttValue.addGuessedType("theta(CM)", "double");
                DefaultHepRepAttValue.addGuessedType("theta(lab)", "double");
                DefaultHepRepAttValue.addGuessedType("time", "double");
                DefaultHepRepAttValue.addGuessedType("thCelErr", "double");
                DefaultHepRepAttValue.addGuessedType("thCelHypo", "double");
                DefaultHepRepAttValue.addGuessedType("thCkaHypo", "double");
                DefaultHepRepAttValue.addGuessedType("thCMeas", "double");
                DefaultHepRepAttValue.addGuessedType("thCmuHypo", "double");
                DefaultHepRepAttValue.addGuessedType("thCpiHypo", "double");
                DefaultHepRepAttValue.addGuessedType("thCprHypo", "double");
                DefaultHepRepAttValue.addGuessedType("trkElectronID", "String");
                DefaultHepRepAttValue.addGuessedType("trkKaonID", "String");
                DefaultHepRepAttValue.addGuessedType("trkMuonID", "String");
                DefaultHepRepAttValue.addGuessedType("trkPionID", "String");
                DefaultHepRepAttValue.addGuessedType("trkProtonID", "String");
                DefaultHepRepAttValue.addGuessedType("truthName", "String");
                DefaultHepRepAttValue.addGuessedType("UID", "int");
                DefaultHepRepAttValue.addGuessedType("view", "int");
                DefaultHepRepAttValue.addGuessedType("wire", "int");
                DefaultHepRepAttValue.addGuessedType("z0", "double");
                
                // three vectors...
                DefaultHepRepAttValue.addGuessedType("pickRotation", "String");
                DefaultHepRepAttValue.addGuessedType("pickScale", "String");
                DefaultHepRepAttValue.addGuessedType("pickTranslation", "String");

            }            

            HepRep geometry = null;
            if (babar) {
                String babarGeometry = "/babargeometry/babar.xml.gz";
                HepRepProvider converter = HepRepUtil.getHepRepProvider(((Studio)Application.getApplication()).getLookup(), babarGeometry);
                if (converter != null) {
                    try {
                        geometry = converter.convert(babarGeometry);
                        heprep.addTypeTree(geometry.getTypeTree("GeometryTypes", "1.0"));
                        heprep.addInstanceTree(geometry.getInstanceTreeTop("BaBarGeometry", "1.0"));
                        layers.addAll(geometry.getLayerOrder());
                        
                    } catch (HepRepConversionException e) {
                        Application.getApplication().error(babarGeometry+" conversion failed", e);
                    }
                } else {
                    if (!converterNotFoundSignalled) {
                        converterNotFoundSignalled = true;
                        Application.getApplication().error(babarGeometry+" converter not found: geometry will not be shown.");
                    }
                }
            }

            // convert event
            HepRep event = factory.createHepRep(heprep1);
            
            heprep.addTypeTree(event.getTypeTree("Types", "1.0"));        
    
            HepRepInstanceTree instanceTree = event.getInstanceTreeTop("Instances", "1.0");
            heprep.addInstanceTree(instanceTree);
            layers.addAll(event.getLayerOrder());

            if (geometry != null) {
                instanceTree.addInstanceTree(factory.createHepRepTreeID("BaBarGeometry", "1.0"));
            }
    
            // merge layers
            Collections.sort(layers, new NumericalComparator());
            for (Iterator i=layers.iterator(); i.hasNext(); ) {
                heprep.addLayer((String)i.next());
            }
            
            return heprep;
        }
        
        return null;
    }

}        
