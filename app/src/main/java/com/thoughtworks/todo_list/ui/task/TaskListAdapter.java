package com.thoughtworks.todo_list.ui.task;

import android.graphics.Paint;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thoughtworks.todo_list.R;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskItemViewHolder> {
    private List<TaskDetails> mTasks = new ArrayList<>();

    private Callback callback;

    public TaskListAdapter(Callback callback) {
        this.callback = callback;
    }

    public void setTasks(List<TaskDetails> tasks) {
        this.mTasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_view, parent, false);
        return new TaskItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskItemViewHolder holder, int position) {
        TaskDetails taskDetails = mTasks.get(position);
        holder.setDone(taskDetails.isDone());
        holder.setTitle(taskDetails.getTitle());
        holder.setDeadline(DateFormat.format("MM月dd日", taskDetails.getDeadline()).toString());
        holder.registerListener(new TaskItemViewHolder.Listener(taskDetails, callback));
    }

    @Override
    public void onViewRecycled(@NonNull TaskItemViewHolder holder) {
        holder.recycle();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    interface Callback {
        void onChecked(TaskDetails taskDetails, boolean isChecked);

        void onClick(TaskDetails taskDetails);
    }

    static class TaskItemViewHolder extends RecyclerView.ViewHolder {
        private TextView taskTitle;
        private TextView taskDeadline;
        private CheckBox taskDone;

        public TaskItemViewHolder(@NonNull View itemView) {
            super(itemView);
            taskDone = itemView.findViewById(R.id.task_done);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDeadline = itemView.findViewById(R.id.task_deadline);
        }

        public void setTitle(String title) {
            taskTitle.setText(title);
        }

        public void setDeadline(String deadline) {
            taskDeadline.setText(deadline);
        }

        public void setDone(boolean done) {
            taskDone.setChecked(done);
            if (done) {
                taskTitle.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                taskTitle.getPaint().setFlags(taskTitle.getPaint().getFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        public void registerListener(Listener listener) {
            taskDone.setOnCheckedChangeListener(listener);
            taskTitle.setOnClickListener(listener);
        }

        public void recycle() {
            taskDone.setOnCheckedChangeListener(null);
            taskTitle.setOnClickListener(null);
        }

        static class Listener implements CheckBox.OnCheckedChangeListener, View.OnClickListener {
            private TaskDetails taskDetails;
            private Callback callback;

            public Listener(TaskDetails taskDetails, Callback callback) {
                this.taskDetails = taskDetails;
                this.callback = callback;
            }

            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onClick(taskDetails);
                }
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (callback != null) {
                    callback.onChecked(taskDetails, isChecked);
                }
            }
        }
    }
}
