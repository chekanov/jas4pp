package org.freehep.jas.extensions.text.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.freehep.util.Value;

/**
 *
 * @author  Tony Johnson
 */
public class DoubleFormat implements ColumnFormat
{
   private final static String pattern = "\\s*-?(\\d+|\\d*\\.\\d+|\\d+\\.\\d*)((e|E)-?\\d+)?\\s*";
   private Matcher matcher = Pattern.compile(pattern).matcher("");
   public boolean check(String token)
   {
      if (token == null) return false;
      matcher.reset(token);
      return matcher.matches();
   }
   public String getName()
   {
      return "Double";
   }
   public void parse(Value value, String token)
   {
      try
      {
         if (token==null) value.set(Double.NaN);
         else value.set(Double.parseDouble(token));
      }
      catch (NumberFormatException x)
      {
         value.set(Double.NaN);
      }
   }
   
   public Class getJavaClass()
   {
      return Double.TYPE;
   }
   
}