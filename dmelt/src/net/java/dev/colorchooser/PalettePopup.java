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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import javax.swing.JComponent;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Encapsulates the logic of a popup palette which can callback a color chooser
 * to set its transient color or its final color if the mouse is released over
 * the palette.
 * 
 * @author Tim Boudreau
 */
class PalettePopup extends MouseAdapter implements MouseMotionListener,
PropertyChangeListener {
	private Popup pop = null;
	private PalettePanel panel = null;
	private Palette pal = null;
	private static Reference defaultInstance = null;

	/** Creates a new instance of PalettePopup */
	private PalettePopup() {
	}

	public static PalettePopup getDefault() {
		PalettePopup result = null;
		if (defaultInstance != null) {
			result = (PalettePopup) defaultInstance.get();
		}

		if (result == null) {
			result = new PalettePopup();
			defaultInstance = new SoftReference(result);
		}
		return result;
	}

	private PalettePanel getPalettePanel() {
		if (panel == null) {
			panel = new PalettePanel();
		}
		return panel;
	}

	public void setPalette(final Palette pal) {
		if (pal != this.pal) {
			if (isPopupVisible()) {
				final Dimension newSize = pal.getSize();
				final Dimension oldSize = this.pal.getSize();
				if (newSize.equals(oldSize)) {
					panel.setPalette(pal);
					panel.repaint();
				} else {
					synchronized (lastOwner.getTreeLock()) {
						pop.hide();
						panel.setPalette(pal);
						pop = PopupFactory.getSharedInstance().getPopup(
								lastOwner, getPalettePanel(), lastCoords.x,
								lastCoords.y);
						pop.show();
					}
				}
			}
			this.pal = pal;
		}
	}

	private Palette getPalette() {
		return pal;
	}

	private ColorChooser lastOwner;
	private Point lastCoords;

	public void showPopup(final ColorChooser owner, final Point coords) {
		if (pal == null) {
			throw new IllegalStateException("No palette specified");
		}
		setPopupOwner(owner);
		lastCoords = coords;
		if (pop != null) {
			pop.hide();
			pop = null;
		}
		final PalettePanel pp = getPalettePanel();
		pp.setPalette(getPalette());
		final Dimension sz = pp.getPreferredSize();
		final Rectangle r = owner.getGraphicsConfiguration().getDevice()
		.getDefaultConfiguration().getBounds();
		final Rectangle test = new Rectangle(coords, sz);
		if (!r.contains(test)) {
			final int offy = Math.max(0, (test.y + test.height)
					- (r.y + r.height));
			final int offx = Math.max(0, (test.x + test.width)
					- (r.x + r.width));
			coords.x -= offx;
			coords.y -= offy;
		}

		pop = PopupFactory.getSharedInstance().getPopup(owner, pp, coords.x,
				coords.y);
		pop.show();
		owner.firePickerVisible(true);
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		.addPropertyChangeListener(this);
	}

	private void setPopupOwner(final ColorChooser owner) {
		if (lastOwner == owner) {
			return;
		}
		if (lastOwner != null) {
			detachFromOwner();
			lastOwner.firePickerVisible(false);
		}
		attachToOwner(owner);
	}

	private void detachFromOwner() {
		if (lastOwner != null) {
			lastOwner.removeMouseMotionListener(this);
			lastOwner.removeMouseListener(this);
		}
		lastOwner = null;
	}

	private void attachToOwner(final ColorChooser owner) {
		lastOwner = owner;
		owner.addMouseListener(this);
		owner.addMouseMotionListener(this);
	}

	public void hidePopup(final ColorChooser owner) {
		if (owner != lastOwner) {
			return;
		}
		hidePopup();
	}

	private void hidePopup() {
		if (pop != null) {
			pop.hide();
			pop = null;
			detachFromOwner();
			KeyboardFocusManager.getCurrentKeyboardFocusManager()
			.removePropertyChangeListener(this);
		}
	}

	private boolean isPopupVisible() {
		return pop != null;
	}

	public boolean isPopupVisible(final ColorChooser chooser) {
		return lastOwner == chooser && isPopupVisible();
	}

	public void mouseDragged(final java.awt.event.MouseEvent e) {
		final Point p = e.getPoint();
		SwingUtilities.convertPointToScreen(p, lastOwner);
		convertPointToPalette(p);
		final Dimension d = panel.getOffset();
		if (d != null) {
			p.x -= d.width;
			p.y -= d.height;
		}
		if (p.x >= 0 && p.y >= 0 && p.x <= pal.getSize().width
				&& p.y < pal.getSize().height) {
			lastOwner.setTransientColor(pal.getColorAt(p.x, p.y));
			panel.setDisplayTitle(pal.getNameAt(p.x, p.y));
		} else {
			lastOwner.setTransientColor(null);
			panel.setDisplayTitle(null);
		}
	}

	public void mouseMoved(final java.awt.event.MouseEvent e) {
	}

	private void convertPointToPalette(final Point p) {
		p.x -= lastCoords.x;
		p.y -= lastCoords.y;
	}

	public void propertyChange(final PropertyChangeEvent evt) {
		if ("focusOwner".equals(evt.getPropertyName())) {
			final Object o = evt.getNewValue();
			if (o != panel && o != lastOwner) {
				hidePopup();
			}
		}
	}

	private class PalettePanel extends JComponent {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Palette pal = null;

		private PalettePanel() {
		}

		private String title = null;

		public void setPalette(final Palette pal) {
			Dimension oldSize = null;
			if (pal != null && isShowing()) {
				oldSize = pal.getSize();
			}
			this.pal = pal;
			if (oldSize != null && pal != null
					&& !pal.getSize().equals(oldSize)) {
				firePropertyChange("preferredSize", oldSize, pal.getSize()); // NOI18N
			}
		}

		public Dimension getOffset() {
			if (pal == null || pal.getDisplayName() == null) {
				return null;
			}
			return new Dimension(0,
					((getPreferredSize().height - pal.getSize().height) / 2));
		}

		@Override
		public void paintComponent(final Graphics g) {
			final Dimension d = pal.getSize();
			final int y = getHeight() - d.height;
			if (pal.getDisplayName() != null) {
				final GradientPaint gp = new GradientPaint(0, 0, UIManager
						.getColor("controlHighlight"), 0, y / 2, UIManager
						.getColor("controlShadow"));
				((Graphics2D) g).setPaint(gp);
				g.fillRect(0, 0, getWidth(), y / 2);

				final String s = pal.getDisplayName();
				g.setFont(getFont().deriveFont(Font.BOLD,
						getFont().getSize() - 2));
				final int ht = g.getFontMetrics(g.getFont()).getHeight();
				final int wid = g.getFontMetrics(g.getFont()).stringWidth(s);
				final int xpos = (getWidth() / 2) - (wid / 2);

				g.setColor(UIManager.getColor("controlShadow"));
				g.drawLine(0, 0, getWidth() - 1, 0);
				g.drawLine(0, 0, 0, (y / 2) - 1);
				g.drawLine(getWidth() - 1, 0, getWidth() - 1, (y / 2) - 1);

				g.setColor(UIManager.getColor("textText"));
				g.drawString(s, xpos, ht - 3);
				g.translate(0, y / 2);
			}
			pal.paintTo(g);

			final int top = pal.getDisplayName() == null ? y : y / 2;
			final int bottom = pal.getDisplayName() == null ? getHeight()
					: getHeight() - top;

			final GradientPaint gp = new GradientPaint(0, getHeight() - y,
					UIManager.getColor("controlHighlight"), 0, bottom,
					UIManager.getColor("controlShadow")); // NOI18N

			((Graphics2D) g).setPaint(gp);
			g.fillRect(0, getHeight() - y, getWidth(), top);
			if (title != null) {
				g.setColor(UIManager.getColor("textText")); // NOI18N
				g.setFont(getFont().deriveFont(Font.PLAIN,
						getFont().getSize() - 2));
				final int xp = (getWidth() - g.getFontMetrics(g.getFont())
						.stringWidth(title)) - 3;
				g.drawString(title, xp, getHeight()
						- (pal.getDisplayName() == null ? 4 : (y / 2) + 4));
			}
			g.setColor(UIManager.getColor("controlShadow")); // NOI18N
			g.drawLine(0, getHeight() - y, 0, getHeight());
			g.drawLine(0, getHeight() - (top + 1), getWidth() - 1, getHeight()
					- (top + 1));
			g.drawLine(getWidth() - 1, getHeight() - (top + 1), getWidth() - 1,
					getHeight() - y);
		}

		public void setDisplayTitle(final String s) {
			if (s != title
					&& ((s != null) != (title != null) || ((s != null && !s
							.equals(title))))) {
				title = s;
				repaint();
			}
			if (s == null && title != null) {
				title = null;
				repaint();
			}
		}

		@Override
		public Dimension getPreferredSize() {
			if (pal != null) {
				final Dimension result = new Dimension(pal.getSize());
				int spacing = 14;
				if (lastOwner != null) {
					Graphics g = lastOwner.getGraphics();
					if (g == null) {
						try {
							g = GraphicsEnvironment
							.getLocalGraphicsEnvironment()
							.getDefaultScreenDevice()
							.getDefaultConfiguration()
							.createCompatibleImage(1, 1)
							.createGraphics();
						} catch (final SecurityException e) {
							g = new BufferedImage(1, 1,
									BufferedImage.TYPE_INT_ARGB)
							.createGraphics();
						}
					}
					if (g != null) {
						Font f = getFont() == null ? UIManager
								.getFont("controlFont") : getFont();
								if (f == null) {
									f = new Font("Serif", Font.PLAIN, 12);
								}
								spacing = g.getFontMetrics(
										f.deriveFont(Font.PLAIN, f.getSize() - 2))
										.getHeight();
					}
					g.dispose();
				}
				if (pal.getDisplayName() != null) {
					spacing *= 2;
				}
				result.height += spacing;
				return result;
			} else {
				return new Dimension(10, 10);
			}
		}
	}
}
