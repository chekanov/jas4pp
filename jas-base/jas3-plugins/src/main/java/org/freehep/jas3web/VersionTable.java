package org.freehep.jas3web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.freehep.util.VersionComparator;

/**
 *
 * @author tonyj
 */
class VersionTable {
    private boolean isModified = false;
    private Set<String> set = new HashSet<String>();

    public VersionTable(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select version from jas_version");
        try {
            ResultSet rs = stmt.executeQuery();
            try {
                while (rs.next()) {
                    set.add(rs.getString(1));
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }

    String lookup(String version) {
        if (version != null) {
            isModified |= set.add(version);
        }
        return version;
    }

    void commit(Connection conn) throws SQLException {
        if (isModified) {
            List<String> versions = new ArrayList<String>(set);
            Collections.sort(versions, new VersionComparator());
            Map<String, Integer> map = new HashMap<String, Integer>();
            for (int i = 0; i < versions.size(); i++) {
                map.put(versions.get(i), i);
            }
            // Update the database
            PreparedStatement stmt = conn.prepareStatement("select version,sort_order,is_snapshot from jas_version", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            try {
                ResultSet rs = stmt.executeQuery();
                try {
                    while (rs.next()) {
                        String version = rs.getString(1);
                        int order = rs.getInt(2);
                        Integer pos = map.remove(version);
                        if (pos != order) {
                            rs.updateInt(2, pos);
                            rs.updateRow();
                        }
                    }
                    for (int pos : map.values()) {
                        String version = versions.get(pos);
                        rs.moveToInsertRow();
                        rs.updateString(1, version);
                        rs.updateInt(2, pos);
                        rs.updateString(3, version.endsWith("-SNAPSHOT") ? "Y" : "N");
                        rs.insertRow();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
            isModified = false;
        }
    }    
}
