
package com.example.scriptumapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity implements View.OnClickListener{

    Button loginButton;
    TextView signUp, forgotPassword;

   EditText emailInputEdittext, passwordInputEditText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        loginButton = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signUp);
        forgotPassword = findViewById(R.id.forgotPassword);
        emailInputEdittext = findViewById(R.id.editTextEmailEditText);
        passwordInputEditText = findViewById(R.id.editTextPasswordEditText);


        loginButton.setOnClickListener(this);
        signUp.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);

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
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.toast_layout_ok,
                                            findViewById(R.id.toastLayoutOk));
                                    TextView txtMsg = layout.findViewById(R.id.toastMessage);
                                    txtMsg.setText(R.string.user_logged);
                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();

                                    Intent intent = new Intent(Login.this, UserMenu.class);
                                    startActivity(intent);
                                } else {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.toast_layout_fail,
                                             findViewById(R.id.toastLayoutFail));
                                    TextView txtMsg = layout.findViewById(R.id.toastMessage);
                                    txtMsg.setText(R.string.incorrect_name_pass);
                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();
                                }
                            }
                        });


            }
            // Hacer un intent para pasar a la MainActivity

        } else if (id == R.id.signUp) {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        } else if (id == R.id.forgotPassword){
            //Hacer en firebase lo del olvido de contraseña
        }
    }
}

