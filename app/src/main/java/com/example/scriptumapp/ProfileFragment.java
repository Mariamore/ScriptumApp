package com.example.scriptumapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{


    private ImageView personalDataButton, uploadBookButton, checkBookButton, locationButton, logoutButton;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
       View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        personalDataButton = rootView.findViewById(R.id.userButton);
        uploadBookButton = rootView.findViewById(R.id.uploadButton);
        checkBookButton = rootView.findViewById(R.id.consultButton);
        locationButton = rootView.findViewById(R.id.ubiButton);
        logoutButton = rootView.findViewById(R.id.logoutButton);
        personalDataButton.setOnClickListener(this);
        uploadBookButton.setOnClickListener(this);
        checkBookButton.setOnClickListener(this);
        locationButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.userButton){
            replaceFragment(new PersonalDataFragment());
        }else if (id == R.id.uploadButton){
            replaceFragment(new UploadBookFragment());
        } else if (id == R.id.consultButton){
            //fragment de consultar libros
        } else if (id == R.id.ubiButton){
            replaceFragment(new LocationFragment());
        } else if (id == R.id.logoutButton){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // Cerrar sesión del usuario actual
                mAuth.signOut();
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_layout_ok,
                        requireActivity().findViewById(R.id.toastLayoutOk));
                TextView txtMsg = layout.findViewById(R.id.toastMessage);
                txtMsg.setText("User logged out");
                Toast toast = new Toast(requireContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                replaceFragment(new LoginFragment());
            } else {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_layout_fail,
                        requireActivity().findViewById(R.id.toastLayoutFail));
                TextView txtMsg = layout.findViewById(R.id.toastMessage);
                txtMsg.setText("No user is logged in");
                Toast toast = new Toast(requireContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}