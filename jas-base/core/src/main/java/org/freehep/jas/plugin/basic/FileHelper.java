package org.freehep.jas.plugin.basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

/**
 *
 * @author tonyj
 * @version $Id: FileHelper.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public class FileHelper
{
   private static final int KEYSIZE =  32;
   private Class klass;
   private EventListenerList listeners = new EventListenerList();
   private boolean fireListenersOnAWTThread = true;
   private Thread thread;
   private final boolean debug = Boolean.getBoolean("debugFileHelper");
   /** Creates a new instance of FileHelper 
    * @param klass The klass on whose behalf this FileHelper is working
    */
   public FileHelper(Class klass)
   {
      this.klass = klass;
   }
   public void addActionListener(ActionListener al)
   {
      listeners.add(ActionListener.class,al);
   }
   public void removeActionListener(ActionListener al)
   {
      listeners.remove(ActionListener.class,al);
   }
   protected void fireActionPerformed(ActionEvent e)
   {
      ActionListener[] al = (ActionListener[]) listeners.getListeners(ActionListener.class);
      for (int i=0; i<al.length; i++)
      {
         al[i].actionPerformed(e);
      }
   }
   public void start()
   {
      thread = new Thread()
      {
         public void run()
         {
            runHelper();
         }
      };
      thread.start();
   }
   public void stop()
   {
      thread.interrupt();
      try
      {
         Preferences prefs = Preferences.userNodeForPackage(klass);
         prefs.clear();
      }
      catch (BackingStoreException x) 
      {
         if (debug) x.printStackTrace();
      }
   }
   private void runHelper()
   {
      try
      {
         // Start by creating the random key
         SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
         byte[] key = new byte[KEYSIZE];
         rand.nextBytes(key);
         // Open a socket to listen on
         ServerSocket socket = new ServerSocket();
         socket.bind(null);
         // Now use the preferences API to store these
         Preferences prefs = Preferences.userNodeForPackage(klass);
         prefs.putByteArray("key", key);
         prefs.putInt("port",socket.getLocalPort());
         prefs.flush();
         for (;;)
         {
            try
            {
               // Now listen on the socket for incoming requests
               Socket connection = socket.accept();
               connection.setSoTimeout(2000);
               try
               {
                  InputStream in = connection.getInputStream();
                  // The first 32 bytes sent must be the key
                  for (int i=0; i<KEYSIZE; i++)
                  {
                     byte ii = (byte) in.read();
                     if (ii != key[i]) throw new IOException("Bad key");
                  }
                  // Rest of the message should be the command
                  Reader reader = new InputStreamReader(in);
                  StringBuffer message = new StringBuffer();
                  for (;;)
                  {
                     int i = reader.read();
                     if (i < 0) break;
                     message.append((char) i);
                  }
                  final ActionEvent e = new ActionEvent(this,0,message.toString());
                  if (fireListenersOnAWTThread)
                  {
                     Runnable run = new Runnable()
                     {
                        public void run()
                        {
                           fireActionPerformed(e);
                        }
                     };
                     SwingUtilities.invokeLater(run);
                  }
                  else fireActionPerformed(e);                
               }
               finally
               {
                  connection.close();
               }
            }
            catch (IOException x)
            {
               if (debug) x.printStackTrace();
            }
         }
      }
      catch (Throwable x)
      {
         if (debug) x.printStackTrace();
      }
   }
   public void send(String message) throws IOException
   {
      Preferences prefs = Preferences.userNodeForPackage(klass);
      int port = prefs.getInt("port",0);
      if (port == 0) throw new IOException("Missing port");
      byte[] key = prefs.getByteArray("key",null);
      if (key == null) throw new IOException("Missing key");
      
      Socket socket = new Socket();
      socket.setSoTimeout(2000);
      socket.connect(new InetSocketAddress(InetAddress.getLocalHost(),port));
      try
      {
         OutputStream out = socket.getOutputStream();
         out.write(key);
         Writer writer = new OutputStreamWriter(out);
         writer.write(message.toCharArray());
         writer.close();
         out.close();
      }
      finally
      {
         socket.close();
      }
   }
   
   /** Getter for property fireListenersOnAWTThread.
    * @return Value of property fireListenersOnAWTThread.
    *
    */
   public boolean isFireListenersOnAWTThread()
   {
      return this.fireListenersOnAWTThread;
   }
   
   /** Setter for property fireListenersOnAWTThread.
    * @param fireListenersOnAWTThread New value of property fireListenersOnAWTThread.
    *
    */
   public void setFireListenersOnAWTThread(boolean fireListenersOnAWTThread)
   {
      this.fireListenersOnAWTThread = fireListenersOnAWTThread;
   }
   
}