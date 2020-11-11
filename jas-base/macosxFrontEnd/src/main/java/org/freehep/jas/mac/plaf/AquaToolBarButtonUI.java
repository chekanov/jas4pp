/*
 * AquaToolBarButtonUI.java
 *
 * Created on January 17, 2004, 1:54 PM
 */

package org.freehep.jas.mac.plaf;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonListener;

/** A finder-style aqua toolbar button UI
 *
 * @author  Tim Boudreau
 */
class AquaToolBarButtonUI extends ButtonUI implements ChangeListener {
    private static BasicButtonListener listener = 
        new BasicButtonListener(null);
    
    /** Creates a new instance of AquaToolBarButtonUI */
    public AquaToolBarButtonUI() {
    }
    
    public void installUI (JComponent c) {
        AbstractButton b = (AbstractButton) c;
        b.addMouseListener (listener);
        b.addChangeListener(this);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setFocusable(false);
        b.setBorderPainted(false);
        b.setBorder (BorderFactory.createEmptyBorder());
    }
    
    public void uninstallUI(JComponent c) {
        c.removeMouseListener (listener);
    }
    
    public void stateChanged(ChangeEvent e) {
        ((AbstractButton) e.getSource()).repaint();
    }
    
    private final Rectangle scratch = new Rectangle();
    public void paint (Graphics g, JComponent c) {
        Rectangle r = c.getBounds(scratch);
        AbstractButton b = (AbstractButton) c;
        r.x = 0;
        r.y = 0;
        Paint temp = ((Graphics2D) g).getPaint();
        paintBackground ((Graphics2D)g, b, r);
        paintIcon (g, b, r);
        ((Graphics2D) g).setPaint(temp);
    }
    
    private void paintBackground (Graphics2D g, AbstractButton b, Rectangle r) {
        if (!b.isEnabled()) {
            if (!isLast(b)) {
                drawDivider ((Graphics2D)g, r);
            }
        } else if (b.getModel().isPressed()) {
            compositeColor (g, r, Color.BLUE, 0.3f);
        } else if (b.getModel().isSelected()) {
            compositeColor (g, r, new Color (0, 120, 255), 0.2f);;
        } else {
            if (!isLast(b)) {
                drawDivider ((Graphics2D)g, r);
            }
        }
    }
    
    private void compositeColor (Graphics2D g, Rectangle r, Color c, float alpha) {
        g.setColor (c);
        Composite comp = g.getComposite();

        g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, alpha));

        g.fillRect (r.x, r.y, r.width, r.height);
        g.setComposite(comp);
    }
    
    private void drawDivider (Graphics2D g, Rectangle r) {
       g.setColor (Color.GRAY);
        Composite comp = g.getComposite();
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g.drawLine (r.width-1, 0, r.width-1, r.height-1);
        g.setComposite(comp);
    }
    
    private static boolean isLast (AbstractButton b) {
        return b == b.getParent().getComponent(b.getParent().getComponentCount()-1);
    }
    
    private static boolean isFirst (AbstractButton b) {
        if (b.getParent() != null && b.getParent().getComponentCount() > 1) {
            //The grip is always component 0, so see if the button
            //is component 1
            return b == b.getParent().getComponent(1);
        } else {
            return false;
        }
    }
    
    private void paintIcon (Graphics g, AbstractButton b, Rectangle r) {
        Icon ic = getIconForState (b);
        if (ic != null) {
            int iconX = 0;
            int iconY = 0;
            int iconW = ic.getIconWidth();
            int iconH = ic.getIconHeight();
            
            if (iconW <= r.width) {
                iconX = (r.width / 2) - (iconW / 2);
            }
            if (iconH <= r.height) {
                iconY = (r.height / 2) - (iconH / 2);
            }
            iconY -= 1;
            ic.paintIcon(b, g, iconX, iconY);
        }
    }
    
    private Icon getIconForState (AbstractButton b) {
        ButtonModel mdl = b.getModel();
        Icon result = null;
        if (!b.isEnabled()) {
            result = mdl.isSelected() ? b.getDisabledSelectedIcon() : b.getDisabledIcon();
            if (result == null && mdl.isSelected()) {
                result = b.getDisabledIcon();
            }
        } else {
            if (mdl.isArmed() && !mdl.isPressed()) {
                result = mdl.isSelected() ? b.getRolloverSelectedIcon() : b.getRolloverIcon();
                if (result == null & mdl.isSelected()) {
                    result = b.getRolloverIcon();
                }
            }
            if (mdl.isPressed()) {
                result = b.getPressedIcon();
            } else if (mdl.isSelected()) {
                result = b.getSelectedIcon();
            }
        }
        if (result == null) {
            result = b.getIcon();
        }
        return result;
    }
    
    private static final int minButtonSize = 24;
    public Dimension getPreferredSize(JComponent c) {
        if (c instanceof AbstractButton) {
            Icon ic = getIconForState((AbstractButton) c);
            Dimension result;
            int minSize = isFirst((AbstractButton)c) ? 0 : minButtonSize;
            if (ic != null) {
                result = new Dimension(Math.max(minSize, ic.getIconWidth()+1), 
                    Math.max(minButtonSize,ic.getIconHeight() + 1));
            } else {
                result = new Dimension (minButtonSize, minButtonSize);
            }
            return result;
        } else {
            if (c.getLayout() != null) {
                return c.getLayout().preferredLayoutSize(c);
            }
        }
        return null;
    }    
}
