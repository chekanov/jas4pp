package org.freehep.jas.extension.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import org.freehep.application.studio.Studio;
import org.freehep.jas.services.PreferencesTopic;

/**
 *
 * @author tonyj
 * @version $Id: ClassPathPreferences.java 13884 2011-09-20 23:10:19Z tonyj $
 */
public class ClassPathPreferences implements PreferencesTopic
{
   private final static String[] topic = { "Java", "Classpath" };
   private Studio app;
   private JASClassManager manager;

   /** Creates a new instance of ClassPathPreferences */
   public ClassPathPreferences(Studio app, JASClassManager manager)
   {
      this.app = app;
      this.manager = manager;
   }
   public boolean apply(JComponent panel)
   {
      manager.setClasspathFiles(((ClassPathPanel) panel).update());
      return true;
   }
   
   public JComponent component()
   {
      return new ClassPathPanel(app,manager.getClasspathFiles());
   }
   
   public String[] path()
   {
      return topic;
   }   
}
