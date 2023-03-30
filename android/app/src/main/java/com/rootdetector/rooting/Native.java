package com.rootdetector.rooting;

class Native {
    static {
        System.loadLibrary("native-lib");
    }

    static native boolean isMagiskPresentNative();
}
