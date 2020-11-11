package org.freehep.jas.plugin.web;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.filechooser.FileFilter;

import org.freehep.application.ProgressMeter;
import org.freehep.application.PropertyUtilities;
import org.freehep.application.RecentItemTextField;
import org.freehep.application.StatusBar;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.jas.services.FileHandler;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.jas.services.WebBrowser;
import org.freehep.swing.ExtensionFileFilter;
import org.freehep.util.commanddispatcher.BooleanCommandState;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.images.ImageHandler;
import org.freehep.util.template.PropertiesValueProvider;
import org.freehep.util.template.TemplateEngine;
import org.freehep.util.template.ValueProvider;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.openide.util.Lookup;

import org.xml.sax.SAXException;

/**
 *
 * @author tonyj
 * @version $Id: SimpleWebBrowser.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public class SimpleWebBrowser extends Plugin implements FileHandler, PreferencesTopic, WebBrowser {

    private static final String[] path = {"Web Browser"};
    private Icon icon = ImageHandler.getIcon("images/Web.gif", SimpleWebBrowser.class);
    private Properties props;
    private TemplateEngine engine;
    private WebBrowserCommands commands;

    public void setAntiAlias(boolean antiAlias) {
        PropertyUtilities.setBoolean(props, "antiAlias", antiAlias);

        PageContext pg = getApplication().getPageManager().getSelectedPage();
        if (pg != null) {
            Component c = pg.getPage();
            if (c instanceof HTMLPage) {
                c.repaint();
            }
        }
    }

    public boolean isAntiAlias() {
        return PropertyUtilities.getBoolean(props, "antiAlias", false);
    }

    @Override
    public FileFilter getFileFilter() {
        return new ExtensionFileFilter(new String[]{"html", "htm"}, "HTML File");
    }

    public void setHomePage(URL homePage) {
        if (homePage == null) {
            props.remove("startPage");
        } else {
            props.setProperty("startPage", homePage.toExternalForm());
        }
    }

    public URL getHomePage() {
        String url = props.getProperty("startPage", "classpath:/org/freehep/jas/web/welcome.html");
        if (url == null) {
            return null;
        }
        try {
            return new URL(url);
        } catch (MalformedURLException x) {
            return null;
        }
    }

    public ProgressMeter getProgressMeter() {
        final StatusBar bar = getApplication().getStatusBar();
        ProgressMeter meter = new ProgressMeter();
        bar.add(meter);
        bar.revalidate();
        return meter;
    }

    public void setShowAtStart(boolean showAtStart) {
        PropertyUtilities.setBoolean(props, "showWelcomePageAtStart", showAtStart);
        commands.setChanged();
    }

    public boolean isShowAtStart() {
        return PropertyUtilities.getBoolean(props, "showWelcomePageAtStart", true);
    }

    //*************************************//
    // Methods for the FileHandler service //
    //*************************************//
    @Override
    public boolean accept(File file) throws IOException {
        return file.getName().endsWith(".html") || file.getName().endsWith(".htm");
    }

    @Override
    public boolean apply(JComponent panel) {
        return ((WebPreferences) panel).apply();
    }

    @Override
    public JComponent component() {
        return new WebPreferences(this);
    }

    //****************************************//
    public void freeProgressMeter(ProgressMeter meter) {
        final StatusBar bar = getApplication().getStatusBar();
        bar.remove(meter);
        bar.revalidate();
    }

    @Override
    protected void postInit() {
        Studio app = getApplication();
        // We do this here to make sure the extension class loader is ready.
        app.getLookup().add(new ClasspathStreamHandler(app.getExtensionLoader()), "classpath");
    }

    @Override
    protected void applicationVisible() {
        Studio app = getApplication();
        if (isShowAtStart()) {
            URL home = getHomePage();
            String startPage = app.getCommandLine().getOption("startPage");
            if (startPage != null) {
                try {
                    home = new URL(startPage);
                } catch (MalformedURLException x) {
                    x.printStackTrace();
                }
            }
            if (home != null) {
                showURL(home);
            }
        }
    }

    @Override
    public void openFile(File file) throws IOException {
        URL url = file.toURL();
        HTMLPage page = new HTMLPage(url, getApplication(), this);
        getApplication().getPageManager().openPage(page, null, icon, "Browser");
    }

    @Override
    public String[] path() {
        return path;
    }

    @Override
    public void showURL(URL url, boolean external) {
        if (external) {
            try {
                Desktop.getDesktop().browse(url.toURI());
            } catch (Exception x) {
                getApplication().error("Error launching external browser.", x);
            }
        } else {
            showURL(url);
        }
    }

    @Override
    public void showURL(URL url) {
        Studio app = getApplication();

        // See if the current page is an HTMLPage
        PageContext context = app.getPageManager().getSelectedPage();
        if (context != null) {
            Component c = context.getPage();
            if (c instanceof HTMLPage) {
                ((HTMLPage) c).showURL(url);
                return;
            }
        }

        // See if there are any open HTMLPages
        List l = app.getPageManager().pages();
        for (Iterator i = l.iterator(); i.hasNext();) {
            context = (PageContext) i.next();

            Component c = context.getPage();
            if (c instanceof HTMLPage) {
                context.requestShow();
                ((HTMLPage) c).showURL(url);
                return;
            }
        }

        // Create a new HTMLPage
        HTMLPage page = new HTMLPage(url, getApplication(), this);
        app.getPageManager().openPage(page, null, icon, "Browser");
    }

    @Override
    protected void init() throws SAXException, IOException {
        final Studio app = getApplication();
        app.getLookup().add(this, "Web Browser");

        props = app.getUserProperties();

        DefaultHTMLComponentFactory factory = new DefaultHTMLComponentFactory();
        factory.init(app);

        XMLMenuBuilder builder = app.getXMLMenuBuilder();
        URL xml = getClass().getResource("SimpleWebBrowser.menus");
        builder.build(xml);

        app.getCommandTargetManager().add(commands = new WebBrowserCommands());
        app.addToolBar(builder.getToolBar("webToolBar"), "Web Toolbar");

        engine = new TemplateEngine();
        engine.addValueProvider(new PropertiesValueProvider(app.getUserProperties()));
        engine.addValueProvider(new TemplateLookup(app));
    }

    TemplateEngine getTemplateEngine() {
        return engine;
    }

    public class WebBrowserCommands extends CommandProcessor {

        public void enableHome(CommandState state) {
            state.setEnabled(getHomePage() != null);
        }

        public void enableShowWelcomeAtStart(BooleanCommandState state) {
            state.setSelected(isShowAtStart());
            state.setEnabled(true);
        }

        public void onHome() {
            showURL(getHomePage());
        }

        public void onShowWelcomeAtStart(boolean start) {
            setShowAtStart(start);
            setChanged();
        }

        public void onWebPage() throws MalformedURLException {
            String input = RecentItemTextField.showInputDialog(getApplication(), "URL", "webPage");
            if (input != null) {
                URL url = new URL(input);
                showURL(url);
            }
        }
    }

    private class TemplateLookup implements ValueProvider {

        private Studio app;

        TemplateLookup(Studio app) {
            this.app = app;
        }

        @Override
        public String getValue(String name) {
            return null;
        }

        @Override
        public List getValues(String name) {
            Lookup lookup = app.getLookup();
            Lookup.Template query = new Lookup.Template(ValueProvider.class, name, null);
            Lookup.Result result = lookup.lookup(query);
            if (result == null) {
                return Collections.EMPTY_LIST;
            }
            return new ArrayList(result.allInstances());
        }
    }
}