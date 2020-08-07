package com.thoughtworks.todo_list.ui.task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface TaskRepository {

    Maybe<List<TaskDetails>> findAll();

    Single<TaskDetails> save(TaskDetails task);

    Completable delete(TaskDetails task);

    Maybe<TaskDetails> findById(long taskId);
}
