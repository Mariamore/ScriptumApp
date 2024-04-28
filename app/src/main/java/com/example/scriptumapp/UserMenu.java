package com.example.scriptumapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserMenu extends AppCompatActivity implements View.OnClickListener{

    ImageView personalDataButton, uploadBookButton, checkBookButton, locationButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        personalDataButton = findViewById(R.id.userButton);
        uploadBookButton = findViewById(R.id.uploadButton);
        checkBookButton = findViewById(R.id.consultButton);
        locationButton = findViewById(R.id.ubiButton);
        personalDataButton.setOnClickListener(this);
        uploadBookButton.setOnClickListener(this);
        checkBookButton.setOnClickListener(this);
        locationButton.setOnClickListener(this);

    }

    @Override

    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.userButton){
            //intent para pasar a la activity de Personal Data
            Intent i = new Intent(UserMenu.this, PersonalData.class);
            startActivity(i);
        } else if (id == R.id.uploadButton){
            //intent a activity de subir libro
            Intent i = new Intent(UserMenu.this, UploadBook.class);
            startActivity(i);
        } else if (id == R.id.consultButton){
            //intent a activity de consultar libros

        } else if (id == R.id.ubiButton){
            //intent a activity de ubicación
            Intent i = new Intent(UserMenu.this, Location.class);
            startActivity(i);
        }
    }
}