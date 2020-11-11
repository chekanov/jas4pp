package org.freehep.jas.services;

import java.util.Map;
import javax.swing.JComponent;

/** An interface to be implemented by plugins which allow objects to be embedded
 * in HTML pages.
 * <p>
 * Example of use:
 * <PRE> HTMLComponentFactory factory = new MyComponentFactory();
 * FreeHEPLookup lookup = getApplication().getLookup();
 * lookup.add(factory,"objectTagA");
 * lookup.add(factory,"objectTagB");</PRE>
 * @author serbo
 * @version $Id:
 */
public interface HTMLComponentFactory
{
   
   /** Called when an Object tag is found in an HTML page.
    * @param name The name referenced in the object tag
    * @param attributes The parameters and values specified in the Object tag.
    * @return The component to be embedded in the HTML page
    */   
   JComponent getComponent(String name, Map attributes);
}

