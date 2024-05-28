package com.example.scriptumapp;

import android.content.Context;
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
    private Context context;


    public ImageCarouselAdapter2(Context context, List<List<String>> imageUrl2) {
        this.context = context;
        this.imageUrl2 = imageUrl2;
    }

    @NonNull
    @Override
    public ImageCarouselAdapter2.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel_image2, parent, false);

        return new ImageCarouselAdapter2.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        List<String> urls = imageUrl2.get(position);
        if (urls.size() > 0) {
            Glide.with(context).load(urls.get(0)).into(holder.imageView1);
        }
        if (urls.size() > 1) {
            Glide.with(context).load(urls.get(1)).into(holder.imageView2);
        }
        if (urls.size() > 2) {
            Glide.with(context).load(urls.get(2)).into(holder.imageView3);
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

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.imageView1);
            imageView2 = itemView.findViewById(R.id.imageView2);
            imageView3 = itemView.findViewById(R.id.imageView3);
        }
    }
}
