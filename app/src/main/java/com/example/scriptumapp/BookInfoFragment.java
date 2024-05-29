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

    private static final String ARG_IMAGE_URL = "imageUrl";
    private String imageUrl;

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

    public static BookInfoFragment newInstance(String imageUrl, String param2) {
        BookInfoFragment fragment = new BookInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_info, container, false);
        initializeVariables(rootView);
        setListeners();
        loadData(); // Llama a loadData sin parámetros

        return rootView;
    }

    private void initializeVariables(View rootView) {
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

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        backButton.setOnClickListener(this);
        contactButton.setOnClickListener(this);
        bookPhoto.setOnClickListener(this);
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

    private void loadData() {
        if (imageUrl == null) {
            negativeToast("La URL de la imagen es nula.");
            return;
        }

        // Query para obtener el libro correspondiente a la URL de la imagen
        Query query = db.collection("booksData").whereEqualTo("photo", imageUrl);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                    bookTitleString = documentSnapshot.getString("title");
                    bookAuthorString = documentSnapshot.getString("author");
                    bookEditorialString = documentSnapshot.getString("editorial");
                    bookStatusString = documentSnapshot.getString("type");
                    final String bookUserString = documentSnapshot.getString("user");
                    bookYearString = documentSnapshot.getString("year");

                    usersCollection = db.collection("usersData");
                    Query userQuery = usersCollection.whereEqualTo("user", bookUserString);
                    userQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                        bookPhoto.setImageResource(R.drawable.photobook);
                    }
                } else {
                    negativeToast(getString(R.string.the_document_does_not_exist));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                negativeToast(getString(R.string.error_retrieving_document));
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backButton) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        } else if (id == R.id.contactButton) {
            Bundle bundle = new Bundle();
            bundle.putString("userIdReceptor", bookUserString);
            MessagesChatFragment messagesChatFragment = new MessagesChatFragment();
            messagesChatFragment.setArguments(bundle);
            replaceFragment(messagesChatFragment);
        } else if (id == R.id.bookPhoto) {
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void negativeToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout_fail,
                requireActivity().findViewById(R.id.toastLayoutFail));
        TextView txtMsg = layout.findViewById(R.id.toastMessage);
        txtMsg.setText(message);
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @JavascriptInterface
    public double getLatitude() {
        return latitude;
    }

    @JavascriptInterface
    public double getLongitude() {
        return longitude;
    }
}


