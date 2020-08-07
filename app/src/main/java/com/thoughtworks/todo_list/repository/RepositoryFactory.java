package com.thoughtworks.todo_list.repository;

import android.content.Context;

import com.thoughtworks.todo_list.data.datasource.DBDataSourceFactory;
import com.thoughtworks.todo_list.data.datasource.LocalDataSource;
import com.thoughtworks.todo_list.data.datasource.RemoteDataSource;
import com.thoughtworks.todo_list.ui.login.UserRepository;
import com.thoughtworks.todo_list.ui.task.TaskRepository;

public class RepositoryFactory {

    private DBDataSourceFactory mDBDataSourceFactory;
    private Context mContext;
    private UserRepository mUserRepository;
    private TaskRepository mTaskRepository;

    private LocalDataSource localDataSource() {
        return new LocalDataSource(mContext);
    }

    private RemoteDataSource remoteDataSource() {
        return new RemoteDataSource();
    }

    public RepositoryFactory(DBDataSourceFactory dbDataSourceFactory, Context context) {
        this.mDBDataSourceFactory = dbDataSourceFactory;
        this.mContext = context;
    }

    public UserRepository getUserRepository() {
        if (mUserRepository == null) {
            mUserRepository = new UserRepositoryImpl(mDBDataSourceFactory.getUserDataSource(), remoteDataSource(), localDataSource());
        }
        return mUserRepository;
    }

    public TaskRepository getTaskRepository() {
        if (mTaskRepository == null) {
            mTaskRepository = new TaskRepositoryImpl(mDBDataSourceFactory.getTaskDataSource());
        }
        return mTaskRepository;
    }
}
