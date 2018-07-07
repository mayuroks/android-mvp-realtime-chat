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
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mayurrokade.chatapp.R;
import com.mayurrokade.chatapp.data.ChatMessage;

import java.util.List;

public class ChatMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> mItems;
    private Context mContext;

    /**
     * Constructor to create a new ChatMessagesAdapter
     *
     * @param items
     * @param context
     */
    public ChatMessagesAdapter(List<ChatMessage> items, Context context) {
        mItems = items;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;

        if (viewType == ChatMessage.TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_message_received, parent, false);
            viewHolder = new ReceivedMessageViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_message_sent, parent, false);
            viewHolder = new SentMessageViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = mItems.get(position);

        if (chatMessage.getType() == ChatMessage.TYPE_MESSAGE_RECEIVED) {
            ((ReceivedMessageViewHolder) holder).tvUsername.setText(chatMessage.getUsername());
            ((ReceivedMessageViewHolder) holder).tvMessage.setText(chatMessage.getMessage());
        } else {
            ((SentMessageViewHolder) holder).tvUsername.setText(chatMessage.getUsername());
            ((SentMessageViewHolder) holder).tvMessage.setText(chatMessage.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

    public void addNewMessage(@NonNull ChatMessage chatMessage) {
        mItems.add(chatMessage);
        notifyItemInserted(mItems.size() - 1);
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvMessage;

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvMessage;

        public SentMessageViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
