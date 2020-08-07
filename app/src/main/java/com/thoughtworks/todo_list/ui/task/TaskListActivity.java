package com.thoughtworks.todo_list.ui.task;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thoughtworks.todo_list.MainApplication;
import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.base.BaseActivity;
import com.thoughtworks.todo_list.data.datasource.LoggedStatus;
import com.thoughtworks.todo_list.ui.login.LoginActivity;
import com.thoughtworks.todo_list.utils.UserContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import butterknife.BindView;

public class TaskListActivity extends BaseActivity {
    private static final String TAG = "TaskListActivity";

    public static final int REQUEST_TASK_DETAILS = 1;

    private TaskListViewModel mViewModel;
    @BindView(R.id.task_list_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.task_count)
    TextView mTaskCount;
    @BindView(R.id.date_month)
    TextView mDateMonth;
    @BindView(R.id.date_week_day)
    TextView mDateWeekDay;
    @BindView(R.id.action_add_task)
    FloatingActionButton mActionAddTask;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private TaskListAdapter mAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_task_list;
    }

    protected void initView() {
        setSupportActionBar(mToolbar);

        LocalDate localDate = LocalDate.now();
        mDateWeekDay.setText(localDate.format(DateTimeFormatter.ofPattern("E, dd")));
        mDateMonth.setText(localDate.format(DateTimeFormatter.ofPattern("MMM")));
        mActionAddTask.setOnClickListener(view -> {
            startActivityForResult(new Intent(TaskListActivity.this, TaskEditActivity.class), REQUEST_TASK_DETAILS);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new TaskListAdapter(new TaskListAdapter.Callback() {
            @Override
            public void onChecked(TaskDetails taskDetails, boolean isChecked) {
                mViewModel.checkTask(taskDetails, isChecked);
            }

            @Override
            public void onClick(TaskDetails taskDetails) {
                Intent intent = new Intent(TaskListActivity.this, TaskEditActivity.class);
                intent.putExtra(TaskEditActivity.EXTRAS_TASK_ID, taskDetails.getId());
                startActivityForResult(intent, TaskListActivity.REQUEST_TASK_DETAILS);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_TASK_DETAILS) {
            Log.d(TAG, "on activity result.");
            mViewModel.init();
        }
    }

    protected void initViewModel() {
        mViewModel = obtainViewModel(TaskListViewModel.class);
        mViewModel.setTaskRepository(((MainApplication) getApplication()).getTaskRepository());
    }

    @Override
    protected void observeViewModel() {
        mViewModel.observeTaskList(this, tasks -> {
            mAdapter.setTasks(tasks);
            mTaskCount.setText(String.format(getString(R.string.label_task_count), tasks.size()));
        });
        mViewModel.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            addDisposable(((MainApplication) getApplication()).getUserRepository()
                    .updateUserLoggedStatus(UserContext.getUser(), LoggedStatus.OFF)
                    .subscribe(() -> {
                        startActivity(new Intent(TaskListActivity.this, LoginActivity.class));
                        finish();
                    }));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}