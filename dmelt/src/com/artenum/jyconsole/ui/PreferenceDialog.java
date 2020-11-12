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

import com.artenum.tk.ui.ToolBox;

import jehep.shelljython.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <pre>
 * <b>Project ref           :</b> JyConsole project
 * <b>Copyright and license :</b> See relevant sections
 * <b>Status                :</b> under development
 * <b>Creation              :</b> 04/03/2005
 * <b>Modification          :</b>
 *
 * <b>Description  :</b> JyConsole preference dialog box.
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
public class PreferenceDialog extends JDialog implements ActionListener {
    // Actions
    public final static String OK = "ok";
    public final static String CANCEL = "cancel";
    public final static String BROWSE = "browse";
    public final static String COLOR_DEFAULT = "color.default";
    public final static String COLOR_ERROR = "color.error";
    public final static String COLOR_BG = "color.bg";
    private JyShell  console;

    // ui components
    private JTextField scriptDir;
    private JButton browse;
    private JButton bgColor;
    private JButton defaultTxtColor;
    private JCheckBox defaultItalic;
    private JCheckBox defaultBold;
    private JButton errorTxtColor;
    private JCheckBox errorItalic;
    private JCheckBox errorBold;
    private JButton ok;
    private JButton cancel;

    public PreferenceDialog(JyShell  console) {
        super(ToolBox.getParentFrame(console));
        this.console = console;
        // Init ui
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        // Style
        JPanel panel = new JPanel(new GridLayout(3, 4));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1), "Style"));
        bgColor = new JButton();
        bgColor.setBackground(Color.WHITE);
        bgColor.setActionCommand(COLOR_BG);
        bgColor.addActionListener(this);
        defaultBold = new JCheckBox("Bold");
        defaultItalic = new JCheckBox("Italic");
        defaultTxtColor = new JButton();
        defaultTxtColor.setBackground(Color.BLACK);
        defaultTxtColor.setActionCommand(COLOR_DEFAULT);
        defaultTxtColor.addActionListener(this);
        errorBold = new JCheckBox("Bold");
        errorItalic = new JCheckBox("Italic");
        errorTxtColor = new JButton();
        errorTxtColor.setBackground(Color.RED);
        errorTxtColor.setActionCommand(COLOR_ERROR);
        errorTxtColor.addActionListener(this);

        panel.add(new JLabel("Default"));
        panel.add(defaultTxtColor);
        panel.add(defaultBold);
        panel.add(defaultItalic);
        panel.add(new JLabel("Error"));
        panel.add(errorTxtColor);
        panel.add(errorBold);
        panel.add(errorItalic);
        panel.add(new JLabel("Bg color"));
        panel.add(bgColor);
        getContentPane().add(panel);

        // Script dir
        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1), "Script base directory"));
        scriptDir = new JTextField();
        if (console.getPreferences().get(JyShell.PREF_SCRIPT_DIR) != null) {
            scriptDir.setText((String) console.getPreferences().get(JyShell.PREF_SCRIPT_DIR));
        }

        browse = new JButton("Browse");
        browse.setActionCommand(BROWSE);
        browse.addActionListener(this);
        panel.add(scriptDir, BorderLayout.CENTER);
        panel.add(browse, BorderLayout.EAST);
        panel.setMaximumSize(new Dimension(10000, scriptDir.getHeight()));
        getContentPane().add(panel);

        // cmds
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        ok = new JButton("Ok");
        ok.setActionCommand(OK);
        ok.addActionListener(this);
        cancel = new JButton("Cancel");
        cancel.setActionCommand(CANCEL);
        cancel.addActionListener(this);
        panel.add(Box.createHorizontalGlue());
        panel.add(ok);
        panel.add(cancel);
        getContentPane().add(panel);

        // 
        pack();
        setLocationRelativeTo(console);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals(OK)) {
            console.getPreferences().put(JyShell.PREF_BG_COLOR, bgColor.getBackground());
            console.getPreferences().put(JyShell.PREF_ERROR_TXT_COLOR, errorTxtColor.getBackground());
            console.getPreferences().put(JyShell.PREF_NORMAL_TXT_COLOR, defaultTxtColor.getBackground());
            console.getPreferences().put(JyShell.PREF_SCRIPT_DIR, scriptDir.getText());
            console.savePreferences();
            // Set pref
            console.setColor(defaultTxtColor.getBackground(), bgColor.getBackground());
            console.setBoldToStyle(JyShell.STYLE_NORMAL, defaultBold.isSelected());
            console.setItalicToStyle(JyShell.STYLE_NORMAL, defaultItalic.isSelected());
            console.setColorToStyle(JyShell.STYLE_ERROR, errorTxtColor.getBackground());
            console.setBoldToStyle(JyShell.STYLE_ERROR, errorBold.isSelected());
            console.setItalicToStyle(JyShell.STYLE_ERROR, errorItalic.isSelected());

            File dir = new File(scriptDir.getText());
            if (dir.exists()) {
                console.getPreferences().put(JyShell.PREF_SCRIPT_DIR, dir.getAbsolutePath());
            }
        } else if (command.equals(CANCEL)) {
            dispose();
        } else if (command.equals(BROWSE)) {
            JFileChooser chooser = new JFileChooser((String) console.getPreferences().get(JyShell.PREF_SCRIPT_DIR));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setMultiSelectionEnabled(false);

            if (chooser.showOpenDialog(console) == JFileChooser.APPROVE_OPTION) {
                File dir = chooser.getSelectedFile();
                scriptDir.setText(dir.getAbsolutePath());
            }
        } else if (command.equals(COLOR_BG)) {
            Color currentColor = bgColor.getBackground();
            String title = "Choose background color";
            Color result = JColorChooser.showDialog(this, title, currentColor);
            if (result != null) {
                bgColor.setBackground(result);
            }
        } else if (command.equals(COLOR_DEFAULT) || command.equals(COLOR_ERROR)) {
            Color currentColor = null;
            String title = "Choose text color for style: ";
            if (command.equals(COLOR_DEFAULT)) {
                currentColor = defaultTxtColor.getBackground();
                title += "Default";
            } else {
                currentColor = errorTxtColor.getBackground();
                title += "Error";
            }

            Color result = JColorChooser.showDialog(this, title, currentColor);
            if (result == null) {
                return;
            }

            if (command.equals(COLOR_DEFAULT)) {
                defaultTxtColor.setBackground(result);
            } else {
                errorTxtColor.setBackground(result);
            }
        }
    }
}
