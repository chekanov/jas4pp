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
 * <b>Description  :</b> Define the default ENTER action.
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
public class DefaultValidationAction extends AbstractAction {
    private JyShell  console;

    public DefaultValidationAction(JyShell  console) {
        this.console = console;
    }

    public void actionPerformed(ActionEvent ae) {
        String command = console.getInteractiveCommandLine().getCmdLine();
        if (command.trim().length() > 0) {
            // Check preset commands
            if (command.trim().toLowerCase().equals("clear")) {
                console.clear();
                return;
            }

            if (command.trim().toLowerCase().equals("copyright")) {
                console.copyright();
                return;
            }

            if (command.trim().toLowerCase().equals("credits")) {
                console.credits();
                return;
            }

            if (command.trim().toLowerCase().equals("license")) {
                console.license();
                return;
            }

            // Check if we need to add a new line in command
            if ((command.split("\n")[0].trim().endsWith(":")) && !command.endsWith("\n")) {
                console.getInteractiveCommandLine().append("\n");
            } else {
                console.executeCmdLine();
            }
        } else {
            console.executeCmdLine();
        }
    }
}
