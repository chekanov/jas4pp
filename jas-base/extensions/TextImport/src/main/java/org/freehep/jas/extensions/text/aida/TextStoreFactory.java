package org.freehep.jas.extensions.text.aida;

import hep.aida.dev.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.freehep.jas.extensions.text.core.TextMetaData;

/**
 * An implementation of IStoreFactory that creates a TextStore.
 */
public class TextStoreFactory implements IStoreFactory
{
   private static Map metaDataMap = new HashMap();
   public IStore createStore()
   {
      return new TextStore(metaDataMap);
   }
   public String description()
   {
      return "Text File";
   }
   public boolean supportsType(String type)
   {
      return "text".equalsIgnoreCase(type);
   }
   public static void registerMetaData(File file, TextMetaData meta) throws IOException
   {
      metaDataMap.put(file.getCanonicalPath(),meta);
   }
}