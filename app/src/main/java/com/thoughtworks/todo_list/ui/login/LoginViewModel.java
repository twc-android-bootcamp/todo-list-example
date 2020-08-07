package com.thoughtworks.todo_list.ui.login;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.base.BaseViewModel;
import com.thoughtworks.todo_list.data.entity.User;
import com.thoughtworks.todo_list.data.datasource.LoggedStatus;
import com.thoughtworks.todo_list.utils.Encryptor;
import com.thoughtworks.todo_list.utils.UserContext;

import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;

public class LoginViewModel extends BaseViewModel {
    private static final String TAG = "LoginViewModel";
    private MutableLiveData<LoginFormState> mLoginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> mLoginResult = new MutableLiveData<>();

    private UserRepository userRepository;

    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    void observeLoginFormState(LifecycleOwner lifecycleOwner, Observer<LoginFormState> observer) {
        mLoginFormState.observe(lifecycleOwner, observer);
    }

    void observeLoginResult(LifecycleOwner lifecycleOwner, Observer<LoginResult> observer) {
        mLoginResult.observe(lifecycleOwner, observer);
    }

    @SuppressLint("CheckResult")
    public void login(String username, String password) {
        userRepository.findByName(username)
                .subscribe(new MaybeObserver<User>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onSuccess(User user) {
                        if (user.getPassword().equals(Encryptor.md5(password))) {
                            addDisposable(userRepository.updateUserLoggedStatus(user, LoggedStatus.ON).subscribe(() -> {
                                UserContext.setUser(user);
                                mLoginResult.postValue(new LoginResult());
                                Log.i(TAG, "login successfully");
                            }));
                            return;
                        }
                        mLoginResult.postValue(new LoginResult(R.string.invalid_login_failed_password));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginResult.postValue(new LoginResult(e.getMessage()));
                        Log.i(TAG, "login error", e);
                    }

                    @Override
                    public void onComplete() {
                        mLoginResult.postValue(new LoginResult(R.string.invalid_login_failed_username));
                        Log.i(TAG, "login failed");
                    }
                });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            mLoginFormState.postValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            mLoginFormState.postValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            mLoginFormState.postValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.trim().isEmpty()) {
            return false;
        }
        return username.trim().matches("[a-zA-Z0-9]{3,13}");
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 6 && password.trim().length() <= 18;
    }
}