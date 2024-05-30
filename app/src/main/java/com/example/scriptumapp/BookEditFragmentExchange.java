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
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookEditFragmentExchange extends Fragment {

    private EditText authorEditText_bookEditExchange, titleEditText_bookEditExchange, editorialEditText_bookEditExchange, yearEditText_bookEditExchange, statusEditText_bookEditExchange;
    private ImageView imageBook_bookEditExchange;
    private Button button_saveBookEditExchange;
    private FirebaseFirestore db;
    StorageReference stRe;
    private static final String DOC_ID = "docId";
    private String docId;
    private Uri imageUri;
    private static final String YEAR_REGEX = "^\\d{4}$";
    private static final Pattern YEAR_PATTERN = Pattern.compile(YEAR_REGEX);

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
                //selecionamos la imagen
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, COD_SEL_IMAGE);
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
                           // Toast.makeText(getContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Toast.makeText(getContext(), "Failed to load document", Toast.LENGTH_SHORT).show();
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

        if (title.isEmpty()){
            titleEditText_bookEditExchange.setError(getString(R.string.required_field));
            titleEditText_bookEditExchange.requestFocus();
        } else if (author.isEmpty()){
            authorEditText_bookEditExchange.setError(getString(R.string.required_field));
            authorEditText_bookEditExchange.requestFocus();
        } else if(editorial.isEmpty()){
            editorialEditText_bookEditExchange.setError(getString(R.string.required_field));
            editorialEditText_bookEditExchange.requestFocus();
        } else if (year.isEmpty()){
            yearEditText_bookEditExchange.setError(getString(R.string.required_field));
            yearEditText_bookEditExchange.requestFocus();
        } else if (!isValidYear(year)) {
            yearEditText_bookEditExchange.setError(getString(R.string.invalid_year_format));
            yearEditText_bookEditExchange.requestFocus();
        } else {
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
                            }
                            positiveToast("Book updated!");
                            getParentFragmentManager().popBackStack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            negativeToast(getString(R.string.error_updating_book));
                        }
                    });
        }
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
                                               // Toast.makeText(getContext(), "Book and image updated successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                               // Toast.makeText(getContext(), "Error updating photo URL in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(getContext(), "Error getting download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Toast.makeText(getContext(), "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static boolean isValidYear(String year) {
        if (year == null) {
            return false;
        }
        Matcher matcher = YEAR_PATTERN.matcher(year);
        return matcher.matches();
    }

    private void negativeToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout_fail, requireActivity().findViewById(R.id.toastLayoutFail));
        TextView txtMsg = layout.findViewById(R.id.toastMessage);
        txtMsg.setText(message);
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void positiveToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout_ok, requireActivity().findViewById(R.id.toastLayoutOk));
        TextView txtMsg = layout.findViewById(R.id.toastMessage);
        txtMsg.setText(message);
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}