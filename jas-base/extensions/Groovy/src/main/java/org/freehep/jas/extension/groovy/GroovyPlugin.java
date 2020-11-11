package org.freehep.jas.extension.groovy;

import org.freehep.util.ClasspathUtilities;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.EventObject;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.freehep.application.ApplicationEvent;
import org.freehep.application.mdi.PageEvent;
import org.freehep.application.mdi.PageListener;
import org.freehep.application.studio.EventSender;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.application.studio.StudioListener;
import org.freehep.jas.event.EditorPopupEvent;
import org.freehep.jas.event.ScriptEvent;
import org.freehep.jas.plugin.console.Console;
import org.freehep.jas.plugin.console.ConsoleInputStream;
import org.freehep.jas.plugin.console.ConsoleOutputStream;
import org.freehep.jas.plugin.console.ConsoleService;
import org.freehep.jas.services.*;
import org.freehep.swing.ExtensionFileFilter;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.images.ImageHandler;
import org.freehep.util.template.Template;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.xml.sax.SAXException;
import groovy.lang.GroovyShell;
import groovy.lang.Binding;

/**
 *
 * @author chekanov 
 */
public class GroovyPlugin extends Plugin implements FileHandler, URLHandler, StudioListener
{
   private static boolean init = false;
   private static Icon groovyIcon = ImageHandler.getIcon("groovy.gif", GroovyPlugin.class);
   private static Icon consoleIcon = ImageHandler.getIcon("groovycon.gif", GroovyPlugin.class);
   private static String mimeType = "text/groovy";

   public FileFilter getFileFilter()
   {
      return new ExtensionFileFilter(new String[] { "groovy", "gvy", "gy" }, "Groovy File");
   }

   public boolean accept(File file) throws IOException
   {
      return file.getName().endsWith(".groovy") || file.getName().endsWith(".gvy") || file.getName().endsWith(".gy");
   }

   public void handleEvent(EventObject e)
   {
      if (e instanceof EditorPopupEvent)
      {
         final EditorPopupEvent event = (EditorPopupEvent) e;
         String type = event.getEditor().getMimeType();
         if (type.equals(mimeType))
         {
            JPopupMenu menu = event.getMenu();
            JMenuItem item = new JMenuItem("Run Script", 'R')
            {
               public void fireActionPerformed(ActionEvent e)
               {
                  GroovyThread t = new GroovyThread();
                  t.setPriority(t.NORM_PRIORITY - 1);
                  t.setFile(event.getEditor().getText());
                  t.start();
               }
            };
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
            item.setIcon(consoleIcon);
            menu.add(item);
         }
      }
      else if (e instanceof ApplicationEvent && (((ApplicationEvent) e).getID() == ApplicationEvent.INITIALIZATION_COMPLETE))
      {
         TextEditorService te = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
         if (te != null)
            te.addMimeType(mimeType, groovyIcon);
      }
   }

   public void openFile(File file) throws IOException
   {
      TextEditorService te = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
      te.show(file, mimeType);
   }

   protected void init() throws SAXException, IOException
   {
      Studio app = getApplication();
      XMLMenuBuilder builder = app.getXMLMenuBuilder();
      URL xml = getClass().getResource("Groovy.menus");
      builder.build(xml);

      Commands commands = new Commands();
      app.getCommandTargetManager().add(commands);
      app.getLookup().add(this);

      app.getEventSender().addEventListener(this, EditorPopupEvent.class);
      app.getEventSender().addEventListener(this, ApplicationEvent.class);
      app.getPageManager().addPageListener(commands);
      
      Template map = new Template();
      map.set("title", "Groovy Examples");
      map.set("url", "classpath:/org/freehep/jas/extension/groovy/web/examples.html");
      map.set("description", "Examples written in the Groovy scripting language");
      app.getLookup().add(map, "examples");
   }
   protected void postInit() 
   {
      Studio app = getApplication();

      // set some properties so that jython can create and load the package cache correctly
      String prop = app.getUserProperties().getProperty("userGroovyCache", "{user.home}/.{appName}/groovyCache");
      Properties sysProps = System.getProperties();
      sysProps.setProperty("groovy.cachedir", prop);
      sysProps.setProperty("groovy.packages.paths", "java.class.path,sun.boot.class.path,app.class.path,java.ext.dirs");
     //sysProps.setProperty("groovy.packages.paths", "java.class.path");

     //The call below is to extract all jar files defined in the Class-Path Manifest element for all
     //the jars on the System Classpath. This is because Groovy does not do that.
     //ClasspathUtilities.setFullSystemClasspathInSystemProperty("full.java.class.path");

     StringBuffer appPath = new StringBuffer();
     URL[] urls = app.getExtensionLoader().getURLs();

    // ClassLoader cl = ClassLoader.getSystemClassLoader();
    // URL[] urls = ((URLClassLoader)cl).getURLs();
    //String classpath = System.getProperty("java.ext.dirs"); 
    //ClassLoader systemClassLoader = System.class.getClassLoader();
    //System.out.println("systemClassLoader="+classpath);

      for (int i = 0; i < urls.length;)
      {
         try
         {
            URI uri = new URI(urls[i].toExternalForm());
            File file = new File(uri.normalize());
            appPath.append(file.getAbsolutePath());
            // System.out.println(file.getAbsolutePath());
            if (urls.length == ++i) break;
            appPath.append(File.pathSeparatorChar);
         }
         catch (URISyntaxException x)
         {
            x.printStackTrace();
         }
      }

     sysProps.setProperty("app.class.path", appPath.toString());

   }

   void openConsole()
   {
      //Thread t = new PythonThread();
      //t.setPriority(t.NORM_PRIORITY - 1);
      //t.start();
   }

   public boolean accept(URL url) throws IOException
   {
      TextEditorService editor = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
      return editor != null && (url.getFile().endsWith(".groovy") || url.getFile().endsWith(".gvy") || url.getFile().endsWith(".gy") );
   }
   
   public void openURL(URL url) throws IOException
   {
      TextEditorService editor = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
      if (editor != null) editor.show(url, mimeType);
   }
   
   public class Commands extends CommandProcessor implements PageListener
   {
      public void enableRun(CommandState state)
      {
         // Only enabled if current window is an editor && the mimetype
         // is approptiate.
         TextEditorService tes = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
         TextEditor te = tes.getCurrentEditor();
         state.setEnabled((te != null) && mimeType.equals(te.getMimeType()));
      }

      public void onGroovyConsole()
      {
         openConsole();
      }

      public void onGroovyScript()
      {
         TextEditorService te = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
         te.show("", mimeType, null);
      }

      public void onRun()
      {
         TextEditorService tes = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
         TextEditor te = tes.getCurrentEditor();
         GroovyThread t = new GroovyThread();
         t.setPriority(t.NORM_PRIORITY - 1);
         t.setFile(te.getText());
         t.start();
      }

      public void pageChanged(PageEvent e)
      {
         setChanged();
      }
   }

   private class GroovyThread extends Thread implements ScriptEngine
   {

      private Binding binding;
      private GroovyShell interp;
      private String file;

      public boolean canAccept(String mimeType)
      {
         return mimeType.equalsIgnoreCase(mimeType);
      }

      public void registerVariable(String name, Object value)
      {
         //interp.set(name, value);
      }

      public void run()
      {
         Studio app = getApplication();
         if (!init)
         {
            //PythonInterpreter.initialize(app.getAppProperties(), app.getUserProperties(), null);
            init = true;
         }
         try
         {

/*
            //Now start the interpreter
            PySystemState state = new PySystemState();
            DynamicClassLoader compiler = (DynamicClassLoader) app.getLookup().lookup(DynamicClassLoader.class);
            if (compiler != null) state.setClassLoader(compiler.getClassLoader());            
            interp = new JASInteractiveInterpreter(state);
*/

            ConsoleService service = (ConsoleService) app.getLookup().lookup(ConsoleService.class);
            Console console = service.createConsole("Groovy", consoleIcon);

            // Generate a scriptStarting event, to allow anyone to register variables
            EventSender es = app.getEventSender();
            if (es.hasListeners(ScriptEvent.class))
            {
               es.broadcast(new ScriptEvent(this));
            }
            ConsoleOutputStream out = console.getOutputStream(null);

            binding = new Binding();
            binding.setProperty("out", out); 
            interp = new GroovyShell( binding );

            //interp.setOut(out);
            service.redirectStandardOutputOnThreadToConsole(this, out);

            SimpleAttributeSet red = new SimpleAttributeSet();
            red.addAttribute(StyleConstants.Foreground, Color.red);

/*
            interp.setErr(console.getOutputStream(red));
*/

            if (file != null)
            {
               interp.evaluate(file);
            }

            ConsoleInputStream in = console.getInputStream(">>> ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuffer commands = new StringBuffer();

/*
            for (;;)
            {
               String line = reader.readLine();
               if (line == null)
               {
                  break;
               }
               if (interp.buffer.length() > 0)
               {
                  interp.buffer.append("\n");
               }
               interp.buffer.append(line);

               boolean more = interp.runsource(interp.buffer.toString(), "console");
               if (!more)
               {
                  interp.resetbuffer();
                  in.setPrompt(">>> ");
               }
               else
               {
                  in.setPrompt("... ");

                  StringBuffer initialEntry = new StringBuffer();
                  for (int i = 0; i < line.length(); i++)
                  {
                     if (line.charAt(i) == ' ')
                        initialEntry.append(' ');
                     else
                        break;
                  }
                  if (line.endsWith(":"))
                     initialEntry.append("  ");
                  in.setInitialEntry(initialEntry.toString());
               }
            }
*/ 

        }
         catch (Throwable t)
         {
            app.error("Groovy thread exited", t);
         }
      }

      public void runScript(String file) {}

      private void setFile(String file)
      {
         this.file = file;
      }
   }
}
