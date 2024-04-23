package com.example.scriptumapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class BookExchange extends AppCompatActivity implements View.OnClickListener {
    //Variables
    Button backBookExchange, editBookExchange, addBookExchange;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_exchange);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Inicializamos variables
        backBookExchange = findViewById(R.id.backBookExchange);
        editBookExchange = findViewById(R.id.editBookExchange);
        addBookExchange = findViewById(R.id.addBookExchange);

        //Ponemos a la escucha
        backBookExchange.setOnClickListener(this);
        editBookExchange.setOnClickListener(this);
        addBookExchange.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

    }
}