package com.group.bookstore.frontend;
/**
 * @author Fangzhou Liu
 */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.group.bookstore.R;
import com.group.bookstore.backend.Message;
import com.group.bookstore.backend.ActivityObserver;
import com.group.bookstore.backend.UserManager;
import com.group.bookstore.frontend.adapter.MessageAdapter;

import java.util.ArrayList;

public class MessageListActivity extends ActivityObserver {

    ListView listView;
    ArrayList<Message> messages = new ArrayList<>();
    MessageAdapter adapter;
    AdapterView.OnItemClickListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        UserManager.getInstance().addObserver(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        listView = findViewById(R.id.message_list);
        if (UserManager.getInstance().getCurrentUser() != null)
            messages = new ArrayList<>(UserManager.getInstance().getCurrentUser().getMessages());
        else
            messages = new ArrayList<>();
        adapter = new MessageAdapter(this,messages);
        listView.setAdapter(adapter);

        Button new_message = findViewById(R.id.new_message);
        new_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageListActivity.this, NewMessageActivity.class);
                startActivity(intent);
            }
        });

        listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Message message = messages.get(i);
                message.openMessage();

                Intent intent = new Intent(MessageListActivity.this, MessageActivity.class);
                intent.putExtra("addresser", message.getAddresser());
                intent.putExtra("receiver", message.getReceiver());
                intent.putExtra("content", message.getMessageContent());
                intent.putExtra("message", message);
                intent.putExtra("index", i);
                startActivity(intent);
            }
        };
        listView.setOnItemClickListener(listener);

        Button block = findViewById(R.id.block);
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageListActivity.this, BlackListActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void update() {
        if (UserManager.getInstance().getCurrentUser() != null)
            messages = new ArrayList<>(UserManager.getInstance().getCurrentUser().getMessages());
        else
            messages = new ArrayList<>();
        adapter = new MessageAdapter(this,messages);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
    }
}
