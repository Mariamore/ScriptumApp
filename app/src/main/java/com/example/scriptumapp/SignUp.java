package com.example.scriptumapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity implements View.OnClickListener{
    //Variables
    Button createAccButton;
    TextView exit;

    EditText nameSurnameInputEditText, addressInputEditText, emailInputeditText, passwordInputEditText;

    //Agregamos Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        //Instanciamos Firebase
        mAuth = FirebaseAuth.getInstance();




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createAccButton = findViewById(R.id.createAccountButton);
        exit = findViewById(R.id.exitText);

        createAccButton.setOnClickListener(this); //crear cuenta
        exit.setOnClickListener(this); //salir

        //Referencias de las cajas
        nameSurnameInputEditText = findViewById(R.id.nameSurnameInputEditText);
        addressInputEditText = findViewById(R.id.nameSurnameInputEditText);
        emailInputeditText = findViewById(R.id.nameSurnameInputEditText);
        passwordInputEditText = findViewById(R.id.nameSurnameInputEditText);



    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.createAccountButton) {
            //crear cuenta en firebase





            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout,
                    (ViewGroup) findViewById(R.id.toastLayout));
            TextView txtMsg = (TextView)layout.findViewById(R.id.toastMessage);
            txtMsg.setText(R.string.account_created);
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            // Hacer un intent para pasar a la MainActivity
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.exitText) {
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
        }
    }
}