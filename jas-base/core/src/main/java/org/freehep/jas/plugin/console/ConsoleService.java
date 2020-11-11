package org.freehep.jas.plugin.console;

import java.io.IOException;
import javax.swing.Icon;

import javax.swing.text.AttributeSet;
import org.freehep.application.mdi.PageContext;
import org.freehep.swing.popup.HasPopupItems;

/**
 * A service that allows for creating, writing to, and reading from Consoles.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: ConsoleService.java 16111 2014-08-21 20:13:34Z onoprien $
 */
public interface ConsoleService {

    /**
     * Get a pointer to an existing console with a given name.
     *
     * @return The console, or <CODE>null</CODE> if no console by this name
     * exists
     * @param name The name to search for
     */
    Console getConsole(String name);

    /**
     * Create a new console. Multiple consoles with the same name are allowed,
     * but will render the other methods in this interface of limited use.
     *
     * @param name The name of the newly created console
     * @param icon An icon to associate with the console, or <CODE>null</CODE>
     * for no icon.
     * @return The newly created console.
     */
    Console createConsole(String name, Icon icon);
    /**
     * Create a new console, with extra popup items.
     * @param name The name of the newly created console
     * @param icon An icon to associate with the console, or <CODE>null</CODE>
     * for no icon.
     * @param popupItems Allows extra popup items to be attached to console.
     * @return The newly created console.
     */
    Console createConsole(String name, Icon icon, HasPopupItems popupItems);

    /**
     * Creates an output stream associated to a named console area. All output
     * written to the output stream will appear in the console area. More than
     * one OutputStream can be associated with a single console area. The
     * returned OutputStream will be thread safe, so it can be written to from
     * any thread. Writing to this output stream will cause the corresponding
     * console to appear if it is not visible, (or to be reopened if it has been
     * closed).
     *
     * @param name The name of the console.
     * @param icon The Icon to be used if a new console is created,
     * or <CODE>null</CODE>.
     * @return The newly created OutputStream.
     * @throws IOException If an IO error occurs
     */
    ConsoleOutputStream getConsoleOutputStream(String name, Icon icon) throws IOException;

    /**
     * Create an attributed output stream
     *
     * @see #getConsoleOutputStream(String, Icon)
     * @param name The name of the Console
     * @param icon The icon to use if a new console is created,
     * or <CODE>null</CODE>
     * @param set The attributes for text created using this outputstream,
     * or <CODE>null</CODE> for default attributes.
     * @return The newly created output stream
     * @throws IOException If an IO error occurs
     */
    ConsoleOutputStream getConsoleOutputStream(String name, Icon icon, AttributeSet set) throws IOException;

    /**
     * Redirects output to System.out for a specific thread to a given console
     *
     * @param thread The thread for which redirection should apply
     * @param out The output stream to redirect to
     */
    void redirectStandardOutputOnThreadToConsole(Thread thread, ConsoleOutputStream out);

    /**
     * Requests that a given console be made visible
     *
     * @param console The console to be made visible
     */
    void showConsole(Console console);
    
    /** Gets the page context corresponding to a given console.
     * @param console The console
     * @return The PageContext (or <code>null</code> if the console has been closed).
     * @since 3.0.0
     */
    PageContext getPageContextForConsole(Console console);
}