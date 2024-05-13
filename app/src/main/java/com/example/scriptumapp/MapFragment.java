package com.example.scriptumapp;


import android.os.Bundle;

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

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements View.OnClickListener{



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private WebView webView;

    private float latitude;
    private float longitude;
    private EditText addressInput;
    private String addressWithCoordinates;
    private Button searchButton, backButton, saveButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
    }





    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        webView = rootView.findViewById(R.id.webView);
        addressInput = rootView.findViewById(R.id.searchAddressTextInput);
        searchButton = rootView.findViewById(R.id.searchButton);
        backButton = rootView.findViewById(R.id.backButton);
        saveButton = rootView.findViewById(R.id.saveButton);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "Android");
        webView.setWebViewClient(new WebViewClient());


        webView.loadUrl("file:///android_res/raw/map.html"); // Ruta al archivo HTML del mapa

        searchButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.searchButton){
            String address = addressInput.getText().toString();
            if (address.isEmpty()){
                addressInput.setError("Enter an address");

            }
            webView.evaluateJavascript("searchAddress('" + address + "')", null);

        } else if (id == R.id.saveButton){

            Bundle bundle = new Bundle();
            bundle.putString("address",addressInput.getText().toString());
            bundle.putFloat("latitude", latitude);
            bundle.putFloat("longitude", longitude);
            getParentFragmentManager().setFragmentResult("key",bundle);

            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

            // Retroceder al fragmento anterior en la pila de retroceso
            fragmentManager.popBackStack();
        } else if (id == R.id.backButton){
            replaceFragment(new SignUpFragment());
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

            // Retroceder al fragmento anterior en la pila de retroceso
            fragmentManager.popBackStack();
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    @JavascriptInterface
    public void setCoordinates(float latitude, float longitude) {
        // Guarda las coordenadas y la dirección en variables de instancia
        this.latitude = latitude;
        this.longitude = longitude;
        addressWithCoordinates = String.format(Locale.getDefault(), "%.6f, %.6f", latitude, longitude);
        if (latitude == 999.999999 && longitude == -999.999999){
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout_fail,
                    (ViewGroup) requireActivity().findViewById(R.id.toastLayoutFail));
            TextView txtMsg = layout.findViewById(R.id.toastMessage);
            txtMsg.setText("Enter a valid address");
            Toast toast = new Toast(requireContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            addressInput.setText("");
            addressInput.requestFocus();

        }


        //showToast("Coordenadas: " + addressWithCoordinates);
    }


    private void showToast(String message) {
        // Crea un nuevo Toast con el mensaje especificado
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

}