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

public class BookEditFragment extends Fragment {

    private EditText authorEditText_bookEdit, titleEditText_bookEdit, editorialEditText_bookEdit, yearEditText_bookEdit, statusEditText_bookEdit;
    private ImageView imageBook_bookEdit;
    private Button button_saveBookEdit;
    private FirebaseFirestore db;
    StorageReference stRe;
    private static final String DOC_ID = "docId";
    private String docId;
    private Uri imageUri;
    private static final String YEAR_REGEX = "^\\d{4}$";
    private static final Pattern YEAR_PATTERN = Pattern.compile(YEAR_REGEX);

    public BookEditFragment() {
        // Required empty public constructor
    }

    public static BookEditFragment newInstance(String docId) {
        BookEditFragment fragment = new BookEditFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_book_edit, container, false);

        authorEditText_bookEdit = rootView.findViewById(R.id.authorEditText_bookEdit);
        titleEditText_bookEdit  = rootView.findViewById(R.id.titleEditText_bookEdit);
        yearEditText_bookEdit = rootView.findViewById(R.id.yearEditText_bookEdit);
        imageBook_bookEdit = rootView.findViewById(R.id.rectangle_bookEditImage);
        statusEditText_bookEdit = rootView.findViewById(R.id.currentStatusEditText_bookEdit);
        editorialEditText_bookEdit= rootView.findViewById(R.id.editorialEditText_bookEdit);
        button_saveBookEdit = rootView.findViewById(R.id.button_saveBookEdit);

        //extraemos los datos
        dataBook();

        //Selecinar Imagen nueva en la edicion
        imageBook_bookEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selecionamos la iamgen
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, COD_SEL_IMAGE);
            }
        });

        //Guardamos los nuevos datos al pulsar en el boton
        button_saveBookEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBookEdit();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == COD_SEL_IMAGE && data != null) {
            imageUri = data.getData();
            Picasso.get()
                    .load(imageUri)
                    .resize(200, 300)
                    .centerInside()
                    .into(imageBook_bookEdit);
        }
    }

    public void dataBook(){
        db.collection("booksData").document(docId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            titleEditText_bookEdit.setText(documentSnapshot.getString("title"));
                            authorEditText_bookEdit.setText(documentSnapshot.getString("author"));
                            editorialEditText_bookEdit.setText(documentSnapshot.getString("editorial"));
                            yearEditText_bookEdit.setText(documentSnapshot.getString("year"));
                            statusEditText_bookEdit.setText(documentSnapshot.getString("status"));
                            String imageUrl = documentSnapshot.getString("photo");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Picasso.get()
                                        .load(imageUrl)
                                        .resize(200, 300)
                                        .centerInside()
                                        .into(imageBook_bookEdit);
                            }
                        } else {
                           // Toast.makeText(getContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        negativeToast("Error updating book");
                        //Toast.makeText(getContext(), "Failed to load document", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //guardamos los datos nuevos
    public void saveBookEdit(){

        String title = titleEditText_bookEdit.getText().toString();
        String author = authorEditText_bookEdit.getText().toString();
        String editorial = editorialEditText_bookEdit.getText().toString();
        String year = yearEditText_bookEdit.getText().toString();
        String status = statusEditText_bookEdit.getText().toString();

        if (title.isEmpty()){
            titleEditText_bookEdit.setError(getString(R.string.required_field));
            titleEditText_bookEdit.requestFocus();
        } else if (author.isEmpty()){
            authorEditText_bookEdit.setError(getString(R.string.required_field));
            authorEditText_bookEdit.requestFocus();
        } else if(editorial.isEmpty()){
            editorialEditText_bookEdit.setError(getString(R.string.required_field));
            editorialEditText_bookEdit.requestFocus();
        } else if (year.isEmpty()){
            yearEditText_bookEdit.setError(getString(R.string.required_field));
            yearEditText_bookEdit.requestFocus();
        } else if (!isValidYear(year)) {
            yearEditText_bookEdit.setError(getString(R.string.invalid_year_format));
            yearEditText_bookEdit.requestFocus();
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
                                uploadPhoto(docId);
                            }
                            positiveToast("Book updated!");
                            getParentFragmentManager().popBackStack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            negativeToast("Error updating book");
                        }
                    });
        }
    }

    public void uploadPhoto(String docId){
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
                                  //  Toast.makeText(getContext(), "Book and image updated successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                  //  Toast.makeText(getContext(), "Error updating photo URL in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  //  Toast.makeText(getContext(), "Error getting download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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




