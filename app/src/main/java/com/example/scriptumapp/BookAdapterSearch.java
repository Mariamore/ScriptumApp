package com.example.scriptumapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAdapterSearch extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> titlesList;
    private final List<String> authorsList;
    private final List<String> photosList;
    private final List<String> usersList;
    private final OnMessageButtonClickListener listener;

    public interface OnMessageButtonClickListener {
        void onMessageButtonClick(int position);
        void onItemClick(int position);
    }

    public BookAdapterSearch(Context context, List<String> titlesList, List<String> authorsList,
                             List<String> photosList, List<String> usersList, OnMessageButtonClickListener listener) {
        super(context, R.layout.item_book, titlesList);
        this.context = context;
        this.titlesList = titlesList;
        this.authorsList = authorsList;
        this.photosList = photosList;
        this.usersList = usersList;
        this.listener = listener;
    }

    /**
     * Obtiene una vista que muestra los datos en la posición especificada en el conjunto de datos.
     *
     * @param position    La posición del elemento dentro del conjunto de datos del adaptador que representa la vista a obtener.
     * @param convertView La vista convertida que se puede reutilizar. Será null si no hay vista para reutilizar.
     * @param parent      El ViewGroup al que se añadirá la nueva vista después de ser vinculada a una posición del adaptador.
     * @return La vista que muestra los datos en la posición especificada en el conjunto de datos.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.item_book, parent, false);

        TextView titleTextView = rootView.findViewById(R.id.titleTextView);
        TextView authorTextView = rootView.findViewById(R.id.authorTextView);
        ImageView photoImageView = rootView.findViewById(R.id.BookImageView);
        TextView userTextView = rootView.findViewById(R.id.userTextView);
        ImageButton messageButton = rootView.findViewById(R.id.messageButton);

        titleTextView.setText(titlesList.get(position));
        authorTextView.setText(authorsList.get(position));
        userTextView.setText(usersList.get(position));

        // Cargar imagen usando Picasso desde la URL
        String imageUrl = photosList.get(position);
        Picasso.get().load(imageUrl)
                .resize(200,300)
                .centerCrop()
                .placeholder(R.drawable.libro) // Placeholder en caso de que la carga falle
                .error(R.drawable.libro) // Imagen a mostrar si hay un error al cargar la imagen
                .into(photoImageView);

        // Configurar el OnClickListener para el botón de mensaje
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMessageButtonClick(position);
                }
            }
        });

        // Configurar el OnClickListener para el rootView
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userTextView.toString();
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });


        return rootView;
    }
}