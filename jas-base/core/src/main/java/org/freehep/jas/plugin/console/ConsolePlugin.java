package org.freehep.jas.plugin.console;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.swing.popup.HasPopupItems;
import org.freehep.util.FreeHEPLookup;
import org.freehep.xml.menus.XMLMenuBuilder;

/**
 * The main class of the console plugin
 * @author tonyj
 * @version $Id: ConsolePlugin.java 14500 2013-03-19 00:41:26Z tonyj $
 */
public class ConsolePlugin extends Plugin implements ConsoleService {
  
    // This maps from console name to console
    private Map<String,Console> consoles = new HashMap<String,Console>();
    // Maps from console to the page context associated with the window containing the console
    private Map<Console,PageContext> contexts = new HashMap<Console,PageContext>();
      
    private static final String STD_OUT_CONSOLE_NAME = "Standard Output";
    private static final String STD_ERR_CONSOLE_NAME = "Standard Error";
    private PerThreadOutputStream stdoutThreadedOutput;
    private SwitchedOutputStream stdoutSwitchedOutput;
    private SwitchedOutputStream stderrSwitchedOutput;
    private ConsolePreferences pref;
    private StudioOutputStream stdoutOutput;
    private StudioOutputStream stderrOutput;

    @Override
    public Console getConsole(String name) {
        return consoles.get(name);
    }

    @Override
    public Console createConsole(String name, Icon icon) {
        return createConsole(name,icon,null);
    }
    @Override
    public Console createConsole(String name, Icon icon, HasPopupItems popupItems) {
        StudioConsole console = new StudioConsole(getApplication(), this, popupItems);
        console.setMaxScrollback(pref.getDefaultScrollback());
        PageContext context = getApplication().getConsoleManager().openPage(new JScrollPane(console), name, icon, name, false);
        context.addPageListener(console);
        // Problem, how do we cleanly ensure that we get the initial page selected message?
        if (getApplication().getConsoleManager().getSelectedPage() == context) {
            console.setSelected(true);
        }
        consoles.put(name, console);
        contexts.put(console, context);
        return console;
    }
    /** Called when close is called on a StudioConsole
     * @param console The console being closed
     */
    void closeMe(Console console) {
        PageContext context = contexts.get(console);
        if (context != null) {
            context.close();
        }
    }
    /** Called when the context associated with the a StudioConsole is closed
     * @param console The console being closed.
     */
    void consoleIsClosed(Console console) {
        PageContext context = contexts.remove(console);
        String name = context.getTitle();
        consoles.remove(name);
    }

    @Override
    public ConsoleOutputStream getConsoleOutputStream(String name, Icon icon) throws IOException {
        return getConsoleOutputStream(name, icon, null);
    }

    @Override
    public ConsoleOutputStream getConsoleOutputStream(String name, Icon icon, AttributeSet set) throws IOException { 
        return new StudioOutputStream(this, name, icon, set);
    }

    @Override
    protected void init() throws org.xml.sax.SAXException, java.io.IOException {
        Studio app = getApplication();
        FreeHEPLookup lookup = app.getLookup();
        lookup.add(this);

        XMLMenuBuilder builder = app.getXMLMenuBuilder();
        URL xml = getClass().getResource("Console.menus");
        builder.build(xml);

        /**
         * Streams on which stdout/stderr will be written _if_ they are redirected
         */
        stdoutOutput = new StudioOutputStream(this,STD_OUT_CONSOLE_NAME,null,null,false);
        stderrOutput = new StudioOutputStream(this,STD_ERR_CONSOLE_NAME,null,null,false);
        
        /**
         * Switches to control whether stderr,stdout are redirected
         */
        stdoutSwitchedOutput = new SwitchedOutputStream(stdoutOutput,System.out);
        stderrSwitchedOutput = new SwitchedOutputStream(stderrOutput,System.err);
        /**
         * Stream to control per-thread redirection of stdout
         */
        stdoutThreadedOutput = new PerThreadOutputStream(stdoutSwitchedOutput);
        
        pref = new ConsolePreferences(this);
        lookup.add(pref);
        preferencesChanged(pref);
        
        System.setOut(new PrintStream(stdoutThreadedOutput, true));
        System.setErr(new PrintStream(stderrSwitchedOutput, true));
    }

    @Override
    public void redirectStandardOutputOnThreadToConsole(Thread thread, ConsoleOutputStream out) {
        stdoutThreadedOutput.mapThreadToOutputStream(thread, out);
    }

    /**
     * This is what the StudioOutputStream calls when it is ready to write to a
     * console. Note, console output may be written from any thread.
     */
    synchronized ConsoleOutputStream getStreamToWriteTo(final StudioOutputStream out) throws IOException {
        try {
            final String name = out.getName();
            Console console = consoles.get(name);
            if (console == null) {
                ConsoleCreator cc = new ConsoleCreator(name, out.getIcon());
                if (SwingUtilities.isEventDispatchThread()) {
                    cc.run();
                } else {
                    SwingUtilities.invokeAndWait(cc);
                }
                console = cc.getConsole();
            }
            return console.getOutputStream(out.getAttributeSet(), out.isAutoShow());
        } catch (InvocationTargetException x) {
            throw new IOException(x.getTargetException());
        } catch (InterruptedException x) {
            throw new InterruptedIOException();
        }
    }

    synchronized void showMe(Console console) {
        final PageContext context = (PageContext) contexts.get(console);
        if (context != null) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    context.requestShow();
                }
            };
            if (SwingUtilities.isEventDispatchThread()) {
                run.run();
            } else {
                SwingUtilities.invokeLater(run);
            }
        }
    }

    @Override
    public void showConsole(Console console) {
        showMe(console);
    }

    @Override
    public PageContext getPageContextForConsole(Console console) {
        return contexts.get(console);
    }

    void preferencesChanged(ConsolePreferences pref) {
        stdoutSwitchedOutput.setUseOne(pref.isRedirectStandardOutput());
        stderrSwitchedOutput.setUseOne(pref.isRedirectStandardError());
        stdoutOutput.setAutoShow(pref.isAutoShowStandardOutput());
        stderrOutput.setAutoShow(pref.isAutoShowStandardError());
    }

    private class ConsoleCreator implements Runnable {

        private String name;
        private Icon icon;
        private Console console;

        ConsoleCreator(String name, Icon icon) {
            this.name = name;
            this.icon = icon;
        }

        @Override
        public void run() {
            console = createConsole(name, icon);
        }

        Console getConsole() {
            return console;
        }
    }
    /**
     * A Stream which can redirect output on a per-Thread basis
     */
    private static class PerThreadOutputStream extends OutputStream {
        private final OutputStream fallback;
        private Map<Thread,OutputStream> threadOutputs = new WeakHashMap<Thread,OutputStream>();

        /**
         * 
         * @param fallback The stream to use if no thread-specific stream is found.
         */
        PerThreadOutputStream(OutputStream fallback) {
            this.fallback = fallback;
        }

        @Override
        public void write(int b) throws IOException {
            OutputStream out = threadOutputs.get(Thread.currentThread());
            (out==null?fallback:out).write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            OutputStream out = threadOutputs.get(Thread.currentThread());
            (out==null?fallback:out).write(b, off, len); 
        }
        
        void mapThreadToOutputStream(Thread t, OutputStream out) {
            threadOutputs.put(t,out);
        }

    }
    /** An output stream which can be switched between two alternative output streams.
     */
    private static class SwitchedOutputStream extends OutputStream {
        private final OutputStream one;
        private final OutputStream two;
        private boolean useOne = true;
        
        SwitchedOutputStream(OutputStream one, OutputStream two) {
            this.one = one;
            this.two = two;
        }

        @Override
        public void write(int b) throws IOException {
            (useOne ? one : two).write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            (useOne ? one : two).write(b, off, len);
        }

        public boolean isUseOne() {
            return useOne = true;
        }

        public void setUseOne(boolean useOne) {
            this.useOne = useOne;
        }
    }
}