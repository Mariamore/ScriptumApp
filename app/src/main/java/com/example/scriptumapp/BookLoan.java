package com.example.scriptumapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BookLoan extends AppCompatActivity implements View.OnClickListener {

    //Variables
    Button backBookLoan, addBookLoan, editBookLoan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_loan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Inicializamos Variables
        backBookLoan = findViewById(R.id.backBookLoan);
        addBookLoan = findViewById(R.id.addBookLoan);
        editBookLoan = findViewById(R.id.editBookLoan);

        //Ponemos a la escucha
        backBookLoan.setOnClickListener(this);
        addBookLoan.setOnClickListener(this);
        editBookLoan.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {



    }
}