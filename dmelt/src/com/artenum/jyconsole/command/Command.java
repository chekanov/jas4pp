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

import com.artenum.jyconsole.python.JInteractiveInterpreter;
import org.python.core.PyException;
import java.io.File;
import javax.swing.JOptionPane;

/**
 * <pre>
 * <b>Project ref           :</b> JyConsole project
 * <b>Copyright and license :</b> See relevant sections
 * <b>Status                :</b> under development
 * <b>Creation              :</b> 23/06/2006
 * <b>Modification          :</b>
 *
 * <b>Description  :</b> A command is either a single line of Jython or a file of commands. The
 *                        isRunning() method is a condition variable so you can synchronize on
 *                        completion of the command.
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
public class Command implements Runnable {
    private JInteractiveInterpreter pythonInterpreter;
    private File commandFile;
    private String command;
    private boolean isRunning = false;
    private Thread myThread;
    private boolean full=false;

    /**
     * Run a script from a file.
     *
     * @param pythonInterpreter
     * @param commandFile
     */
    public Command(JInteractiveInterpreter pythonInterpreter, File commandFile) {
        this.pythonInterpreter = pythonInterpreter;
        this.commandFile = commandFile;
        full=false;
    }

    /**
     * Run a single line of Jython.
     *
     * @param pythonInterpreter
     * @param command
     */
    public Command(JInteractiveInterpreter pythonInterpreter, String command) {
        this.pythonInterpreter = pythonInterpreter;
        this.command = command;
        full=false;
    }


   /**
     * Run a single line of Jython.
     * When full is true, run complete command. 
     * @param pythonInterpreter
     * @param command. 
     */
    public Command(JInteractiveInterpreter pythonInterpreter, String command, boolean full) {
        this.pythonInterpreter = pythonInterpreter;
        this.command = command;
        this.full=full;
    }

    /**
     * Is the command currently running? If you synchronize on the object you
     * can use this method as a condition variable and be notified when the command
     * finishes.
     *
     * i.e. (without exception handling...)
     *
     * synchronized (command)
     * {
     *   if (command.isRunning())
     *   {
     *      command.wait() ;
     *   }
     * }
     *
     * @return
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * If the command is running stop it.
     */
    public void stop() {
        synchronized (this) {
            if (isRunning()) {
                myThread.interrupt();
            }
        }
    }

    /**
     * Actually performs the command.
     */
    public void run() {
        try {
            synchronized (this) {
                isRunning = true;
                myThread = Thread.currentThread();
            }


            if (commandFile != null) {
                try {
                pythonInterpreter.execfile(commandFile.getAbsolutePath());
                 } catch (PyException e) {
                String serror=e.toString();
                pythonInterpreter.exec("print \'"+serror+"\'");
                ErrorMessage(serror);
                }
                }


            if (command != null && full==false) {
                pythonInterpreter.runsource(command);
            }

            if (command != null && full==true) {
                // System.out.println("Running..");
                try {
                  pythonInterpreter.exec(command);
                } catch (PyException e) {
                // System.out.println("OOOO");
                String serror=e.toString();
                // pythonInterpreter.exec("print \'"+serror+"\'");
                ErrorMessage(serror);
                }

            }


        } finally {
            synchronized (this) {
                isRunning = false;
                notifyAll();
            }
        }
    }

    public String toString() {
        if (commandFile != null) {
            return commandFile.getAbsolutePath();
        }

        return command;
    }





  /**
         * Generate error message
         * 
         * @param a
         *            Message
         */

        private void ErrorMessage(String a) {

                JOptionPane dialogError = new JOptionPane();
                JOptionPane.showMessageDialog(dialogError, a, "Error",
                                JOptionPane.ERROR_MESSAGE);
        }






}
