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

package com.mayurrokade.chatapp.data.source;

import android.support.annotation.NonNull;

import com.mayurrokade.chatapp.data.ChatMessage;
import com.mayurrokade.chatapp.eventservice.EventListener;

import java.net.URISyntaxException;

import io.reactivex.Flowable;

/**
 * Repository can send events to a remote data source and can listen
 * for incoming events from remote data source as well. This bidirectional
 * flow of events is what makes the app realtime.
 *
 * Repository implements {@link DataSource} which can send and receive events.
 */
public class Repository implements DataSource {

    private static Repository INSTANCE = null;
    private final DataSource mRemoteDataSource;
    private final DataSource mLocalDataSource;
    private EventListener mPresenterEventListener;

    // Prevent direct instantiation
    private Repository(@NonNull DataSource remoteDataSource,
                       @NonNull DataSource localDataSource) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
        mRemoteDataSource.setEventListener(this);
    }

    /**
     * Returns single instance of this class, creating it if necessary.
     *
     * @param remoteDataSource
     * @param localDataSource
     * @return
     */
    public static Repository getInstance(@NonNull DataSource remoteDataSource,
                                         @NonNull DataSource localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new Repository(remoteDataSource, localDataSource);
        }

        return INSTANCE;
    }

    @Override
    public void setEventListener(EventListener eventListener) {
        mPresenterEventListener = eventListener;
    }

    /**
     * Connect to remote chat server.
     *
     * @param username
     * @throws URISyntaxException
     */
    @Override
    public void connect(String username) throws URISyntaxException {
        mRemoteDataSource.connect(username);
    }

    /**
     * Disconnect from remote chat server.
     *
     */
    @Override
    public void disconnect() {
        mRemoteDataSource.disconnect();
    }

    /**
     * Send chat message.
     *
     * @param chatMessage
     * @return
     */
    @Override
    public Flowable<ChatMessage> sendMessage(ChatMessage chatMessage) {
        return mRemoteDataSource.sendMessage(chatMessage);
    }

    @Override
    public void onTyping() {
        mRemoteDataSource.onTyping();
    }

    @Override
    public void onStopTyping() {
        mRemoteDataSource.onStopTyping();
    }

    @Override
    public void onConnect(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onConnect(args);
    }

    @Override
    public void onDisconnect(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onDisconnect(args);
    }

    @Override
    public void onConnectError(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onConnectError(args);
    }

    @Override
    public void onConnectTimeout(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onConnectTimeout(args);
    }

    @Override
    public void onNewMessage(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onNewMessage(args);
    }

    @Override
    public void onUserJoined(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onUserJoined(args);
    }

    @Override
    public void onUserLeft(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onUserLeft(args);
    }

    @Override
    public void onTyping(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onTyping(args);
    }

    @Override
    public void onStopTyping(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onStopTyping(args);
    }
}
