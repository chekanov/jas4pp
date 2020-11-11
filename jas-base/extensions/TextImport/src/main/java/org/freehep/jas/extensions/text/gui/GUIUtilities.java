package org.freehep.jas.extensions.text.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.freehep.jas.extensions.text.core.*;

/**
 *
 * @author Tony Johnson
 */
class GUIUtilities extends TextUtilities
{
   private File file;

   private LineSource source;
   private LineSource preview;

   GUIUtilities(File file, boolean isGzip) throws IOException
   {
      this(file,null, isGzip);
   }
   GUIUtilities(File file, TextMetaData meta) throws IOException
   {
      this(file,meta,meta.isGzip());
   }
   private GUIUtilities(File file, TextMetaData meta, boolean isGzip) throws IOException
   {
      super(meta);
      this.file = file;
      if (meta == null) this.meta = metaDataForFile(file);
      source = new BufferedLineSource(file, isGzip);
   }
   /*
    * Returns a line source suitable for use as a preview 
    */
   LineSource getPreview()
   {
      if (preview == null)
      {
         preview = new InMemoryLineSource(source, 1000);
      }
      return preview;
   }
   public void dispose() throws IOException
   {
      if (preview != null) preview.close();
      source.close();
      getMetaData().getDelimiterManager().removeAllListeners();
   }

   void headersChanged()
   {
      headers = null;
   }
   
   File getFile()
   {
      return file;
   }
}