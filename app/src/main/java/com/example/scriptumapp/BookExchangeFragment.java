package com.example.scriptumapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
 * Use the {@link BookLoanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookExchangeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button backBookLoan;

    RecyclerView mRecycler;
    BookAdapterExchange exchangeAdapterBook;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    FirebaseUser authUser;
    Query query;

    public BookExchangeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookLoanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookLoanFragment newInstance(String param1, String param2) {
        BookLoanFragment fragment = new BookLoanFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_exchange, container, false);


        mAuth = FirebaseAuth.getInstance();
        authUser = mAuth.getCurrentUser();
        String idUser = authUser.getUid();

        //Inicializamos
        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = rootView.findViewById(R.id.recyclerViewSingle3);
        mRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        //query para la coleccion Loan de la BBDD
        query = mFirestore.collection("users").document(idUser).collection("exchange");

        //Crear opciones de la consulta
        FirestoreRecyclerOptions<Book> exchangeOp = new FirestoreRecyclerOptions.Builder<Book>().setQuery(query, Book.class).build();

        //Inicializar el adaptador con las opciones
        exchangeAdapterBook = new BookAdapterExchange(exchangeOp);
        mRecycler.setAdapter(exchangeAdapterBook);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        exchangeAdapterBook.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        exchangeAdapterBook.stopListening();
    }


}