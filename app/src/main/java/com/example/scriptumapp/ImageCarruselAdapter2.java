package com.example.scriptumapp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ImageCarruselAdapter2 extends RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder> {

    private List<List<String>> imageUrl;

    public ImageCarruselAdapter2(List<List<String>> imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ImageCarouselAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel_image2, parent, false);
        return new ImageCarouselAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageCarouselAdapter.ImageViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
