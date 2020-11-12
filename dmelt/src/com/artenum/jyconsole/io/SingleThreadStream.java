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

/**
 * @author Sebastien Jourdain, jourdain@artenum.com
 */
public class SingleThreadStream extends SingleStream {
    private OutputStream os;
    private String threadName;

    public SingleThreadStream(OutputStream os, String threadName) {
        this.os = os;
        this.threadName = threadName;
    }

    public boolean isActive() {
        return Thread.currentThread().getName().startsWith(threadName);
    }

    public void write(int b) throws IOException {
        os.write(b);
    }

    public void write(byte[] b) throws IOException {
        os.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        os.write(b, off, len);
    }
}
