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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Default border for the color chooser, which uses bevels that lighten/darken
 * the selected color, and includes slightly different painting logic for Metal
 * LAF.
 * 
 * @author Tim Boudreau
 */
class CCBorder implements Border {
	public Insets getBorderInsets(final Component c) {
		Insets result;
		if (UIManager.getLookAndFeel() instanceof MetalLookAndFeel) {
			result = new Insets(2, 2, 1, 1);
		} else {
			result = new Insets(1, 1, 1, 1);
		}
		return result;
	}

	public boolean isBorderOpaque() {
		return true;
	}

	public void paintBorder(final Component c, final Graphics g, final int x,
			final int y, final int w, final int h) {
		final ColorChooser cc = (ColorChooser) c;

		if (!cc.isEnabled()) {
			g.setColor(cc.getColor());
			g.fillRect(x, y, w, h);
			return;
		}
		Color col = cc.transientColor() == null ? cc.getColor() : cc
				.transientColor();

		if (cc == null) {
			col = c.getBackground();
			if (col == null) {
				col = Color.BLACK;
			}
		}
		if (UIManager.getLookAndFeel() instanceof MetalLookAndFeel) {
			g.setColor(darken(col));
			g.drawLine(x, y, x + w - 1, y);
			g.drawLine(x, y, x, y + h - 1);
			g.drawLine(x + w - 1, y + h - 1, x, y + h - 1);
			g.drawLine(x + w - 1, y + h - 1, x + w - 1, y);
			g.setColor(brighten(col));
			g.drawLine(x + w - 2, y + h - 2, x + 1, y + h - 2);
			g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + 1);
		} else {
			g.setColor(darken(col));
			g.drawLine(x + w - 1, y + h - 1, x, y + h - 1);
			g.drawLine(x + w - 1, y + h - 1, x + w - 1, y);
			g.setColor(brighten(col));
			g.drawLine(x, y, x + w - 1, y);
			g.drawLine(x, y, x, y + h - 1);
		}
	}

	/** Slightly more subtle than Color.darker() */
	private static final Color darken(final Color c) {
		final int amount = 30;
		final int r = normalizeToByte(c.getRed() - amount);
		final int g = normalizeToByte(c.getGreen() - amount);
		final int b = normalizeToByte(c.getGreen() - amount);
		return new Color(r, g, b);
	}

	/** Slightly more subtle than Color.brighter() */
	private static final Color brighten(final Color c) {
		final int amount = 30;
		final int r = normalizeToByte(c.getRed() + amount);
		final int g = normalizeToByte(c.getGreen() + amount);
		final int b = normalizeToByte(c.getGreen() + amount);
		return new Color(r, g, b);
	}

	/** Ensure an int is within the possible range for a byte */
	private static final int normalizeToByte(final int i) {
		return Math.min(255, Math.max(0, i));
	}
}