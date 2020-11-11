/*
 * AIDATreeDataSourceProvider.java
 *
 * Created on August 23, 2004, 3:59 PM
 */

package org.freehep.jas.extension.aida;

import java.util.*;

import hep.aida.*;
import hep.aida.ref.plotter.adapter.AIDAAdapter;

import jas.hist.DataSource;
import jas.hist.XYDataSource;
import jas.hist.HasDataSource;
import jas.hist.JASHist;
import jas.hist.JASHistData;

import org.freehep.util.FreeHEPLookup;
import org.freehep.application.studio.Studio;

/**
 *
 * @author  serbo
 */
public class AIDATreeDataSourceProvider implements HasDataSource {
    private ITree aidaMasterTree;
    
    public DataSource getDataSource(String path) throws IllegalArgumentException {
        IManagedObject obj = getAIDAObject(path);

        DataSource data = null;
        if ( obj instanceof DataSource )
            data = (DataSource) obj;
        else if (obj instanceof IProfile)
            data = AIDAAdapter.create((IProfile) obj);
        else if (obj instanceof ICloud)
            data =  AIDAAdapter.create((ICloud) obj);
        else if (obj instanceof IHistogram)
            data =  AIDAAdapter.create((IHistogram) obj);
        else if (obj instanceof IDataPointSet) {
            data =  AIDAAdapter.create((IDataPointSet) obj);
        } else if (obj instanceof IFunction)
            data =  AIDAAdapter.create((IFunction) obj);
        else throw new IllegalArgumentException("Invalid object "+obj.getClass().getName()+", for Path="+path);
        
        return data;
    }
    
    public IManagedObject getAIDAObject(String path) throws IllegalArgumentException {
        if (aidaMasterTree == null) {
            Studio app = (Studio) org.freehep.application.studio.Studio.getApplication();
            aidaMasterTree = (ITree) app.getLookup().lookup(ITree.class);
        }
        IManagedObject obj = null;
        try {
            obj = aidaMasterTree.find(path);
        } catch (IllegalArgumentException iae) {
            
            // Try case insensitive search here - will take first item that fits
            String newPath = "/";
            StringTokenizer tokenizer = new StringTokenizer(path,"/");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                while (token.endsWith("\\")) {
                    token = token.substring(0, token.length()-1)+ "/";
                    if (tokenizer.hasMoreTokens()) token = token  + tokenizer.nextToken();
                }
                String[] names = (String[]) aidaMasterTree.listObjectNames(newPath);
                String tmpPath = newPath +token;
                if (tokenizer.hasMoreTokens()) tmpPath += "/";
                if (names != null) {
                    for (int i=0; i<names.length; i++) {
                        if (names[i].equalsIgnoreCase(tmpPath)) {
                            newPath = names[i];
                            break;
                        }
                    }                  
                }
            }

            try {
                obj = aidaMasterTree.find(newPath);
            } catch (IllegalArgumentException iae2) {
                throw iae;
            }
        }
        return obj;
    }
    
}
