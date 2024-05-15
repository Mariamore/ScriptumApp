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
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button searchButton;
    EditText searchEditText;
    TextView textViewPruebas;
    ListView searchListView;
    List<String> titlesList = new ArrayList<String>();
    List<String> authorsList = new ArrayList<>();
    List<String> photosList = new ArrayList<>();
    private List<Book> books;
    private List<String> titles;
    private CollectionReference booksCollection;
    private Query query;
    private String queryText, titleBook, authorBook, photoBook;
    private FirebaseFirestore db;
    View rootView;

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
        booksCollection = db.collection("books");
        books = new ArrayList<>();
        searchButton = rootView.findViewById((R.id.searchButton));
        textViewPruebas = rootView.findViewById(R.id.textViewPruebas);
        searchListView = rootView.findViewById(R.id.searchListView);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryText = searchEditText.getText().toString();
                //textViewPruebas.setText(queryText);
                query = booksCollection.whereGreaterThanOrEqualTo("title", queryText.toLowerCase());

                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                if(document.contains("title") && document.contains("author")){
                                titleBook = document.getString("title");
                                authorBook = document.getString("author");
                                //photoBook = document.getString("photo");

                                //Prueba para ver arriba el título que ha encontrado
                                textViewPruebas.setText(titleBook);
                                }
                            }
                            updateUi();
                        } else {
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
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout_fail,
                                requireActivity().findViewById(R.id.toastLayoutFail));
                        TextView txtMsg = layout.findViewById(R.id.toastMessage);
                        txtMsg.setText("Error");
                        Toast toast = new Toast(requireContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                });
            }
        });
        return rootView;
    }

    private void updateUi(){
        db.collection("books")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        titlesList.clear();
                        authorsList.clear();
                        //photosList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            String titleString = doc.getString("title");
                            if (titleString != null && titleString.toLowerCase().contains(queryText.toLowerCase())){
                                titlesList.add(doc.getString("title"));
                                authorsList.add(doc.getString("author"));
                                //photosList.add(doc.getString("photo"));
                            }
                        }

                        //Rellenar el listview con el adapter
                        if(titlesList.isEmpty()){
                            searchListView.setAdapter(null);
                        }else{
                            CustomAdapter bookAdapter = new CustomAdapter(requireActivity(), titlesList, authorsList);
                            searchListView.setAdapter(bookAdapter);
                        }
                    }
                });
    }

    // Añadir este método para poder cambiar de fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}