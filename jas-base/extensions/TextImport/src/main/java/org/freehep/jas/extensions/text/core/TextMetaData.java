package org.freehep.jas.extensions.text.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.freehep.xml.util.ClassPathEntityResolver;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

/**
 * This class encapsulates all the information needed to read a text file.
 * It is created by answering questions presented by the reader, 
 * scanning the file, reading a specification file, or
 * some combination of the above.
 * @author Tony Johnson
 */
public class TextMetaData
{
   private DelimiterManager delimiterManager;
   private int previewLines = 1000;
   private boolean gzip = false;

   private String commentDelimiter = "#";
   private int firstDataRow = 1;
   private int columnHeaderRow = 1;
   private int dataType = DELIMITED;
   
   public static final int FIXED = 1;
   public static final int DELIMITED = 0;
   
   private boolean commentsInFile = false;
   private boolean columnHeadersInFile = false;
   private BitSet columnSkip = new BitSet();
   private Vector columnHeaders = new Vector();
   private Vector columnFormats = new Vector();
   
   public TextMetaData()
   {
      delimiterManager = new DelimiterManager();
   }
   
   public DelimiterManager getDelimiterManager()
   {
      return this.delimiterManager;
   }
   
   public void setDelimiterManager(DelimiterManager delimiterManager)
   {
      this.delimiterManager = delimiterManager;
   }
   
   public String getColumnHeaders(int index)
   {
      if (index >= columnHeaders.size()) return null;
      else return (String) columnHeaders.get(index);
   }
   
   public void setColumnHeaders(int index, String value)
   {
      if (index >= columnHeaders.size()) columnHeaders.setSize(index+1);
      columnHeaders.set(index,value);
   }
   
   public int getPreviewLines()
   {
      return this.previewLines;
   }
   
   public void setPreviewLines(int previewLines)
   {
      this.previewLines = previewLines;
   }
   
   public boolean isGzip()
   {
      return this.gzip;
   }
   
   public void setGzip(boolean gzip)
   {
      this.gzip = gzip;
   }
   
   public ColumnFormat getColumnFormats(int index)
   {
      if (index >= columnFormats.size()) return null;
      return (ColumnFormat) columnFormats.get(index);
   }
   
   public void setColumnFormats(int index, ColumnFormat format)
   {
      if (index >= columnFormats.size()) columnFormats.setSize(index+1);
      columnFormats.set(index,format);
   }
   
   public Tokenizer getTokenizer()
   {
      return new PatternTokenizer(delimiterManager.getPattern());
   }  
   
   public String getCommentDelimiter()
   {
      return this.commentDelimiter;
   }
   
   public void setCommentDelimiter(String commentDelimiter)
   {
      this.commentDelimiter = commentDelimiter;
   }
   
   public int getFirstDataRow()
   {
      return this.firstDataRow;
   }
   
   public void setFirstDataRow(int firstDataRow)
   {
      this.firstDataRow = firstDataRow;
   }
   
   public int getColumnHeaderRow()
   {
      return this.columnHeaderRow;
   }
   
   public void setColumnHeaderRow(int columnHeaderRow)
   {
      this.columnHeaderRow = columnHeaderRow;
   }
   
   public int getDataType()
   {
      return this.dataType;
   }

   public void setDataType(int dataType)
   {
      this.dataType = dataType;
   }
   
   public void setCommentsInFile(boolean comment)
   {
      this.commentsInFile = comment;
   }
   
   public boolean hasCommentsInFile()
   {
      return commentsInFile;
   }
   
   public void setColumnHeadersInFile(boolean columnHeadersInFile)
   {
      this.columnHeadersInFile = columnHeadersInFile;
   }
   public boolean hasColumnHeadersInFile()
   {
      return columnHeadersInFile;
   }
   
   public boolean getColumnSkip(int index)
   {
      return columnSkip.get(index);
   }
   public BitSet getColumnSkip()
   {
      return columnSkip;
   }
   public void setColumnSkip(int index, boolean columnSkip)
   {
      this.columnSkip.set(index,columnSkip);
   }
   public void resetColumns()
   {
      columnSkip.clear();
      columnHeaders.clear();
      columnFormats.clear();
   }
   public void fromXML(InputStream in) throws IOException, JDOMException
   {
      SAXBuilder builder = new SAXBuilder(true);
      builder.setEntityResolver(new ClassPathEntityResolver("textmetadata.dtd",TextMetaData.class));
      Document doc = builder.build(in);
      
      Element root = doc.getRootElement();
      Element type = root.getChild("options");
      if (type != null)
      {
         commentDelimiter = type.getAttributeValue("comment");
         commentsInFile = commentDelimiter != null;
         String value = type.getAttributeValue("columnHeaderRow");
         columnHeadersInFile = (value != null); 
         columnHeaderRow = toInt(value,0);
         firstDataRow = toInt(type.getAttributeValue("firstDataRow"),1);
         previewLines = toInt(type.getAttributeValue("previewRows"),1000);
         gzip = toBoolean(type.getAttributeValue("gzip"),false);
      }
   
      Element delimited = root.getChild("delimited");
      if (delimited != null)
      {
         dataType = DELIMITED;
         String content = delimited.getTextNormalize();
         if (content != null) content.trim();
         if (content != null && content.length()>0)
         {
            delimiterManager.setRegularExpression(content);
            delimiterManager.setRegularExpressionSet(true);
         }
         else
         {
            delimiterManager.setRegularExpressionSet(false);
            delimiterManager.setTab(toBoolean(delimited.getAttributeValue("tabIsDelimiter"),false));
            delimiterManager.setSpace(false);
            delimiterManager.setSemicolon(false);
            delimiterManager.setComma(false);
                        
            String delimiters = delimited.getAttributeValue("delimiters");
            StringBuffer buffer = new StringBuffer();
            for (int i=0; i<delimiters.length(); i++)
            {
               char c = delimiters.charAt(i); 
               if      (c == '\t') delimiterManager.setTab(true);
               else if (c == ' ')  delimiterManager.setSpace(true);
               else if (c == ';')  delimiterManager.setSemicolon(true);
               else if (c == ',')  delimiterManager.setComma(true);
               else buffer.append(c);
            }
            delimiterManager.setOther(buffer.length() > 0);
            delimiterManager.setOtherString(buffer.toString());
            delimiterManager.setTreatConsecutiveAsOne(toBoolean(delimited.getAttributeValue("coalesceDelimiters"),true));
            
            String quote = delimited.getAttributeValue("textQualifier");
            if      (quote.charAt(0) == '\'') delimiterManager.setTextQualifier(DelimiterManager.QUALIFIER_SINGLEQUOTE);
            else if (quote.charAt(0) == '"')  delimiterManager.setTextQualifier(DelimiterManager.QUALIFIER_DOUBLEQUOTE);
            else delimiterManager.setTextQualifier(DelimiterManager.QUALIFIER_NONE);
         }
      }
      Element columns = root.getChild("columns");
      if (columns != null)
      {
         List list = columns.getChildren("column");
         for (Iterator i = list.iterator(); i.hasNext(); )
         {
            Element column = (Element) i.next();
            int index = column.getAttribute("index").getIntValue();
            if (toBoolean(column.getAttributeValue("skip"),false)) setColumnSkip(index, true);
            String name = column.getAttributeValue("name");
            if (name != null) setColumnHeaders(index,name);
            
            Element format = column.getChild("format");
            if (format != null)
            {
               String fName = format.getAttributeValue("name");
               setColumnFormats(index,FormatManager.formatForName(fName));
            }
         }
      }
   }
   private int toInt(String value, int defaultValue)
   {
      if (value == null) return defaultValue;
      else return Integer.parseInt(value);
   }
   private boolean toBoolean(String value, boolean defaultValue)
   {
      if (value == null) return defaultValue;
      else return Boolean.valueOf(value).booleanValue();
   }
   public void toXML(OutputStream out) throws IOException
   {
      Element root = new Element("textmetadata");
      Element type = new Element("options");
      if (commentsInFile && commentDelimiter!=null) type.setAttribute("comment",commentDelimiter);
      if (columnHeadersInFile) type.setAttribute("columnHeaderRow",String.valueOf(columnHeaderRow));
      if (firstDataRow != 1) type.setAttribute("firstDataRow",String.valueOf(firstDataRow));
      if (gzip) type.setAttribute("gzip","true");
      if (previewLines != 1000) type.setAttribute("previewRows",String.valueOf(previewLines));
      if (!type.getAttributes().isEmpty()) root.addContent(type);

      if (dataType == DELIMITED)
      {       
         Element delimited = new Element("delimited");
         root.addContent(delimited);
         if (delimiterManager.isRegularExpressionSet())
         {
            delimited.addContent(delimiterManager.getRegularExpression());
         }
         else
         {
            delimited.setAttribute("coalesceDelimiters",String.valueOf(delimiterManager.isTreatConsecutiveAsOne()));
            StringBuffer delims = new StringBuffer();
            if (delimiterManager.isComma())     delims.append(',');
            if (delimiterManager.isSpace())     delims.append(' ');
            if (delimiterManager.isSemicolon()) delims.append(';');
            if (delimiterManager.isOther())     delims.append(delimiterManager.getOtherString());
            String quote = null;
            if      (delimiterManager.getTextQualifier() == DelimiterManager.QUALIFIER_DOUBLEQUOTE) quote = "\"";
            else if (delimiterManager.getTextQualifier() == DelimiterManager.QUALIFIER_SINGLEQUOTE) quote = "'";
            
            delimited.setAttribute("delimiters",delims.toString());
            if (quote != null) delimited.setAttribute("textQualifier",quote);
            
            if (delimiterManager.isTab()) delimited.setAttribute("tabIsDelimiter","true");
         }
      }
      {
         Element columns = new Element("columns");
         int size = columnSkip.size();
         size = Math.max(size,columnHeaders.size());
         size = Math.max(size,columnFormats.size());
         for (int i=0; i<size; i++)
         {
            boolean skip = getColumnSkip(i);
            String header = getColumnHeaders(i);
            ColumnFormat format = getColumnFormats(i);
            if (skip || header != null || format != null)
            {
               Element column = new Element("column");
               columns.addContent(column);
               column.setAttribute("index",String.valueOf(i));
               if (skip) column.setAttribute("skip","true");
               if (header != null) column.setAttribute("name",header);
               if (format != null) 
               {
                  Element formatElement = new Element("format");
                  formatElement.setAttribute("name",format.getName());
                  column.addContent(formatElement);
               }
            }
         }
         if (!columns.getChildren().isEmpty()) root.addContent(columns);
      }   
      
      Document doc = new Document();
      doc.setRootElement(root);
      doc.setDocType(new DocType("textmetadata","http://java.freehep.org/schemas/textmetadata/1.0/textmetadata.dtd"));
      XMLOutputter writer = new XMLOutputter(Format.getPrettyFormat());
      writer.output(doc,out);
   }

}
