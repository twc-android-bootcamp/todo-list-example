package com.thoughtworks.todo_list.data.datasource;

import com.thoughtworks.todo_list.data.entity.User;
import com.thoughtworks.todo_list.utils.HttpUtils;
import com.thoughtworks.todo_list.utils.JsonUtils;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

public class RemoteDataSource {

    public Maybe<User> findByName(String name) {
        return Flowable.fromFuture(HttpUtils.getString("https://twc-android-bootcamp.github.io/fake-data/data/user.json"))
                .map(response -> JsonUtils.from(response, User.class)).filter(user -> user.getName().equals(name)).firstElement();
    }
}
