package com.example.scriptumapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> titlesList;
    private final List<String> authorsList;
    private final List<String> photosList;

    public CustomAdapter(Context context, List<String> titlesList, List<String> authorsList, List<String> photosList) {
        super(context, R.layout.item_book, titlesList);
        this.context = context;
        this.titlesList = titlesList;
        this.authorsList = authorsList;
        this.photosList = photosList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_book, parent, false);

        TextView titleTextView = rowView.findViewById(R.id.titleTextView);
        TextView authorTextView = rowView.findViewById(R.id.authorTextView);
        ImageView photoImageView = rowView.findViewById(R.id.BookImageView);

        titleTextView.setText(titlesList.get(position));
        authorTextView.setText(authorsList.get(position));

        // Cargar imagen usando Picasso desde la URL
        String imageUrl = photosList.get(position);
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.libro) // Placeholder en caso de que la carga falle
                .error(R.drawable.libro) // Imagen a mostrar si hay un error al cargar la imagen
                .into(photoImageView);

        return rowView;
    }
}