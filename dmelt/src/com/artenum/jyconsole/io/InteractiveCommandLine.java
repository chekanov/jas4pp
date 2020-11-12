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

import jehep.shelljython.*;
import com.artenum.jyconsole.ui.Prompt;

import java.awt.FontMetrics;
import java.awt.Point;

import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 * @author Sebastien Jourdain, jourdain@artenum.com
 */
public class InteractiveCommandLine implements DocumentListener {
    private String styleName;
    private StyledDocument doc;
    private JTextPane uiPart;
    private Prompt prompt;

    // Completion var
    private boolean needToUpdateCompletionModel;
    private String completionPart;
    private int completionCmdPos;
    private String filterPart;
    private int filterPos;
    private StringBuffer txtBefore;
    private String txtAfter;

    public InteractiveCommandLine(StyledDocument doc, JTextPane uiPart, Prompt prompt, String defaultStyleName) {
        this.doc = doc;
        this.styleName = defaultStyleName;
        this.uiPart = uiPart;
        this.prompt = prompt;
        //
        doc.addDocumentListener(this);
        needToUpdateCompletionModel = true;
    }

    public int getCaretPosition() {
        return uiPart.getCaretPosition();
    }

    public String getCmdLine() {
        try {
            return doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void append(String txt) {
        try {
            doc.insertString(getCaretPosition(), txt, doc.getStyle(styleName));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        prompt.updateLineView();
    }

    public void reset() {
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        uiPart.setCaretPosition(0);
        prompt.updateLineView();
    }

    // Completion cmd methode
    public Point getCaretPositionPoint() {
        FontMetrics fontMetric = uiPart.getFontMetrics(uiPart.getFont());
        int nbEndLine = 1;
        int startLineCharPos = 0;
        int findPos = -1;
        String txt = "";
        try {
            txt = doc.getText(0, getCaretPosition());
        } catch (Exception e1) {}
        while ((findPos = txt.indexOf("\n")) != -1) {
            startLineCharPos += (findPos + 1);
            txt = txt.substring(findPos + 1);
            nbEndLine++;
        }

        try {
            return new Point(fontMetric.stringWidth(doc.getText(startLineCharPos, (filterPos + 1) - startLineCharPos)) + 4,
                (nbEndLine * fontMetric.getHeight()) + 2);
        } catch (BadLocationException e) {
            return new Point(0, 0);
        }
    }

    public boolean askForDictionnary() {
        if (needToUpdateCompletionModel) {
            updateCompletionModel();
        }

        return (completionPart.length() == 0) || (completionPart.equals(filterPart));
    }

    public String getCompletionCmd() {
        if (needToUpdateCompletionModel) {
            updateCompletionModel();
        }

        return completionPart;
    }

    public String getFilterCmd() {
        if (needToUpdateCompletionModel) {
            updateCompletionModel();
        }

        return filterPart;
    }

    public void setCompletionCmd(String cCmd) {
        try {
            doc.remove(completionCmdPos, completionPart.length());
            doc.insertString(completionCmdPos, cCmd, doc.getStyle(JyShell.STYLE_NORMAL));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        uiPart.setCaretPosition(completionCmdPos + cCmd.length());
    }

    public void setFilteredCmd(String fCmd) {
        try {
            doc.remove(filterPos + 1, filterPart.length());
            doc.insertString(filterPos + 1, fCmd, doc.getStyle(JyShell.STYLE_NORMAL));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        uiPart.setCaretPosition(filterPos + 1 + fCmd.length());
    }

    private void updateCompletionModel() {
        StringBuffer cmdLine = new StringBuffer(doc.getLength());
        try {
            cmdLine.append(doc.getText(0, doc.getLength()));
        } catch (BadLocationException e) {}

        txtBefore = new StringBuffer();
        // Cut uninteresting part
        txtAfter = cmdLine.substring(uiPart.getCaretPosition());
        cmdLine.delete(uiPart.getCaretPosition(), cmdLine.length());
        // Cut Line
        if (cmdLine.indexOf("\n") != -1) {
            txtBefore.append(cmdLine.substring(0, cmdLine.lastIndexOf("\n")));
            cmdLine.delete(0, cmdLine.lastIndexOf("\n"));
        }

        // Cut outter methodes
        ArrayList tree = new ArrayList();
        for (int i = 0; i < cmdLine.length(); i++) {
            if (cmdLine.charAt(i) == '(') {
                tree.add(new Integer(i));
            }

            if (cmdLine.charAt(i) == ')') {
                tree.remove(tree.size() - 1);
            }
        }

        if (tree.size() > 0) {
            txtBefore.append(cmdLine.substring(0, 1 + ((Integer) tree.get(tree.size() - 1)).intValue()));
            cmdLine.delete(0, 1 + ((Integer) tree.get(tree.size() - 1)).intValue());
        }

        if (cmdLine.indexOf(" ") != -1) {
            txtBefore.append(cmdLine.substring(0, cmdLine.lastIndexOf(" ") + 1));
            cmdLine.delete(0, cmdLine.lastIndexOf(" ") + 1);
        }

        completionPart = cmdLine.toString();
        if (cmdLine.indexOf(".") != -1) {
            completionPart = cmdLine.toString().substring(0, cmdLine.lastIndexOf("."));
            cmdLine.delete(0, cmdLine.lastIndexOf(".") + 1);
        }

        filterPart = cmdLine.toString();
        completionCmdPos = txtBefore.length();
        filterPos = completionCmdPos + completionPart.length();
        needToUpdateCompletionModel = false;
        //

        /*
           System.out.println("===\ncmd: " + completionPart);
           System.out.println("cmdPos: " + completionCmdPos);
           System.out.println("filter: " + filterPart);
           System.out.println("filterPos: " + filterPos);
           System.out.println("before: " + txtBefore.toString());
           System.out.println("after: " + txtAfter);
         */
    }

    public void backSpace() {
        try {
            if (getCaretPosition() > 0) {
                doc.remove(getCaretPosition() - 1, 1);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void changedUpdate(DocumentEvent e) {
        needToUpdateCompletionModel = true;
    }

    public void insertUpdate(DocumentEvent e) {
        needToUpdateCompletionModel = true;
    }

    public void removeUpdate(DocumentEvent e) {
        needToUpdateCompletionModel = true;
    }

    public void getFocus() {
        uiPart.grabFocus();
    }
}
