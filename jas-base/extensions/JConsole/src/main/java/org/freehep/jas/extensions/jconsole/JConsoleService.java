package org.freehep.jas.extensions.jconsole;

import java.io.IOException;
import javax.swing.Icon;
import javax.swing.text.AttributeSet;
import org.freehep.application.mdi.PageContext;
import org.freehep.jas.plugin.console.Console;
import org.freehep.jas.plugin.console.ConsoleOutputStream;
import org.freehep.swing.popup.HasPopupItems;

/**
 *
 * @author onoprien
 */
public interface JConsoleService {
  
  /**
   * Adds a provider that will be used to create consoles of the specified type.
   * 
   * @param consoleType Type of consoles the specified provider will be used to create.
   * @param provider The provider.
   */
  void addConsoleProvider(Class consoleType, JConsoleProvider provider);

  /**
   * Returns an existing console with the specified name. If there are multiple
   * consoles with the given name, this method will return one of them.
   *
   * @param name The name of the console.
   * @return The console, or <tt>null</tt> if there is no console by this name.
   */
  JConsole getConsole(String name);

  /**
   * Creates a new console.
   *
   * @param consoleType Type of the console to be created.
   * @param name The name of the newly created console
   * @param icon An icon to associate with the console, or <tt>null</tt> for no icon.
   * @param popupItems Pop-up items to be attached to the console, or <tt>null</tt> for no none.
   * @return The newly created console.
   */
  JConsole createConsole(Class consoleType, String name, Icon icon, HasPopupItems popupItems);

  /**
   * Creates an output stream associated to a named console area. All output
   * written to the output stream will appear in the console area. More than one
   * OutputStream can be associated with a single console area. The returned
   * OutputStream will be thread safe, so it can be written to from any thread.
   * Writing to this output stream will cause the corresponding console to
   * appear if it is not visible, (or to be reopened if it has been closed).
   *
   * @param name The name of the console.
   * @param icon The Icon to be used if a new console is created, or
   * <CODE>null</CODE>.
   * @return The newly created OutputStream.
   * @throws IOException If an IO error occurs
   */
  default ConsoleOutputStream getConsoleOutputStream(String name, Icon icon) throws IOException {
    throw new UnsupportedOperationException();
  }

  /**
   * Create an attributed output stream
   *
   * @see #getConsoleOutputStream(String, Icon)
   * @param name The name of the Console
   * @param icon The icon to use if a new console is created, or
   * <CODE>null</CODE>
   * @param set The attributes for text created using this outputstream, or
   * <CODE>null</CODE> for default attributes.
   * @return The newly created output stream
   * @throws IOException If an IO error occurs
   */
  default ConsoleOutputStream getConsoleOutputStream(String name, Icon icon, AttributeSet set) throws IOException {
    throw new UnsupportedOperationException();
  }

  /**
   * Redirects output to System.out for a specific thread to a given console
   *
   * @param thread The thread for which redirection should apply
   * @param out The output stream to redirect to
   */
  default void redirectStandardOutputOnThreadToConsole(Thread thread, ConsoleOutputStream out) {
    throw new UnsupportedOperationException();
  }

  /**
   * Requests that the specified console be made visible
   *
   * @param console The console to be made visible.
   */
  void showConsole(Console console);

  /**
   * Returns the page context corresponding to the specified console.
   *
   * @param console The console
   * @return The PageContext (or <tt>null</tt> if the console has been closed).
   */
  PageContext getPageContextForConsole(JConsole console);
  
}
