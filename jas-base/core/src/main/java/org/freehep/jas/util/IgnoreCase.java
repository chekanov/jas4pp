/*
 * IgnoreCase.java
 *
 * Created on March 3, 2005, 3:08 PM
 */

package org.freehep.jas.util;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author  serbo
 */
public class IgnoreCase {
    
    
    public static Object getIgnoreCase(Map map, String key) {
        Object obj = null;
        if (map == null || map.isEmpty() || key == null) return obj;
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            Object objKey = it.next();
            if (objKey instanceof String) {
                String tmpKey = (String) objKey;
                if (key.equalsIgnoreCase(tmpKey)) {
                    obj = map.get(tmpKey);
                    break;
                }
            }
        }
        return obj;
    }
    
    public static boolean containsIgnoreCase(Map map, String key) {
        boolean contains = false;
        if (map == null || map.isEmpty() || key == null) return contains;
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            Object objKey = it.next();
            if (objKey instanceof String) {
                String tmpKey = (String) objKey;
                if (key.equalsIgnoreCase(tmpKey)) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
   }
    
}
