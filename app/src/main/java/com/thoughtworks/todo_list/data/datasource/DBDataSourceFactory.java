package com.thoughtworks.todo_list.data.datasource;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.thoughtworks.todo_list.data.entity.Task;
import com.thoughtworks.todo_list.data.entity.User;

@Database(entities = {User.class, Task.class}, version = 3)
@TypeConverters(DateConverters.class)
public abstract class DBDataSourceFactory extends RoomDatabase {
    public abstract UserDataSource getUserDataSource();

    public abstract TaskDataSource getTaskDataSource();
}