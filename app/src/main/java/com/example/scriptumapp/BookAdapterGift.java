package com.example.scriptumapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class BookAdapterGift extends FirestoreRecyclerAdapter<Book, BookAdapterGift.ViewHolder> {

    FirebaseFirestore mFirestore;
    private FragmentManager fragmentManager; //Instanciamos para cambiar de Fragment
    //Le pasamos el parametro al constructor
    public BookAdapterGift(@NonNull FirestoreRecyclerOptions<Book> options, FragmentManager fragmentManager ) {
        super(options);
        this.fragmentManager = fragmentManager;
    }


    /**
     * Vincula los datos al ViewHolder para una posición dada en el RecyclerView.
     *
     * @param viewHolder El ViewHolder que debe ser actualizado para representar el contenido del elemento en la posición dada.
     * @param position   La posición del elemento dentro del conjunto de datos del adaptador.
     * @param Book       El objeto Book que contiene los datos que se deben vincular al ViewHolder.
     */
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position, @NonNull Book Book) {
        viewHolder.title.setText(Book.getTitle());
        viewHolder.author.setText(Book.getAuthor());
        viewHolder.status.setText(Book.getStatus());

        // Carga la imagen del libro utilizando Picasso
        Picasso.get()
                .load(Book.getPhoto())
                .resize(200,300)
                .centerInside()
                .into(viewHolder.photo);

        // Configura el OnClickListener para el botón de edición
        viewHolder.imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtenemos id
                String docId = getSnapshots().getSnapshot(position).getId();
                BookEditFragment bookEditFragment = BookEditFragment.newInstance(docId);
                viewHolder.replaceFragment(bookEditFragment);

            }
        });

        // Configura el OnClickListener para el botón de eliminación
        viewHolder.imageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //id del libro
                String docId = getSnapshots().getSnapshot(position).getId();
                mFirestore.getInstance().collection("booksData").document(docId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(viewHolder.itemView.getContext(), "Book deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(viewHolder.itemView.getContext(), "Error book delete", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }


    /**
     * Crea un nuevo ViewHolder cuando el RecyclerView lo necesita.
     *
     * @param parent   El ViewGroup al que se añadirá la nueva vista después de ser vinculada a una posición del adaptador.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Un nuevo ViewHolder que contiene la vista para el elemento del RecyclerView.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_view_book_single, parent, false);
        return new ViewHolder(v);
    }


     //ViewHolder para el RecyclerView que representa un único libro.

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, author, status;
        ImageView photo;
        ImageButton imageButtonEdit, imageButtonDelete;

        /**
         * Constructor que inicializa las vistas de un elemento del RecyclerView.
         *
         * @param itemView La vista del elemento del RecyclerView.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemTitleText);
            author = itemView.findViewById(R.id.itemAuthorText);
            status = itemView.findViewById(R.id.itemStatusText);
            photo = itemView.findViewById(R.id.itemImageView);

            imageButtonEdit = itemView.findViewById(R.id.imageButtonEditBook);
            imageButtonDelete = itemView.findViewById(R.id.imageButtonDeleteBook);

        }

    /**
     * Reemplaza el fragmento actual con uno nuevo.
     *
     * @param fragment El nuevo fragmento que se mostrará.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment); // Usa el ID del contenedor de fragmentos
        transaction.addToBackStack(null);
        transaction.commit();
    }



    }

}
