package com.example.scriptumapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageCarouselAdapter2 extends RecyclerView.Adapter<ImageCarouselAdapter2.ImageViewHolder> {

    private List<List<String>> imageUrl2;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String imageUrl);
    }

    public ImageCarouselAdapter2(List<List<String>> imageUrl2, OnItemClickListener onItemClickListener) {
        this.imageUrl2 = imageUrl2;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel_image2, parent, false);
        return new ImageViewHolder(view, onItemClickListener);
    }

    /**
     * Vincula los datos de las imágenes a las vistas correspondientes en el RecyclerView.
     * Carga las tres primeras imágenes de la lista de URLs de imágenes en cada grupo y las muestra en ImageView.
     * Configura los clics en las ImageView para abrir el fragmento de detalle del libro correspondiente.
     *
     * @param holder   El ViewHolder que contendrá las vistas.
     * @param position La posición del elemento en los datos.
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        List<String> imageUrlsGroup = imageUrl2.get(position);
        if (imageUrlsGroup != null && imageUrlsGroup.size() >= 3) {
            // Carga las imágenes en las ImageView usando Glide
            Glide.with(holder.itemView.getContext()).load(imageUrlsGroup.get(0))
                    .centerCrop()
                    .into(holder.imageView1);
            Glide.with(holder.itemView.getContext()).load(imageUrlsGroup.get(1))
                    .centerCrop()
                    .into(holder.imageView2);
            Glide.with(holder.itemView.getContext()).load(imageUrlsGroup.get(2))
                    .centerCrop()
                    .into(holder.imageView3);

            // Configura los clics en las ImageView para abrir el fragmento de detalle del libro correspondiente
            holder.imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(imageUrlsGroup.get(0));
                    }
                }
            });

            holder.imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(imageUrlsGroup.get(1));
                    }
                }
            });

            holder.imageView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(imageUrlsGroup.get(2));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageUrl2.size();
    }

    /*
     * ViewHolder para mantener las referencias de las vistas de cada elemento de la RecyclerView.
     * Contiene tres ImageView para mostrar las imágenes de los libros.
     */
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;

        /**
         * Constructor que asigna las vistas de las ImageView.
         *
         * @param itemView             La vista inflada que representa un elemento en la RecyclerView.
         * @param onItemClickListener El listener para manejar los clics en las imágenes.
         */
        public ImageViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.imageView1);
            imageView2 = itemView.findViewById(R.id.imageView2);
            imageView3 = itemView.findViewById(R.id.imageView3);
        }
    }
}

