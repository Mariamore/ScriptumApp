package com.example.scriptumapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SavedBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedBooksFragment extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView mRecycler;
    BookAdapter mAdapter;
    FirebaseFirestore mFirestore;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SavedBooksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SavedBooksFragment newInstance(String param1, String param2) {
        SavedBooksFragment fragment = new SavedBooksFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_saved_books, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = rootView.findViewById(R.id.recyclerView);
        mRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        Query query = mFirestore.collection("books").select("title", "author", "status");


        // Configurar el FirestoreRecyclerOptions para tu adaptador
        FirestoreRecyclerOptions<Book> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query, Book.class)
                .build();

        // Inicializar el adaptador con las opciones
        mAdapter = new BookAdapter(firestoreRecyclerOptions);
        mAdapter.notifyDataSetChanged();
        // Establecer el adaptador en el RecyclerView
        mRecycler.setAdapter(mAdapter);
        return rootView;
    }
}