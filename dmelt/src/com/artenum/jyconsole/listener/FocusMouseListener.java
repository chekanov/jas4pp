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
package com.artenum.jyconsole.listener;

import com.artenum.jyconsole.AutoScrollable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextPane;

/**
 * @author Sebastien Jourdain, jourdain@artenum.com
 */
public class FocusMouseListener implements MouseListener {
    private JTextPane txtPane;
    private AutoScrollable autoScroll;

    public FocusMouseListener(JTextPane txtPane, AutoScrollable autoScroll) {
        this.txtPane = txtPane;
        this.autoScroll = autoScroll;
    }

    public void mouseClicked(MouseEvent e) {
        txtPane.grabFocus();
        //txtPane.setCaretPosition(txtPane.getCaretPosition());
        autoScroll.updateScrollPosition();
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}
}
