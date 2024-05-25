package com.example.scriptumapp;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FullScreenImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FullScreenImageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button backButton;
    private ImageView fullScreenImageView;
    public FullScreenImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FullScreenImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FullScreenImageFragment newInstance(String param1, String param2) {
        FullScreenImageFragment fragment = new FullScreenImageFragment();
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
        View rootview =inflater.inflate(R.layout.fragment_full_screen_image, container, false);

        fullScreenImageView = rootview.findViewById(R.id.fullScreenImageView);
        backButton = rootview.findViewById(R.id.backButton);

        Bundle bundle = getArguments();
        if (bundle != null) {
           String photoUrl= bundle.getString("photoUrl");

           if (photoUrl !=null && !photoUrl.isEmpty()) {
               FirebaseStorage storage = FirebaseStorage.getInstance();

               // Aquí debes proporcionar la URL de tu imagen en el storage
               StorageReference storageRef = storage.getReferenceFromUrl(photoUrl);

               // Descarga la imagen y muestra en el ImageView
               storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       // Uri de la imagen descargada
                       String imageUrl = uri.toString();

                       // Usar una biblioteca de carga de imágenes como Picasso o Glide para cargar la imagen en el ImageView
                       // Por ejemplo, con Picasso:
                       Picasso.get().load(imageUrl).into(fullScreenImageView);
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       // Manejo de errores al descargar la imagen
                       // Por ejemplo, puedes mostrar una imagen de carga predeterminada o un mensaje de error en el ImageView
                       fullScreenImageView.setImageResource(R.drawable.photobook);
                   }
               });
           } else {
               fullScreenImageView.setImageResource(R.drawable.photobook);
           }


        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Retroceder al fragmento anterior en la pila de retroceso
                fragmentManager.popBackStack();
            }
        });

        return rootview;
    }


}