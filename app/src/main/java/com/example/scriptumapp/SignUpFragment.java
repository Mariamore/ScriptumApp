package com.example.scriptumapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SignUpFragment extends Fragment implements View.OnClickListener {

    // Argumentos para pasar al fragmento si fuese necesario
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";




    // Variables
    private Button createAccButton;
    private TextView exit;
    private EditText emailInputEdittext, passwordInputEditText, nameInputEditText, addressInputEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String idUser, addressReceived;
    private Float latitude, longitude;


    // Argumentos para almacenar valores para pasárselos al fragmento si fuese necesario
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    // Método para crear una nueva instancia del fragmento con argumentos que se le pasen, si fuese necesario
    public static SignUpFragment newInstance(String param1, String param2, String savedNameSurname, String savedEmail, String savedAddress, String savedPassword) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

            getParentFragmentManager().setFragmentResultListener("key", this, new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                    addressReceived = bundle.getString("address");
                    if (addressInputEditText != null) {
                        addressInputEditText.setText(addressReceived);
                    }
                    latitude = bundle.getFloat("latitude");
                    longitude = bundle.getFloat("longitude");
                }
            });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        initializeVariables(rootView);
        setListeners();

        return rootView;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.createAccountButton) {

            String email = emailInputEdittext.getText().toString();
            String password = passwordInputEditText.getText().toString();
            String address = addressInputEditText.getText().toString();
            String nameSurname = nameInputEditText.getText().toString();

            if (nameSurname.isEmpty()){
                nameInputEditText.setError(getString(R.string.required_field));
                nameInputEditText.requestFocus();
            } else if (address.isEmpty()){
                addressInputEditText.setError(getString(R.string.required_field));

            } else if(email.isEmpty()){
                emailInputEdittext.setError(getString(R.string.required_field));
                emailInputEdittext.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailInputEdittext.setError(getString(R.string.enter_a_valid_email));
                emailInputEdittext.requestFocus();
            } else if (password.isEmpty()){
                passwordInputEditText.setError(getString(R.string.required_field));
                passwordInputEditText.requestFocus();
            } else if(password.length() < 6){
                passwordInputEditText.setError(getString(R.string._6_characters_minimum));
                passwordInputEditText.requestFocus();

            }else {
                    //crear cuenta en firebase
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        idUser = mAuth.getCurrentUser().getUid();
                                        //Guardar datos en colección
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("nameSurname",nameSurname);
                                        data.put("latitude", latitude);
                                        data.put("longitude", longitude);
                                        data.put("user", idUser);
                                        data.put("address", address);
                                        db.collection("usersData")
                                                .add(data)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        positiveToast(getString(R.string.account_created));
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        negativeToast(getString(R.string.sign_up_failed));
                                                    }
                                                });
                                        replaceFragment(new ProfileFragment());
                                    } else {
                                        negativeToast(getString(R.string.sign_up_failed));
                                    }
                                }
                            });
                }

        } else if (id == R.id.exitText) {
            replaceFragment(new LoginFragment());
        } else if (id == R.id.addressInputEditText){
            replaceFragment(new MapFragment());
        }
    }


    // Método para poder cambiar de fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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

    private void initializeVariables(View rootView){
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailInputEdittext = rootView.findViewById(R.id.emailInputEditText);
        passwordInputEditText = rootView.findViewById(R.id.passwordInputEditText);
        nameInputEditText = rootView.findViewById(R.id.nameSurnameInputeditText);
        addressInputEditText = rootView.findViewById(R.id.addressInputEditText);
        createAccButton = rootView.findViewById(R.id.createAccountButton);
        exit = rootView.findViewById(R.id.exitText);
        db = FirebaseFirestore.getInstance();
    }

    private void setListeners(){
        createAccButton.setOnClickListener(this);
        exit.setOnClickListener(this);
        addressInputEditText.setOnClickListener(this);
    }

}