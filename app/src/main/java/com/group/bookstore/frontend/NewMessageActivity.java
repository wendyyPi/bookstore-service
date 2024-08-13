package com.group.bookstore.frontend;
/**
 * @author Fangzhou Liu
 */
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.group.bookstore.R;
import com.group.bookstore.backend.UserManager;

public class NewMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        Button send = findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText message_receiver = findViewById(R.id.message_receiver);
                EditText message_content = findViewById(R.id.message_content);
                String receiver = message_receiver.getText().toString().trim();
                String content = message_content.getText().toString().trim();
                if(!UserManager.getInstance().getCurrentUser().sendMessage(receiver, content)){
                    Toast.makeText(NewMessageActivity.this,"No such user",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewMessageActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }
}