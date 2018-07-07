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

package com.mayurrokade.chatapp.util;

import java.util.UUID;

/**
 * Utility to get and set username.
 */
public class User {

    public static final String USER_SUFFIX = "_User";
    private static boolean isUpdated = false;

    // Set random username
    private static String USER_NAME
            = UUID.randomUUID().toString().substring(0,5) + USER_SUFFIX;

    /**
     * Get username
     *
     * @return String username
     */
    public static String getUsername() {
        return USER_NAME;
    }

    /**
     * Set username
     *
     * @param String username
     */
    public static void setUsername(String username) {
        USER_NAME = username;
        isUpdated = true;
    }

    /**
     * Check if the username has been updated by the user
     *
     * @return boolean isUsernameUpdated
     */
    public static boolean isUsernameUpdated() {
        return isUpdated;
    }
}
