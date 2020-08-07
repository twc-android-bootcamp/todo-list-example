package com.thoughtworks.todo_list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.thoughtworks.todo_list.data.datasource.DBDataSourceFactory;
import com.thoughtworks.todo_list.repository.RepositoryFactory;
import com.thoughtworks.todo_list.ui.task.TaskRepository;
import com.thoughtworks.todo_list.ui.login.UserRepository;

public class MainApplication extends Application {

    private DBDataSourceFactory mDBDataSourceFactory;
    private RepositoryFactory mRepositoryFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        openDatabase();
    }

    public TaskRepository getTaskRepository() {
        return getRepositoryFactory().getTaskRepository();
    }

    public UserRepository getUserRepository() {
        return getRepositoryFactory().getUserRepository();
    }

    public RepositoryFactory getRepositoryFactory() {
        if (mRepositoryFactory == null) {
            mRepositoryFactory = new RepositoryFactory(mDBDataSourceFactory, getApplicationContext());
        }
        return mRepositoryFactory;
    }

    private void openDatabase() {
        mDBDataSourceFactory = Room.databaseBuilder(getApplicationContext(), DBDataSourceFactory.class, this.getClass().getSimpleName()).addMigrations(new Migration(2, 3) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("ALTER TABLE `Task` ADD COLUMN `userId` INTEGER NOT NULL default 1");
            }
        }).build();
    }

    @Override
    public void onTerminate() {
        try {
            mDBDataSourceFactory.close();
        } catch (Exception ignored) {

        }
        super.onTerminate();
    }
}
