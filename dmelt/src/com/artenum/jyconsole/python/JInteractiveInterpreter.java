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

import org.python.util.InteractiveInterpreter;

/**
 * @author Sebastien Jourdain, jourdain@artenum.com
 */
public class JInteractiveInterpreter extends InteractiveInterpreter {
    private static MultiStreamWriter mStdOut;
    private static MultiStreamWriter mStdErr;

    public JInteractiveInterpreter() {
        this(true);
    }

    public JInteractiveInterpreter(boolean addDefaultOutput) {
        super();
        initOutputStream(addDefaultOutput);
    }

    public JInteractiveInterpreter(PyObject obj) {
        this(obj, true);
    }

    public JInteractiveInterpreter(PyObject obj, boolean addDefaultOutput) {
        super(obj);
        initOutputStream(addDefaultOutput);
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
