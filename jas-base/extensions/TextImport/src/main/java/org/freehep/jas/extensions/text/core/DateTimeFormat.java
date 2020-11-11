package org.freehep.jas.extensions.text.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Tony Johnson
 */
public class DateTimeFormat implements ColumnFormat
{
   private SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
   public boolean check(String token)
   {
      try
      {
         if (token == null) return false;
         format.parse(token);
         return true;
      }
      catch (ParseException x)
      {
         return false;
      }
   }
   
   public String getName()
   {
      return "Date+Time";
   }
   
   public void parse(org.freehep.util.Value value, String token)
   {
      try
      {
         value.set(format.parse(token));
      }
      catch (ParseException x)
      {
         value.set((Date) null);
      }
   }
   
   public Class getJavaClass()
   {
      return Date.class;
   }
   
}
