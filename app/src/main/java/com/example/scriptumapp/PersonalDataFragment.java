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
        //definimos las variables
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
        //establecemos llos listeners
        editPasswordButton.setOnClickListener(this);
        editEmailButton.setOnClickListener(this);
        editNameButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        idUser = user.getUid();
        db = FirebaseFirestore.getInstance();

        //extraemos los datos del usuario
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
                            //como no vamos a poner la contraseña en hardcode, ponemos un string cualquiera, que además sea menor
                            //de 6 caracteres de longitud para que no pueda coincidir con la contraseña real
                            passwordString = "pass";

                            //rellenamos los edittext con los datos del usuario
                            nameEditText.setText(nameSurnameString);
                            emailEditText.setText(emailString);
                            passwordEditText.setText(passwordString);

                        }
                    }
                });

        /*
         * Establece un OnTouchListener en la vista raíz para manejar eventos táctiles.
         * Cuando el usuario toca la pantalla, este listener verifica si alguno de los
         * campos EditText (nameEditText, emailEditText o passwordEditText) tiene el foco y el
         * usuario toca/hace click en otro lugar de la pantalla, le quita el foco.
         */
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (nameEditText.hasFocus()) {
                        nameEditText.clearFocus();

                    } else if (emailEditText.hasFocus()) {
                        emailEditText.clearFocus();

                    } else if (passwordEditText.hasFocus()) {
                        newPasswordString = passwordEditText.getText().toString();
                        if (newPasswordString.isEmpty()) {
                            passwordEditText.setText(passwordString);
                        } else if(newPasswordString.length()<6){
                            //si la contraseña tiene menos de 6 caracteres, devuelve el foco al
                            passwordEditText.setError("6 characters minimum");
                            negativeToast("Password should be at least 6 characters");
                            passwordEditText.requestFocus();
                            return false;
                        }

                        passwordEditText.clearFocus();

                    }
                }
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.editPasswordButton){
            //Si hacemos click en el boton de editar de la password, hace foco y permite escribir
            passwordEditText.setFocusable(true);
            passwordEditText.setFocusableInTouchMode(true);
            passwordEditText.setText("");
            passwordEditText.requestFocus();
            passwordEditText.setOnFocusChangeListener(this);

        } else if (id == R.id.editNameButton){
            //Si hacemos click en el boton de editar de el nombre, hace foco y permite escribir
            nameEditText.setFocusable(true);
            nameEditText.setFocusableInTouchMode(true);
            nameEditText.setText("");
            nameEditText.requestFocus();
            nameEditText.setOnFocusChangeListener(this);

        } else if (id == R.id.editEmailButton){
            //Si hacemos click en el boton de editar de el email, hace foco y permite escribir
            emailEditText.setFocusable(true);
            emailEditText.setFocusableInTouchMode(true);
            emailEditText.setText("");
            emailEditText.requestFocus();
            emailEditText.setOnFocusChangeListener(this);


        } else if (id == R.id.saveButton){
            //recogemos el contenido de los edit text
            newnameString = nameEditText.getText().toString();
            newPasswordString = passwordEditText.getText().toString();
            newEmailString = emailEditText.getText().toString();

            //comprobamos que la contraseña no sea menor de 6 caracteres
            if (passwordEditText.length()<6 && !newPasswordString.equals("pass")){
                editPasswordButton.performClick();
            } else {
                //si la contraseña es distinta al string de muestra que fijamos al principio, la cambiamos
                if(!newPasswordString.equals("pass")){
                    updatePassword(newPasswordString);
                }
                //si el string del edittext del nombre y/o el email es diferente al inicial, lo actualizamos
                if (!nameSurnameString.equals(newnameString)) {
                    updateNameSurname(newnameString);
                }
                if (!emailString.equals(newEmailString)){
                    updateEmail(newEmailString);
                }
                replaceFragment(new ProfileFragment());
            }


        } else if (id == R.id.backButton){
            //si damos al botón de atrás, vuelve al fragment anterior
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
     * Método que se ejecuta cuando se cambia el foco de un EditText.
     * Realiza acciones específicas dependiendo del campo EditText que pierde el foco.
     *
     * @param v La vista que ha cambiado el foco.
     * @param hasFocus Indica si la vista tiene el foco o no.
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId(); // Obtiene el ID de la vista que ha cambiado el foco
        EditText editText = (EditText) v; // Convierte la vista en un EditText para acceder a sus métodos y propiedades

        // Si la vista ha perdido el foco
        if (!v.hasFocus()) {
            // Verifica el ID de la vista que ha cambiado el foco y realiza acciones correspondientes

            // Si el EditText del nombre y apellido ha perdido el foco
            if (id == R.id.nameSurnameEditText) {
                String newNameString = nameEditText.getText().toString(); // Obtiene el texto del EditText del nombre y apellido
                // Si el texto del nombre y apellido está vacío, restaura el texto original
                if (newNameString.isEmpty()) {
                    nameEditText.setText(nameSurnameString);
                }

                // Si el EditText de la contraseña ha perdido el foco
            } else if (id == R.id.passwordEditText) {
                String newPasswordString = passwordEditText.getText().toString(); // Obtiene el texto del EditText de la contraseña
                // Si el texto de la contraseña está vacío, restaura el texto original
                if (newPasswordString.isEmpty()) {
                    passwordEditText.setText(passwordString);
                }

                // Si el EditText del correo electrónico ha perdido el foco
            } else if (id == R.id.emailEditText) {
                String newEmailString = emailEditText.getText().toString(); // Obtiene el texto del EditText del correo electrónico
                // Si el texto del correo electrónico está vacío, restaura el texto original
                if (newEmailString.isEmpty()) {
                    emailEditText.setText(emailString);
                }
            }

            // Deshabilita la capacidad de obtener foco y de tocar para el edittext que ha perdido el foco
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
        }
    }

    /**
     * Actualiza el nombre y apellido del usuario en la base de datos Firestore.
     *
     * @param newNameString El nuevo nombre y apellido del usuario.
     */
    private void updateNameSurname(String newNameString) {
        // Crea un mapa de actualizaciones con el nuevo nombre
        Map<String, Object> updates = new HashMap<>();
        updates.put("nameSurname", newNameString);

        // Realiza una consulta en la colección "usersData" para encontrar el documento del usuario actual
        db.collection("usersData")
                .whereEqualTo("user", idUser)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Verifica si la consulta retorna algún documento
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Obtiene el primer documento retornado por la consulta
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Obtiene el ID del documento
                            String id = documentSnapshot.getId();

                            // Actualiza los datos del documento
                            db.collection("usersData").document(id)
                                    .update(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Muestra un mensaje de éxito si la actualización fue exitosa
                                            positiveToast("Nombre actualizado");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Muestra un mensaje de error si la actualización falla
                                            negativeToast("Error actualizando el nombre");
                                        }
                                    }
                            );
                        }
                    }
                });
    }


    /**
     * Actualiza la contraseña del usuario en Firebase Authentication.
     *
     * @param newPasswordString La nueva contraseña del usuario.
     */
    private void updatePassword(String newPasswordString) {
        // Obtiene la instancia del usuario actualmente autenticado
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Actualiza la contraseña del usuario utilizando la nueva contraseña proporcionada
        user.updatePassword(newPasswordString)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Verifica si la actualización de la contraseña se completó con éxito
                        if (task.isSuccessful()) {
                            // Muestra un mensaje de éxito si la contraseña se actualizó correctamente
                            positiveToast("¡Contraseña actualizada!");
                        } else {
                            // Si hay un error al actualizar la contraseña
                            Exception e = task.getException();
                            if (e != null) {
                                // Registra el error en los registros si está disponible
                                Log.e("Firebase", "Error al actualizar la contraseña: " + e.getMessage());
                            }
                            // Muestra un mensaje de error si la actualización de la contraseña falla
                            negativeToast("Error al actualizar la contraseña. Intente iniciar sesión nuevamente.");
                        }
                    }
                }
        );
    }


    /**
     * Actualiza el correo electrónico del usuario en Firebase Authentication.
     *
     * @param newEmailString El nuevo correo electrónico del usuario.
     */
    private void updateEmail(String newEmailString) {
        // Verifica y solicita la actualización del correo electrónico del usuario
        user.verifyBeforeUpdateEmail(newEmailString)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Verifica si la solicitud de actualización se completa con éxito
                        if (task.isSuccessful()) {
                            // Muestra un mensaje indicando que el correo electrónico se ha actualizado y que se debe verificar
                            positiveToast("Correo electrónico actualizado. Verifica tu nuevo correo electrónico.");

                            // Obtiene la instancia de Firebase Authentication
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            // Obtiene el usuario actualmente autenticado
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            // Verifica si hay un usuario autenticado actualmente
                            if (currentUser != null) {
                                // Cierra la sesión del usuario actual
                                mAuth.signOut();
                                // Muestra un mensaje indicando que el usuario ha cerrado sesión y debe iniciar sesión con las nuevas credenciales
                                positiveToast("Usuario desconectado. Inicia sesión con tus nuevas credenciales.");
                                // Reemplaza el fragmento actual con el fragmento de inicio de sesión
                                replaceFragment(new LoginFragment());
                            } else {
                                // Muestra un mensaje indicando que no hay ningún usuario autenticado
                                negativeToast("No hay ningún usuario conectado.");
                            }

                        } else {
                            // Si hay un error al actualizar el correo electrónico
                            Exception e = task.getException();
                            if (e != null) {
                                // Registra el error en los registros si está disponible
                                Log.e("Firebase", "Error al actualizar el correo electrónico: " + e.getMessage());
                            }
                            // Muestra un mensaje de error si la actualización del correo electrónico falla
                            negativeToast("Error al actualizar el correo electrónico.");
                        }

                    }
                });
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
        // Obtiene el servicio de inflater para inflar el diseño del toast
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