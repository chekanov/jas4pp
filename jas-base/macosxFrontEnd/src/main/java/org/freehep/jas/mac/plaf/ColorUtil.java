/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.freehep.jas.mac.plaf;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;

/** Simple utility class for easier obtaining of colors. Saves clients from
 * having to check values coming from Swing UIManager for null.
 *
 * @author Dafe Simonek
 */
public final class ColorUtil {
    private static Map gpCache = null;    
    private static HashMap hintsMap = null;
    private static final boolean noGpCache = Boolean.getBoolean("netbeans.winsys.nogpcache");
    
    /** Utility class, no instances should be created. */
    private ColorUtil() {
    }

    /** constants for hardcoded colors */
    /*public static final Integer TAB_BOTTOM_BORDER = new Integer(1);
    public static final Integer TAB_FOCUSED_FILL = new Integer(2);
    public static final Integer TAB_UNSELECTED_FILL = new Integer(3);
    public static final Integer TAB_BORDER = new Integer(4);
    public static final Integer WORKPLACE_FILL = new Integer(5);*/
    /** mapping from color keys to actual color objects */
    //private static Map colorMap;
    
    /** Retrieves color of specified key from Swing ui manager tables. If this
     * call is not succesfull (i.e. returns null), then return backingColor
     * passed as parameter.
     * @return Color of specified key or color passed as parameter.
     */
    public static Color get (String key, Color backingColor) {
        Color c = UIManager.getColor(key);
        return c == null ? backingColor : c;
    }
    
    /** Retrieves color of specified key from Swing ui manager tables. If this
     * call is not succesfull (i.e. returns null), then return color created
     * from input rgb values.
     * @return Color of specified key or color from input rgb values.
     */
    public static Color get (String key, int r, int g, int b) {
        Color c = UIManager.getColor(key);
        return c == null ? new Color(r, g, b) : c;
    }
    
    /** Retrieves color of specified key from Swing ui manager tables.
     * Throws IllegalArgumentException if color can't be found in tables.
     * @return Color of specified color key.
     */
    public static Color get (String key) {
        Color c = UIManager.getColor(key);
        if (c == null) {
            throw new IllegalArgumentException("Unknown or not installed color key " + key);
        }
        return c;
    }
    
    /** Computes "middle" color in terms of rgb color space. Ignores alpha
     * (transparency) channel
     */
    public static Color getMiddle (Color c1, Color c2) {
        return new Color((c1.getRed() + c2.getRed()) / 2,
                        (c1.getGreen() + c2.getGreen()) / 2,
                        (c1.getBlue() + c2.getBlue()) / 2);
    }
    
    /** Retrieves color of specified integer key.
     */
    /*public static Color get (Integer key) {
        if (colorMap == null) {
            // initialize color map with hardcoded colors
            // TBD - this should change to dynamic, not hardocded colors as soon
            // as we will find a way to retrieve XP system colors from Swing
            colorMap = new HashMap();
            colorMap.put(TAB_BOTTOM_BORDER, new Color(127, 157, 185));
            colorMap.put(TAB_FOCUSED_FILL, new Color(215, 228, 252));
            colorMap.put(TAB_UNSELECTED_FILL, new Color(248, 248, 243));
            colorMap.put(TAB_BORDER, new Color(145, 167, 180));
            colorMap.put(WORKPLACE_FILL, new Color(226, 223, 214));
        }
        
        Color result = (Color)colorMap.get(key);
        if (result == null) {
            throw new IllegalArgumentException("No color mapped to key: " + key);
        }
        return result;
    }*/
    
    public static GradientPaint getGradientPaint (float x1, float y1, Color upper, float x2, float y2, Color lower) {
        return getGradientPaint (x1, y1, upper, x2, y2, lower, false);
    }
    
    /** GradientPaint creation is somewhat expensive.  This method keeps cached
     * GradientPaint instances, and normalizes the resulting GradientPaint for
     * horizontal and vertical cases */
    public static GradientPaint getGradientPaint (float x1, float y1, Color upper, float x2, float y2, Color lower, boolean repeats) {
        if (noGpCache) {
            return new GradientPaint(x1, y1, upper, x2, y2, lower, repeats);
        }
        if (gpCache == null) {
            gpCache = new HashMap(20);
        }
        //Normalize any non-repeating gradients
        boolean horizontal = x1 == x2;
        boolean vertical = y1 == y2;
        if (horizontal && vertical) {
            y1 = 28; //Hack: gradient paint w/ 2 matching points causes endless loop in native code on OS-X
        } else if (horizontal && !repeats) {
            x1 = 0;
            x2 = 0;
        } else if (vertical && !repeats) {
            y1 = 0;
            y2 = 0;
        }
        //TODO: Normalize non-planar repeating gp's by vector/relative location
        
        //Generate a hash code for looking up an existing paint
	long bits = Double.doubleToLongBits(x1)
                    + Double.doubleToLongBits(y1) * 37
                    + Double.doubleToLongBits(x2) * 43
                    + Double.doubleToLongBits(y2) * 47;
	int hash = (((int) bits) ^ ((int) (bits >> 32))) ^ 
            upper.hashCode() ^ lower.hashCode() * (repeats ? 31 : 1);
        
        Object key = new Integer(hash);
        GradientPaint result = (GradientPaint) gpCache.get(key);
        if (result == null) {
            result = new GradientPaint (x1, y1, upper, x2, y2, lower, repeats);
            if (gpCache.size() > 40) {
                gpCache.clear();
            }
            gpCache.put(key, result);
        }
        return result;
    }
    
    public static final Map getHints() {
        if (hintsMap == null) {
            hintsMap = new HashMap();
            hintsMap.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            hintsMap.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            hintsMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        return hintsMap;
    }
    
    public static final void configureRenderingHints (Graphics g) {
        ((Graphics2D)g).addRenderingHints(getHints());
    }
    
    //**************Some static utility methods for color manipulation**********
    
    public static boolean isBrighter(Color a, Color b) {
        int[] ac = new int[]{a.getRed(), a.getGreen(), a.getBlue()};
        int[] bc = new int[]{b.getRed(), b.getGreen(), b.getBlue()};
        int dif=0;
        
        for (int i=0; i < 3; i++) {
            int currDif = ac[i] - bc[i];
            if (Math.abs(currDif) > Math.abs(dif)) {
                dif = currDif;
            }
        }
        return dif > 0;
    }
    
    private static int minMax(int i) {
        if (i < 0) {
            return 0;
        } else if (i > 255) {
            return 255;
        } else {
            return i;
        }
    }
    
    public static int averageDifference(Color a, Color b) {
        int[] ac = new int[]{a.getRed(), a.getGreen(), a.getBlue()};
        int[] bc = new int[]{b.getRed(), b.getGreen(), b.getBlue()};
        int dif=0;
        for (int i=0; i < 3; i++) {
            dif += bc[i] - ac[i];
        }
        return dif / 3;
    }
    
    public static Color adjustComponentsTowards(Color toAdjust, Color towards) {
        int r = toAdjust.getRed();
        int g = toAdjust.getGreen();
        int b = toAdjust.getBlue();

        int ra = towards.getRed();
        int ga = towards.getGreen();
        int ba = towards.getBlue();
        
        r += minMax((ra - r) /3);
        g += minMax((ga - g) /3);
        b += minMax((ba - b) /3);
        
        return new Color(r,g,b);
    }    
    
    public static Color adjustTowards(Color toAdjust, int amount, Color towards) {
        int r = toAdjust.getRed();
        int g = toAdjust.getGreen();
        int b = toAdjust.getBlue();
        int factor = isBrighter(towards, toAdjust) ? 1 : -1;
        r = minMax(r + (factor * amount));
        g = minMax(g + (factor * amount));
        b = minMax(b + (factor * amount));
        return new Color(r,g,b);
    }
    
    public static Color adjustBy (Color toAdjust, int amount) {
        int r = minMax(toAdjust.getRed() + amount);
        int g = minMax(toAdjust.getGreen() + amount);
        int b = minMax(toAdjust.getBlue() + amount);
        return new Color(r,g,b);
    }
    
    public static Color adjustBy (Color toAdjust, int[] amounts) {
        int r = minMax(toAdjust.getRed() + amounts[0]);
        int g = minMax(toAdjust.getGreen() + amounts[1]);
        int b = minMax(toAdjust.getBlue() + amounts[2]);
        return new Color(r,g,b);
    }
    

    /** Takes a base color (for example, the standard Windows window title color),
     * and the color that the spec defines (which is somehow related but not the same),
     * and the actual color gotten from the system (which may be different because of
     * OS theme or whatever), and returns a color that expresses a complementary
     * relationship to the actual color passed in */
    public static Color adjustRelative (Color expectedSource, Color expectedTarget, Color actualSource) {
        if (expectedSource.equals(actualSource)) {
            return expectedTarget;
        }
        if (expectedSource.equals(expectedTarget)) {
            return actualSource;
        }
        float[] baseHSB = Color.RGBtoHSB(expectedSource.getRed(), expectedSource.getGreen(), expectedSource.getBlue(), null);
        float[] targHSB = Color.RGBtoHSB(expectedTarget.getRed(), expectedTarget.getGreen(), expectedTarget.getBlue(), null);
        float[] realHSB = Color.RGBtoHSB(actualSource.getRed(), actualSource.getGreen(), actualSource.getBlue(), null);
        float[] resultHSB = new float[3];
        
        
        resultHSB[0] = minMax(realHSB[0] + (targHSB[0] - baseHSB[0]));
        resultHSB[1] = minMax(realHSB[1] + (targHSB[1] - baseHSB[1]));
        resultHSB[2] = minMax(realHSB[2] + (targHSB[2] - baseHSB[2]));
        
        if (resultHSB[1] == 0f) {
            resultHSB[1] = targHSB[1];
        }
        
        Color result = new Color (Color.HSBtoRGB(resultHSB[0], resultHSB[1], resultHSB[2]));
        return result;
    }
    
    /** Rotates a float value around 0-1 */
    private static float minMax (float f) {
        return Math.max(0, Math.min(1, f));
    }    
}
