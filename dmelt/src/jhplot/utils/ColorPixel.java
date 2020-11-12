package jhplot.utils;

import java.util.*;
import java.awt.*;

import jhplot.P0I;
import jhplot.PNI;

/**
 * Static methods which help to convert colors and vectors with pixels. They are
 * helpful to integrate ImageJ API with ScaVi  API. The methods allows to
 * convert pixel colors (integer) into the usual (R,G,B). Also conversion of
 * arrays is supported.
 * 
 * @author S.Chekanov
 * 
 */

public class ColorPixel {

	/**
	 * Return color from pixel color (integer compact value representing color
	 * in the ImageJ representation).
	 * 
	 * @param c
	 *            pixel color in compact representation
	 * @return java color
	 */
	public static Color getColor(int c) {
		int r = (c & 0xff0000) >> 16;
		int g = (c & 0xff00) >> 8;
		int b = c & 0xff;
		return new Color(r, g, b);
	}

	/**
	 * Get (R,G,B) from a pixel color.
	 * 
	 * @param c
	 *            pixel color
	 * @return (R,G,B) of this pixel.
	 */
	public static int[] getRGB(int c) {
		int[] tmp = new int[3];
		tmp[0] = (c & 0xff0000) >> 16;
		tmp[1] = (c & 0xff00) >> 8;
		tmp[2] = c & 0xff;
		return tmp;
	}

	/**
	 * Get array list with (R,G,B) from list of pixels. The array has dimension
	 * 3 and contains 3 arrays with (R,G,B).
	 * 
	 * @param c
	 *            pixel color
	 * @return array with 3 elements. Each is an array with (R,G,B).
	 */
	public static ArrayList<int[]> getRGB(int[] c) {

		
		
		int[] r = new int[3];
		int[] g = new int[3];
		int[] b = new int[3];
		for (int i = 0; i < c.length; i++) {
			
			r[i] = (c[i] & 0xff0000) >> 16;
			g[i] = (c[i] & 0xff00) >> 8;
			b[i] = c[i] & 0xff;
		
		}
		ArrayList<int[]> tt = new ArrayList<int[]>();
		tt.add(r);
		tt.add(g);
		tt.add(b);
		return tt;
	}

	/**
	 * Get array list with (R,G,B) from list of pixels. The array has size 3,
	 * and each object is P0D keeping R,G,B color.
	 * 
	 * @param c
	 *            pixel color
	 * @return array with 3 elements. Each is an array with (R,G,B).
	 */
	public static ArrayList<P0I> getP0I(int[] c) {

		P0I r = new P0I("red");
		P0I g = new P0I("green");
		P0I b = new P0I("blue");
		for (int i = 0; i < c.length; i++) {
			int rr = (c[i] & 0xff0000) >> 16;
			int gg = (c[i] & 0xff00) >> 8;
			int bb = c[i] & 0xff;
			r.add(rr);
			g.add(gg);
			b.add(bb);
		}

		ArrayList<P0I> tt = new ArrayList<P0I>();
		tt.add(r);
		tt.add(g);
		tt.add(b);
		return tt;
	}

	/**
	 * Get array list with (R,G,B) from list of pixels. The array has size 3,
	 * and each object is P0D keeping R,G,B color.
	 * 
	 * @param c
	 *            pixel color
	 * @return array with 3 elements. Each is an array with (R,G,B).
	 */
	public static PNI getPNI(int[] c) {

		PNI r = new PNI("colors");

		for (int i = 0; i < c.length; i++) {

			int[] tmp = new int[3];
			tmp[0] = (c[i] & 0xff0000) >> 16;
			tmp[1] = (c[i] & 0xff00) >> 8;
			tmp[2] = c[i] & 0xff;
			r.add(tmp);

		}

		return r;
	}

	/**
	 * Convert color into compact integer representation of pixel.
	 * 
	 * @param c
	 *            color
	 * @return integer representing a pixel.
	 */
	public static int getPixel(Color c) {
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();
		int pix = ((red & 0xff) << 16) + ((green & 0xff) << 8) + (blue & 0xff);
		return pix;
	}

	/**
	 * Convert color (red,green,blue) into compact integer representation of
	 * pixel.
	 * 
	 * @param red
	 *            Red
	 * @param green
	 *            Green
	 * @param blue
	 *            Blue
	 * @return integer representing a pixel.
	 */
	public static int getPixel(int red, int green, int blue) {

		int pix = ((red & 0xff) << 16) + ((green & 0xff) << 8) + (blue & 0xff);
		return pix;
	}

	/**
	 * Convert arrays with R,G,B into a pixel color as in ImageJ. All arrays
	 * should have the same length.
	 * 
	 * @param red
	 *            array with red (0-255)
	 * @param green
	 *            array with green
	 * @param blue
	 *            array with blue
	 * @return pixel array color in compact form.
	 */
	public static int[] getPixel(int[] red, int[] green, int[] blue) {

		int[] tmp = new int[red.length];
		for (int i = 0; i < red.length; i++) {

			int r = red[i];
			int g = green[i];
			int b = blue[i];
			tmp[i] = ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);

		}
		return tmp;
	}

}
