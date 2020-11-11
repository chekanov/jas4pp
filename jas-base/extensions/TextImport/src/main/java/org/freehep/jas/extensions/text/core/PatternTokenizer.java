package org.freehep.jas.extensions.text.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Tony Johnson
 */
public class PatternTokenizer implements Tokenizer
{
   private Matcher matcher;
   private StringBuffer buffer;
   private boolean atEnd = true;
   
   public PatternTokenizer(Pattern pattern)
   {
      matcher = pattern.matcher("");
   }
   
   public String nextToken()
   {
      if (atEnd) return null;
      if (!matcher.find()) { atEnd = true; return null; }
      
      int count = matcher.groupCount();
      if      (count == 0) return "";
      else if (count == 1)
      {
         String result = matcher.group(1);
         return result == null ? "" : result;
      }
      else
      {
         if (buffer == null) buffer = new StringBuffer();
         else buffer.setLength(0);
         for (int i=1; i<=count; i++) 
         {
            String group = matcher.group(i);
            if (group != null) buffer.append(group);
         }
         return buffer.toString();
      }
   }   
   
   public void setLine(CharSequence in)
   {
      matcher.reset(in);
      atEnd = false;
   }
}
