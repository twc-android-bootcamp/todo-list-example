package com.thoughtworks.todo_list.data.datasource;

import android.content.Context;
import android.content.SharedPreferences;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public class LocalDataSource {

    private Context mContext;

    private static final String SP_SETTINGS = "settings";
    private static final String SP_SETTINGS_LOGGED_USER = "logged-user";

    public LocalDataSource(Context context) {
        this.mContext = context;
    }

    public Completable updateUserLoggedStatus(String username, LoggedStatus status) {
        return Completable.fromAction(() -> {
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            if (status == LoggedStatus.ON) {
                editor.putString(SP_SETTINGS_LOGGED_USER, username);
            } else {
                editor.remove(SP_SETTINGS_LOGGED_USER);
            }
            editor.apply();
        });
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(SP_SETTINGS, Context.MODE_PRIVATE);
    }

    public Maybe<String> loadLoggedUsername() {
        return Maybe.fromCallable(() -> getSharedPreferences().getString(SP_SETTINGS_LOGGED_USER, null));
    }
}
