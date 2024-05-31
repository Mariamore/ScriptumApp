package com.example.scriptumapp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookInfoFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText bookTitleEditText, bookAuthorEditText, bookEditorialEditText, bookYearEditText, bookStatusEditText, bookUserEditText;
    private String bookTitleString, bookAuthorString, bookEditorialString, bookYearString, bookStatusString, bookUserString, photoUrl, userNameString;
    private ImageView bookPhoto;
    private Query query;
    private Double latitude, longitude;
    private CollectionReference usersCollection;
    private FirebaseFirestore db;
    private Button backButton, contactButton;
    private WebView webView;


    public BookInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookInfoFragment newInstance(String param1, String param2) {
        BookInfoFragment fragment = new BookInfoFragment();
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
        View rootView =inflater.inflate(R.layout.fragment_book_info, container, false);
        initializeVariables(rootView);
        setListeners();
        loadData();

        return rootView;
    }

    /**
     * Maneja los clicks en las vistas.
     *
     * @param v La vista que ha sido seleccionada mediante click.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.backButton){
            //Para volver al fragment anterior
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        } else if (id == R.id.contactButton){
            //Iniciamos un chat individual con el usuario
            Bundle bundle = new Bundle();
            bundle.putString("userIdReceptor", bookUserString);
            MessagesChatFragment messagesChatFragment = new MessagesChatFragment();
            messagesChatFragment.setArguments(bundle);
            replaceFragment(messagesChatFragment);
        } else if (id == R.id.bookPhoto){
            if (photoUrl != null && !photoUrl.isEmpty()) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FullScreenImageFragment fullScreenImageFragment = new FullScreenImageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("photoUrl", photoUrl);
                fullScreenImageFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.frame_layout, fullScreenImageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
                negativeToast(getString(R.string.there_s_no_image));
            }


        }
    }

    /**
     * Reemplaza el fragmento actual con el fragmento proporcionado.
     *
     * @param fragment El fragmento que se debe mostrar.
     */
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    /**
     * Devuelve la latitud almacenada para su uso en mapUser.html.
     *
     * @return La latitud almacenada.
     */
    @JavascriptInterface
    public double getLatitude(){
        return latitude;
    }

    /**
     * Devuelve la longitud almacenada para su uso en mapUser.html.
     *
     * @return La longitud almacenada.
     */
    @JavascriptInterface
    public double getLongitude(){
        return longitude;
    }

    /**
     * Muestra un Toast con un mensaje negativo.
     *
     * @param message El mensaje que se mostrará en el Toast.
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
     * Inicializa las variables de la interfaz de usuario.
     *
     * @param rootView La vista raíz del fragmento.
     */
    private void initializeVariables(View rootView){
        bookTitleEditText = rootView.findViewById(R.id.bookTitleEditText);
        bookAuthorEditText = rootView.findViewById(R.id.bookAuthorEditText);
        bookEditorialEditText = rootView.findViewById(R.id.bookEditorialEditText);
        bookYearEditText = rootView.findViewById(R.id.bookYearEditText);
        bookStatusEditText = rootView.findViewById(R.id.bookStatusEditText);
        bookUserEditText = rootView.findViewById(R.id.bookUserEditText);

        backButton = rootView.findViewById(R.id.backButton);
        contactButton = rootView.findViewById(R.id.contactButton);

        bookPhoto = rootView.findViewById(R.id.bookPhoto);
        db = FirebaseFirestore.getInstance();

        webView = rootView.findViewById(R.id.webView);
    }

    /**
     * Establece los listeners para los botones y la WebView.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setListeners(){
        backButton.setOnClickListener(this);
        contactButton.setOnClickListener(this);
        bookPhoto.setOnClickListener(this);
        //para evitar el scroll en la pantalla al moverte por el mapa
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
    }

    /**
     * Carga los datos del libro seleccionado en el searchFragment anterior, utilizando el ID del libro extraido a través de un bundle.
     */
    private void loadData(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            String bookId = bundle.getString("bookId");
            DocumentReference docRef = db.collection("booksData").document(bookId);
            docRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                bookTitleString =  documentSnapshot.getString("title");
                                bookAuthorString = documentSnapshot.getString("author");
                                bookEditorialString = documentSnapshot.getString("editorial");
                                bookStatusString = documentSnapshot.getString("type");
                                bookUserString = documentSnapshot.getString("user");
                                bookYearString = documentSnapshot.getString("year");

                                usersCollection = db.collection("usersData");
                                query = usersCollection.whereEqualTo("user", bookUserString);
                                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                                userNameString = document.getString("nameSurname");
                                                bookUserEditText.setText(userNameString);
                                                latitude = document.getDouble("latitude");
                                                longitude = document.getDouble("longitude");
                                                webView.getSettings().setJavaScriptEnabled(true);
                                                webView.getSettings().setAllowContentAccess(true);
                                                webView.getSettings().setAllowFileAccess(true);
                                                webView.addJavascriptInterface(BookInfoFragment.this, "Android");
                                                webView.setWebViewClient(new WebViewClient());
                                                webView.loadUrl("file:///android_res/raw/mapuser.html");

                                            }
                                        }

                                    }
                                });

                                bookTitleEditText.setText(bookTitleString);
                                bookAuthorEditText.setText(bookAuthorString);
                                bookEditorialEditText.setText(bookEditorialString);
                                bookStatusEditText.setText(bookStatusString);
                                bookYearEditText.setText(bookYearString);

                                FirebaseStorage storage = FirebaseStorage.getInstance();

                                photoUrl = documentSnapshot.getString("photo");
                                if (photoUrl != null && !photoUrl.isEmpty()) {
                                    StorageReference storageRef = storage.getReferenceFromUrl(photoUrl);
                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Uri de la imagen descargada
                                            String imageUrl = uri.toString();
                                            Picasso.get().load(imageUrl).into(bookPhoto);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            bookPhoto.setImageResource(R.drawable.photobook);
                                        }
                                    });
                                } else {
                                    // Manejo del caso cuando la URL es nula o vacía
                                    bookPhoto.setImageResource(R.drawable.photobook);
                                }

                            } else {
                                negativeToast(getString(R.string.the_document_does_not_exist));
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            negativeToast(getString(R.string.error_retrieving_document));
                        }
                    });

        }
    }
}