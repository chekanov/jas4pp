package org.freehep.jas.extensions.text.core;

/**
 *
 * @author Tony Johnson
 */
public class StringFormat implements ColumnFormat
{
   
   public boolean check(String token)
   {
      return true;
   }   
   
   public String getName()
   {
      return "String";
   }
   
   public void parse(org.freehep.util.Value value, String token)
   {
      value.set(token);
   }
   
   public Class getJavaClass()
   {
      return String.class;
   }
   
}
