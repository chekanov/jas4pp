package org.freehep.jas3web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author tonyj
 */
public class Plugins extends HttpServlet {

    private DataSource dataSource;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
        String appName = request.getParameter("app.name");
        String appVersion = request.getParameter("app.version");
        String osName =  request.getParameter("os.name");
        String osArch = request.getParameter("os.arch");
        String osVersion = request.getParameter("os.version");
        String javaVersion = request.getParameter("java.version");
        String javaVendor = request.getParameter("java.vendor");
        String snapshots = request.getParameter("snapshots");
        PrintWriter out = response.getWriter();
        try {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<!DOCTYPE plugins SYSTEM \"http://java.freehep.org/schemas/plugin/1.1/plugin.dtd\">");
            out.println("<plugins>");
            Connection conn = dataSource.getConnection();
            try {
                if (appVersion != null) {
                    // Make sure the version we are asking about exists in the version table
                    VersionTable versions = new VersionTable(conn);
                    versions.lookup(appVersion);
                    versions.commit(conn);
                }

                String sql = "with plugins as (select p.name,p.author,p.version,p.shortdescription,p.category,p.plugin_class,"
                        + "p.j2se_min_version,p.jas_min_version,p.j2se_max_version,p.jas_max_version,p.load_at_start,p.description,v.sort_order"
                        + " from jas_plugin_version p "
                        + "join jas_version v on  (p.version=v.version) "
                        + "left outer JOIN jas_version v1 ON (p.jas_min_version=v1.version) "
                        + "left outer JOIN jas_version v2 ON (p.jas_max_version=v2.version) "
                        + " where 1=1 ";
                        if (!Boolean.valueOf(snapshots)) sql += "and v.is_snapshot='N' ";
                        if (appVersion != null) { 
                            sql += "and (v1.sort_order is null or v1.sort_order<=(select sort_order from jas_version where version=?)) ";
                            sql += "and (v2.sort_order is null or v2.sort_order>=(select sort_order from jas_version where version=?)) ";
                        }
                        sql += ") select * from plugins p where sort_order=(select max(sort_order) from plugins where p.name=name)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                if (appVersion != null) {
                    stmt.setString(1,appVersion);
                    stmt.setString(2,appVersion);
                }
                PreparedStatement stmt2 = conn.prepareStatement("select url,location from jas_plugin_file where name=? and version=?");
                PreparedStatement stmt3 = conn.prepareStatement("select property,value from jas_property where name=? and version=?");
                PreparedStatement stmt4 = conn.prepareStatement("select depends_name,depends_min_version,depends_max_version from jas_depends where name=? and version=?");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String name = rs.getString("name");
                    String author = rs.getString("author");
                    String version = rs.getString("version");
                    String shortDescription = rs.getString("shortDescription");
                    String description = rs.getString("description");
                    String category = rs.getString("category");
                    String loadAtStart = rs.getString("load_at_start");
                    String j2seMinVersion = rs.getString("j2se_min_version");
                    String jasMinVersion = rs.getString("jas_min_version");
                    String j2seMaxVersion = rs.getString("j2se_max_version");
                    String jasMaxVersion = rs.getString("jas_max_version");
                    String pluginClass = rs.getString("plugin_class");
                    
                    out.println("<plugin>");
                    out.println("<information>");
                    out.printf("<name>%s</name>\n",name);
                    out.printf("<author>%s</author>\n", author);
                    out.printf("<version>%s</version>\n", version);
                    if (shortDescription!=null) out.printf("<description kind=\"short\">%s</description>",shortDescription);
                    if (description!=null) out.printf("<description>%s</description>",description);
                    if (category!=null) out.printf("<category>%s</category>",category);
                    if ("Y".equals(loadAtStart)) out.println("<load-at-start/>");
                    out.println("</information>");
                    out.println("<resources>");
                    if (j2seMinVersion!=null || j2seMaxVersion!=null) {
                        out.print("<j2se"); 
                        if (j2seMinVersion!=null) out.printf(" minVersion=\"%s\"",j2seMinVersion);
                        if (j2seMaxVersion!=null) out.printf(" maxVersion=\"%s\"",j2seMaxVersion);
                        out.println("/>");
                    }
                    if (jasMinVersion!=null || jasMaxVersion!=null) { 
                        out.printf("<application");
                        if (jasMinVersion!=null) out.printf(" minVersion=\"%s\"",jasMinVersion);
                        if (jasMaxVersion!=null) out.printf(" maxVersion=\"%s\"",jasMaxVersion);
                        out.println("/>");
                    }
                    stmt2.setString(1, name);
                    stmt2.setString(2, version);
                    ResultSet rs2 = stmt2.executeQuery();
                    while (rs2.next()) {
                        out.printf("<file href=\"%s\" location=\"%s\"/>\n",rs2.getString(1),rs2.getString(2));
                    }
                    stmt3.setString(1, name);
                    stmt3.setString(2, version);
                    ResultSet rs3 = stmt3.executeQuery();
                    while (rs3.next()) {
                        out.printf("<property name=\"%s\" value=\"%s\"/>\n",rs3.getString(1),rs3.getString(2));
                    }
                    stmt4.setString(1, name);
                    stmt4.setString(2, version);
                    ResultSet rs4 = stmt4.executeQuery();
                    while (rs4.next()) {
                        out.printf("<depends plugin=\"%s\"",rs4.getString(1));
                        String minVersion = rs4.getString(2);
                        String maxVersion = rs4.getString(3);
                        if (minVersion!=null) out.printf(" minVersion=\"%s\"",minVersion);
                        if (maxVersion!=null) out.printf(" maxVersion=\"%s\"",maxVersion);   
                        out.println("/>");
                    }
                    out.println("</resources>");
                    if (pluginClass != null) {
                        out.printf("<plugin-desc class=\"%s\"/>\n", pluginClass);
                    }
                    out.println("</plugin>");
                }
            } finally {
                conn.close();
            }
            out.println("</plugins>");
        } catch (SQLException x) {
            throw new ServletException("Database error while getting data", x);
        } finally {
            out.close();
        }
    }

    @Override
    public void init() throws ServletException {
        try {
            String dataSourceName = getInitParameter("dataSource");
            Context env = (Context) new InitialContext().lookup("java:comp/env");

            dataSource = (DataSource) env.lookup(dataSourceName);

            if (dataSource == null) {
                throw new ServletException("`" + dataSourceName + "' is an unknown DataSource");
            }
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
