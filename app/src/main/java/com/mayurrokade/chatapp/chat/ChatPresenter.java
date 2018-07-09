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

/**
 * Listens to user actions and sends data to remote data source.
 * <p>
 * Presenter implements EventListener. Whenever the server sends events
 * to the Repository, it passes those events to the Presenter via EventListener.
 */
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
    private final EventListener mViewEventListener;

    /**
     * Use this constructor to create a new ChatPresenter.
     *
     * @param view              {@link ChatContract.View}
     * @param eventListener     {@link EventListener} listens to server events.
     * @param schedulerProvider {@link BaseSchedulerProvider}
     * @param repository        {@link Repository}
     */
    public ChatPresenter(@NonNull ChatContract.View view,
                         @NonNull EventListener eventListener,
                         @NonNull BaseSchedulerProvider schedulerProvider,
                         @NonNull Repository repository) {
        mView = view;
        mViewEventListener = eventListener;
        mSchedulerProvider = schedulerProvider;
        mRepository = repository;

        // Setting the view's eventListener in the repository so that
        // when server sends events to repository, it passes the
        // events to the view
        mRepository.setEventListener(this);

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

    @Override
    public void onConnect(Object... args) {
        mViewEventListener.onConnect(args);
    }

    @Override
    public void onDisconnect(Object... args) {
        mViewEventListener.onDisconnect(args);
    }

    @Override
    public void onConnectError(Object... args) {
        mViewEventListener.onConnectError(args);
    }

    @Override
    public void onConnectTimeout(Object... args) {
        mViewEventListener.onConnectTimeout(args);
    }

    @Override
    public void onNewMessage(Object... args) {
        mViewEventListener.onNewMessage(args);
    }

    @Override
    public void onUserJoined(Object... args) {
        mViewEventListener.onUserJoined(args);
    }

    @Override
    public void onUserLeft(Object... args) {
        mViewEventListener.onUserLeft(args);
    }

    @Override
    public void onTyping(Object... args) {
        mViewEventListener.onTyping(args);
    }

    @Override
    public void onStopTyping(Object... args) {
        mViewEventListener.onStopTyping(args);
    }
}
