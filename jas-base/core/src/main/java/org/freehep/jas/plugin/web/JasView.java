package org.freehep.jas.plugin.web;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.html.HTML.Attribute;
import org.freehep.application.Application;
import org.freehep.application.studio.Studio;
import org.freehep.jas.services.HTMLComponentFactory;
import org.freehep.util.images.ImageHandler;
import org.openide.util.Lookup.Item;
import org.openide.util.Lookup.Template;


/**
 * A ComponentView for displaying an arbitrary JComponent embedded within
 * a web page.
 * @author serbo
 * @version $Id: JasView.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public class JasView extends ComponentView
{
   protected URL baseURL;
   protected Studio app;
   
   /** Creates a new instance of JasView */
   public JasView(Element elem, URL baseURL, Studio studio)
   {
      super(elem);
      this.baseURL = baseURL;
      this.app = studio;
   }
   
   /**
    * Create the component.  The classid is used
    * as a specification of the classname, which
    * we try to load.
    */
   protected Component createComponent()
   {
      Component comp = null;
      try
      {
         //System.out.println("JasView.createComponent:: BASEURL="+baseURL);
         AttributeSet attrs = getElement().getAttributes();
         Enumeration en = attrs.getAttributeNames();
         Map map = new Hashtable();
         map.put("BASEURL",  baseURL);
         map.put("STUDIO",  app);
         String classID = (String) attrs.getAttribute(Attribute.CLASSID);
         String name = (String) attrs.getAttribute(Attribute.ID);
         //System.out.println("\n");
         while (en.hasMoreElements())
         {
            Object key = en.nextElement();
            String stringKey = key.toString().toUpperCase();
            String value = attrs.getAttribute(key).toString();
            map.put(key, value);
            //System.out.println("key = "+key+",  value = "+value);
         }
         comp = lookupAndCreateComponent(classID, map);
      } 
      catch (Throwable t)
      { 
         comp = getUnloadableRepresentation(t); 
      }
      return comp;
   }
   
   /**
    * Fetch a component that can be used to represent the
    * object if it can't be created.
    */
   protected Component getUnloadableRepresentation(final Throwable t)
   {
      JButton bomb = new JButton("Error...",ImageHandler.getIcon("images/Bomb.gif",JasView.class))
      {
         protected void fireActionPerformed(ActionEvent e)
         {
            app.error("Error loading object",t);
         }
      };
      bomb.setToolTipText("Click on Bomb to see error");
      return bomb;
   }
   
   protected Component lookupAndCreateComponent(String classID, Map map) throws Exception
   {      
      Template template = new Template(HTMLComponentFactory.class, classID, null);
      Item result = app.getLookup().lookupItem(template);
      if (result == null) throw new IllegalArgumentException("Can not find HTMLComponentFactory for: "+classID);
      HTMLComponentFactory factory = (HTMLComponentFactory) result.getInstance();
      return factory.getComponent(classID, map);
   }
}
