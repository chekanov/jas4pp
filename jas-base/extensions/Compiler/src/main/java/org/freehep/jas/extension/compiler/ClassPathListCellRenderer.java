package org.freehep.jas.extension.compiler;

import java.awt.Component;
import java.io.File;
import java.util.zip.ZipFile;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.freehep.util.images.ImageHandler;

/**
 * A Cell renderer for rendering classpath items
 */
class ClassPathListCellRenderer extends DefaultListCellRenderer
{
   private static Icon dir = ImageHandler.getIcon("images/folder.gif", ClassPathFileView.class);
   private static Icon jar = ImageHandler.getIcon("/toolbarButtonGraphics/development/Jar16.gif", ClassPathFileView.class);

   public Component getListCellRendererComponent(JList p1, Object p2, int p3, boolean p4, boolean p5)
   {
      Component c = super.getListCellRendererComponent(p1, p2, p3, p4, p5);
      if (c instanceof JLabel)
      {
         JLabel label = (JLabel) c;
         if (p2.toString().endsWith(".jar"))
            label.setIcon(jar);
         else
            label.setIcon(dir);
      }
      return c;
   }
}
