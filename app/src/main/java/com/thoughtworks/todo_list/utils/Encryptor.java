package com.thoughtworks.todo_list.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryptor {
    public static String md5(String source) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(source.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
