package org.freehep.jas.extension.pnuts;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventObject;
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
import org.freehep.jas.services.DynamicClassLoader;
import org.freehep.jas.services.FileHandler;
import org.freehep.jas.services.ScriptEngine;
import org.freehep.jas.services.TextEditor;
import org.freehep.jas.services.TextEditorService;
import org.freehep.jas.services.URLHandler;
import org.freehep.jas.services.WebBrowser;
import org.freehep.swing.ExtensionFileFilter;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.images.ImageHandler;
import org.freehep.util.template.Template;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.xml.sax.SAXException;
import pnuts.compiler.CompilerPnutsImpl;
import pnuts.lang.Context;
import pnuts.lang.Package;
import pnuts.lang.ParseException;
import pnuts.lang.Pnuts;
import pnuts.lang.PnutsException;
import pnuts.tools.PnutsCompiler;

/**
 *
 * @author tonyj
 */
public class PnutsPlugin extends Plugin implements FileHandler, URLHandler, StudioListener
{
   private static Icon pnutIcon = ImageHandler.getIcon("pnuts16.png", PnutsCompiler.class);
   private static Icon runIcon = ImageHandler.getIcon("/toolbarButtonGraphics/media/FastForward16.gif", PnutsPlugin.class);
   
   private static String mimeType = "text/pnuts";
   private Commands commands;
   
   private static int nConsoles = 0;
   
   public FileFilter getFileFilter()
   {
      return new ExtensionFileFilter("pnut", "Pnuts File");
   }
   
   //*************************************//
   // Methods for the FileHandler service //
   //*************************************//
   public boolean accept(File file) throws IOException
   {
      return file.getName().endsWith(".pnut");
   }
   
   //****************************************//
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
                  PnutsThread t = new PnutsThread();
                  t.setPriority(t.NORM_PRIORITY - 1);
                  t.setTextEditor(event.getEditor());
                  t.start();
               }
            };
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
            item.setIcon(runIcon);
            menu.add(item);
         }
      }
      else if (e instanceof ApplicationEvent && (((ApplicationEvent) e).getID() == ApplicationEvent.INITIALIZATION_COMPLETE))
      {
         TextEditorService te = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
         if (te != null)
            te.addMimeType(mimeType, pnutIcon);
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
      URL xml = getClass().getResource("Pnuts.menus");
      builder.build(xml);
      
      app.getCommandTargetManager().add(commands = new Commands());
      app.getLookup().add(this);
      
      app.getEventSender().addEventListener(this, EditorPopupEvent.class);
      app.getEventSender().addEventListener(this, ApplicationEvent.class);
      app.getPageManager().addPageListener(commands);
      
      Template map = new Template();
      map.set("title", "Pnuts Examples");
      map.set("url", "classpath:/org/freehep/jas/extension/pnuts/web/examples.html");
      map.set("description", "AIDA examples written in the pnuts scripting language");
      app.getLookup().add(map, "examples");
   }
   
   void openConsole()
   {
      Thread t = new PnutsThread();
      t.setPriority(t.NORM_PRIORITY - 1);
      t.start();
   }
   
   static String version()
   {
      try
      {
         Class.forName("java.lang.Package");
         
         java.lang.Package pkg = Pnuts.class.getPackage();
         return pkg.getSpecificationVersion() + " (" + pkg.getImplementationVersion() + ")";
      }
      catch (ClassNotFoundException e)
      {
         return Pnuts.pnuts_version;
      }
   }
   
   public boolean accept(URL url) throws IOException
   {
      TextEditorService editor = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
      return editor != null && url.getFile().endsWith(".pnut");
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
      
      public void onPnutsConsole()
      {
         openConsole();
      }
      
      public void onPnutsScript()
      {
         TextEditorService te = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
         te.show("", mimeType, null);
      }
      
      public void onShowPnutsDocumentation() throws MalformedURLException
      {
         WebBrowser browser = (WebBrowser) getApplication().getLookup().lookup(WebBrowser.class);
         browser.showURL(new URL("http://pnuts.org/1.1/snapshot/20061118/doc/"),true);
      }
      
      public void onRun()
      {
         TextEditorService tes = (TextEditorService) getApplication().getLookup().lookup(TextEditorService.class);
         TextEditor te = tes.getCurrentEditor();
         PnutsThread t = new PnutsThread();
         t.setPriority(t.NORM_PRIORITY - 1);
         t.setTextEditor(te);
         t.start();
      }
      
      public void pageChanged(PageEvent e)
      {
         setChanged();
      }
   }
   
   private class PnutsThread extends Thread implements ScriptEngine
   {
      private Context context;
      private TextEditor te;
      private final Package rootPackage;
      private final String packageName;
      
      PnutsThread()
      {
         packageName = "rootPackage"+(nConsoles++);
         rootPackage = new Package(packageName, null);
      }
      
      public boolean canAccept(String mimeType)
      {
         return mimeType.equalsIgnoreCase(mimeType);
      }
      
      public void registerVariable(String name, Object value)
      {
         rootPackage.set(name.intern(), value, context);
      }
      
      public void run()
      {
         Studio app = getApplication();
         try
         {
            DynamicClassLoader compiler = (DynamicClassLoader) app.getLookup().lookup(DynamicClassLoader.class);
            // Create the Pnuts context
            context = new Context(rootPackage);
            if (compiler != null) context.setClassLoader(compiler.getClassLoader());
            context.setImplementation(new CompilerPnutsImpl());
            
            ConsoleService service = (ConsoleService) app.getLookup().lookup(ConsoleService.class);
            Console console = service.createConsole("Pnuts", pnutIcon);
            
            // Generate a scriptStarting event, to allow anyone to register variables
            EventSender es = app.getEventSender();
            if (es.hasListeners(ScriptEvent.class))
            {
               es.broadcast(new ScriptEvent(this));
            }
            
            ConsoleOutputStream out = console.getOutputStream(null);
            context.setTerminalWriter(new OutputStreamWriter(out), true);
            service.redirectStandardOutputOnThreadToConsole(this, out);
            
            SimpleAttributeSet red = new SimpleAttributeSet();
            red.addAttribute(StyleConstants.Foreground, Color.red);
            context.setErrorWriter(new OutputStreamWriter(console.getOutputStream(red)), true);
            
            context.getWriter().println("Pnuts version " + version());
            context.getWriter().flush();
            
            File profile = new File(app.getUserProperties().getProperty("pnutsProfile","{user.home}/.{appName}/pnuts/profile.pnut"));
            if (profile.canRead())
            {
               Pnuts.loadFile(profile.getAbsolutePath(),context);
            }
            
            if (te != null)
            {
               if ((te.getFile() != null) && !te.isModified())
               {
                  Pnuts.loadFile(te.getFile().getAbsolutePath(), context);
               }
               else if (te.getText() != null)
               {
                  Pnuts pn = Pnuts.parse(new StringReader(te.getText()));
                  pn.run(context);
               }
            }
            
            ConsoleInputStream in = console.getInputStream(". ");
            context.setTerminalWriter(new TerminalWriter(context.getWriter(), in));
            
            Pnuts.load(in, true, context);
            System.err.println("Pnuts thread exited");
            console.close();
         }
         catch (PnutsException x)
         {
            String message = "Execution error";
            if (x.getLine() == -1)
               message += " (for line info, save script before running).";
            else
               message += " at line " + x.getLine();
               if (x.getScriptSource() != null) message += " of " + x.getScriptSource();
            app.error(message, x.getThrowable());
         }
         catch (ParseException pe)
         {
            app.error("Parsing error at line " + pe.getErrorLine() + " column " + pe.getErrorColumn(), pe);
         }
         catch (Throwable t)
         {
            app.error("Pnuts thread exited unexpectedly", t);
         }
      }
      
      public void runScript(String file)
      {}
      
      private void setTextEditor(TextEditor te)
      {
         this.te = te;
      }
   }
   
   private class TerminalWriter extends PrintWriter
   {
      ConsoleInputStream cin;
      
      TerminalWriter(PrintWriter out, ConsoleInputStream cin)
      {
         super(out);
         this.cin = cin;
      }
      
      public void print(String s)
      {
         cin.setOneTimePrompt(s);
      }
      
      public void println(String s)
      {
         if (!s.equals("null"))
         {
            super.print(s);
            super.println();
         }
      }
   }
}
