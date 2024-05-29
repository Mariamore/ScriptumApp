package com.example.scriptumapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchMenuFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button searchTextButton, searchMapButton;
    private FirebaseUser user;

    public SearchMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchMenuFragment newInstance(String param1, String param2) {
        SearchMenuFragment fragment = new SearchMenuFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_search_menu, container, false);
        searchMapButton = rootView.findViewById(R.id.searchMapButton);
        searchTextButton = rootView.findViewById(R.id.searchTextButton);
        user = FirebaseAuth.getInstance().getCurrentUser();

        searchTextButton.setOnClickListener(this);
        searchMapButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.searchTextButton){
            replaceFragment(new SearchFragment());
        } else if (id == R.id.searchMapButton){
            if(user!=null){
                replaceFragment(new SearchByMapFragment());
            } else {
                negativeToast("You need to be logged in");
            }

        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
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
}