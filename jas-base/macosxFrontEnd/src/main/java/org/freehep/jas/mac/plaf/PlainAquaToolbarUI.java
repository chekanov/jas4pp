/*
 * PlainAquaToolbarUI.java
 *
 * Created on January 17, 2004, 3:00 AM
 */

package org.freehep.jas.mac.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;
//import org.netbeans.core.windows.view.ui.tabcontrol.ColorUtil;

/** A ToolbarUI subclass that gets rid of all borders
 * on buttons and provides a finder-style toolbar look.
 * 
 * @author  Tim Boudreau
 */
public class PlainAquaToolbarUI extends BasicToolBarUI implements ContainerListener {
    private static final AquaTbBorder aquaborder = new AquaTbBorder();
    
    private static final Color UPPER_GRADIENT_TOP = new Color(255,255,255);
//    private static final Color UPPER_GRADIENT_BOTTOM = Color.WHITE;
//    private static final Color UPPER_GRADIENT_BOTTOM = Color.ORANGE;
    private static final Color UPPER_GRADIENT_BOTTOM = new Color (228,230,232);
    
    private static final Color LOWER_GRADIENT_TOP = new Color(228,227,215);
    private static final Color LOWER_GRADIENT_BOTTOM = new Color(249,249,249);
        
    
    /** Creates a new instance of PlainAquaToolbarUI */
    public PlainAquaToolbarUI() {
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new PlainAquaToolbarUI();
    }
    
    public void installUI( JComponent c ) {
        super.installUI(c);
        c.setBorder(aquaborder);
        c.setOpaque(true);
        c.addContainerListener(this);
        installButtonUIs (c);
        if (c instanceof JToolBar)
        {
           JToolBar bar = (JToolBar) c;
           bar.putClientProperty("wasFloating",Boolean.valueOf(bar.isFloatable()));
           bar.setFloatable(false);
        }
    }
    
    public void uninstallUI (JComponent c) {
        super.uninstallUI (c);
        c.setBorder (null);
        c.removeContainerListener(this);
        if (c instanceof JToolBar)
        {
           JToolBar bar = (JToolBar) c;
           Boolean b = (Boolean) bar.getClientProperty("wasFloating");
           if (b != null) bar.setFloatable(b.booleanValue());
        }
    }
    
    public void paint(Graphics g, JComponent c) {
        ColorUtil.configureRenderingHints(g);
        Color temp = g.getColor();
        Dimension size = c.getSize();
        
        Shape s = aquaborder.getInteriorShape(size.width, size.height);
        Shape clip = g.getClip();
        if (clip != null) {
            Area a = new Area(clip);
            a.intersect(new Area(s));
            g.setClip (a);
        } else {
            g.setClip(s);
        }
        
        Graphics2D g2d = (Graphics2D) g;
        //g.setColor (Color.ORANGE);

        g2d.setPaint (aquaborder.getUpperPaint(size.width,size.height));
        g2d.fill (aquaborder.getUpperBevelShape(size.width, size.height));
        g2d.setPaint (aquaborder.getLowerPaint(size.width,size.height));
        g2d.fill (aquaborder.getLowerBevelShape(size.width, size.height));
        
        g.setClip (clip);
        g.setColor(temp);
    }
    
    
    protected Border createRolloverBorder() {
        return BorderFactory.createEmptyBorder(2,2,2,2);
    }
    
    protected Border createNonRolloverBorder() {
        return createRolloverBorder();
    }
    
    private Border createNonRolloverToggleBorder() {
        return createRolloverBorder();
    }
    
    protected void setBorderToRollover(Component c) {
        if (c instanceof AbstractButton) {
            ((AbstractButton) c).setBorderPainted(false);
            ((AbstractButton) c).setBorder(BorderFactory.createEmptyBorder());
            ((AbstractButton) c).setContentAreaFilled(false);
            ((AbstractButton) c).setOpaque(false);
        }
        if (c instanceof JComponent) {
            ((JComponent) c).setOpaque(false);
        }
    }
    
    protected void setBorderToNormal(Component c) {
        if (c instanceof AbstractButton) {
            ((AbstractButton) c).setBorderPainted(false);
            ((AbstractButton) c).setContentAreaFilled(false);
            ((AbstractButton) c).setOpaque(false);
        }
        if (c instanceof JComponent) {
            ((JComponent) c).setOpaque(false);
        }
    }
    
    public void setFloating(boolean b, Point p) {
        //nobody wants this
    }
    
    private void installButtonUI (Component c) {
        if (c instanceof AbstractButton) {
            ((AbstractButton) c).setUI(buttonui);
        }
        if (c instanceof JComponent) {
            ((JComponent) c).setOpaque(false);
        }
    }
    
    private void installButtonUIs (Container parent) {
        Component[] c = parent.getComponents();
        for (int i=0; i < c.length; i++) {
            installButtonUI(c[i]);
        }
    }
    
    private static final ButtonUI buttonui = new AquaToolBarButtonUI();
    public void componentAdded(ContainerEvent e) {
        installButtonUI (e.getChild());
    }
    
    public void componentRemoved(ContainerEvent e) {
        //do nothing
    }

    private static boolean isFinderLook (Component c) {
        if (c instanceof JComponent) {
            return Boolean.TRUE.equals (((JComponent) c).getClientProperty("finderLook"));
        }
        return false;
    }
    
    
    private static class AquaTbBorder implements Border {

        int arcsize = 13;
        
        public Insets getBorderInsets(Component c) {
            return new Insets (2,4,0,0);
        }
        
        public boolean isBorderOpaque() {
            return true;
        }
        
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            ColorUtil.configureRenderingHints(g);
            y+=2; //leave a gap at the top
            h-=2; //leave a gap at the top
            
            boolean finderLook = isFinderLook (c);
            Color col;
            if (finderLook) {
                col = mezi(UIManager.getColor("controlShadow"), 
                    UIManager.getColor("control")); //NOI18N
            } else {
                col = UIManager.getColor("controlShadow");
            }
            
            g.setColor(col);
            
            int ytop = y;
            
            drawUpper (g, x, y, ytop, w, h);

            g.setColor (mezi (col, UIManager.getColor("control"))); //NOI18N
            if (finderLook) {
                drawUpper (g, x+1, y, ytop+1, w-2, h-1);
            }
            
            if (finderLook) {
                col = mezi(UIManager.getColor("controlShadow"), 
                    UIManager.getColor("control")); //NOI18N
            }
            if (finderLook) {
                drawLower (g, x, y, w, h, col, finderLook);
            } else {
//                drawLower (g, x, y, w, h, Color.LIGHT_GRAY, finderLook);
                drawLower (g, x, y-1, w, h, col, finderLook);
                g.setColor(new Color(200,200,200));
                g.drawLine (x+(arcsize/2)-3, y+h-1, x+w-(arcsize/2), y+h-1);
            }
        }
        
        private void drawLower (Graphics g, int x, int y, int w, int h, Color col, boolean finderLook) {

            g.setColor(col);
            g.drawLine(x, y+(arcsize/2), x, y+h-(arcsize / 2));
            g.drawLine(x+w-1, y+(arcsize/2), x+w-1, y+h-(arcsize / 2));

            if (!finderLook) {
                g.setColor(new Color(220,220,220));
                g.drawArc (x-1, y+1+h-arcsize, arcsize, arcsize, 180, 90);
                g.drawArc ((x+1)+w-(arcsize+1), y+1+h-(arcsize+1), arcsize, arcsize, 270, 90);
                g.setColor(col);
            }
            
            g.drawArc (x, y+h-arcsize, arcsize, arcsize, 180, 90);
            g.drawArc (x+w-(arcsize+1), y+h-(arcsize+1), arcsize, arcsize, 270, 90);

            if (!finderLook) {
                
                g.setColor (new Color(80,80,80));
            }
            g.drawLine (x+(arcsize/2)-3, y+h-1, x+w-(arcsize/2), y+h-1);
        }
        
        private void drawUpper (Graphics g, int x, int y, int ytop, int w, int h) {
            g.drawArc (x, ytop, arcsize, arcsize, 90, 90);

            g.drawArc (x+w-(arcsize+1), ytop, arcsize, arcsize, 90, -90);
            
            g.drawLine(x+(arcsize/2), ytop, x+w-(arcsize/2), ytop);
        }
        
        Paint getUpperPaint (Color top, Color bottom, int w, int h) {
            GradientPaint result = 
                ColorUtil.getGradientPaint (0, h/4, top, 0, (h/2) + (h/4), 
                bottom);
            return result;
        }
        
        Paint getLowerPaint (Color top, Color bottom, int w, int h) {
            /*
            GradientPaint result = 
                ColorUtil.getGradientPaint (0, h/4, top, 0, h, 
                bottom);
             */
            GradientPaint result = 
                ColorUtil.getGradientPaint (0, h/2, top, 0, (h/2) + (h/4), 
                bottom);
            
            return result;
        }
        
        Paint getUpperPaint (int w, int h) {
            return getUpperPaint (UPPER_GRADIENT_TOP, UPPER_GRADIENT_BOTTOM, w, h);
        }

        Paint getLowerPaint (int w, int h) {
            return getLowerPaint (LOWER_GRADIENT_TOP, 
                LOWER_GRADIENT_BOTTOM , w, h);
        }
        
        Shape getInteriorShape(int w, int h) {
            int off = Boolean.getBoolean("apple.awt.brushMetalLook") ? 2 : 0;
            RoundRectangle2D r2d = new RoundRectangle2D.Double(0, off, w, h-2*off, arcsize, arcsize);
            return r2d;
        }
        
        Shape getUpperBevelShape(int w, int h) {
            int[] xpoints = new int[] {
                0,
                0,
                h / 2,
                w - (h / 4),
                w,
                w,
                0
            };
            
            int off = Boolean.getBoolean("apple.awt.brushMetalLook") ? 2 : 0;
            int[] ypoints = new int[] {
                off,
                h - (h / 4),
                h / 2,
                h / 2,
                h / 4,
                off,
                off
            };
            Polygon p = new Polygon (xpoints, ypoints, ypoints.length);
            return p;
        }
        
        Shape getLowerBevelShape(int w, int h) {
            int[] xpoints = new int[] {
                0,
                0,
                h / 4,
                w - (h / 4),
                w,
                w,
                0
            };
            
            int off = Boolean.getBoolean("apple.awt.brushMetalLook") ? 2 : 0;
            int[] ypoints = new int[] {
                h-off,
                h - (h / 4),
                h / 2,
                h / 2,
                h / 4,
                h-off,
                h-off
                
            };
            Polygon p = new Polygon (xpoints, ypoints, ypoints.length);
            return p;
        }
        
    }
    
    private static Color mezi (Color c1, Color c2) {
        return new Color((c1.getRed() + c2.getRed()) / 2,
                        (c1.getGreen() + c2.getGreen()) / 2,
                        (c1.getBlue() + c2.getBlue()) / 2);
    }
    
    private static Map hintsMap = null;
    public static final Map getHints() {
        if (hintsMap == null) {
            hintsMap = new HashMap();
            hintsMap.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            hintsMap.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            hintsMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        return hintsMap;
    }    
    
//    public static void main (String[] args) {
//        System.getProperties().put("apple.awt.brushMetalLook","true");
//        javax.swing.JFrame jf = new javax.swing.JFrame();
//        jf.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//        javax.swing.JToolBar jtb = new javax.swing.JToolBar();
//        jtb.setRollover(true);
//        jtb.setUI (new PlainAquaToolbarUI());
//        jf.getContentPane().setLayout (new java.awt.BorderLayout());
//        javax.swing.JPanel panel = new javax.swing.JPanel();
//        panel.add(jtb);
//        jf.getContentPane().add (panel, java.awt.BorderLayout.NORTH);
//        javax.swing.JButton b = new javax.swing.JButton("Some button");
//        jtb.add (b);
//        javax.swing.JButton b2 = new javax.swing.JButton("Another button");
//        jtb.add(b2);
//        
//        jf.setBounds (20,20, 400, 300);
//        
//        javax.swing.JTextArea foo = new javax.swing.JTextArea("Foodbar");
//        jf.getContentPane().add (foo, java.awt.BorderLayout.SOUTH);
//        
//        jf.show();
//        System.out.println("fl="+isFinderLook (jf));
//
//    }
}
