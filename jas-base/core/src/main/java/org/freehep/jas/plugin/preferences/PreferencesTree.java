package org.freehep.jas.plugin.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.util.FreeHEPLookup;
import org.openide.util.Lookup;

class PreferencesTree extends JTree
{   
   PreferencesTree(FreeHEPLookup lookup)
   {
      setModel(create(lookup));
      getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      setShowsRootHandles(true);
      setRootVisible(false);
      DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
      renderer.setClosedIcon(null);
      renderer.setOpenIcon(null);
      renderer.setLeafIcon(null);
      setCellRenderer(renderer);
   }
   PreferencesTopic getSelectedTopic()
   {
      if (this.getSelectionCount() == 0) return null;
      PrefTreeNode node = (PrefTreeNode) getSelectionPath().getLastPathComponent();
      return node.getTopic();
   }
   
   /** Creates a new instance of PreferencesTreeModel */
   private static TreeModel create(FreeHEPLookup lookup)
   {
      Lookup.Template template = new Lookup.Template(PreferencesTopic.class);
      Lookup.Result result = lookup.lookup(template);
      
      PrefTreeNode root = new PrefTreeNode();
      
      for (Iterator i = result.allInstances().iterator(); i.hasNext(); )
      {
         PreferencesTopic topic = (PreferencesTopic) i.next();
         String[] path = topic.path();
         PrefTreeNode node = root;
         for (int j=0; j<path.length; j++)
         {
            String name = path[j];
            PrefTreeNode newNode = node.find(name);
            if (newNode == null)
            {
               newNode = new PrefTreeNode(node,name);
            }
            node = newNode;
         }
         node.setTopic(topic);
      }
      root.sort();
      return new DefaultTreeModel(root);
   }
   private static class PrefTreeNode implements TreeNode
   {
      private PrefTreeNode parent;
      private String name;
      private PreferencesTopic topic;
      private List children;
      private Map map = new TreeMap();
      
      PrefTreeNode(PrefTreeNode parent, String name)
      {
         this.parent = parent;
         this.name = name;
         parent.add(this);
      }
      PrefTreeNode()
      {
         this.name = "root";
      }
      public Enumeration children()
      {
         return Collections.enumeration(children);
      }
      public boolean getAllowsChildren()
      {
         return true;
      }
      public TreeNode getChildAt(int childIndex)
      {
         return (TreeNode) children.get(childIndex);
      }
      public int getChildCount()
      {
         return children.size();
      }
      public int getIndex(TreeNode node)
      {
         return children.indexOf(node);
      }
      public TreeNode getParent()
      {
         return parent;
      }
      public boolean isLeaf()
      {
         return children == null;
      }
      private void add(PrefTreeNode child)
      {
         map.put(child.name,child);
      }
      void sort()
      {
         if (map.size() > 0) 
         { 
            children = new ArrayList(map.values());
            for (Iterator i = children.iterator(); i.hasNext(); )
            {
               ((PrefTreeNode) i.next()).sort();
            }
         }
         map = null;
      }
      PrefTreeNode find(String name)
      {
         return (PrefTreeNode) map.get(name);
      }
      void setTopic(PreferencesTopic topic)
      {
         this.topic = topic;
      }
      public String toString()
      {
         return name;
      }
      PreferencesTopic getTopic()
      {
         return topic;
      }
   }
}
