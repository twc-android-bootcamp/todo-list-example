package com.thoughtworks.todo_list.utils;

public class StringUtils {
    public static boolean isBlank(String string) {
        return string == null || string.trim().isEmpty();
    }
}
