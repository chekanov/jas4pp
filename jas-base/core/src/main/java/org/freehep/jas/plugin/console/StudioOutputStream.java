package org.freehep.jas.plugin.console;

import java.io.IOException;
import javax.swing.Icon;
import javax.swing.text.AttributeSet;

/**
 * Associates a name and a icon with a stream. Deal with automatically opening
 * associated console windows when text is sent to the stream.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: StudioOutputStream.java 14085 2012-12-13 18:37:19Z tonyj $
 */
class StudioOutputStream extends ConsoleOutputStream {

    private ConsolePlugin plugin;
    private String name;
    private Icon icon;
    private ConsoleOutputStream out;

    StudioOutputStream(ConsolePlugin plugin, String name, Icon icon, AttributeSet set) {
        this(plugin,name,icon,set,true);
    }
    
    StudioOutputStream(ConsolePlugin plugin, String name, Icon icon, AttributeSet set, boolean autoShow) {    
        super(set, autoShow);
        this.plugin = plugin;
        this.name = name;
        this.icon = icon;
    }

    @Override
    public void write(byte[] b, int off, int len, AttributeSet set) throws IOException {
        if (out == null) {
            out = plugin.getStreamToWriteTo(this);
            out.setAutoShow(isAutoShow());
        }
        try {
            out.write(b, off, len, set);
        } catch (IOException x) { 
            // We assume this is because the corresponding console was closed
            out = plugin.getStreamToWriteTo(this);
            out.setAutoShow(isAutoShow());
            out.write(b, off, len, set);
        }
    }

    String getName() {
        return name;
    }

    Icon getIcon() {
        return icon;
    }

    @Override
    void setAutoShow(boolean autoShow) {
        super.setAutoShow(autoShow); 
        if (out != null) {
            out.setAutoShow(autoShow);
        }
    }
    
}