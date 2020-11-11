package org.freehep.jas.extension.compiler;

import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileView;
import org.freehep.util.images.ImageHandler;


class ClassPathFileView extends FileView
{
   private static Icon dir = ImageHandler.getIcon("images/folder.gif", ClassPathFileView.class);
   private static Icon jar = ImageHandler.getIcon("/toolbarButtonGraphics/development/Jar16.gif", ClassPathFileView.class);

   public String getDescription(File f)
   {
      return null; // let the FileView figure this out
   }

   public Icon getIcon(File f)
   {
      if (f.isDirectory())
         return dir;
      else
         return jar;
   }

   public String getName(File f)
   {
      return null; // let the FileView figure this out
   }

   public Boolean isTraversable(File f)
   {
      return null; // let the FileView figure this out
   }

   public String getTypeDescription(File f)
   {
      return null; // let the FileView figure this out		
   }
}
