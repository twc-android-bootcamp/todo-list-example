package com.thoughtworks.todo_list.utils;

import com.google.gson.Gson;

public class JsonUtils {
    private static Gson gson = new Gson();

    public static <T> T from(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }
}
