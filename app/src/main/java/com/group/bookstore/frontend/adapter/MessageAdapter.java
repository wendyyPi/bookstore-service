package com.group.bookstore.frontend.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.group.bookstore.R;
import com.group.bookstore.backend.Message;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    Context context;
    List<Message> messageList;
    public MessageAdapter(Context context, List<Message> list) {
        super(context, R.layout.single_message, list);
        this.context = context;
        this.messageList = list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.single_message, parent, false);
        }

        Message currentMessage = messageList.get(position);
        View unread = convertView.findViewById(R.id.unread_indicator);
        if (currentMessage.getStatus() == Message.Status.UNREAD){
            unread.setVisibility(View.VISIBLE);
        }
        else{
            unread.setVisibility(View.GONE);
        }

        // Fetching and setting other book details
        TextView senderView = convertView.findViewById(R.id.tvSenderName);
        TextView dateView = convertView.findViewById(R.id.tvDate);

        senderView.setText(currentMessage.getAddresser());
        dateView.setText(currentMessage.getDate());


        return convertView;
    }


}
