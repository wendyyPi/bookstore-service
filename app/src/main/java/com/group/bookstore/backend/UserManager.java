package com.group.bookstore.backend;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * This class is to sync user and message data with Firebase
 * @author Ziyu Wang for upload and download method
 * @author Fangzhou Liu for other method
 */
public class UserManager {
    private static ArrayList<User> users;
    private static User currentUser;
    private static UserManager instance = null;
    private static HashSet<ActivityObserver> activityObservers = new HashSet<>();

    public static void setCurrentUser(User user){
        currentUser = user;
    }
    private UserManager() {
        downLoad();
    }

    public static void init(){
        downLoad();
    }

    public void addObserver(ActivityObserver activityObserver){
        activityObservers.add(activityObserver);
    }

    public static void update(String json) {
        Gson gson = new Gson();
        ArrayList<User> new_users = new ArrayList<>(Arrays.asList(gson.fromJson(json, User[].class)));
        System.out.println("Users: " + users);
        for(User user: new_users){
            if(currentUser != null && currentUser.getName().equals(user.getName())){
                currentUser = user;
            }
        }
        users = new_users;
        for(ActivityObserver activityObserver : activityObservers){
            activityObserver.update();
        }
    }
    public static UserManager getInstance() {
        if(instance == null)
            instance = new UserManager();
        return instance;
    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public User getCurrentUser(){
        return currentUser;
    }

    public User get(String userName) {
        for(User user: users){
            if(user.getName().equals(userName))
                return user;
        }
        return null;
    }


    public static void upLoad(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bookstore-60867-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference messageRef = database.getReference("message");


// 准备要上传的数据，这里假设您有一个名为 "messageData" 的 JSON 数据字符串
        Gson gson = new Gson();
        String messageData = gson.toJson(users);

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

    public static void downLoad(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bookstore-60867-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference messageRef = database.getReference("message");

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 从数据库中加载数据
                    String messageData = dataSnapshot.getValue(String.class); // 假设数据是字符串
                    Gson gson = new GsonBuilder().create();

                    System.out.println("Update message: " + messageData);
                    update(messageData);


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




}
