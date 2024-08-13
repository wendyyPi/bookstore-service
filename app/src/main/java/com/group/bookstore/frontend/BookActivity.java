package com.group.bookstore.frontend;
/**
 * @author Ziyu Wang
 */
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.group.bookstore.R;

import java.io.IOException;
import java.io.InputStream;

public class BookActivity extends AppCompatActivity {
    ImageView imageView;
    EditText Bauthor;
    EditText Btitle;
    EditText Bdate;
    EditText Blanguage;
    EditText Bprice;
    String date;
    String title;
    String author;
    String language;
    String bookshelves;
    int price;
    int id;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        date = intent.getStringExtra("date");
        title = intent.getStringExtra("title");
        author = intent.getStringExtra("author");
        language = intent.getStringExtra("language");
        bookshelves = intent.getStringExtra("bookshelves");
        price = intent.getIntExtra("price", 0);


        imageView = findViewById(R.id.imageView); // 获取ImageView的引用
        Bauthor=findViewById(R.id.Bauthor);
        Bprice=findViewById(R.id.Bprice);
        Blanguage=findViewById(R.id.Blanguage);
        Bdate=findViewById(R.id.Bdate);
        Btitle=findViewById(R.id.Btitle);

        Bauthor.setText("Author: "+author);
        Bprice.setText("Price: "+price);
        Blanguage.setText("Language: "+language);
        Bdate.setText("Date: "+date);
        Btitle.setText("Title: "+title);
        // 使用AssetManager加载图像
        AssetManager assetManager = getAssets();
        try {
            // "img/9.jpg" 是图像文件的路径，可以根据您的文件结构进行调整
            String fileName = "img/" + id + ".jpg"; // 根据id生成文件名
            InputStream inputStream = assetManager.open(fileName);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//    Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
//intent.putExtra("id", id);
//intent.putExtra("date", date);
//intent.putExtra("title", title);
//intent.putExtra("author", author);
//intent.putExtra("language", language);
//intent.putExtra("bookshelves", bookshelves);
//intent.putExtra("price", price);
//    startActivity(intent);
