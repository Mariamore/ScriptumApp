package com.example.scriptumapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.search_user_recycler_view);
        // Asignamos un LayoutManager al RecyclerView
        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        allUsers();
        return view;
    }

    private void allUsers(){
        db.collection("usersData").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
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
                } else {
                    toastNoResultsFound();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastNoResultsFound();
            }
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
                            bundle.putString("text_key", textToSend);
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