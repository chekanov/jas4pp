package org.freehep.jas.extension.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoManager;
import org.freehep.application.PropertyUtilities;
import org.freehep.application.mdi.ManagedPage;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.studio.EventSender;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.jas.event.EditorPopupEvent;
import org.freehep.jas.plugin.xmlio.XMLPluginIO;
import org.freehep.jas.services.FileHandler;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.jas.services.TextEditor;
import org.freehep.jas.services.TextEditorService;
import org.freehep.swing.ExtensionFileFilter;
import org.freehep.swing.popup.HasPopupItems;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.images.ImageHandler;
import org.freehep.xml.io.XMLIOManager;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.jdom.Element;
import org.xml.sax.SAXException;

/**
 * A simple editor to use until we get something better (jEdit, netbeans?)
 * built in.
 * @author tonyj
 * @version $Id: SimpleEditor.java 13874 2011-09-20 00:51:24Z tonyj $
 */
public class SimpleEditor extends Plugin implements TextEditorService, FileHandler, XMLPluginIO, PreferencesTopic, WindowFocusListener {

    private static Icon defIcon = ImageHandler.getIcon("/toolbarButtonGraphics/general/Edit16.gif", SimpleEditor.class);
    private GlobalCommands global = new GlobalCommands();
    private Map mimeTypes = new HashMap();
    private boolean listenerInstalled = false;
    private Properties userProperties;
    private static String[] textFields = {"Code", "Line Number"};
    private static String[] defaultColor = {"-16777216", "-8372160"};
    private static String[] defaultSize = {"12", "10"};
    private static String[] defaultType = {"Dialog", "Dialog"};
    private static String[] defaultStyle = {"Plain", "Italic"};
    private static String pageType = "Editor";

    @Override
    public TextEditor getCurrentEditor() {
        PageContext context = getApplication().getPageManager().getSelectedPage();
        if (context != null) {
            Component c = context.getPage();
            if (c instanceof JScrollPane) {
                c = ((JScrollPane) c).getViewport().getView();
            }
            if (c instanceof TextEditor) {
                return (TextEditor) c;
            }
        }
        return null;
    }

    @Override
    public FileFilter getFileFilter() {
        return new ExtensionFileFilter("txt", "Text File");
    }

    @Override
    public Icon getIconForMimeType(String mimeType) {
        return (Icon) mimeTypes.get(mimeType);
    }

    //*************************************//
    // Methods for the FileHandler service //
    //*************************************//
    @Override
    public boolean accept(File file) throws IOException {
        return file.getName().endsWith(".txt");
    }

    @Override
    public void addMimeType(String mimeType, Icon icon) {
        mimeTypes.put(mimeType, icon);
    }

    @Override
    public List editors() {
        List l = new ArrayList();
        List pages = getApplication().getPageManager().pages();
        for (Iterator i = pages.iterator(); i.hasNext();) {
            PageContext context = (PageContext) i.next();
            Component c = context.getPage();

            if (c instanceof JScrollPane) {
                c = ((JScrollPane) c).getViewport().getView();
            }
            if (c instanceof TextEditor) {
                l.add(c);
            }
        }
        return l;
    }

    @Override
    public void openFile(File file) throws IOException {
        show(file, "text/plain");
    }

    private File getLastDir() {
        String file = userProperties.getProperty("editor.lastDir", "{user.home}");
        return new File(file);
    }

    private void setLastDir(File dir) {
        userProperties.setProperty("editor.lastDir", dir.getAbsolutePath());
    }

    @Override
    public int restore(int level, XMLIOManager manager, Element el) {
        switch (level) {
            case RESTORE_DATA:
                try {
                    List editAreas = el.getChildren("editArea");
                    for (int i = 0; i < editAreas.size(); i++) {
                        Element editAreaEl = (Element) editAreas.get(i);
                        String fileName = editAreaEl.getAttributeValue("file");
                        String mimeType = editAreaEl.getAttributeValue("mime");
                        if (fileName != null) {
                            File file = new File(fileName);
                            show(file, mimeType);
                        } else {
                            Element urlEl = editAreaEl.getChild("URL");
                            String host = urlEl.getAttributeValue("host");
                            String protocol = urlEl.getAttributeValue("protocol");
                            String file = urlEl.getAttributeValue("file");
                            URL url = new URL(protocol, host, file);
                            show(url, mimeType);
                        }
                    }
                } catch (Throwable t) {
                    //FIXME: Do we need this? If so we should at least keep the original error as the "cause"
                    // of the runtime exception.
                    throw new RuntimeException(t.getMessage());
                }
                return RESTORE_DONE;

            default:
                throw new IllegalArgumentException("Illegal level " + level + ". Please report this problem");
        }
    }

    @Override
    public void save(XMLIOManager manager, Element el) {
        for (Iterator i = editors().iterator(); i.hasNext();) {
            EditArea editArea = (EditArea) i.next();
            Element fileEl = new Element("editArea");
            File file = editArea.getFile();
            if (file != null) {
                fileEl.setAttribute("file", file.getAbsolutePath());
            }
            URL url = editArea.getURL();
            if (url != null) {
                //FIXME: Why not just use urlEl.toExternalForm()?
                Element urlEl = new Element("URL");
                urlEl.setAttribute("file", url.getFile());
                urlEl.setAttribute("protocol", url.getProtocol());
                urlEl.setAttribute("host", url.getHost());
                fileEl.addContent(urlEl);
            }
            if (url != null || file != null) {
                fileEl.setAttribute("mime", editArea.getMimeType());
                el.addContent(fileEl);
            }
        }
    }

    private String makeType(String mimeType) {
        String tmpType = mimeType.substring(mimeType.indexOf("/") + 1);
        tmpType = tmpType.toUpperCase().substring(0, 1) + tmpType.toLowerCase().substring(1);
        return tmpType + " " + pageType;
    }

    @Override
    public void show(String text, String mimeType, String title) {
        if (title == null) {
            title = "Untitled";
        }
        getApplication().getPageManager().openPage(new JScrollPane(new EditArea(getApplication(), mimeType, text)), title, getIconForMimeType(mimeType), makeType(mimeType));
    }

    @Override
    public void show(File file, String mimeType) throws IOException {
        EditArea text = new EditArea(getApplication(), mimeType, file);
        getApplication().getPageManager().openPage(new JScrollPane(text), file.getName(), getIconForMimeType(mimeType), makeType(mimeType));
    }

    @Override
    public void show(URL url, String mimeType) throws IOException {
        String title = url.getFile();
        int pos = title.lastIndexOf("/");
        if (pos > 0) {
            title = title.substring(pos + 1);
        }

        EditArea text = new EditArea(getApplication(), mimeType, url);
        getApplication().getPageManager().openPage(new JScrollPane(text), title, getIconForMimeType(mimeType), makeType(mimeType));
    }

    @Override
    public void show(Reader reader, String mimeType, String title) throws IOException {
        EditArea text = new EditArea(getApplication(), mimeType, reader);
        if (title == null) {
            title = "Untitled";
        }
        getApplication().getPageManager().openPage(new JScrollPane(text), title, getIconForMimeType(mimeType), makeType(mimeType));
    }

    @Override
    protected void init() throws SAXException, IOException {
        Studio app = getApplication();
        app.getLookup().add(this);

        XMLMenuBuilder builder = app.getXMLMenuBuilder();
        URL xml = getClass().getResource("SimpleEditor.menus");
        builder.build(xml);

        app.getCommandTargetManager().add(global);

        app.addToolBar(builder.getToolBar("editToolBar"), "Edit Toolbar");
        app.addToolBar(builder.getToolBar("fileToolBar"), "File Toolbar");
        userProperties = app.getUserProperties();
    }

    private void show(String text, String mimeType, String title, Icon icon) {
        getApplication().getPageManager().openPage(new JScrollPane(new EditArea(getApplication(), mimeType, text)), title, icon, makeType(mimeType));
    }

    @Override
    public boolean apply(JComponent panel) {
        ((FontPropertyDialog) panel).apply();
        for (Iterator i = editors().iterator(); i.hasNext();) {
            EditArea editArea = (EditArea) i.next();
            editArea.setFontAndColor(getFont(textFields[0]), getFontColor(textFields[0]));
            LineNumberBorder lnb = (LineNumberBorder) editArea.getBorder();
            lnb.setFontAndColor(getFont(textFields[1]), getFontColor(textFields[1]));
        }
        return true;
    }

    @Override
    public JComponent component() {
        return new FontPropertyDialog(this, textFields);
    }

    @Override
    public String[] path() {
        return new String[]{"SimpleEditor"};
    }

    void setFontSize(String text, String fontSize) {
        userProperties.setProperty(text + "FontSize", fontSize);
    }

    void setFontType(String text, String fontType) {
        userProperties.setProperty(text + "FontType", fontType);
    }

    void setFontStyle(String text, String fontStyle) {
        userProperties.setProperty(text + "FontStyle", fontStyle);
    }

    void setColor(String text, Color color) {
        String colorSt = String.valueOf(color.getRGB());
        userProperties.setProperty(text + "FontColor", colorSt);
    }

    String getFontType(String text) {
        return PropertyUtilities.getString(userProperties, text + "FontType", defaultType[textFieldIndex(text)]);
    }

    String getFontSize(String text) {
        return PropertyUtilities.getString(userProperties, text + "FontSize", defaultSize[textFieldIndex(text)]);
    }

    String getFontStyle(String text) {
        return PropertyUtilities.getString(userProperties, text + "FontStyle", defaultStyle[textFieldIndex(text)]);
    }

    Color getFontColor(String text) {
        String colorSt = PropertyUtilities.getString(userProperties, text + "FontColor", defaultColor[textFieldIndex(text)]);
        return new Color(Integer.valueOf(colorSt).intValue());
    }

    boolean isCheckForFileChanges() {
        return PropertyUtilities.getBoolean(userProperties, "checkForFileChanges", true);
    }

    void setCheckForFileChanges(boolean value) {
        PropertyUtilities.setBoolean(userProperties, "checkForFileChanged", value);
    }

    private Font getFont(String textField) {
        String fontType = getFontType(textField);
        int fontSize = Integer.valueOf(getFontSize(textField)).intValue();
        Font font = new Font(fontType, Font.PLAIN, fontSize);
        String fontStyle = getFontStyle(textField);
        if (fontStyle.indexOf("Bold") != -1) {
            font = font.deriveFont(Font.BOLD);
        }
        if (fontStyle.indexOf("Italic") != -1) {
            font = font.deriveFont(Font.ITALIC);
        }
        return font;
    }

    private int textFieldIndex(String textField) {
        for (int i = 0; i < textFields.length; i++) {
            if (textField.equals(textFields[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid text field " + textField);
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        TextEditor te = getCurrentEditor();
        if (te instanceof EditArea) {
            ((EditArea) te).checkForFileChanged();
        }
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
    }

    //****************************************//
    class GlobalCommands extends CommandProcessor {

        public void enableSaveAll(CommandState state) {
            boolean canSaveAll = false;
            List list = getApplication().getPageManager().pages();
            for (Iterator i = list.iterator(); i.hasNext();) {
                PageContext context = (PageContext) i.next();
                Component c = context.getPage();
                if (c instanceof JScrollPane) {
                    c = ((JScrollPane) c).getViewport().getView();
                }
                if (c instanceof EditArea) {
                    EditArea edit = (EditArea) c;
                    if (edit.isModified()) {
                        canSaveAll = true;
                        break;
                    }
                }
            }
            state.setEnabled(canSaveAll);
        }

        public void onSaveAll() {
            List list = getApplication().getPageManager().pages();
            for (Iterator i = list.iterator(); i.hasNext();) {
                PageContext context = (PageContext) i.next();
                Component c = context.getPage();
                if (c instanceof JScrollPane) {
                    c = ((JScrollPane) c).getViewport().getView();
                }
                if (c instanceof EditArea) {
                    EditArea edit = (EditArea) c;
                    if (edit.isModified()) {
                        edit.save(null);
                    }
                }
            }
        }
    }

    private class EditArea extends JTextArea implements HasPopupItems, ManagedPage, TextEditor, Scrollable {

        private Commands commands = new Commands();
        private File file; // associated file (if any)
        private long timestamp; // timestamp on file when it was opened
        private URL url; // associated url (if any)
        private PageContext pageContext;
        private String mimeType;
        private Studio app;
        private boolean modified = false;

        EditArea(Studio app, String mimeType) {
            init(app, mimeType);
        }

        EditArea(Studio app, String mimeType, String text) {
            setText(text);
            init(app, mimeType);
        }

        EditArea(Studio app, String mimeType, File file) throws IOException {
            this(app, mimeType, new FileReader(file), file);
            this.file = file;
            timestamp = file.lastModified();
        }

        EditArea(Studio app, String mimeType, URL url) throws IOException {
            this(app, mimeType, new InputStreamReader(url.openStream()));
            this.url = url;
        }

        EditArea(Studio app, String mimeType, Reader reader) throws IOException {
            this(app, mimeType, reader, null);
        }

        EditArea(Studio app, String mimeType, Reader reader, Object desc) throws IOException {
            try {
                read(reader, desc);
            } finally {
                reader.close();
            }
            init(app, mimeType);
        }

        @Override
        public File getFile() {
            return file;
        }

        public URL getURL() {
            return url;
        }

        @Override
        public String getMimeType() {
            return mimeType;
        }

        @Override
        public boolean isModified() {
            return modified;
        }

        @Override
        public void setPageContext(PageContext context) {
            pageContext = context;
        }

        @Override
        public String getTitle() {
            return pageContext.getTitle();
        }

        @Override
        public boolean close() {
            boolean close = true;
            if (modified) {
                int rc = JOptionPane.showConfirmDialog(app, "Save Changes?");
                if (rc == JOptionPane.YES_OPTION) {
                    close = save(file);
                } else {
                    close = rc == JOptionPane.NO_OPTION;
                }
            }
            return close;
        }

        @Override
        public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {
            app.getXMLMenuBuilder().mergePopupMenu("editPopupMenu", menu);

            EventSender es = app.getEventSender();
            if (es.hasListeners(EditorPopupEvent.class)) {
                EditorPopupEvent e = new EditorPopupEvent(this, menu);
                es.broadcast(e);
            }
            return menu;
        }

        @Override
        public void pageClosed() {
        }

        @Override
        public void pageDeiconized() {
        }

        @Override
        public void pageDeselected() {
            app.getCommandTargetManager().remove(commands);
        }

        @Override
        public void pageIconized() {
        }

        @Override
        public void pageSelected() {
            app.getCommandTargetManager().add(commands);
            // Check the timestamp to see if file has been modified since last open
            checkForFileChanged();
        }

        private void checkForFileChanged() {
            if (file != null && isCheckForFileChanges()) {
                long curr = file.lastModified();
                if (timestamp != curr) {
                    timestamp = curr; // Be sure to only ask once
                    String message = "File " + file.getName() + " modified on disk, reload?";
                    if (modified) {
                        message += "\nWarning: Answering yes will discard local changes";
                    }
                    int type = modified ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE;
                    int rc = JOptionPane.showConfirmDialog(app, message, "Reload?", JOptionPane.YES_NO_OPTION, type);
                    if (rc == JOptionPane.YES_OPTION) {
                        reload();
                    }
                }
            }
        }

        private void init(Studio app, String mimeType) {
            this.app = app;
            this.mimeType = mimeType;
            setBorder(new LineNumberBorder(this));
            addCaretListener(commands);
            getDocument().addUndoableEditListener(commands);
            setFontAndColor(SimpleEditor.this.getFont(SimpleEditor.textFields[0]),
                    SimpleEditor.this.getFontColor(SimpleEditor.textFields[0]));
        }

        protected void setFontAndColor(Font font, Color color) {
            this.setFont(font);
            this.setForeground(color);
        }

        private void reload() {
            try {
                super.read(new FileReader(file), file);
                getDocument().addUndoableEditListener(commands); // New document!
                timestamp = file.lastModified();
                modified = false;
                commands.clearEdits();
                commands.setChanged();
                global.setChanged();

            } catch (IOException x) {
                app.error("Couldn't reload file" + file.getName(), x);
            }
        }

        /**
         * Save the file.
         * @param f The file to save to, or null for save As
         */
        private boolean save(File f) {
            if (f == null) {

                JFileChooser chooser = new JFileChooser(getLastDir());
                if (file != null) {
                    chooser.setSelectedFile(file);
                }
                chooser.setDialogTitle("Save As...");

                int returnVal = chooser.showSaveDialog(app);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return false;
                } else {
                    f = chooser.getSelectedFile();
                    if (f.exists()) {
                        int rc = JOptionPane.showConfirmDialog(app, "Replace existing file?", null, JOptionPane.OK_CANCEL_OPTION);
                        if (rc != JOptionPane.OK_OPTION) {
                            return false;
                        }
                    }
                }
            }
            try {
                Writer out = new FileWriter(f);
                try {
                    write(out);
                    if (f != file) {
                        file = f;
                        setLastDir(f.getParentFile());
                        pageContext.setTitle(f.getName());
                    }
                    modified = false;
                    commands.setChanged();
                    global.setChanged();
                    return true;
                } finally {
                    out.close();
                    timestamp = f.lastModified();
                }
            } catch (IOException eh) {
                app.error("Couldn't save to file" + f.getName(), eh);
                return false;
            }
        }

        @Override
        public void saveText() {
            save(file);
        }

        @Override
        public void addNotify() {
            super.addNotify();

            if (!listenerInstalled) {
                Window window = (Window) SwingUtilities.getAncestorOfClass(Window.class, getApplication());
                window.addWindowFocusListener(SimpleEditor.this);
                listenerInstalled = true;
            }
        }

        class Commands extends CommandProcessor implements UndoableEditListener, CaretListener {

            private UndoManager um = new UndoManager();

            @Override
            public void caretUpdate(CaretEvent e) {
                setChanged();
            }

            public void enableCopy(CommandState state) {
                state.setEnabled(getSelectedText() != null);
            }

            public void enableCut(CommandState state) {
                state.setEnabled(getSelectedText() != null);
            }

            public void enableRedo(CommandState state) {
                state.setEnabled(um.canRedo());
            }

            public void enableSave(CommandState state) {
                state.setEnabled(modified);
            }

            public void enableRefresh(CommandState state) {
                state.setEnabled(file != null);
            }

            public void enableUndo(CommandState state) {
                state.setEnabled(um.canUndo());
            }

            public void onCopy() {
                copy();
            }

            public void onCut() {
                cut();
            }

            public void onPaste() {
                paste();
            }

            public void onRedo() {
                um.redo();
                modified = true;
                setChanged();
                global.setChanged();
            }

            public void onSave() {
                save(file);
            }

            public void onSaveAs() {
                save(null);
            }

            public void onUndo() {
                um.undo();
                modified = true;
                setChanged();
                global.setChanged();
            }

            public void onRefresh() {
                if (modified) {
                    int rc = JOptionPane.showConfirmDialog(app, "Discard changes?", "Discard?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (!(rc == JOptionPane.YES_OPTION)) {
                        return;
                    }
                }
                reload();
            }

            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                um.addEdit(e.getEdit());
                modified = true;
                setChanged();
                global.setChanged();
            }

            void clearEdits() {
                um.discardAllEdits();
            }
        }
    }

    private final class LineNumberBorder implements Border {

        private Color background = new Color(224, 224, 224);
        private Color foreground;
        ;
        private Font font;
        private Insets insets = new Insets(0, 0, 0, 0);
        private JTextArea textArea;
        private int extraPad = 2;
        private int leftPad = 2;
        private int rightPad = 2;

        LineNumberBorder(JTextArea area) {
            textArea = area;
            setFontAndColor(SimpleEditor.this.getFont(SimpleEditor.textFields[1]),
                    SimpleEditor.this.getFontColor(SimpleEditor.textFields[1]));
        }

        protected void setFontAndColor(Font font, Color color) {
            this.font = font;
            this.foreground = color;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            FontMetrics fm = textArea.getFontMetrics(font);
            int nRows = textArea.getLineCount();
            int l = String.valueOf(nRows).length();
            insets.left = (l * fm.charWidth('9')) + leftPad + rightPad + extraPad;
            return insets;
        }

        @Override
        public boolean isBorderOpaque() {
            return false; // because of "extraPad"
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(background);
            g.fillRect(x, y, insets.left - extraPad, height);

            g.setColor(foreground);
            g.setFont(font);

            FontMetrics tafm = textArea.getFontMetrics(textArea.getFont());
            int lineHeight = tafm.getHeight();
            int textAreaHeight = textArea.getHeight();

            Rectangle clip = g.getClipBounds();
            int firstRow = 1 + ((clip.y - y) / lineHeight);
            int lastRow = 2 + (((clip.y + clip.height) - y) / lineHeight);
            lastRow = Math.min(lastRow, textArea.getLineCount());

            int yPos = (y + (firstRow * lineHeight)) - tafm.getDescent();
            int xPos = (x + insets.left) - rightPad - extraPad;

            FontMetrics fm = textArea.getFontMetrics(font);

            for (int i = firstRow; i <= lastRow; i++) {
                String number = String.valueOf(i);
                int lineWidth = fm.stringWidth(number);
                g.drawString(number, xPos - lineWidth, yPos);
                yPos += lineHeight;
            }
        }
    }
}
