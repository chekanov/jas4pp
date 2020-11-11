package org.freehep.jas.extensions.text.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.freehep.util.Value;

/**
 *
 * @author Tony Johnson
 */
public class IntegerFormat implements ColumnFormat
{
   private Matcher matcher = Pattern.compile("\\s*-?\\d+\\s*").matcher("");
   private static final int BADVALUE = -999;
   public boolean check(String token)
   {
      if (token == null) return false;
      matcher.reset(token);
      return matcher.matches();
   }
   public String getName()
   {
      return "Integer";
   }
   public void parse(Value value, String token)
   {
      try
      {
         if (token == null) value.set(BADVALUE);
         else value.set(Integer.parseInt(token.trim()));
      }
      catch (NumberFormatException x)
      {
         value.set(BADVALUE);
      }
   }
   
   public Class getJavaClass()
   {
      return Integer.TYPE;
   }
}