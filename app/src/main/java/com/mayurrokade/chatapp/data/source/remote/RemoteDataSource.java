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

package com.mayurrokade.chatapp.data.source.remote;

import com.mayurrokade.chatapp.data.ChatMessage;
import com.mayurrokade.chatapp.data.source.DataSource;
import com.mayurrokade.chatapp.eventservice.EventListener;
import com.mayurrokade.chatapp.eventservice.EventService;
import com.mayurrokade.chatapp.eventservice.EventServiceImpl;

import io.reactivex.Flowable;

public class RemoteDataSource implements DataSource {

    private static RemoteDataSource INSTANCE;
    private static EventService mEventService = EventServiceImpl.getInstance();
    private EventListener mRepoEventListener;

    private RemoteDataSource() {
        mEventService.setEventListener(this);
    }

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }

        return INSTANCE;
    }

    @Override
    public void onConnect(Object... args) {
        if (mRepoEventListener != null)
            mRepoEventListener.onConnect(args);
    }

    @Override
    public void onDisconnect(Object... args) {
        if (mRepoEventListener != null)
            mRepoEventListener.onDisconnect(args);
    }

    @Override
    public void onConnectError(Object... args) {
        if (mRepoEventListener != null)
            mRepoEventListener.onConnectError(args);
    }

    @Override
    public void onConnectTimeout(Object... args) {
        if (mRepoEventListener != null)
            mRepoEventListener.onConnectTimeout(args);
    }

    @Override
    public void onNewMessage(Object... args) {
        if (mRepoEventListener != null)
            mRepoEventListener.onNewMessage(args);
    }

    @Override
    public void setEventListener(EventListener eventListener) {
        mRepoEventListener = eventListener;
    }

    @Override
    public Flowable<ChatMessage> sendMessage(ChatMessage chatMessage) {
        return mEventService.sendMessage(chatMessage);
    }
}
