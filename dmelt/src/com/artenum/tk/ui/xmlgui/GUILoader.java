/**
 * Project        : ArtTk
 * Copyright      : (c) Artenum SARL, 24 rue Louis Blanc
 *                  75010, Paris, France 2009-2010
 *                  http://www.artenum.com
 *                  All copyright and trademarks reserved.
 * Email          : contact@artenum.com
 * Licence        : cf. LICENSE.txt
 * Developed By   : Artenum SARL
 * Authors        : Sebastien Jourdain      (jourdain@artenum.com)
 *                  Benoit thiebault        (thiebault@artenum.com)
 *                  Jeremie Turbet (JeT)    (turbet@artenum.com)
 *                  Julien Forest           (j.forest@artenum.com)
 * Created        : 11 Nov. 2005
 * Modified       : 23 Aug. 2010
 */
package com.artenum.tk.ui.xmlgui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GUILoader implements ActionListener {
    private ArrayList<ActionListener> actionListeners;
    private XmlLoader xmlLoader;

    public GUILoader() {
        actionListeners = new ArrayList<ActionListener>();
        xmlLoader = new XmlLoader(this);
    }

    public Object getBuildedObject(String keyNameOfObject) {
        return xmlLoader.getResult().get(keyNameOfObject);
    }

    public void load(String filePath) throws IOException, SAXException, ParserConfigurationException {
        File file = new File(filePath);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(file, xmlLoader);
    }

    public void load(File file) throws IOException, SAXException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(file, xmlLoader);
    }

    public void load(InputStream in) throws IOException, SAXException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(in, xmlLoader);
    }

    public void addActionListener(ActionListener al) {
        actionListeners.add(al);
    }

    public void removeActionListener(ActionListener al) {
        actionListeners.remove(al);
    }

    public void actionPerformed(ActionEvent e) {
        for (Iterator<ActionListener> i = actionListeners.iterator(); i.hasNext();) {
            ((ActionListener) i.next()).actionPerformed(e);
        }
    }

    private class XmlLoader extends DefaultHandler {
        public final static String TXT_CONTENT = "txtContent";
        public final static String ACTION_COMMAND = "actionCommand";
        public final static String RESULT_KEY = "resultKeyName";
        public final static String ICON_PATH = "iconPath";
        public final static String TOOL_TIP = "toolTip";
        public final static String CLASS_NAME = "className";
        public final static String SEPARATOR = "Separation";

        //
        public ArrayList<JComponent> depth;
        public Hashtable<String, Object> result;
        public ActionListener al;

        //
        String txt;
        String actionCommand;
        String resultKey;
        String iconPath;
        String className;
        String toolTip;

        public XmlLoader(ActionListener al) {
            depth = new ArrayList<JComponent>();
            result = new Hashtable<String, Object>();
            this.al = al;
        }

        public Hashtable<String, Object> getResult() {
            return result;
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
            Object obj = null;
            txt = attributes.getValue(TXT_CONTENT);
            actionCommand = attributes.getValue(ACTION_COMMAND);
            resultKey = attributes.getValue(RESULT_KEY);
            className = attributes.getValue(CLASS_NAME);
            iconPath = attributes.getValue(ICON_PATH);
            toolTip = attributes.getValue(TOOL_TIP);

            if (className == null) {
                return;
            }

            try {
                obj = Class.forName(className).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (obj instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) obj;

                // Reduce button size for toolbar
                if (button instanceof JButton) {
                    button.setMargin(new Insets(0, 0, 0, 0));
                }

                if (txt != null) {
                    button.setText(txt);
                }

                if (actionCommand != null) {
                    button.setActionCommand(actionCommand);
                    button.addActionListener(al);
                }

                if (toolTip != null) {
                    button.setToolTipText(toolTip);
                }

                if (iconPath != null) {
                    URL imgURL = GUILoader.class.getResource(iconPath);
                    if (imgURL != null) {
                        button.setIcon(new ImageIcon(imgURL));
                    } else {
                        System.err.println("Couldn't find file: " + iconPath);
                    }
                }
            }

            if (obj instanceof JComponent) {
                JComponent parentComponent = (JComponent) obj;
                depth.add(parentComponent);

                if (depth.size() > 1) {
                    ((JComponent) depth.get(depth.size() - 2)).add((JComponent) depth.get(depth.size() - 1));
                }
            }

            if (resultKey != null) {
                result.put(resultKey, obj);
            }
        }

        public void endElement(String uri, String localName, String qName)
            throws SAXException {
            if (qName.equals(SEPARATOR)) {
                Object obj = depth.get((depth.size() - 1));
                if (obj instanceof JMenu) {
                    ((JMenu) obj).addSeparator();
                }
            } else if (depth.size() > 0) {
                depth.remove(depth.size() - 1);
            }
        }
    }
}
