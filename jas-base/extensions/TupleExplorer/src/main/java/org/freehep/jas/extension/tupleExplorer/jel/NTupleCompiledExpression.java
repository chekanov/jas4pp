package org.freehep.jas.extension.tupleExplorer.jel;

import gnu.jel.CompiledExpression;
import java.util.Vector;

/**
 *
 * @author serbo
 * @version $Id:
 */
public class NTupleCompiledExpression {
    
    private CompiledExpression ce;
    private Vector columns;
    
    public NTupleCompiledExpression(CompiledExpression cexpr, Vector cols) {
        ce = cexpr;
        columns = cols;
    }
    
    public Vector getColumns() { return columns; }
    public CompiledExpression getCompiledExpression() { return ce; }
    /*
    public int getType() { return ce.getType(); }
     
    public Object  evaluate        (Object[] dl) throws Throwable {  return ce.evaluate(dl); }
    public boolean evaluate_boolean(Object[] dl) throws Throwable {  return ce.evaluate_boolean(dl); }
    public byte    evaluate_byte   (Object[] dl) throws Throwable {  return ce.evaluate_byte(dl); }
    public short   evaluate_short  (Object[] dl) throws Throwable {  return ce.evaluate_short(dl); }
    public char    evaluate_char   (Object[] dl) throws Throwable {  return ce.evaluate_char(dl); }
    public int     evaluate_int    (Object[] dl) throws Throwable {  return ce.evaluate_int(dl); }
    public long    evaluate_long   (Object[] dl) throws Throwable {  return ce.evaluate_long(dl); }
    public float   evaluate_float  (Object[] dl) throws Throwable {  return ce.evaluate_float(dl); }
    public double  evaluate_double (Object[] dl) throws Throwable {  return ce.evaluate_double(dl); }
     */
    //public static int compare(String s1,String s2) {
    
}
