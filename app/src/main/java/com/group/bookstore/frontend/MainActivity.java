package com.group.bookstore.frontend;

/**
 * @author Ziyu Wang
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.group.bookstore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import com.group.bookstore.backend.Book;
import com.group.bookstore.backend.User;
import com.group.bookstore.backend.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private EditText userName;
    private EditText passWord;
    private Button login;

    private Button signup;
    private FirebaseAuth mAuth;
    private TextView signupPrompt;

    private String userNameStr;
    private String passWordStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        UserManager.init();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();
        delete_input();

        userName = (EditText) findViewById(R.id.Lemail_signup);
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userNameStr = editable.toString();
            }
        });

        passWord = (EditText) findViewById(R.id.Lpassword_signup);
        passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passWordStr = editable.toString();
            }
        });

        login = (Button) findViewById(R.id.Llogin_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if((userNameStr.equals("comp2100@anu.edu.au") && passWordStr.equals("comp2100")) || (userNameStr.equals("comp6442@anu.edu.au") && passWordStr.equals("comp6442"))){
//                    Toast.makeText(MainActivity.this,"Login success",Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                    startActivity(intent);
//                }else{
//                    Toast.makeText(MainActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
//                }
                login();
            }
        });

        signupPrompt = findViewById(R.id.LSignUp_prompt);

        signupPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });





    }



    private void login(){
        String user = userName.getText().toString().trim();
        String pass = passWord.getText().toString().trim();

        if(user.isEmpty()||pass.isEmpty()){
            Toast.makeText(this,"Login Detail cannot be empty",Toast.LENGTH_SHORT).show();
        }else{
            mAuth.signInWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        for(User userInManager: UserManager.getInstance().getUsers()) {
                            if(user.equals(userInManager.getName())){
                                UserManager.setCurrentUser(userInManager);
                                System.out.println("Current user: " + UserManager.getInstance().getCurrentUser());
                            }
                        }

//                        InputStream inputStream = MainActivity.this.getResources().openRawResource(R.raw.users);
//                        FirebaseReference synchronizeData= FirebaseReference.getInstance();
//                        synchronizeData.readJsonFile(inputStream);
//                        synchronizeData.upLoad();
//                        synchronizeData.downLoad();
                        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this,"LOGIN Successfully",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this,"LOGIN failed",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    JSONObject jsonObject;
    String json_String;


    public void readJsonFile(Context context){

        InputStream inputStream = context.getResources().openRawResource(R.raw.bookdata);


//        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//        // 获取资源ID
//        int resourceId = getResources().getIdentifier("my_json_file", "raw", getPackageName());
//
//// 使用资源ID打开输入流
//        InputStream inputStream = getResources().openRawResource(resourceId);

// 读取文件内容并进行处理
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            json_String=jsonString.toString();

            // 现在你可以将JSON字符串转换为JSONObject
            jsonObject = new JSONObject(jsonString.toString());

            // 处理JSONObject
            // 例如：String value = jsonObject.getString("keyName");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    public void upLoad(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bookstore-60867-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference messageRef = database.getReference("message");


// 准备要上传的数据，这里假设您有一个名为 "messageData" 的 JSON 数据字符串
        String messageData = json_String;

// 使用 setValue() 将数据上传到 "message" 路径
        messageRef.setValue(messageData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 数据成功上传到数据库
                        Log.d("Fire", "Data uploaded successfully to 'message' path");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 数据上传失败
                        Log.e("Fire", "Data upload failed: " + e.getMessage());
                    }
                });

    }
    Book[] books;

    public void downLoad(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bookstore-60867-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference messageRef = database.getReference("message");

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 从数据库中加载数据
                    String messageData = dataSnapshot.getValue(String.class); // 假设数据是字符串



                    Gson gson = new GsonBuilder().create();

                    try{
                        books = gson.fromJson(messageData, Book[].class);
                    }catch (Exception e){
                        Log.d("jsonparse","failed");
                    }

                    System.out.println(books.length);
                    // 在这里处理加载的数据
                    Log.d("Firebase", "Loaded message data: " + messageData);

                } else {
                    // 数据路径 "message" 不存在数据
                    Log.d("Firebase", "No data found at 'message' path");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 加载数据时发生错误
                Log.e("Firebase", "Data load error: " + databaseError.getMessage());
            }
        });

    }

    private void delete_input() {
        userName = (EditText) findViewById(R.id.Lemail_signup);
        passWord = (EditText) findViewById(R.id.Lpassword_signup);
        ImageView emailDelete_sign = findViewById(R.id.Ldel_email_sign);
        ImageView passwordDelete_sign = findViewById(R.id.Ldel_password_sign);
        EditTextClearTools.addclerListener(userName, emailDelete_sign);
        EditTextClearTools.addclerListener(passWord, passwordDelete_sign);
    }
}