package com.thoughtworks.todo_list.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.thoughtworks.todo_list.data.datasource.DBDataSourceFactory;
import com.thoughtworks.todo_list.data.entity.Task;
import com.thoughtworks.todo_list.ui.task.TaskDetails;
import com.thoughtworks.todo_list.ui.task.TaskRepository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TaskRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private DBDataSourceFactory dbDataSourceFactory;

    private TaskRepository taskRepository;


    @Before
    public void setUp() {
        dbDataSourceFactory = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                DBDataSourceFactory.class).build();
        taskRepository = new TaskRepositoryImpl(dbDataSourceFactory.getTaskDataSource());
    }

    @After
    public void tearDown() {
        dbDataSourceFactory.close();
    }

    @Test
    public void should_save_task_successfully() {
        TaskDetails task = buildTask(System.currentTimeMillis());

        Task savedTask = taskRepository.save(task).blockingGet();

        Assert.assertEquals(task.getTitle(), savedTask.getTitle());

        dbDataSourceFactory.getTaskDataSource().findById(savedTask.getId()).test().assertValue(db -> savedTask.getId() == db.getId());
    }

    @Test
    public void should_find_task_by_id() throws InterruptedException {
        Task savedTask = dbDataSourceFactory.getTaskDataSource().save(buildTask(System.currentTimeMillis())).blockingGet();

        taskRepository.findById(savedTask.getId()).test().await().assertValue(task -> task.getId() == savedTask.getId());
    }

    @Test
    public void should_find_all_tasks() throws InterruptedException {
        List<Task> tasks = new ArrayList<>();
        tasks.add(dbDataSourceFactory.getTaskDataSource().save(buildTask(System.currentTimeMillis())).blockingGet());
        tasks.add(dbDataSourceFactory.getTaskDataSource().save(buildTask(System.currentTimeMillis())).blockingGet());

        List<TaskDetails> loadedTasks = taskRepository.findAll().blockingGet();
        Assert.assertEquals(tasks.size(), loadedTasks.size());
    }

    @Test
    public void should_delete_successfully() throws InterruptedException {
        Task savedTask = dbDataSourceFactory.getTaskDataSource().save(buildTask(System.currentTimeMillis())).blockingGet();

        taskRepository.delete(TaskDetails.of(savedTask)).test().await().assertComplete();

        dbDataSourceFactory.getTaskDataSource().findById(savedTask.getId()).test().assertNoValues().assertComplete();
    }

    private TaskDetails buildTask(long time) {
        TaskDetails task = new TaskDetails();
        task.setTitle("title");
        task.setDescription("description");
        task.setUserId(1);
        task.setDeadline(new Date(time));
        return task;
    }
}
