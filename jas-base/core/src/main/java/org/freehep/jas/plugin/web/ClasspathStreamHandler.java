package org.freehep.jas.plugin.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * A stream handler which allows files to be read directly from the
 * classpath using classpath:/path syntax for URLS. Files read this way
 * are also fed through a simple template engine to allow basic token
 * substitution.
 * @author tonyj
 * @version $Id: ClasspathStreamHandler.java 13876 2011-09-20 00:52:21Z tonyj $
 */
class ClasspathStreamHandler extends URLStreamHandler
{
   private ClassLoader loader;
   ClasspathStreamHandler()
   {
      this(null);
   }
   ClasspathStreamHandler(ClassLoader loader)
   {
      this.loader = loader == null ? getClass().getClassLoader() : loader;
   }
   protected URLConnection openConnection(URL u) throws IOException
   {
      return new ClasspathURLConnection(u,loader);
   }
   
   static class ClasspathURLConnection extends URLConnection
   {
      private ClassLoader loader;
      private URLConnection conn;
      ClasspathURLConnection(URL u, ClassLoader loader)
      {
         super(u);
         this.loader = loader;
      }
      
      public void connect() throws IOException
      {
         String path = url.getPath();
         if (path.startsWith("/")) path = path.substring(1);
         URL resourceURL = loader.getResource(path);
         if (resourceURL == null) throw new IOException("Can not open "+url);
         conn = resourceURL.openConnection();
         if (conn == null) throw new IOException("Can not open "+url);
      }
      public InputStream getInputStream() throws IOException
      {
         if (conn == null) connect();
         return conn.getInputStream();
      }
      public Object getContent() throws IOException
      {
         if (conn == null) connect();
         return conn.getContent();
      }
      public String getContentType()
      {
         try
         {
            if (conn == null) connect();
            return conn.getContentType();
         }
         catch (IOException x)
         {
            return null;
         }
      }
      
      public long getDate()
      {
         try
         {
            if (conn == null) connect();
            return conn.getDate();
         }
         catch (IOException x)
         {
            return 0;
         }
      }
      
      public int getContentLength()
      {
         try
         {
            if (conn == null) connect();
            return conn.getContentLength();
         }
         catch (IOException x)
         {
            return -1;
         }
      }
      public long getLastModified()
      {
         try
         {
            if (conn == null) connect();
            return conn.getLastModified();
         }
         catch (IOException x)
         {
            return 0;
         }
      }
      
      public String getContentEncoding()
      {
         try
         {
            if (conn == null) connect();
            return conn.getContentEncoding();
         }
         catch (IOException x)
         {
            return null;
         }
      }
   }
}