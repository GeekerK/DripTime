package com.geekerk.driptime.db.natived;

/**
 * Created with Android Studio IDEA
 * Author：LiYan
 * Date：2016/6/18
 * Time：13:25
 */
public final class JNIManager {
    static {
        System.loadLibrary("Embeded");
    }

    private static JNIManager sJNIManager;

    private JNIManager(){};

    public static JNIManager getInstance() {
        if (sJNIManager == null) {
            sJNIManager = new JNIManager();
        }
        return sJNIManager;
    }

    public native Object getEmbededResult(String sql);
}
