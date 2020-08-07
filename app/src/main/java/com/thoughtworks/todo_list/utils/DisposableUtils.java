package com.thoughtworks.todo_list.utils;

import android.util.Log;

import io.reactivex.disposables.Disposable;

public class DisposableUtils {

    private static final String TAG = "DisposableUtils";

    public static void closeQuietly(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            Log.d(TAG, "close disposable quietly.");
            disposable.dispose();
        }
    }
}
