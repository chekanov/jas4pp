/*
 * SpreadsheetPlugin.java
 *
 * Created on December 3, 2002, 2:31 PM
 */
package org.freehep.jas.extension.spreadsheet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoManager;

import org.freehep.application.Application;
import org.freehep.application.PropertyUtilities;
import org.freehep.application.mdi.ManagedPage;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.studio.EventSender;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.jas.services.FileHandler;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.swing.ExtensionFileFilter;
import org.freehep.swing.popup.HasPopupItems;
import org.freehep.util.FreeHEPLookup;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.images.ImageHandler;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.openide.util.Lookup;

import org.sharptools.spreadsheet.Cell;
import org.sharptools.spreadsheet.CellPoint;
import org.sharptools.spreadsheet.CellRange;
import org.sharptools.spreadsheet.JSpreadsheet;
import org.sharptools.spreadsheet.SpreadsheetSelectionEvent;
import org.sharptools.spreadsheet.SpreadsheetSelectionListener;


/**
 *
 * @author tonyj
 */
public class SpreadsheetPlugin extends Plugin implements PreferencesTopic, SpreadsheetFactory, SpreadsheetIO
{
   private static Icon defIcon = ImageHandler.getIcon("image/sheet.gif", SpreadsheetPlugin.class);
   private static int sheet = 1;
   private GlobalCommands global = new GlobalCommands();
   private String findValue;
   private boolean matchCase;
   private boolean matchCell;
   private Properties user;

   public JSpreadsheet createSpreadsheet(int rows, int cols, SpreadsheetIO io, File file)
   {
      Spreadsheet ss = new Spreadsheet(rows, cols);
      ss.file = file;
      ss.io = io;
      return ss;
   }
   public void showSpreadsheet(JSpreadsheet ss, String name, Icon icon)
   {
      if (icon == null) icon = defIcon;
      if (name == null) name = "Sheet " + (sheet++);
      getApplication().getPageManager().openPage(ss, name, icon, "Spreadsheet");
   }

   //*************************************//
   // Methods for the FileHandler service //
   //*************************************//

   public boolean accept(File file) throws IOException
   {
      return file.getName().endsWith(".csv");
   }

   public FileFilter getFileFilter()
   {
      return new ExtensionFileFilter("csv", "Comma Separated Values");
   }

   public void openFile(File file) throws IOException
   {
      StringWriter writer = new StringWriter();
      FileReader reader = new FileReader(file);
      char[] buffer = new char[8096];
      for (;;)
      {
         int l = reader.read(buffer);
         if (l < 0)
         {
            break;
         }
         writer.write(buffer, 0, l);
      }
      reader.close();
      writer.close();
      JSpreadsheet spreadsheet = createSpreadsheet(1,1,this,file);
      spreadsheet.setContents(writer.getBuffer().toString(),',');
      spreadsheet.setModified(false);
      showSpreadsheet(spreadsheet,file.getName(),null);
   }

   //****************************************//
   
   
   public void write(File file, JSpreadsheet spreadsheet) throws IOException
   {
      PrintWriter pw = new PrintWriter(new FileWriter(file));
      pw.print(spreadsheet.getContents(','));
      pw.close();
   }
   protected void init() throws org.xml.sax.SAXException, java.io.IOException
   {
      Studio app = getApplication();
      FreeHEPLookup lookup = app.getLookup();
      lookup.add(this);
      user = app.getUserProperties();

      XMLMenuBuilder builder = app.getXMLMenuBuilder();
      URL xml = getClass().getResource("Spreadsheet.menus");
      builder.build(xml);

      app.getCommandTargetManager().add(global);

      app.addToolBar(builder.getToolBar("fileToolBar"), "File Toolbar");
      app.addToolBar(builder.getToolBar("editToolBar"), "Edit Toolbar");
      app.addToolBar(builder.getToolBar("spreadsheetToolBar"), "Speadsheet Toolbar");
   }

   public boolean apply(JComponent panel)
   {
      ((PrefsDialog) panel).apply(this);
      return true;
   }
   
   public JComponent component()
   {
      return new PrefsDialog(this);
   }
   
   public String[] path()
   {
      return new String[]{"Spreadsheet"};
   }
   private final static String SPREADSHEET_ROWS = "org.freehep.jas.extension.spreadsheet.Rows";
   private final static String SPREADSHEET_COLS = "org.freehep.jas.extension.spreadsheet.Columns";
   private final static String COLUMN_WIDTH = "org.freehep.jas.extension.spreadsheet.columnWidth";
   public int getDefaultRows()
   {
      return PropertyUtilities.getInteger(user, SPREADSHEET_ROWS, 40);
   }
   
   void setDefaultRows(int defaultRows)
   {
      user.setProperty(SPREADSHEET_ROWS, String.valueOf(defaultRows));
   }
   
   int getDefaultColumns()
   {
      return PropertyUtilities.getInteger(user, SPREADSHEET_COLS, 10);
   }
   
   void setDefaultColumns(int defaultColumns)
   {
      user.setProperty(SPREADSHEET_COLS, String.valueOf(defaultColumns));
   }
   
   int getColumnWidth()
   {
       return PropertyUtilities.getInteger(user, COLUMN_WIDTH, 80);
   }
   
   void setColumnWidth(int columnWidth)
   {
      user.setProperty(COLUMN_WIDTH, String.valueOf(columnWidth));
   }

   public class GlobalCommands extends CommandProcessor
   {
      public void onNewSpreadSheet()
      {
         Application app = Application.getApplication();
         int rows = getDefaultRows();
         int columns = getDefaultColumns();
         NewDialog newDialog = new NewDialog(rows, columns);
         int rc = newDialog.show(app, "New Spreadsheet");
         if (rc != JOptionPane.OK_OPTION)
         {
            return;
         }
         rows = newDialog.getRows();
         columns = newDialog.getColumns();
         if (newDialog.getSaveAsDefault())
         {
            setDefaultRows(rows);
            setDefaultColumns(columns);
         }
         showSpreadsheet(createSpreadsheet(rows, columns, null, null), null, null);
      }
   }

   private class Spreadsheet extends JSpreadsheet implements HasPopupItems, ManagedPage
   {
      private Commands commands = new Commands();
      private File file; // associated file (if any)
      private SpreadsheetIO io; // The IO which read the spreadsheet (if any)
      private Studio app = (Studio) Application.getApplication();
      private boolean modified = false;

      Spreadsheet(int rows, int columns)
      {
         super(rows, columns);
         addUndoableEditListener(commands);
         addSelectionListener(commands);
         setColumnWidth(SpreadsheetPlugin.this.getColumnWidth());
      }

      protected JTable createTable()
      {
         JTable table = new JTable()
         {
            /**
             * Prevent auto cell-editing from starting unless the key pressed corresponds to a regular character.
             */
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
					int condition, boolean pressed) {
                if (!isEditing() && ((e.getModifiersEx() & e.CTRL_DOWN_MASK) != 0 || e.getKeyChar() == e.CHAR_UNDEFINED) && getInputMap(condition).get(ks) == null)
                {
                   return false;
                }
                else return super.processKeyBinding(ks, e, condition, pressed);
            } 
         };
         
         return table;
      }
      public void setPageContext(PageContext context)
      {
      }

      public boolean close()
      {
         if (isModified())
         {
            int rc = JOptionPane.showConfirmDialog(app, "Save Changes?");
            if (rc == JOptionPane.YES_OPTION)
            {
               return save(file);
            }
            return rc == JOptionPane.NO_OPTION;
         }
         return true;
      }

      public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p)
      {
         app.getXMLMenuBuilder().mergePopupMenu("spreadsheetPopupMenu", menu);

         //         EventSender es = app.getEventSender();
         //         if (es.hasListeners(EditorPopupEvent.class))
         //         {
         //            EditorPopupEvent e = new EditorPopupEvent(source,menu,mimeType,getDocument());
         //            es.broadcast(e);
         //         }
         return menu;
      }

      public void pageClosed()
      {
      }

      public void pageDeiconized()
      {
      }

      public void pageDeselected()
      {
         app.getCommandTargetManager().remove(commands);
      }

      public void pageIconized()
      {
      }

      public void pageSelected()
      {
         app.getCommandTargetManager().add(commands);
      }

      public void remove(boolean byRow)
      {
         CellRange range = getSelectedRange();
         if (range != null)
         {
            if (byRow)
            {
               int start = range.getStartRow();
               int end = range.getEndRow();
               if ((baseRow + (end - start + 1)) >= getRowCount())
               {
                  tooMuchDeletion();
               }
               else
               {
                  if (isDeletionSafe(byRow, start, end) || unsafeDeletion())
                  {
                     remove(byRow, start, end);
                  }
               }
            }
            else
            {
               int start = range.getStartCol();
               int end = range.getEndCol();
               if ((baseCol + (end - start + 1)) >= getColumnCount())
               {
                  tooMuchDeletion();
               }
               else
               {
                  if (isDeletionSafe(byRow, start, end) || unsafeDeletion())
                  {
                     remove(byRow, start, end);
                  }
               }
            }
         }
      }

      public void sort(boolean byRow)
      {
         CellRange range = getSelectedRange();
         if (range != null)
         {
            //create and show the sort dialog
            SortDialog sortDialog = new SortDialog(byRow, range);
            int rc = sortDialog.show(app, "Sort");
            if (rc == JOptionPane.OK_OPTION)
            {
               int first = sortDialog.getCriteriaA();
               first += (byRow ? range.getStartRow() : range.getStartCol());

               int second = sortDialog.getCriteriaB();
               if (second >= 0)
               {
                  second += (byRow ? range.getStartRow() : range.getStartCol());
               }
               sort(range, first, second, byRow, sortDialog.firstAscending(), sortDialog.secondAscending());
            }
         }
      }

      /** 
       *
       * @param newValue is true if it should require a new value (Find...)
       *                 is false if it already has a value (Find Next)
       */
      private void find(boolean newValue)
      {
         CellPoint start;

         //checks if anything is selected
         CellRange range = getSelectedRange();

         if (range != null)
         {
            int x = range.getStartRow();
            int y = range.getStartCol();

            // start from the next cell
            if (!newValue)
            {
               if (y < getColumnCount())
               {
                  y++;
               }
               else
               {
                  y = 1;
                  x++;
               }
            }

            start = new CellPoint(x, y);
         }
         else
         {
            // or start from the beginning
            start = new CellPoint(baseRow, baseCol);
         }

         if (newValue)
         {
            // ask for new value         
            FindDialog findDialog = new FindDialog(findValue, matchCase, matchCell);
            int rc = findDialog.show(app, "Find");
            if (rc != JOptionPane.OK_OPTION)
            {
               return;
            }

            String inputValue = findDialog.getString();

            //if input is cancelled or nothing is entered then don't change anything
            if ((inputValue == null) || (inputValue.length() == 0))
            {
               return;
            }
            else
            {
               findValue = inputValue;
               matchCase = findDialog.isCaseSensitive();
               matchCell = findDialog.isCellMatching();
            }
         }
         else if (findValue == null)
         {
            return;
         }

         CellPoint found = find(start, findValue, matchCase, matchCell);
         if (found != null)
         {
            setSelectedRange(new CellRange(found.getRow(), found.getRow(), found.getCol(), found.getCol()));
         }
         else
         {
            app.error("Search complete and no more \"" + findValue + "\" were found.");
         }
      }

      private void insert(boolean byRow)
      {
         CellRange range = getSelectedRange();
         if (range != null)
         {
            if (byRow)
            {
               insert(byRow, range.getStartRow(), range.getEndRow());
            }
            else
            {
               insert(byRow, range.getStartCol(), range.getEndCol());
            }
         }
      }

      private boolean save(File f)
      {
         SpreadsheetIO ioUsed = io;
         try
         {
            if (f == null || ioUsed == null)
            {
               JFileChooser chooser = new JFileChooser();
               chooser.setDialogTitle("Save As...");
               chooser.setAcceptAllFileFilterUsed(false);

               Lookup.Template template = new Lookup.Template(SpreadsheetIO.class);
               Lookup.Result result = getApplication().getLookup().lookup(template);
               FileFilter selected = null;
               for (Iterator i = result.allInstances().iterator(); i.hasNext(); )
               {
                  SpreadsheetIO io = (SpreadsheetIO) i.next();
                  FileFilter ff = io.getFileFilter();
                  chooser.addChoosableFileFilter(ff);  
                  if (io == this.io) selected = ff;
               }
               if (selected != null) chooser.setFileFilter(selected);

               int returnVal = chooser.showOpenDialog(app);
               if (returnVal != JFileChooser.APPROVE_OPTION)
               {
                  return false;
               }
               else
               {
                  f = chooser.getSelectedFile();
                  FileFilter ff = chooser.getFileFilter();
                  FileFilter[] ffs = chooser.getChoosableFileFilters();
                  int index = 0;
                  for (Iterator i = result.allInstances().iterator(); i.hasNext(); index++)
                  {
                     ioUsed = (SpreadsheetIO) i.next();
                     if (ffs[index] == ff) break; 
                  }

                  if (f.exists())
                  {
                     int rc = JOptionPane.showConfirmDialog(app, "Replace existing file?", null, JOptionPane.OK_CANCEL_OPTION);
                     if (rc != JOptionPane.OK_OPTION)
                     {
                        return false;
                     }
                  }
               }
            }
            ioUsed.write(f,this);
            setModified(false);
            this.file = f;
            this.io = ioUsed;
            return true;
         }
         catch (IOException eh)
         {
            app.error("Couldn't save to file" + f.getName(), eh);
            return false;
         }
      }

      private void tooMuchDeletion()
      {
         JOptionPane.showMessageDialog(app, "You can not delete all the rows or columns!", "Delete", JOptionPane.ERROR_MESSAGE);
      }

      private boolean unsafeDeletion()
      {
         int choice = JOptionPane.showConfirmDialog(app, "The deletion may cause irriversible data loss in other cells.\n\n" + "Do you really want to proceed?\n\n", "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

         return choice == JOptionPane.YES_OPTION;
      }

      public class Commands extends CommandProcessor implements UndoableEditListener, SpreadsheetSelectionListener
      {
         private UndoManager um = new UndoManager();
         private boolean isSelected = false;

         public void enableClear(CommandState state)
         {
            state.setEnabled(isSelected);
         }

         public void enableCopy(CommandState state)
         {
            state.setEnabled(isSelected);
         }

         public void enableCut(CommandState state)
         {
            state.setEnabled(isSelected);
         }

         public void enableFill(CommandState state)
         {
            state.setEnabled(isSelected);
         }

         public void enableInsertColumn(CommandState state)
         {
            state.setEnabled(isSelected);
         }

         public void enableInsertRow(CommandState state)
         {
            state.setEnabled(isSelected);
         }
         public void enablePaste(CommandState state)
         {
            state.setEnabled(isSelected);
         }
         public void enableRedo(CommandState state)
         {
            state.setEnabled(um.canRedo());
         }

         public void enableRemoveColumn(CommandState state)
         {
            state.setEnabled(isSelected);
         }

         public void enableRemoveRow(CommandState state)
         {
            state.setEnabled(isSelected);
         }

         public void enableSortByColumn(CommandState state)
         {
            state.setEnabled(isSelected);
         }

         public void enableSortByRow(CommandState state)
         {
            state.setEnabled(isSelected);
         }

         public void enableUndo(CommandState state)
         {
            state.setEnabled(um.canUndo());
         }

         public void onClear()
         {
            CellRange range = getSelectedRange();
            if (range != null)
            {
               clear(range);
            }
         }

         public void onCopy()
         {
            copy();
         }

         public void onCut()
         {
            cut();
         }

         public void onFill()
         {
            CellRange range = getSelectedRange();
            Cell first = getCellAt(range.getStartRow(), range.getStartCol());
            String fillValue = first.toString();

            Icon fillIcon = ImageHandler.getIcon("image/fill32.gif", SpreadsheetPlugin.class);
            String inputValue = (String) JOptionPane.showInputDialog(app, "Please enter a value to fill the range", "Fill", JOptionPane.INFORMATION_MESSAGE, fillIcon, null, fillValue);

            //if input is cancelled or nothing is entered 
            //then don't change anything
            if ((inputValue != null) && (inputValue.length() != 0))
            {
               fill(range, inputValue);
            }
         }

         public void onFind()
         {
            find(true);
         }
         public void enableFind(CommandState state)
         {
            state.setEnabled(isSelected);
         }
         public void onFindNext()
         {
            find(false);
         }
         public void enableFindNext(CommandState state)
         {
            state.setEnabled(isSelected);
         }
         public void onInsertColumn()
         {
            insert(false);
         }

         public void onInsertRow()
         {
            insert(true);
         }

         public void onPaste()
         {
            paste();
         }

         public void onRedo()
         {
            um.redo();
            setChanged();
         }

         public void onRemoveColumn()
         {
            remove(false);
         }

         public void onRemoveRow()
         {
            remove(true);
         }

         public void onSave()
         {
            save(file);
            setChanged();
         }

         public void onSaveAs()
         {
            save(null);
            setChanged();
         }

         public void onSortByColumn()
         {
            sort(false);
         }

         public void onSortByRow()
         {
            sort(true);
         }

         public void onUndo()
         {
            um.undo();
            setChanged();
         }

         public void selectionChanged(SpreadsheetSelectionEvent e)
         {
            isSelected = e.getSelectionRange() != null;
            setChanged();
         }

         /** An undoable edit happened
          *
          */
         public void undoableEditHappened(UndoableEditEvent e)
         {
            um.addEdit(e.getEdit());
            setChanged();
            global.setChanged();
         }
      }
   }
}