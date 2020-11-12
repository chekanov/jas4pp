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

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

/**
 * @author Sebastien Jourdain, jourdain@artenum.com
 */
public class MiniTextPane extends JTextPane {
    private final Dimension MIN_SIZE = new Dimension(0, 0);

    public MiniTextPane() {
        super();
    }

    public MiniTextPane(StyledDocument doc) {
        super(doc);
    }

    // Remove because of bug on Mac OS X

    /*
       public Dimension getPreferredSize() {
           if (getText().length() > 0) {
               return super.getPreferredSize();
           } else {
               return getMinimumSize();
           }
       }
     */
    public Dimension getMinimumSize() {
        return MIN_SIZE;
    }
}
