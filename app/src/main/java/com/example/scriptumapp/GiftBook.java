package com.example.scriptumapp;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GiftBook extends AppCompatActivity implements View.OnClickListener {

    //Variables
    Button addGiftBook, editGiftBook, backGiftBook;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gift_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Inicializamos variables
        addGiftBook = findViewById(R.id.addGiftBook);
        editGiftBook = findViewById(R.id.editGiftBook);
        backGiftBook = findViewById(R.id.backGiftBook);

        //Ponemos varibles a la escucha

        addGiftBook.setOnClickListener(this);
        editGiftBook.setOnClickListener(this);
        backGiftBook.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }
}