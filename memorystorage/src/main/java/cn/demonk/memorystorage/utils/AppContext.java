package cn.demonk.memorystorage.utils;

import android.content.Context;

public class AppContext {
    static Context context;

    public static void init(Context ctx) {
        context = ctx;
    }

    public static Context appContext() {
        return context;
    }
}
