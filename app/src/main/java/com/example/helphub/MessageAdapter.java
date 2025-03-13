package com.example.helphub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;
    
    private List<MessageItem> messages;

    public MessageAdapter(List<MessageItem> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        MessageItem message = messages.get(position);
        return message.isFromMe() ? VIEW_TYPE_MY_MESSAGE : VIEW_TYPE_OTHER_MESSAGE;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = viewType == VIEW_TYPE_MY_MESSAGE ? 
            R.layout.item_message_mine : R.layout.item_message_other;
            
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutRes, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageItem message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView senderText;
        private TextView messageText;
        private TextView timeText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.senderText);
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
        }

        public void bind(MessageItem message) {
            if (!message.isFromMe()) {
                senderText.setText(message.getSender());
                senderText.setVisibility(View.VISIBLE);
            } else {
                senderText.setVisibility(View.GONE);
            }
            messageText.setText(message.getContent());
            timeText.setText(message.getTime());
        }
    }
} 