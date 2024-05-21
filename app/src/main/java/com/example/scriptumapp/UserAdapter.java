package com.example.scriptumapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context context;
    private final List<String> userIdList;
    private final List<String> userNameList;
    private final List<String> photoList;
    private final OnMessageButtonClickListener listener;

    public interface OnMessageButtonClickListener {
        void onMessageButtonClick(int position);
    }

    public UserAdapter(Context context, List<String> userIdList, List<String> userNameList,
                       List<String> photoList, OnMessageButtonClickListener listener){
        this.context = context;
        this.userIdList = userIdList;
        this.userNameList = userNameList;
        this.photoList = photoList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacts_messages, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.userIdTextView.setText(userIdList.get(position));
        holder.textNameContact.setText(userNameList.get(position));

        // Cargar imagen usando Picasso desde la URL
        String imageUrl = photoList.get(position);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl)
                    .transform(new ImageCircle())
                    .resize(200,200)
                    .centerCrop()
                    .placeholder(R.drawable.buttonimagecontact) // Placeholder en caso de que la carga falle
                    .error(R.drawable.buttonimagecontact) // Imagen a mostrar si hay un error al cargar la imagen
                    .into(holder.imageContact);
        }else{
            holder.imageContact.setImageResource(R.drawable.buttonimagecontact);
        }


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMessageButtonClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userIdList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNameContact;
        TextView userIdTextView;
        ImageView imageContact;

        public ViewHolder(View itemView) {
            super(itemView);
            textNameContact = itemView.findViewById(R.id.textNameContact);
            userIdTextView = itemView.findViewById(R.id.userIdTextView);
            imageContact = itemView.findViewById(R.id.imageContact);
        }
    }

}
