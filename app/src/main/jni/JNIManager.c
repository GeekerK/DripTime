#include "com_geekerk_driptime_db_natived_JNIManager.h"
#include <stdio.h>

extern char* socket_core(char* buffer);
/*
 * Class:     com_geekerk_driptime_db_natived_JNIManager
 * Method:    getEmbededResult
 * Signature: (Ljava/lang/String;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_geekerk_driptime_db_natived_JNIManager_getEmbededResult
  (JNIEnv *env, jobject thiz, jstring string)
  {
        const char *sql = (*env)->GetStringUTFChars(env, string, NULL);
        char *result = socket_core(sql);
        jstring resultObject = (*env)->NewStringUTF(env, result);
        (*env)->ReleaseStringUTFChars(env, string, sql);
        return resultObject;
  }
