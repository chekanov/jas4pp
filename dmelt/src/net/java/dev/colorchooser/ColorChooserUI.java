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
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * Parent class of UI delegates for color choosers. This class handles popping
 * up palettes and selection/setting transient color and firing events.
 * Generally, subclasses will simply want to override the painting logic.
 * <p>
 * To completely override all behavior, override <code>installListeners()</code>
 * and <code>uninstallListeners()</code> and do not have them call super.
 * 
 * @author Tim Boudreau
 */
public abstract class ColorChooserUI extends ComponentUI {
	/** Creates a new instance of ColorChooserUI */
	protected ColorChooserUI() {
	}

	@Override
	public final void installUI(final JComponent jc) {
		installListeners((ColorChooser) jc);
		init((ColorChooser) jc);
	}

	@Override
	public final void uninstallUI(final JComponent jc) {
		uninstallListeners((ColorChooser) jc);
		uninit((ColorChooser) jc);
	}

	/** Optional initialization method called from <code>installUI()</code> */
	protected void init(final ColorChooser c) {

	}

	/** Optional initialization method called from <code>uninstallUI()</code> */
	protected void uninit(final ColorChooser c) {

	}

	/** Begin listening for mouse events on the passed component */
	protected void installListeners(final ColorChooser c) {
		final L l = new L();
		c.addMouseListener(l);
		c.addFocusListener(l);
		c.addKeyListener(l);
		c.putClientProperty("uiListener", l); // NOI18N
	}

	/** Stop listening for mouse events on the passed component */
	protected void uninstallListeners(final ColorChooser c) {
		final Object o = c.getClientProperty("uiListener"); // NOI18N
		if (o instanceof L) {
			final L l = (L) o;
			c.removeMouseListener(l);
			c.removeFocusListener(l);
			c.removeKeyListener(l);
		}
	}

	/** Running on macintosh? */
	static final boolean MAC = System.getProperty("mrj.version") // NOI18N
	!= null;

	/**
	 * 
	 * Map a key event to an integer used to index into the array of available
	 * palettes, used to change which palette is displayed on the fly. Note this
	 * method reads the key code, not the modifiers, of the key event.
	 * <p>
	 * If you override this method, also override
	 * <code>paletteIndexFromModifiers</code>.
	 * <p>
	 * The palette actually used is as follows:
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
	 */
	protected int paletteIndexFromKeyCode(final KeyEvent ke) {
		final int keyCode = ke.getKeyCode();
		int result = (keyCode == KeyEvent.VK_SHIFT) ? 1 : 0;
		if (MAC) {
			result += (keyCode == KeyEvent.VK_META) ? 2 : 0;
		} else {
			result += (keyCode == KeyEvent.VK_CONTROL) ? 2 : 0;
		}
		result += (keyCode == KeyEvent.VK_ALT) ? 4 : 0;
		return result;
	}

	/**
	 * 
	 * Map the modifiers on an input event to an integer used to index into the
	 * array of available palettes, used to change which palette is displayed on
	 * the fly. Note this method uses the value of from the event's
	 * <code>getModifiersEx()</code> method.
	 * <p>
	 * If you override this method, also override
	 * <code>paletteIndexFromKeyCode</code>.
	 * <p>
	 * The palette actually used is as follows:
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
	 */
	protected int paletteIndexFromModifiers(final InputEvent me) {
		final int mods = me.getModifiersEx();
		int result = ((mods & InputEvent.SHIFT_DOWN_MASK) != 0) ? 1 : 0;
		result += ((mods & InputEvent.CTRL_DOWN_MASK) != 0) ? 2 : 0;
		result += ((mods & InputEvent.ALT_DOWN_MASK) != 0) ? 4 : 0;
		return result;
	}

	private JColorChooser jchooser = null;

	/**
	 * Called when the color chooser is invoked from the keyboard (user pressed
	 * space or enter).
	 */
	protected void keyboardInvoke(final ColorChooser colorChooser) {
		if (!colorChooser.isEnabled()) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		if (jchooser == null) {
			jchooser = new JColorChooser();
		} else {
			return;
		}
		jchooser.setColor(colorChooser.getColor());
		final Color nue = JColorChooser.showDialog(colorChooser, ColorChooser
				.getString("chooseColor"), // NOI18N
				colorChooser.getColor());

		if (nue != null) {
			colorChooser.setColor(nue);
			fireColorChanged(colorChooser);
		}
		jchooser = null;
	}

	/**
	 * Cause the passed color chooser to fire an action event to its listeners
	 * notifying them that the color has changed.
	 */
	protected void fireColorChanged(final ColorChooser chooser) {
		chooser.fireActionPerformed(new ActionEvent(chooser,
				ActionEvent.ACTION_PERFORMED, "color")); // NOI18N
	}

	@Override
	public Dimension getMaximumSize(final JComponent c) {
		if (!c.isMaximumSizeSet()) {
			return getPreferredSize(c);
		} else {
			return super.getMaximumSize(c);
		}
	}

	@Override
	public Dimension getMinimumSize(final JComponent c) {
		if (!c.isMinimumSizeSet()) {
			return getPreferredSize(c);
		} else {
			return super.getMinimumSize(c);
		}
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		if (!c.isPreferredSizeSet()) {
			return new Dimension(24, 24);
		} else {
			return super.getPreferredSize(c);
		}
	}

	private class L extends MouseAdapter implements FocusListener, KeyListener {
		private int paletteIndex = 0;
		private transient Point nextFocusPopupLocation = null;

		int getPaletteIndex() {
			return paletteIndex;
		}

		void initPaletteIndex(final ColorChooser c, final MouseEvent me) {
			paletteIndex = paletteIndexFromModifiers(me);
			checkRange(c);
		}

		private void checkRange(final ColorChooser chooser) {
			final Palette[] p = chooser.getPalettes();
			if (paletteIndex >= p.length) {
				paletteIndex = p.length - 1;
			}
		}

		private void updatePaletteIndex(final ColorChooser chooser,
				final int value, final boolean pressed) {
			final int oldIndex = paletteIndex;
			int result = paletteIndex;
			if (pressed) {
				result |= value;
			} else {
				result ^= value;
			}
			if (oldIndex != result
					&& PalettePopup.getDefault().isPopupVisible(chooser)) {
				paletteIndex = result;
				checkRange(chooser);
				PalettePopup.getDefault().setPalette(
						chooser.getPalettes()[paletteIndex]);
			}
		}

		@Override
		public void mousePressed(final MouseEvent me) {
			// boolean rightclic=me.isPopupTrigger();

			final boolean rightclic = (me.getModifiers() == Event.META_MASK);

			final ColorChooser chooser = (ColorChooser) me.getSource();
			if (!chooser.isEnabled()) {
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			// Point p = me.getPoint();
			final Point p = chooser.getLocationOnScreen();
			p.translate(chooser.getWidth(), chooser.getHeight());
			// SwingUtilities.convertPointToScreen(p, chooser);
			initPaletteIndex(chooser, me);

			if (rightclic) {
				PalettePopup.getDefault().setPalette(chooser.getPalettes()[7]);

			} else {
				PalettePopup.getDefault().setPalette(
						chooser.getPalettes()[getPaletteIndex()]);
			}

			if (chooser.hasFocus()) {
				PalettePopup.getDefault().showPopup(chooser, p);
			} else {
				nextFocusPopupLocation = p;
				chooser.requestFocus();
				return;
			}

			me.consume();
			nextFocusPopupLocation = null;
		}

		@Override
		public void mouseReleased(final MouseEvent me) {
			// if (me.isPopupTrigger()) return;
			final ColorChooser chooser = (ColorChooser) me.getSource();
			if (!chooser.isEnabled()) {
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			nextFocusPopupLocation = null;
			if (PalettePopup.getDefault().isPopupVisible(chooser)) {
				PalettePopup.getDefault().hidePopup(chooser);
				final Color transientColor = chooser.transientColor();
				if (transientColor != null) {
					RecentColors.getDefault().add(transientColor);
					final Color old = new Color(transientColor.getRed(),
							transientColor.getGreen(), transientColor.getBlue());
					chooser.setTransientColor(null);
					chooser.setColor(old);
					fireColorChanged(chooser);
					me.consume();
				}
			}
		}

		public void focusGained(final FocusEvent e) {
			final ColorChooser chooser = (ColorChooser) e.getSource();
			if (nextFocusPopupLocation != null && chooser.isEnabled()) {
				PalettePopup.getDefault().showPopup(chooser,
						nextFocusPopupLocation);
			}
			nextFocusPopupLocation = null;
			chooser.repaint();
		}

		public void focusLost(final FocusEvent e) {
			final ColorChooser chooser = (ColorChooser) e.getSource();
			chooser.repaint();
		}

		public void keyTyped(final KeyEvent e) {
		}

		public void keyPressed(final KeyEvent e) {
			processKeyEvent(e, true);
		}

		public void keyReleased(final KeyEvent e) {
			processKeyEvent(e, false);
		}

		protected void processKeyEvent(final KeyEvent ke, final boolean pressed) {
			final ColorChooser chooser = (ColorChooser) ke.getSource();
			updatePaletteIndex(chooser, paletteIndexFromKeyCode(ke), pressed);
			if (ke.getKeyCode() == KeyEvent.VK_ALT
					|| ke.getKeyCode() == KeyEvent.VK_CONTROL
					|| ke.getKeyCode() == KeyEvent.VK_SHIFT) {
				ke.consume();
			} else if ((ke.getKeyCode() == KeyEvent.VK_SPACE || ke.getKeyCode() == KeyEvent.VK_ENTER)
					&& ke.getID() == KeyEvent.KEY_RELEASED) {
				keyboardInvoke(chooser);
			}
		}
	}
}
