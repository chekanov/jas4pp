/*
 * (c) Copyright: Artenum SARL, 101-103 Boulevard Mac Donald,
 *                75019, Paris, France 2005.
 *                http://www.artenum.com
 *
 * License:
 *
 *  This program is free software; you can redistribute it
 *  and/or modify it under the terms of the Q Public License;
 *  either version 1 of the License.
 *
 *  This program is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *  PURPOSE. See the Q Public License for more details.
 *
 *  You should have received a copy of the Q Public License
 *  License along with this program;
 *  if not, write to:
 *    Artenum SARL, 101-103 Boulevard Mac Donald,
 *    75019, PARIS, FRANCE, e-mail: contact@artenum.com
 */
package com.artenum.jyconsole.ui;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

/**
 * <pre>
 *
 *  &lt;b&gt;Project ref           :&lt;/b&gt; JyConsole project
 *  &lt;b&gt;Copyright and license :&lt;/b&gt; See relevant sections
 *  &lt;b&gt;Status                :&lt;/b&gt; under development
 *  &lt;b&gt;Creation              :&lt;/b&gt; 04/03/2005
 *  &lt;b&gt;Modification          :&lt;/b&gt;
 *
 *  &lt;b&gt;Description  :&lt;/b&gt;  Scrollable component that will follow the windows width and will
 *                    force inner component to wrap inside and extend vertically.
 *
 *
 * </pre>
 *
 * <table cellpadding="3" cellspacing="0" border="1" width="100%">
 * <tr BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 * <td><b>Version number </b></td>
 * <td><b>Author (name, e-mail) </b></td>
 * <td><b>Corrections/Modifications </b></td>
 * </tr>
 * <tr>
 * <td>0.1</td>
 * <td>Sebastien Jourdain, jourdain@artenum.com</td>
 * <td>Creation</td>
 * </tr>
 * </table>
 *
 * @author Sebastien Jourdain
 * @version 0.1
 */
public class ScrollingView extends JPanel implements Scrollable {
    public ScrollingView() {}

    public ScrollingView(LayoutManager lm) {
        super(lm);
    }

    public Dimension getPreferredSize() {
        Dimension minSize = ((JViewport) getParent()).getExtentSize();
        Dimension prefSize = super.getPreferredSize();
        return (prefSize.height > minSize.height) ? prefSize : minSize;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
        return 10;
    }

    public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
        return 10;
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
