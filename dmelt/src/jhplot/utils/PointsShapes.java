// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://jwork.org/scavis/ 
// * Copyright (c) 2005 by S.Chekanov 
// * All rights reserved.


package jhplot.utils;


import java.awt.*;
import java.math.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;


/**
*
**/
public class PointsShapes {
	/**
	 * Draws a point-type corresponding to a symbolic constant, yielding values
	 * from 0 to <a href="GraphSettings.html#NO_SYMBOL">NO_SYMBOL</a> (==13).
	 * 
	 * @param index
	 *            symbolic constant
	 * @param x
	 *            x-coordinate of the midpoint of the point
	 * @param y
	 *            y-coordinate of the midpoint of the point
	 * @param ps
	 *            size of the point in pixels
	 */
	public static Shape drawPointType(int index, Color c,  double _x, double _y,
			float ps) {

		
            	if (ps <= 0.0f) return null; 
                Color cold = c;
 
              
		float x = (float) (_x) - ps / 2.0f;
		float y = (float) (_y) - ps / 2.0f;
		
		/*
		if (index == 0) { 
			return new Ellipse2D.Float(x, y, ps, ps); 
		} else if (index == 1) { 
		    return new Rectangle2D.Float(x, y, ps, ps); 
		} else if (index == 2) {
			GeneralPath gp = new GeneralPath();
			gp.moveTo(x, y + ps / 2.0f);
			gp.lineTo(x + ps / 2.0f, y + ps);
			gp.lineTo(x + ps, y + ps / 2.0f);
			gp.lineTo(x + ps / 2.0f, y);
			gp.lineTo(x, y + ps / 2.0f);
			return gp; 
		} else if (index == 3) {
			GeneralPath gp = new GeneralPath();
			gp.moveTo(x, y + ps);
			gp.lineTo(x + ps, y + ps);
			gp.lineTo(x + ps / 2.0f, y);
			gp.lineTo(x, y + ps);
			return gp;
                      
		}
		else if (index == 4) {
		    return new Ellipse2D.Float(x, y, ps, ps);
		} else if (index == 5) {
			return new Rectangle2D.Float(x, y, ps, ps);
		} else if (index == 6) {
			GeneralPath gp = new GeneralPath();
			gp.moveTo(x, y + ps / 2.0f);
			gp.lineTo(x + ps / 2.0f, y + ps);
			gp.lineTo(x + ps, y + ps / 2.0f);
			gp.lineTo(x + ps / 2.0f, y);
			gp.lineTo(x, y + ps / 2.0f);
			return gp;
		} else if (index == 7) {
			GeneralPath gp = new GeneralPath();
			gp.moveTo(x, y + ps);
			gp.lineTo(x + ps, y + ps);
			gp.lineTo(x + ps / 2.0f, y);
			gp.lineTo(x, y + ps);
	
			return gp;
                      
		} else if (index == 8) {
			GeneralPath gp = new GeneralPath();
			gp.append(new Line2D.Float(x, y + ps / 2.0f, x + ps, y + ps / 2.0f),true);
			gp.append(new Line2D.Float(x + ps / 2.0f, y, x + ps / 2.0f, y + ps),true);
			return gp;
			
		} else if (index == 9) {
			GeneralPath gp = new GeneralPath();
			gp.append(new Line2D.Float(x, y, x + ps, y + ps),true);
			gp.append(new Line2D.Float(x + ps, y, x, y + ps),true);
			return gp;
			
		} else if (index == 10) {
			GeneralPath gp = new GeneralPath();
			
			gp.append(new Line2D.Float(x, y + ps / 2.0f, x + ps, y + ps / 2.0f),true);
			gp.append(new Line2D.Float(x + ps / 2.0f, y, x + ps / 2.0f, y + ps),true);
			gp.append(new Line2D.Float(x, y, x + ps, y + ps),true);
			gp.append(new Line2D.Float(x + ps, y, x, y + ps),true);
            return gp;
		} else if (index == 11) {
			GeneralPath gp = new GeneralPath();
                         ps=2.0f;  
                         x = (float) (_x) - ps;
                         y = (float) (_y) - ps;
                         gp.append(new Ellipse2D.Float(x, y, ps, ps),true);
                         return gp;
		} else if (index == 12) {
			g2.draw(new Line2D.Float(x, y + ps / 3.0f, x + ps, y + ps / 3.0f));
			g2.draw(new Line2D.Float(x, y + 2.0f * ps / 3.0f, x + ps, y + 2.0f
					* ps / 3.0f));
			g2.draw(new Line2D.Float(x + ps / 3.0f, y, x + ps / 3.0f, y + ps));
			g2.draw(new Line2D.Float(x + 2.0f * ps / 3.0f, y, x + 2.0f * ps
					/ 3.0f, y + ps));
                        return;
                } else if (index == 20)  {
                        g2.draw(new Line2D.Float(x-1.0f*ps, y+0.5f*ps, x+0.5f*ps, y+0.5f*ps));
                        return;
                  // non-filled
                } else if (index == 21)  {
                        g2.draw(new Rectangle2D.Float(x-1.0f*ps, y, 2*ps, ps));
                        return;
                } 
                else if (index == 22)  {
                        g2.fill(new Rectangle2D.Float(x-1.0f*ps, y, 2*ps, ps));
                        return;
                } else if (index == 23)  {
                        g2.fill(new Rectangle2D.Float(x-1.0f*ps, y, 2*ps, ps)); 
                        g2.setColor(Color.black);
                        g2.draw(new Rectangle2D.Float(x-1.0f*ps, y, 2*ps, ps));
                        g2.setColor(cold); 
                }

     
*/
                return new Ellipse2D.Float(x, y, ps, ps); 
	}

}
