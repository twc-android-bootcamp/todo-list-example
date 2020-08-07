package com.thoughtworks.todo_list.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.thoughtworks.todo_list.data.datasource.DBDataSourceFactory;
import com.thoughtworks.todo_list.data.datasource.LocalDataSource;
import com.thoughtworks.todo_list.data.datasource.LoggedStatus;
import com.thoughtworks.todo_list.data.datasource.RemoteDataSource;
import com.thoughtworks.todo_list.data.entity.User;
import com.thoughtworks.todo_list.ui.login.UserRepository;
import com.thoughtworks.todo_list.utils.HttpUtils;
import com.thoughtworks.todo_list.utils.JsonUtils;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.reflect.Field;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;


@RunWith(AndroidJUnit4.class)
public class UserRepositoryTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private DBDataSourceFactory dbDataSourceFactory;

    private UserRepository userRepository;

    private LocalDataSource localDataSource;


    @Before
    public void setUp() {
        dbDataSourceFactory = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                DBDataSourceFactory.class).build();
        localDataSource = new LocalDataSource(InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext());
        userRepository = new UserRepositoryImpl(dbDataSourceFactory.getUserDataSource(),
                new RemoteDataSource(), localDataSource);
    }

    @After
    public void tearDown() {
        dbDataSourceFactory.close();
    }

    @Test
    public void should_find_from_database() throws InterruptedException {
        //given
        User savedUser = buildUser();
        dbDataSourceFactory.getUserDataSource().save(savedUser).subscribe();

        //when...then
        userRepository.findByName("sjyuan")
                .test().await().assertValue(user -> user.getId() == savedUser.getId());
    }

    @NotNull
    private User buildUser() {
        User savedUser = new User();
        savedUser.setName("sjyuan");
        savedUser.setPassword("123");
        savedUser.setId(1);
        return savedUser;
    }

    @Test
    public void should_find_from_remote_data_source() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        //given
        User remoteUser = buildUser();
        String body = JsonUtils.toJson(remoteUser);
        inject(HttpUtils.class, "client", new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                return new Response.Builder()
                        .code(200)
                        .message(body)
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_0)
                        .body(ResponseBody.create(MediaType.parse("application/json"), body.getBytes()))
                        .addHeader("content-type", "application/json")
                        .build();
            }
        }).build());
        //when...then
        userRepository.findByName("sjyuan")
                .test().await().assertValue(user -> user.getName().equals(remoteUser.getName()));
        //断言本地保存
        dbDataSourceFactory.getUserDataSource().findByName("sjyuan").test().await().assertValue(user -> user.getId() == remoteUser.getId());
    }

    private void inject(Class clzz, String fieldName, Object obj) throws NoSuchFieldException, IllegalAccessException {
        Field field = clzz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(clzz, obj);
    }

    @Test
    public void should_update_user_login_status_to_on() throws InterruptedException {
        User user = buildUser();

        userRepository.updateUserLoggedStatus(user, LoggedStatus.ON).test().await().assertComplete();
        localDataSource.loadLoggedUsername().test().assertValue("sjyuan");
    }

    @Test
    public void should_update_user_login_status_to_off() throws InterruptedException {
        User user = buildUser();

        userRepository.updateUserLoggedStatus(user, LoggedStatus.OFF).test().await().assertComplete();
        localDataSource.loadLoggedUsername().test().assertNoValues().assertComplete();
    }

    @Test
    public void should_load_logged_user() throws InterruptedException {
        User loggedUser = buildUser();
        dbDataSourceFactory.getUserDataSource().save(loggedUser).blockingAwait();
        userRepository.updateUserLoggedStatus(loggedUser,LoggedStatus.ON).blockingAwait();

        userRepository.loadLoggedUser().test().await().assertValue(user -> user.getId() == loggedUser.getId());
    }

}