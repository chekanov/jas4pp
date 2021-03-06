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
import com.artenum.jyconsole.io.InteractiveCommandLine;
import com.artenum.jyconsole.ui.CompletionWindow;

import org.python.core.PyList;
import org.python.core.PyString;

import java.awt.event.ActionEvent;

import java.lang.reflect.Method;

import java.util.ArrayList;

import javax.swing.AbstractAction;

/**
 * <pre>
 *
 *
 *   &lt;b&gt;Project ref           :&lt;/b&gt; JyConsole project
 *   &lt;b&gt;Copyright and license :&lt;/b&gt; See relevant sections
 *   &lt;b&gt;Status                :&lt;/b&gt; under development
 *   &lt;b&gt;Creation              :&lt;/b&gt; 04/03/2005
 *   &lt;b&gt;Modification          :&lt;/b&gt;
 *
 *   &lt;b&gt;Description  :&lt;/b&gt; Define the default Completion action. (ask for completion)
 *
 *
 *
 * </pre>
 *
 * <table cellpadding="3" cellspacing="0" border="1" width="100%">
 * <tr BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 * <td><b>Version number </b></td>
 * <td><b>Author (name, e-mail) </b></td>
 * <td><b>Corrections/Modifications </b></td>
 * </tr>
 * <tr>
 * <td>0.1</td>
 * <td>Sebastien Jourdain, jourdain@artenum.com</td>
 * <td>Creation</td>
 * </tr>
 * </table>
 *
 * @author Sebastien Jourdain
 * @version 0.1
 */
public class DefaultCompletionAction extends AbstractAction {
    private static boolean javaCompletionOk;
    private JyShell console;
    private ArrayList duplicate;
    private CompletionWindow compWin;
    private InteractiveCommandLine cmd;
    private Thread currentCompletionThread;

    public DefaultCompletionAction(JyShell console) {
        this.console = console;
        duplicate = new ArrayList();
        cmd = console.getInteractiveCommandLine();
    }

    private void initCompletionWindow() {
        compWin = new CompletionWindow(console);
    }

    public void actionPerformed(ActionEvent ae) {
        if (compWin == null) {
            initCompletionWindow();
        }

        if (cmd.askForDictionnary()) {
            // build dictionnary
            currentCompletionThread = new Thread(new Runnable() {
                        public void run() {

                           ArrayList listJava = new ArrayList();
                            console.getPythonInterpreter().runsource("comp_tmp=dir()");
                            PyList list = ((PyList) console.getPythonInterpreter().get("comp_tmp"));
                            list.sort();
                            int length = list.__len__();
                            for (int i = 0; i < length; i++) {
                                listJava.add(((PyString) list.__getitem__(i)).toString());
                            }

                            compWin.getModel().updateData(listJava);
                            compWin.getModel().setFilter(cmd.getFilterCmd());
                            compWin.showWindow();



                        }
                    }, "completion");
            currentCompletionThread.start();
            try {
                currentCompletionThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                javaCompletionOk = true;
                // try the java completion
                currentCompletionThread = new Thread(new Runnable() {
                            public void run() {
                               try {
                                    console.getPythonInterpreter().runsource("comp_tmp=" + cmd.getCompletionCmd()+ ".getClass().__name__");
                                    String className = console.getPythonInterpreter().get("comp_tmp").toString();
                                    // System.out.println(className);
                                    Method[] methodList = Class.forName(className).getMethods();
                                    compWin.getModel().updateData(methodList);
                                    compWin.getModel().setFilter(cmd.getFilterCmd());
                                    javaCompletionOk = compWin.getModel().getSize() > 0;
                                } catch (Exception error) {
                                    compWin.getModel().updateData(new ArrayList());
                                    javaCompletionOk = false;
                                }
                            }
                        }, "completion");






                currentCompletionThread.start();
                try {
                    currentCompletionThread.join();
                } catch (InterruptedException e) {}

                if (!javaCompletionOk) {
                    // If java completion didn't work build Python completion
                    try {
                        currentCompletionThread = new Thread(new Runnable() {
                                    public void run() {


                                      try {
                                            console.getPythonInterpreter().runsource("comp_tmp=dir(" + cmd.getCompletionCmd() + ")");
                                            PyList list = ((PyList) console.getPythonInterpreter().get("comp_tmp"));
                                            list.sort();
                                            int length = list.__len__();
                                            ArrayList listJava = new ArrayList();
                                            for (int i = 0; i < length; i++) {
                                                listJava.add(((PyString) list.__getitem__(i)).toString());
                                            }

                                            try {
                                                console.getPythonInterpreter().runsource("comp_tmp=" + cmd.getCompletionCmd() + ".__class__.__name__");
                                                String className = ((PyString) console.getPythonInterpreter().get("comp_tmp")).internedString();
                                                console.getPythonInterpreter().runsource("comp_tmp=dir(" + className + ")");
                                                list = ((PyList) console.getPythonInterpreter().get("comp_tmp"));
                                                list.sort();
                                                length = list.__len__();
                                                for (int i = 0; i < length; i++) {
                                                    listJava.add(((PyString) list.__getitem__(i)).toString());
                                                }
                                            } catch (Exception exc) {}

                                            compWin.getModel().updateData(listJava);
                                            compWin.getModel().setFilter(cmd.getFilterCmd());
                                        } catch (Exception exc) {
                                            compWin.getModel().updateData(new ArrayList());
                                        }
                                    }
                                }, "completion");



                        currentCompletionThread.start();
                        currentCompletionThread.join();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                compWin.showWindow();
            } catch (Exception e) {
                // Python completion
                //console.getOutputComponent().appendText("\n>>> " +
                // console.getInputComponent().getText(),
                // JyConsole.STYLE_DEFAULT);
                //console.getInterpreter().runsource("dir(" +
                // cmd.getCompletionCommand() + ")");
            }
        }

        console.updateScrollPosition();
    }
}
