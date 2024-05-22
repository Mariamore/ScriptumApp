package com.example.scriptumapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class BookAdapterGift extends FirestoreRecyclerAdapter<Book, BookAdapterGift.ViewHolder> {

    private FragmentManager fragmentManager; //Instanciamos para cambiar de Fragment
    //Le pasamos el parametro al constructor
    public BookAdapterGift(@NonNull FirestoreRecyclerOptions<Book> options, FragmentManager fragmentManager ) {
        super(options);
        this.fragmentManager = fragmentManager;
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position, @NonNull Book Book) {
        viewHolder.title.setText(Book.getTitle());
        viewHolder.author.setText(Book.getAuthor());
        viewHolder.status.setText(Book.getStatus());
        Picasso.get().load(Book.getPhoto()).into(viewHolder.photo);

        viewHolder.imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtenemos id
                String docId = getSnapshots().getSnapshot(position).getId();
                BookEditFragment bookEditFragment = BookEditFragment.newInstance(docId);
                viewHolder.replaceFragment(bookEditFragment);

            }
        });
    }


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

    //cambiar de fragment
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // Usa el ID del contenedor de fragmentos
        transaction.addToBackStack(null);
        transaction.commit();
    }



    }

}
