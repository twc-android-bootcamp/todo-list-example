package com.thoughtworks.todo_list.data.datasource;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.thoughtworks.todo_list.data.entity.Task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class TaskDataSource {
    @Query("SELECT * FROM task")
    public abstract Maybe<List<Task>> findAllTasks();

    @Insert(onConflict = REPLACE, entity = Task.class)
    protected abstract Single<Long> saveAndReturnPK(Task task);

    public Single<Task> save(Task task) {
        return saveAndReturnPK(task).map(id -> {
            task.setId(id);
            return task;
        });
    }

    @Delete
    public abstract Completable delete(Task task);

    @Query("SELECT * FROM task where id = :taskId")
    public abstract Maybe<Task> findById(long taskId);
}
