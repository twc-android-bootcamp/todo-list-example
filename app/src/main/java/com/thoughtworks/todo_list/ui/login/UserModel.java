package com.thoughtworks.todo_list.ui.login;

import com.thoughtworks.todo_list.data.entity.User;

public class UserModel {
    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static UserModel of(User user) {
        UserModel userModel = new UserModel();
        userModel.setName(user.getName());
        userModel.setPassword(user.getPassword());
        return userModel;
    }
}
