/**
 * Project        : ArtTk
 * Copyright      : (c) Artenum SARL, 24 rue Louis Blanc
 *                  75010, Paris, France 2009-2010
 *                  http://www.artenum.com
 *                  All copyright and trademarks reserved.
 * Email          : contact@artenum.com
 * Licence        : cf. LICENSE.txt
 * Developed By   : Artenum SARL
 * Authors        : Sebastien Jourdain      (jourdain@artenum.com)
 *                  Benoit thiebault        (thiebault@artenum.com)
 *                  Jeremie Turbet (JeT)    (turbet@artenum.com)
 *                  Julien Forest           (j.forest@artenum.com)
 * Created        : 11 Nov. 2005
 * Modified       : 23 Aug. 2010
 */
package com.artenum.tk.ui.xmlgui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;

public class DesktopManager extends JTabbedPane {
	private static final long serialVersionUID = 487990680719191308L;
	private MoveInternalFramePopup moveToDesktopPopup;
    private PopupListener popupListener;
    private ArrayList<String> desktopList;

    public DesktopManager() {
        super();
        desktopList = new ArrayList<String>();
        moveToDesktopPopup = new MoveInternalFramePopup();
        popupListener = new PopupListener();
    }

    public void addDesktop(String deskName) {
        addTab(deskName, new JScrollPane(new JDesktopPane()));
        desktopList.add(deskName);
    }

    public void removeDesktop(String deskName) {
        removeTabAt(indexOfTab(deskName));
        desktopList.remove(deskName);
    }

    public JDesktopPane getCurrentDesktop() {
        return getDesktopFromTabContent(getSelectedComponent());
    }

    public JDesktopPane getDesktop(String deskName) {
        return getDesktopFromTabContent(getComponentAt(indexOfTab(deskName)));
    }

    public JDesktopPane getDesktop(int index) {
        return getDesktopFromTabContent(getComponentAt(index));
    }

    public JInternalFrame createMultiDesktopInternalFrame(String title, boolean resizable, boolean closeable, boolean maximizable, boolean iconifiable) {
        JInternalFrame internalFrame = new JInternalFrame(title, resizable, closeable, maximizable, iconifiable);
        internalFrame.addMouseListener(popupListener);
        return internalFrame;
    }

    public ArrayList<String> getDesktopNameList() {
        return desktopList;
    }

    private JDesktopPane getDesktopFromTabContent(Component tabContent) {
        return (JDesktopPane) ((JViewport) ((JScrollPane) tabContent).getComponent(0)).getComponent(0);
    }

    @SuppressWarnings("unchecked")
    private class MoveInternalFramePopup extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 1635335641054074108L;
		private JInternalFrame currentFrame;
        private ArrayList<String> desktopList;

		public MoveInternalFramePopup() {
            super();
            desktopList = (ArrayList<String>) getDesktopNameList().clone();
            loadDesktopList();
        }

        public void show(Component invoker, int x, int y) {
            loadDesktopList();
            super.show(invoker, x, y);
        }

        public void setInternalFrame(JInternalFrame currentFrame) {
            this.currentFrame = currentFrame;
        }

        private void loadDesktopList() {
            if (!desktopList.equals(getDesktopNameList())) {
                desktopList = (ArrayList<String>) getDesktopNameList().clone();
                removeAll();
                for (Iterator<String> i = getDesktopNameList().iterator(); i.hasNext();) {
                    JMenuItem item = new JMenuItem((String) i.next());
                    item.addActionListener(this);
                    add(item);
                }
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (getDesktop(desktopList.indexOf(e.getActionCommand())).equals(currentFrame.getDesktopPane())) {
                return;
            }

            JDesktopPane oldDesk = currentFrame.getDesktopPane();
            ((JDesktopPane) getDesktop(desktopList.indexOf(e.getActionCommand()))).add(currentFrame);
            currentFrame.getDesktopPane().validate();
            currentFrame.getDesktopPane().repaint();
            try {
                currentFrame.setSelected(false);
            } catch (PropertyVetoException e1) {}

            oldDesk.repaint();
        }
    }

    class PopupListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                moveToDesktopPopup.setInternalFrame((JInternalFrame) e.getComponent());
                moveToDesktopPopup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
