package com.example.scriptumapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> titlesList;
    private final List<String> authorsList;
    //private final List<String> photosList;

    public CustomAdapter(Context context, List<String> titlesList, List<String> authorsList) {
        super(context, R.layout.item_book, titlesList);
        this.context = context;
        this.titlesList = titlesList;
        this.authorsList = authorsList;
        //this.photosList = photosList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_book, parent, false);

        TextView titleTextView = rowView.findViewById(R.id.titleTextView);
        TextView authorTextView = rowView.findViewById(R.id.authorTextView);
        //ImageView photoImageView = rowView.findViewById(R.id.BookImageView);

        titleTextView.setText(titlesList.get(position));
        authorTextView.setText(authorsList.get(position));
        // Obtener el identificador del recurso de la imagen
//        String imageName = photosList.get(position);
//        int resourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
//        if (resourceId != 0) {
//            // Si se encontró el identificador del recurso, establecer la imagen
//            photoImageView.setImageResource(resourceId);
//        } else {
//            // Si no se encontró el identificador del recurso, mostrar una imagen genérica
//            photoImageView.setImageResource(R.drawable.libro);
//        }

        return rowView;
    }
}