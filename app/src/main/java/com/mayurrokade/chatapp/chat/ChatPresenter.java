/*
 * Copyright 2018 Mayur Rokade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.mayurrokade.chatapp.chat;

import android.support.annotation.NonNull;

import com.mayurrokade.chatapp.data.ChatMessage;
import com.mayurrokade.chatapp.data.source.Repository;
import com.mayurrokade.chatapp.eventservice.EventListener;
import com.mayurrokade.chatapp.util.schedulers.BaseSchedulerProvider;

import java.net.URISyntaxException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ChatPresenter implements ChatContract.Presenter {

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    @NonNull
    private final CompositeDisposable mCompositeDisposable;

    @NonNull
    private final Repository mRepository;

    @NonNull
    private final ChatContract.View mView;

    @NonNull
    private final EventListener mEventListener;

    public ChatPresenter(@NonNull ChatContract.View view,
                         @NonNull EventListener eventListener,
                         @NonNull BaseSchedulerProvider schedulerProvider,
                         @NonNull Repository repository) {
        mView = view;
        mEventListener = eventListener;
        mRepository = repository;
        repository.setEventListener(mEventListener);
        mSchedulerProvider = schedulerProvider;
        mCompositeDisposable = new CompositeDisposable();
        mView.setPresenter(this);
        mView.initView();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    @Override
    public void sendMessage(ChatMessage chatMessage) {
        Disposable disposable =
                mRepository.sendMessage(chatMessage)
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe(new Consumer<ChatMessage>() {
                            @Override
                            public void accept(ChatMessage chatMessage) throws Exception {
                                mView.onMessageDelivered(chatMessage);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                mView.showAlert(throwable.getMessage(), true);
                            }
                        });

        mCompositeDisposable.add(disposable);
    }

    @Override
    public void changeUsername(String username) {
        try {
            mRepository.disconnect();
            mRepository.connect(username);
            mView.updateUsername(username);
            mView.showAlert("Username set", false);
        } catch (URISyntaxException e) {
            mView.showAlert("Changing username failed", true);
            e.printStackTrace();
        }
    }

    @Override
    public void onTyping() {
        mRepository.onTyping();
    }

    @Override
    public void onStopTyping() {
        mRepository.onStopTyping();
    }
}
