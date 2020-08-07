package com.thoughtworks.todo_list.repository;

import com.thoughtworks.todo_list.data.datasource.TaskDataSource;
import com.thoughtworks.todo_list.ui.task.TaskDetails;
import com.thoughtworks.todo_list.ui.task.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class TaskRepositoryImpl implements TaskRepository {
    private TaskDataSource mTaskDataSource;

    public TaskRepositoryImpl(TaskDataSource taskDataSource) {
        this.mTaskDataSource = taskDataSource;
    }

    @Override
    public Maybe<List<TaskDetails>> findAll() {
        return mTaskDataSource.findAllTasks().map(tasks -> tasks.stream().map(TaskDetails::of).collect(Collectors.toList())).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<TaskDetails> save(TaskDetails taskDetails) {
        return mTaskDataSource.save(taskDetails.to()).map(TaskDetails::of).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable delete(TaskDetails taskDetails) {
        return mTaskDataSource.delete(taskDetails.to()).subscribeOn(Schedulers.io());
    }

    @Override
    public Maybe<TaskDetails> findById(long taskId) {
        return mTaskDataSource.findById(taskId).map(TaskDetails::of).subscribeOn(Schedulers.io());
    }
}
