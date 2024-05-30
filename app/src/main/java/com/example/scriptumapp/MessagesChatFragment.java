package com.example.scriptumapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessagesChatFragment extends Fragment {

    private String userIdContact; // ID del usuario con el que se está chateando
    private String currentUserId; // ID del usuario actual
    private String chatId;
    private String messageId;
    private String messageText;
    private long timestamp;
    private FirebaseFirestore db;
    private EditText messageEditText;
    private Button sendButton;
    private TextView textViewReceiver;
    private ImageView imageViewReceiver;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;

    public MessagesChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages_chat, container, false);

        //Recibimos el usuario del anterior fragment mediante bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            userIdContact = bundle.getString("userIdReceptor");
        }
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatId = getChatId(userIdContact, currentUserId);
        messageEditText = rootView.findViewById(R.id.message_edit_text);
        sendButton = rootView.findViewById(R.id.sendButton);
        textViewReceiver = rootView.findViewById(R.id.textViewReceiver);
        imageViewReceiver = rootView.findViewById(R.id.imageViewReceiver);
        recyclerView = rootView.findViewById(R.id.recylerViewMessages);

        createToastError(getString(R.string.sensitive_information));

        getUserProfile();

        // Inicializamos la lista de mensajes y el adaptador
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageText = messageEditText.getText().toString().trim();
                if (!messageText.isEmpty()){
                    sendMessage(chatId, currentUserId, userIdContact, messageText);
                } else {
                    createToastError(getString(R.string.message_empty));
                }

            }
        });
        // Llamamos al método para recibir mensajes
        receiveMessages(chatId);

        return rootView;
    }

    public void sendMessage(String chatId, String senderId, String receiverId, String messageText) {
        messageId = db.collection("messages").document().getId(); // Generar un ID único para el mensaje
        timestamp = System.currentTimeMillis();
        Message message = new Message(messageId, chatId, senderId, receiverId, messageText, timestamp);

        db.collection("messages").document(messageId).set(message)
                .addOnSuccessListener(aVoid -> {
                    // Éxito
                    messageEditText.setText(""); //Limpiamos el EditText de contenido
                })
                .addOnFailureListener(e -> {
                    // Error
                    createToastError(getString(R.string.message_error));
                });
    }

    public void receiveMessages(String chatId) {
        db.collection("messages").whereEqualTo("chatId", chatId)
                .orderBy("timestamp")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        return;
                    }
                    if (snapshots != null) {
                        messageList.clear();
                        for (QueryDocumentSnapshot document : snapshots) {
                            Message message = document.toObject(Message.class);
                            messageList.add(message);
                        }
                        adapter.notifyDataSetChanged();
                        // Desplazamiento al último mensaje
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    public String getChatId(String userId1, String userId2) {
        // Se ordenan los IDs de los usuarios alfabéticamente
        String[] userIds = {userId1, userId2};
        Arrays.sort(userIds);

        // Concatenamos los IDs de los usuarios para formar el chatId
        return userIds[0] + "_" + userIds[1];
    }

    public void getUserProfile(){
        db.collection("usersData").whereEqualTo("user", userIdContact)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String nameSurname = documentSnapshot.getString("nameSurname");
                        textViewReceiver.setText(nameSurname);
                        if(documentSnapshot.contains("profileImageUrl")){
                            String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                            Picasso.get()
                                    .load(profileImageUrl)
                                    .transform(new ImageCircle())
                                    .resize(200,200)
                                    .centerCrop()
                                    .into(imageViewReceiver);
                        }
                    } else {
                        createToastError(getString(R.string.error_loading_user_data));
                    }
                })
                .addOnFailureListener(e -> {
                    createToastError(getString(R.string.error_loading_user_data));
                });
    }

    private void createToastError(String text) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout_fail,
                requireActivity().findViewById(R.id.toastLayoutFail));
        TextView txtMsg = layout.findViewById(R.id.toastMessage);
        txtMsg.setText(text);
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}