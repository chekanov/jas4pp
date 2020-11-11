package org.freehep.jas3web;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class BriefWriter extends FilterWriter {

    private boolean filter;
    private boolean newLine = false;
    private boolean suppress = false;

    public BriefWriter(OutputStream out, boolean filter) {
        this(new OutputStreamWriter(out), filter);
    }

    public BriefWriter(Writer writer, boolean filter) {
        super(writer);
        this.filter = filter;
    }

    @Override
    public void write(int c) throws IOException {
        if (c == '\n') {
            newLine = true;
        } else if (newLine) {
            if (c == '\t') {
                suppress = true;
            } else {
                suppress = false;
            }
            newLine = false;
        }
        if (!suppress || !filter) {
            super.write(c);
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            write(cbuf[i + off]);
        }
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            write(str.charAt(i + off));
        }
    }
}
