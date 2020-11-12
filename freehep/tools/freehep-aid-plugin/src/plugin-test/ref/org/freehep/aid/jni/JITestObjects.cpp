// AID-GENERATED
// =========================================================================
// This class was generated by AID - Abstract Interface Definition          
// DO NOT MODIFY, but use the org.freehep.aid.Aid utility to regenerate it. 
// =========================================================================

#include <cstdlib>
#include <iostream>
#include <string>
#include <vector>

#include "AID/JAIDRef.h"
#include "JITestObjects.h"

using namespace AIDTEST;


JITestObjects::JITestObjects(JNIEnv *env, jobject object)
        : JAIDRef(env, object) {
    jclass cls = env->GetObjectClass(ref);

    returnObjectOCLjava_lang_StringEMethod = env->GetMethodID(cls, "returnObject", "()Ljava/lang/String;");
    if (returnObjectOCLjava_lang_StringEMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "returnObject" << "()Ljava/lang/String;" << std::endl;
    }

    returnReferenceOCLjava_lang_StringEMethod = env->GetMethodID(cls, "returnReference", "()Ljava/lang/String;");
    if (returnReferenceOCLjava_lang_StringEMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "returnReference" << "()Ljava/lang/String;" << std::endl;
    }

    returnPointerOCLjava_lang_StringEMethod = env->GetMethodID(cls, "returnPointer", "()Ljava/lang/String;");
    if (returnPointerOCLjava_lang_StringEMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "returnPointer" << "()Ljava/lang/String;" << std::endl;
    }

    returnColorOCLjava_awt_ColorEMethod = env->GetMethodID(cls, "returnColor", "()Ljava/awt/Color;");
    if (returnColorOCLjava_awt_ColorEMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "returnColor" << "()Ljava/awt/Color;" << std::endl;
    }

    returnConstReferenceOCLjava_lang_StringEMethod = env->GetMethodID(cls, "returnConstReference", "()Ljava/lang/String;");
    if (returnConstReferenceOCLjava_lang_StringEMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "returnConstReference" << "()Ljava/lang/String;" << std::endl;
    }

    returnConstReferenceConstOCLjava_lang_StringEMethod = env->GetMethodID(cls, "returnConstReferenceConst", "()Ljava/lang/String;");
    if (returnConstReferenceConstOCLjava_lang_StringEMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "returnConstReferenceConst" << "()Ljava/lang/String;" << std::endl;
    }

    returnConstReferenceThrowsOCLjava_lang_StringEMethod = env->GetMethodID(cls, "returnConstReferenceThrows", "()Ljava/lang/String;");
    if (returnConstReferenceThrowsOCLjava_lang_StringEMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "returnConstReferenceThrows" << "()Ljava/lang/String;" << std::endl;
    }

    returnConstReferenceConstThrowsOCLjava_lang_StringEMethod = env->GetMethodID(cls, "returnConstReferenceConstThrows", "()Ljava/lang/String;");
    if (returnConstReferenceConstThrowsOCLjava_lang_StringEMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "returnConstReferenceConstThrows" << "()Ljava/lang/String;" << std::endl;
    }

    findOLjava_lang_StringECLjava_lang_StringEMethod = env->GetMethodID(cls, "find", "(Ljava/lang/String;)Ljava/lang/String;");
    if (findOLjava_lang_StringECLjava_lang_StringEMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "find" << "(Ljava/lang/String;)Ljava/lang/String;" << std::endl;
    }

    findOLjava_awt_ColorECLjava_lang_StringEMethod = env->GetMethodID(cls, "find", "(Ljava/awt/Color;)Ljava/lang/String;");
    if (findOLjava_awt_ColorECLjava_lang_StringEMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "find" << "(Ljava/awt/Color;)Ljava/lang/String;" << std::endl;
    }

    cdOLjava_lang_StringECZMethod = env->GetMethodID(cls, "cd", "(Ljava/lang/String;)Z");
    if (cdOLjava_lang_StringECZMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "cd" << "(Ljava/lang/String;)Z" << std::endl;
    }

    lsOLjava_lang_StringEZLjava_io_OutputStreamECVMethod = env->GetMethodID(cls, "ls", "(Ljava/lang/String;ZLjava/io/OutputStream;)V");
    if (lsOLjava_lang_StringEZLjava_io_OutputStreamECVMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "ls" << "(Ljava/lang/String;ZLjava/io/OutputStream;)V" << std::endl;
    }

    mkdirOLjava_lang_StringECVMethod = env->GetMethodID(cls, "mkdir", "(Ljava/lang/String;)V");
    if (mkdirOLjava_lang_StringECVMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "mkdir" << "(Ljava/lang/String;)V" << std::endl;
    }

    mvOLjava_lang_StringELjava_lang_StringECVMethod = env->GetMethodID(cls, "mv", "(Ljava/lang/String;Ljava/lang/String;)V");
    if (mvOLjava_lang_StringELjava_lang_StringECVMethod == NULL) {
        std::cerr << "ITestObjects" << ": Could not find method: " << "mv" << "(Ljava/lang/String;Ljava/lang/String;)V" << std::endl;
    }

}

JITestObjects::~JITestObjects() {
}

std::string JITestObjects::returnObject() {
    std::string result;
    // Call to Java
    jstring jniResult = (jstring)env->CallObjectMethod(ref, returnObjectOCLjava_lang_StringEMethod);
    // converting String to string
    jboolean isCopy1;
    result = env->GetStringUTFChars(jniResult, &isCopy1);
    return result;
}

std::string & JITestObjects::returnReference() {
    std::string result;
    // Call to Java
    jstring jniResult = (jstring)env->CallObjectMethod(ref, returnReferenceOCLjava_lang_StringEMethod);
    // converting String to string
    jboolean isCopy1;
    result = env->GetStringUTFChars(jniResult, &isCopy1);
    // copying into instance variable
    const_cast<JITestObjects*>(this) -> returnReferenceOCLjava_lang_StringEResult = result;
    return returnReferenceOCLjava_lang_StringEResult;
}

std::string * JITestObjects::returnPointer() {
    std::string result;
    // Call to Java
    jstring jniResult = (jstring)env->CallObjectMethod(ref, returnPointerOCLjava_lang_StringEMethod);
    // converting String to string
    jboolean isCopy1;
    result = env->GetStringUTFChars(jniResult, &isCopy1);
    // copying into instance variable
    const_cast<JITestObjects*>(this) -> returnPointerOCLjava_lang_StringEResult = result;
    return &returnPointerOCLjava_lang_StringEResult;
}

std::vector<double> JITestObjects::returnColor() {
    std::vector<double> result;
    // Call to Java
    jobject jniResult = (jobject)env->CallObjectMethod(ref, returnColorOCLjava_awt_ColorEMethod);
    // convert Color to vector<double>
    jclass colorClass1 = env->GetObjectClass(jniResult);
    jmethodID getRGBComponentsMethod1 = env->GetMethodID(colorClass1, "getRGBComponents", "([F)[F");
    jfloatArray o1 = (jfloatArray)env->CallObjectMethod(jniResult, getRGBComponentsMethod1, NULL);
    float* c1 = env->GetFloatArrayElements(o1, NULL);
    result.push_back(*c1++);
    result.push_back(*c1++);
    result.push_back(*c1++);
    result.push_back(*c1++);
    return result;
}

const std::string & JITestObjects::returnConstReference() {
    std::string result;
    // Call to Java
    jstring jniResult = (jstring)env->CallObjectMethod(ref, returnConstReferenceOCLjava_lang_StringEMethod);
    // converting String to string
    jboolean isCopy1;
    result = env->GetStringUTFChars(jniResult, &isCopy1);
    // copying into instance variable
    const_cast<JITestObjects*>(this) -> returnConstReferenceOCLjava_lang_StringEResult = result;
    return returnConstReferenceOCLjava_lang_StringEResult;
}

const std::string & JITestObjects::returnConstReferenceConst() {
    std::string result;
    // Call to Java
    jstring jniResult = (jstring)env->CallObjectMethod(ref, returnConstReferenceConstOCLjava_lang_StringEMethod);
    // converting String to string
    jboolean isCopy1;
    result = env->GetStringUTFChars(jniResult, &isCopy1);
    // copying into instance variable
    const_cast<JITestObjects*>(this) -> returnConstReferenceConstOCLjava_lang_StringEResult = result;
    return returnConstReferenceConstOCLjava_lang_StringEResult;
}

const std::string & JITestObjects::returnConstReferenceThrows() {
    std::string result;
    // Call to Java
    jstring jniResult = (jstring)env->CallObjectMethod(ref, returnConstReferenceThrowsOCLjava_lang_StringEMethod);
    // converting String to string
    jboolean isCopy1;
    result = env->GetStringUTFChars(jniResult, &isCopy1);
    // copying into instance variable
    const_cast<JITestObjects*>(this) -> returnConstReferenceThrowsOCLjava_lang_StringEResult = result;
    return returnConstReferenceThrowsOCLjava_lang_StringEResult;
}

const std::string & JITestObjects::returnConstReferenceConstThrows() {
    std::string result;
    // Call to Java
    jstring jniResult = (jstring)env->CallObjectMethod(ref, returnConstReferenceConstThrowsOCLjava_lang_StringEMethod);
    // converting String to string
    jboolean isCopy1;
    result = env->GetStringUTFChars(jniResult, &isCopy1);
    // copying into instance variable
    const_cast<JITestObjects*>(this) -> returnConstReferenceConstThrowsOCLjava_lang_StringEResult = result;
    return returnConstReferenceConstThrowsOCLjava_lang_StringEResult;
}

std::string * JITestObjects::find(const std::string & path) {
    std::string result;
    jstring jnipath;
    // converting string to String
    jnipath = env->NewStringUTF(path.c_str());
    // Call to Java
    jstring jniResult = (jstring)env->CallObjectMethod(ref, findOLjava_lang_StringECLjava_lang_StringEMethod, jnipath);
    // converting String to string
    jboolean isCopy1;
    result = env->GetStringUTFChars(jniResult, &isCopy1);
    // copying into instance variable
    const_cast<JITestObjects*>(this) -> findOLjava_lang_StringECLjava_lang_StringEResult = result;
    return &findOLjava_lang_StringECLjava_lang_StringEResult;
}

std::string * JITestObjects::find(std::vector<double> color) {
    std::string result;
    jobject jnicolor;
    // convert vector<double> to Color
    jfloat alpha1 = color[0];
    jfloat red1 = color[1];
    jfloat green1 = color[2];
    jfloat blue1 = color[3];
    jclass colorClass1 = env->FindClass("java.awt.Color");
    jmethodID constructor1 = env->GetMethodID(colorClass1, "<init>", "(FFFF)V");
    jnicolor = env->NewObject(colorClass1, constructor1, red1, green1, blue1, alpha1);
    // Call to Java
    jstring jniResult = (jstring)env->CallObjectMethod(ref, findOLjava_awt_ColorECLjava_lang_StringEMethod, jnicolor);
    // converting String to string
    jboolean isCopy1;
    result = env->GetStringUTFChars(jniResult, &isCopy1);
    // copying into instance variable
    const_cast<JITestObjects*>(this) -> findOLjava_awt_ColorECLjava_lang_StringEResult = result;
    return &findOLjava_awt_ColorECLjava_lang_StringEResult;
}

bool JITestObjects::cd(const std::string & path) {
    bool result;
    jstring jnipath;
    // converting string to String
    jnipath = env->NewStringUTF(path.c_str());
    // Call to Java
    jboolean jniResult = (jboolean)env->CallBooleanMethod(ref, cdOLjava_lang_StringECZMethod, jnipath);
    result = jniResult;
    return result;
}

void JITestObjects::ls(const std::string & path, bool recursive, std::ostream & os) {
    jstring jnipath;
    // converting string to String
    jnipath = env->NewStringUTF(path.c_str());
    jboolean jnirecursive;
    jnirecursive = recursive;
    jobject jnios;
// WARNING no conversion for OutputStream&
    // Call to Java
    env->CallVoidMethod(ref, lsOLjava_lang_StringEZLjava_io_OutputStreamECVMethod, jnipath, jnirecursive, jnios);
}

bool JITestObjects::mkdir(const std::string & path) {
    jstring jnipath;
    // converting string to String
    jnipath = env->NewStringUTF(path.c_str());
    // Call to Java
    env->CallVoidMethod(ref, mkdirOLjava_lang_StringECVMethod, jnipath);
    jthrowable e = env->ExceptionOccurred();
    env->ExceptionClear();
    return (e != NULL) ? false : true;
}

void JITestObjects::mv(const std::string & oldPath, const std::string & newPath) {
    jstring jnioldPath;
    // converting string to String
    jnioldPath = env->NewStringUTF(oldPath.c_str());
    jstring jninewPath;
    // converting string to String
    jninewPath = env->NewStringUTF(newPath.c_str());
    // Call to Java
    env->CallVoidMethod(ref, mvOLjava_lang_StringELjava_lang_StringECVMethod, jnioldPath, jninewPath);
}