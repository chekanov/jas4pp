package org.freehep.jas.plugin.web;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Dictionary;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;
import org.freehep.application.ProgressMeter;
import org.freehep.application.StoppableInputStream;
import org.freehep.application.mdi.ManagedPage;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.studio.Studio;
import org.freehep.jas.services.FileHandler;
import org.freehep.jas.services.ProgressMeterProvider;
import org.freehep.jas.services.HTMLEditorKitProvider;
import org.freehep.jas.services.TextEditorService;
import org.freehep.jas.services.URLHandler;
import org.freehep.swing.popup.HasPopupItems;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.template.TemplateEngine;
import org.openide.util.Lookup.Result;
import org.openide.util.Lookup.Template;

class HTMLPage extends JPanel implements HasPopupItems, ManagedPage
{
   private HTMLCommand htmlCommand = new HTMLCommand();
   private HTMLEditorPane jep;
   //private JASHTMLEditorKit editorKit = new JASHTMLEditorKit();
   private HTMLEditorKit editorKit;
   private JScrollPane jsp;
   private PageContext pageContext;
   private SimpleWebBrowser plugin;
   private Studio app;
   private URL homePage;
   private URLHistory urlHistory;
   
   HTMLPage(URL homePage, Studio app, SimpleWebBrowser plugin)
   {
      super(new BorderLayout());
      this.homePage = homePage;
      this.plugin = plugin;
      this.app = app;
      
      HTMLEditorKitProvider hekp = (HTMLEditorKitProvider) app.getLookup().lookup(HTMLEditorKitProvider.class);
      if (hekp != null)
      {
         editorKit = hekp.getHTMLEditorKit();
         try
         {
            java.lang.reflect.Method method = editorKit.getClass().getDeclaredMethod("setURL", new Class[] { URL.class });
            if (method != null) method.invoke(editorKit, new Object[] { homePage });
         }
         catch (Exception ex)
         { }
         
      }
      else
      {
         editorKit = new JASHTMLEditorKit();
      }
      
      jep = new HTMLEditorPane();
      
      // Here's the editor pane configuration.  It's important to make
      // the "setEditable(false)" call, otherwise our hyperlinks won't
      // work.  (If the text is editable, then clicking on a hyperlink
      // simply means that you want to change the text...not follow the
      // link.)
      jep.setEditable(false);
      jep.setMargin(new Insets(0, 0, 0, 0));
      
      // Here's where we force the pane to use HTML editor kit with our own ViewFactory
      jep.setEditorKitForContentType("text/html", editorKit);
      jep.setEditorKitForContentType("text/htm", editorKit);
      
      jsp = new JScrollPane(jep);
      
      // Speed up window scrolling -- see http://java.sun.com/products/jfc/tsc/articles/performance/index.html
      //jsp.getViewport().putClientProperty("EnableWindowBlit", Boolean.TRUE);
      add(jsp, BorderLayout.CENTER);
      
      // and last but not least, hook up our event handlers
      jep.addHyperlinkListener(new SimpleLinkListener());
      
      //jep.addMouseListener(new jas.plot.PopupMenuBuilder());
      jep.addCaretListener(htmlCommand);
   }
   
   public CommandProcessor getCommandProcessor()
   {
      return htmlCommand;
   }
   
   public void setPageContext(PageContext context)
   {
      this.pageContext = context;
      
      urlHistory = new URLHistory(homePage);
      try
      {
         jep.setPage(homePage);
      }
      catch (Exception x)
      {
         app.error("Could not open home page", x);
      }
   }
   
   /** Called by the page manager when this page is about to be closed.
    * The page can, if needed, pop up a dialog box to ask the users permission,
    * and veto the close operation by returning false.
    * @return false to veto the close operation
    */
   public boolean close()
   {
      return true;
   }
   
   public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p)
   {
      app.getXMLMenuBuilder().mergePopupMenu("webPopupMenu", menu);
      
      JMenu history = new JMenu("History");
      history.setMnemonic('H');
      urlHistory.populateMenu(history);
      menu.add(history);
      return menu;
   }
   
   public void pageClosed()
   {}
   
   public void pageDeiconized()
   {}
   
   public void pageDeselected()
   {
      app.getCommandTargetManager().remove(getCommandProcessor());
   }
   
   public void pageIconized()
   {}
   
   public void pageSelected()
   {
      app.getCommandTargetManager().add(getCommandProcessor());
   }
   
   void showURL(URL url)
   {
      SwingUtilities.invokeLater(new PageLoader(url));
   }
   
   public class HTMLCommand extends CommandProcessor implements CaretListener
   {
      public void setChanged()
      {
         super.setChanged();
      }
      
      public void caretUpdate(CaretEvent e)
      {
         setChanged(); // Selection changed, so enabledCopy
      }
      
      public void enableBack(CommandState state)
      {
         state.setEnabled(urlHistory.enableBack());
      }
      
      public void enableCopy(CommandState state)
      {
         state.setEnabled(jep.getSelectedText() != null);
      }
      
      public void enableForward(CommandState state)
      {
         state.setEnabled(urlHistory.enableForward());
      }
      
      public void enableViewSource(CommandState state)
      {
         state.setEnabled(app.getLookup().lookup(TextEditorService.class) != null);
      }
      
      public void onBack() throws IOException
      {
         urlHistory.goBack();
         setChanged();
      }
      
      public void onCopy()
      {
         jep.copy();
      }
      
      public void onForward() throws IOException
      {
         urlHistory.goForward();
         setChanged();
      }
      
      //public void onPrint() throws Exception
      //{
      //   PrintHelper ph = PrintHelper.instance();
      //   ph.printTarget(jep);
      //}
      public void onRefresh() throws IOException
      {
         urlHistory.reload();
      }
      
      public void onViewSource()
      {
         TextEditorService text = (TextEditorService) app.getLookup().lookup(TextEditorService.class);
         text.show(jep.getText(), "text/html", "HTML Source");
      }
   }
   
   class JASHTMLEditorKit extends HTMLEditorKit
   {
      JASHTMLEditorKit()
      {
         super();
      }
      
      public ViewFactory getViewFactory()
      {
         return new JASHTMLFactory();
      }
   }
   
   /** Modified HTMLFactory that incorporates OBJECT Tag */
   class JASHTMLFactory extends HTMLFactory
   {
      public View create(Element elem)
      {
         Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
         if (o == Tag.OBJECT)
         {
            return new JasView(elem, jep.getPage(), app);
         }
         return super.create(elem);
      }
   }
   
   /**
    * temporary class that loads synchronously (although
    * later than the request so that a cursor change
    * can be done).
    */
   class PageLoader implements Runnable
   {
      private Cursor cursor;
      private URL url;
      
      PageLoader(URL u)
      {
         url = u;
         cursor = jep.getCursor();
         jep.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      }
      
      public void run()
      {
         try
         {
            jep.setPage(url);
            urlHistory.recordVisit(url);
            htmlCommand.setChanged();
         }
         catch (IOException x)
         {
            app.error("Could not load page " + url, x);
         }
         finally
         {
            jep.setCursor(cursor);
         }
      }
   }
   
   private class HTMLEditorPane extends JEditorPane implements Runnable
   {
      private ProgressMeter meter;
      private ProgressMeterProvider pmp = (ProgressMeterProvider) app.getLookup().lookup(ProgressMeterProvider.class);
      private int requestedOffset;
      
      public void paint(Graphics g)
      {
         if (plugin.isAntiAlias() && g instanceof Graphics2D)
         {
            ((Graphics2D) g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
         }
         super.paint(g);
      }
      
      public void setPage(URL page) throws IOException
      {
         if (page == null)
         {
            throw new IOException("invalid url");
         }
         EditorKit kit = getEditorKit();
         try
         {
            java.lang.reflect.Method method = kit.getClass().getDeclaredMethod("setURL", new Class[] { URL.class });
            if (method != null) method.invoke(kit, new Object[] { page });
         }
         catch (Exception ex)
         { }
         
         super.setPage(page);
      }
      
      public void run()
      {
         if (meter != null)
         {
            pmp.freeProgressMeter(meter);
            meter.setStoppable(null);
            meter = null;
         }
         if (requestedOffset != 0)
         {
            try
            {
               Point p = modelToView(requestedOffset).getLocation();
               
               // There is always a small offset from 0, which we ignore
               if (p.y > 20)
                  jsp.getViewport().setViewPosition(p);
            }
            catch (BadLocationException x)
            {}
            requestedOffset = 0;
         }
         
         Dictionary dict = ((AbstractDocument) getDocument()).getDocumentProperties();
         String title = (String) dict.get("title");
         pageContext.setTitle((title == null) ? "Untitled" : title);
         if (title != null)
            urlHistory.recordTitle(title);
      }
      
      protected InputStream getStream(URL page) throws IOException
      {
         // Note, get stream can be called multiple times, if page contains style sheets etc.
         URLConnection conn = page.openConnection();
         StoppableInputStream sin = new StoppableInputStream(super.getStream(page), conn.getContentLength());
         if ((pmp != null) && (meter == null))
         {
            meter = pmp.getProgressMeter();
            meter.setStoppable(sin);
         }
         
         TemplateEngine engine = plugin.getTemplateEngine();
         if (engine != null)
         {
            Reader reader = engine.filter(new InputStreamReader(sin));
            return new ReaderInputStream(reader);
         }
         else
         {
            return sin;
         }
      }
      
      protected void firePropertyChange(String property, Object oldValue, Object newValue)
      {
         super.firePropertyChange(property, oldValue, newValue);
         
         // Since document is loaded asncyhronously this may be called on a background thread.
         if (property.equals("page"))
            SwingUtilities.invokeLater(this);
      }
      
      void setPage(URL url, int offset) throws IOException
      {
         this.requestedOffset = offset;
         setPage(url);
      }
   }
   
   private class SimpleLinkListener implements HyperlinkListener
   {
      private Element findChild(Element parent, String name)
      {
         for (int i=0; i<parent.getElementCount(); i++)
         {
            Element child = parent.getElement(i);
            System.out.println("child.getName()="+child.getName());
            if (name.equals(child.getName())) return child;
         }
         return null;
      }
      public void hyperlinkUpdate(HyperlinkEvent he)
      {
         /*
          * TODO: Framesets
          * We dont seem to get called when the mouse passes over links in a
          * frameset, nor do links within frames get included properly in the
          * history (Back will take you back to before the frameset was entered)
          */
         EventType type = he.getEventType();
         
         // Ok.  Decide which event we got...
         if (type == EventType.ENTERED)
         {
            URL url = he.getURL();
            if (url != null)
            {
               jep.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
               app.setStatusMessage(url.toString());
            }
         }
         else if (type == EventType.EXITED)
         {
            jep.setCursor(Cursor.getDefaultCursor());
            app.setStatusMessage(" ");
         }
         else
         {
            if (he instanceof HTMLFrameHyperlinkEvent)
            {
               HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) he;
               HTMLDocument doc = (HTMLDocument) jep.getDocument();
               doc.processHTMLFrameHyperlinkEvent(evt);
            }
            else
            {
               URL url = he.getURL();
               Object target = null;
               Element element = he.getSourceElement();
               if (element != null)
               {
                  AttributeSet as = element.getAttributes();
                  if (as != null)
                  {
                     Object tag = as.getAttribute(javax.swing.text.html.HTML.Tag.A);
                     if (tag instanceof AttributeSet)
                     {
                        target = ((AttributeSet) tag).getAttribute(javax.swing.text.html.HTML.Attribute.TARGET);
                     }
                  }
               }
               if ("_external".equals(target))
               {
                  plugin.showURL(url,true);
               }
               else
               {
                  try
                  {
                     if (url.getProtocol().equals("file") && url.getHost().equals("") && !url.getFile().endsWith(".html"))
                     {
                        // Look for a file handler to open the local file
                        Template template = new Template(FileHandler.class);
                        Result result = app.getLookup().lookup(template);   

                        for (Iterator i = result.allInstances().iterator(); i.hasNext(); )
                        {
                           FileHandler handler = (FileHandler) i.next();
                           File file = new File(url.getFile());
                           if (handler.accept(file))
                           {
                              handler.openFile(file);
                              return;
                           }
                        }
                     }
                     // no good, try using a URL handler
 
                     Template template = new Template(URLHandler.class);
                     Result result = app.getLookup().lookup(template);

                     for (Iterator i = result.allInstances().iterator(); i.hasNext(); )
                     {
                        URLHandler handler = (URLHandler) i.next();
                        if (handler.accept(url))
                        {
                           handler.openURL(url);
                           return;
                        }
                     }
                  }
                  catch (IOException x)
                  {
                     app.error("IO error while following link.",x);
                     return;
                  }
                  
                  //OK, we are about to follow a link, we need to record the current
                  // document position so we can return to it if the user selects "Back"
                  int pos = jep.viewToModel(jsp.getViewport().getViewPosition());
                  urlHistory.recordPosition(pos);
                  showURL(url);
               }
            }
         }
      }
   }
   
   private class URLHistory
   {
      private ListItem m_current;
      private ListItem m_root;
      
      URLHistory(URL root)
      {
         m_root = m_current = new ListItem(root);
      }
      
      boolean enableBack()
      {
         return m_current.prev != null;
      }
      
      boolean enableForward()
      {
         return m_current.next != null;
      }
      
      void goBack() throws IOException
      {
         m_current = m_current.prev;
         jep.setPage(m_current.url, m_current.pos);
      }
      
      void goForward() throws IOException
      {
         m_current = m_current.next;
         jep.setPage(m_current.url, m_current.pos);
      }
      
      void goTo(ListItem item) throws IOException
      {
         m_current = item;
         jep.setPage(m_current.url, m_current.pos);
      }
      
      void populateMenu(JMenu history)
      {
         ListItem c = m_root;
         boolean enabled = false;
         while (c != null)
         {
            if (c != m_current)
            {
               history.add(new HistoryItem(c));
               enabled = true;
            }
            c = c.next;
         }
         history.setEnabled(enabled);
      }
      
      void recordPosition(int pos)
      {
         m_current.pos = pos;
      }
      
      void recordTitle(String title)
      {
         m_current.title = title;
      }
      
      void recordVisit(URL url)
      {
         if ((m_current.next == null) || !m_current.next.url.equals(url))
         {
            clear(m_current.next);
            (m_current.next = new ListItem(url)).prev = m_current;
            
            // Requires explanation?
            // a) The new item is m_current.next
            // b) <new item>.prev is m_current
         }
         m_current = m_current.next;
      }
      
      void reload() throws IOException
      {
         // This should force a reload
         jep.getDocument().putProperty(Document.StreamDescriptionProperty, null);
         jep.setPage(m_current.url);
      }
      
      private void clear(ListItem i)
      {
         if (i == null)
         {
            return;
         }
         clear(i.next);
         i.next = i.prev = null;
         i.url = null;
      }
      
      private class HistoryItem extends JMenuItem
      {
         private ListItem item;
         
         HistoryItem(ListItem item)
         {
            super((item.title == null) ? item.url.toExternalForm() : item.title);
            this.item = item;
         }
         
         protected void fireActionPerformed(ActionEvent e)
         {
            try
            {
               goTo(item);
               htmlCommand.setChanged();
            }
            catch (IOException x)
            {
               app.error("Could not open web page",x);
            }
            super.fireActionPerformed(e);
         }
      }
      
      private class ListItem
      {
         ListItem next;
         ListItem prev;
         String title;
         URL url;
         int pos;
         
         ListItem(URL url)
         {
            this.url = url;
         }
      }
   }
}
