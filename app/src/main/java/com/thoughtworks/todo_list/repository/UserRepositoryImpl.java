package com.thoughtworks.todo_list.repository;

import android.util.Log;

import com.thoughtworks.todo_list.data.datasource.LocalDataSource;
import com.thoughtworks.todo_list.data.datasource.LoggedStatus;
import com.thoughtworks.todo_list.data.datasource.RemoteDataSource;
import com.thoughtworks.todo_list.data.datasource.UserDataSource;
import com.thoughtworks.todo_list.data.entity.User;
import com.thoughtworks.todo_list.ui.login.UserRepository;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserRepositoryImpl implements UserRepository {
    private static final String TAG = "UserRepositoryImpl";

    private UserDataSource mDataSource;
    private RemoteDataSource mRemoteDataSource;
    private LocalDataSource mLocalDataSource;

    public UserRepositoryImpl(UserDataSource dataSource, RemoteDataSource remoteDataSource, LocalDataSource localDataSource) {
        this.mDataSource = dataSource;
        this.mRemoteDataSource = remoteDataSource;
        this.mLocalDataSource = localDataSource;
    }

    @Override
    public Maybe<User> findByName(String name) {
        AtomicReference<Disposable> disposable = new AtomicReference<>();
        return mDataSource.findByName(name).switchIfEmpty(mRemoteDataSource.findByName(name).doOnSuccess(user -> {
            disposable.set(mDataSource.save(user).subscribe());
        }).doOnDispose(() -> {
            Log.d(TAG, "dispose do on success stream.");
            if (disposable.get() != null) {
                disposable.getAndSet(null).dispose();
            }
        })).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable updateUserLoggedStatus(User user, LoggedStatus status) {
        return mLocalDataSource.updateUserLoggedStatus(user.getName(), status).subscribeOn(Schedulers.io());
    }

    @Override
    public Maybe<User> loadLoggedUser() {
        return mLocalDataSource.loadLoggedUsername().flatMap(name -> mDataSource.findByName(name)).subscribeOn(Schedulers.io());
    }
}