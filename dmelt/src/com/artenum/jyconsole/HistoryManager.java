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
package com.artenum.jyconsole;

import java.util.ArrayList;
import jehep.shelljython.*;

/**
 * <pre>
 * <b>Project ref           :</b> JyConsole project
 * <b>Copyright and license :</b> See relevant sections
 * <b>Status                :</b> under development
 * <b>Creation              :</b> 04/03/2005
 * <b>Modification          :</b>
 *
 * <b>Description  :</b> Keep track of the command history.
 *
 * </pre>
 * <table cellpadding="3" cellspacing="0" border="1" width="100%">
 * <tr BGCOLOR="#CCCCFF" CLASS="TableHeadingColor"><td><b>Version number</b></td><td><b>Author (name, e-mail)</b></td><td><b>Corrections/Modifications</b></td></tr>
 * <tr><td>0.1</td><td>Sebastien Jourdain, jourdain@artenum.com</td><td>Creation</td></tr>
 * </table>
 *
 * @author        Sebastien Jourdain
 * @version       0.1
 */
public class HistoryManager {

    private JyShell  console;
    private ArrayList history;
    private int historyIndex;
    private boolean inHistory;

    public HistoryManager(JyShell  console) {
        this.console = console;
        this.history = new ArrayList();
    }

    public void addCommandInHistory(String command) {
        //System.out.println("="+command+"=");
        //System.out.println("="+command.indexOf("\n")+"=");
        stopHistoryNavigation();
        history.remove(command);
        history.add(command);
    }

    public void showPreviousCommand() {
        if (!inHistory) {
            startHistoryNavigation();
        }

        historyIndex--;
        if (isInRange()) {
            console.getInteractiveCommandLine().reset();
            console.getInteractiveCommandLine().append((String) history.get(historyIndex));
        } else {
            historyIndex++;
        }
    }

    public void showNextCommand() {
        if (!inHistory) {
            return;
        }

        historyIndex++;
        if (isInRange()) {
            console.getInteractiveCommandLine().reset();
            console.getInteractiveCommandLine().append((String) history.get(historyIndex));
        } else {
            historyIndex--;
        }
    }

    public void startHistoryNavigation() {
        inHistory = true;
        history.add(console.getInteractiveCommandLine().getCmdLine());
        historyIndex = history.size() - 1;
    }

    public void stopHistoryNavigation() {
        if (!inHistory) {
            return;
        }

        inHistory = false;
        if ((history.size() > 0) && (((String) history.get(history.size() - 1)).trim().length() == 0)) {
            history.remove(history.size() - 1);
        }
    }

    public boolean isInHistory() {
        return inHistory;
    }

    public boolean isInRange() {
        return (historyIndex >= 0) && (historyIndex < history.size());
    }
}
