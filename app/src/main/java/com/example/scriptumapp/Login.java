package com.example.scriptumapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity implements View.OnClickListener{

    Button loginButton;
    TextView signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginButton = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signUp);
        loginButton.setOnClickListener(this);
        signUp.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.loginButton) {
            //login en Firebase

            // Hacer un intent para pasar a la MainActivity
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.signUp) {
            //signup en firebase

            // Hacer un intent para pasar a la MainActivity
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
    }
}