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
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

/**
 * Palette implementation that can have recent colors added to it.
 * 
 * @author Tim Boudreau
 */
class RecentColors extends Palette {

    private Palette palette;
    private boolean changed=true;

    /** Creates a new instance of RecentColors */
    private RecentColors() {
    }

    private Palette getWrapped() {
        if (changed||palette==null) {
            palette=createPalette();
            changed=false;
        }
        return palette;
    }

    @Override
    public java.awt.Color getColorAt(final int x, final int y) {
        return getWrapped().getColorAt(x, y);
    }

    @Override
    public String getDisplayName() {
        try {
            return ResourceBundle.getBundle(
                    "carmetal.org.netbeans.swing.colorchooser.Bundle").getString(
                    "recent"); // NOI18N
        } catch (final MissingResourceException mre) {
            // mre.printStackTrace();
            return "Recent colors";
        }
    }

    @Override
    public Dimension getSize() {
        final Dimension result=((PredefinedPalette) getWrapped()).calcSize();
        return result;
    }

    @Override
    public void paintTo(final java.awt.Graphics g) {
        getWrapped().paintTo(g);
    }

    @Override
    public String getNameAt(final int x, final int y) {
        return getWrapped().getNameAt(x, y);
    }
    Stack stack=new Stack();

    void add(final Color c) {
        if (c instanceof RecentColor) {
            return;
        }
        if (stack.indexOf(c)==-1) {
            final String name=c instanceof PredefinedPalette.BasicNamedColor?((PredefinedPalette.BasicNamedColor) c).getDisplayName()
                    :null;
            final String toString=c instanceof PredefinedPalette.BasicNamedColor?((PredefinedPalette.BasicNamedColor) c).toString()
                    :null;
            final Color col=new RecentColor(name, c.getRed(), c.getGreen(), c.getBlue(), toString);
            stack.push(col);
            changed=true;
            palette=null;
            if (c instanceof NamedColor) {
                addToNameCache((NamedColor) c);
            }
            saveToPrefs();
        }
    }
    public static final String INNER_DELIMITER="^$";
    public static final String OUTER_DELIMITER="!*";

    public void saveToPrefs() {
        final Preferences prefs=getPreferences();
        if (prefs==null) {
            return;
        }
        int count=0;
        final StringBuffer sb=new StringBuffer();
        final Stack stack=new Stack();
        stack.addAll(this.stack);
        while (!stack.isEmpty()&&count<64) {
            count++;
            final Color c=(Color) stack.pop();
            if (c instanceof DummyColor) {
                break;
            }
            String name="null";
            if (c instanceof PredefinedPalette.BasicNamedColor) {
                final PredefinedPalette.BasicNamedColor nc=(PredefinedPalette.BasicNamedColor) c;
                name=nc.getDisplayName();
            }
            if (name=="null") { // NOI18N
                name=null;
            }
            sb.append(name);
            sb.append(INNER_DELIMITER);
            sb.append(c.getRed());
            sb.append(INNER_DELIMITER);
            sb.append(c.getGreen());
            sb.append(INNER_DELIMITER);
            sb.append(c.getBlue());
            sb.append(INNER_DELIMITER);
            if (c instanceof PredefinedPalette.BasicNamedColor) {
                sb.append(c.toString());
            } else {
                sb.append('x');
            }
            sb.append(OUTER_DELIMITER); // NOI18N
        }
        prefs.put("recentColors", sb.toString()); // NOI18N
    }
    static Map namedMap=null;

    static NamedColor findNamedColor(final Color color) {
        if (namedMap==null) {
            return null;
        }
        final NamedColor result=(NamedColor) namedMap.get(new Integer(color.getRGB()));
        return result;
    }

    static void addToNameCache(final NamedColor color) {
        if (namedMap==null) {
            namedMap=new HashMap(40);
        }
        namedMap.put(new Integer(color.getRGB()), color);
    }

    private Preferences getPreferences() {
        try {
            final Preferences base=Preferences.userNodeForPackage(getClass());
            return base.node("1.5"); // NOI18N
        } catch (final Exception ace) {
            return null;
        }
    }

    public void loadFromPrefs() {
        final Preferences prefs=getPreferences();
        if (prefs==null) {
            return;
        }
        final String s=prefs.get("recentColors", null); // NOI18N
        stack=new Stack();
        final Color[] col=new Color[64];
        Arrays.fill(col, new DummyColor());
        int count=63;
        try {
            if (s!=null) {
                // a weird but highly unlikely delimiter
                final StringTokenizer tok=new StringTokenizer(s,
                        OUTER_DELIMITER); // NOI18N
                while (tok.hasMoreTokens()&&count>=0) {
                    final String curr=tok.nextToken();
                    // another weird but highly unlikely delimiter
                    final StringTokenizer tk2=new StringTokenizer(curr,
                            INNER_DELIMITER); // NOI18N
                    while (tk2.hasMoreTokens()) {
                        String name=tk2.nextToken();
                        if ("null".equals(name)) {
                            name=null;
                        }
                        final int r=Integer.parseInt(tk2.nextToken());
                        final int g=Integer.parseInt(tk2.nextToken());
                        final int b=Integer.parseInt(tk2.nextToken());
                        final String toString=tk2.nextToken();
                        if ("x".equals(toString)) { // NOI18N
                            col[count]=new RecentColor(name, r, g, b);
                        } else {
                            col[count]=new RecentColor(name, r, g, b,
                                    toString);
                        }
                        addToNameCache((NamedColor) col[count]);
                    }
                    count--;
                }
            }
            stack.addAll(Arrays.asList(col));
        } catch (final Exception e) {
            System.err.println("Error loading color preferences"); // NOI18N
            e.printStackTrace();
        }
    }

    private Palette createPalette() {
        final PredefinedPalette.BasicNamedColor[] nc=(PredefinedPalette.BasicNamedColor[]) stack.toArray(new PredefinedPalette.BasicNamedColor[0]);
        return new PredefinedPalette("", nc); // NOI18N
    }

    private class RecentColor extends PredefinedPalette.BasicNamedColor {

        /**
         *
         */
        private static final long serialVersionUID=1L;
        String displayName;
        String toString=null;

        public RecentColor(final String name, final int r, final int g,
                final int b) {
            super(name, r, g, b);
            displayName=name;
        }

        public RecentColor(final String name, final int r, final int g,
                final int b, final String toString) {
            this(name, r, g, b);
            displayName=name;
            this.toString=toString;
        }

        @Override
        public int compareTo(final Object o) {
            return stack.indexOf(o)-stack.indexOf(this);
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public boolean equals(final Object o) {
            if (o instanceof Color) {
                final Color c=(Color) o;
                return c.getRGB()==getRGB();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return getRGB();
        }

        @Override
        public String toString() {
            if (toString!=null) {
                return toString;
            } else {
                return "new java.awt.Color("+getRed()+","+getGreen()
                        +","+getBlue()+")"; // NOI18N
            }
        }
    }
    private static RecentColors defaultInstance=null;

    public static final RecentColors getDefault() {
        if (defaultInstance==null) {
            defaultInstance=new RecentColors();
            ((RecentColors) defaultInstance).loadFromPrefs();
        }
        return defaultInstance;
    }

    /**
     * A stand in for colors to fill up the array of recent colors until we
     * really have something to put there.
     */
    private class DummyColor extends RecentColor {

        /**
         *
         */
        private static final long serialVersionUID=1L;

        public DummyColor() {
            super(null, 0, 0, 0);
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        // Ensure that no color will match this, so black swing colors can
        // be put into the recent colors array
        @Override
        public boolean equals(final Object o) {
            return o==this;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }

    @Override
    public void setSize(final int w, final int h) {
    }
}
