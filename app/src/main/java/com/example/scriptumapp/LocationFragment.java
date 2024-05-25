package com.example.scriptumapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button searchButton, backButton, saveButton;
    private EditText addressInput;
    private WebView webView;
    private FirebaseAuth mAuth;
    private String idUser;
    public double longitude, latitude;
    private FirebaseFirestore db;
    private String lastSearchedAddress = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public LocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);
        loadData(rootView);
        return rootView;
    }

    /**
     * Carga los datos necesarios y configura la vista del fragmento.
     *
     * @param rootView La vista raíz del fragmento.
     */
    private void loadData(View rootView){

        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        webView = rootView.findViewById(R.id.webView);
        addressInput = rootView.findViewById(R.id.searchAddressTextInput);

        db = FirebaseFirestore.getInstance();
        db.collection("usersData")
                .whereEqualTo("user", idUser)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {

                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            latitude = documentSnapshot.getDouble("latitude");
                            longitude = documentSnapshot.getDouble("longitude");
                            String address = documentSnapshot.getString("address");
                            addressInput.setText(address);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.getSettings().setAllowContentAccess(true);
                            webView.getSettings().setAllowFileAccess(true);
                            webView.addJavascriptInterface(LocationFragment.this, "Android");
                            webView.setWebViewClient(new WebViewClient());

                            if(latitude != 0 && longitude != 0){
                                //si el user no tiene dirección asociada, sale una por defecto
                                webView.loadUrl("file:///android_res/raw/mapuser.html");

                            } else {
                                webView.loadUrl("file:///android_res/raw/map.html");
                            }

                            searchButton = rootView.findViewById(R.id.searchButton);
                            backButton = rootView.findViewById(R.id.backButton);
                            saveButton = rootView.findViewById(R.id.saveButton);

                            searchButton.setOnClickListener(LocationFragment.this);
                            saveButton.setOnClickListener(LocationFragment.this);
                            backButton.setOnClickListener(LocationFragment.this);

                        } else {
                            negativeToast(getString(R.string.the_document_does_not_exist));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        negativeToast(getString(R.string.the_document_does_not_exist));
                    }
                });

    }

    /**
     * Obtiene la latitud.
     *
     * @return La latitud actual.
     */
    @JavascriptInterface
    public double getLatitude(){
        return latitude;
    }

    /**
     * Obtiene la longitud.
     *
     * @return La longitud actual.
     */
    @JavascriptInterface
    public double getLongitude(){
        return longitude;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.searchButton){
            String address = addressInput.getText().toString();
            if (address.isEmpty()){
                addressInput.setError(getString(R.string.enter_a_valid_address));
            }
            // Almacenar el contenido del addressInput
            lastSearchedAddress = address;
            webView.evaluateJavascript("searchAddress('" + address + "')", null);

        } else if (id == R.id.saveButton){
            // Obtener el contenido actual del addressInput
            String currentAddress = addressInput.getText().toString();
            // Comparar el contenido actual con el último contenido buscado
            if (!currentAddress.equals(lastSearchedAddress)) {
                negativeToast(getString(R.string.you_must_search_the_address_first));
            } else {
                db.collection("usersData")
                        .whereEqualTo("user", idUser)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    String documentId = documentSnapshot.getId();
                                    updateDocument(documentId);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                negativeToast(getString(R.string.the_document_does_not_exist));
                            }
                        });


                replaceFragment(new ProfileFragment());
            }
        } else if (id == R.id.backButton){
            replaceFragment(new ProfileFragment());
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Establece nuevas coordenadas y las guarda en las variables de instancia.
     *
     * @param newLatitude  La nueva latitud.
     * @param newLongitude La nueva longitud.
     */
    @JavascriptInterface
    public void setCoordinates(double newLatitude, double newLongitude) {
        this.latitude = newLatitude;
        this.longitude = newLongitude;
       String addressWithCoordinates = String.format(Locale.getDefault(), "%.6f, %.6f", latitude, longitude);
        if (latitude == 0 && longitude == 0){
            negativeToast(getString(R.string.enter_a_valid_address));
        }
    }

    /**
     * Actualiza el documento de Firestore con los nuevos datos.
     *
     * @param documentId El ID del documento a actualizar.
     */
    private void updateDocument(String documentId) {
        DocumentReference docRef = db.collection("usersData").document(documentId);

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("latitude", latitude);
        updatedData.put("longitude", longitude);
        updatedData.put("address",  addressInput.getText().toString());

        docRef.update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        positiveToast(getString(R.string.address_updated));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        negativeToast(getString(R.string.error_updating_address));
                    }
                });
    }

    /**
     * Muestra un mensaje de toast negativo.
     *
     * @param message El mensaje a mostrar.
     */
    private void negativeToast(String message){
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout_fail,
                (ViewGroup) requireActivity().findViewById(R.id.toastLayoutFail));
        TextView txtMsg = layout.findViewById(R.id.toastMessage);
        txtMsg.setText(message);
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * Muestra un mensaje de toast positivo.
     *
     * @param message El mensaje a mostrar.
     */
    private void positiveToast(String message){
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout_ok,
                (ViewGroup) requireActivity().findViewById(R.id.toastLayoutOk));
        TextView txtMsg = layout.findViewById(R.id.toastMessage);
        txtMsg.setText(message);
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}