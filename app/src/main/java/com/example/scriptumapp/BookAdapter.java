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

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final FirestoreRecyclerAdapter<Book, BookViewHolder> loanAdapter;
    private final FirestoreRecyclerAdapter<Book, BookViewHolder> exchangeAdapter;
    private final FirestoreRecyclerAdapter<Book, BookViewHolder> giftAdapter;

    public BookAdapter(FirestoreRecyclerOptions<Book> loanOptions, FirestoreRecyclerOptions<Book> exchangeOptions, FirestoreRecyclerOptions<Book> giftOptions) {
        this.loanAdapter = new FirestoreRecyclerAdapter<Book, BookViewHolder>(loanOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Book model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_view_book_single, parent, false);
                return new BookViewHolder(view);
            }
        };

        this.exchangeAdapter = new FirestoreRecyclerAdapter<Book, BookViewHolder>(exchangeOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Book model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_view_book_single, parent, false);
                return new BookViewHolder(view);
            }
        };

        this.giftAdapter = new FirestoreRecyclerAdapter<Book, BookViewHolder>(giftOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Book model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_view_book_single, parent, false);
                return new BookViewHolder(view);
            }
        };
    }

    @Override
    public int getItemCount() {
        return loanAdapter.getItemCount() + exchangeAdapter.getItemCount() + giftAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < loanAdapter.getItemCount()) {
            return 0;
        } else if (position < loanAdapter.getItemCount() + exchangeAdapter.getItemCount()) {
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_view_book_single, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            loanAdapter.onBindViewHolder(holder, position);
        } else if (getItemViewType(position) == 1) {
            exchangeAdapter.onBindViewHolder(holder, position - loanAdapter.getItemCount());
        } else {
            giftAdapter.onBindViewHolder(holder, position - loanAdapter.getItemCount() - exchangeAdapter.getItemCount());
        }
    }

    public void startListening() {
        loanAdapter.startListening();
        exchangeAdapter.startListening();
        giftAdapter.startListening();
    }

    public void stopListening() {
        loanAdapter.stopListening();
        exchangeAdapter.stopListening();
        giftAdapter.stopListening();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, editorial, year, status;
        ImageView photo;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitleText);
            author = itemView.findViewById(R.id.itemAuthorText);
            status = itemView.findViewById(R.id.itemStatusText);
            photo = itemView.findViewById(R.id.itemImageView);
        }

        public void bind(Book book) {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            status.setText(book.getStatus());
            Picasso.get().load(book.getPhoto()).into(photo);
        }
    }
}