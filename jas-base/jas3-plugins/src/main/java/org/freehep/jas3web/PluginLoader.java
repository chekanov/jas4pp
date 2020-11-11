package org.freehep.jas3web;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author tonyj
 */
public class PluginLoader {
    private DataSource ds;

    public PluginLoader(DataSource ds) {
        this.ds = ds;
    }

    void loadPlugin(String xml) throws JDOMException, IOException, SQLException {

        Connection conn = ds.getConnection();
        conn.setAutoCommit(false);

        try {
            VersionTable versions = new VersionTable(conn);

            // Parse the XML
            SAXBuilder builder = new SAXBuilder(true); // validating JDOM builder if true
            builder.setFeature("http://apache.org/xml/features/validation/schema", true);
            
            for (int pass=0; pass<2; pass++) {
                Document doc = builder.build(new StringReader(xml));
                List<Element> plugins = doc.getRootElement().getChildren("plugin");
                for (Element plugin : plugins) {
                    Element information = plugin.getChild("information");
                    String name = information.getChildTextNormalize("name");
                    String author = information.getChildTextNormalize("author");
                    String version = versions.lookup(information.getChildTextNormalize("version"));
                    String shortDescription = null;
                    String description = null;
                    for (Element descr : (List<Element>) information.getChildren("description")) {
                        String kind = descr.getAttributeValue("kind");
                        if ("short".equals(kind)) shortDescription = descr.getTextNormalize();
                        else description = descr.getTextNormalize();
                    }
                    String category = information.getChildTextNormalize("category");
                    boolean loadAtStart = information.getChild("load-at-start") != null;
                    Element resources = plugin.getChild("resources");
                    String j2seMinVersion = versions.lookup(attributeOf(resources.getChild("j2se"), "minVersion"));
                    String jasMinVersion = versions.lookup(attributeOf(resources.getChild("application"), "minVersion"));
                    String j2seMaxVersion = versions.lookup(attributeOf(resources.getChild("j2se"), "maxVersion"));
                    String jasMaxVersion = versions.lookup(attributeOf(resources.getChild("application"), "maxVersion"));
                    Element pluginDesc = plugin.getChild("plugin-desc");
                    String pluginClass = pluginDesc == null ? null : pluginDesc.getAttributeValue("class");
                    if (pass==1) {
                        String sql = "select name,author,version,shortdescription,category,plugin_class,"
                                + "j2se_min_version,jas_min_version,j2se_max_version,jas_max_version,load_at_start,description "
                                + "from jas_plugin_version where name=? and version=?";
                        PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        stmt.setString(1, name);
                        stmt.setString(2, version);
                        ResultSet rs = stmt.executeQuery();
                        boolean exists = rs.next();
                        if (!exists) {
                            rs.moveToInsertRow();
                            rs.updateString(1, name);
                            rs.updateString(3, version);
                        }
                        rs.updateString(2, author);
                        rs.updateString(4, shortDescription);
                        rs.updateString(5, category);
                        rs.updateString(6, pluginClass);
                        rs.updateString(7, j2seMinVersion);
                        rs.updateString(8, jasMinVersion);
                        rs.updateString(9, j2seMaxVersion);
                        rs.updateString(10, jasMaxVersion);
                        rs.updateString(11, loadAtStart ? "Y" : "N");
                        rs.updateString(12, description);
                        if (exists) {
                            rs.updateRow();
                        } else {
                            rs.insertRow();
                        }
                        handleFiles(conn, name, version, resources);
                        handleProperties(conn, name, version, resources);
                        handleDepends(conn, name, version, resources, versions);
                    }
                }
                versions.commit(conn);
            } 
            conn.commit();
        } finally {
            conn.close();
        }
    }

    private void handleFiles(Connection conn, String name, String version, Element resources) throws SQLException {
        String sql = "select name,version,url,location from jas_plugin_file where name=? and version=?";
        PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        stmt.setString(1, name);
        stmt.setString(2, version);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
           rs.deleteRow();
        }
        for (Element file : (List<Element>) resources.getChildren("file")) {
            String href = StringEscapeUtils.escapeXml(file.getAttributeValue("href"));
            String location = file.getAttributeValue("location");
            rs.moveToInsertRow();
            rs.updateString(1,name);
            rs.updateString(2,version);
            rs.updateString(3,href);
            rs.updateString(4,location);
            rs.insertRow();
        }
    }
    private void handleProperties(Connection conn, String name, String version, Element resources) throws SQLException {
        String sql = "select name,version,property,value from jas_property where name=? and version=?";
        PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        stmt.setString(1, name);
        stmt.setString(2, version);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
           rs.deleteRow();
        }
        for (Element file : (List<Element>) resources.getChildren("property")) {
            String property = file.getAttributeValue("name");
            String value = file.getAttributeValue("value");
            rs.moveToInsertRow();
            rs.updateString(1,name);
            rs.updateString(2,version);
            rs.updateString(3,property);
            rs.updateString(4,value);
            rs.insertRow();
        }
    }
    private void handleDepends(Connection conn, String name, String version, Element resources, VersionTable versions) throws SQLException {
        String sql = "select name,version,depends_name,depends_min_version,depends_max_version from jas_depends where name=? and version=?";
        PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        stmt.setString(1, name);
        stmt.setString(2, version);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
           rs.deleteRow();
        }
        for (Element file : (List<Element>) resources.getChildren("depends")) {
            String plugin = file.getAttributeValue("plugin");
            String minVersion = versions.lookup(file.getAttributeValue("minVersion"));
            String maxVersion = versions.lookup(file.getAttributeValue("maxVersion"));
            rs.moveToInsertRow();
            rs.updateString(1,name);
            rs.updateString(2,version);
            rs.updateString(3,plugin);
            rs.updateString(4,minVersion);
            rs.updateString(5,maxVersion);
            rs.insertRow();
        }
    }
    
    private String attributeOf(Element child, String attributeName) {
        return child == null ? null : child.getAttributeValue(attributeName);
    }
}
