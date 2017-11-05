// IApiSampleService.aidl
package com.itc.control;

// Declare any non-default types here with import statements

interface IApiSampleService {

    /**
     * Try connection to ItcSample System Service
     */
    void callApiSampleFunc(String str);

    /**
     * Test access to MemoryFile (shared mmemory)
     */
    void callUpdateDisplayFunc(String str);

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
