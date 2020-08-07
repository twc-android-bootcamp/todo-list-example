package com.thoughtworks.todo_list.data.datasource;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.thoughtworks.todo_list.data.entity.User;

import io.reactivex.Completable;
import io.reactivex.Maybe;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDataSource {
    @Query("SELECT * FROM user WHERE name = :name")
    Maybe<User> findByName(String name);

    @Insert(onConflict = REPLACE)
    Completable save(User user);
}
