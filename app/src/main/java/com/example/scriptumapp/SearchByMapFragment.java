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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchByMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchByMapFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private WebView webView;
    private EditText locationEditText;
    private Button searchButton;
    private FirebaseFirestore db;
    private String user, userIdMarker;
    private List<String> users = new ArrayList<>();
    List<Double[]> locations = new ArrayList<>();

    public SearchByMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchByMapFragment.
     */
    // TODO: Rename and change types and number of parameters
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_by_map, container, false);

        webView = rootView.findViewById(R.id.webView);
        locationEditText = rootView.findViewById(R.id.locationEditText);
        searchButton = rootView.findViewById(R.id.searchButton);

        db = FirebaseFirestore.getInstance();

        
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "Android");
        webView.loadUrl("file:///android_res/raw/searchmap.html");




       /* if (locations.size() > 0) {
            for (Double[] coordinates : locations) {
                Double latitude = coordinates[0];
                Double longitude = coordinates[1];

                // Ejecutar la función addUserLocation con las coordenadas actuales
                webView.evaluateJavascript("addUserLocation(" + latitude + ", " + longitude + ")", null);
            }
        } else {*/
            obtenerUbicacionesDeFirebase();
        //}
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = locationEditText.getText().toString();
                if (address.isEmpty()){
                    locationEditText.setError(getString(R.string.enter_an_address));
                }
                webView.evaluateJavascript("searchAddress('" + address + "')", null);

            }
        });

        return rootView;
    }
    /*
    private void obtenerUbicacionesDeFirebase() {

        db.collection("booksData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    users.clear();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        if (document.contains("user")) {
                            user = document.getString("user");
                            if (user != null) {
                                users.add(user);
                            }
                        }

                    }
                }
            }
                                                               });

        // Realiza una consulta a Firebase para obtener las ubicaciones de los usuarios
        db.collection("usersData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();



                    // Itera sobre los documentos y obtén las ubicaciones de los usuarios
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                            if(users.contains(document.getString("user"))){
                                if (document.contains("latitude") && document.contains("longitude")) {
                                    Double latitude = document.getDouble("latitude");
                                    Double longitude = document.getDouble("longitude");
                                    if (latitude != null && longitude != null) {
                                        // Envía la ubicación al mapa Leaflet a través de una llamada JavaScript
                                        webView.evaluateJavascript("addUserLocation(" + latitude + ", " + longitude + ")", null);
                                        locations.add(new Double[]{latitude, longitude});
                                    }
                                }
                            }


                    }

                } else {
                    // Maneja la falla de obtener las ubicaciones de Firebase
                }
            }
        });
    }



     */
    private void obtenerUbicacionesDeFirebase() {
        db.collection("booksData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    users.clear();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        if (document.contains("user")) {
                            user = document.getString("user");
                            if (user != null) {
                                users.add(user);
                            }
                        }
                    }
                    Log.d("Firebase", "Users obtained: " + users);
                }
            }
        });

        db.collection("usersData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    locations.clear();

                    //webView.evaluateJavascript("clearMarkers()", null);
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        if (users.contains(document.getString("user"))) {
                            if (document.contains("latitude") && document.contains("longitude")) {
                                Double latitude = document.getDouble("latitude");
                                Double longitude = document.getDouble("longitude");
                                if (latitude != null && longitude != null) {
                                    // Llama a la función JavaScript para agregar la ubicación
                                    String jsCode = "addUserLocation(" + latitude + ", " + longitude + ")";
                                    Log.d("Firebase", "Evaluating JS: " + jsCode); // Registro de depuración

                                    webView.evaluateJavascript(jsCode, new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                            Log.d("JavaScript", "JS Result: " + value); // Registro de depuración
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
    // Clase para permitir la comunicación entre JavaScript y Java

        @JavascriptInterface
        public void onMarkerClick(double lat, double lon) {
            // Maneja la latitud y longitud del marcador que se ha hecho clic
            // Aquí puedes hacer cualquier operación necesaria, como mostrar un Toast
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String s = Double.toString(lat) + ", " + Double.toString(lon);
                   negativeToast(s);
                    db.collection("usersData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                List<Double[]> locations = new ArrayList<>();


                                // Itera sobre los documentos y obtén las ubicaciones de los usuarios
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
                                bundle.putString("userMarkerId",userIdMarker);
                                // Crear una instancia de BookInfoFragment y asignar el Bundle
                                UsersBooksFragment fragment = new UsersBooksFragment();
                                fragment.setArguments(bundle);

                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                // Reemplazar el fragmento actual con BookInfoFragment
                                fragmentTransaction.replace(R.id.frame_layout,fragment);
                                fragmentTransaction.commit();


                            } else {
                                // Maneja la falla de obtener las ubicaciones de Firebase
                            }
                        }
                    });
                }
            });
    }



    /**
     * Muestra un mensaje de toast negativo.
     *
     * @param message El mensaje a mostrar.
     */
    private void negativeToast(String message){
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


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}