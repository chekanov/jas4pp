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
package com.artenum.jyconsole.command;

import org.python.core.PyException;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * <b>Project ref           :</b> JyConsole project
 * <b>Copyright and license :</b> See relevant sections
 * <b>Status                :</b> under development
 * <b>Creation              :</b> 23/06/2006
 * <b>Modification          :</b>
 *
 * <b>Description  :</b> A CommandRunner that sends all commands to a single thread.
 *
 * </pre>
 * <table cellpadding="3" cellspacing="0" border="1" width="100%">
 * <tr BGCOLOR="#CCCCFF" CLASS="TableHeadingColor"><td><b>Version number</b></td><td><b>Author (name, e-mail)</b></td><td><b>Corrections/Modifications</b></td></tr>
 * <tr><td>0.1</td><td>Colin Crist, colincrist@hermesjms.com</td><td>Contribution integrated by Sebastien Jourdain, jourdain@artenum.com</td></tr>
 * </table>
 *
 * @author        Colin Crist, colincrist@hermesjms.com
 * @author        Sebastien Jourdain
 * @version       0.1
 */
public class SingleThreadCommandRunner implements CommandRunner, Runnable {
    private List runnables = new ArrayList();
    private Thread currentThread;
    private String threadName;

    public SingleThreadCommandRunner(String threadName) {
        this.threadName = threadName;

        reset();
    }

    public void stop() {
        if ((currentThread != null) && currentThread.isAlive()) {
            currentThread.interrupt();
        }
    }

    public void invokeLater(Command r) {
        synchronized (runnables) {
            runnables.add(r);

            if (runnables.size() > 0) {
                runnables.notify();
            }
        }
    }

    public void reset() {
        if ((currentThread != null) && currentThread.isAlive()) {
            currentThread.interrupt();
        }

        currentThread = new Thread(this, threadName);
        currentThread.start();
    }

    public void run() {
        while (currentThread == Thread.currentThread()) {
            Command r = null;

            synchronized (runnables) {
                while (runnables.size() == 0) {
                    try {
                        runnables.wait();
                    } catch (InterruptedException e) {}
                }

                if (currentThread == Thread.currentThread()) {
                    r = (Command) runnables.remove(0);
                }
            }

            try {
                if (r != null) {
                    r.run();
                }
            } catch (PyException t) {
                t.printStackTrace();
            }
        }
    }
}
