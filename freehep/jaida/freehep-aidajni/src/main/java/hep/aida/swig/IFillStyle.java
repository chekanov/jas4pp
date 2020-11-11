/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.31
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package hep.aida.swig;
 
import hep.aida.jni.AIDAJNIUtil;

public class IFillStyle extends IBrushStyle implements hep.aida.IFillStyle {
  private long swigCPtr;

  public IFillStyle(long cPtr, boolean cMemoryOwn) {
    super(AIDAJNI.SWIGIFillStyleUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(hep.aida.IFillStyle obj) {
    if (obj instanceof IFillStyle) {
      return (obj == null) ? 0 : ((IFillStyle)obj).swigCPtr;
    } else {
      long cPtr = AIDAJNI.new_IFillStyle();
      // FIXME, memory leak if Java class gets finalized, since C++ director is not freed.
      AIDAJNI.IFillStyle_director_connect(obj, cPtr, true, true);
      return cPtr;
    }
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      AIDAJNI.delete_IFillStyle(swigCPtr);
    }
    swigCPtr = 0;
    super.delete();
  }

  protected void swigDirectorDisconnect() {
    swigCMemOwn = false;
    delete();
  }

  public void swigReleaseOwnership() {
    swigCMemOwn = false;
    AIDAJNI.IFillStyle_change_ownership(this, swigCPtr, false);
  }

  public void swigTakeOwnership() {
    swigCMemOwn = true;
    AIDAJNI.IFillStyle_change_ownership(this, swigCPtr, true);
  }

  public String[] availablePatterns() {
    return AIDAJNIUtil.toStringArray(AIDAJNI.IFillStyle_availablePatterns(swigCPtr, this));
  }

  public String pattern() {
    return AIDAJNI.IFillStyle_pattern(swigCPtr, this);
  }

  public boolean setPattern(String pattern) {
    return AIDAJNI.IFillStyle_setPattern(swigCPtr, this, pattern);
  }

  public IFillStyle() {
    this(AIDAJNI.new_IFillStyle(), true);
    AIDAJNI.IFillStyle_director_connect(this, swigCPtr, swigCMemOwn, true);
  }

}
