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

public class SearchMenuFragment extends Fragment implements View.OnClickListener{

    private Button searchTextButton, searchMapButton;
    private FirebaseUser user;

    public SearchMenuFragment() {
        // Required empty public constructor
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