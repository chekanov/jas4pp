package org.freehep.jas.extensions.text.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.regex.Pattern;

/**
 *
 * @author Tony Johnson
 */
public class DelimiterManager
{
   private boolean tab = true;
   private boolean semicolon;
   private boolean comma;
   private boolean space = true;
   private boolean other;
   private String otherString;
   private boolean treatConsecutiveAsOne = true;
   private int textQualifier;
   private boolean regularExpressionSet;
   private Pattern pattern;
   private PropertyChangeSupport propertyChangeSupport =  new PropertyChangeSupport(this);
   
   public static final int QUALIFIER_NONE = 2;
   public static final int QUALIFIER_DOUBLEQUOTE = 0;
   public static final int QUALIFIER_SINGLEQUOTE = 1;
   
   public DelimiterManager()
   {
      buildRegularExpression();
   }
   private void buildRegularExpression()
   {
      // Note, we take some extra effort to try to make the generated expression
      // as simple as possible (to the human eye)
      if (!regularExpressionSet)
      {
         String delim = buildDelimiterList();
         StringBuffer result = new StringBuffer();
         if (textQualifier == QUALIFIER_DOUBLEQUOTE) result.append("\"([^\"]*)\"|");
         if (textQualifier == QUALIFIER_SINGLEQUOTE) result.append("'([^']*)'|");
         result.append('(');
         if (delim.length() ==0)  result.append('.');
         else result.append("[^").append(delim).append(']');
         if (treatConsecutiveAsOne) result.append('+');
         else                       result.append('*');
         result.append(')');
         if (!treatConsecutiveAsOne) result.append(".|$");
         setRegularExpression(result.toString());
      }
   }
   private String buildDelimiterList()
   {
      StringBuffer result = new StringBuffer();
      if (tab) addDelim(result,'\t');
      if (space) addDelim(result,' ');
      if (semicolon) addDelim(result,';');
      if (comma) addDelim(result,',');
      if (other && otherString != null) 
      {
         for (int i=0; i<otherString.length(); i++) 
         {
            addDelim(result,otherString.charAt(i));
         }
      }
      return result.toString();
   }
   private void addDelim(StringBuffer result, char x)
   {
      if      (x == '\t') result.append("\\t");
      else if (x == ' ') result.append(x);
      else if (x == ';') result.append(x);
      else if (x == ',') result.append(x);
      else if (Character.isLetterOrDigit(x)) result.append(x);
      else
      {
         String hex = Integer.toHexString((int)x);
         result.append("\\u").append("0000".substring(0,4-hex.length())).append(hex);
      }
   }
   public boolean isTab()
   {
      return this.tab;
   }

   public void setTab(boolean tab)
   {
      this.tab = tab;
      buildRegularExpression();
   }
   
   public boolean isSemicolon()
   {
      return this.semicolon;
   }
 
   public void setSemicolon(boolean semicolon)
   {
      this.semicolon = semicolon;
      buildRegularExpression();
   }
   
   public boolean isComma()
   {
      return this.comma;
   }
   
   public void setComma(boolean comma)
   {
      this.comma = comma;
      buildRegularExpression();      
   }
   
   public boolean isSpace()
   {
      return this.space;
   }
   
   public void setSpace(boolean space)
   {
      this.space = space;
      buildRegularExpression();     
   }

   public boolean isOther()
   {
      return this.other;
   }

   public void setOther(boolean other)
   {
      this.other = other;
      buildRegularExpression();
   }
   
   public String getOtherString()
   {
      return this.otherString;
   }
   
   public void setOtherString(String otherString)
   {
      this.otherString = otherString;
      buildRegularExpression();
   }
   
   public boolean isTreatConsecutiveAsOne()
   {
      return this.treatConsecutiveAsOne;
   }
   
   public void setTreatConsecutiveAsOne(boolean treatConsecutiveAsOne)
   {
      this.treatConsecutiveAsOne = treatConsecutiveAsOne;
      buildRegularExpression();
   }
   
   public int getTextQualifier()
   {
      return this.textQualifier;
   }
   
   public void setTextQualifier(int textQualifier)
   {
      this.textQualifier = textQualifier;
      buildRegularExpression();      
   }

   public boolean isRegularExpressionSet()
   {
      return this.regularExpressionSet;
   }
   
   public void setRegularExpressionSet(boolean regularExpressionSet)
   {
      this.regularExpressionSet = regularExpressionSet;
      buildRegularExpression();
   }
   
   public void addPropertyChangeListener(PropertyChangeListener l)
   {
      propertyChangeSupport.addPropertyChangeListener(l);
   }
   
   public void removePropertyChangeListener(PropertyChangeListener l)
   {
      propertyChangeSupport.removePropertyChangeListener(l);
   }
   public void removeAllListeners()
   {
      PropertyChangeListener[] l = propertyChangeSupport.getPropertyChangeListeners();
      for (int i=0; i<l.length; i++) propertyChangeSupport.removePropertyChangeListener(l[i]);
   }
   
   public String getRegularExpression()
   {
      return pattern.pattern();
   }
   
   public void setRegularExpression(String regularExpression)
   {
      String oldRegularExpression = pattern == null ? null : pattern.pattern();
      pattern = Pattern.compile(regularExpression);
      propertyChangeSupport.firePropertyChange("regularExpression", oldRegularExpression, regularExpression);
   } 
   public Pattern getPattern()
   {
      return pattern;
   }
}