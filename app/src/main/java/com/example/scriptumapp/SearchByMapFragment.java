package com.example.scriptumapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchByMapFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private WebView webView;
    private EditText locationEditText;
    private Button searchButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser userF;
    private String user, userIdMarker, userId;
    private List<String> users = new ArrayList<>();
    private List<Double[]> locations = new ArrayList<>();

    public SearchByMapFragment() {
        // Required empty public constructor
    }

    public static SearchByMapFragment newInstance(String param1, String param2) {
        SearchByMapFragment fragment = new SearchByMapFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_by_map, container, false);

        webView = rootView.findViewById(R.id.webView);
        locationEditText = rootView.findViewById(R.id.locationEditText);
        searchButton = rootView.findViewById(R.id.searchButton);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userF = mAuth.getCurrentUser();
        userId = userF.getUid();

        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "Android");
        webView.loadUrl("file:///android_res/raw/searchmap.html");

        obtenerUbicacionesDeFirebase();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = locationEditText.getText().toString();
                if (address.isEmpty()) {
                    locationEditText.setError(getString(R.string.enter_an_address));
                }
                webView.evaluateJavascript("searchAddress('" + address + "')", null);
            }
        });

        return rootView;
    }

    private void obtenerUbicacionesDeFirebase() {
        db.collection("booksData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    users.clear();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        if (document.contains("user") && !document.getString("user").equals(userId)) {
                            user = document.getString("user");
                            if (user != null) {
                                users.add(user);
                            }
                        }
                    }
                    Log.d("Firebase", "Users obtained: " + users);
                    obtainUsersLocations();
                }
            }
        });
    }

    private void obtainUsersLocations() {
        db.collection("usersData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    locations.clear();

                    if (!isAdded()) return;

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        if (users.contains(document.getString("user"))) {
                            if (document.contains("latitude") && document.contains("longitude")) {
                                Double latitude = document.getDouble("latitude");
                                Double longitude = document.getDouble("longitude");
                                if (latitude != null && longitude != null) {
                                    String jsCode = "addUserLocation(" + latitude + ", " + longitude + ")";
                                    Log.d("Firebase", "Evaluating JS: " + jsCode);
                                    webView.evaluateJavascript(jsCode, new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                            Log.d("JavaScript", "JS Result: " + value);
                                        }
                                    });
                                    locations.add(new Double[]{latitude, longitude});
                                }
                            }
                        }
                    }
                } else {
                    Log.e("Firebase", "Error getting locations", task.getException());
                }
            }
        });
    }

    @JavascriptInterface
    public void onMarkerClick(double lat, double lon) {
        if (!isAdded()) return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String s = Double.toString(lat) + ", " + Double.toString(lon);

                db.collection("usersData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();

                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                if (document.contains("latitude") && document.contains("longitude")) {
                                    Double latitude = document.getDouble("latitude");
                                    Double longitude = document.getDouble("longitude");

                                    if (latitude == lat && longitude == lon) {
                                        userIdMarker = document.getString("user");
                                    }
                                }
                            }

                            Bundle bundle = new Bundle();
                            bundle.putString("userMarkerId", userIdMarker);
                            UsersBooksFragment fragment = new UsersBooksFragment();
                            fragment.setArguments(bundle);

                            FragmentManager fragmentManager = getParentFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        } else {
                            Log.e("Firebase", "Error getting user data", task.getException());
                        }
                    }
                });
            }
        });
    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}