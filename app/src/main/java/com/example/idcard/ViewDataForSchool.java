package com.example.idcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewDataForSchool extends AppCompatActivity {

    String [] status = {"Pending","Ready To Print"};
    String [] role = {"Student", "Staff"};
    String strStatus; // To send the status to next activity
    String strRole; // To send role to next activity
    Intent intentPending, intentReadyToPrint;
    AutoCompleteTextView autoCompleteSchool, autoCompleteStatus, autoCompleteRole;
    ArrayAdapter<String> adapterSchool, adapterStatus, adapterRole;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data_for_school);

        // Initializing the intent
        intentPending = new Intent(ViewDataForSchool.this, PendingStudent.class);
        intentReadyToPrint = new Intent(ViewDataForSchool.this, ReadyToPrintStudent.class);

        // Setting the school name
        TextView userName = findViewById(R.id.userName);
        userName.setText(getUserName());

        ImageView appLogo = findViewById(R.id.app_img);
        appLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), About.class);
                startActivity(intent);
            }
        });

        submit = findViewById(R.id.submit_btn);


        // Status Dropdown
        autoCompleteStatus = findViewById(R.id.status_dropdown);
        adapterStatus = new ArrayAdapter<String>(this,R.layout.list_item,status);
        autoCompleteStatus.setAdapter(adapterStatus);
        autoCompleteStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Switch case to set strStatus based on selected item
                switch (position) {
                    case 0:
                        strStatus = "Panding";
                        break;
                    case 1:
                        strStatus = "Ready to print";
                        break;
                }
            }
        });

        // Role Dropdown
        autoCompleteRole = findViewById(R.id.role_dropdown);
        adapterRole = new ArrayAdapter<String>(this,R.layout.list_item,role);
        autoCompleteRole.setAdapter(adapterRole);
        autoCompleteRole.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Switch case to set strStatus based on selected item
                switch (position) {
                    case 0:
                        strRole = "Student";
                        break;
                    case 1:
                        strRole = "Staff";
                        break;
                }
            }
        });

        // Switch to next Activity according to the status
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strStatus.equals("Panding")){
                    intentPending.putExtra("Status",strStatus);
                    intentPending.putExtra("Role",strRole);
                    startActivity(intentPending);
                }
                else if(strStatus.equals("Ready to print")){
                    intentReadyToPrint.putExtra("Status",strStatus);
                    intentReadyToPrint.putExtra("Role",strRole);
                    startActivity(intentReadyToPrint);
                }
            }
        });
    }
    // main function ends

    // -----------------------------------------------------------------------------------------------------------------------

    // Method to get the token and name saved in local storage
    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    private String getSchoolId() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("_schoolId", "");
    }

    private String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }
    // End of method to get the token and name saved in local storage

    // -----------------------------------------------------------------------------------------------------------------------
}