package com.group.bookstore.frontend.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.group.bookstore.R;
import com.group.bookstore.backend.Book;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    Context context;
    List<Book> booksList;

    public BookAdapter(Context context, List<Book> list) {
        super(context, R.layout.single_item, list);
        this.context = context;
        this.booksList = list;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView authorView;
        TextView dateView;
        TextView priceView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.titleView = convertView.findViewById(R.id.title);
            holder.authorView = convertView.findViewById(R.id.author);
            holder.dateView = convertView.findViewById(R.id.date);
            holder.priceView = convertView.findViewById(R.id.price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Book currentBook = booksList.get(position);

        Bitmap bookImage = getBookImage(currentBook.getId());
        if (bookImage != null) {
            holder.imageView.setImageBitmap(bookImage);
        }

        holder.titleView.setText(currentBook.getTitle());
        holder.authorView.setText(currentBook.getAuthor());
        holder.dateView.setText(currentBook.getDate());
        holder.priceView.setText("$" + currentBook.getPrice());

        return convertView;
    }

    public void updateData(List<Book> newBooks) {
        this.booksList.clear();
        this.booksList.addAll(newBooks);
        notifyDataSetChanged();
    }

    private Bitmap getBookImage(int bookId) {
        Bitmap bitmap = null;
        String imagePath = "img/" + bookId + ".jpg";
        try {
            InputStream is = context.getAssets().open(imagePath);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
