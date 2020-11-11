package org.freehep.jas.extensions.jconsole;

import java.awt.Component;
import javax.swing.ScrollPaneConstants;

/**
 * Jas 3 Console.
 *
 * @author onoprien
 */
public interface JConsole {
  
// -- Getters : ----------------------------------------------------------------
  
  default Class getType() {
    return getClass();
  }
  
  String getName();
  
  Component getView();
  
  /**
   * Tells Jas3 whether it should provide vertical scrolling capability for this console.
   * <p>
   * Possible return values:
   * <ul>
   *   <li>ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED</li>
   *   <li>ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER</li>
   *   <li>ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS</li>
   * </ul>
   * 
   * @return the requested vertical scroll bar policy value.
   */
  default int getVerticalScrollBarPolicy() {
    return ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
  }
  
  /**
   * Tells Jas3 whether it should provide horizontal scrolling capability for this console.
   * <p>
   * Possible return values:
   * <ul>
   *   <li>ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED</li>
   *   <li>ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER</li>
   *   <li>ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS</li>
   * </ul>
   * 
   * @return the requested vertical scroll bar policy value.
   */
  default int getHorizontalScrollBarPolicy() {
    return ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
  }
  
  
// -- Life cycle : -------------------------------------------------------------
  
  default void dispose() {}
  
}
