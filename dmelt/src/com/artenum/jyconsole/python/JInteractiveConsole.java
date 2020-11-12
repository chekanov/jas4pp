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
package com.artenum.jyconsole.python;

import com.artenum.jyconsole.io.MultiStreamWriter;
import com.artenum.jyconsole.io.SimpleSingleStream;
import com.artenum.jyconsole.io.SingleStream;

import org.python.core.PyObject;

import org.python.util.InteractiveConsole;

/**
 * @author Sebastien Jourdain, jourdain@artenum.com
 */
public class JInteractiveConsole extends InteractiveConsole {
    private static MultiStreamWriter mStdOut;
    private static MultiStreamWriter mStdErr;

    public JInteractiveConsole() {
        this(true);
    }

    public JInteractiveConsole(PyObject obj) {
        this(obj, true);
    }

    public JInteractiveConsole(PyObject obj, String name) {
        this(obj, name, true);
    }

    public JInteractiveConsole(boolean addDefaultStream) {
        super();
        initOutputStream(addDefaultStream);
    }

    public JInteractiveConsole(PyObject obj, boolean addDefaultStream) {
        super(obj);
        initOutputStream(addDefaultStream);
    }

    public JInteractiveConsole(PyObject obj, String name, boolean addDefaultStream) {
        super(obj, name);
        initOutputStream(addDefaultStream);
    }

    private void initOutputStream(boolean addDefaultStream) {
        if (mStdOut == null) {
            mStdOut = new MultiStreamWriter();
            setOut(mStdOut);
            if (addDefaultStream) {
                addOut(new SimpleSingleStream(System.out, true));
            }
        }

        if (mStdErr == null) {
            mStdErr = new MultiStreamWriter();
            setErr(mStdErr);
            if (addDefaultStream) {
                addErr(new SimpleSingleStream(System.err, true));
            }
        }
    }

    public void addOut(SingleStream ss) {
        mStdOut.addSingleStream(ss);
    }

    public void removeOut(SingleStream ss) {
        mStdOut.removeSingleStream(ss);
    }

    public void addErr(SingleStream ss) {
        mStdErr.addSingleStream(ss);
    }

    public void removeErr(SingleStream ss) {
        mStdErr.removeSingleStream(ss);
    }
}
