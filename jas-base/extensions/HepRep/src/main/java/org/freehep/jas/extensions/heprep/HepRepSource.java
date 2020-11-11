package org.freehep.jas.extensions.heprep;

import java.io.IOException;
import java.util.*;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepReader;
import org.freehep.record.source.AbstractRecordSource;
import org.freehep.record.source.DefaultRecordTag;
import org.freehep.record.source.NoSuchRecordException;
import org.freehep.record.source.RecordSource;
import org.freehep.record.source.RecordTag;

/**
 * Wrapper for {@link HepRepReader} that implements {@link RecordSource}.
 *
 * @author Dmitry Onoprienko
 * @version $Id: $
 */
public class HepRepSource  extends AbstractRecordSource {

// -- Private parts : ----------------------------------------------------------
  
  static protected int UNKNOWN = -2;
  
  protected HepRepReader _reader;
  protected HepRep _record;
  protected int _index;
  protected RecordTag _tag;

  
// -- Construction and initialization : ----------------------------------------
  
  public HepRepSource(HepRepReader reader, String name) throws IOException {
    super(name);
    _reader = reader;
    _index = -1;
    if (reader.hasSequentialAccess() && reader.hasNext()) {
      try {
        next();
        rewind();
      } catch (NoSuchRecordException x) {
        throw new IOException();
      }
    }
  }
 

// -- Implementing RecordSource : ----------------------------------------------
  
  public Class getRecordClass() {
    return HepRep.class;
  }

  public long getEstimatedSize() {
    int n = _reader.size();
    if (n == -1) throw new UnsupportedOperationException();
    return n;
  }

  public List<RecordTag> getTags() {
    final List entryNames = _reader.entryNames();
    if (entryNames == null) throw new UnsupportedOperationException();
    return new AbstractList() {
      public int size() {
        return entryNames.size();
      }
      public RecordTag get(int index) {
        return new DefaultRecordTag((String) entryNames.get(index));
      }
    };
  }

  public long getCurrentIndex() {
    if (_index == UNKNOWN) throw new IllegalStateException();
    return _index;
  }

  public RecordTag getCurrentTag() {
    if (_index == -1) throw new IllegalStateException();
    String name = _reader.entryName();
    if (name == null) throw new UnsupportedOperationException();
    return new DefaultRecordTag(name);
  }

  public Object getCurrentRecord() throws IOException {
    if (_index == -1) throw new IllegalStateException();
    if (_record == null) restore();
    return _record;
  }

  public void releaseRecord() {
    _record = null;
  }
  
  /** Returns <tt>true</tt> if this source supports reloading current record. */
  public boolean supportsCurrent() {
    return true;
  }
  
  /** Returns <tt>true</tt> if this source supports loading next record. */
  public boolean supportsNext() {
    try {
      return _reader.hasSequentialAccess();
    } catch (IOException x) {return false;}
  }
  
  /** Returns <tt>true</tt> if this source supports loading previous record. */
  public boolean supportsPrevious() {
    try {
      return _reader.hasSequentialAccess();
    } catch (IOException x) {return false;}
  }
  
  /** Returns <tt>true</tt> if this source supports selecting records by index. */
  public boolean supportsIndex() {
    try {
      return _reader.hasSequentialAccess();
    } catch (IOException x) {return false;}
  }
  
  /** Returns <tt>true</tt> if this source supports selecting records by tag. */
  public boolean supportsTag() {
    try {
      return _reader.hasRandomAccess();
    } catch (IOException x) {return false;}
  }
  
  /** Returns <tt>true</tt> if this source supports selecting records by offset with respect to the current record. */
  public boolean supportsShift() {
    try {
      return _reader.hasSequentialAccess();
    } catch (IOException x) {return false;}
  }

  public boolean hasCurrent() {
    return _index != -1;
  }

  public boolean hasNext() {
    try {
      return _reader.hasNext();
    } catch (IOException e) {
      return false;
    } catch (UnsupportedOperationException x) {
      return false;
    }
  }

  public boolean hasPrevious() {
    return _index > 0;
  }

  public boolean hasIndex(long index) {
    try {
      return _reader.hasSequentialAccess() && index >= 0L;
    } catch (IOException x) {
      return false;
    }
  }

  public boolean hasShift(long numberOfRecords) {
    try {
      return _reader.hasSequentialAccess() && 
             (numberOfRecords > 0L || (_index != UNKNOWN && (_index+numberOfRecords >= 0L)));
    } catch (IOException x) {
      return false;
    }
  }

  public boolean hasTag(RecordTag tag) {
    try {
      return _reader.hasRandomAccess();
    } catch (IOException x) {
      return false;
    }
  }

  public void current() throws IOException, NoSuchRecordException {
    if (_index != UNKNOWN) {
      jump(_index);
    } else if (_tag != null) {
      jump(_tag);
    } else {
      throw new NoSuchRecordException();
    }
  }

  public void next() throws IOException, NoSuchRecordException {
    if (_reader.hasNext()) {
      try {
        _record = _reader.next();
      } catch (NoSuchElementException x) {
        throw new NoSuchRecordException();
      }
      if (_index == UNKNOWN) {
        String name = _reader.entryName();
        _tag = name == null ? null : new DefaultRecordTag(name);
      } else {
        _index++;
        _tag = null;
      }
    } else {
      throw new NoSuchRecordException();
    }
  }

  public void previous() throws IOException, NoSuchRecordException {
    jump(_index - 1);
  }

  public void jump(long index) throws IOException, NoSuchRecordException {
    if (!_reader.hasSequentialAccess()) throw new UnsupportedOperationException();
    if (index < 0L || index > Integer.MAX_VALUE) throw new NoSuchRecordException();
    int old = _index;
    try {
      if (_index == UNKNOWN || index <= _index) rewind();
      int n = (int) index - (_index + 1);
      if (n > 0 && _reader.skip(n) != n) throw new NoSuchRecordException();
      try {
        _record = _reader.next();
        _index = (int)index;
        _tag = null;
      } catch (NoSuchElementException x) {
        throw new NoSuchRecordException();
      }
    } catch (NoSuchRecordException x) {
      _index = old;
      restore();
      throw x;
    }
  }

  public void shift(long index) throws IOException, NoSuchRecordException {
    if (index > 0L) {
      if (index > Integer.MAX_VALUE) throw new NoSuchRecordException();
      try {
        int n = (int)index - 1;
        if (n > 0 && _reader.skip(n) != n) throw new NoSuchRecordException();
        try {
          _record = _reader.next();
          if (_index == UNKNOWN) {
            String name = _reader.entryName();
            _tag = name == null ? null : new DefaultRecordTag(name);
          } else {
            _index+= (int)index;
            _tag = null;
          }
        } catch (NoSuchElementException x) {
          throw new NoSuchRecordException();
        }
      } catch (NoSuchRecordException x) {
        restore();
        throw x;
      }
    } else {
      jump(_index + index);
    }
  }

  public void jump(RecordTag tag) throws IOException, NoSuchRecordException {
    try {
      _record = _reader.read(tag.humanReadableName());
      _tag = new DefaultRecordTag(tag);
      _index = UNKNOWN;
    } catch (NoSuchElementException e) {
      throw new NoSuchRecordException();
    }
  }
  
  public boolean supportsRewind() {
    try {
      return _reader.hasSequentialAccess();
    } catch (IOException x) {return false;}
  }
  
  public boolean hasRewind() {
    return supportsRewind() && _index != -1;
  }

  public void rewind() throws IOException {
    _reader.reset();
    _index = -1;
    _tag = null;
    _record = null;
  }

  public void close() throws IOException {
    _reader.close();
    _record = null;
    _index = UNKNOWN;
    _tag = null;
  }
  
  
// -- Local methods : ----------------------------------------------------------
  
  protected void restore() throws IOException {
    if (_index == UNKNOWN) {
      if (_tag != null) {
        try {
          _record = _reader.read(_tag.humanReadableName());
        } catch (NoSuchElementException x) {
          _tag = null;
          throw new IOException();
        } catch (UnsupportedOperationException x) {
          _tag = null;
          throw new IOException();
        }
      }
    } else {
      if (_index >= 0) {
        _reader.reset();
        if (_index > 0 && _reader.skip(_index) != _index) throw new IOException();
        try {
          _record = _reader.next();
        } catch (NoSuchElementException x) {
          throw new IOException();
        }
      }
    }
  }

// -----------------------------------------------------------------------------
}
