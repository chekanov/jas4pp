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

/**
 * An abstract class representing a color which has a name and may provide
 * custom code for instantiation. Implements comparable in order to appear in an
 * ordered way in palettes. Note that this class is internal to the color
 * chooser. It is not acceptable for the color chooser to provide instances of
 * NamedColor from its getColor method, since they may be serialized and will
 * not be deserializable if their implementation is not on the classpath.
 * 
 * @author Tim Boudreau
 */
abstract class NamedColor extends Color implements Comparable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of NamedColor
	 * 
	 * @param name
	 * @param r
	 *            red
	 * @param g
	 *            green
	 * @param b
	 *            blue
	 */
	protected NamedColor(final String name, final int r, final int g,
			final int b) {
		super(r, g, b);
	}

	/**
	 * Get a localized display name for this color if possible. For some colors,
	 * such as named system colors, a localized variant is not a reasonable
	 * option.
	 * 
	 * @return the localized (or not) display name
	 */
	public abstract String getDisplayName();

	/**
	 * Get the programmatic name, if any, for this color, such as a Swing
	 * UIDefaults key or an SVG constant name.
	 */
	public abstract String getName();

	/**
	 * Fetch a java code snippet for instantiating this color. For cases such as
	 * named defaults from the Swing UIManager, this method might return
	 * something such as <code>UIManager.getColor(&quot;control&quot;)</code>.
	 * Useful when implementing a property editor.
	 * 
	 * @return a string that could be pasted into Java code to instantiate a
	 *         color with these rgb values
	 */
	public String getInstantiationCode() {
		return toString();
	}

	static NamedColor create(final Color c, final String name) {
		return new DefaultNamedColor(c, name);
	}

	private static final class DefaultNamedColor extends NamedColor {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final String name;

		public DefaultNamedColor(final Color c, final String name) {
			super(name, c.getRed(), c.getGreen(), c.getBlue());
			this.name = name;
		}

		@Override
		public String getDisplayName() {
			return name;
		}

		@Override
		public String getName() {
			return name;
		}

		public int compareTo(final Object o) {
			if (o instanceof NamedColor) {
				final NamedColor nc = (NamedColor) o;
				final String nm = nc.getDisplayName();
				if (nm == null && getDisplayName() == null) {
					return 0;
				} else {
					return nm != null && getDisplayName() != null ? getDisplayName()
							.compareTo(nm)
							: -1;
				}
			} else {
				return -1;
			}
		}
	}
}
