package com.example.scriptumapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Queries extends AppCompatActivity implements View.OnClickListener
{

    Button book_loan, book_exchange, gift_book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_queries);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Inicializamos variables
        book_loan = findViewById(R.id.book_loan);
        book_exchange = findViewById(R.id.book_exchange);
        gift_book = findViewById(R.id.gift_book);

        //Ponemos a la escucha
        book_loan.setOnClickListener(this);
        book_exchange.setOnClickListener(this);
        gift_book.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {



    }
}