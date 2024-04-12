package com.example.idcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SchoolHome extends AppCompatActivity {

    CardView cardViewAddData, cardViewViewData, cardViewContact, cardViewHelp;
    Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_home);

        cardViewAddData = findViewById(R.id.cardViewAddData);
        cardViewViewData = findViewById(R.id.cardViewViewData);
        cardViewContact = findViewById(R.id.cardViewContact);
        cardViewHelp = findViewById(R.id.cardViewHelpAndSupport);
        logOut = findViewById(R.id.buttonLogout);

        cardViewAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchoolHome.this, AddStudent.class);
                intent.putExtra("UserType","School");
                startActivity(intent);
            }
        });

        cardViewViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchoolHome.this, ListStudents.class);
                intent.putExtra("UserType","School");
                startActivity(intent);
            }
        });

        cardViewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchoolHome.this, Contact.class);
                startActivity(intent);
            }
        });

        cardViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchoolHome.this, Service.class);
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchoolHome.this, Login.class);
                startActivity(intent);
            }
        });

    }
}