package org.freehep.jas.extensions.text.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  Tony Johnson
 */
public class TextUtilities
{
   private final static String alphabet = "abcdefghijklmnopqrstuvwxyz";
   protected TextMetaData meta;
   private String header;
   protected String headers[];
   private ColumnFormat[] formats;

   public TextUtilities(TextMetaData meta)
   {
      this.meta = meta;
   }
   public static TextMetaData metaDataForFile(File file)
   {
      TextMetaData meta = new TextMetaData();
      File specFile = new File(file.getAbsolutePath()+".spec");
      if (specFile.exists() && specFile.canRead())
      {
         try
         {
            InputStream in = new FileInputStream(specFile);
            meta.fromXML(in);
            in.close();
         }
         catch (Throwable x)
         {
            System.err.println("Could not read spec file");
            x.printStackTrace();
         }
      }
      return meta;
   }
   
   private String generateColumnHeader(int index)
   {
      int len = alphabet.length();
      if (index<len) return alphabet.substring(index,index+1);
      else
      {
         StringBuffer result = new StringBuffer();
         while (index > 0)
         {
            result.insert(0,alphabet.substring(index%len,index%len+1));
            index /= len;
         }
         return result.toString();
      }
   }  
   public String getColumnName(int columnIndex)
   {
      String result = meta.getColumnHeaders(columnIndex);
      if (result == null && meta.hasColumnHeadersInFile())
      {
         if (headers == null) computeColumnHeaders();
         if (headers != null && columnIndex < headers.length) result = (String) headers[columnIndex];
      }
      if (result == null) result = generateColumnHeader(columnIndex);
      return result;
   }
   private void computeColumnHeaders()
   {
      Tokenizer tokenizer = meta.getTokenizer();
      if (header != null)
      {
         tokenizer.setLine(header);
         List list = new ArrayList();
         for (;;)
         {
            String token = tokenizer.nextToken();
            if (token == null) break;
            list.add(token);
         }
         headers = new String[list.size()];
         list.toArray(headers);
      }
   }
   public ColumnFormat getColumnFormat(int columnIndex)
   {
      ColumnFormat format = meta.getColumnFormats(columnIndex);
      if (format == null) format = formats[columnIndex];
      return format;
   }
   public void setComputedColumnFormats(ColumnFormat[] formats)
   {
      this.formats = formats; 
   }
   public void setHeader(String header)
   {
      this.header = header;
   }
   public TextMetaData getMetaData()
   {
      return meta;
   }
}
