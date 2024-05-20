package com.example.scriptumapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MessagesChatFragment extends Fragment {

    private String userId; // ID del usuario con el que se está chateando
    private String currentUserId; // ID del usuario actual

    public MessagesChatFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_chat, container, false);

        //Recibimos el usuario del anterior fragment mediante bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("text_key");
        }
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Toast.makeText(getActivity(), "Message for contact: " + userId, Toast.LENGTH_SHORT).show();

        return view;
    }

}