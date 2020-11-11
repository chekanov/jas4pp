package org.freehep.jas.extensions.text.aida;

import hep.aida.dev.IDevTree;
import hep.aida.dev.IStore;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Map;
import org.freehep.jas.extensions.text.core.*;

/**
 * An implementation of IStore for reading text files.
 * @author tonyj
 * @version $Id: TextStore.java,v 1.1.1.1 2004/05/21 00:12:31 tonyj Exp $
 */
class TextStore implements IStore
{
   private LineSource source;
   private Map metaDataMap;
   TextStore(Map map)
   {
      metaDataMap = map;
   }
   public boolean isReadOnly()
   {
      return true;
   }
   
   public void read(IDevTree tree, Map options, boolean readOnly, boolean createNew) throws IOException
   {
      String storeName = tree.storeName();
      File file = new File(storeName);
      if (!file.exists() && !file.canRead()) throw new IOException("Can not read: "+storeName);
      TextMetaData meta = findMetaDataForFile(file, options);
      TextUtilities util = new TextUtilities(meta);
      source = new BufferedLineSource(file,meta.isGzip());
      source.setLineComment(meta.getCommentDelimiter());
      String header = null;
      if (meta.hasColumnHeadersInFile())
      {
         source.setRow(meta.getColumnHeaderRow()-1);
         util.setHeader(source.getLine());
      }
      source.setStartLine(meta.getFirstDataRow()-1);
      
      // Scan to decide on column types
      LineSource temp = new InMemoryLineSource(source,meta.getPreviewLines());
      Tokenizer tokenizer = meta.getTokenizer();
      TypeScanner scanner = new TypeScanner(FormatManager.getAvailableFormats());
      scanner.scan(temp,tokenizer);
      int count = scanner.getColumnCount();
      util.setComputedColumnFormats(scanner.getFormats());
      temp.close();      
      
      String[] columnHeaders = new String[count];
      ColumnFormat[] columnFormats = new ColumnFormat[count];
      for (int i=0; i<count; i++)
      {
         columnHeaders[i] = util.getColumnName(i);
         columnFormats[i] = util.getColumnFormat(i);
      }      

      ValueSource vs = new ValueSource(source,tokenizer,columnFormats);
      TextTuple tuple = new TextTuple("tuple",vs,columnHeaders,columnFormats,meta.getColumnSkip());
      tree.add("/",tuple);
   }
   private TextMetaData findMetaDataForFile(File file, Map options) throws IOException
   {
      TextMetaData meta = (TextMetaData) metaDataMap.get(file.getCanonicalPath());
      if (meta == null) meta = TextUtilities.metaDataForFile(file);
      return meta;
   }
   public void commit(IDevTree tree, Map options) throws IOException
   {
      throw new UnsupportedOperationException();
   }
   
   public void close() throws IOException
   {
      source.close();
   }
}