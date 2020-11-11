// -*- C++ -*-
// AID-GENERATED
// =========================================================================
// This class was generated by AID - Abstract Interface Definition          
// DO NOT MODIFY, but use the org.freehep.aid.Aid utility to regenerate it. 
// =========================================================================
#ifndef AIDTEST_JITESTCONSTANTS_H
#define AIDTEST_JITESTCONSTANTS_H 1

// Copyright 2002, SLAC, Stanford University, U.S.A.
// AID - Compiler Test File

#include <jni.h>

#include "AIDTEST/ITestConstants.h"

namespace AIDTEST {

/**
 * TestConstants to test the aid compiler.
 *
 * @author Mark Donszelmann
 */
class JITestConstants: public virtual ITestConstants {

protected:
    inline JITestConstants() { };
    inline JITestConstants(const JITestConstants& r) { };
    inline JITestConstants& operator=(const JITestConstants&) { return *this; };

public: 
    /**
     * Default JNI Constructor
     */
    JITestConstants(JNIEnv *env, jobject object);

    /// Destructor.
    virtual ~JITestConstants();

    /**
     * Final comment
     */
}; // class
}; // namespace AIDTEST

/**
 * Really Final comment
 */
#endif /* ifndef AIDTEST_JITESTCONSTANTS_H */
