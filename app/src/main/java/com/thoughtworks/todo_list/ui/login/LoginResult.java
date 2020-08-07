package com.thoughtworks.todo_list.ui.login;

import androidx.annotation.Nullable;

class LoginResult {

    private boolean isSucceed;
    @Nullable
    private Integer error;
    @Nullable
    private String errorText;


    LoginResult(@Nullable Integer error) {
        this.error = error;
        isSucceed = false;
    }

    LoginResult(@Nullable String errorText) {
        this.errorText = errorText;
        isSucceed = false;
    }

    LoginResult() {
        this.isSucceed = true;
    }


    boolean isSucceed() {
        return isSucceed;
    }

    @Nullable
    Integer getError() {
        return error;
    }

    @Nullable
    public String getErrorText() {
        return errorText;
    }

}