package com.thoughtworks.todo_list.ui;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.thoughtworks.todo_list.MainApplication;
import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.data.entity.User;
import com.thoughtworks.todo_list.ui.login.UserRepository;
import com.thoughtworks.todo_list.ui.login.LoginActivity;
import com.thoughtworks.todo_list.utils.Encryptor;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.internal.operators.maybe.MaybeCreate;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void should_login_successfully_when_login_given_correct_username_and_password() throws InterruptedException {
        MainApplication applicationContext = (MainApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        UserRepository userRepository = applicationContext.getUserRepository();
        User user = new User();
        user.setId(1);
        user.setPassword(Encryptor.md5("123456"));
        user.setName("sjyuan");
        when(userRepository.findByName("sjyuan")).thenReturn(Maybe.create(emitter -> emitter.onSuccess(user)));

        onView(ViewMatchers.withId(R.id.username)).perform(typeText("sjyuan"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.login)).perform(click());
        Thread.sleep(250);

        onView(withText(R.string.prompt_login_successfully)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void should_login_failed_when_login_given_invalid_password() throws InterruptedException {
        MainApplication applicationContext = (MainApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        UserRepository userRepository = applicationContext.getUserRepository();
        User user = new User();
        user.setId(1);
        user.setPassword(Encryptor.md5("12345"));
        user.setName("sjyuan");
        when(userRepository.findByName("sjyuan")).thenReturn(new MaybeCreate(emitter -> emitter.onSuccess(user)));

        onView(withId(R.id.username)).perform(typeText("sjyuan"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.login)).perform(click());
        Thread.sleep(250);

        onView(withText(R.string.invalid_login_failed_password)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void should_login_failed_when_login_given_username_does_not_exist() throws InterruptedException {
        MainApplication applicationContext = (MainApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        UserRepository userRepository = applicationContext.getUserRepository();
        User user = new User();
        user.setId(1);
        user.setPassword("12345");
        user.setName("sjyuan");
        when(userRepository.findByName("notexist")).thenReturn(new Maybe<User>() {
            @Override
            protected void subscribeActual(MaybeObserver<? super User> observer) {
                observer.onComplete();
            }
        });
        onView(withId(R.id.username)).perform(typeText("notexist"));
        onView(withId(R.id.password)).perform(typeText("12345"));
        onView(withId(R.id.login)).perform(click());
        Thread.sleep(250);

        onView(withText(R.string.invalid_login_failed_username)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }
}