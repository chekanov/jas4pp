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
package com.artenum.jyconsole.action;

import com.artenum.jyconsole.HistoryManager;
import jehep.shelljython.*;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * <pre>
 * <b>Project ref           :</b> JyConsole project
 * <b>Copyright and license :</b> See relevant sections
 * <b>Status                :</b> under development
 * <b>Creation              :</b> 04/03/2005
 * <b>Modification          :</b>
 *
 * <b>Description  :</b> Define the default key for the command history navigation.
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
public class DefaultHistoryNavigationAction extends AbstractAction {
    public final static int UP = 1;
    public final static int DOWN = 2;
    private JyShell  console;
    private HistoryManager historyManager;
    private int mode;

    public DefaultHistoryNavigationAction(JyShell  console, int mode) {
        this.console = console;
        this.mode = mode;
    }

    public void actionPerformed(ActionEvent ae) {
        switch (mode) {
        case UP:
            console.showPreviousCmdInHistory();
            break;
        case DOWN:
            console.showNextCmdInHistory();
            break;
        }
    }
}
