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
import com.artenum.jyconsole.io.InteractiveCommandLine;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
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
public class CompletionWindow extends JWindow implements KeyListener, ListSelectionListener, MouseMotionListener, MouseListener, FocusListener {
    private JList list;
    private MethodeModel model;
    private JyShell console;
    private JScrollPane scroll;
    private InteractiveCommandLine cmdOrigine;
    private int maxHeight = -1;
    private JPanel resize;
    private Dimension size;

    public CompletionWindow(JyShell console) {
        super(ToolBox.getParentFrame(console));
        this.console = console;
        resize = new JPanel();
        resize.setMinimumSize(new Dimension(10, 10));
        resize.addMouseMotionListener(this);
        resize.addMouseListener(this);
        model = new MethodeModel();
        list = new JList(model);
        list.addFocusListener(this);
        list.addListSelectionListener(this);
        scroll = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setCorner(JScrollPane.LOWER_RIGHT_CORNER, resize);
        getContentPane().add(scroll);
        setSize(300, 100);
        list.addKeyListener(this);
        list.getInputMap().clear();
        scroll.getInputMap().clear();
        maxHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        cmdOrigine = console.getInteractiveCommandLine();
    }

    public MethodeModel getModel() {
        return model;
    }

    public void showWindow() {
        Point p = console.getCompletionWindowLocation();

        // translateVerticaly if not enought space
        if ((p.y + getHeight()) > Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
            p.y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - getHeight());
        }

        setLocation(p);
        setVisible(true);
        if (System.getProperty("os.name").toLowerCase().indexOf("linux") == -1) {
            showOnWindows();
        } else {
            if (System.getProperty("java.vm.version").startsWith("1.4")) {
                showOnLinux();
            } else {
                showOnLinux15();
            }
        }
    }

    private void showOnWindows() {
        if (list.getModel().getSize() > 0) {
            list.setSelectedIndex(0);
        } else {
            list.grabFocus();
        }
    }

    private void showOnLinux15() {
        if (list.getModel().getSize() > 0) {
            list.setSelectedIndex(0);
        }

        list.grabFocus();
    }

    private void showOnLinux() {
        // Fix Focus Error on linux 
        for (int i = 0; i < 3; i++)
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (!list.hasFocus() || !hasFocus()) {
                            setVisible(true);
                            requestFocus();
                            list.grabFocus();
                            if (list.getModel().getSize() > 0) {
                                list.setSelectedIndex(0);
                            }
                        }
                    }
                });

    }

    private void selectMethodeWhichStartWith(String start) {
        model.setFilter(start);
        list.setSelectedIndex(0);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            String selectedMethode = ((String) list.getSelectedValue());
            if (cmdOrigine.askForDictionnary()) {
                if (cmdOrigine.getCompletionCmd().equals(cmdOrigine.getFilterCmd())) {
                    cmdOrigine.setCompletionCmd(selectedMethode);
                } else {
                    cmdOrigine.setFilteredCmd(selectedMethode);
                }
            } else if ((selectedMethode != null) && selectedMethode.toLowerCase().startsWith(cmdOrigine.getFilterCmd().toLowerCase()) &&
                    !selectedMethode.equals(cmdOrigine.getFilterCmd())) {
                if ((selectedMethode.indexOf("(") == -1) || selectedMethode.endsWith("()")) {
                    cmdOrigine.setFilteredCmd(selectedMethode);
                } else {
                    cmdOrigine.setFilteredCmd(selectedMethode.substring(0, selectedMethode.lastIndexOf("(") + 1));
                }
            }

            setVisible(false);
            cmdOrigine.getFocus();
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // Do nothing
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            setVisible(false);
            cmdOrigine.getFocus();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (model.getSize() > 0) {
                list.setSelectedIndex((list.getSelectedIndex()) % model.getSize());
            }
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (model.getSize() > 0) {
                list.setSelectedIndex((model.getSize() + list.getSelectedIndex()) % model.getSize());
            }
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (cmdOrigine.getFilterCmd().length() > 0) {
                cmdOrigine.backSpace();
                selectMethodeWhichStartWith(cmdOrigine.getFilterCmd());
            } else {
                setVisible(false);
                cmdOrigine.getFocus();
            }
        } else {
            if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
                cmdOrigine.append("" + e.getKeyChar());
                selectMethodeWhichStartWith(cmdOrigine.getFilterCmd());
            }
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public void valueChanged(ListSelectionEvent e) {
        list.ensureIndexIsVisible(list.getSelectedIndex());
    }

    public void mouseDragged(MouseEvent e) {
        resize((int) (size.getWidth() + e.getX()), (int) (size.getHeight() + e.getY()));
    }

    public void mouseMoved(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {
        validate();
    }

    public void mousePressed(MouseEvent e) {
        size = getSize();
    }

    public void focusGained(FocusEvent e) {}

    public void focusLost(FocusEvent e) {
        setVisible(false);
    }

    public class MethodeModel extends AbstractListModel {
        private ArrayList data;
        private ArrayList filteredData;
        private String filter;

        public MethodeModel() {
            data = new ArrayList();
            filteredData = new ArrayList();
        }

        public Object getElementAt(int index) {
            return (filter != null) ? filteredData.get(index) : data.get(index);
        }

        public int getSize() {
            return (filter != null) ? filteredData.size() : data.size();
        }

        public void setFilter(String filter) {
            if ((filter == null) || ((filter != null) && (filter.length() == 0))) {
                this.filter = null;
            } else {
                this.filter = filter;
                filteredData.clear();
                String currentLine;
                for (Iterator i = data.iterator(); i.hasNext();) {
                    currentLine = (String) i.next();
                    if (currentLine.toLowerCase().startsWith(filter.toLowerCase())) {
                        filteredData.add(currentLine);
                    }
                }
            }

            fireContentsChanged(this, 0, getSize());
        }

        public void updateData(Collection list) {
            Object[] sort = list.toArray();
            Arrays.sort(sort);
            data.clear();
            for (int i = 0; i < sort.length; i++) {
                data.add(sort[i].toString());
            }

            setFilter(null);
        }

        public void updateData(Method[] methodList) {
            data.clear();
            StringBuffer buffer;
            for (int i = 0; i < methodList.length; i++) {
                buffer = new StringBuffer(methodList[i].getName());
                buffer.append("( ");
                for (int j = 0; j < methodList[i].getParameterTypes().length; j++) {
                    buffer.append(" ");
                    buffer.append(methodList[i].getParameterTypes()[j].getName());
                    buffer.append(" ,");
                }

                data.add(buffer.substring(0, buffer.length() - 1) + ")");
            }

            Object[] sort = data.toArray();
            Arrays.sort(sort);
            data.clear();
            for (int i = 0; i < sort.length; i++) {
                data.add(sort[i]);
            }

            setFilter(null);
        }
    }
}
