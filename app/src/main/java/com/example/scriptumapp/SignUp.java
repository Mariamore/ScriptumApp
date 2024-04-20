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

public class SignUp extends AppCompatActivity implements View.OnClickListener{

    Button createAccButton;
    TextView exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createAccButton = findViewById(R.id.createAccountButton);
        exit = findViewById(R.id.exitText);
        createAccButton.setOnClickListener(this);
        exit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.createAccountButton) {
            //crear cuenta en firebase

            // Hacer un intent para pasar a la MainActivity
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.exitText) {
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
        }
    }
}