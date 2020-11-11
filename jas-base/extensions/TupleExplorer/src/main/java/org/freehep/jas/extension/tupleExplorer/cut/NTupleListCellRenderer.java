package org.freehep.jas.extension.tupleExplorer.cut;

import hep.aida.ref.tuple.FTupleColumn;
import javax.swing.DefaultListCellRenderer;

/**
 *
 * @author  tonyj
 * @version
 */
public class NTupleListCellRenderer extends DefaultListCellRenderer {
    public java.awt.Component getListCellRendererComponent(javax.swing.JList jList, java.lang.Object obj, int param, boolean param3, boolean param4) {
        java.awt.Component retValue = super.getListCellRendererComponent(jList, obj, param, param3, param4);
        if (obj instanceof FTupleColumn) setText(((FTupleColumn) obj).name());
        if ( obj instanceof Cut ) setText( ((Cut)obj).getName() );
        return retValue;
    }
    
}
