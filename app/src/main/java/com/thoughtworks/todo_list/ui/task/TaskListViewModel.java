package com.thoughtworks.todo_list.ui.task;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.thoughtworks.todo_list.base.BaseViewModel;
import com.thoughtworks.todo_list.data.entity.Task;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskListViewModel extends BaseViewModel {
    private MutableLiveData<List<TaskDetails>> mLiveTasks = new MutableLiveData<>();
    private TaskRepository mTaskRepository;

    public void setTaskRepository(TaskRepository taskRepository) {
        this.mTaskRepository = taskRepository;
    }

    public void observeTaskList(LifecycleOwner owner, Observer<List<TaskDetails>> observer) {
        mLiveTasks.observe(owner, observer);
    }

    public void init() {
        addDisposable(mTaskRepository.findAll().subscribe(tasks -> {
            mLiveTasks.postValue(sortedTasks(tasks.stream().map(TaskDetails::of).collect(Collectors.toList())));
        }));
    }

    private List<TaskDetails> sortedTasks(List<TaskDetails> tasks) {
        return tasks.stream().sorted(Comparator.comparing(Task::isDone).thenComparing(Task::getDeadline))
                .collect(Collectors.toList());
    }

    public void checkTask(TaskDetails taskDetails, boolean isChecked) {
        taskDetails.setDone(isChecked);
        addDisposable(mTaskRepository.save(taskDetails).subscribe(savedTask -> {
            mLiveTasks.postValue(sortedTasks(Objects.requireNonNull(mLiveTasks.getValue())));
        }));
    }
}