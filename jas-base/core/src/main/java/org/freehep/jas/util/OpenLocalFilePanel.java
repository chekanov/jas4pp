package org.freehep.jas.util;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import org.freehep.application.Application;
import org.freehep.application.RecentItemTextField;


/**
 * This class provides a convenient way to get a file name from the user.  It is merely
 * a panel with a text field for the file name with some built in convenience items.
 * This panel does not actually open a file for you; it simply provides an easy way to get
 * a file name from the user.  Use <code>getText()</code> to get the file name selected.
 * <ul>
 *  <li>The text field is in fact a RecentItemTextField, so recently selected files are
 *  available on a drop-down list.  The drop-down list is stored in the Application's
 *  UserProperties object according to a key that you specify for the constructor.</li>
 *  <li>A "Browse..." button opens a file dialog so that the user can select a file.
 *  The object will remember the last directory used according to the given key and
 *  open the dialog to that directory the next time it is opened.</li>
 *  <li>Optionally, you can have a "Preview..." button that opens a dialog showing the
 *  beginning of the file.</li>
 *  <li>Optionally, you can have GZip option, where the user can select whether the file
 *  in in GZip format.</li>
 *  <li>If you add an <a href="http://www.javasoft.com/products/jdk/1.2/docs/api/java.awt.event.ActionListener.html">ActionListener</a>
 *  to this class, you will receive <a href="http://www.javasoft.com/products/jdk/1.2/docs/api/java.awt.event.ActionEvent.html">ActionEvents</a>
 *  from the RecentItemTextField.  An <a href="http://www.javasoft.com/products/jdk/1.2/docs/api/java.awt.event.ActionEvent.html">ActionEvent</a>
 *  will be sent only when the 'Enter' button is clicked in the text field.  Similarly,
 *  if you add a <a href="http://java.sun.com/products/jfc/swingdoc-api/javax.swing.event.ChangeListener.html#_top_">ChangeListener</a>
 *  to this class, you will receive <a href="http://java.sun.com/products/jfc/swingdoc-api/javax.swing.event.ChangeEvent.html#_top_">ChangeEvents</a>
 *  from the RecentItemTextField.  A <a href="http://java.sun.com/products/jfc/swingdoc-api/javax.swing.event.ChangeEvent.html#_top_">ChangeEvent</a>
 *  will be sent every time the visible text changes.</li>
 * </ul>
 * Be sure to invoke the <code>saveState()</code> method when you are done.  This will set
 * include the selected file in the drop-down list for the next time the panel is used.
 *  @author Jonas Gifford
 *  @author Tony Johnson
 *  @see #getText()
 *  @see #saveState()
 */

public class OpenLocalFilePanel extends JPanel
{
   /**
    * Creates an OpenLocalFilePanel component that you can add to a container.
    * @param includePreviewButton whether a "Preview" button should be shown
    * @param includeGZIP whether the GZip option should be shown
    * @param key the key used to store the drop-down items and the last directory for the browse dialog
    * @param filter sets a FileFilter for the browse dialog
    */
   public OpenLocalFilePanel(String key, FileFilter filter, boolean includePreviewButton, boolean includeGZIP)
   {
      this(key, includePreviewButton, includeGZIP);
      m_filter = filter;
   }
   
   /**
    * Creates an OpenLocalFilePanel component that you can add to a container.
    * @param includePreviewButton whether a "Preview" button should be shown
    * @param includeGZIP whether the GZip option should be shown
    * @param key the key used to store the drop-down items and the last directory for the browse dialog
    */
   public OpenLocalFilePanel(String key, boolean includePreviewButton, boolean includeGZIP)
   {
      m_lastLocalDirectory_Key = key +"-directory";
      m_fileName = new RecentItemTextField(key +"-files", 4, true);
      m_fileName.setMinWidth(250);
      m_fileName.setMaxWidth(250);
      m_fileName.addChangeListener(listener);
      add(m_fileName);
      
      m_browse = new JButton("Browse...");
      m_browse.addActionListener(listener);
      m_browse.setMnemonic('B');
      add(m_browse);
      
      if (includePreviewButton)
      {
         m_view = new JButton("Preview");
         m_view.addActionListener(listener);
         m_view.setMnemonic('V');
         add(m_view);
         m_view.setEnabled(false);
      }
      
      if (includeGZIP)
      {
         m_gzip = new JCheckBox("GZIPed");
         m_gzip.addActionListener(listener);
         m_gzip.setMnemonic('Z');
         add(m_gzip);
         m_gzip.setEnabled(false);
      }
      
      setBorder(BorderFactory.createTitledBorder("Enter the data file name"));
      checkIfFileExists();
   }
   private void checkIfFileExists()
   {
      String text = m_fileName.getText();
      boolean enabled = text.length() > 0;
      if (enabled)
      {
         File file = new File(text);
         enabled = file.exists() && !file.isDirectory() && file.canRead();
         if (m_gzip != null)
         {
            try
            {
               InputStream in = new FileInputStream(file);
               try
               {
                  InputStream gin = new GZIPInputStream(in);
                  gin.close();
                  m_gzip.setSelected(true);
               }
               finally
               {
                  in.close();
               }
            }
            catch (IOException x)
            {
               m_gzip.setSelected(false);
            }
         }
      }
      if (enabled != wasEnabled)
      {
         wasEnabled = enabled;
         fireStateChanged(event);
      }
      if (m_view != null) m_view.setEnabled(enabled);
      if (m_gzip != null) m_gzip.setEnabled(enabled);
   }
   private void fireStateChanged(ChangeEvent e)
   {
      ChangeListener[] listeners = (ChangeListener[]) listenerList.getListeners(ChangeListener.class);
      for (int i=listeners.length-1; i>=0; i--)
      {
         listeners[i].stateChanged(e);
      }
   }
   public boolean isFileSelected()
   {
      return wasEnabled;
   }
//   private void setViewEnabled()
//   {
//      String text = m_fileName.getText();
//      
//      boolean enable = text.length() > 0;
//      if (m_view != null) m_view.setEnabled(enable);
//      if (m_gzip != null)
//      {
//         m_gzip.setEnabled(enable);
//         m_gzip.setSelected(text.endsWith(".gz"));
//      }
//   }
   /**
    * The given ChangeListener will be notified when the visible
    * text changes in the file name field.
    */
//   public void addChangeListener(ChangeListener cl)
//   {
//      m_fileName.addChangeListener(cl);
//   }
//   public void removeChangeListener(ChangeListener cl)
//   {
//      m_fileName.addChangeListener(cl);
//   } 
   public void addChangeListener(ChangeListener cl)
   {
      listenerList.add(ChangeListener.class,cl);
   }
   public void removeChangeListener(ChangeListener cl)
   {
      listenerList.remove(ChangeListener.class,cl);
   }   
   /**
    * The given ActionListener will be notified when the "Enter" button
    * is clicked in the file name text field.
    */
   public void addActionListener(ActionListener al)
   {
      m_fileName.addActionListener(al);
   }
   public void removeActionListener(ActionListener al)
   {
      m_fileName.removeActionListener(al);
   }
   
   /**
    * Returns the file name showing in the text field.
    *  @return the selected file name
    */
   public String getText()
   {
      return wasEnabled ? m_fileName.getText() : "";
   }
   public File getFile()
   {
      return wasEnabled ? new File(m_fileName.getText()) : null;
   }
   
   /**
    * Includes the currently selected file name in the drop-down list for the next time
    * this class is instantiated.  It merely invokes the <code>saveState()</code> method in the
    * RecentItemTextField that is shown on the panel.
    *  @see org.freehep.application.RecentItemTextField
    *  @see org.freehep.application.RecentItemTextField#saveState()
    */
   
   public void saveState()
   {
      m_fileName.saveState();
   }
   
   /**
    * Returns whether the user has selected the GZip option.  If the GZip option was not
    * available (i.e., in the constructor the parameter <code>includeGZIP</code> was <code>false</code>) then it will
    * return <code>false</code>.
    *  @return whether the GZip option was selected
    */
   
   public boolean getGZIPed()
   {
      return m_gzip == null ? false : m_gzip.isSelected();
   }
   private String m_lastLocalDirectory_Key;
   private RecentItemTextField m_fileName;
   private JButton m_browse, m_view = null;
   private JCheckBox m_gzip = null;
   private Application app = Application.getApplication();
   final private Properties m_prop = app == null ? new Properties() : app.getUserProperties();
   private FileFilter m_filter = null;
   private MyListener listener = new MyListener();
   private boolean wasEnabled = false;
   private ChangeEvent event = new ChangeEvent(this);
   
   private class MyListener implements ActionListener, ChangeListener
   {
      public final void actionPerformed(ActionEvent e)
      {
         Object source = e.getSource();
         if (source == m_browse)
         {
            JFileChooser dlg = new JFileChooser(m_prop.getProperty(m_lastLocalDirectory_Key, "{user.home}"));
            dlg.setDialogTitle("Select a file");
            dlg.setApproveButtonText("Select");
            if (m_filter != null) dlg.setFileFilter(m_filter);
            if (dlg.showOpenDialog(OpenLocalFilePanel.this) == JFileChooser.APPROVE_OPTION)
            {
               final File file = dlg.getSelectedFile();
               m_fileName.setText(file.getAbsolutePath());
               m_prop.setProperty(m_lastLocalDirectory_Key, file.getParent());
            }
         }
         else if (source == m_view)
         {
            try
            {
               FilePreview.create(OpenLocalFilePanel.this, new File(m_fileName.getText()),
               m_gzip == null ? false : m_gzip.isSelected());
            }
            catch (Exception x)
            {
               Application.error(OpenLocalFilePanel.this,"Error opening file",x);
            }
         }
      }
      public final void stateChanged(ChangeEvent e)
      {
         checkIfFileExists();
      }
   }
   private static class FilePreview extends JDialog
   {
      static FilePreview create(Component parent, File f, boolean gzip) throws IOException, FileNotFoundException
      {
         Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class, parent);
         FilePreview result;
         if      (w instanceof Dialog) result = new FilePreview((Dialog) w);
         else if (w instanceof Frame)  result = new FilePreview((Frame) w);
         else                          result = new FilePreview();
         result.init(f,gzip);
         result.pack();
         result.setLocationRelativeTo(w);
         result.setVisible(true);
         return result;
      }
      private FilePreview()
      {
         super();
      }
      private FilePreview(Frame frame)
      {
         super(frame);
      }
      private FilePreview(Dialog dlg)
      {
         super(dlg);
      }
      private void init(File f, boolean gzip) throws IOException, FileNotFoundException
      {
         setTitle("File Preview");
         JTextArea text = new JTextArea();
         if (!gzip) text.read(new PreviewReader(new FileReader(f)), f);
         else text.read(new PreviewReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f)))), f);
         text.setEditable(false);
         JScrollPane pane = new JScrollPane(text);
         pane.setPreferredSize(new Dimension(400,400));
         setContentPane(pane);
      }
   }
   private static class PreviewReader extends LineNumberReader
   {
      PreviewReader(Reader r)
      {
         super(r);
      }
      public int read() throws IOException
      {
         return getLineNumber()>maxLines ? -1 : super.read();
      }
      public int read(char[] cbuf, int off, int len) throws IOException
      {
         return getLineNumber()>maxLines ? -1 : super.read(cbuf,off,len);
      }
      public String readLine() throws IOException
      {
         return getLineNumber()>maxLines ? null : super.readLine();
      }
      private final int maxLines = 100;
   }
}