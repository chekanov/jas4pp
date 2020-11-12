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


import  jehep.shelljython.*;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * <pre>
 * <b>Project ref           :</b> JyConsole project
 * <b>Copyright and license :</b> See relevant sections
 * <b>Status                :</b> under development
 * <b>Creation              :</b> 04/03/2005
 * <b>Modification          :</b>
 *
 * <b>Description  :</b> JyConsole contextual menu.
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
public class PopupMenu extends MouseAdapter implements ActionListener {
    private JyShell  console;
    private JPopupMenu popupMenu;
    private PreferenceDialog prefDialog;

    public PopupMenu(JyShell  console) {
        this.console = console;
        popupMenu = new JPopupMenu();
        JMenuItem loadFile = new JMenuItem("Load a script");
        loadFile.setActionCommand("LOAD_SCRIPT");
        loadFile.addActionListener(this);
        popupMenu.add(loadFile);
        prefDialog = new PreferenceDialog(console);
        JMenuItem style = new JMenuItem("Change style");
        style.setActionCommand("STYLE");
        style.addActionListener(this);
        popupMenu.add(style);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("LOAD_SCRIPT")) {
            JFileChooser chooser = new JFileChooser();
            if (console.getPreferences().get(JyShell.PREF_SCRIPT_DIR) != null) {
                chooser.setCurrentDirectory(new File((String) console.getPreferences().get(JyShell.PREF_SCRIPT_DIR)));
            }

            if (chooser.showOpenDialog(console) == JFileChooser.APPROVE_OPTION) {
                console.executePythonFile(chooser.getSelectedFile());
            }
        } else if (command.equals("STYLE")) {
            prefDialog.setVisible(true);
        }
    }

    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
