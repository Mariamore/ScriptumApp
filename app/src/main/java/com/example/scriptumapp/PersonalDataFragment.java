package com.example.scriptumapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.UpdateDocumentRequest;
import com.google.firestore.v1.UpdateDocumentRequestOrBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalDataFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView editNameButton, editEmailButton, editPasswordButton;

    private EditText nameEditText, emailEditText, passwordEditText;

    private Button saveButton, backButton;
    private FirebaseAuth mAuth;

    private FirebaseUser user;
    private FirebaseFirestore db;
    private String idUser, nameSurnameString, emailString, passwordString;
     public String newEmailString, newnameString, newPasswordString;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PersonalDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalDataFragment newInstance(String param1, String param2) {
        PersonalDataFragment fragment = new PersonalDataFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_personal_data, container, false);

        editNameButton = rootView.findViewById(R.id.editNameButton);
        editEmailButton = rootView.findViewById(R.id.editEmailButton);
        editPasswordButton = rootView.findViewById(R.id.editPasswordButton);

        nameEditText = rootView.findViewById(R.id.nameSurnameEditText);
        emailEditText = rootView.findViewById(R.id.emailEditText);
        passwordEditText = rootView.findViewById(R.id.passwordEditText);

        emailEditText.setFocusable(false);
        nameEditText.setFocusable(false);
        passwordEditText.setFocusable(false);

        saveButton = rootView.findViewById(R.id.saveButton);
        backButton = rootView.findViewById(R.id.backButton);

        editPasswordButton.setOnClickListener(this);
        editEmailButton.setOnClickListener(this);
        editNameButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        idUser = user.getUid();
        db = FirebaseFirestore.getInstance();

        db.collection("usersData")
                .whereEqualTo("user", idUser)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            nameSurnameString = documentSnapshot.getString("nameSurname");
                            emailString = user.getEmail();
                            passwordString = "password";

                            nameEditText.setText(nameSurnameString);
                            emailEditText.setText(emailString);
                            passwordEditText.setText(passwordString);

                        }
                    }
                });

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Verifica si el EditText tiene el foco y si se ha tocado fuera de él
                if (event.getAction() == MotionEvent.ACTION_DOWN && emailEditText.hasFocus()) {
                    // Quita el foco del EditText
                    emailEditText.clearFocus();
                    return true; // Indica que el evento ha sido manejado
                }
                return false; // Deja que otros controles manejen el evento si es necesario
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();


        if(id == R.id.editPasswordButton){
            passwordEditText.setFocusable(true);
            passwordEditText.setFocusableInTouchMode(true);
            passwordEditText.setText("");
            passwordEditText.requestFocus();
            newPasswordString = passwordEditText.getText().toString();
            passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        // El EditText ha perdido el foco, hacer lo que necesites aquí
                        String newPasswordString = passwordEditText.getText().toString();
                        if (newPasswordString.isEmpty()) {
                            passwordEditText.setText(passwordString);
                        }
                        passwordEditText.setFocusable(false);
                        passwordEditText.setFocusableInTouchMode(false);
                    }
                }
            });

        } else if (id == R.id.editNameButton){
            nameEditText.setFocusable(true);
            nameEditText.setFocusableInTouchMode(true);
            nameEditText.setText("");
            nameEditText.requestFocus();
            newnameString = nameEditText.getText().toString();
            nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        // El EditText ha perdido el foco, hacer lo que necesites aquí
                        String newNameString = nameEditText.getText().toString();
                        if (newNameString.isEmpty()) {
                            nameEditText.setText(nameSurnameString);
                        }
                        nameEditText.setFocusable(false);
                        nameEditText.setFocusableInTouchMode(false);
                    }
                }
            });

        } else if (id == R.id.editEmailButton){
            emailEditText.setFocusable(true);
            emailEditText.setFocusableInTouchMode(true);
            emailEditText.setText("");
            emailEditText.requestFocus();
            newEmailString = emailEditText.getText().toString();
            emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        // El EditText ha perdido el foco, hacer lo que necesites aquí
                        String newEmailString = emailEditText.getText().toString();
                        if (newEmailString.isEmpty()) {
                            emailEditText.setText(emailString);
                        }
                        emailEditText.setFocusable(false);
                        emailEditText.setFocusableInTouchMode(false);
                    }
                }
            });



        } else if (id == R.id.saveButton){
            newnameString = nameEditText.getText().toString();
            Map<String, Object> updates = new HashMap<>();
            updates.put("nameSurname", newnameString);
            db.collection("usersData")
                    .whereEqualTo("user", idUser)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(!queryDocumentSnapshots.isEmpty()){
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                              String id =  documentSnapshot.getId();

                              db.collection("usersData").document(id)
                                      .update(updates)
                                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void aVoid) {
                                             /* LayoutInflater inflater = getLayoutInflater();
                                              View layout = inflater.inflate(R.layout.toast_layout_ok,
                                                      requireActivity().findViewById(R.id.toastLayoutOk));
                                              TextView txtMsg = layout.findViewById(R.id.toastMessage);
                                              txtMsg.setText("Bien");
                                              Toast toast = new Toast(requireContext());
                                              toast.setDuration(Toast.LENGTH_LONG);
                                              toast.setView(layout);
                                              toast.show();

                                              */
                                          }
                                      })
                                      .addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                              LayoutInflater inflater = getLayoutInflater();
                                              View layout = inflater.inflate(R.layout.toast_layout_ok,
                                                      requireActivity().findViewById(R.id.toastLayoutOk));
                                              TextView txtMsg = layout.findViewById(R.id.toastMessage);
                                              txtMsg.setText("Mal");
                                              Toast toast = new Toast(requireContext());
                                              toast.setDuration(Toast.LENGTH_LONG);
                                              toast.setView(layout);
                                              toast.show();
                                          }
                                      });


                            }
                        }
                    });
            newPasswordString = passwordEditText.getText().toString();
            user = FirebaseAuth.getInstance().getCurrentUser();
            user.updatePassword(newPasswordString)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.toast_layout_ok,
                                        requireActivity().findViewById(R.id.toastLayoutOk));
                                TextView txtMsg = layout.findViewById(R.id.toastMessage);
                                txtMsg.setText("Bien");
                                Toast toast = new Toast(requireContext());
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                            }
                            else {
                                // Error al actualizar la contraseña
                                // Puedes obtener más detalles sobre el error utilizando task.getException()
                                Exception e = task.getException();
                                if (e != null) {
                                    Log.e("Firebase", "Error updating password: " + e.getMessage());
                                }
                                Toast.makeText(requireContext(), "Error updating password", Toast.LENGTH_SHORT).show();
                            }

                        }


                    });
            newEmailString = emailEditText.getText().toString();

          user.verifyBeforeUpdateEmail(newEmailString)
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful()) {
                              // Correo electrónico enviado para verificar la nueva dirección
                              // El usuario debe confirmar el cambio antes de que se haga efectivo
                              // El correo electrónico de confirmación se enviará a newEmailString
                              LayoutInflater inflater = getLayoutInflater();
                              View layout = inflater.inflate(R.layout.toast_layout_ok,
                                      requireActivity().findViewById(R.id.toastLayoutOk));
                              TextView txtMsg = layout.findViewById(R.id.toastMessage);
                              txtMsg.setText("Email Updated");
                              Toast toast = new Toast(requireContext());
                              toast.setDuration(Toast.LENGTH_LONG);
                              toast.setView(layout);
                              toast.show();
                          } else {
                                  // Error al actualizar la contraseña
                                  // Puedes obtener más detalles sobre el error utilizando task.getException()
                                  Exception e = task.getException();
                                  if (e != null) {
                                      Log.e("Firebase", "Error updating email: " + e.getMessage());
                                  }
                                  Toast.makeText(requireContext(), "Error updating email", Toast.LENGTH_SHORT).show();
                              }

                      }
                  });



            replaceFragment(new ProfileFragment());

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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (!hasFocus && id == R.id.nameSurnameEditText){
            String newNameString = nameEditText.getText().toString();
            if (newNameString.isEmpty()) {
                nameEditText.setText(nameSurnameString);
            }
            nameEditText.setFocusable(false);
            nameEditText.setFocusableInTouchMode(false);
        
        }
    }
}