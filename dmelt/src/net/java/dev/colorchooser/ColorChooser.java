/*DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2006-2008. Tim Boudreau. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  This particular file is designated
 * as subject to the "Classpath" exception as provided
 * in the GPL Version 2 section of the License file that
 * accompanied this code.
 */
package net.java.dev.colorchooser;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.UIManager;

/**
 * A color chooser which can pop up a pluggable set of palettes. The palette
 * displayed is controlled by combinations of the alt and shift and ctrl
 * (command on macintosh) keys. Will fire an action event when a color is
 * selected. For accessibility, it will show a standard Swing color chooser if
 * focused and either space or enter are pressed.
 * <p>
 * By default, supports two sets of palettes - a set of 4 continuous palettes
 * and a set of 4 tiled, fixed palettes (the SVG/X palette, Swing/AWT palettes
 * and a history of recently selected colors). Whether the tiled or continuous
 * palettes are given precedence depends on the property <code>
 * continuousPalettePreferred</code>.
 * <p>
 * Palettes are pluggable, so it is possible to provide your own
 * implementation(s) of Palette to be displayed when the component is clicked.
 * <p>
 * Typical usage: Attach an ActionListener; it will be notified when the user
 * selects a color.
 * <p>
 * To catch colors as the user selects, listen for PROP_TRANSIENT_COLOR. The
 * component will fire changes in PROP_COLOR along with actin events, when the
 * user selects a color. PROP_COLOR changes are fired both in response to use
 * actions and programmatic changes to the color property.
 * <P>
 * 
 * @author Tim Boudreau
 */
public class ColorChooser extends JComponent {

    /**
     *
     */
    private static final long serialVersionUID=1L;
    /**
     * UI Class ID under which the UI delegate class is stored in UIManager (see
     * UIManager.getUI()). The string value is
     * <code>&quot;nbColorChooserUI&quot;</code>
     */
    public static final String UI_CLASS_ID="nbColorChooserUI"; // NOI18N
    private transient Palette[] palettes=null;
    private Color color=Color.BLUE;
    private transient Color transientColor=null;
    private transient ArrayList<ActionListener> actionListenerList;
    /**
     * Property name for property fired when the color property changes.
     */
    public static final String PROP_COLOR="color"; // NOI18N
    /**
     * Property name for property fired when the transient color property (the
     * color while the user is selecting) changes.
     */
    public static final String PROP_TRANSIENT_COLOR="transientColor"; // NOI18N
    /**
     * Fired when the value of the continuous palette changes.
     */
    public static final String PROP_CONTINUOUS_PALETTE="continuousPalette"; // NOI18N
    /**
     * Property indicating the visibility of the popup palette. Code that tracks
     * PROP_TRANSIENT_COLOR can listen for this property with a value of false
     * to do a final update using the value from getColor() to ensure the set
     * color is in sync with the actual value of the color picker - in the case
     * that the mouse was released off the palette, the color may be restored to
     * its previous value.
     */
    public static final String PROP_PICKER_VISIBLE="pickerVisible";
    private boolean continuousPalette=true;

    /** Create a color chooser */
    public ColorChooser() {
        this((Color) null);
    }

    /**
     * Create a color chooser initialized to the passed color, defaulted to show
     * a continuous palette on initial click.
     *
     * @param initialColor
     */
    public ColorChooser(final Color initialColor) {
        this(null, initialColor);
    }

    /**
     * Create a color chooser with the passed array of 8 palettes and
     * initialized with the passed color.
     *
     * @param palettes
     * @param initialColor
     */
    public ColorChooser(final Palette[] palettes, final Color initialColor) {
        setPalettes(palettes);
        if (initialColor!=null) {
            color=initialColor;
        }
        updateUI();
    }

    /**
     * Create a color chooser with the passed array of 8 or fewer palettes.
     *
     * @param palettes
     */
    public ColorChooser(final Palette[] palettes) {
        this(palettes, null);
    }

    /**
     * Overridden to return <code>UI_CLASS_ID</code>
     *
     * @return
     */
    public String getUIClassId() {
        return UI_CLASS_ID;
    }

    /**
     *
     */
    @Override
    public void updateUI() {
            if (UIManager.get(UI_CLASS_ID)!=null) {
                setUI((ColorChooserUI) UIManager.getUI(this));
            } else {
                setUI(DefaultColorChooserUI.createUI(this));
            }
    }

    /**
     * Get the color currently represented by this component. If the user is in
     * the process of selecting (the palette or color chooser is open), this
     * will be the last known value, until such time as the user selects a color
     * and an action event is fired.
     *
     * @return
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the color this color chooser currently represents. Note this will
     * fire a change in <code>PROP_COLOR</code> but will not trigger an action
     * event to be fired.
     *
     * @param c
     */
    public void setColor(Color c) {
        if (c.getClass()!=Color.class) {
            c=new Color(c.getRed(), c.getGreen(), c.getBlue());
        }
        if ((color!=null&&!color.equals(c))||(color==null&&c!=null)) {
            final Color old=color;
            color=c;
            repaint();
            firePropertyChange(PROP_COLOR, old, c); // NOI18N
        }
    }

    void setTransientColor(final Color c) {
        final Color old=transientColor;
        transientColor=c;
        if ((c!=null&&!color.equals(old))||(old==null&&c!=null)) {
            firePropertyChange(PROP_TRANSIENT_COLOR, old, getTransientColor());
            repaint();
        } else if (c==null) {
            firePropertyChange(PROP_TRANSIENT_COLOR, old, getColor());
            repaint();
        }
    }

    /**
     * Returns the currently displayed color which may not be the same as the
     * value of <code>getColor()</code> but is the color currently displayed as
     * the user moves the mouse to select the color.
     *
     * @see #PROP_TRANSIENT_COLOR
     * @see #setTransientColor
     * @return the color currently being displayed (not necessarily the one
     *         returned by <code>getColor()</code>).
     */
    public Color getTransientColor() {
        return transientColor==null?null:new Color(transientColor.getRed(), transientColor.getGreen(), transientColor.getBlue());
    }

    /**
     * Get a string representation of a color, if possible returning a named,
     * localized constant if the passed color matches one of the SVG constants;
     * else returning a String representing RGB values.
     *
     * @param c
     * @return
     */
    public static String colorToString(final Color c) {
        RecentColors.getDefault();
        final NamedColor named=RecentColors.findNamedColor(c);
        if (named==null) {
            final StringBuffer sb=new StringBuffer();
            sb.append(c.getRed());
            sb.append(',');
            sb.append(c.getGreen());
            sb.append(',');
            sb.append(c.getBlue());
            return sb.toString();
        } else {
            return named.getDisplayName();
        }
    }

    Color transientColor() {
        return transientColor;
    }

    /**
     * Returns the SVG or Swing constant name for the passed color, if the color
     * exactly matches a color in the Swing UIManager constants or the
     * SVG/X-Windows constants.
     *
     * @param color
     * @return
     */
    public static String getColorName(final Color color) {
        return PredefinedPalette.getColorName(color);
    }

    /**
     * Set whether the initial palette shown when clicked with no keys pressed
     * is one showing a continuous (rainbow) palette or a set of tiles with
     * different colors.
     *
     * @param val
     *            The value, true to show a continuous palette by default
     */
    public void setContinuousPalettePreferred(final boolean val) {
        if (val!=continuousPalette) {
            continuousPalette=val;
            setPalettes(null);
            firePropertyChange(PROP_CONTINUOUS_PALETTE, !val, val);
        }
    }

    /**
     * Determine whether the initial palette shown when clicked with no keys
     * pressed is one showing a continuous (rainbow) palette or a set of tiles
     * with different colors. The default is <code>TRUE</code>.
     *
     * @return whether or not to default to a continuous palette
     */
    public boolean isContinuousPalettePreferred() {
        return continuousPalette;
    }

    /**
     * Set the Palette objects this color chooser will display. Can be null to
     * reset to defaults. The passed array length must less than or equal to 8.
     * <p>
     * Which palette is shown to the user depends on what if any control keys
     * are being held when the user initially clicks or presses while dragging
     * the mouse to select. The mapping between key combinations and palette
     * entries is:
     * <ul>
     * <li>No keys held: 0</li>
     * <li>Shift: 1</li>
     * <li>Ctrl (Command on macintosh): 2</li>
     * <li>Shift-Ctrl(Command): 3</li>
     * <li>Alt: 4</li>
     * <li>Alt-Shift: 5</li>
     * <li>Alt-Ctrl(Command): 6</li>
     * <li>Alt-Ctrl(Command)-Shift: 7</li>
     * </ul>
     *
     * @param palettes
     */
    public void setPalettes(Palette[] palettes) {
        if (palettes!=null&&palettes.length>8) {
            throw new IllegalArgumentException("Must be <= 8 palettes"); // NOI18N
        }
        final Palette[] old=this.palettes;
        if (palettes==null) {
            palettes=Palette.getDefaultPalettes(continuousPalette);
        }
        this.palettes=palettes;
        firePropertyChange("palettes", old, palettes.clone()); // NOI18N
    }

    /**
     * Get the array of palettes that will be displayed when the user clicks
     * this color chooser component and holds down various keys.
     *
     * @return
     */
    public Palette[] getPalettes() {
        final Palette[] result=new Palette[palettes.length];
        System.arraycopy(palettes, 0, result, 0, palettes.length);
        return result;
    }

    static String getString(final String key) {
        if (key==null) {
            return null;
        }
        final String BUNDLE="net.java.dev.colorchooser.Bundle"; // NOI18N
        try {
            return ResourceBundle.getBundle(BUNDLE).getString(key);
        } catch (final MissingResourceException mre) {
            // mre.printStackTrace();
            return key;
        }
    }

    // ****************** Action listener support **************************
    /**
     * Registers ActionListener to receive events. Action events are fired when
     * the user selects a color, either by click-drag-releasing the mouse over
     * the popup palette, or by pressing space or enter and selecting a color
     * from the popup <code>JColorChooser</code>.
     *
     * @param listener
     *            The listener to register.
     */
    public synchronized void addActionListener(
            final java.awt.event.ActionListener listener) {
        if (actionListenerList==null) {
            actionListenerList=new ArrayList<ActionListener>();
        }
        actionListenerList.add(listener);
    }

    /**
     * Removes ActionListener from the list of listeners. Action events are
     * fired when the user selects a color, either by click-drag-releasing the
     * mouse over the popup palette, or by pressing space or enter and selecting
     * a color from the popup <code>JColorChooser</code> (note they are
     * <i>not</i> fired if you call <code>setColor()</code>).
     *
     * @param listener
     *            The listener to remove.
     */
    public synchronized void removeActionListener(
            final java.awt.event.ActionListener listener) {
        if (actionListenerList!=null) {
            actionListenerList.remove(listener);
        }
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param event
     *            The event to be fired
     */
    void fireActionPerformed(final ActionEvent event) {
        List<?> list;
        synchronized (this) {
            if (actionListenerList==null) {
                return;
            }
            list=(List<?>) ((ArrayList<ActionListener>) actionListenerList).clone();
        }
        for (int i=0; i<list.size(); i++) {
            ((java.awt.event.ActionListener) list.get(i)).actionPerformed(event);
        }
    }

    void firePickerVisible(final boolean val) {
        firePropertyChange(PROP_PICKER_VISIBLE, !val, val);
    }
}
