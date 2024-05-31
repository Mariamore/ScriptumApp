package com.example.scriptumapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        EdgeToEdge.enable(this);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Crea un Intent para iniciar MainActivity
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);

                // Cierra la actividad actual para evitar que el usuario pueda volver a ella
                finish();
            }
        }, 3000);//segundos durante los que se mantiene el splash
    }


}