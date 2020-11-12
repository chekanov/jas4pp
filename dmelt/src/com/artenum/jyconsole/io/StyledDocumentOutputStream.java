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
package com.artenum.jyconsole.io;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 * @author Sebastien Jourdain, jourdain@artenum.com
 */
public class StyledDocumentOutputStream extends OutputStream {
    private String styleName;
    private StyledDocument doc;

    public StyledDocumentOutputStream(StyledDocument doc, String styleName) {
        this.styleName = styleName;
        this.doc = doc;
    }

    public void write(int b) throws IOException {
        try {
            doc.insertString(doc.getLength(), String.valueOf((char) b), doc.getStyle(styleName));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        try {
            doc.insertString(doc.getLength(), new String(b, off, len), doc.getStyle(styleName));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] b) throws IOException {
        try {
            doc.insertString(doc.getLength(), new String(b), doc.getStyle(styleName));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
