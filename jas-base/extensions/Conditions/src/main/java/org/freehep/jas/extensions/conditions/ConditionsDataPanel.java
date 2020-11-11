package org.freehep.jas.extensions.conditions;

import java.awt.CardLayout;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.mdi.PageEvent;
import org.freehep.application.mdi.PageListener;
import org.freehep.conditions.CachedConditions;
import org.freehep.conditions.Conditions;
import org.freehep.conditions.ConditionsEvent;
import org.freehep.conditions.ConditionsInvalidException;
import org.freehep.conditions.ConditionsListener;
import org.freehep.conditions.ConditionsSet;
import org.freehep.conditions.RawConditions;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class ConditionsDataPanel extends JPanel implements TreeSelectionListener, PageListener, ConditionsListener {

// -- Private parts : ----------------------------------------------------------
  
  private final ConditionsPlugin _plugin;
  private PageContext _page;
  private Conditions _conditions;
  
  private static final String TEXT_PANEL = "Text";
  private static final String SET_PANEL = "Set";
  private static final String EMPTY_PANEL = "Empty";

  private final JTextPane _textPanel;
  private final ConditionsSetView _setPanel;

// -- Construction and initialization : ----------------------------------------
  
  ConditionsDataPanel(ConditionsPlugin plugin) {
    super(new CardLayout());
    _plugin = plugin;

    JLabel emptyPanel = new JLabel("No conditions data");
    emptyPanel.setHorizontalAlignment(SwingConstants.CENTER);
    emptyPanel.setHorizontalTextPosition(SwingConstants.CENTER);
    add(emptyPanel, EMPTY_PANEL);

    _textPanel = new JTextPane();
    _textPanel.setEditable(false);
    _textPanel.setContentType("text/html; charset=UTF-8");
    add(new JScrollPane(_textPanel), TEXT_PANEL);

    _setPanel = new ConditionsSetView();
    add(_setPanel, SET_PANEL);
  }
  
  void setPage(PageContext page) {
    _page = page;
    _page.addPageListener(this);
  }


// -- Responding to selection changes : ----------------------------------------

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    Conditions con = _plugin.getTreePanel().getSelection();
    if (con != _conditions) {
      if (_conditions != null) _conditions.removeConditionsListener(this);
      _conditions = con;
      if (_conditions != null) _conditions.addConditionsListener(this);
      conditionsChanged(null);
    }
  }
  
// -- Responding to page events : ----------------------------------------------
  
  @Override
  public void pageChanged(PageEvent pe) {
    switch (pe.getID()) {
      case PageEvent.PAGESELECTED:
      case PageEvent.PAGEOPENED:
        ConditionsTreePanel treePanel = _plugin.getTreePanel();
        Conditions selection = treePanel.getSelection();
        if (_conditions != selection) {
          if (_conditions == null) {
            _conditions = selection;
          } else {
            if (!treePanel.setSelection(_conditions)) _conditions = selection;
          }
          conditionsChanged(null);
        }
        treePanel.addTreeSelectionListener(this);
        break;
      case PageEvent.PAGEDESELECTED:
        _plugin.getTreePanel().removeTreeSelectionListener(this);
        break;
      case PageEvent.PAGECLOSED:
        _plugin.getTreePanel().removeTreeSelectionListener(this);
        if (_conditions != null) _conditions.removeConditionsListener(this);
        _conditions = null;
        break;
    }
  }
  
  
// -- Responding to conditions changes : ---------------------------------------
  
  @Override
  public void conditionsChanged(ConditionsEvent event) {
    CardLayout layout = (CardLayout)getLayout();
    StringBuilder sb;
    if (_conditions == null) {
      layout.show(this, EMPTY_PANEL);
      _page.setTitle("Conditions Data");
    } else {
      _page.setTitle(_conditions.getName());
      try {
        switch (_conditions.getCategory()) {
          case RAW:
            sb = new StringBuilder();
            sb.append("Raw conditions: ").append(_conditions.getName()).append("<br/>");
            InputStream in = null;
            try {
              in = ((RawConditions)_conditions).getInputStream();
              sb.append(in.toString());
            } catch (IOException x) {
              sb.append("Cannot open stream:<br/>").append(x);
            } finally {
              try {
                if (in != null) in.close();
              } catch (IOException x) {}
            }
            _textPanel.setText(sb.toString());
            _textPanel.setContentType("text/html; charset=UTF-8");
            layout.show(this, TEXT_PANEL);
            break;
          case SET:
            _setPanel.set((ConditionsSet) _conditions);
            layout.show(this, SET_PANEL);
            break;
          case CACHED:
            String text = "";
            _textPanel.setContentType("text/html; charset=UTF-8");
            Object data = ((CachedConditions)_conditions).getCachedData();
            if (data == null) {
              text = "NULL";
            } else if (data instanceof Document) {
              text = prettyFormat((Document)data);
              _textPanel.setContentType("text/plain; charset=UTF-8");
            } else {
              text = data.toString();
            }
            _textPanel.setText(text);
            _textPanel.setCaretPosition(0);
            layout.show(this, TEXT_PANEL);
            break;
          default:
            _textPanel.setText("Conditions : " + _conditions.getName() + " of category " + _conditions.getCategory());
            layout.show(this, TEXT_PANEL);
        }
      } catch (ConditionsInvalidException x) {
        _textPanel.setText("Conditions : " + _conditions.getName() + "<br/><font color=\"#990000\">CURRENTLY INVALID</font>");
        layout.show(this, TEXT_PANEL);
      }
    }
    revalidate();
  }

  
// -- Local methods : ----------------------------------------------------------

  private String prettyFormat(Document document) {
    try {
      final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
      final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
      final LSSerializer writer = impl.createLSSerializer();
      try {
        writer.getDomConfig().setParameter("format-pretty-print", true); // Set this to true if the output needs to be beautified.
        writer.getDomConfig().setParameter("xml-declaration", false);    // Set this to true if the declaration is needed to be outputted.
      } catch (DOMException x) {
      } // Protect against older parser implementations
      return writer.writeToString(document);
    } catch (Exception ex) {
      throw new RuntimeException("Error formating XML", ex);
    }
  }
  
}
