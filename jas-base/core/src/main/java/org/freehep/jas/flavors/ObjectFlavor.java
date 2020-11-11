package org.freehep.jas.flavors;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author  tonyj
 */
public class ObjectFlavor extends DataFlavor
{
   public ObjectFlavor(Class c)
   {
      super(c,"Java Object of class: "+c.getName());
   } 
   public String getMimeType()
   {
      return javaJVMLocalObjectMimeType;
   }
}