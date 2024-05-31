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

public class BookEditFragmentLoan extends Fragment {

    private EditText authorEditText_bookEditLoan, titleEditText_bookEditLoan, editorialEditText_bookEditLoan, yearEditText_bookEditLoan, statusEditText_bookEditLoan;
    private ImageView imageBook_bookEditLoan;
    private Button button_saveBookEditLoan;
    private FirebaseFirestore db;
    StorageReference stRe;
    private static final String DOC_ID = "docId";
    private String docId;
    private Uri imageUri;
    private static final String YEAR_REGEX = "^\\d{4}$";
    private static final Pattern YEAR_PATTERN = Pattern.compile(YEAR_REGEX);

    public BookEditFragmentLoan() {
        // Required empty public constructor
    }

    public static BookEditFragmentLoan newInstance(String docId) {
        BookEditFragmentLoan fragment = new BookEditFragmentLoan();
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
       View rootView = inflater.inflate(R.layout.fragment_book_edit_loan, container, false);

        authorEditText_bookEditLoan = rootView.findViewById(R.id.authorEditText_bookEditLoan);
        titleEditText_bookEditLoan  = rootView.findViewById(R.id.titleEditText_bookEditLoan);
        yearEditText_bookEditLoan = rootView.findViewById(R.id.yearEditText_bookEditLoan);
        imageBook_bookEditLoan = rootView.findViewById(R.id.rectangle_bookEditImageLoan);
        statusEditText_bookEditLoan = rootView.findViewById(R.id.currentStatusEditText_bookEditLoan);
        editorialEditText_bookEditLoan= rootView.findViewById(R.id.editorialEditText_bookEditLoan);
        button_saveBookEditLoan = rootView.findViewById(R.id.button_saveBookEditLoan);

        //Funcion para extraer los datos del libro
        dataBookLoan();

        //Selecionamos imagen en la nueva pantalla de edicion
        imageBook_bookEditLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selecionamos la imagen
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, COD_SEL_IMAGE);
            }
        });

        //Guardamos los datos nuevos
        button_saveBookEditLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Funcion para guardar los datos del libro
                saveBookEditLoan();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == COD_SEL_IMAGE && data != null) {
            imageUri = data.getData();
            imageBook_bookEditLoan.setImageURI(imageUri);
        }
    }

    /**
     * Obtiene los datos del libro de Firestore y los muestra en los campos de edición de intercambio.
     */
    private void dataBookLoan(){

        db.collection("booksData").document(docId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            titleEditText_bookEditLoan.setText(documentSnapshot.getString("title"));
                            authorEditText_bookEditLoan.setText(documentSnapshot.getString("author"));
                            editorialEditText_bookEditLoan.setText(documentSnapshot.getString("editorial"));
                            yearEditText_bookEditLoan.setText(documentSnapshot.getString("year"));
                            statusEditText_bookEditLoan.setText(documentSnapshot.getString("status"));
                            String imageUrl = documentSnapshot.getString("photo");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Picasso.get().load(imageUrl).into(imageBook_bookEditLoan);
                            }
                        } else {
                        //Toast.makeText(getContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getContext(), "Failed to load document", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Guarda los cambios realizados al editar el libro.
     */
    private void saveBookEditLoan(){

        String title = titleEditText_bookEditLoan.getText().toString();
        String author = authorEditText_bookEditLoan.getText().toString();
        String editorial = editorialEditText_bookEditLoan.getText().toString();
        String year = yearEditText_bookEditLoan.getText().toString();
        String status = statusEditText_bookEditLoan.getText().toString();

        if (title.isEmpty()){
            titleEditText_bookEditLoan.setError(getString(R.string.required_field));
            titleEditText_bookEditLoan.requestFocus();
        } else if (author.isEmpty()){
            authorEditText_bookEditLoan.setError(getString(R.string.required_field));
            authorEditText_bookEditLoan.requestFocus();
        } else if(editorial.isEmpty()){
            editorialEditText_bookEditLoan.setError(getString(R.string.required_field));
            editorialEditText_bookEditLoan.requestFocus();
        } else if (year.isEmpty()){
            yearEditText_bookEditLoan.setError(getString(R.string.required_field));
            yearEditText_bookEditLoan.requestFocus();
        } else if (!isValidYear(year)) {
            yearEditText_bookEditLoan.setError(getString(R.string.invalid_year_format));
            yearEditText_bookEditLoan.requestFocus();
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
                        public void onSuccess(Void unused) {
                            if (imageUri != null) {
                                uploadPhotoEditLoan(docId);
                            }
                            positiveToast(getString(R.string.book_updated));
                            getParentFragmentManager().popBackStack();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            negativeToast(getString(R.string.error_updating_book));
                        }
                    });
        }
    }

    /**
     * Sube la foto seleccionada a Firebase Storage y actualiza la URL de la imagen en Firestore.
     *
     * @param docId El ID del documento del libro del que se va a actualizar la foto.
     */
    private void uploadPhotoEditLoan(String docId){

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
                                                //Toast.makeText(getContext(), "Error updating photo URL in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                               // Toast.makeText(getContext(), "Error getting download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getContext(), "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Verifica si el año proporcionado es válido según un patrón específico.
     *
     * @param year El año a verificar.
     * @return True si el año es válido, False en caso contrario.
     */
    public static boolean isValidYear(String year) {
        if (year == null) {
            return false;
        }
        Matcher matcher = YEAR_PATTERN.matcher(year);
        return matcher.matches();
    }

    /**
     * Muestra un toast con un diseño personalizado para indicar una operación fallida.
     *
     * @param message El mensaje que se mostrará en el toast.
     */
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

    /**
     * Muestra un toast con un diseño personalizado para indicar una operación exitosa.
     *
     * @param message El mensaje que se mostrará en el toast.
     */
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