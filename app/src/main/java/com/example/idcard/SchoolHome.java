package com.example.idcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SchoolHome extends AppCompatActivity {

    CardView cardViewAddData, cardViewViewData, cardViewContact, cardViewHelp;
    Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_home);

        // Setting the school name
        TextView userName = findViewById(R.id.userName);
        userName.setText(getUserName());

        cardViewAddData = findViewById(R.id.cardViewAddData);
        cardViewViewData = findViewById(R.id.cardViewViewData);
        cardViewContact = findViewById(R.id.cardViewContact);
        cardViewHelp = findViewById(R.id.cardViewHelpAndSupport);
        logOut = findViewById(R.id.buttonLogout);

        cardViewAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchoolHome.this, AddDataForSchool.class);
                startActivity(intent);
            }
        });

        cardViewViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchoolHome.this, ViewDataForSchool.class);
                startActivity(intent);
            }
        });

        cardViewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchoolHome.this, Contact.class);
                intent.putExtra("UserType","School");
                startActivity(intent);
            }
        });

        cardViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchoolHome.this, Service.class);
                intent.putExtra("UserType","School");
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SchoolHome.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                clearAuthToken();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(SchoolHome.this, Login.class);
                startActivity(intent);
            }
        });

    }
    // Main function ends
    private void clearAuthToken() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("authToken");
        editor.apply();
    }

    // -----------------------------------------------------------------------------------------------------------------------

    // Method to get the token and name saved in local storage
    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    private String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }
    // End of method to get the token and name saved in local storage

    // -----------------------------------------------------------------------------------------------------------------------

}