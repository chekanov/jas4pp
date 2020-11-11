package org.freehep.jas.extension.compiler;

import java.io.File;
import java.io.OutputStream;

/**
 *
 * @author tonyj
 * @version $Id: CompilerInterface.java 13884 2011-09-20 23:10:19Z tonyj $
 */
public interface CompilerInterface
{
   void setOutputStream(OutputStream out);
   void setClassPath(String classpath);
   void setOutputDir(File out);
   boolean compile(File file);
}