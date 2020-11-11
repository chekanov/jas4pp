package org.freehep.jas.plugin.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

class ReaderInputStream extends InputStream
{
   private Reader in;
   private Writer out;
   private int pointer;
   private MyWriter writer;
   ReaderInputStream(Reader in)
   {
      this.in = in;
      writer = new MyWriter();
      out = new OutputStreamWriter(writer);
   }
   public int read(byte[] b, int off, int len) throws IOException
   {
      int l = writer.getCount() - pointer;
      if (l <= 0)
      {
         fillBuffer();
         l = writer.getCount();
         if (l == 0) return -1;
      }
      l = Math.min(len,l);
      System.arraycopy(writer.getBuf(), pointer, b, off, l);
      pointer += l;
      return l;
   }
   
   public int read() throws IOException
   {
      if (pointer >= writer.getCount())
      {
         fillBuffer();
         if (writer.getCount() == 0) return -1;
      }
      return writer.getBuf()[pointer++];
   }
   private void fillBuffer() throws IOException
   {
      pointer = 0;
      writer.clear();
      for (int i=0;i<1000;i++)
      {
         int c = in.read();
         if (c < 0) break;
         out.write(c);
      }
      out.flush();
   }
   
   public void close() throws IOException
   {
      in.close();
   }
   
   private class MyWriter extends ByteArrayOutputStream
   {
      void clear()
      {
         count = 0;
      }
      int getCount()
      {
         return count;
      }
      byte[] getBuf()
      {
         return buf;
      }
   }
}