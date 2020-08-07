package com.thoughtworks.todo_list.ui.task;

import android.os.Parcel;
import android.os.Parcelable;

import com.thoughtworks.todo_list.data.entity.Task;

import java.util.Date;

public class TaskDetails extends Task implements Parcelable {

    public TaskDetails() {

    }

    protected TaskDetails(Parcel in) {
        setId(in.readLong());
        setTitle(in.readString());
        setDescription(in.readString());
        setRemind(in.readByte() != 0);
        setDone(in.readByte() != 0);
        setDeadline(new Date(in.readLong()));
        setUserId(in.readLong());
    }

    public static final Creator<TaskDetails> CREATOR = new Creator<TaskDetails>() {
        @Override
        public TaskDetails createFromParcel(Parcel in) {
            return new TaskDetails(in);
        }

        @Override
        public TaskDetails[] newArray(int size) {
            return new TaskDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getTitle());
        dest.writeString(getDescription());
        dest.writeByte((byte) (isRemind() ? 1 : 0));
        dest.writeByte((byte) (isDone() ? 1 : 0));
        dest.writeLong(getDeadline().getTime());
        dest.writeLong(getUserId());
    }

    public static TaskDetails of(Task task) {
        TaskDetails details = new TaskDetails();
        details.setId(task.getId());
        details.setTitle(task.getTitle());
        details.setDescription(task.getDescription());
        details.setDeadline(task.getDeadline());
        details.setRemind(task.isRemind());
        details.setDone(task.isDone());
        details.setUserId(task.getUserId());
        return details;
    }

    public Task to() {
        Task task = new Task();
        task.setId(getId());
        task.setTitle(getTitle());
        task.setDescription(getDescription());
        task.setDeadline(getDeadline());
        task.setRemind(isRemind());
        task.setDone(isDone());
        task.setUserId(getUserId());
        return task;
    }
}
