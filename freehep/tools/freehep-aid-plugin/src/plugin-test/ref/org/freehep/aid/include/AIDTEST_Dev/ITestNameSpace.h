// -*- C++ -*-
// AID-GENERATED
// =========================================================================
// This class was generated by AID - Abstract Interface Definition          
// DO NOT MODIFY, but use the org.freehep.aid.Aid utility to regenerate it. 
// =========================================================================
#ifndef AIDTEST_DEV_ITESTNAMESPACE_H
#define AIDTEST_DEV_ITESTNAMESPACE_H 1

// Copyright 2002, SLAC, Stanford University, U.S.A.
// AID - Compiler Test File

#include "AIDTEST/ITestInterface.h"
#include "AIDTEST/ITestPrimitives.h"

namespace AIDTEST {
namespace Dev {

/**
 * TestInterface to test the aid compiler.
 *
 * @author Mark Donszelmann
 */
class ITestNameSpace : virtual public AIDTEST::ITestInterface {

public: 
    /// Destructor.
    virtual ~ITestNameSpace() { /* nop */; }

    virtual AIDTEST::ITestPrimitives returnPrimitives() = 0;

    virtual ITestNameSpace instance() = 0;
}; // class
} // namespace Dev
} // namespace AIDTEST
#endif /* ifndef AIDTEST_DEV_ITESTNAMESPACE_H */
