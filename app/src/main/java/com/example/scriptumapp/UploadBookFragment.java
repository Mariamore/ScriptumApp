package com.example.scriptumapp;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadBookFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int COD_SEL_IMAGE =300;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageButton bookUploadButton, rectanglePhotoBook;
    EditText titleBookEditText, authorEditText, editorialEditText, publicationYearEditText, currentStatus, commentsEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String idUser;

    StorageReference storageReference;


    private Uri imageUri;


    Spinner spinner;

    public UploadBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadBookFragment newInstance(String param1, String param2) {
        UploadBookFragment fragment = new UploadBookFragment();
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_upload_book, container, false);

        bookUploadButton = rootView.findViewById(R.id.bookuploadbutton);
        rectanglePhotoBook = rootView.findViewById(R.id.rectangle_photobook);//
        titleBookEditText = rootView.findViewById(R.id.titleEditText);
        authorEditText = rootView.findViewById(R.id.authorEditText);
        editorialEditText = rootView.findViewById(R.id.editorialEditText);
        publicationYearEditText = rootView.findViewById(R.id.yearEditText);
        currentStatus = rootView.findViewById(R.id.currentStatusEditText);

        spinner = rootView.findViewById(R.id.spinner);//spinner
        //Opciones Spinner
        List<String> spinnnerOp = new ArrayList<>();
        spinnnerOp.add("loan");
        spinnnerOp.add("exchange");
        spinnnerOp.add("gift");
        //utilizamos ArrayAdpter para adaptar los datos de Spinner
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnnerOp);
        //Diseño del despliegue
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);


        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(); //referencia

        bookUploadButton.setOnClickListener(this);
        rectanglePhotoBook.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.rectangle_photobook){
            selectImage();
        } else if (id == R.id.bookuploadbutton) {
            String title = titleBookEditText.getText().toString();
            String author = authorEditText.getText().toString();
            String editorial = editorialEditText.getText().toString();
            String publicationYear = publicationYearEditText.getText().toString();
            String status = currentStatus.getText().toString();
            String spinnerSelection = spinner.getSelectedItem().toString();//Seleccionamos la opcion

            String bookId = UUID.randomUUID().toString();

            // Add a new document with a generated id.
            Map<String, Object> data = new HashMap<>();
            data.put("title", title);
            data.put("author", author);
            data.put("editorial", editorial);
            data.put("year", publicationYear);
            data.put("status", status);
            data.put("user", idUser);

            db.collection("users").document(idUser).collection(spinnerSelection).document(bookId).
            set(data)
            //DocumentReference reDocument = db.collection("user").document(idUser).collection(spinnerSelection).document();
            //reDocument.set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override
                        public void onSuccess(Void unused) {

                            uploadPhoto(bookId, spinnerSelection);

                           // replaceFragment(new SavedBooksFragment());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error saving book data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }
    //selecionamos la imagen
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, COD_SEL_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == COD_SEL_IMAGE && data != null) {
            imageUri = data.getData();
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                rectanglePhotoBook.setImageURI(imageUri);
            }
        }
    }

    private void uploadPhoto(String bookId, String category) {

        if (imageUri == null) { // Asegurarse de que la imagen se ha seleccionado
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        String imageName = bookId + ".jpg";
        StorageReference imageRef = storageReference.child("users/" + idUser + "/" + category + "/" + imageName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();

                                Map<String, Object> update = new HashMap<>();
                                update.put("photo", imageUrl);
                                db.collection("users").document(idUser).collection(category).document(bookId)
                                        .update(update)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "Image uploaded and link saved in Firestore successfully", Toast.LENGTH_SHORT).show();
                                                replaceFragment(new QueriesFragment());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Error updating 'photo' field in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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



    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager(); // Obtiene el FragmentManager del padre
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(this.getId(), fragment); // Reemplaza el fragmento actual con el nuevo fragmento
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



    }


