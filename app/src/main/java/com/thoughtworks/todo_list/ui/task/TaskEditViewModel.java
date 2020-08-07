package com.thoughtworks.todo_list.ui.task;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.thoughtworks.todo_list.base.BaseViewModel;

public class TaskEditViewModel extends BaseViewModel {
    private MutableLiveData<TaskDetails> mSavedTask = new MutableLiveData<>();
    private MutableLiveData<TaskDetails> mDeletedTask = new MutableLiveData<>();
    private MutableLiveData<TaskDetails> mInitializedTask = new MutableLiveData<>();

    private TaskRepository mTaskRepository;

    public void saveTask(TaskDetails task) {
        addDisposable(mTaskRepository.save(task).subscribe(savedTask -> {
            this.mSavedTask.postValue(TaskDetails.of(savedTask));
        }));
    }

    public void observeTaskSaved(LifecycleOwner owner, Observer<TaskDetails> savedObserver) {
        mSavedTask.observe(owner, savedObserver);
    }

    public void observeTaskDeleted(LifecycleOwner owner, Observer<TaskDetails> deletedObserver) {
        mDeletedTask.observe(owner, deletedObserver);
    }

    public void observeTaskInited(LifecycleOwner owner, Observer<TaskDetails> taskInitedObserver) {
        mInitializedTask.observe(owner, taskInitedObserver);
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.mTaskRepository = taskRepository;
    }

    public void deleteTask(TaskDetails taskDetails) {
        if (taskDetails != null && taskDetails.getId() > 0) {
            addDisposable(this.mTaskRepository.delete(taskDetails).subscribe(() -> {
                mDeletedTask.postValue(taskDetails);
            }));
        } else {
            mDeletedTask.postValue(taskDetails);
        }
    }

    public void init(long taskId) {
        if (taskId > 0) {
            addDisposable(mTaskRepository.findById(taskId).subscribe(task -> {
                mInitializedTask.postValue(TaskDetails.of(task));
            }));
        }
    }
}