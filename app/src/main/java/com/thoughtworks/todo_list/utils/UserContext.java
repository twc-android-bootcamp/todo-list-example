package com.thoughtworks.todo_list.utils;

import android.util.Log;

import com.thoughtworks.todo_list.data.entity.User;

public class UserContext {
    private static final String TAG = "UserContext";

    private static User user;

    public static void setUser(User user) {
        Log.d(TAG, "set user : [" + user.getId() + "]");
        UserContext.user = user;
    }

    public static User getUser() {
        return user;
    }

}
