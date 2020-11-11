package org.freehep.application.mdi;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.event.EventListenerList;

/**
 * Handle for a graphical component (page) managed by a {@link PageManager}.
 * Keeps information about the page properties and state.
 * Allows the user to interact with a page in an abstract way.
 * Keeps a list of listeners that should be notified of changes in the page state.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: PageContext.java 16331 2015-09-10 23:37:57Z onoprien $
 */
public class PageContext {

    /**
     * Constructs an instance.
     * 
     * @param component Graphical component (page) to be managed by this {@code PageContext} instance.
     * @param title Name of the page.
     * @param icon Icon associated with the page.
     * @param type Type of the page. Can be used to apply bulk operations to all pages of a certain type.
     */
    PageContext(Component component, String title, Icon icon, String type) {
        this.component = component;
        this.title = title;
        this.icon = icon;
        this.type = type;
    }

    void setPageManager(PageManager manager) {
        this.pageManager = manager;
    }

    PageManager getPageManager() {
        return pageManager;
    }

    /**
     * Adds a page listener to receive notifications of user initiated changes.
     *
     * @param listener The PageListener to install
     */
    public void addPageListener(PageListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(PageListener.class, listener);
    }

    /**
     * Remove a previously installed PageListener
     *
     * @param listener The PageListener to remove
     */
    public void removePageListener(PageListener listener) {
        listenerList.remove(PageListener.class, listener);
    }

    void firePageEvent(PageEvent event, int id) {
        if (listenerList != null) {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == PageListener.class) {
                    // Lazily create the event:
                    if (event == null) {
                        event = new PageEvent(this, id);
                    }
                    ((PageListener) listeners[i + 1]).pageChanged(event);
                }
            }
        }
    }

    /**
     * Requests that the associated page be shown.
     * If the page is iconized it is deiconized, and brought to the top.
     */
    public void requestShow() {
        pageManager.show(this);
    }

    /** Closes this page. */
    public void close() {
        pageManager.close(this);
    }

    /** Returns the component associated with this page. */
    public Component getPage() {
        return component;
    }

    /** Returns the name associated with the page. */
    public String getTitle() {
        return title;
    }

    /** Returns the icon associated with the page. */
    public Icon getIcon() {
        return icon;
    }

    /** Sets the title of this page. */
    public void setTitle(String title) {
        this.title = title;
        pageManager.titleChanged(this);
    }

    /** Sets the icon associated with this page. */
    public void setIcon(Icon icon) {
        this.icon = icon;
        pageManager.iconChanged(this);
    }

    @Override
    public String toString() {
        return "PageContext: " + title;
    }

    /** Returns the type of this page. */
    public String type() {
        return type;
    }
    
    /** Graphical component handled by this PageContext. */
    private final Component component;
    
    private PageManager pageManager;
    private EventListenerList listenerList;
    
    private final String type;
    
    private String title;
    private Icon icon;
}
