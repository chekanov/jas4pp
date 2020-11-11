package org.freehep.jas.extension.tupleExplorer.jel;

import gnu.jel.CompilationException;
import hep.aida.ref.tuple.FTuple;
import hep.aida.ref.tuple.FTupleColumn;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import org.freehep.jas.extension.tupleExplorer.cut.CutColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.util.Value;

/**
 *
 * @author tonyj
 * @version $Id: ExpressionField.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public class ExpressionField extends JTextField {
    
    private MutableTuple tuple;
    private MutableTupleTree tupleTree;
    private Value value = new Value();
    
    /** Creates new ExpressionField */
    public ExpressionField(MutableTuple tuple, MutableTupleTree tupleTree) {
        this.tuple = tuple;
        this.tupleTree = tupleTree;
        JPopupMenu menu = new PopupMenu();
        addMouseListener(new PopupListener(menu));
    }
    
    public NTupleCompiledExpression compile(Class resultType) throws CompilationException {
        return Compiler.compile(tupleTree,getText(),resultType);
    }
        
    private static class PopupListener extends MouseAdapter {
        private JPopupMenu menu;
        
        PopupListener(JPopupMenu menu) {
            this.menu = menu;
        }
        public void mouseReleased(MouseEvent mouseEvent) {
            maybePopup(mouseEvent);
        }
        public void mousePressed(MouseEvent mouseEvent) {
            maybePopup(mouseEvent);
        }
        private void maybePopup(MouseEvent me) {
            if (menu.isPopupTrigger(me)) {
                menu.show(me.getComponent(),me.getX(),me.getY());
            }
        }
    }
    
    
    
    private void fillMenu(JMenu menu, MutableTupleTree tree, MutableTuple nt, String fullName) {
        for (int i=0; i<nt.columns(); i++) {
            MutableTupleColumn col = (MutableTupleColumn)nt.column(i);
            if ( col.isFolder() ) {
//                if ( nt.treePath().pathByAddingChild(col.name()).isDescendant(tuple.treePath()) ) {
                    JMenu m = new JMenu(col.name());
                    String name;
                    if ( fullName != null && fullName != "" )
                        name = fullName+"."+col.name();
                    else
                        name = col.name();
                    fillMenu(m, tupleTree, tupleTree.mutableTupleForPath(nt.treePath().pathByAddingChild(col.name()) ),name);
//                    FolderMenu m = new FolderMenu( tupleTree.mutableTupleForPath( nt.treePath().pathByAddingChild( col.name() )) , fullName );
                    menu.add(m);
//                }
            }
            else if ( ! CutColumn.class.isAssignableFrom( col.getClass() ) ) 
                menu.add(new ColumnMenuItem(nt.column(i), fullName));
        }
    }
    
    
    private class PopupMenu extends JPopupMenu {
        PopupMenu() {
            JMenu menu = new JMenu("Insert");
            fillMenu(menu, tupleTree, tupleTree.rootMutableTuple(), null);
            add(menu);
        }
        
    }

    private class ColumnMenuItem extends JMenuItem {
        private FTupleColumn col;
        private String fullName;
        
        ColumnMenuItem(FTupleColumn col, String parentFullName) {
            super(col.name());
            this.col = col;
            if (parentFullName == null) this.fullName = new String(this.col.name());
            else this.fullName = new String(parentFullName+"."+this.col.name());
        }
        protected void fireActionPerformed(ActionEvent e) {
            String name = col.name();
            if (isJavaIdentifier(name)) {
                //replaceSelection(name);
                replaceSelection(fullName);
            }
            else {
                Class type = col.type();
                String typeName = NTupleNameResolver.nameForType(type);
                replaceSelection("get"+typeName+"Property(\""+name+"\")");
            }
        }
        private boolean isJavaIdentifier(String name) {
            if (name.length() < 0) return false;
            if (!Character.isJavaIdentifierStart(name.charAt(0))) return false;
            for (int i=1; i<name.length(); i++) {
                if (!Character.isJavaIdentifierPart(name.charAt(i))) return false;
            }
            return true;
        }
    }
    
}
