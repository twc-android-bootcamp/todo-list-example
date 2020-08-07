package com.thoughtworks.todo_list.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.thoughtworks.todo_list.ui.login.LoginActivity;
import com.thoughtworks.todo_list.utils.DisposableUtils;
import com.thoughtworks.todo_list.utils.UserContext;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    private CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (UserContext.getUser() == null) {
            if (!isAllowedContinueWhenUserNotLogin()) {
                Log.d(TAG, "allow continue when user not login");
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return;
            }
        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getLayoutResId());

        ButterKnife.bind(this);

        initViewModel();

        initView();

        observeViewModel();
    }

    protected boolean isAllowedContinueWhenUserNotLogin() {
        return true;
    }

    protected void observeViewModel() {
        Log.d(TAG, "observe view model");
    }

    protected void initViewModel() {
        Log.d(TAG, "init view model");
    }

    protected void initView() {
        Log.d(TAG, "init view");
    }

    protected abstract int getLayoutResId();

    protected <T extends ViewModel> T obtainViewModel(Class<T> viewModelClass) {
        return new ViewModelProvider(this).get(viewModelClass);
    }

    protected void addDisposable(Disposable disposable) {
        this.mDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        DisposableUtils.closeQuietly(mDisposable);
        super.onDestroy();
    }
}
