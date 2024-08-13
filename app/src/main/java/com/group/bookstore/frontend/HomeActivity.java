package com.group.bookstore.frontend;

/**
 * @author Yusen Nian
 */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.group.bookstore.R;
import com.group.bookstore.backend.ActivityObserver;
import com.group.bookstore.backend.UserManager;

public class HomeActivity extends ActivityObserver {

    private Button search;
    private int unreadNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        UserManager.getInstance().addObserver(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView unreadCount = findViewById(R.id.unread_count);
        if (UserManager.getInstance().getCurrentUser() != null)
            unreadNum = UserManager.getInstance().getCurrentUser().unreadNum();
        if (unreadNum > 0) {
            unreadCount.setText(String.valueOf(unreadNum));
            unreadCount.setVisibility(View.VISIBLE);
        } else {
            unreadCount.setVisibility(View.GONE);
        }

        search = (Button) findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        Button chat_button = findViewById(R.id.chat_button);
        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MessageListActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void update() {
        TextView unreadCount = findViewById(R.id.unread_count);
        if (UserManager.getInstance().getCurrentUser() != null) {
            unreadNum = UserManager.getInstance().getCurrentUser().unreadNum();
            if (unreadNum > 0) {
                unreadCount.setText(String.valueOf(unreadNum));
                unreadCount.setVisibility(View.VISIBLE);
            } else {
                unreadCount.setVisibility(View.GONE);
            }
        }
    }
}
