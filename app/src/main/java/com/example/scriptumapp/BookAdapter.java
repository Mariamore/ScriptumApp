package com.example.scriptumapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class BookAdapter extends FirestoreRecyclerAdapter<Book, BookAdapter.ViewHolder> {


    public BookAdapter(@NonNull FirestoreRecyclerOptions<Book> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookAdapter.ViewHolder viewHolder, int i, @NonNull Book book) {
        viewHolder.titleTextView.setText(book.getTitle());
        viewHolder.authorTextView.setText(book.getAuthor());
        viewHolder.statusTextView.setText(book.getStatus());
        Picasso.get().load(book.getPhoto()).into(viewHolder.photoImageView);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from (parent.getContext()).inflate(R.layout.fragment_view_book_single, parent, false);
        return new ViewHolder(v);

    }


        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;
            TextView authorTextView;
            TextView statusTextView;
            ImageView photoImageView;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.itemTitleText);
                authorTextView = itemView.findViewById(R.id.itemAuthorText);
                statusTextView = itemView.findViewById(R.id.itemStatusText);
                photoImageView = itemView.findViewById(R.id.itemImageView);
            }
        }
}

