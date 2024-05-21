package com.example.scriptumapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class BookAdapterGift extends FirestoreRecyclerAdapter<Book, BookAdapterGift.ViewHolder> {

    private FragmentManager fragmentManager; //Instanciamos para cambiar de Fragment
    //Le pasamos el parametro al constructor
    public BookAdapterGift(@NonNull FirestoreRecyclerOptions<Book> options, FragmentManager fragmentManager, String idUser ) {
        super(options);
        this.fragmentManager = fragmentManager;
    }

    public BookAdapterGift(@NonNull FirestoreRecyclerOptions<Book> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Book Book) {
        viewHolder.title.setText(Book.getTitle());
        viewHolder.author.setText(Book.getAuthor());
        viewHolder.status.setText(Book.getStatus());
        Picasso.get().load(Book.getPhoto()).into(viewHolder.photo);

        viewHolder.imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cambiamos de fragment


            }
        });
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
        ImageButton imageButtonEdit, imageButtonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemTitleText);
            author = itemView.findViewById(R.id.itemAuthorText);
            status = itemView.findViewById(R.id.itemStatusText);
            photo = itemView.findViewById(R.id.itemImageView);

            imageButtonEdit = itemView.findViewById(R.id.imageButtonEditBook);
            imageButtonDelete = itemView.findViewById(R.id.imageButtonDeleteBook);

        }

        /*
        viewHolder.imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiamos de fragment a BookEditFragment
                BookEditFragment bookEditFragment = BookEditFragment.newInstance(Book , idUser, getSnapshots().getSnapshot(i).getId());
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, bookEditFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
            */
    }

}
