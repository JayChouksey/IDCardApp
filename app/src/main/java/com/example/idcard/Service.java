package com.example.idcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Service extends AppCompatActivity {

    private EditText editTextHelp;
    private EditText editTextName;
    private EditText editTextContact;
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        TextView userName = findViewById(R.id.userName);
        userName.setText(getUserName());

        ImageView topIcon = findViewById(R.id.user_school_icon);
        if(getRole().equals("school")){
            topIcon.setImageResource(R.drawable.school_home_icon);
        }

        editTextHelp = findViewById(R.id.editTextHelp);
        editTextName = findViewById(R.id.editTextName);
        editTextContact = findViewById(R.id.editTextContact);
        buttonSend = findViewById(R.id.buttonSend);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String helpMessage = editTextHelp.getText().toString().trim();
                String name = editTextName.getText().toString().trim();
                String contact = editTextContact.getText().toString().trim();

                String message = "Name: " + name + "\n\nContact: " + contact + "\n\nNeeded help: " + helpMessage;

                if (!helpMessage.isEmpty()) {
                    // Send message to WhatsApp
                    Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
                    whatsappIntent.setData(Uri.parse("https://api.whatsapp.com/send?phone=+918770994162&text=" + message));
                    startActivity(whatsappIntent);
                }
            }
        });
    }

    // Method to get the token saved in local storage
    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    private String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }

    private String getRole() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("role", "");
    }
    // End of method to get the token saved in local storage

    // --------------------------------------------------------------------------------------------------------------------------
}