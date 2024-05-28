package com.example.scriptumapp;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder> {

    private List<List<String>> imageUrls;

    public ImageCarouselAdapter(List<List<String>> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        List<String> imageUrlsGroup = imageUrls.get(position);
        if (imageUrlsGroup != null && imageUrlsGroup.size() >= 3) {
            Glide.with(holder.itemView.getContext()).load(imageUrlsGroup.get(0)).into(holder.imageView1);
            Glide.with(holder.itemView.getContext()).load(imageUrlsGroup.get(1)).into(holder.imageView2);
            Glide.with(holder.itemView.getContext()).load(imageUrlsGroup.get(2)).into(holder.imageView3);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.imageView1);
            imageView2 = itemView.findViewById(R.id.imageView2);
            imageView3 = itemView.findViewById(R.id.imageView3);
        }
    }
}
