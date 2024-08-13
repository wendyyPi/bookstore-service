package com.group.bookstore.frontend;
/**
 * @author Fangzhou Liu
 */
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.group.bookstore.R;
import com.group.bookstore.backend.ActivityObserver;
import com.group.bookstore.backend.User;
import com.group.bookstore.backend.UserManager;

import java.util.ArrayList;

public class BlackListActivity extends ActivityObserver {
    ListView listView;
    ArrayList<String> blacklist;
    ArrayAdapter<String> adapter;
    AdapterView.OnItemClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        if (UserManager.getInstance().getCurrentUser() != null)
            blacklist = new ArrayList<>(UserManager.getInstance().getCurrentUser().getBlackList());
        else
            blacklist = new ArrayList<>();

        listView = (ListView) findViewById(R.id.blacklist);

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, blacklist);
        listView.setAdapter(adapter);

        Button add = findViewById(R.id.add_block);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BlackListActivity.this);
                builder.setTitle("Block user");
                final EditText input = new EditText(getApplicationContext());
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String userInput = input.getText().toString();
                        User user = UserManager.getInstance().get(userInput);
                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "No such user", Toast.LENGTH_SHORT).show();
                        } else {
                            UserManager.getInstance().getCurrentUser().blockUser(user);
                            UserManager.upLoad();
                            finish();
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });
        listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BlackListActivity.this);
                builder.setTitle("Unblock user?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int h) {
                        if (i < 0)
                            return;
                        User user = UserManager.getInstance().get(blacklist.get(i));
                        if (user != null)
                            UserManager.getInstance().getCurrentUser().unBlockUser(user);
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        };
        listView.setOnItemClickListener(listener);
    }

    @Override
    public void update() {
        if (UserManager.getInstance().getCurrentUser() != null)
            blacklist = new ArrayList<>(UserManager.getInstance().getCurrentUser().getBlackList());
        else
            blacklist = new ArrayList<>();

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, blacklist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
    }
}