package org.freehep.jas.extension.compiler;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import org.freehep.application.ApplicationEvent;
import org.freehep.application.PropertyUtilities;
import org.freehep.application.mdi.PageEvent;
import org.freehep.application.mdi.PageListener;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.application.studio.StudioListener;
import org.freehep.jas.event.EditorPopupEvent;
import org.freehep.jas.plugin.console.Console;
import org.freehep.jas.plugin.console.ConsoleOutputStream;
import org.freehep.jas.plugin.console.ConsoleService;
import org.freehep.jas.plugin.tree.FTree;
import org.freehep.jas.plugin.tree.FTreeProvider;
import org.freehep.jas.services.DynamicClassLoader;
import org.freehep.jas.services.FileHandler;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.jas.services.TextEditor;
import org.freehep.jas.services.TextEditorService;
import org.freehep.jas.services.URLHandler;
import org.freehep.swing.ExtensionFileFilter;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.images.ImageHandler;
import org.freehep.util.template.Template;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.xml.sax.SAXException;

/**
 *
 * @author tonyj
 * @version $Id: CompilerPlugin.java 15634 2013-08-02 13:01:07Z onoprien $
 */
public class CompilerPlugin extends Plugin implements StudioListener, FileHandler, URLHandler, PreferencesTopic, DynamicClassLoader {
    final static Icon javaIcon = ImageHandler.getIcon("images/java.gif", CompilerPlugin.class);
    final static Icon compileIcon = ImageHandler.getIcon("images/compile.gif", CompilerPlugin.class);
    final static Icon runIcon = ImageHandler.getIcon("/toolbarButtonGraphics/media/FastForward16.gif", CompilerPlugin.class);
    private final static String mimeType = "text/java";
    private final static String[] preferencesPath = { "Java" , "Compiler" };
    private Properties props;
    private JASClassManager classManager;
    
    public FileFilter getFileFilter() {
        return new ExtensionFileFilter("java", "Java File");
    }
    
    /** Setter for property outputDirectory.
     * @param outputDirectory New value of property outputDirectory.
     *
     */
    public void setOutputDirectory(File outputDirectory) {
        props.setProperty("userClasses", outputDirectory.getAbsolutePath());
    }
    
    /** Getter for property outputDirectory.
     * @return Value of property outputDirectory.
     *
     */
    public File getOutputDirectory() {
        Properties props = getApplication().getUserProperties();
        String userClassDir = props.getProperty("userClasses", "{user.home}/.{appName}/classes");
        return new File(userClassDir);
    }
    
    /** Setter for property redirectOutput.
     * @param redirectOutput New value of property redirectOutput.
     *
     */
    public void setRedirectOutput(boolean redirectOutput) {
        PropertyUtilities.setBoolean(props, "redirectOutput", redirectOutput);
    }
    
    /** Getter for property redirectOutput.
     * @return Value of property redirectOutput.
     *
     */
    public boolean isRedirectOutput() {
        return PropertyUtilities.getBoolean(props, "redirectOutput", true);
    }
    
    public boolean accept(File file) throws IOException {
        return file.getName().endsWith(".java");
    }
    
    /** Called when the user pushes the apply button in the preferences dialog.
     * This is also called if the user changes to another preferences topic, or
     * if the user hits OK to dismiss the preferences dialog.
     * @param panel The component currently being displayed
     * @return true if success, false if an error occured (invalid input)
     * @see #component()
     *
     */
    public boolean apply(JComponent panel) {
        return ((CompilerPreferences) panel).apply();
    }
    
    private Class loadRequested(String name) throws ClassNotFoundException
    {
        return classManager.loadClass(name);
    }
    
    private void load(TextEditor te) {
        File f = te.getFile();
        String className = f.getName();
        int pos = className.indexOf(".");
        if (pos > 0) className = className.substring(0, pos);
        String packageName = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            String line;
            Pattern p = Pattern.compile("\\s*package\\s+(.+);.*");
            while ((line = in.readLine()) != null) {
              if (!line.isEmpty()) {
                Matcher m = p.matcher(line);
                if (m.matches()) {
                  packageName = m.group(1);
                }
                break;
              }
            }
            in.close();
            if (packageName != null) className = packageName +"."+ className;
            loadRequested(className);
        } catch (Throwable x) {
            getApplication().error("Error loading class " + className, x);
        }
    }
    
    private void run(TextEditor te) {
        File f = te.getFile();
        //TODO: Fixme! Need to look for package statement.
        String className = f.getName();
        int pos = className.indexOf(".");
        if (pos > 0)
            className = className.substring(0, pos);
        try {
            Class x = loadRequested(className);
            classManager.run(x);
        }
        catch (ClassNotFoundException xx) {
            getApplication().error("Class not found " + className, xx);
        }
    }
    /**
     * The method that does the real work!
     */
    private void compile(TextEditor te) {
        try {
            if ( te.getFile() == null || te.isModified() )
                if ( isSaveBeforeCompiling() )
                    te.saveText();
                else {
                    getApplication().error("You must save the file before compiling (check the Compiler's preferences for automatic save)");
                    return;
                }
            PrintStream out = getCompilerPrintStream();
            
            Studio app = getApplication();
            char delimiter = File.pathSeparatorChar;
            StringBuffer cp = new StringBuffer(System.getProperty("java.class.path"));
            URL[] urls = app.getExtensionLoader().getURLs();
            for (int i = 0; i < urls.length; i++)
                cp.append(delimiter).append(URLDecoder.decode(urls[i].getPath(),"UTF-8"));
            
            String jcp = classManager.getClasspath();
            if (jcp.length() > 0) cp.append(delimiter).append(jcp);
            
            /*
             * Many compilers (such as jikes) need the rt.jar file explicitly in the CLASSPATH
             * They also do not like to have non-existent jar files in the CLASSPATH
             */
            File rt = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar");
            if (rt.exists())
                cp.append(delimiter).append(rt.getCanonicalPath());
            
            CompilerInterface compiler = isUseBuiltInCompiler() ? (CompilerInterface) new InternalCompiler() : (CompilerInterface) new ExternalCompiler(getCompilerCommand());
            compiler.setClassPath(cp.toString());
            if (isRedirectOutput())
                compiler.setOutputDir(getOutputDirectory());
            compiler.setOutputStream(out);
            
            boolean ok = compiler.compile(te.getFile());
            String now = SimpleDateFormat.getTimeInstance().format(new Date());
            if (ok) {
                out.println(now+" ----------- compile successful");
                app.getToolkit().beep();
                
                classManager.reload();
            }
            else {
                out.println(now+" ----------- compile failed");
                app.error("Compile failed (see Compiler Output window for details)");
            }
            out.close();
        }
        catch (Throwable x) {
            getApplication().error("Error during compile", x);
        }
    }
    
    /** Get the component to display in the preferences dialog
     * @return The component to be used.
     *
     */
    public JComponent component() {
        return new CompilerPreferences(this);
    }
    
    public void handleEvent(EventObject e) {
        if (e instanceof EditorPopupEvent) {
            final EditorPopupEvent event = (EditorPopupEvent) e;
            String type = event.getEditor().getMimeType();
            if (type.equals(mimeType)) {
                JPopupMenu menu = event.getMenu();
                JMenuItem item = new JMenuItem("Compile", 'C') {
                    public void fireActionPerformed(ActionEvent e) {
                        compile(event.getEditor());
                    }
                };
                item.setIcon(compileIcon);
                item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9,0));
                menu.add(item);
                
                item = new JMenuItem("Load",'L') {
                    public void fireActionPerformed(ActionEvent e) {
                        load(event.getEditor());
                    }
                };
                menu.add(item);
                
                item = new JMenuItem("Run",'R') {
                    public void fireActionPerformed(ActionEvent e) {
                        run(event.getEditor());
                    }
                };
                item.setIcon(runIcon);
                item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2,0));
                menu.add(item);
            }
        }
        else if (e instanceof ApplicationEvent && (((ApplicationEvent) e).getID() == ApplicationEvent.INITIALIZATION_COMPLETE)) {
            Studio app = getApplication();
            TextEditorService te = (TextEditorService) app.getLookup().lookup(TextEditorService.class);
            if (te != null)
                te.addMimeType(mimeType, javaIcon);
            
            getOutputDirectory().mkdirs();
            
            FTreeProvider provider = (FTreeProvider) getApplication().getLookup().lookup(FTreeProvider.class);
            FTree masterTree = provider.tree();
            classManager = new JASClassManager(getApplication(), CompilerPlugin.this, getApplication().getExtensionLoader(), masterTree);
            app.getLookup().add(new ClassPathPreferences(app,classManager));
        }
    }
    
    public void openFile(File file) throws IOException {
        TextEditorService te = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
        te.show(file, mimeType);
    }
    
    /** Specifies where in the preferences tree this item should appear.
     * @return The path under which this topic should be displayed in the preferences dialog.
     *
     */
    public String[] path() {
        return preferencesPath;
    }
    
    protected void init() throws SAXException, IOException {
        Studio app = getApplication();
        props = app.getUserProperties();
        
        XMLMenuBuilder builder = app.getXMLMenuBuilder();
        URL xml = getClass().getResource("Compiler.menus");
        builder.build(xml);
        
        Commands commands = new Commands();
        app.getCommandTargetManager().add(commands);
        app.getLookup().add(this);
        List list = new ArrayList();
        list.add(".");
        
        app.getEventSender().addEventListener(this, EditorPopupEvent.class);
        app.getEventSender().addEventListener(this, ApplicationEvent.class);
        app.getPageManager().addPageListener(commands);
        
        Template map = new Template();
        map.set("title","Java Examples");
        map.set("url","classpath:/org/freehep/jas/extension/compiler/web/examples.html");
        map.set("description","AIDA examples written in the Java language");
        app.getLookup().add(map,"examples");
    }
    
    /** Setter for property clearConsole.
     * @param clearConsole New value of property clearConsole.
     *
     */
    void setClearConsole(boolean clearConsole) {
        PropertyUtilities.setBoolean(props, "CompilerClearConsole", clearConsole);
    }
    
    /** Getter for property clearConsole.
     * @return Value of property clearConsole.
     *
     */
    boolean isClearConsole() {
        return PropertyUtilities.getBoolean(props, "CompilerClearConsole", true);
    }
    
    /**
     * Setter for property autoSave.
     * @param autoSave New value of property autoSave.
     *
     */
    void setSaveBeforeCompiling(boolean saveBeforeCompiling) {
        PropertyUtilities.setBoolean(props, "SaveBeforeCompiling", saveBeforeCompiling);
    }
    
    
    /**
     * Getter for property autoSave.
     * @return Value of property autoSave.
     *
     */
    boolean isSaveBeforeCompiling() {
        return PropertyUtilities.getBoolean(props, "SaveBeforeCompiling", true);
    }
    
    /** Setter for property compilerCommand.
     * @param compilerCommand New value of property compilerCommand.
     *
     */
    void setCompilerCommand(String compilerCommand) {
        props.setProperty("Compiler", compilerCommand);
    }
    
    /** Getter for property compilerCommand.
     * @return Value of property compilerCommand.
     *
     */
    String getCompilerCommand() {
        String result = props.getProperty("Compiler");
        if (result == null)
        {
           result = "javac";
           try
           {
              File javadir = new File(System.getProperty("java.home"));
              File javac = new File(javadir.getParentFile(),"bin/javac");
              if (javac.exists()) result = javac.getAbsolutePath();
              else
              {
                 javac = new File(javadir.getParentFile(),"bin/javac.exe");
                 if (javac.exists()) result = javac.getAbsolutePath();
                 else
                 {
                    // If we are on windows we can try the registry
                    javac = new File(WindowsHelper.getJDKLocation(),"bin/javac.exe");
                    if (javac.exists()) result = javac.getAbsolutePath();
                 }
              }              
           }
           catch (Exception x)
           {
              //x.printStackTrace();
           }
        }
        return result;
    }
    
    void setUseBuiltInCompiler(boolean useBuiltInCompiler) {
        PropertyUtilities.setBoolean(props, "UseBuiltInCompiler", useBuiltInCompiler);
    }

    boolean isUseBuiltInCompiler() {
        return isBuiltInCompilerAvailable() && PropertyUtilities.getBoolean(props, "UseBuiltInCompiler", true);
    }
    
    boolean isBuiltInCompilerAvailable() {
        // Currently the built-in compiler does not work with java 1.5
        return false;
    }
    private PrintStream getCompilerPrintStream() throws IOException {
        ConsoleService cs = (ConsoleService) getApplication().getLookup().lookup(ConsoleService.class);
        Console console = cs.getConsole("Compiler");
        if (console == null)
            console = cs.createConsole("Compiler", null);
        else if (isClearConsole())
            console.clear();
        
        ConsoleOutputStream os = console.getOutputStream(null,true);
        return new PrintStream(os);
    }
    
    public ClassLoader getClassLoader() {
        return classManager.getClassLoader();
    }
    
    public boolean accept(URL url) throws IOException {
        TextEditorService editor = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
        return editor != null && url.getFile().endsWith(".java");
    }
    
    public void openURL(URL url) throws IOException {
        Studio app = getApplication();
        
        try {
            String file = url.getFile();
            TextEditorService editor = (TextEditorService) app.getLookup().lookup(TextEditorService.class);
            if (editor != null) {
                // We copy the URL to a temporary file, to keep the Java compiler happy.
                Properties props = app.getUserProperties();
                File userJavaDir = new File(props.getProperty("userJava", "{user.home}/.{appName}/java"));
                userJavaDir.mkdirs();
                
                int pos = file.lastIndexOf('/');
                if (pos > 0)
                    file = file.substring(pos + 1);
                
                File f = new File(userJavaDir, file);
                f.deleteOnExit();
                
                InputStream in = url.openStream();
                OutputStream out = new FileOutputStream(f);
                try {
                    byte[] buf = new byte[4000];
                    for (;;) {
                        int l = in.read(buf);
                        if (l < 0)
                            break;
                        out.write(buf, 0, l);
                    }
                }
                finally {
                    in.close();
                    out.close();
                }
                editor.show(f, "text/java");
            }
        }
        catch (IOException x) {
            app.error("Error reading Java file", x);
        }
    }
    
    class Commands extends CommandProcessor implements PageListener {
        public void enableCompile(CommandState state) {
            TextEditorService tes = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
            TextEditor te = tes.getCurrentEditor();
            state.setEnabled((te != null) && te.getMimeType().equals(mimeType));
        }
        
        public void enableRun(CommandState state) {
            TextEditorService tes = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
            TextEditor te = tes.getCurrentEditor();
            state.setEnabled((te != null) && te.getMimeType().equals(mimeType));
        }
        
        
        public void onCompile() {
            TextEditorService tes = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
            TextEditor te = tes.getCurrentEditor();
            compile(te);
        }
        
        public void onJavaFile() {
            TextEditorService te = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
            te.show("", mimeType, null);
        }
        
        public void onLoadClass() throws MalformedURLException, ClassNotFoundException {
            LoadDialog load = new LoadDialog(getApplication(),classManager);
            load.showDialog(getApplication());
        }
        
        public void onRun() {
            TextEditorService tes = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
            TextEditor te = tes.getCurrentEditor();
            run(te);
        }
        
        public void pageChanged(PageEvent e) {
            setChanged();
        }
    }
}
