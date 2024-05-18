package com.example.scriptumapp;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

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
        args.putParcelable("book", (Parcelable) book);//pasar datos entre fragments
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = getArguments().getParcelable("book");
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

        //Extraemos los datos del libro
        authorEditText_bookEdit.setText(book.getAuthor());
        titleEditText_bookEdit.setText(book.getTitle());
        //yearEditText_bookEdit.setText(book.get);
        statusEditText_bookEdit.setText(book.getStatus());
        Picasso.get().load(book.getPhoto()).into(imageBook_bookEdit);
        //editorialEditText_bookEdit.setText(book.get);

        button_bookEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                book.setTitle(titleEditText_bookEdit.getText().toString());
                book.setAuthor(authorEditText_bookEdit.getText().toString());
                book.setStatus(statusEditText_bookEdit.getText().toString());



            }
        });


        return rootView;
    }


}