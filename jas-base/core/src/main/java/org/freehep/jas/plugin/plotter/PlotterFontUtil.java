package org.freehep.jas.plugin.plotter;

import java.awt.Font;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class PlotterFontUtil {
    
    public static Font getFont( String fontName, String style, String size ) {
        float fontSize = Float.parseFloat(size);
        int fontStyle = Integer.parseInt(style);
        Font font = new Font(fontName, fontStyle, 10);
        return font.deriveFont(fontSize);
    }

    public static Font getFont( String fontName, String isItalic, String isBold, String size ) {
        boolean italic  = Boolean.valueOf(isItalic).booleanValue();
        boolean bold    = Boolean.valueOf(isBold).booleanValue();
        int style = Font.PLAIN;
        if ( italic && bold ) style = Font.ITALIC|Font.BOLD;
        else if ( italic ) style = Font.ITALIC;
        else if ( bold ) style = Font.BOLD;
        return getFont( fontName,  String.valueOf(style), size );
    }

    
    
}
