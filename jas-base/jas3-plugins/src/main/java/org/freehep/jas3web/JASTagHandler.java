package org.freehep.jas3web;

import java.io.PrintWriter;
import javax.naming.InitialContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.sql.DataSource;

/**
 * Used to process uploaded XML plugin descriptor files.
 * @author tonyj
 */
public class JASTagHandler extends SimpleTagSupport {

    private String xml;
    private String dataSourceName;
    private boolean brief=true;

    /**
     * Called by the container to invoke this tag. 
     * The implementation of this method is provided by the tag library developer,
     * and handles all tag processing, body iteration, etc.
     */
    @Override
    public void doTag() throws JspException {
        JspWriter out = getJspContext().getOut();

        try {

            DataSource ds = (DataSource) InitialContext.doLookup("java:comp/env/" + dataSourceName);
            PluginLoader loader = new PluginLoader(ds);
            loader.loadPlugin(xml);
            out.println("<p>File uploaded successfully</p>");

        } catch (Exception x) {
            x.printStackTrace();
            PrintWriter pw = new PrintWriter(new BriefWriter(out, brief));
            pw.println("<pre class=\"error\">");
            x.printStackTrace(pw);
            pw.println("</pre>");
            pw.flush();
        }
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public void setDataSource(String dataSource) {
        this.dataSourceName = dataSource;
    }
}
