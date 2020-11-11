package org.freehep.jas.extensions.heprep;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.JComponent;
import org.xml.sax.SAXException;

import org.openide.util.Lookup;
import org.freehep.application.studio.Plugin;
import org.freehep.jas.services.FileHandler;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.record.source.RecordSource;
import org.freehep.swing.ExtensionFileFilter;
import org.freehep.util.FreeHEPLookup;
import org.freehep.jas.plugin.datasource.FileHandlerDataSource;
import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepReader;
import hep.graphics.heprep.HepRepVersionException;
import hep.graphics.heprep.HepRepViewer;
import hep.graphics.heprep.xml.XMLHepRepFactory;
// FIXME: update when HEPREP-13 is implemented
import hep.graphics.heprep.ref.DefaultHepRep;

/**
 * Handles the opening of single event .heprep, .xml.heprep and multi event .heprep.zip
 * HepRep files to provide HepReps to a registered HepRep Viewer.
 *
 * @author tonyj
 * @author Mark Donszelmann
 * @version $Id: HepRepPlugin.java 13988 2012-05-09 01:09:42Z onoprien $
 */
public final class HepRepPlugin extends Plugin implements FileHandler, PreferencesTopic {

  protected void init() throws IOException {
    FreeHEPLookup lookup = getApplication().getLookup();
    lookup.add(this, "HepRepPlugin");
    lookup.add(new FileHandlerDataSource(this));
    lookup.add(new HepRepConverter());
  }

  public FileFilter getFileFilter() {
    return new ExtensionFileFilter(
            new String[]{
              ".heprep.zip",
              ".bheprep.zip",
              ".heprep.gz",
              ".bheprep.gz",
              ".heprep.xml",
              ".heprep",
              ".bheprep",
              ".heprep1",
              ".heprep2"
            }, "HepRep XML");
  }

  public void setPreferredViewer(String name) {
    getApplication().getUserProperties().setProperty("heprep.viewer", name);
  }

  public String getPreferredViewer() {
    return getApplication().getUserProperties().getProperty("heprep.viewer", "WIRED4");
  }

  public boolean accept(File file) throws IOException {
    return file.getName().endsWith(".heprep.zip")
            || file.getName().endsWith(".bheprep.zip")
            || file.getName().endsWith(".heprep.gz")
            || file.getName().endsWith(".bheprep.gz")
            || file.getName().endsWith(".heprep.xml")
            || file.getName().endsWith(".heprep")
            || file.getName().endsWith(".bheprep")
            || file.getName().endsWith(".heprep1")
            || file.getName().endsWith(".heprep2");
  }

  public void openFile(File file) throws IOException {
    
    FreeHEPLookup registry = getApplication().getLookup();
    String filename = file.getName().toLowerCase();

    if (filename.endsWith(".heprep1.zip") || filename.endsWith(".heprep1.gz")) {
      HepRepReader reader = new HepRep1Reader(file);
      RecordSource heprepSource = new HepRepSource(reader, file.getName());
      registry.add(heprepSource);
      return;
    }

    if (filename.endsWith(".heprep2.zip") || filename.endsWith(".bheprep.zip") ||
        filename.endsWith(".heprep2.gz") || filename.endsWith(".bheprep.gz")) {
      XMLHepRepFactory factory = new XMLHepRepFactory();
      HepRepReader reader = factory.createHepRepReader(file.getPath());
      RecordSource heprepSource = new HepRepSource(reader, file.getName());
      registry.add(heprepSource);
      return;
    }

    if (filename.endsWith(".zip") || filename.endsWith(".gz")) {
      try {
        // HepRep2 ?
        XMLHepRepFactory factory = new XMLHepRepFactory();
        HepRepReader reader = factory.createHepRepReader(file.getPath());
        RecordSource heprepSource = new HepRepSource(reader, file.getName());
        registry.add(heprepSource);
        return;
      } catch (IOException e) {
        if ((e.getCause() instanceof SAXException)
                && (((SAXException) e.getCause()).getException() instanceof HepRepVersionException)) {
          // HepRep1 ?
          HepRepReader reader = new HepRep1Reader(file);
          RecordSource heprepSource = new HepRepSource(reader, file.getName());
          registry.add(heprepSource);
          return;
        } else {
          throw e;
        }
      }
    }

    // single event
    HepRep heprep = null;
    if (filename.endsWith(".heprep1")) {
      heprep = readHepRep1(file);
    } else if (filename.endsWith(".heprep2") || filename.endsWith(".bheprep")) {
      heprep = readHepRep2(file);
    } else {
      try {
        // HepRep2 ?
        heprep = readHepRep2(file);
      } catch (IOException e) {
        if ((e.getCause() instanceof SAXException)
                && (((SAXException) e.getCause()).getException() instanceof HepRepVersionException)) {
          // HepRep1 ?
          heprep = readHepRep1(file);
        } else {
          throw e;
        }
      }
    }

    // set the name as a property
    // FIXME: update when HEPREP-13 is implemented
    if (heprep instanceof DefaultHepRep) {
      ((DefaultHepRep) heprep).setProperty("transient.filename", file.getName());
      ((DefaultHepRep) heprep).setProperty("transient.path", file.getPath());
    }

    // Find a HepRepViewer, and display
    Lookup.Result result = registry.lookup(new Lookup.Template(HepRepViewer.class));
    Collection items = result.allItems();
    if (items.isEmpty()) {
      getApplication().error("A suitable HepRepViewer cannot be found. Please install one, for instance WIRED 4");
    } else {
      String preferredViewer = getPreferredViewer();
      for (Iterator iterator = items.iterator(); iterator.hasNext();) {
        Lookup.Item item = (Lookup.Item) iterator.next();
        if (preferredViewer.equals(getViewerName(item)) || !iterator.hasNext()) {
          HepRepViewer viewer = (HepRepViewer) item.getInstance();
          viewer.display(heprep);
          break;
        }
      }
    }
  }

  String getViewerName(Lookup.Item item) {
    // Ideally the viewers would all register as lookup.add(me,"MyName"), but since the currently
    // don't we have to "guess" the names here.
    String name = item.getDisplayName();
    if (item.getType().getName().startsWith("ch.cern")) {
      name = "WIRED3";
    }
    if (item.getType().getName().startsWith("hep.wired")) {
      name = "WIRED4";
    }
    return name;
  }

  private HepRep readHepRep1(File file) throws IOException {
    hep.graphics.heprep1.xml.XMLHepRepReader reader = new hep.graphics.heprep1.xml.XMLHepRepReader(new FileInputStream(file));
    hep.graphics.heprep1.HepRep heprep1 = reader.next();
    reader.close();
    HepRepConverter converter = new HepRepConverter();
    return converter.convert(heprep1);
  }

  private HepRep readHepRep2(File file) throws IOException {
    XMLHepRepFactory factory = new XMLHepRepFactory();
    InputStream in = new FileInputStream(file);
    if (file.getName().toLowerCase().indexOf(".bheprep") >= 0) {
      in = new DataInputStream(in);
    }
    HepRepReader reader = factory.createHepRepReader(in);
    HepRep heprep = reader.next();
    reader.close();
    return heprep;
  }

  public boolean apply(JComponent panel) {
    HepRepPreferencesPanel prefs = (HepRepPreferencesPanel) panel;
    return prefs.apply(this);
  }

  public javax.swing.JComponent component() {
    return new HepRepPreferencesPanel(this);
  }

  public String[] path() {
    return new String[]{"HepRep"};
  }


// -----------------------------------------------------------------------------
  
}
