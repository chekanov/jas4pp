package org.freehep.jas.extensions.heprep;

import java.io.IOException;
import java.util.*;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepReader;
import java.io.File;

/**
 * Wrapper for HepRep1 reader that implements HepRep2 reader.
 *
 * @author Dmitry Onoprienko
 * @version $Id: $
 */
public final class HepRep1Reader implements HepRepReader {

// -- Private parts : ----------------------------------------------------------
  
  hep.graphics.heprep1.HepRepReader _reader;

// -- Construction and initialization : ----------------------------------------
  
  public HepRep1Reader(File file) throws IOException {
    _reader = new hep.graphics.heprep1.xml.XMLHepRepReader(file.getPath());
  }
  
  public HepRep1Reader(String fileName) throws IOException {
    _reader = new hep.graphics.heprep1.xml.XMLHepRepReader(fileName);
  }
  
  public HepRep1Reader(hep.graphics.heprep1.HepRepReader reader) {
    _reader = reader;
  }

// -- Implementing HepRep2 reader : --------------------------------------------

  public void close() throws IOException {
    _reader.close();
  }

  public String entryName() {
    return _reader.entryName();
  }

  public List entryNames() {
    return _reader.entryNames();
  }

  public String getProperty(String string, String string1) throws IOException {
    throw new UnsupportedOperationException();
  }

  public boolean hasNext() throws IOException, UnsupportedOperationException {
    return _reader.hasNext();
  }

  public boolean hasRandomAccess() throws IOException {
    return _reader.hasRandomAccess();
  }

  public boolean hasSequentialAccess() throws IOException {
    return _reader.hasSequentialAccess();
  }

  public HepRep next() throws IOException, UnsupportedOperationException, NoSuchElementException {
    hep.graphics.heprep1.HepRep heprep1 = _reader.next();
    HepRepConverter converter = new HepRepConverter();
    return converter.convert(heprep1);
  }

  public HepRep read(String string) throws IOException, UnsupportedOperationException, NoSuchElementException {
    hep.graphics.heprep1.HepRep heprep1 = _reader.read(string);
    HepRepConverter converter = new HepRepConverter();
    return converter.convert(heprep1);
  }

  public void reset() throws IOException, UnsupportedOperationException {
    _reader.reset();
  }

  public int size() {
    return _reader.size();
  }

  public int skip(int i) throws UnsupportedOperationException {
    return _reader.skip(i);
  }

// -----------------------------------------------------------------------------
}
