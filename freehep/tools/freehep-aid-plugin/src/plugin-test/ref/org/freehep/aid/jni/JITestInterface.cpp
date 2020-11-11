// AID-GENERATED
// =========================================================================
// This class was generated by AID - Abstract Interface Definition          
// DO NOT MODIFY, but use the org.freehep.aid.Aid utility to regenerate it. 
// =========================================================================

#include <cstdlib>
#include <iostream>

#include "AID/JAIDRef.h"
#include "JITestInterface.h"

using namespace AIDTEST;


JITestInterface::JITestInterface(JNIEnv *env, jobject object)
        : JAIDRef(env, object) {
    jclass cls = env->GetObjectClass(ref);

    noargsOCVMethod = env->GetMethodID(cls, "noargs", "()V");
    if (noargsOCVMethod == NULL) {
        std::cerr << "ITestInterface" << ": Could not find method: " << "noargs" << "()V" << std::endl;
    }

    commitOCVMethod = env->GetMethodID(cls, "commit", "()V");
    if (commitOCVMethod == NULL) {
        std::cerr << "ITestInterface" << ": Could not find method: " << "commit" << "()V" << std::endl;
    }

}

JITestInterface::~JITestInterface() {
}

void JITestInterface::noargs() {
    // Call to Java
    env->CallVoidMethod(ref, noargsOCVMethod);
}

bool JITestInterface::commit() {
    // Call to Java
    env->CallVoidMethod(ref, commitOCVMethod);
    jthrowable e = env->ExceptionOccurred();
    env->ExceptionClear();
    return (e != NULL) ? false : true;
}
