package org.freehep.jas.extensions.conditions;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.freehep.conditions.Conditions;
import org.freehep.conditions.ConditionsManager;

/**
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class ConditionsTreePanel extends JPanel implements TreeSelectionListener {

// -- Private parts : ----------------------------------------------------------
  
  private final ConditionsPlugin _plugin;
  private final JScrollPane _scrollPane;
  private final Set<TreeSelectionListener> _treeSelectionListeners;
  
  private EnumMap<Conditions.Category,DefaultMutableTreeNode> _roots;
  private JTree _tree;

// -- Construction and initialization : ----------------------------------------
  
  ConditionsTreePanel(ConditionsPlugin plugin) {
    super(new GridLayout(1,0));
    _scrollPane = new JScrollPane();
    add(_scrollPane);
    _treeSelectionListeners = Collections.newSetFromMap(new  IdentityHashMap<TreeSelectionListener,Boolean>());
    _plugin = plugin;
  }
  
// -- Updating : ---------------------------------------------------------------
  
  /**
   * Updates this panel from ConditionsManager.
   * Selection is not changed if the selected <tt>Conditions</tt> still exists.
   */
  void update() {
    
    ConditionsManager conMan = (ConditionsManager) _plugin.getApplication().getLookup().lookup(ConditionsManager.class);
    
    if (conMan == null) {
      
      _roots = null;
      _tree = null;
      JLabel label = new JLabel("<html>No ConditionsManager<br>in JAS 3 registry.");
      label.setBackground(Color.WHITE);
      label.setOpaque(true);
      label.setVerticalTextPosition(SwingConstants.CENTER);
      label.setVerticalAlignment(SwingConstants.CENTER);
      label.setHorizontalTextPosition(SwingConstants.CENTER);
      label.setHorizontalAlignment(SwingConstants.CENTER);
      label.setBorder(new EmptyBorder(3, 3, 3, 3));
      add(new JScrollPane(label));
      
    } else {
      
      Conditions selection = getSelection();
      
      DefaultMutableTreeNode selectedNode = null;
      DefaultMutableTreeNode top = new DefaultMutableTreeNode("Conditions");
      _roots = new EnumMap(Conditions.Category.class);
      for (Conditions.Category cat : Conditions.Category.values()) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(cat);
        _roots.put(cat, root);
        top.add(root);
        for (Conditions con : conMan.listConditions(cat)) {
          DefaultMutableTreeNode node = new DefaultMutableTreeNode(con);
          root.add(node);
          if (con == selection) selectedNode = node;
        }
      }
      
      DefaultTreeModel treeModel = new DefaultTreeModel(top);
      _tree = new JTree(treeModel);
      _tree.addTreeSelectionListener(this);
      if (selectedNode != null) {
        TreePath path = new TreePath(treeModel.getPathToRoot(selectedNode));
        _tree.setSelectionPath(path);
      }
      
      _tree.setRootVisible(true);
      _tree.setBorder(new EmptyBorder(3, 3, 3, 3));
      _tree.setCellRenderer(new DefaultTreeCellRenderer() {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
          if (value != null && value instanceof DefaultMutableTreeNode) {
            value = ((DefaultMutableTreeNode)value).getUserObject();
            if (value != null && value instanceof Conditions) {
              value = ((Conditions)value).getName();
            }
          }
          return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        }
      });
      _scrollPane.setViewportView(_tree);
      this.revalidate();
    }
    
  }
  
  
// -- Handling selection : -----------------------------------------------------
  
  public void addTreeSelectionListener(TreeSelectionListener tsl) {
    _treeSelectionListeners.add(tsl);
  }
  
  public void removeTreeSelectionListener(TreeSelectionListener tsl) {
    _treeSelectionListeners.remove(tsl);
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    for (TreeSelectionListener listener : _treeSelectionListeners) {
      listener.valueChanged(e);
    }
  }
  
  public Conditions getSelection() {
    try {
      return (Conditions) ((DefaultMutableTreeNode)(_tree.getSelectionPath().getLastPathComponent())).getUserObject();
    } catch (NullPointerException x) {
    } catch (ClassCastException x) {
    }
    return null;
  }
  
  /**
   * Selects node with the specified <tt>Conditions</tt>.
   * If the given Conditions object is not in the current tree, the selection does not change.
   * @return True if the specified Conditions was found and selected.
   */
  public boolean setSelection(Conditions conditions) {
    if (_roots == null) return false;
    DefaultMutableTreeNode root = _roots.get(conditions.getCategory());
    if (root == null) return false;
    Enumeration en = root.depthFirstEnumeration();
    while (en.hasMoreElements()) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
      if (conditions == node.getUserObject()) {
        _tree.setSelectionPath(new TreePath(node.getPath()));
        revalidate();
        return true;
      }
    }
    return false;
  }

}
