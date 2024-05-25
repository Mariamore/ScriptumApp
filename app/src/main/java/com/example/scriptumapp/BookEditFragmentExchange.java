package com.example.scriptumapp;

import static com.example.scriptumapp.UploadBookFragment.COD_SEL_IMAGE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookEditFragmentExchange#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookEditFragmentExchange extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText authorEditText_bookEditExchange, titleEditText_bookEditExchange, editorialEditText_bookEditExchange, yearEditText_bookEditExchange, statusEditText_bookEditExchange;
    private ImageView imageBook_bookEditExchange;
    private ImageButton button_bookEdit;

    private Button button_saveBookEditExchange;

    private Book book;
    private FirebaseFirestore db;
    StorageReference stRe;
    private String idUser;
    private static final String DOC_ID = "docId";

    private String docId;
    private Uri imageUri;
    //private String bookId;

    public BookEditFragmentExchange() {
        // Required empty public constructor
    }


    public static BookEditFragmentExchange newInstance(String docId) {
        BookEditFragmentExchange fragment = new BookEditFragmentExchange();
        Bundle args = new Bundle();
        args.putString(DOC_ID, docId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            docId = getArguments().getString(DOC_ID);
        }
        // Instanciamos la base de datos
        db = FirebaseFirestore.getInstance();
        stRe = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_book_edit_exchange, container, false);

        authorEditText_bookEditExchange = rootView.findViewById(R.id.authorEditText_bookEditExchange);
        titleEditText_bookEditExchange  = rootView.findViewById(R.id.titleEditText_bookEditExchange);
        yearEditText_bookEditExchange = rootView.findViewById(R.id.yearEditText_bookEditExchange);
        imageBook_bookEditExchange = rootView.findViewById(R.id.rectangle_bookEditImageExchange);
        statusEditText_bookEditExchange = rootView.findViewById(R.id.currentStatusEditText_bookEditExchange);
        editorialEditText_bookEditExchange= rootView.findViewById(R.id.editorialEditText_bookEditExchange);
        button_saveBookEditExchange = rootView.findViewById(R.id.button_saveBookEditExchange);

        //Extraemos los datos del libro
        dataBookExchange();

        //Selecinar Imagen nueva en la edicion
        imageBook_bookEditExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selecionamos la iamgen
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, COD_SEL_IMAGE);//deprecado
            }
        });

        //guardamos los datos al pulsar el boton
        button_saveBookEditExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Funcion para guardar
                saveBookEditExhange();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == COD_SEL_IMAGE && data != null) {
            imageUri = data.getData();
            imageBook_bookEditExchange.setImageURI(imageUri);
        }
    }


    public void dataBookExchange(){

        db.collection("booksData").document(docId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            titleEditText_bookEditExchange.setText(documentSnapshot.getString("title"));
                            authorEditText_bookEditExchange.setText(documentSnapshot.getString("author"));
                            editorialEditText_bookEditExchange.setText(documentSnapshot.getString("editorial"));
                            yearEditText_bookEditExchange.setText(documentSnapshot.getString("year"));
                            statusEditText_bookEditExchange.setText(documentSnapshot.getString("status"));
                            String imageUrl = documentSnapshot.getString("photo");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Picasso.get().load(imageUrl).into(imageBook_bookEditExchange);
                            }
                        } else {
                            Toast.makeText(getContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to load document", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //guardamos los datos nuevos
    public void saveBookEditExhange(){

        String title = titleEditText_bookEditExchange.getText().toString();
        String author = authorEditText_bookEditExchange.getText().toString();
        String editorial = editorialEditText_bookEditExchange.getText().toString();
        String year = yearEditText_bookEditExchange.getText().toString();
        String status = statusEditText_bookEditExchange.getText().toString();

        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("author", author);
        data.put("editorial", editorial);
        data.put("year", year);
        data.put("status", status);

        db.collection("booksData").document(docId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (imageUri != null) {
                            uploadPhotoExchange(docId);
                        } else {
                            Toast.makeText(getContext(), "Book updated", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error updating book", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void uploadPhotoExchange(String docId){

        String imageName= docId + ".jpg";
        StorageReference imageRef = stRe.child("booksData/" + docId + "/" + imageName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();

                                // Actualizar la URL de la imagen en Firestore
                                db.collection("booksData").document(docId)
                                        .update("photo", imageUrl)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "Book and image updated successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Error updating photo URL in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error getting download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

}