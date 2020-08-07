package com.thoughtworks.todo_list.ui.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.thoughtworks.todo_list.MainApplication;
import com.thoughtworks.todo_list.ui.login.UserRepository;
import com.thoughtworks.todo_list.ui.login.LoginActivity;
import com.thoughtworks.todo_list.ui.task.TaskListActivity;
import com.thoughtworks.todo_list.utils.DisposableUtils;
import com.thoughtworks.todo_list.utils.UserContext;

import io.reactivex.disposables.Disposable;

public class LauncherActivity extends Activity {
    private Disposable mDisposable;
    private static final String TAG = "LauncherActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserRepository userRepository = ((MainApplication) getApplication()).getUserRepository();
        if (UserContext.getUser() == null) {
            mDisposable = userRepository.loadLoggedUser().subscribe(user -> {
                UserContext.setUser(user);
                gotoTaskHome();
            }, e -> {
                //
            }, () -> {
                startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                finish();
            });
        } else {
            gotoTaskHome();
        }
    }

    private void gotoTaskHome() {
        Log.d(TAG, "go to task home");

        Intent intent = new Intent(LauncherActivity.this, TaskListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        DisposableUtils.closeQuietly(mDisposable);
        super.onDestroy();
    }
}
