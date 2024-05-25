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
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private Button resetButton, backButton;
    private EditText emailInputEdittext;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResetPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetPasswordFragment newInstance(String param1, String param2) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_reset_password, container, false);

        mAuth = FirebaseAuth.getInstance();
        resetButton = rootView.findViewById(R.id.resetButton);
        backButton = rootView.findViewById(R.id.goBackButton);
        emailInputEdittext = rootView.findViewById(R.id.emailEditText);

        backButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        return rootView;
    }



    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.resetButton){
            String email = emailInputEdittext.getText().toString();

            if(email.isEmpty()){
                emailInputEdittext.setError(getString(R.string.required_field));
                emailInputEdittext.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailInputEdittext.setError(getString(R.string.enter_a_valid_email));
                emailInputEdittext.requestFocus();
            } else {
                //Llamamos al método para resetear la contraseña
                resetPassword(email);
            }
        } else if (id == R.id.goBackButton){
            replaceFragment(new LoginFragment());
        }
    }

    //Creamos el método para resetear la contraseña

        // Enviamos el correo
        private void resetPassword(String email){
            // Enviamos el correo
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(requireActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout_ok,
                            requireActivity().findViewById(R.id.toastLayoutOk));
                    TextView txtMsg = layout.findViewById(R.id.toastMessage);
                    txtMsg.setText(R.string.reset_password);
                    Toast toast = new Toast(requireContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            });
        }


}
