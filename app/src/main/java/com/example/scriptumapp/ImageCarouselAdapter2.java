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

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        List<String> imageUrlsGroup = imageUrl2.get(position);
        if (imageUrlsGroup != null && imageUrlsGroup.size() >= 3) {
            Glide.with(holder.itemView.getContext()).load(imageUrlsGroup.get(0))
                    .centerCrop()
                    .into(holder.imageView1);
            Glide.with(holder.itemView.getContext()).load(imageUrlsGroup.get(1))
                    .centerCrop()
                    .into(holder.imageView2);
            Glide.with(holder.itemView.getContext()).load(imageUrlsGroup.get(2))
                    .centerCrop()
                    .into(holder.imageView3);

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

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;

        public ImageViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.imageView1);
            imageView2 = itemView.findViewById(R.id.imageView2);
            imageView3 = itemView.findViewById(R.id.imageView3);
        }
    }
}

