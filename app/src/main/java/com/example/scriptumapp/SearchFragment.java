package com.example.scriptumapp;

import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
*/
public class SearchFragment extends Fragment implements CustomAdapter.OnMessageButtonClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button searchButton, messageButton;
    private EditText searchEditText;
    private ListView searchListView;
    private List<String> titlesList = new ArrayList<String>();
    private List<String> authorsList = new ArrayList<>();
    private List<String> photosList = new ArrayList<>();
    private List<String> usersList = new ArrayList<>();
    private List<String> booksIdList = new ArrayList<>();
    private List<String> savedTitlesList = new ArrayList<>();
    private List<String> savedAuthorsList = new ArrayList<>();
    private List<String> savedPhotosList = new ArrayList<>();
    private List<String> savedUsersList = new ArrayList<>();
    private List<String> savedBooksIdList = new ArrayList<>();
    private CollectionReference booksCollection;
    private Query query;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String queryText, titleBook, authorBook, photoBook, userBook, idUser, bookId;
    private FirebaseFirestore db;
    private View rootView;

    //Añadimos binding para cargar otro fragment
    //FragmentSearchBinding binding;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
*/
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Estas dos líneas cambiarían el fragment
        //binding = FragmentSearchBinding.inflate(getLayoutInflater());
        //replaceFragment(new HomeFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Añadir estas dos líneas siguientes
        //binding = FragmentSearchBinding.inflate(inflater, container, false);
        //return binding.getRoot();
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        searchEditText = rootView.findViewById(R.id.search_edit_text);
        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
//hay que pensar si queremos que usuarios no autenticados puedan buscar o no
        if (user != null) {
            idUser = user.getUid();
            // Resto del código que utiliza el UID del usuario
        } /*else {
           idUser = "non_authenticated_user_id";

        }
*/
        booksCollection = db.collection("booksData");
        searchButton = rootView.findViewById((R.id.searchButton));
        searchListView = rootView.findViewById(R.id.searchListView);

        if (savedTitlesList.size() > 0) {
            CustomAdapter bookAdapter = new CustomAdapter(requireActivity(), savedTitlesList,
                    savedAuthorsList, savedPhotosList, savedUsersList, this);
            searchListView.setAdapter(bookAdapter);
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryText = searchEditText.getText().toString();
                query = booksCollection.whereGreaterThanOrEqualTo("title", queryText.toLowerCase())
                        .whereLessThanOrEqualTo("title", queryText + "\uf8ff")
                        ;
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                if(!document.getString("user").equals(idUser)){
                                    if(document.contains("title") && document.contains("author")){
                                    titleBook = document.getString("title");
                                    authorBook = document.getString("author");
                                    photoBook = document.getString("photo");
                                    userBook = document.getString("user");
                                    bookId = document.getId();
                                    }
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
        });
        return rootView;
    }

    private void updateUi(){
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

                        savedTitlesList.clear();
                        savedAuthorsList.clear();
                        savedPhotosList.clear();
                        savedUsersList.clear();
                        savedBooksIdList.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            String titleString = doc.getString("title");
                            if (titleString != null && titleString.toLowerCase().contains(queryText.toLowerCase())){
                                titlesList.add(doc.getString("title"));
                                authorsList.add(doc.getString("author"));
                                photosList.add(doc.getString("photo"));
                                usersList.add(doc.getString("user"));
                                booksIdList.add(doc.getId());


                            }
                        }
                        savedTitlesList.addAll(titlesList);
                        savedAuthorsList.addAll(authorsList);
                        savedPhotosList.addAll(photosList);
                        savedUsersList.addAll(usersList);
                        savedBooksIdList.addAll(booksIdList);
                        //Rellenar el listview con el adapter
                        if(titlesList.isEmpty()){
                            searchListView.setAdapter(null);
                            toastNoResultsFound();
                        }else{
                            CustomAdapter bookAdapter = new CustomAdapter(requireActivity(), titlesList,
                                    authorsList, photosList, usersList,SearchFragment.this);
                            searchListView.setAdapter(bookAdapter);
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
    public void onItemClick(int position, String user) {
        String bookId = booksIdList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("bookId",bookId);
        // Crear una instancia de BookInfoFragment y asignar el Bundle
        BookInfoFragment fragment = new BookInfoFragment();
        fragment.setArguments(bundle);

        // Reemplazar el fragmento actual con BookInfoFragment
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


}


