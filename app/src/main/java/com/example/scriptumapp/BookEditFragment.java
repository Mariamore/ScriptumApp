package com.example.scriptumapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookEditFragment extends Fragment {


    private String mParam1;
    private String mParam2;

    private EditText authorEditText_bookEdit, titleEditText_bookEdit, editorialEditText_bookEdit, yearEditText_bookEdit, statusEditText_bookEdit;
    private ImageView imageBook_bookEdit;
    private Button button_bookEdit;

    private Book book;


    public BookEditFragment() {
        // Required empty public constructor
    }


    public static BookEditFragment newInstance(Book book) {
        BookEditFragment fragment = new BookEditFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_book_edit, container, false);


        authorEditText_bookEdit = rootView.findViewById(R.id.authorEditText_bookEdit);
        titleEditText_bookEdit  = rootView.findViewById(R.id.titleEditText_bookEdit);
        yearEditText_bookEdit = rootView.findViewById(R.id.yearEditText_bookEdit);
        imageBook_bookEdit = rootView.findViewById(R.id.rectangle_bookEditImage);
        button_bookEdit = rootView.findViewById(R.id.button_bookEdit);
        statusEditText_bookEdit = rootView.findViewById(R.id.currentStatusEditText_bookEdit);
        editorialEditText_bookEdit= rootView.findViewById(R.id.editorialEditText_bookEdit);


        return rootView;
    }


}