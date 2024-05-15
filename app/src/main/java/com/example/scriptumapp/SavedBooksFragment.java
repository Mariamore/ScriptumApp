package com.example.scriptumapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    //
    FirebaseAuth mAuth;
    FirebaseUser authUser;

    Query loanQ, exchangeQ, giftQ;
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

        //Extraemos el id del usuario
        mAuth = FirebaseAuth.getInstance();
        authUser = mAuth.getCurrentUser();

        String idUser = authUser.getUid();

        View rootView = inflater.inflate(R.layout.fragment_saved_books, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = rootView.findViewById(R.id.recyclerView);
        mRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        //consultas a la base de datos
        loanQ = mFirestore.collection("users").document(idUser).collection("loan");
        exchangeQ = mFirestore.collection("users").document(idUser).collection("exchange");
        giftQ = mFirestore.collection("users").document(idUser).collection("gift");


        //Contruimos la query
        FirestoreRecyclerOptions<Book> loanReOp = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(loanQ, Book.class)
                .build();
        FirestoreRecyclerOptions<Book> exchangeReOp = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(exchangeQ, Book.class)
                .build();
        FirestoreRecyclerOptions<Book> gifReOp = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(giftQ, Book.class)
                .build();

        //inicializamos el adaptador con la combinacion de las consultas
        mAdapter = new BookAdapter(loanReOp,exchangeReOp,gifReOp);
        Log.d("SavedBooksFragment", "Configuración de RecyclerView completada");
        mAdapter.notifyDataSetChanged();
        //Establecemos el adaptador en el RecyclerView
        mRecycler.setAdapter(mAdapter);
        Log.d("SavedBooksFragment", "Adaptador establecido en RecyclerView");
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}