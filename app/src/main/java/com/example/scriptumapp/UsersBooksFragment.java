package com.example.scriptumapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersBooksFragment extends Fragment implements BookAdapterSearch.OnMessageButtonClickListener{

    private Button backButton, messageButton;
    private ListView searchListView;
    private List<String> titlesList = new ArrayList<String>();
    private List<String> authorsList = new ArrayList<>();
    private List<String> photosList = new ArrayList<>();
    private List<String> usersList = new ArrayList<>();
    private List<String> booksIdList = new ArrayList<>();
    private CollectionReference booksCollection;
    private FirebaseUser user;
    private String idUser;
    private FirebaseFirestore db;
    private View rootView;

    public UsersBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_users_books, container, false);

        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            idUser = user.getUid();
        }

        Bundle bundle = getArguments();
        String userId = bundle.getString("userMarkerId");
        booksCollection = db.collection("booksData");
        backButton = rootView.findViewById((R.id.backButton));
        searchListView = rootView.findViewById(R.id.searchListView);

        searchAndUpdateUi(userId);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        return rootView;
    }

    private void searchAndUpdateUi(String userId){
        db.collection("booksData")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        titlesList.clear();
                        authorsList.clear();
                        photosList.clear();
                        usersList.clear();
                        booksIdList.clear();

                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc.getString("user").equals(userId)) {

                                    titlesList.add(doc.getString("title"));
                                    authorsList.add(doc.getString("author"));
                                    photosList.add(doc.getString("photo"));
                                    usersList.add(doc.getString("user"));
                                    booksIdList.add(doc.getId());
                                }
                            }
                        }

                        if (isAdded()) {
                            if (titlesList.isEmpty()) {
                                searchListView.setAdapter(null);
                                toastNoResultsFound();
                            } else {
                                BookAdapterSearch bookAdapter = new BookAdapterSearch(requireActivity(), titlesList, authorsList, photosList, usersList, UsersBooksFragment.this);
                                searchListView.setAdapter(bookAdapter);
                            }
                        }
                    }
                });
    }

    @Override
    public void onMessageButtonClick(int position) {
        //Enviamos el usuario al fragment de messagesChatFragment
        String textToSend = usersList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("userIdReceptor", textToSend);
        MessagesChatFragment messagesChatFragment = new MessagesChatFragment();
        messagesChatFragment.setArguments(bundle);
        replaceFragment(messagesChatFragment);
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

    // Añadir este método para poder cambiar de fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemClick(int position) {
        String bookId = booksIdList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("bookId",bookId);
        // Crear una instancia de BookInfoFragment y asignar el Bundle
        BookInfoFragment fragment = new BookInfoFragment();
        fragment.setArguments(bundle);

        // Reemplazar el fragmento actual con BookInfoFragment
        if (isAdded()) {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}