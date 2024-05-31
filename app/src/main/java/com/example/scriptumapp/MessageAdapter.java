package com.example.scriptumapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private List<Message> messageList;
    private String currentUserId;

    public MessageAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    /**
     * Método para determinar el tipo de vista de un mensaje en una posición determinada.
     *
     * @param position Posición del mensaje en la lista.
     * @return Tipo de vista del mensaje (enviado o recibido).
     */
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    /**
     * Método para crear un ViewHolder para un tipo de vista de mensaje específico.
     *
     * @param parent   El ViewGroup al que se adjuntará la nueva vista.
     * @param viewType Tipo de vista del mensaje.
     * @return ViewHolder correspondiente al tipo de vista de mensaje.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    /**
     * Método para asociar datos a las vistas en el ViewHolder.
     *
     * @param holder   ViewHolder que contendrá las vistas.
     * @param position Posición del elemento en los datos.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


     //Clase ViewHolder para mensajes enviados.

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestampText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textViewMessageSent);
            timestampText = itemView.findViewById(R.id.textViewTimestampSent);
        }

        /**
         * Método para asociar datos de mensaje a las vistas en el ViewHolder.
         *
         * @param message Objeto de mensaje.
         */
        void bind(Message message) {
            messageText.setText(message.getMessageText());
            timestampText.setText(formatTimestamp(message.getTimestamp()));
        }
    }

    //Clase ViewHolder para mensajes recibidos.
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestampText;


        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textViewMessageReceived);
            timestampText = itemView.findViewById(R.id.textViewTimestampReceived);
        }

        /**
         * Método para asociar datos de mensaje a las vistas en el ViewHolder.
         *
         * @param message Objeto de mensaje.
         */
        void bind(Message message) {
            messageText.setText(message.getMessageText());
            timestampText.setText(formatTimestamp(message.getTimestamp()));
        }
    }

    /**
     * Método para dar formato a una marca de tiempo en formato de fecha y hora legible.
     *
     * @param timestamp Marca de tiempo del mensaje.
     * @return Cadena de fecha y hora formateada.
     */
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}

