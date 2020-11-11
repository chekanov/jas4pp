// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * EMF Panose
 * 
 * @author Mark Donszelmann
 * @version $Id: freehep-graphicsio-emf/src/main/java/org/freehep/graphicsio/emf/gdi/Panose.java c0f15e7696d3 2007/01/22 19:26:48 duns $
 */
public class Panose implements EMFConstants {

    private int familyType;

    private int serifStyle;

    private int weight;

    private int proportion;

    private int contrast;

    private int strokeVariation;

    private int armStyle;

    private int letterForm;

    private int midLine;

    private int xHeight;

    public Panose() {
        // FIXME, fixed
        this.familyType = PAN_NO_FIT;
        this.serifStyle = PAN_NO_FIT;
        this.proportion = PAN_NO_FIT;
        this.weight = PAN_NO_FIT;
        this.contrast = PAN_NO_FIT;
        this.strokeVariation = PAN_NO_FIT;
        this.armStyle = PAN_ANY;
        this.letterForm = PAN_ANY;
        this.midLine = PAN_ANY;
        this.xHeight = PAN_ANY;
    }

    public Panose(EMFInputStream emf) throws IOException {
        familyType = emf.readBYTE();
        serifStyle = emf.readBYTE();
        proportion = emf.readBYTE();
        weight = emf.readBYTE();
        contrast = emf.readBYTE();
        strokeVariation = emf.readBYTE();
        armStyle = emf.readBYTE();
        letterForm = emf.readBYTE();
        midLine = emf.readBYTE();
        xHeight = emf.readBYTE();
    }

    public void write(EMFOutputStream emf) throws IOException {
        emf.writeBYTE(familyType);
        emf.writeBYTE(serifStyle);
        emf.writeBYTE(weight);
        emf.writeBYTE(proportion);
        emf.writeBYTE(contrast);
        emf.writeBYTE(strokeVariation);
        emf.writeBYTE(armStyle);
        emf.writeBYTE(letterForm);
        emf.writeBYTE(midLine);
        emf.writeBYTE(xHeight);
    }

    public String toString() {
        return "  Panose\n" + "    familytype: " + familyType +
            "\n    serifStyle: " + serifStyle +
            "\n    weight: " + weight +
            "\n    proportion: " + proportion +
            "\n    contrast: " + contrast +
            "\n    strokeVariation: " + strokeVariation +
            "\n    armStyle: " + armStyle +
            "\n    letterForm: " + letterForm +
            "\n    midLine: " + midLine +
            "\n    xHeight: " + xHeight;
    }
}
