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

public class BookAdapterLoan extends FirestoreRecyclerAdapter<Book, BookAdapterLoan.ViewHolder> {


    public BookAdapterLoan(@NonNull FirestoreRecyclerOptions<Book> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Book Book) {
        viewHolder.title.setText(Book.getTitle());
        viewHolder.author.setText(Book.getAuthor());
        viewHolder.status.setText(Book.getStatus());
        Picasso.get().load(Book.getPhoto()).into(viewHolder.photo);
    }

    //mostrar datos
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_view_book_single, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, author, status;
        ImageView photo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemTitleText);
            author = itemView.findViewById(R.id.itemAuthorText);
            status = itemView.findViewById(R.id.itemStatusText);
            photo = itemView.findViewById(R.id.itemImageView);

        }
    }
}