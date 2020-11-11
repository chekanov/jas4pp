package org.freehep.jas.extension.compiler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.freehep.application.studio.Studio;
import org.freehep.jas.event.ClassLoadedEvent;
import org.freehep.jas.event.ClassUnloadEvent;
import org.freehep.jas.plugin.console.ConsoleOutputStream;
import org.freehep.jas.plugin.console.ConsoleService;
import org.freehep.jas.plugin.tree.FTree;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreeNodeAddedNotification;
import org.freehep.jas.plugin.tree.FTreeNodeObjectProvider;
import org.freehep.jas.plugin.tree.FTreeNodeRemovedNotification;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.jas.plugin.tree.FTreeProvider;

/**
 *
 * @author tonyj
 * @version $Id: JASClassManager.java 13884 2011-09-20 23:10:19Z tonyj $
 */
public class JASClassManager
{
   private FTree tree;
   private static boolean programNodeCreated = false;
   private JASClassLoader loader;
   private Set loadedClasses = new HashSet();
   private Map classMap = new HashMap();
   private CompilerPlugin plugin;
   private Studio app;
   private final static String programNode = "Programs";
   
   /** Creates a new instance of JASClassLoader */
   public JASClassManager(Studio app, CompilerPlugin plugin, ClassLoader parent, FTree tree)
   {
      this.app = app;
      this.plugin = plugin;
      this.tree = tree;
      FTreeProvider treeProvider = (FTreeProvider) app.getLookup().lookup(FTreeProvider.class);
      treeProvider.treeNodeAdapterRegistry().registerNodeAdapter( new ProgramsAdapter(), JASClassLoader.class );
      treeProvider.treeNodeAdapterRegistry().registerNodeAdapter( new ProgramAdapter(), Class.class );

      loader = createClassLoader(parent);
   }
   private JASClassLoader createClassLoader(ClassLoader parent)
   {
      JASClassLoader loader = new JASClassLoader(this,parent);
      List files = getClasspathFiles();
      for (Iterator i = files.iterator(); i.hasNext(); ) 
      {
         try
         {
            File f = (File) i.next();
            loader.addURL(f.toURL());
         }
         catch (MalformedURLException x)
         {
         }
      }
      return loader;
   }
   List getClasspathFiles()
   {
      StringTokenizer st = new StringTokenizer(getClasspath(),File.pathSeparator);
      int n = st.countTokens();
      List files = new ArrayList(n);
      for (int i=0; i<n; i++) files.add(new File(st.nextToken()));
      return Collections.unmodifiableList(files);
   }
   void setClasspathFiles(List files)
   {
      // If we the list is unchanged, or has only new items, then keep current 
      // class loader. Otherwise we need to reload.b
      List oldFiles = getClasspathFiles();
      boolean onlyNew = files.containsAll(oldFiles);
      
      StringBuffer b = new StringBuffer();
      for (Iterator i = files.iterator(); i.hasNext(); )
      {
         b.append(i.next());
         b.append(File.pathSeparatorChar);
      }
      if (b.length() > 0) b.setLength(b.length() - 1);
      setClasspath(b.toString());
      
      if (onlyNew)
      {
         for (Iterator i = files.iterator(); i.hasNext(); )
         {
            File f = (File) i.next();
            try
            {
               if (!oldFiles.contains(f)) loader.addURL(f.toURL());
            }
            catch (MalformedURLException x) {}
         }
      }
      else reload();
   }
   
   String getClasspath()
   {
      Properties props = app.getUserProperties();
      return props.getProperty("JASClassPath",plugin.getOutputDirectory().getAbsolutePath());
   }
   
   private void setClasspath(String classpath)
   {
      Properties props = app.getUserProperties();
      props.setProperty("JASClassPath", classpath);
   }
   ClassLoader getClassLoader()
   {
      return loader;
   }
   Class loadClass(String name) throws ClassNotFoundException
   {
      Class x = loader.loadClass(name);
      loadedClasses.add(name);
      // Fire an event to tell the rest of the world this class is available!
      app.getEventSender().broadcast(new ClassLoadedEvent(this,x));
      return x;
   }
   void reload()
   {
      clearTree();
      loader = createClassLoader(loader.getParent());
      Iterator i = loadedClasses.iterator();
      loadedClasses = new HashSet();
      // Fire an unload event
      app.getEventSender().broadcast(new ClassUnloadEvent(this));
      
      while (i.hasNext())
      {
         String name = (String) i.next();
         try
         {
            loadClass(name);
         }
         catch (Throwable t)
         {
            app.error("Error reloading class: "+name,t);
         }
      }
   }
   void unload()
   {
      clearTree();
      loadedClasses.clear();
      loader = createClassLoader(loader.getParent());
      // Fire an unload event
      app.getEventSender().broadcast(new ClassUnloadEvent(this));
   }
   private void clearTree()
   {
      for (Iterator i = classMap.keySet().iterator(); i.hasNext(); )
      {
         String[] fPath = { programNode, (String) i.next() };
         FTreePath path = new FTreePath(fPath);
         FTreeNodeRemovedNotification e = new FTreeNodeRemovedNotification(this, path);
         tree.treeChanged(e);
      }
      classMap.clear();
   }
   void classLoaded(Class x)
   {
      if (tree != null)
      {
         if (!programNodeCreated)
         {
            FTreePath path = new FTreePath(programNode);
            FTreeNodeAddedNotification e = new FTreeNodeAddedNotification(this, path, JASClassLoader.class);
            tree.treeChanged(e);   
            programNodeCreated = true;
         }
         // Build a tree path
         String[] sPath = { programNode, x.getName() };
         FTreePath path = new FTreePath(sPath);
         FTreeNodeAddedNotification e = new FTreeNodeAddedNotification(this, path, Class.class);
         tree.treeChanged(e);         
         classMap.put(x.getName(),  x);
      }
   }
   private boolean isMainClass(Class x)
   {
      try
      {
         Class[] args = { String[].class };
         Method main = x.getMethod("main", args);
         return true;
      }
      catch (NoSuchMethodException xx) { return false; }
   }
   void run(final Class x)
   {
      try
      {
         Class[] argc = { String[].class };
         final Object[] args = { new String[0] };
         final Method main = x.getMethod("main", argc);
         
         Thread t = new Thread()
         {
            public void run()
            {
               try
               {
                  main.invoke(null, args);
               }
               catch (final Throwable xx)
               {
                  Runnable end = new Runnable()
                  {
                     public void run()
                     {
                        if (xx instanceof InvocationTargetException)
                        {
                           app.error("Error running " + x.getName(), ((InvocationTargetException) xx).getTargetException());
                        }
                        else
                        {
                           app.error("Error running " + x.getName(), xx);
                        }
                     }
                  };
                  SwingUtilities.invokeLater(end);
               }
            }
         };
         // Try to get a console to send output to
         ConsoleService cs = (ConsoleService) app.getLookup().lookup(ConsoleService.class);
         if (cs != null)
         {
            try
            {
               ConsoleOutputStream out = cs.getConsoleOutputStream(x.getName(),CompilerPlugin.javaIcon);
               cs.redirectStandardOutputOnThreadToConsole(t,out);
            }
            catch (IOException xx)
            {
               xx.printStackTrace();
            }
         }
         t.start();
      }
      catch (NoSuchMethodException xx)
      {
         app.error("Error running "+x.getName(), xx);
      }
   }
   private class ProgramsAdapter extends DefaultFTreeNodeAdapter implements ActionListener
   {
      ProgramsAdapter()
      {
         super(100);
      }
      public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu)
      {
         JMenuItem item = new JMenuItem("Unload All");
         item.setActionCommand("unload");
         item.addActionListener(this);
         menu.add(item);
         item = new JMenuItem("Reload All");
         item.setActionCommand("reload");
         item.addActionListener(this);
         menu.add(item);
         return menu;
      }
      public void actionPerformed(ActionEvent e)
      {
         String command = e.getActionCommand();
         if      (command.equals("unload")) unload();
         else if (command.equals("reload")) reload();
      }      
      public boolean allowsChildren(FTreeNode node, boolean allowsChildren)
      {
         return true;
      }      
   }
   private class ProgramAdapter extends DefaultFTreeNodeAdapter implements ActionListener
   {
      ProgramAdapter()
      {
         super(100, new ClassObjectProvider() );
      }
      public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu)
      {
         Class node = (Class) nodes[0].objectForClass(Class.class);
         JMenuItem item = new JMenuItem("Go To Source");
         item.setEnabled(false);
         item.addActionListener(this);
         menu.add(item);
         item = new JMenuItem("Run");
         item.setActionCommand("run");
         item.setEnabled(isMainClass(node));
         item.addActionListener(this);
         menu.add(item);
         item = new JMenuItem("Properties");
         item.setEnabled(false);
         item.addActionListener(this);
         menu.add(item);
         return menu;
      }      

      public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded)
      {
         return CompilerPlugin.javaIcon;
      }        
      
      public void actionPerformed(ActionEvent e)
      {
          FTreeNode[] nodes = tree.selectedNodes();
         String command = e.getActionCommand();
         if (command.equals("run"))
         {
            Class node = (Class) nodes[0].objectForClass(Class.class);
            run(node);
         }
      }
   }
   
   class ClassObjectProvider implements FTreeNodeObjectProvider {

       public Object objectForNode(FTreeNode node, Class clazz) {
           if ( clazz == Class.class )
               return classMap.get(node.path().getLastPathComponent());
           return null;
       }       
       
   }
}
