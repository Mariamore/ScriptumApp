package com.example.scriptumapp;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.io.Files;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    static final int COD_SEL_IMAGE =300;

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
    private long timestamp;
    private static final String YEAR_REGEX = "^\\d{4}$";
    private static final Pattern YEAR_PATTERN = Pattern.compile(YEAR_REGEX);

    // Method to check if the string is a valid year
    public static boolean isValidYear(String year) {
        if (year == null) {
            return false;
        }
        Matcher matcher = YEAR_PATTERN.matcher(year);
        return matcher.matches();
    }


    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        imageUri = result.getData().getData();
                        Glide.with(UploadBookFragment.this).load(imageUri).into(rectanglePhotoBook);

                    }
                }
            }
    );
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
        //utilizamos ArrayAdapter para adaptar los datos de Spinner
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getContext(), R.layout.spinner_item, spinnnerOp);
        //Diseño del despliegue
        adapterSpinner.setDropDownViewResource(R.layout.spinner_dropdown_item);
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

            if (title.isEmpty()){
                titleBookEditText.setError(getString(R.string.required_field));
                titleBookEditText.requestFocus();
            }else if (author.isEmpty()){
                authorEditText.setError(getString(R.string.required_field));
                authorEditText.requestFocus();
            } else if(editorial.isEmpty()){
                editorialEditText.setError(getString(R.string.required_field));
                editorialEditText.requestFocus();
            }else if (publicationYear.isEmpty()){
                publicationYearEditText.setError(getString(R.string.required_field));
                publicationYearEditText.requestFocus();
            } else if (!isValidYear(publicationYear)) {
                publicationYearEditText.setError("Invalid year format");
                publicationYearEditText.requestFocus();
            }else if (imageUri == null) {
                negativeToast("Please select an image");
                return;
            } else{
                String bookId = UUID.randomUUID().toString();
                timestamp = System.currentTimeMillis();

                // Add a new document with a generated id.
                Map<String, Object> data = new HashMap<>();
                data.put("bookId", bookId);
                data.put("title", title);
                data.put("author", author);
                data.put("editorial", editorial);
                data.put("year", publicationYear);
                data.put("status", status);
                data.put("type", spinnerSelection);
                data.put("user", idUser);
                data.put("timestamp", timestamp);

                db.collection("booksData")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                uploadPhoto(documentReference.getId());
                                positiveToast("Book uploaded");
                                titleBookEditText.setText("");
                                authorEditText.setText("");
                                editorialEditText.setText("");
                                publicationYearEditText.setText("");
                                currentStatus.setText("");
                                spinner.setSelection(0);
                                rectanglePhotoBook.setImageResource(R.drawable.photobook);


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                negativeToast("Error saving book data: " + e.getMessage());
                            }
                        });
            }


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



    private void uploadPhoto(String idUser) {

        if (imageUri == null) { // Asegurarse de que la imagen se ha seleccionado
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        String imageName = idUser + ".jpg";
        StorageReference imageRef = storageReference.child("booksData/" + idUser + "/" + imageName);


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
                                db.collection("booksData").document(idUser)
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
/*
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    /**
     * Sube la imagen seleccionada a Firebase Storage y actualiza el perfil del usuario en Firestore.

    private void uploadPhoto(String idUser) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + Files.getFileExtension(imageUri.toString()));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("photo", uri.toString());

                                    db.collection("booksData").document(idUser)
                                            .update(updates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //positiveToast("Image uploaded successfully");

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    negativeToast("Error updating 'photo' field in Firestore: " + e.getMessage());
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    negativeToast("Error getting download URL: " + e.getMessage());
                                }
                            });
                                }
                            });
                        }

        }
*/

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager(); // Obtiene el FragmentManager del padre
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(this.getId(), fragment); // Reemplaza el fragmento actual con el nuevo fragmento
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void negativeToast(String message) {
        // Usa inflater para inflar el diseño del toast
        LayoutInflater inflater = getLayoutInflater();
        // Infla el diseño personalizado del toast
        View layout = inflater.inflate(R.layout.toast_layout_fail,
                requireActivity().findViewById(R.id.toastLayoutFail));
        // Busca el TextView dentro del diseño del toast
        TextView txtMsg = layout.findViewById(R.id.toastMessage);
        // Establece el mensaje proporcionado en el TextView
        txtMsg.setText(message);
        // Crea y muestra el toast
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void positiveToast(String message) {
        // Obtiene el servicio de inflater para inflar el diseño del toast
        LayoutInflater inflater = getLayoutInflater();
        // Infla el diseño personalizado del toast
        View layout = inflater.inflate(R.layout.toast_layout_ok,
                requireActivity().findViewById(R.id.toastLayoutOk));
        // Busca el TextView dentro del diseño del toast
        TextView txtMsg = layout.findViewById(R.id.toastMessage);
        // Establece el mensaje proporcionado en el TextView
        txtMsg.setText(message);
        // Crea y muestra el toast
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}


