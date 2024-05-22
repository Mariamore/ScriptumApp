package com.example.scriptumapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessagesFragment extends Fragment {

    private String currentUserId;
    public List<String> userIdList = new ArrayList<>();
    public List<String> userNameList = new ArrayList<>();
    public List<String> photoList = new ArrayList<>();
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private UserAdapter adapter;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = rootView.findViewById(R.id.userlist_recycler_view);
        // Asignamos un LayoutManager al RecyclerView
        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //allUsers();
        loadChatUsers();
        return rootView;
    }

    //Método para cargar todos los usuarios (solo pruebas)
    private void allUsers(){
        db.collection("usersData").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                userIdList.clear();
                userNameList.clear();
                photoList.clear();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    if(document.contains("user") && document.contains("nameSurname")){
                        String user = document.getString("user");
                        String nameSurname = document.getString("nameSurname");
                        String profileImageUrl = document.getString("profileImageUrl");
                        userIdList.add(user);
                        userNameList.add(nameSurname);
                        photoList.add(profileImageUrl);
                    }
                }
                updateUi();
            }
        });
    }

    private void loadChatUsers() {

        userIdList.clear();
        userNameList.clear();
        photoList.clear();

        // Creamos un HashSet para almacenar los IDs de los usuarios que ya hemos intentado cargar
        Set<String> loadedUserIds = new HashSet<>();

        db.collection("messages")
                .whereEqualTo("senderId", currentUserId)
                .addSnapshotListener((senderSnapshots, senderException) -> {
                    if (senderException != null) {
                        // Manejar el error
                        return;
                    }

                    for (DocumentSnapshot senderDocument : senderSnapshots) {
                        String receiverId = senderDocument.getString("receiverId");
                        // Intentamos cargar los datos del usuario si aún no lo hemos hecho
                        if (!loadedUserIds.contains(receiverId)) {
                            loadedUserIds.add(receiverId);
                            getUserData(receiverId);
                        }
                    }
                });

        db.collection("messages")
                .whereEqualTo("receiverId", currentUserId)
                .addSnapshotListener((receiverSnapshots, receiverException) -> {
                    if (receiverException != null) {
                        // Manejar el error
                        return;
                    }

                    for (DocumentSnapshot receiverDocument : receiverSnapshots) {
                        String senderId = receiverDocument.getString("senderId");
                        if (!loadedUserIds.contains(senderId)) {
                            loadedUserIds.add(senderId);
                            getUserData(senderId);
                        }
                    }
                });
    }

    private void getUserData(String userId) {
        db.collection("usersData").whereEqualTo("user", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot userDocument : task.getResult()) {
                            String nameSurname = userDocument.getString("nameSurname");
                            String profileImageUrl = userDocument.getString("profileImageUrl");
                            userIdList.add(userId);
                            userNameList.add(nameSurname);
                            photoList.add(profileImageUrl);
                        }
                    } else {
                        toastNoResultsFound();
                    }
                    updateUi();
                });
    }

    private void updateUi(){
        if (userIdList.isEmpty()) {
            toastNoResultsFound();
        } else {
            adapter = new UserAdapter(requireActivity(), userIdList, userNameList, photoList,
                    new UserAdapter.OnMessageButtonClickListener() {
                        @Override
                        public void onMessageButtonClick(int position) {
                            // Enviamos el usuario al fragment de messagesChatFragment (PRUEBA)
                            String textToSend = userIdList.get(position);
                            Bundle bundle = new Bundle();
                            bundle.putString("userIdReceptor", textToSend);
                            MessagesChatFragment messagesChatFragment = new MessagesChatFragment();
                            messagesChatFragment.setArguments(bundle);
                            replaceFragment(messagesChatFragment);
                        }
                    });
            recyclerView.setAdapter(adapter);
            // Notifica al adaptador de los cambios en los datos
            adapter.notifyDataSetChanged();
        }
    }

    public String getChatId(String userId1, String userId2) {
        // Ordenar los IDs de los usuarios alfabéticamente
        String[] userIds = {userId1, userId2};
        Arrays.sort(userIds);

        // Concatenar los IDs de los usuarios para formar el chatId
        return userIds[0] + "_" + userIds[1];
    }

    // Añadir este método para poder cambiar de fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    private void toastNoResultsFound() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout_fail,
                requireActivity().findViewById(R.id.toastLayoutFail));
        TextView txtMsg = layout.findViewById(R.id.toastMessage);
        txtMsg.setText(R.string.no_results_found);
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}