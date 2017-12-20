package com.ksyun.ts.ShortVideoDemo.ui;

import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by gaoyuanpeng on 2017/2/23.
 */

public class DefaultOnClick implements View.OnClickListener {
    private ObservableEmitter mEmitter;

    public DefaultOnClick(Function<View, View> function, Consumer<View> consumer2) {

        Observable observable = Observable.create(new ObservableOnSubscribe<View>() {
            @Override
            public void subscribe(ObservableEmitter<View> emitter) throws Exception {
                mEmitter = emitter;
            }
        })
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());
        if (function == null && consumer2 != null) {
            observable.subscribe(consumer2);
        } else if (function != null && consumer2 != null) {
            observable
                    .map(function)
                    .subscribe(consumer2);
        } else if (function != null) {
            observable
                    .map(function)
                    .subscribe(new Consumer() {
                        @Override
                        public void accept(Object o) throws Exception {

                        }
                    });
        } else {
            observable
                    .subscribe(new Consumer() {
                        @Override
                        public void accept(Object o) throws Exception {

                        }
                    });
        }
    }


    @Override
    public void onClick(View view) {
        mEmitter.onNext(view);
    }
}
