package org.freehep.jas.plugin.console;

import java.awt.Component;
import java.awt.Point;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.freehep.application.Application;
import org.freehep.application.mdi.PageEvent;
import org.freehep.application.mdi.PageListener;
import org.freehep.swing.popup.HasPopupItems;
import org.freehep.util.commanddispatcher.BooleanCommandState;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;

class StudioConsole extends Console implements HasPopupItems, PageListener {

    private Application app;
    private ConsolePlugin plugin;
    private CommandProcessor commands = new Commands();
    private boolean selected = false;
    private final HasPopupItems morePopupItems;

    StudioConsole(Application app, ConsolePlugin plugin, HasPopupItems morePopupItems) {
        this.app = app;
        this.plugin = plugin;
        this.morePopupItems = morePopupItems;
    }

    @Override
    public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {
        app.getXMLMenuBuilder().mergePopupMenu("consolePopupMenu", menu);
        if (morePopupItems != null) {
            morePopupItems.modifyPopupMenu(menu, source, p);
        }
        return menu;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            app.getCommandTargetManager().add(commands);
        } else {
            app.getCommandTargetManager().remove(commands);
        }
    }

    @Override
    public void pageChanged(PageEvent e) {
        if (e.getID() == PageEvent.PAGESELECTED) {
            setSelected(true);
        } else if (e.getID() == PageEvent.PAGEDESELECTED) {
            setSelected(false);
        } else if (e.getID() == PageEvent.PAGECLOSED) {
            plugin.consoleIsClosed(this);
            dispose();
        }
    }

    @Override
    public void close() {
        plugin.closeMe(this);
    }

    @Override
    protected void autoShow() {
        if (!selected) {
            plugin.showMe(this);
        }
    }

    class Commands extends CommandProcessor {

        private PrintWriter pw;

        public void enableClear(CommandState state) {
            state.setEnabled(getDocument().getLength() > 0);
        }

        public void enableCloseLogFile(CommandState state) {
            state.setEnabled(pw != null);
        }

        public void enableCopyConsoleSelection(CommandState state) {
            state.setEnabled(getSelectedText() != null);
        }

        public void enableEnableLogging(BooleanCommandState state) {
            state.setSelected(isLoggingEnabled());
            state.setEnabled(pw != null);
        }

        public void enableOpenLogFile(CommandState state) {
            state.setEnabled(pw == null);
        }

        public void enableSelectAll(CommandState state) {
            state.setEnabled(getDocument().getLength() > 0);
        }

        public void onClear() {
            clear();
            setChanged();
        }

        public void onCloseLogFile() {
            setLogStream(null);
            pw.println("Log file closed on " + new Date());
            pw.close();
            pw = null;
            setChanged();
        }

        public void onCopyConsoleSelection() {
            copy();
        }

        public void onEnableLogging(boolean enabled) {
            if (enabled) {
                pw.println("Logging resumed at " + new Date());
            }
            setLoggingEnabled(enabled);
            if (!enabled) {
                pw.println("Logging paused at " + new Date());
            }
            setChanged();
        }

        public void onOpenLogFile() {
            JFileChooser chooser = new JFileChooser();

            //JavaAnalysisStudio.getApp().getUserProperties().getString(
            //"LastProgramPath", System.getProperty("user.home")));
            chooser.setDialogTitle("Open or Create Log File...");

            int returnVal = chooser.showOpenDialog(StudioConsole.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final File f = chooser.getSelectedFile();
                if (f.exists()) {
                    int rc = JOptionPane.showConfirmDialog(StudioConsole.this, "Replace existing file?", null, JOptionPane.OK_CANCEL_OPTION);
                    if (rc != JOptionPane.OK_OPTION) {
                        return;
                    }
                }

                try {
                    OutputStream logOutput = new BufferedOutputStream(new FileOutputStream(f));
                    pw = new PrintWriter(new OutputStreamWriter(logOutput));
                    pw.println("Log file created on " + new Date());
                    pw.flush();
                    setLogStream(logOutput);
                    setChanged();
                } catch (IOException eh) {
                    app.error("Couldn't open file" + f.getName(), eh);
                }
            }
        }

        public void onSelectAll() {
            selectAll();
            setChanged();
        }

        public void onProperties() {
            final ConsolePropertiesPanel cpp = new ConsolePropertiesPanel();
            cpp.setMaxScrollback(getMaxScrollback());
            cpp.setAutoShow(getAutoShow());
            String title = plugin.getPageContextForConsole(StudioConsole.this).getTitle();
            int rc = JOptionPane.showOptionDialog(app, cpp, title + " properties", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
            if (rc == JOptionPane.OK_OPTION) {
                setMaxScrollback(cpp.getMaxScrollback());
                setAutoShow(cpp.getAutoShow());
            }
        }
    }
}