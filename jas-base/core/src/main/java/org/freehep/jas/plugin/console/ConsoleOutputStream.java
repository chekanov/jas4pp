package org.freehep.jas.plugin.console;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.text.AttributeSet;

/**
 * An output stream returned by a console. No public functionality beyond a
 * normal output stream. Note that this output stream will be closed when the
 * corresponding console is closed, any further attempts to write to the output
 * stream will return an IOException.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: ConsoleOutputStream.java 14085 2012-12-13 18:37:19Z tonyj $
 */
public abstract class ConsoleOutputStream extends OutputStream {

    private AttributeSet set;
    private boolean autoShow;
    private byte[] one = new byte[1];

    ConsoleOutputStream(AttributeSet set, boolean autoShow) {
        this.set = set;
        this.autoShow = autoShow;
    }

    @Override
    public void write(int b) throws IOException {
        one[0] = (byte) b;
        write(one, 0, 1, set);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        write(b, off, len, set);
    }

    AttributeSet getAttributeSet() {
        return set;
    }

    boolean isAutoShow() {
        return autoShow;
    }

    void setAutoShow(boolean autoShow) {
        this.autoShow = autoShow;
    }

    abstract void write(byte[] b, int off, int len, AttributeSet set) throws IOException;
}