package com.group.bookstore.frontend;
/**
 * @author Fangzhou Liu
 */
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.group.bookstore.backend.Message;

import com.group.bookstore.R;
import com.group.bookstore.backend.UserManager;

public class MessageActivity extends AppCompatActivity {
    private Message message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        TextView sender = findViewById(R.id.sender);
        TextView receiver = findViewById(R.id.receiver);
        TextView content = findViewById(R.id.messageContent);
        message = (Message) getIntent().getExtras().getSerializable("message");
        sender.setText(getIntent().getExtras().getString("addresser"));
        receiver.setText(getIntent().getExtras().getString("receiver"));
        content.setText(getIntent().getExtras().getString("content"));
        UserManager.upLoad();

        Button delete = findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setTitle("Delete message?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int index = getIntent().getExtras().getInt("index");
                        UserManager.getInstance().getCurrentUser().deleteMessage(index);
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
        });

    }
}
