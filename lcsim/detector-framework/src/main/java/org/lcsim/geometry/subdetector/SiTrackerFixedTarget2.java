package org.lcsim.geometry.subdetector;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepFactory;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.lcsim.detector.converter.heprep.DetectorElementToHepRepConverter;

public class SiTrackerFixedTarget2 extends AbstractTracker
{
    SiTrackerFixedTarget2( Element node ) throws JDOMException
    {
        super( node );
    }

    public void appendHepRep( HepRepFactory factory, HepRep heprep )
    {
        DetectorElementToHepRepConverter.convert( getDetectorElement(), factory, heprep, 4, false, getVisAttributes().getColor() );
    }

    public boolean isEndcap()
    {
        return false;
    }

    public boolean isBarrel()
    {
        return true;
    }

}