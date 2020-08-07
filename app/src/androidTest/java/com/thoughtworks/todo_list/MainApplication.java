package com.thoughtworks.todo_list;

import android.app.Application;

import com.thoughtworks.todo_list.repository.RepositoryFactory;
import com.thoughtworks.todo_list.ui.login.UserRepository;

import static org.mockito.Mockito.mock;

public class MainApplication extends Application {
    private RepositoryFactory repositoryFactory;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public RepositoryFactory getRepositoryFactory() {
        if (repositoryFactory == null) {
            repositoryFactory = mock(RepositoryFactory.class);
        }
        return repositoryFactory;
    }

    public UserRepository getUserRepository() {
        return mock(UserRepository.class);
    }

}
