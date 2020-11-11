package org.freehep.jas.extension.compiler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.freehep.util.images.ImageHandler;

/**
 * A general purpose visible list from which things can be added and removed.
 * @author tonyj
 * @version $Id: AddRemovePanel.java 13884 2011-09-20 23:10:19Z tonyj $
 */
class AddRemovePanel extends JPanel implements ListSelectionListener, ActionListener
{
   private JButton add = new JButton(ImageHandler.getIcon("/toolbarButtonGraphics/general/Add16.gif", AddRemovePanel.class));
   private JButton delete = new JButton(ImageHandler.getIcon("/toolbarButtonGraphics/general/Remove16.gif", AddRemovePanel.class));
   private JButton down = new JButton(ImageHandler.getIcon("/toolbarButtonGraphics/navigation/Down16.gif", AddRemovePanel.class));
   private JButton up = new JButton(ImageHandler.getIcon("/toolbarButtonGraphics/navigation/Up16.gif", AddRemovePanel.class));
   private JList list;
   private ListModel model;
   private String addText;

   AddRemovePanel(List path, String instructions, String borderText, String addText, ListCellRenderer r)
   {
      this(path, instructions, borderText, addText);
      list.setCellRenderer(r);
   }

   AddRemovePanel(List path, String instructions, String borderText, String addText)
   {
      super(new BorderLayout());
      if (borderText != null)
         setBorder(BorderFactory.createTitledBorder(borderText));
      this.model = new ListModel(path);
      this.list = new JList(model);
      this.addText = addText;

      Box box = Box.createVerticalBox();

      box.add(up);
      box.add(down);
      box.add(delete);
      box.add(add);

      up.addActionListener(this);
      down.addActionListener(this);
      delete.addActionListener(this);
      add.addActionListener(this);

      Insets margin = new Insets(0, 0, 0, 0);
      up.setMargin(margin);
      down.setMargin(margin);
      delete.setMargin(margin);
      add.setMargin(margin);

      up.setToolTipText("Move item up");
      down.setToolTipText("Move item down");
      delete.setToolTipText("Delete item");
      add.setToolTipText("New item");

      list.addListSelectionListener(this);

      if (instructions != null)
         add(new JLabel(instructions), BorderLayout.NORTH);

      JScrollPane scroll = new JScrollPane(list);
      scroll.setPreferredSize(new Dimension(100, 40));
      add(scroll, BorderLayout.CENTER);
      add(box, BorderLayout.EAST);
      setSensitive();
   }

   public void setEnabled(boolean value)
   {
      super.setEnabled(value);
      setSensitive();
   }

   public void actionPerformed(ActionEvent e)
   {
      Object source = e.getSource();
      int i = list.getSelectedIndex();
      if (source == up)
      {
         model.swap(i, i - 1);
         list.setSelectedIndex(i - 1);
      }
      else if (source == down)
      {
         model.swap(i, i + 1);
         list.setSelectedIndex(i + 1);
      }
      else if (source == delete)
      {
         model.delete(i);
      }
      else if (source == add)
      {
         Object o = add(addText);
         if (o instanceof Object[])
         {
            Object[] oo = (Object[]) o;
            for (int j = 0; j < oo.length; j++)
               model.add(oo[j]);
         }
         else if (o != null)
            model.add(o);
      }
      setSensitive();
   }

   public List update()
   {
      return model.update();
   }

   public void valueChanged(ListSelectionEvent e)
   {
      setSensitive();
   }

   protected Object add(String addText)
   {
      return JOptionPane.showInputDialog(this, addText);
   }

   void setModel(List model)
   {
      this.model = new ListModel(model);
      list.setModel(this.model);
   }

   void addListDataListener(ListDataListener l)
   {
      model.addListDataListener(l);
   }

   private void setSensitive()
   {
      boolean enabled = isEnabled();
      int i = list.getSelectedIndex();
      delete.setEnabled(enabled && (i >= 0));
      up.setEnabled(enabled && (i > 0));
      down.setEnabled(enabled && (i >= 0) && (i < (model.getSize() - 1)));
      add.setEnabled(enabled);
      list.setEnabled(enabled);
   }

   /**
    * A list model, used for representing lists in JASAddRemovePanel
    */
   private class ListModel extends AbstractListModel
   {
      private List vector;

      ListModel(List path)
      {
         vector = new ArrayList(path);
      }

      public Object getElementAt(int i)
      {
         return vector.get(i);
      }

      final public int getSize()
      {
         return vector.size();
      }

      void add(Object f)
      {
         int i = vector.size();
         vector.add(f);
         fireIntervalAdded(this, i, 1);
      }

      void delete(int i)
      {
         vector.remove(i);
         fireIntervalRemoved(this, i, 1);
      }

      void swap(int i1, int i2)
      {
         Object x = vector.get(i1);
         vector.set(i1, vector.get(i2));
         vector.set(i2, x);
         fireContentsChanged(this, i1, i2);
      }

      List update()
      {
         return Collections.unmodifiableList(vector);
      }
   }
}
