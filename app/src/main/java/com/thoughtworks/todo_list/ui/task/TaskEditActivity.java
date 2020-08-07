package com.thoughtworks.todo_list.ui.task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.PopupWindowCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thoughtworks.todo_list.MainApplication;
import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.base.BaseActivity;
import com.thoughtworks.todo_list.receiver.AlarmReciever;
import com.thoughtworks.todo_list.ui.utils.ToastUtils;
import com.thoughtworks.todo_list.utils.StringUtils;
import com.thoughtworks.todo_list.utils.UserContext;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import butterknife.BindView;

public class TaskEditActivity extends BaseActivity {

    private static final String TAG = "TaskEditActivity";

    public final static String EXTRAS_TASK_ID = "taskId";
    public static final int SIX_HOURS_MILLIS = 6 * 60 * 60 * 1000;

    @BindView(R.id.save_task)
    FloatingActionButton mSaveTask;
    @BindView(R.id.delete_task)
    FloatingActionButton mDeletelTask;
    @BindView(R.id.task_title)
    EditText mTaskTitle;
    @BindView(R.id.task_description)
    EditText mTaskDescription;
    @BindView(R.id.task_remind)
    Switch mTaskRemind;
    @BindView(R.id.task_deadline_display)
    TextView mTaskDeadlineDisplay;
    @BindView(R.id.task_done)
    CheckBox mTaskDone;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private PopupWindow mPopupWindow;

    CalendarView mTaskDeadline;

    private TaskEditViewModel mViewModel;
    private LocalDate mDeadline;
    private long mTaskId;
    private TaskDetails mTaskDetails;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_task_edit;
    }

    protected void initView() {
        mTaskId = getIntent().getLongExtra(EXTRAS_TASK_ID, 0);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        mTaskDeadline = new CalendarView(this);
        mTaskDeadline.setBackgroundResource(R.color.white);
        mPopupWindow = new PopupWindow(mTaskDeadline, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);

        mSaveTask.setOnClickListener(view -> {
            saveTask();
        });
        mDeletelTask.setOnClickListener(view -> {
            mViewModel.deleteTask(mTaskDetails);
        });
        mTaskDeadlineDisplay.setOnClickListener(view -> {

            mTaskDeadline.measure(makeDropDownMeasureSpec(mPopupWindow.getWidth()),
                    makeDropDownMeasureSpec(mPopupWindow.getHeight()));

            int offsetX = Math.abs(mPopupWindow.getContentView().getMeasuredWidth() - view.getWidth()) / 2;
            int offsetY = 0;

            PopupWindowCompat.showAsDropDown(mPopupWindow, view, -offsetX, offsetY, Gravity.START);
        });

        mTaskDeadline.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            mDeadline = LocalDate.of(year, month + 1, dayOfMonth);

            mTaskDeadlineDisplay.setText(format(mDeadline));
            mPopupWindow.dismiss();
        });

    }

    private String format(LocalDate deadline) {
        return deadline.format(DateTimeFormatter.ofPattern("yyyy年MM月dd"));
    }

    protected void initViewModel() {
        mViewModel = obtainViewModel(TaskEditViewModel.class);
        mViewModel.setTaskRepository(((MainApplication) getApplication()).getTaskRepository());
    }

    protected void observeViewModel() {
        mViewModel.observeTaskSaved(this, task -> {
            ToastUtils.show(this, R.string.prompt_saved_successfully);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager == null) {
                return;
            }
            Intent intent = new Intent(this, AlarmReciever.class);
            intent.putExtra(EXTRAS_TASK_ID, task.getId());

            sendBroadcast(intent);//查看通知效果，不需等待每天6点

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) task.getId(), intent, 0);
            long triggerAtTime = task.getDeadline().getTime() + SIX_HOURS_MILLIS;

            boolean isNeededRemind = task.isRemind() && !task.isDone() && triggerAtTime > System.currentTimeMillis();
            if (isNeededRemind) {
                Log.d(TAG, "set alarm ：" + new Date(triggerAtTime).toString());

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, pendingIntent);
            } else {
                Log.d(TAG, "cancel task [" + task.getId() + "] alarm.");

                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
            }
            setResult(RESULT_OK);
            finish();
        });

        mViewModel.observeTaskDeleted(this, task -> {
            if (task != null && task.getId() > 0) {
                ToastUtils.show(this, R.string.prompt_deleted_successfully);
            }
            setResult(RESULT_OK);
            finish();
        });

        mViewModel.observeTaskInited(this, task -> {
            if (task != null) {
                mTaskTitle.setText(task.getTitle());
                mTaskDescription.setText(task.getDescription());
                mTaskDone.setChecked(task.isDone());
                mTaskRemind.setChecked(task.isRemind());
                if (task.getDeadline() != null) {
                    mDeadline = task.getDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    mTaskDeadlineDisplay.setText(format(mDeadline));
                    mTaskDeadline.setDate(task.getDeadline().getTime());
                }
                mDeletelTask.show();
            }
            mTaskDetails = task;
        });

        mViewModel.init(mTaskId);

    }

    private void saveTask() {
        if (mDeadline == null) {
            ToastUtils.show(this, R.string.prompt_deadline_is_not_empty);
            return;
        }

        String title = getString(mTaskTitle);
        if (StringUtils.isBlank(title)) {
            ToastUtils.show(this, R.string.prompt_task_title_is_not_empty);
            return;
        }

        if (mTaskDetails == null) {
            mTaskDetails = new TaskDetails();
            mTaskDetails.setUserId(UserContext.getUser().getId());
        }

        mTaskDetails.setTitle(title);
        mTaskDetails.setDescription(getString(mTaskDescription));
        mTaskDetails.setDone(mTaskDone.isChecked());
        mTaskDetails.setRemind(mTaskRemind.isChecked());
        mTaskDetails.setDeadline(Date.from(mDeadline.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        mViewModel.saveTask(mTaskDetails);
    }

    private String getString(EditText editText) {
        return editText.getText().toString().trim();
    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }
}
