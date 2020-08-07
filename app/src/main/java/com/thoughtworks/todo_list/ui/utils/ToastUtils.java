package com.thoughtworks.todo_list.ui.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {

    private static Handler handler;

    public static void show(Context context, int resId) {
        if (isInUiThread()) {
            show0(context, resId);
        } else {
            post(context, resId);
        }
    }

    private static void show0(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, String text) {
        if (isInUiThread()) {
            show0(context, text);
        } else {
            post(context, text);
        }

    }

    private static void show0(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private static void post(Context context, int resId) {
        getHandler().post(() -> {
            show0(context, resId);
        });
    }


    private static void post(Context context, String text) {
        getHandler().post(() -> {
            show0(context, text);
        });
    }

    private static boolean isInUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }
}
