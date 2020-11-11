package org.freehep.jas.extension.compiler;

import java.io.File;

import javax.swing.filechooser.FileFilter;


class ClassPathFilter extends FileFilter
{
   // The description of this filter
   public String getDescription()
   {
      return "ClassPath Files";
   }

   public boolean accept(File f)
   {
      if (f.isDirectory())
         return true;

      String s = f.getName();
      int i = s.lastIndexOf('.');
      if ((i > 0) && (i < (s.length() - 1)))
      {
         String extension = s.substring(i + 1).toLowerCase();
         if (extension.equals("jar"))
            return true;
      }
      return false;
   }
}
