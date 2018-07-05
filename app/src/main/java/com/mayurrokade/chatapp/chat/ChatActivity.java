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

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mayurrokade.chatapp.R;
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
    private RecyclerView rvChatMessages;
    private RecyclerView.LayoutManager mLayoutManager;
    private ChatAdapter mChatAdapter;
    private EditText etSendMessage;
    private ImageView ivSendMessage;
    private ChatContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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
        if (item.getItemId() == R.id.change_name) {
            showSetUsernameDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initView() {
        rvChatMessages = findViewById(R.id.rvChatMessages);
        etSendMessage = findViewById(R.id.etSendMessage);
        ivSendMessage = findViewById(R.id.btnSendMessage);

        if (TextUtils.isValidString(User.getUsername())) {
            String title = "Realtime MVP Chat";
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_logo_no_background);
        }

        setupChatMessages();
        setupSendButton();
    }

    @Override
    public void setPresenter(ChatContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showMessage(final String message, boolean isError) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        message,
                        Toast.LENGTH_SHORT)
                        .show();
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
        mChatAdapter = new ChatAdapter(new ArrayList<ChatMessage>(), this);
        mLayoutManager = new LinearLayoutManager(this);
        rvChatMessages.setAdapter(mChatAdapter);
        rvChatMessages.setLayoutManager(mLayoutManager);
    }

    private void setupSendButton() {
        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: sendMessage");
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = etSendMessage.getText().toString().trim();

        if (TextUtils.isValidString(message)) {
            Log.i(TAG, "sendMessage: ");
            ChatMessage chatMessage = new ChatMessage(
                    User.getUsername(), message, ChatMessage.TYPE_MESSAGE_SENT);
            mPresenter.sendMessage(chatMessage);
            addMessage(chatMessage);
            etSendMessage.setText("");
        }
    }

    private void addMessage(ChatMessage chatMessage) {
        mChatAdapter.addNewMessage(chatMessage);
        rvChatMessages.scrollToPosition(mChatAdapter.getItemCount() - 1);
    }

    private void showSetUsernameDialog() {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(
                R.layout.dialog_set_username, null);
        final AlertDialog dialog = builder.setView(view).create();

        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnClose = view.findViewById(R.id.btnClose);
        final EditText etUsername = view.findViewById(R.id.etUsername);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String username = etUsername.getText().toString().trim();
                if (TextUtils.isValidString(username)) {
                    mPresenter.changeUsername(username);
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

    @Override
    public void onConnect(final Object... args) {
    }

    @Override
    public void onDisconnect(final Object... args) {
    }

    @Override
    public void onConnectError(final Object... args) {
    }

    @Override
    public void onConnectTimeout(final Object... args) {
        showMessage("Connection Timeout", false);

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
    public void onMessageDelivered(ChatMessage chatMessage) {
        // Update UI to show the message has been delivered
    }

    @Override
    public void updateUsername(String username) {
        User.setUsername(username);
        getSupportActionBar().setTitle("Chatting as " + username);
    }
}