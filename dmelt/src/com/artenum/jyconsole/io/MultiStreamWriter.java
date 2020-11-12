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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Sebastien Jourdain, jourdain@artenum.com
 */
public class MultiStreamWriter extends OutputStream {
    private ArrayList writers;

    public MultiStreamWriter() {
        writers = new ArrayList();
    }

    public void addSingleStream(SingleStream writer) {
        writers.add(writer);
    }

    public void removeSingleStream(SingleStream writer) {
        writers.remove(writer);
    }

    public void write(int b) throws IOException {
        for (Iterator i = writers.iterator(); i.hasNext();) {
            SingleStream lw = (SingleStream) i.next();
            if (lw.isActive()) {
                lw.write(b);
            }
        }
    }

    public void write(byte[] b) throws IOException {
        for (Iterator i = writers.iterator(); i.hasNext();) {
            SingleStream lw = (SingleStream) i.next();
            if (lw.isActive()) {
                lw.write(b);
            }
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        for (Iterator i = writers.iterator(); i.hasNext();) {
            SingleStream lw = (SingleStream) i.next();
            if (lw.isActive()) {
                lw.write(b, off, len);
            }
        }
    }
}
