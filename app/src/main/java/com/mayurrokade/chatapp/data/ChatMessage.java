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

package com.mayurrokade.chatapp.data;

/**
 * ChatMessage model
 */
public class ChatMessage {
    public static final int TYPE_MESSAGE_SENT = 393;
    public static final int TYPE_MESSAGE_RECEIVED = 529;

    private String username;
    private String message;
    private int type;

    /**
     * Use this constructor to create a new ChatMessage.
     *
     * @param username      Username of the user
     * @param message       The text message user wants to send
     * @param type          Type of message. Whether it's a SENT or RECEIVED message
     */
    public ChatMessage(String username, String message, int type) {
        this.username = username;
        this.message = message;
        this.type = type;
    }

    /**
     * Get username from the chat message.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username for the chat message.
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get text message from the chat message.
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set text message for the chat message.
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get type of the chat message.
     *
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * Set type for the chat message.
     *
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }
}
