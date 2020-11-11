package org.freehep.jas.plugin.basic;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.freehep.application.ApplicationEvent;
import org.freehep.application.PropertyUtilities;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.application.studio.StudioListener;
import org.freehep.jas.JAS3FileOpener;
import org.freehep.jas.services.FileHandler;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.jas.services.TextEditorService;
import org.freehep.swing.AllSupportedFileFilter;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.openide.util.Lookup;

/**
 * The file opener is responsible for opening files within JAS3.
 * It does so by finding a FileHandler to delegate to.
 * FileOpener also handles dropped files and recent file list.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: FileOpener.java 13914 2011-11-18 22:52:49Z tonyj $
 */
public class FileOpener extends Plugin implements StudioListener, ActionListener, PreferencesTopic {

    private RecentFileList recentFiles = new RecentFileList("recentFiles");
    private RecentFilesMenu recentFilesMenu = new RecentFilesMenu("Recent Files", recentFiles);
    private FileHelper fileHelper;
    private boolean restoreLastDirectoryUsed;
    private String defaultDirectory;

    @Override
    public void init() throws org.xml.sax.SAXException, java.io.IOException {
        Studio app = getApplication();
        recentFiles.load(app.getUserProperties());
        recentFilesMenu.setEnabled();

        app.getLookup().add(this);

        XMLMenuBuilder builder = app.getXMLMenuBuilder();
        URL xml = getClass().getResource("FileOpener.menus");
        builder.build(xml);
        addMenu(recentFilesMenu, 100910);

        app.getCommandTargetManager().add(new Commands());

        app.getEventSender().addEventListener(this, ApplicationEvent.class);

        Component dropTarget = app;
        while (dropTarget.getParent() != null) {
            dropTarget = dropTarget.getParent();
        }
        addDropTargetFileOpener(dropTarget);

        fileHelper = new FileHelper(JAS3FileOpener.class);
        fileHelper.addActionListener(this);
        fileHelper.start();
    }

    public void addDropTargetFileOpener(Component c) {
        new DropTarget(c, new FileDropper());
    }

    @Override
    public void handleEvent(EventObject event) {
        if (event instanceof ApplicationEvent) {
            ApplicationEvent ae = (ApplicationEvent) event;
            if (ae.getID() == ApplicationEvent.INITIALIZATION_COMPLETE) {
                // Open any files specified on the command line
                String[] files = ae.getApplication().getCommandLine().getArguments();
                for (int i = 0; i < files.length; i++) {
                    try {
                        openFile(getFileHandlers(), new File(files[i]));
                    } catch (Throwable x) {
                        x.printStackTrace();
                        getApplication().error("Could not open file: " + files[i], x);
                    }
                }
                // Or any files specified with -open option
                String file = ae.getApplication().getCommandLine().getOption("open");
                if (file != null) {
                    try {
                        openFile(getFileHandlers(), new File(file));
                    } catch (Throwable x) {
                        x.printStackTrace();
                        getApplication().error("Could not open file: " + file, x);
                    }
                }
            } else if (ae.getID() == ApplicationEvent.APPLICATION_EXITING) {
                fileHelper.stop();
                recentFiles.save(ae.getApplication().getUserProperties());
            }
        }
    }

    private Lookup.Result getFileHandlers() {
        Lookup.Template template = new Lookup.Template(FileHandler.class);
        return getApplication().getLookup().lookup(template);
    }

    private void openFile(Lookup.Result result, File file) throws IOException {
        List acceptable = new ArrayList();
        for (Iterator i = result.allItems().iterator(); i.hasNext();) {
            Lookup.Item item = (Lookup.Item) i.next();
            FileHandler s = (FileHandler) item.getInstance();
            if (s.accept(file)) {
                acceptable.add(item);
            }
        }
        if (acceptable.size() == 1) {
            ((FileHandler) ((Lookup.Item) acceptable.get(0)).getInstance()).openFile(file);
            recentFiles.add(file);
            recentFilesMenu.setEnabled();
        } else if (acceptable.size() > 1) {
            Object selectedValue = JOptionPane.showInputDialog(getApplication(),
                    "Select plugin to open file", "Select Plugin",
                    JOptionPane.QUESTION_MESSAGE, null,
                    acceptable.toArray(), acceptable.get(0));

            if (selectedValue != null) {
                ((FileHandler) ((Lookup.Item) selectedValue).getInstance()).openFile(file);
                recentFiles.add(file);
                recentFilesMenu.setEnabled();
            }
        } else {
            throw new IllegalArgumentException("No file handler found for " + file);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String fileName = e.getActionCommand();
        File f = new File(fileName);
        try {
            openFile(getFileHandlers(), f);
        } catch (IOException x) {
            getApplication().error("Error opening " + fileName, x);
        }
    }

    boolean isRestoreLastDirectoryUsed() {
        return PropertyUtilities.getBoolean(getApplication().getUserProperties(), "fileOpener.restoreLastDirectoryUsed", true);
    }

    void setRestoreLastDirectoryUsed(boolean restoreLastDirectoryUsed) {
        getApplication().getUserProperties().setProperty("fileOpener.restoreLastDirectoryUsed", String.valueOf(restoreLastDirectoryUsed));
    }

    String getDefaultDirectory() {
        return getApplication().getUserProperties().getProperty("fileOpener.defaultDirectory", "{user.home}");
    }

    void setDefaultDirectory(String defaultDirectory) {
        getApplication().getUserProperties().setProperty("fileOpener.defaultDirectory", defaultDirectory);
    }

    @Override
    public boolean apply(JComponent panel) {
        return ((FileOpenPreferences) panel).apply();
    }

    @Override
    public JComponent component() {
        return new FileOpenPreferences(this);
    }

    @Override
    public String[] path() {
        return "General/Files".split("/");
    }

    public class Commands extends CommandProcessor {

        public void onOpenFile() throws IOException {
            Studio app = getApplication();

            String path = getDefaultDirectory();
            if (isRestoreLastDirectoryUsed()) {
                String last = app.getUserProperties().getProperty("fileOpener.lastDirectoryUsed");
                if (last != null) {
                    path = last;
                }
            }
            JFileChooser dlg = new JFileChooser(path);
            dlg.setDialogTitle("Open File...");

            AllSupportedFileFilter all = new AllSupportedFileFilter();

            // Look for any FileHandler services
            Lookup.Result result = getFileHandlers();
            for (Iterator i = result.allInstances().iterator(); i.hasNext();) {
                FileHandler s = (FileHandler) i.next();
                FileFilter ff = s.getFileFilter();
                all.add(ff);
                dlg.addChoosableFileFilter(ff);
            }
            dlg.setFileFilter(all);

            if (dlg.showOpenDialog(app) == JFileChooser.APPROVE_OPTION) {
                File f = dlg.getSelectedFile();
                try {
                    try {
                        openFile(result, f);
                        app.getUserProperties().setProperty("fileOpener.lastDirectoryUsed", f.getParent());
                    } catch (IllegalArgumentException t) {
                        String[] options = {"Yes", "No, open with Text Editor", "Cancel"};
                        int rc = JOptionPane.showOptionDialog(app, t.getMessage() + "\nSearch on web for plugin to support this file?", "Open File...",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                        if (rc == JOptionPane.YES_OPTION) {
                            JOptionPane.showMessageDialog(app, "Feature not implemented");
                        } else if (rc == JOptionPane.NO_OPTION) {
                            TextEditorService te = (TextEditorService) app.getLookup().lookup(TextEditorService.class);
                            if (te == null) {
                                app.error("No text editor installed");
                            } else {
                                te.show(f, "text/plain");
                                app.getUserProperties().setProperty("fileOpener.lastDirectoryUsed", f.getParent());
                            }
                        }
                    }
                } catch (IOException x) {
                    app.error("Error opening file", x);
                }
            }
        }
    }

    class FileDropper extends DropTargetAdapter {

        @Override
        public void drop(DropTargetDropEvent e) {
            Lookup.Result result = getFileHandlers();
            DropTargetContext context = e.getDropTargetContext();
            try {
                e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                Transferable t = e.getTransferable();
                DataFlavor uriList = new DataFlavor("text/uri-list; class=java.io.Reader; charset=Unicode");
                if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
                    if (data instanceof java.util.List) {
                        java.util.List list = (java.util.List) data;
                        for (int k = 0; k < list.size(); k++) {
                            Object dataLine = list.get(k);
                            if (dataLine instanceof File) {
                                openFile(result, (File) dataLine);
                            }
                        }
                    }
                    context.dropComplete(true);
                } else if (t.isDataFlavorSupported(uriList)) {
                    BufferedReader reader = new BufferedReader(uriList.getReaderForText(t));
                    try {
                        for (;;) {
                            String uri = reader.readLine();
                            if (uri == null) {
                                break;
                            }
                            openFile(result, new File(new URI(uri)));
                        }
                    } finally {
                        reader.close();
                    }
                    context.dropComplete(true);
                } else {
                    context.dropComplete(false);
                    getApplication().error("Unsupported drop type");
                }
            } catch (Exception ex) {
                context.dropComplete(false);
                getApplication().error("Error during Drag and Drop", ex);
            }
        }
    }

    private static class RecentFileList {

        private List list = new LinkedList();
        private int maxLength = 8;
        private String key;

        RecentFileList(String key) {
            this.key = key;
        }

        void add(File file) {
            int pos = list.indexOf(file);
            if (pos == 0) {
                return;
            } else if (pos > 0) {
                list.remove(pos);
                list.add(0, file);
            } else {
                list.add(0, file);
            }
            if (list.size() > maxLength) {
                for (Iterator i = list.listIterator(maxLength); i.hasNext();) {
                    i.next();
                    i.remove();
                }
            }
        }

        void load(Properties props) {
            int len = PropertyUtilities.getInteger(props, key + "-length", 0);
            for (int i = 0; i < len; i++) {
                list.add(new File(props.getProperty(key + "-" + i)));
            }
        }

        void save(Properties props) {
            props.setProperty(key + "-length", String.valueOf(list.size()));
            int i = 0;
            for (Iterator iter = list.iterator(); iter.hasNext(); i++) {
                File file = (File) iter.next();
                props.put(key + "-" + i, file.getAbsolutePath());
            }
        }

        int size() {
            return list.size();
        }

        Iterator iterator() {
            return list.iterator();
        }
    }

    private class RecentFilesMenu extends JMenu implements ActionListener {

        private RecentFileList list;

        public RecentFilesMenu(String name, RecentFileList list) {
            super(name);
            this.list = list;
        }

        public final void setEnabled() {
            setEnabled(list.size() > 0);
        }

        @Override
        protected void fireMenuSelected() {
            Iterator iter = list.iterator();
            for (int i = 0; iter.hasNext(); i++) {
                File file = (File) iter.next();
                JMenuItem item = new JMenuItem(String.valueOf(i) + " " + file.getAbsolutePath());
                item.setActionCommand(file.getAbsolutePath());
                item.setMnemonic('0' + (char) i);
                item.addActionListener(this);
                add(item);
            }
            super.fireMenuSelected();
        }

        @Override
        protected void fireMenuDeselected() {
            super.fireMenuDeselected();
            removeAll();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();
            File file = new File(item.getActionCommand());
            try {
                openFile(getFileHandlers(), file);
            } catch (Throwable x) {
                getApplication().error("Error opening " + file, x);
            }
        }
    }
}