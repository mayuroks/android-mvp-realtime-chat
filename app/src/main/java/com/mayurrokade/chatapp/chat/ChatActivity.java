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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mayurrokade.chatapp.R;
import com.mayurrokade.chatapp.about.AboutActivity;
import com.mayurrokade.chatapp.data.ChatMessage;
import com.mayurrokade.chatapp.eventservice.EventListener;
import com.mayurrokade.chatapp.util.Injection;
import com.mayurrokade.chatapp.util.TextUtils;
import com.mayurrokade.chatapp.util.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity
        extends AppCompatActivity
        implements ChatContract.View, EventListener {

    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final long TYPING_TIMER_LENGTH = 3000;
    private static final long ALERT_LENGTH = 2000;
    private RecyclerView rvChatMessages;
    private RecyclerView.LayoutManager mLayoutManager;
    private ChatMessagesAdapter mChatMessagesAdapter;
    private EditText etSendMessage;
    private ImageView ivSendMessage;
    private LinearLayout llTyping;
    private TextView tvTyping, tvAlert;
    private ChatContract.Presenter mPresenter;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private int mAlerterHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new ChatPresenter(this, this,
                Injection.provideSchedulerProvider(),
                Injection.providesRepository(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_name:
                askUsername();
                break;
            case R.id.info:
                showInfo();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initView() {
        // Init UI elements
        rvChatMessages = findViewById(R.id.rvChatMessages);
        etSendMessage = findViewById(R.id.etSendMessage);
        ivSendMessage = findViewById(R.id.btnSendMessage);
        tvTyping = findViewById(R.id.tvTyping);
        llTyping = findViewById(R.id.llTyping);
        tvAlert = findViewById(R.id.tvAlert);
        tvAlert.setTranslationY(-100);

        getSupportActionBar().setTitle("Realtime MVP Chat");

        // Ask the user to set a username,
        // when the app opens up.
        if (!User.isUsernameUpdated()) {
            askUsername();
        }

        setupChatMessages();
        setupSendButton();
        setupTextWatcher();
    }

    @Override
    public void setPresenter(ChatContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showAlert(final String message, final boolean isError) {
        Log.i(TAG, "showAlert: " + message);
        final int successColor = ContextCompat.getColor(this, R.color.colorSuccess);
        final int errorColor = ContextCompat.getColor(this, R.color.colorError);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvAlert.setText(message);

                if (isError) {
                    tvAlert.setBackgroundColor(errorColor);
                } else {
                    tvAlert.setBackgroundColor(successColor);
                }

                mAlerterHeight = tvAlert.getHeight();
                tvAlert.setTranslationY(-1 * mAlerterHeight);

                tvAlert.animate()
                        .translationY(0)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideAlert();
                                    }
                                }, ALERT_LENGTH);
                            }
                        });
            }
        });
    }

    @Override
    public void hideAlert() {
        tvAlert.animate()
                .translationY(-1 * mAlerterHeight)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        tvAlert.setText("");
                    }
                });
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    private void setupChatMessages() {
        mChatMessagesAdapter = new ChatMessagesAdapter(new ArrayList<ChatMessage>(), this);
        mLayoutManager = new LinearLayoutManager(this);
        rvChatMessages.setAdapter(mChatMessagesAdapter);
        rvChatMessages.setLayoutManager(mLayoutManager);
    }

    private void setupSendButton() {
        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = etSendMessage.getText().toString().trim();

        if (TextUtils.isValidString(message)) {
            ChatMessage chatMessage = new ChatMessage(
                    User.getUsername(), message, ChatMessage.TYPE_MESSAGE_SENT);
            mPresenter.sendMessage(chatMessage);
            addMessage(chatMessage);
            etSendMessage.setText("");
        }
    }

    private void addMessage(ChatMessage chatMessage) {
        mChatMessagesAdapter.addNewMessage(chatMessage);
        rvChatMessages.scrollToPosition(mChatMessagesAdapter.getItemCount() - 1);
    }

    private void askUsername() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(
                R.layout.dialog_set_username, null);
        final AlertDialog dialog = builder.setView(view).setCancelable(false).create();

        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnClose = view.findViewById(R.id.btnClose);
        final EditText etUsername = view.findViewById(R.id.etUsername);
        etUsername.setText(User.getUsername());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString().trim();
                if (TextUtils.isValidString(username)) {
                    mPresenter.changeUsername(username);
                    dialog.dismiss();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showInfo() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    private void setupTextWatcher() {
        etSendMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!mTyping) {
                    mTyping = true;
                    mPresenter.onTyping();
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onConnect(final Object... args) {
        showAlert("Connected", false);
    }

    @Override
    public void onDisconnect(final Object... args) {
        showAlert("Disconnected", false);
    }

    @Override
    public void onConnectError(final Object... args) {
        showAlert("No internet connection", true);
    }

    @Override
    public void onConnectTimeout(final Object... args) {
        showAlert("Connection Timeout", false);

    }

    @Override
    public void onNewMessage(final Object... args) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject data = (JSONObject) args[0];
                String username;
                String message;
                try {
                    username = data.getString("username");
                    message = data.getString("message");
                    ChatMessage chatMessage = new ChatMessage(
                            username, message, ChatMessage.TYPE_MESSAGE_RECEIVED);
                    addMessage(chatMessage);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    return;
                }
            }
        });
    }

    @Override
    public void onUserJoined(Object... args) {
        JSONObject data = (JSONObject) args[0];
        String username;
        int numUsers;
        try {
            username = data.getString("username");
            numUsers = data.getInt("numUsers");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        showAlert(username + " has joined", false);
    }

    @Override
    public void onUserLeft(Object... args) {
        JSONObject data = (JSONObject) args[0];
        String username;
        int numUsers;
        try {
            username = data.getString("username");
            numUsers = data.getInt("numUsers");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        showAlert(username + " has left", false);
    }

    @Override
    public void onTyping(Object... args) {
        JSONObject data = (JSONObject) args[0];
        String username;
        try {
            username = data.getString("username");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        addTyping(username);
        Log.i(TAG, "onTyping: " + username);
    }

    @Override
    public void onStopTyping(Object... args) {
        JSONObject data = (JSONObject) args[0];
        String username;
        try {
            username = data.getString("username");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            return;
        }
        removeTyping(username);
    }

    @Override
    public void onMessageDelivered(ChatMessage chatMessage) {
        // Update UI to show the message has been delivered
    }

    @Override
    public void updateUsername(String username) {
        User.setUsername(username);
    }

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            mPresenter.onStopTyping();
        }
    };

    private void addTyping(final String username) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTyping.setText(username + " is typing");
                llTyping.setVisibility(View.VISIBLE);
            }
        });
    }

    private void removeTyping(String username) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTyping.setText("");
                llTyping.setVisibility(View.GONE);
            }
        });
    }
}