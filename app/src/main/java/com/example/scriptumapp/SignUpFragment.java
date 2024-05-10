package com.example.scriptumapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUpFragment extends Fragment implements View.OnClickListener{

    // Argumentos para pasar al fragmento si fuese necesario
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Variables
    Button createAccButton;
    TextView exit;
    EditText emailInputEdittext, passwordInputEditText;
    private FirebaseAuth mAuth;


    // Argumentos para almacenar valores para pasárselos al fragmento si fuese necesario
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    // Método para crear una nueva instancia del fragmento con argumentos que se le pasen, si fuese necesario
    public static SignUpFragment newInstance(String param1, String param2) {
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailInputEdittext = rootView.findViewById(R.id.emailInputEditText);
        passwordInputEditText = rootView.findViewById(R.id.passwordInputEdittext);
        createAccButton = rootView.findViewById(R.id.createAccountButton);
        exit = rootView.findViewById(R.id.exitText);

        createAccButton.setOnClickListener(this);
        exit.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.createAccountButton) {

            String email = emailInputEdittext.getText().toString();
            String password = passwordInputEditText.getText().toString();

            if(email.isEmpty()){
                emailInputEdittext.setError("Required field");
                emailInputEdittext.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailInputEdittext.setError("Enter a valid email");
                emailInputEdittext.requestFocus();
            } else if (password.isEmpty()){
                passwordInputEditText.setError("Required field");
                passwordInputEditText.requestFocus();
            } else if(password.length() < 6){
                passwordInputEditText.setError("6 characters minimum");
                passwordInputEditText.requestFocus();
            } else{

                //crear cuenta en firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    LayoutInflater inflater = requireActivity().getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.toast_layout_ok,
                                            (ViewGroup) requireActivity().findViewById(R.id.toastLayoutOk));
                                    TextView txtMsg = layout.findViewById(R.id.toastMessage);
                                    txtMsg.setText(R.string.account_created);
                                    Toast toast = new Toast(requireContext());
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();
                                    // Cambiar Fragment a Profile
                                    replaceFragment(new ProfileFragment());
                                } else {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.toast_layout_fail,
                                            requireActivity().findViewById(R.id.toastLayoutFail));
                                    TextView txtMsg = layout.findViewById(R.id.toastMessage);
                                    txtMsg.setText(R.string.sign_up_failed);
                                    Toast toast = new Toast(requireContext());
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();
                                }
                            }
                        });
                }
        } else if (id == R.id.exitText) {
            // Cambiar Fragment a Login
            replaceFragment(new LoginFragment());
        }
    }

    // Método para poder cambiar de fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}