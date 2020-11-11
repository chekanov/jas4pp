package org.freehep.jas.plugin.console;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.Element;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

/**
 * A TextArea that supports terminal like functionality.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: Console.java 14085 2012-12-13 18:37:19Z tonyj $
 */

/*
 * TODO: Support insert/overstrike mode
 * TODO: Cut should only be allowed in input area
 * TODO: Make Ctrl^C work properly
 * TODO: Add support for command completion
 * TODO: Abilty to write images (a la BeanShell) -- maybe done JTextPane has insertIcon method
 * TODO: Worry about key bindings (PageUp, PageDown etc)
 */
/*
 * Note this class tries to avoid any JAS/Application framework dependencies, so it can be used
 * standalone. The corresponding StudioConsole class extends it with JAS specific functionality.
 * In retrospect it was probably a mistake to have this class publically extend JTextPane, the text
 * pane is hard to deal with, and should probably be replaced.
 */
public class Console extends JTextPane {

    private final static String defaultPrompt = "> ";
    private String currentPrompt;
    // History of commands typed in at the console. Used for command recall
    private List<String> history = new LinkedList<String>();
    // List of output not yet displayed in the console
    private List<ListEntry> outputList = new ArrayList<ListEntry>();
    private ListEntry lastItemInOutputList;
    private final Object inputStreamLock = new Object();
    private PrintStream logStream;
    private ConsoleInputStream theInputStream;
    private final List<ConsoleOutputStream> outputStreams = new ArrayList<ConsoleOutputStream>();
    private SimpleAttributeSet defStyle;
    private SimpleAttributeSet promptStyle;
    // We maintain several queues in this code. They are:
    // 1) The typeAhead buffer, stuff which has been typed while the program attached to the console
    //    is busy. As soon as an Return is entered the contents of the typeAhead buffer is moved to the
    //    pasteAhead buffer.
    // 2) The pasteAhead buffer. Complete commands that have been entered, but not yet echoed to the
    //    console because the program attached to the console is busy.
    // 3) The queue, commands that have been entered, echoed to the conole (if appropriate) but not
    //    yet read by the input stream.
    // Stuff pasted while not waiting for input goes into the pasteAhead buffer
    private StringBuffer pasteAhead = new StringBuffer();
    // Stuff typed while not waiting for input goes into the typeAhead buffer
    private StringBuffer typeAhead = new StringBuffer();
    private List<String> queue = new LinkedList<String>();
    private MyTimer timer;
    private boolean log;
    private boolean waitingForInput = false; // True while inputStream waiting for input
    private int historyPos = -1;
    private int maxHistory = 100;
    private int maxScrollback = 1000;
    private int start; // Of input area!
    private int startLastLine; // Start of last line (before prompt)
    private static final Logger logger = Logger.getLogger(Console.class.getName());
    public enum AutoShow { 
        DEFAULT, // Only auto-show if the output stream requests it 
        NEVER, 
        ALWAYS };
    private AutoShow autoShow = AutoShow.DEFAULT;

    /**
     * Create a new Console
     */
    public Console() {
        setFont(new Font("Courier", Font.PLAIN, 14));
        setPreferredSize(new Dimension(300, 200));
        addKeyListener(new CKeyListener());
        timer = new MyTimer(new CTimerListener());
        setEditable(false);
        setEditorKit(new NowrapEditorKit());

        defStyle = new SimpleAttributeSet() {
            @Override
            public Object getAttribute(Object key) {
                if (key.equals(StyleConstants.Foreground)) {
                    return getForeground();
                } else {
                    return super.getAttribute(key);
                }
            }
        };
        promptStyle = new SimpleAttributeSet();
        promptStyle.addAttribute(StyleConstants.Foreground, new Color(0, 153, 51));
    }

    /**
     * Create an input stream for reading user input from the console. The input
     * stream will use the default prompt.
     *
     * @return The newly created input stream
     */
    public ConsoleInputStream getInputStream() {
        return getInputStream(defaultPrompt);
    }

    /**
     * Create an input stream for reading user input from the console.
     *
     * @param initialPrompt The prompt to use for user input
     * @return The newly created input stream
     */
    public ConsoleInputStream getInputStream(String initialPrompt) {
        if (theInputStream == null) {
            theInputStream = new CInputStream(initialPrompt);
        }
        return theInputStream;
    }

    /**
     * Sets a stream to use for writing logging output. All input/output to the
     * console will also be logged to this output stream.
     *
     * @param out The output stream to use, or <CODE>null</CODE> to turn off
     * logging.
     */
    public synchronized void setLogStream(OutputStream out) {
        log = !(out == null);
        if (logStream != null) {
            logStream.flush();
        }
        if (log) {
            logStream = new PrintStream(out);
        } else {
            logStream = null;
        }
    }

    /**
     * Get the current log stream
     *
     * @return The current log stream, or <CODE>null</CODE> if no current stream
     */
    public OutputStream getLogStream() {
        return logStream;
    }

    /**
     * Temporarily disables/enables logging.
     *
     * @param log <CODE>true</CODE> to enable logging.
     */
    public synchronized void setLoggingEnabled(boolean log) {
        this.log = log && (logStream != null);
    }

    /**
     * Test if logging is currently enabled
     *
     * @return <CODE>true</CODE> if logging enabled.
     */
    public boolean isLoggingEnabled() {
        return log;
    }

    /**
     * Get an output stream for writing to the console.
     *
     * @param set The attributes for text created by this output stream,      * or <CODE>null</CODE> for the default attributes.
     * @param autoShow If true the console will "pop to the front" when new
     * output is written.
     * @return The newly created output stream
     */
    public ConsoleOutputStream getOutputStream(AttributeSet set, boolean autoShow) {
        ConsoleOutputStream out = new COutputStream(this, set, autoShow);
        synchronized (outputStreams) {
            outputStreams.add(out);
        }
        return out;
    }

    private void closeOutputStream(ConsoleOutputStream out) {
        synchronized (outputStreams) {
            outputStreams.remove(out);
        }
    }

    public ConsoleOutputStream getOutputStream(AttributeSet set) {
        return getOutputStream(set, false);
    }

    /**
     * Adds a listener for CTRL^C events
     *
     * @param l The listener to add.
     */
    public void addInterruptListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    /**
     * Cleans up resources associated with this console. Closes any input or
     * output streams associated with this console.
     */
    public void dispose() {
        setLogStream(null);
        if (theInputStream != null) {
            try {
                synchronized (inputStreamLock) {
                    theInputStream.close();
                    theInputStream = null;
                    // Notify anyone waiting on input that the stream is closed.
                    inputStreamLock.notify();
                }
            } catch (IOException x) {
            }
        }
        List<ConsoleOutputStream> temp;
        synchronized (outputStreams) {
            temp = new ArrayList<ConsoleOutputStream>(outputStreams);
        }
        for (ConsoleOutputStream out : temp) {
            try {
                out.close();
            } catch (IOException x) {
            }
        }
    }

    /**
     * Insert text (command(s)) as if it were typed by the user. The text will
     * be sent to the Console's input stream. Any text already typed by the user
     * will not be included in the text sent.
     *
     * @param text text to send
     */
    public void insertTextAsIfTypedByUser(String text) {
        if (!isEditable()) {
            throw new RuntimeException("Cannot send text to non-editable console");
        }
        if (!text.endsWith("\n")) {
            text += "\n";
        }
        if (waitingForInput) {
            int pos = text.indexOf('\n');
            String firstLine = text.substring(0, pos);
            // Take anything currently on the input line and move it to the end of the pasteAhead buffer
            String originalText = lastLine();
            Document doc = getDocument();
            remove(start, doc.getLength() - start);
            insertString(start, firstLine + "\n", null);
            sendToInputStream(firstLine);
            pasteAhead.append(text.substring(pos + 1));
            pasteAhead.append(originalText);
        } else {
            pasteAhead.append(text);
            if (!text.endsWith("\n")) {
                pasteAhead.append('\n');
            }
        }
    }

    @Override
    public void paste() {
        if (!isEditable()) {
            getToolkit().beep();
        } else {
            try {
                Transferable t = getToolkit().getSystemClipboard().getContents(this);
                String text = (String) t.getTransferData(DataFlavor.stringFlavor);
                text = removeIllegalCharacters(text);
                if (waitingForInput) {
                    int pos = text.indexOf('\n');
                    boolean containsEOL = pos >= 0;
                    String firstLine = containsEOL ? text.substring(0, pos) : text;

                    // If there is a current selection _in the editable area_ delete it
                    int sStart = Math.max(start, getSelectionStart());
                    int sEnd = Math.max(start, getSelectionEnd());
                    if (sEnd - sStart > 0) {
                        remove(sStart, sEnd - sStart);
                    }
                    // Add first line of pasted text to any current added text, at the insertion point
                    int cPos = getCaretPosition();

                    // If there is a new line in the pasted text, queue the whole line.
                    if (containsEOL) {
                        insertString(cPos, firstLine + "\n", null);
                        sendToInputStream(firstLine);
                        pasteAhead.append(text.substring(pos + 1));
                    } else {
                        insertString(cPos, firstLine, null);
                    }
                } else {
                    pasteAhead.append(text);
                }
            } catch (UnsupportedFlavorException ex) {
                logger.log(Level.SEVERE, "Unsupported flavor during paste", ex);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error during paste", ex);
            }
        }
    }

    private String removeIllegalCharacters(String in) {
        StringBuffer out = null;
        int l = in.length();
        for (int i = 0; i < l; i++) {
            char c = in.charAt(i);
            if (c < ' ' && c != '\n') {
                if (out == null) {
                    out = new StringBuffer(in.substring(0, i));
                }
            } else if (out != null) {
                out.append(c);
            }
        }
        if (out == null) {
            return in;
        } else {
            return out.toString();
        }
    }

    /**
     * A method to be called to request that the console be closed. Typically
     * called by scripting engines when the user types "quit" or the equivalent.
     * This close method does nothing, but is designed to be overridden by
     * subclasses.
     */
    public void close() {
        // Closing of associated input/output streams are dealt with in the dispose method.
    }

    /**
     * A method to be called to request that the console "pop up". This method
     * does nothing but is designed to be overridden by subclassses.
     */
    protected void autoShow() {
    }

    /**
     * Remove a listener for CTRL^C events.
     *
     * @param l The listener to remove
     */
    public void removeInterruptListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    /**
     * Called when CTRL^C is detected. Calls all the registered listeners.
     */
    protected void fireInterruptAction() {
        int count = listenerList.getListenerCount(ActionListener.class);
        if (count > 0) {
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_FIRST, "Break");
            ActionListener[] listeners = (ActionListener[]) listenerList.getListeners(ActionListener.class);
            for (int i = listeners.length; i-- > 0;) {
                listeners[i].actionPerformed(event);
            }
        }
    }

    /**
     * Clears any output from the Console
     */
    public void clear() {
        Document d = getDocument();
        remove(0, d.getLength());
        if (waitingForInput) {
            synchronized (inputStreamLock) {
                inputStreamLock.notify();
            }
        }
    }

    private void setLastLine(String line) {
        int end = getDocument().getLength();
        if (end > start) {
            remove(start, end - start);
        }
        insertString(start, line, null);
    }

    private void consumeAndBeep(KeyEvent event) {
        event.consume();
        getToolkit().beep();
    }

    private void prepareToFlush() {
        if (waitingForInput) {
            // Take anything currently on the input line and move it to the typeAhead buffer
            typeAhead.append(lastLine());
            Document doc = getDocument();
            remove(startLastLine, doc.getLength() - startLastLine);

            // TODO: Fixme, what if temporary prompt was set! Plus we dont want to add the
            // initial entry again, as it was already put into the typeahead.
            writePrompt(theInputStream.getCurrentPrompt(), null);
        } else {
            flush();
        }
    }

    private void flush() {
        List<ListEntry> previousOutputList = outputList;
        synchronized (this) {
            outputList = new ArrayList<ListEntry>();
            lastItemInOutputList = null;
        }

        Document doc = getDocument();
        final JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);
        boolean scrollBarWasAtEnd = viewport != null && viewport.getView() == this
                && viewport.getViewPosition().y + viewport.getExtentSize().height > viewport.getViewSize().height - 5;
        for (ListEntry entry : previousOutputList) {
            insertString(doc.getLength(), entry.string.toString(), entry.set);
        }
        if (scrollBarWasAtEnd) {
            Runnable runnable = new Runnable() {
                // This is needed to get this far enough down the event action queue
                // so that it gets executed after the view has been updated.
                private int delay = 2;

                @Override
                public void run() {
                    if (delay-- > 0) {
                        SwingUtilities.invokeLater(this);
                    }
                    viewport.setViewPosition(new Point(viewport.getViewPosition().x, viewport.getViewSize().height - viewport.getExtentSize().height));
                }
            };
            SwingUtilities.invokeLater(runnable);
        }
    }

    private boolean inLastLine() {
        int caret = getCaretPosition();
        return caret >= start;
    }

    private String lastLine() {
        try {
            Document doc = getDocument();
            return doc.getText(start, doc.getLength() - start);
        } catch (BadLocationException ex) {
            logBadLocation(ex);
            return null;
        }
    }

    private void sendToInputStream(String line) {
        if (logStream != null && log) {
            logStream.println(currentPrompt + line);
        }
        synchronized (inputStreamLock) {
            boolean wasEmpty = queue.isEmpty();
            queue.add(line + "\n");
            if (line.length() > 0) {
                history.add(line);
            }
            while (history.size() >= maxHistory) {
                history.remove(0);
            }
            historyPos = history.size();
            if (wasEmpty) {
                inputStreamLock.notify();
                waitingForInput = false;
            }
        }
    }
    // Overriden to make sure text entered at the keyboard doesnt inherit the prompt color

    @Override
    public MutableAttributeSet getInputAttributes() {
        return defStyle;
    }

    private void writeMessage(String line, AttributeSet style) {
        Document doc = getDocument();
        insertString(doc.getLength(), line, style);
        setCaretPosition(doc.getLength());
    }
    // This method can be called on any thread, so rather than directly updating the
    // console data is added to a queue (list).

    private void writeOutput(byte[] b, int off, int len, AttributeSet set) throws IOException {
        if (isLoggingEnabled()) {
            logStream.write(b, off, len);
        }
        writeOutput(new String(b, off, len), set);
    }

    private void writeOutput(String s, AttributeSet set) throws IOException {
        synchronized (this) {
            boolean wasEmpty = outputList.isEmpty();
            // If the attributes are the same as last time, simply append to 
            // existing ListEntry, otherwise create a new entry.
            if ((lastItemInOutputList != null) && (set == lastItemInOutputList.set)) {
                lastItemInOutputList.string.append(s);
            } else {
                outputList.add(lastItemInOutputList = new ListEntry(s, set));
            }
            if (wasEmpty) {
                timer.startLater();
            }
        }
    }

    private void writePrompt(String prompt, String initialEntry) {
        flush(); // Flush any output still to be sent to the console
        startLastLine = getDocument().getLength();
        try {
            Rectangle r = modelToView(startLastLine);
            Insets insets = this.getInsets();
            if (r != null && r.x > insets.left) {
                writeMessage("\n", defStyle);
                startLastLine = getDocument().getLength();
            }
        } catch (BadLocationException x) {
        } // Can't happen
        currentPrompt = prompt;
        if (prompt != null) {
            writeMessage(prompt, promptStyle);
        }
        start = getDocument().getLength();

        if (pasteAhead.length() > 0) {
            int pos = pasteAhead.indexOf("\n");
            boolean hasEOL = pos >= 0;
            if (hasEOL) {
                String ta = pasteAhead.substring(0, pos);
                writeMessage(ta + "\n", null);
                pasteAhead.delete(0, pos + 1);

                String text = lastLine();
                setCaretPosition(getDocument().getLength());
                text = text.substring(0, text.length() - 1); // trim \n
                sendToInputStream(text);
                return; // without setting waitingForInput to true
            } else {
                writeMessage(pasteAhead.toString(), null);
                pasteAhead.setLength(0);
                if (typeAhead.length() > 0) {
                    writeMessage(typeAhead.toString(), null);
                    typeAhead.setLength(0);
                }
            }
        } else {
            if (initialEntry != null) {
                writeMessage(initialEntry, null);
            }
            if (typeAhead.length() > 0) {
                writeMessage(typeAhead.toString(), null);
                typeAhead.setLength(0);
            }
        }
        setEditable(true);
        // Normally the cursor is automatically made visible when the focus is
        // gained. However if setEditable(true) is done when we already have the
        // focus then this doesn't work, so...
        if (!getCaret().isVisible() && hasFocus()) {
            getCaret().setVisible(true);
        }
        waitingForInput = true;
    }

    /**
     * Getter for property promptColor.
     *
     * @return Value of property promptColor.
     *
     */
    public Color getPromptColor() {
        return (Color) promptStyle.getAttribute(StyleConstants.Foreground);
    }

    /**
     * Setter for property promptColor.
     *
     * @param promptColor New value of property promptColor.
     *
     */
    public void setPromptColor(Color promptColor) {
        promptStyle.addAttribute(StyleConstants.Foreground, promptColor);
    }

    public int getMaxScrollback() {
        return maxScrollback;
    }

    public void setMaxScrollback(int maxLines) {
        this.maxScrollback = maxLines;
        // Truncate any excessive lines
        insertString(getDocument().getLength(), "", null);
    }

    public AutoShow getAutoShow() {
        return autoShow;
    }

    public void setAutoShow(AutoShow autoShow) {
        this.autoShow = autoShow;
    }
    

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    private void insertString(int position, String line, AttributeSet style) {
        final Document doc = getDocument();
        final DefaultCaret caret = (DefaultCaret) getCaret();
        try {
            // We do this to turn off auto-caret management. Otherwise it is impossible
            // to scroll back in a document uzing the mouse wheel when new text is being added.
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
            doc.insertString(position, line, style == null ? defStyle : style);
            // Count lines! This code relies on lines not wrapping
            View baseView = getUI().getRootView(this);
            View root = baseView.getView(0);
            if (root.getViewCount() > maxScrollback) {
                View lineView = root.getView(root.getViewCount() - maxScrollback);
                final int offset = lineView.getStartOffset();
                doc.remove(0, offset);
                if (getSelectionStart() > 0) {
                    setSelectionStart(Math.max(0, getSelectionStart() - offset));
                }
                if (getSelectionEnd() > 0) {
                    setSelectionEnd(Math.max(0, getSelectionEnd() - offset));
                }
            }
        } catch (BadLocationException ex) {
            logBadLocation(ex);
        } finally {
            caret.setUpdatePolicy(DefaultCaret.UPDATE_WHEN_ON_EDT);
        }
    }

    private void remove(int start, int end) {
        try {
            getDocument().remove(start, end);
        } catch (BadLocationException ex) {
            logBadLocation(ex);
        }
    }

    private void logBadLocation(BadLocationException ex) {
        Logger.getLogger(Console.class.getName()).log(Level.INFO, "Unexpected location exception", ex);
    }

    private class CInputStream extends ConsoleInputStream implements Runnable {

        private int pos;
        private byte[] buffer;

        CInputStream(String prompt) {
            setPrompt(prompt);
        }

        @Override
        public int read() throws IOException {
            if ((buffer == null) || (pos >= buffer.length)) {
                if (!fillBuffer()) {
                    return -1;
                }
            }

            return buffer[pos++];
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if ((buffer == null) || (pos >= buffer.length)) {
                if (!fillBuffer()) {
                    return -1;
                }
            }

            int l = Math.min(len, buffer.length - pos);
            System.arraycopy(buffer, pos, b, off, l);
            pos += l;

            return l;
        }

        @Override
        public void run() {
            writePrompt(getCurrentPrompt(), getInitialEntry());
        }
        // Called by the input stream when it needs more input

        private boolean fillBuffer() throws IOException {
            synchronized (inputStreamLock) {
                while (queue.isEmpty()) {
                    try {
                        // In case the console has already been disposed.
                        if (theInputStream == null) {
                            return false;
                        }
                        // Display the prompt
                        SwingUtilities.invokeLater(this);
                        inputStreamLock.wait();
                        // In case someone disposed of the console while we were waiting
                        if (theInputStream == null) {
                            return false;
                        }
                    } catch (InterruptedException x) {
                        throw new InterruptedIOException();
                    }
                }

                String line = queue.remove(0);
                buffer = line.getBytes();
                pos = 0;
            }
            return true;
        }
    }

    private class CTimerListener implements ActionListener {

        /**
         * called (on event thread), when new output to be written to console.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            // If there is stuff which still needs to be flushed, then flush it
            if (!outputList.isEmpty()) {
                prepareToFlush();
            }
        }
    }

    private class CKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            final int kc = keyEvent.getKeyCode();
            if ((kc == KeyEvent.VK_C) && ((keyEvent.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                fireInterruptAction();
            } else if (kc == KeyEvent.VK_ENTER) {
                if (!waitingForInput) {
                    keyEvent.consume();
                } else {
                    setCaretPosition(getDocument().getLength());
                }
            } else if (!inLastLine()) {
                keyEvent.consume(); // No Beep
            } else if (keyEvent.getKeyChar() == KeyEvent.VK_BACK_SPACE && getCaretPosition() == start) {
                consumeAndBeep(keyEvent);
            } else if (kc == KeyEvent.VK_LEFT || kc == KeyEvent.VK_KP_LEFT) {
                if (getCaretPosition() == start) {
                    consumeAndBeep(keyEvent);
                }
            } else if (kc == KeyEvent.VK_HOME) {
                if (getCaretPosition() > start) {
                    setCaretPosition(start);
                    keyEvent.consume();
                } else {
                    consumeAndBeep(keyEvent);
                }
            } else if (kc == KeyEvent.VK_UP || kc == KeyEvent.VK_KP_UP) {
                if (historyPos <= 0) {
                    consumeAndBeep(keyEvent);
                } else {
                    String line = history.get(--historyPos);
                    setLastLine(line);
                    setCaretPosition(getDocument().getLength());
                    keyEvent.consume();
                }
            } else if (kc == KeyEvent.VK_DOWN || kc == KeyEvent.VK_KP_DOWN) {
                if (historyPos >= (history.size() - 1)) {
                    consumeAndBeep(keyEvent);
                } else {
                    String line = history.get(++historyPos);
                    setLastLine(line);
                    setCaretPosition(getDocument().getLength());
                    keyEvent.consume();                    
                }
            }
        }

        @Override
        public void keyTyped(KeyEvent keyEvent) {
            if (waitingForInput) {
                if (!inLastLine()) {
                    setCaretPosition(getDocument().getLength());
                } else {
                    // if the selection spans more than the current line (for instance the prompt)
                    // Then we need to remove the extra region
                    Caret caret = getCaret();
                    if (caret.getMark() < start) {
                        int pos = caret.getDot();
                        caret.setDot(start);
                        caret.moveDot(pos);
                    }
                }
                if (keyEvent.getKeyChar() == KeyEvent.VK_ENTER) {
                    theInputStream.clearOneTimePrompt();
                    String text = lastLine();
                    setCaretPosition(getDocument().getLength());
                    text = text.substring(0, text.length() - 1); // trim \n
                    sendToInputStream(text);
                }
            } else {
                if (keyEvent.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                    int l = typeAhead.length();
                    if (l > 0) {
                        typeAhead.deleteCharAt(l - 1);
                    }
                } else if (keyEvent.getKeyChar() == KeyEvent.VK_ENTER) {
                    // Move the contents of the typeAhead buffer to the pasteAhead buffer
                    pasteAhead.append(typeAhead);
                    pasteAhead.append('\n');
                    typeAhead.setLength(0);
                } else if ((keyEvent.getModifiers() & KeyEvent.CTRL_MASK) == 0) {
                    typeAhead.append(keyEvent.getKeyChar());
                }
                keyEvent.consume();
            }
        }
    }

    private static class COutputStream extends ConsoleOutputStream {

        private Console console;

        COutputStream(Console console, AttributeSet set, boolean autoShow) {
            super(set, autoShow);
            this.console = console;
        }

        @Override
        public void write(byte[] b, int off, int len, AttributeSet set) throws IOException {
            Console local = console;
            if (local == null) {
                throw new IOException("File closed");
            } else {
                local.writeOutput(b, off, len, set);
                if (local.getAutoShow() == AutoShow.ALWAYS || (isAutoShow() && local.getAutoShow() == AutoShow.DEFAULT)) {
                    local.autoShow();
                }
            }

        }

        @Override
        public void close() throws IOException {
            super.close();
            Console local = console;
            if (local != null) {
                local.closeOutputStream(this);
            }
            console = null;
        }
    }

    private static class ListEntry {

        AttributeSet set;
        StringBuffer string;

        ListEntry(String string, AttributeSet set) {
            this.string = new StringBuffer(string);
            this.set = set;
        }
    }

    private static class MyTimer extends Timer implements Runnable {

        MyTimer(ActionListener l) {
            super(10, l);
            setRepeats(false);
        }

        @Override
        public void run() {
            start();
        }

        void startLater() {
            SwingUtilities.invokeLater(this);
        }
    }

    private static class NoWrapParagraphView extends ParagraphView {

        public NoWrapParagraphView(Element elem) {
            super(elem);
        }

        @Override
        public void layout(int width, int height) {
            super.layout(Short.MAX_VALUE, height);
        }

        @Override
        public float getMinimumSpan(int axis) {
            return super.getPreferredSpan(axis);
        }
    }

    private static class NowrapEditorKit extends StyledEditorKit {

        private ViewFactory defaultFactory;

        @Override
        public ViewFactory getViewFactory() {
            if (defaultFactory == null) {
                defaultFactory = new NoWrapViewFactory(super.getViewFactory());
            }
            return defaultFactory;
        }
    }

    private static class NoWrapViewFactory implements ViewFactory {

        private final ViewFactory delegate;

        NoWrapViewFactory(ViewFactory delegate) {
            this.delegate = delegate;
        }

        @Override
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null && kind.equals(AbstractDocument.ParagraphElementName)) {
                return new NoWrapParagraphView(elem);
            } else {
                return delegate.create(elem);
            }

        }
    }
}