package org.freehep.jas.extensions.recordloop;

import java.io.IOException;

import hep.aida.ITuple;
import java.util.ConcurrentModificationException;
import org.freehep.record.source.AbstractRecordSource;
import org.freehep.record.source.NoSuchRecordException;
import org.freehep.record.source.RecordSource;

/**
 * Wrapper for {@link ITuple} that implements {@link RecordSource}.
 * The tuple should not be modified while records are being read from this source.
 * If the tuple is modified, <tt>rewind()</tt> needs to be called on this source before 
 * it can be used again.
 * 
 * @author tonyj
 * @author Dmitry Onoprienko
 */
class ITupleRecordSource extends AbstractRecordSource {
  
// -- Private parts : ----------------------------------------------------------
  
  private final ITuple _tuple;
  private int _row;
  private int _size;
  

// -- Construction : -----------------------------------------------------------

  public ITupleRecordSource(ITuple tuple) {
    _tuple = tuple;
    _row = -1;
    _size = _tuple.rows();
    tuple.start();
  }
  
  
// -- Source parameters : ------------------------------------------------------

  /** Returns the name of this source. */
  public String getName() {
    return _tuple.title();
  }

  /** 
   * Returns the <tt>Class</tt> object that is guaranteed to be a superclass of all records in this source.
   * <p>
   * Implemented to return <tt>ITuple.class</tt>.
   */
  public Class<?> getRecordClass() {
    return ITuple.class;
  }

  /**
   * Returns the number of records in this source.
   */
  public long size() {
    return _size;
  }

  
// -- Current record data : ----------------------------------------------------
  
  /**
   * Returns the index of the current record.
   */
  public long getCurrentIndex() {
    return _row;
  }
 
  /**
   * Returns {@link ITuple} with cursor pointing to the current record.
   */
  public ITuple getCurrentRecord() throws IOException {
    if (_row == -1) throw new IllegalStateException();
    return _tuple;
  }

// -- Checking whether the specified record selection method is supported : ----
  /**
   * Returns <tt>true</tt> if this source supports loading next record. 
   */
  public boolean supportsNext() {return true;}
  
  /** 
   * Returns <tt>true</tt> if this source supports loading previous record. 
   */
  public boolean supportsPrevious() {return true;}
  
  /**
   * Returns <tt>true</tt> if this source supports selecting records by index.
   */
  public boolean supportsIndex() {return true;}
  
  /** 
   * Returns <tt>true</tt> if this source supports selecting records by offset with respect to the current record.
   */
  public boolean supportsShift() {return true;}

  
// -- Checking whether the specified record exist : ----------------------------

  /** 
   * Returns <tt>true</tt> if this source can load the next record.
   */
  public boolean hasNext() {
    return _row < _size-1;
  }

  /** 
   * Returns <tt>true</tt> if this source can load the previous record.
   */
  public boolean hasPrevious() {
    return _row > 0;
  }

  /** 
   * Returns <tt>true</tt> if this source has a record with the specified index.
   */
  public boolean hasIndex(long index) {
    return index >= 0L && index < _size;
  }

  /** 
   * Returns <tt>true</tt> if this source can shift <tt>numberOfRecords</tt> records.
   */
  public boolean hasShift(long numberOfRecords) {
    return hasIndex(_row + numberOfRecords);
  }
  
  
// -- Loading the specified record : -------------------------------------------

  /**
   * Loads the next record.
   */
  public void next() throws IOException, NoSuchRecordException {
    if (_row == _size-1) throw new NoSuchRecordException();
    if (_tuple.next()) {
      _row++;
    } else {
      throw new IOException("ITuple has been modified, please rewind");
    }
  }

  /**
   * Loads the previous record.
   */
  public void previous() throws IOException, NoSuchRecordException {
    jump(_row-1);
  }

  /**
   * Loads the record specified by the index.
   */
  public void jump(long index) throws IOException, NoSuchRecordException {
    if (index < 0L || index >= _size) throw new NoSuchRecordException();
    try {
      int i = (int)index;
      _tuple.setRow(i);
      _row = i;
    } catch (IllegalArgumentException x) {
      throw new ConcurrentModificationException();
    }
  }

  /**
   * Loads the record specified by the offset with respect to the current cursor position. 
   */
  public void shift(long numberOfRecords) throws IOException, NoSuchRecordException {
    try {
      if (numberOfRecords > 0L) {
        if (_row + numberOfRecords < _size) {
          int n = (int)numberOfRecords;
          _tuple.skip(n);
          _row += n;
        } else {
          throw new NoSuchRecordException();
        }
      } else if (numberOfRecords < 0L) {
        long index = _row + numberOfRecords;
        if (index < 0L || index >= _size) throw new NoSuchRecordException();
        int i = (int)index;
        _tuple.setRow(i);
        _row = i;
      }
    } catch (IllegalArgumentException x) {
      throw new ConcurrentModificationException();
    }
  }

  
// -- Rewinding the source : ---------------------------------------------------

  public boolean supportsRewind() {
    return true;
  }

  public boolean hasRewind() {
    return _row > -1;
  }
  
  /**
   * Positions the cursor of this source before the first record.
   */
  public void rewind() throws IOException {
    _tuple.start();
    _row = -1;
    _size = _tuple.rows();
  }
  
// -----------------------------------------------------------------------------  
}