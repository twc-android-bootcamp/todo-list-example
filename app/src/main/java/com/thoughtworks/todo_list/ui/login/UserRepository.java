package com.thoughtworks.todo_list.ui.login;

import com.thoughtworks.todo_list.data.datasource.LoggedStatus;
import com.thoughtworks.todo_list.data.entity.User;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface UserRepository {

    Maybe<User> findByName(String name);

    Completable updateUserLoggedStatus(User user, LoggedStatus on);

    Maybe<User> loadLoggedUser();
}
