package com.example.scriptumapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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



public class SignUp extends AppCompatActivity implements View.OnClickListener{

    //variables
    Button createAccButton;
    TextView exit;

    EditText emailInputEdittext, passwordInputEditText;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailInputEdittext = findViewById(R.id.emailInputEditText);
        passwordInputEditText = findViewById(R.id.passwordInputEdittext);


        createAccButton = findViewById(R.id.createAccountButton);
        exit = findViewById(R.id.exitText);
        createAccButton.setOnClickListener(this);
        exit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.createAccountButton) {

            String email = emailInputEdittext.getText().toString();
            String password = passwordInputEditText.getText().toString();

            if(email.isEmpty()){
                emailInputEdittext.setError("Requested field");
                emailInputEdittext.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailInputEdittext.setError("Enter a valid email");
                emailInputEdittext.requestFocus();
            } else if (password.isEmpty()){
                passwordInputEditText.setError("Requested field");
                passwordInputEditText.requestFocus();
            } else if(password.length() < 6){
                passwordInputEditText.setError("6 characters minimum");
                passwordInputEditText.requestFocus();
            } else{
                //crear cuenta en firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.toast_layout_ok,
                                             findViewById(R.id.toastLayoutOk));
                                    TextView txtMsg = layout.findViewById(R.id.toastMessage);
                                    txtMsg.setText(R.string.account_created);
                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();
                                    // Hacer un intent para pasar a la MainActivity
                                    Intent intent = new Intent(SignUp.this, UserMenu.class);
                                    startActivity(intent);

                                } else {

                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.toast_layout_fail,
                                             findViewById(R.id.toastLayoutFail));
                                    TextView txtMsg = layout.findViewById(R.id.toastMessage);
                                    txtMsg.setText(R.string.sign_up_failed);
                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();

                                }
                            }
                        });

                }



        } else if (id == R.id.exitText) {
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
        }
    }
}