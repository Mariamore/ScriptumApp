package com.example.scriptumapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity implements Animation.AnimationListener {

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
        }, 3000); // 2000 milisegundos = 2 segundos de retraso
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}