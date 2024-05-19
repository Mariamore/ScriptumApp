package com.example.scriptumapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.io.Files;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{

    private ImageView personalDataButton, uploadBookButton, checkBookButton, locationButton, logoutButton;
    private CircleImageView userImage;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String userId;
    private Uri imageUri;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        imageUri = result.getData().getData();
                        Glide.with(ProfileFragment.this).load(imageUri).into(userImage);
                        uploadFile();
                    }
                }
            }
    );

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        // Infla el diseño para este fragmento
       View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

       initializeVariables(rootView);
       setListeners();
       loadData();

        return rootView;
    }

    /**
     * Maneja los clicks en los botones.
     *
     * @param v La vista en la que se hizo click
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.userButton){
            replaceFragment(new PersonalDataFragment());
        }else if (id == R.id.uploadButton){
            replaceFragment(new UploadBookFragment());
        } else if (id == R.id.consultButton){
            //fragment de consultar libros
            replaceFragment(new QueriesFragment());
        } else if (id == R.id.ubiButton){
            replaceFragment(new LocationFragment());
        } else if (id == R.id.logoutButton){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // Cerrar sesión del usuario actual
                mAuth.signOut();
                positiveToast("User logged out");
                replaceFragment(new LoginFragment());
            } else {
                negativeToast("No user is logged in");
            }
        } else if (id == R.id.userLogo){
            openFileChooser();
        }
    }

    /**
     * Reemplaza el fragmento actual por otro.
     *
     * @param fragment El nuevo fragmento a mostrar.
     */
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    /**
     * Abre el selector de archivos para seleccionar una imagen.
     */
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    /**
     * Sube la imagen seleccionada a Firebase Storage y actualiza el perfil del usuario en Firestore.
     */
    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + Files.getFileExtension(imageUri.toString()));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userId = mAuth.getCurrentUser().getUid();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("profileImageUrl", uri.toString());

                                    db.collection("usersData")
                                            .whereEqualTo("user", userId)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    if (!queryDocumentSnapshots.isEmpty()) {
                                                        // Obtiene el primer documento retornado por la consulta
                                                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                                                        // Obtiene el ID del documento
                                                        String id = documentSnapshot.getId();

                                                        // Actualiza los datos del documento
                                                        db.collection("usersData").document(id)
                                                                .update(updates)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        // Muestra un mensaje de éxito si la actualización fue exitosa
                                                                        positiveToast("User photo uploaded");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        // Muestra un mensaje de error si la actualización falla
                                                                        negativeToast("Error uploading user photo");
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    });
        }
    }

    /**
     * Muestra un mensaje de notificación positivo (toast) en la pantalla.
     *
     * @param message El mensaje que se mostrará en el toast.
     */
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

    /**
     * Muestra un mensaje de notificación negativo (toast) en la pantalla.
     *
     * @param message El mensaje que se mostrará en el toast.
     */
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

    /**
     * Inicializa las variables necesarias para la funcionalidad del fragmento.
     *
     * @param rootView La vista raíz del fragmento donde se buscarán los componentes de UI.
     */
    private void initializeVariables(View rootView){
        // Inicialización de referencias Firebase
        storageReference = FirebaseStorage.getInstance().getReference("userProfileImages");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        db = FirebaseFirestore.getInstance();

        // Inicialización de componentes de UI
        personalDataButton = rootView.findViewById(R.id.userButton);
        uploadBookButton = rootView.findViewById(R.id.uploadButton);
        checkBookButton = rootView.findViewById(R.id.consultButton);
        locationButton = rootView.findViewById(R.id.ubiButton);
        logoutButton = rootView.findViewById(R.id.logoutButton);
        userImage = rootView.findViewById(R.id.userLogo);
    }

    /**
     * Configura los listeners para los botones del fragmento.
     */
    private void setListeners(){
        // Configuración de listeners para los botones
        personalDataButton.setOnClickListener(this);
        uploadBookButton.setOnClickListener(this);
        checkBookButton.setOnClickListener(this);
        locationButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        userImage.setOnClickListener(this);
    }

    /**
     * Carga los datos necesarios, como la imagen de perfil del usuario, si existe.
     */
    private void loadData(){
        // Cargar la imagen de perfil del usuario si existe
        db.collection("usersData")
                .whereEqualTo("user", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                            if (profileImageUrl != null) {
                                Glide.with(ProfileFragment.this).load(profileImageUrl).into(userImage);
                            }
                        }
                    }
                });

    }
}
