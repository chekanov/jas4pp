package org.freehep.jas.extension.tupleExplorer.mutableTuple;

import hep.aida.ref.tuple.FTuple;
import hep.aida.ref.tuple.HasFTuple;
import hep.aida.ref.tuple.FTupleColumn;
import hep.aida.ref.tuple.FTupleCursor;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.util.Value;

/**
 *
 * @author  The FreeHEP team @ SLAC.
 *
 */
public class MutableTupleColumn implements FTupleColumn {
    
    private FTupleColumn column = null;
    private String origName;
    private String newName = null;
    private MutableTuple tuple;
    private int columnIndex = -1;
    
    private double min  = Double.NaN;
    private double max  = Double.NaN;
    private double mean = Double.NaN;
    private double rms  = Double.NaN;
    private boolean limitsCalculated = false;
    private boolean calculateLimits;
    private Value value = new Value();
    
    protected MutableTupleColumn(FTupleColumn column, MutableTuple tuple) {
        this(column.name(), tuple, false);
        this.column = column;
    }
    
    public MutableTupleColumn(String name, MutableTuple tuple) {
        this( name, tuple, true );
    }
    
    private MutableTupleColumn(String name, MutableTuple tuple, boolean calculateLimits) {
        this.tuple = tuple;
        this.origName = name;
        this.calculateLimits = calculateLimits;
    }

    public String name() {
        if ( newName != null ) return newName;
        return origName;
    }
    
    public boolean isFolder() {
        return FTuple.class.isAssignableFrom(type()) || HasFTuple.class.isAssignableFrom(column.getClass());
    }
    
    public MutableTuple parent() {
        return tuple;
    }
        
    public Class type() {
        if ( column != null ) {
            Class type = column.type();
            if ( FTuple.class.isAssignableFrom(type) ) return MutableTuple.class;
            return type;
        }
        throw new RuntimeException("This method cannot be invoked for this column "+this);
    }
    
    public void value( FTupleCursor cursor, Value value ) {
        if ( column != null ) {
            if ( columnIndex < 0 ) columnIndex = tuple.columnIndexByName( column.name() );
            tuple.columnValue( columnIndex, cursor, value );
        }
        else
            throw new RuntimeException("This method cannot be invoked for this column "+this);
    }
    
    public boolean limitsCalculated() {
        return limitsCalculated;
    }
    
    public FTreePath treePath() {
        return tuple.treePath().pathByAddingChild( name() );
    }
    
    public void minValue(Value value) {
        if ( column != null )
            column.minValue( value );
        if ( calculateLimits || Double.isNaN( value.getDouble() ) ) {
            if ( ! limitsCalculated ) {
                limitsCalculated = true;
                calculateLimits();
            }
            value.set(min);
        }
    }
    
    public void maxValue(Value value) {
        if ( column != null )
            column.maxValue( value );
        if ( calculateLimits || Double.isNaN( value.getDouble() ) ) {
            if ( ! limitsCalculated ) {
                limitsCalculated = true;
                calculateLimits();
            }
            value.set(max);
        }
    }
    
    public void meanValue(Value value) {
        if ( column != null )
            column.meanValue( value );
        if ( calculateLimits || Double.isNaN( value.getDouble() ) ) {
            if ( ! limitsCalculated ) {
                limitsCalculated = true;
                calculateLimits();
            }
            value.set(mean);
        }
    }
    
    public void rmsValue(Value value) {
        if ( column != null )
            column.rmsValue( value );
        if ( calculateLimits || Double.isNaN( value.getDouble() ) ) {
            if ( ! limitsCalculated ) {
                limitsCalculated = true;
                calculateLimits();
            }
            value.set(rms);
        }
    }
    
    public void setName(String name) {
        try {
            int index = tuple.columnIndexByName( name );
            throw new IllegalArgumentException("Column with name "+name+" already exists");
        } catch ( IllegalArgumentException iae ) {
            this.newName = name;
            tuple.changedMutableTupleColumn( this );
        }
    }
    
    public void defaultValue(Value value) {
        if ( column != null )
            column.defaultValue(value);
        else
            throw new RuntimeException("This method cannot be invoked for this column "+this);
    }
    
    public boolean hasDefaultValue() {
        if ( column != null )
            return column.hasDefaultValue();
        else
            throw new RuntimeException("This method cannot be invoked for this column "+this);
    }

    public void enableColumnsInCursor( MutableTupleTreeNavigator cursor ) {
        cursor.enablePath( tuple.treePath() );
    }

    public boolean isCompatible(MutableTupleColumn mutableTupleColumn) {
        if ( (treePath().getParentPath().isDescendant( mutableTupleColumn.treePath().getParentPath())) ||
            ( mutableTupleColumn.treePath().getParentPath().isDescendant(treePath().getParentPath())) )  return true;
            else return false;
    }

    public boolean cursorAdvanced( MutableTupleTreeNavigator cursor ) {
        return cursor.advanced(tuple.treePath());
    }

    
    public void calculateLimits() {
        MutableTupleTreeNavigator cursor = tuple.treeCursor();
        cursor.disableAllChild();
        enableColumnsInCursor(cursor);
        
        cursor.start();
        String pathName = tuple.treePath().toString();
        while ( cursor.next() ) {
            if ( cursorAdvanced(cursor) ) {
                value( cursor.cursorForPath( pathName ), value );
                double d = value.getDouble();
                if (Double.isNaN(min) || d < min) min = d;
                if (Double.isNaN(max) || d > max) max = d;
            }
        }
    }
}
