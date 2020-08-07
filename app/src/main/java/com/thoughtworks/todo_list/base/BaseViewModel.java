package com.thoughtworks.todo_list.base;

import androidx.lifecycle.ViewModel;

import com.thoughtworks.todo_list.utils.DisposableUtils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseViewModel extends ViewModel {
    private CompositeDisposable mDisposable = new CompositeDisposable();

    protected void addDisposable(Disposable disposable) {
        this.mDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        DisposableUtils.closeQuietly(mDisposable);
        super.onCleared();
    }
}
