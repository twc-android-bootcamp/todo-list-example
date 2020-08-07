package com.thoughtworks.todo_list.utils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {

    private static OkHttpClient client = new OkHttpClient();

    public static CompletableFuture<String> getString(String url) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request
                    .Builder()
                    .url(url)
                    .get()
                    .build();
            try {
                Response response = client.newCall(request)
                        .execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
                throw new IOException(response.body().string());
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }
}
