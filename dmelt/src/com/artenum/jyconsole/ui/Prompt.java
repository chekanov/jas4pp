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

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;

import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

/**
 * @author Sebastien Jourdain, jourdain@artenum.com
 */
public class Prompt extends JPanel implements ComponentListener {
    private final static String l1 = "&gt;&gt;&gt; ";
    private final static String ln = "<br>... ";
    private JTextPane txtCmd;
    private JLabel label;

    public Prompt(JTextPane txtCmd) {
        setOpaque(true);
        label = new JLabel();
        label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        showNLines(1);
        this.txtCmd = txtCmd;
        txtCmd.addComponentListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(label);
        add(Box.createVerticalGlue());
    }



    public void showNLines(int nbLines) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html>");
        buffer.append(l1);
        while (nbLines-- > 1)
            buffer.append(ln);

        buffer.append("</html>");
        label.setText(buffer.toString());
    }

    public void setColor(Color fg, Color bg) {
        setBackground(bg);
        label.setForeground(fg);
    }

    public void updateLineView() {
        LineNumberReader lnr = new LineNumberReader(new StringReader(txtCmd.getText()));
        try {
            while (lnr.readLine() != null)
                ;

            showNLines(lnr.getLineNumber() + 1);
            lnr.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void componentHidden(ComponentEvent e) {
        updateLineView();
    }

    public void componentMoved(ComponentEvent e) {
        updateLineView();
    }

    public void componentResized(ComponentEvent e) {
        updateLineView();
    }

    public void componentShown(ComponentEvent e) {
        updateLineView();
    }
}
