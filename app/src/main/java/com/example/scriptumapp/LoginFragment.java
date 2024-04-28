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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseAuth mAuth;
    Button loginButton;
    TextView signUp, forgotPassword;

    EditText emailInputEdittext, passwordInputEditText;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        loginButton = rootView.findViewById(R.id.loginButton);
        signUp = rootView.findViewById(R.id.signUp);
        forgotPassword = rootView.findViewById(R.id.forgotPassword);
        emailInputEdittext = rootView.findViewById(R.id.emailEditText);
        passwordInputEditText = rootView.findViewById(R.id.passEditText);


        loginButton.setOnClickListener(this);
        signUp.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);

        return rootView;

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.loginButton) {

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

                //login en Firebase
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.toast_layout_ok,
                                            requireActivity().findViewById(R.id.toastLayoutOk));
                                    TextView txtMsg = layout.findViewById(R.id.toastMessage);
                                    txtMsg.setText(R.string.user_logged);
                                    Toast toast = new Toast(requireContext());
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();

                                    replaceFragment(new ProfileFragment());
                                } else {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.toast_layout_fail,
                                            requireActivity().findViewById(R.id.toastLayoutFail));
                                    TextView txtMsg = layout.findViewById(R.id.toastMessage);
                                    txtMsg.setText(R.string.incorrect_name_pass);
                                    Toast toast = new Toast(requireContext());
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();
                                }
                            }
                        });


            }
            // Hacer un intent para pasar a la MainActivity

        } else if (id == R.id.signUp) {
            replaceFragment(new SignUpFragment());

        } else if (id == R.id.forgotPassword){
            //Hacer en firebase lo del olvido de contraseña
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

}