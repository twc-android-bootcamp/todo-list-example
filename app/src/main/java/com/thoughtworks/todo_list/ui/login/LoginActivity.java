package com.thoughtworks.todo_list.ui.login;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.thoughtworks.todo_list.MainApplication;
import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.base.TextWatcherAdapter;
import com.thoughtworks.todo_list.base.BaseActivity;
import com.thoughtworks.todo_list.ui.task.TaskListActivity;
import com.thoughtworks.todo_list.ui.utils.ToastUtils;

import butterknife.BindView;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    private LoginViewModel mLoginViewModel;

    @BindView(R.id.login)
    Button mLoginButton;
    @BindView(R.id.password)
    EditText mPasswordEditText;
    @BindView(R.id.username)
    EditText mUsernameEditText;
    @BindView(R.id.loading)
    ProgressBar mLoadingProgressBar;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        TextWatcher afterTextChangedListener = new UsernamePasswordTextWatcher();
        mUsernameEditText.addTextChangedListener(afterTextChangedListener);
        mPasswordEditText.addTextChangedListener(afterTextChangedListener);

        mPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
            }
            return false;
        });

        mLoginButton.setOnClickListener(v -> {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            login();
        });
    }

    @Override
    protected void initViewModel() {
        UserRepository userRepository = (((MainApplication) getApplication())).getUserRepository();
        mLoginViewModel = obtainViewModel(LoginViewModel.class);
        mLoginViewModel.setUserRepository(userRepository);
    }

    @Override
    protected void observeViewModel() {
        mLoginViewModel.observeLoginFormState(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            mLoginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                mUsernameEditText.setError(getText(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                mPasswordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        mLoginViewModel.observeLoginResult(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            mLoadingProgressBar.setVisibility(View.GONE);

            if (loginResult.isSucceed()) {
                Intent intent = new Intent(this, TaskListActivity.class);
                startActivity(intent);
                finish();
                ToastUtils.show(getApplicationContext(), R.string.prompt_login_successfully);
                return;
            }

            if (loginResult.getError() != null) {
                ToastUtils.show(getApplicationContext(), loginResult.getError());
                return;
            }
            if (loginResult.getErrorText() != null) {
                ToastUtils.show(getApplicationContext(), loginResult.getErrorText());
            }

        });
    }

    private void login() {
        Log.d(TAG, "login");
        mLoginViewModel.login(mUsernameEditText.getText().toString(),
                mPasswordEditText.getText().toString());
    }

    private class UsernamePasswordTextWatcher extends TextWatcherAdapter {

        @Override
        public void afterTextChanged(Editable s) {
            mLoginViewModel.loginDataChanged(mUsernameEditText.getText().toString(),
                    mPasswordEditText.getText().toString());
        }
    }
}