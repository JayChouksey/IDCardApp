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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.idcard.recyclerfiles.DynamicStudent;
import com.example.idcard.recyclerfiles.DynamicStudentAdapter;
import com.example.idcard.recyclerfiles.SchoolInfo;
import com.example.idcard.recyclerfiles.SchoolInfoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListStudents extends AppCompatActivity {

    // Drop Down
    List<String> schoolNames = new ArrayList<>(); // List to store school names
    List<String> schoolId = new ArrayList<>(); // List to store schoolId
    String [] status = {"Pending","Ready To Print","Printed"};
    String [] role = {"Student", "Staff"};
    String strStatus; // To send the status to next activity
    String strRole; // To send role to next activity
    Intent intentPending, intentReadyToPrint, intentPrinted;
    AutoCompleteTextView autoCompleteSchool, autoCompleteStatus, autoCompleteRole;
    ArrayAdapter<String> adapterSchool, adapterStatus, adapterRole;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_students);

        // Initializing the intent
        intentPending = new Intent(ListStudents.this, PendingStudent.class);
        intentReadyToPrint = new Intent(ListStudents.this, ReadyToPrintStudent.class);
        intentPrinted = new Intent(ListStudents.this, PrintedStudent.class);

        // Setting user text name to user
        TextView userName = findViewById(R.id.userName);
        userName.setText(getUserName());

        submit = findViewById(R.id.submit_btn);

        // School Dropdown
        autoCompleteSchool = findViewById(R.id.school_dropdown);
        fetchUserData();

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
                    case 2:
                        strStatus = "Printed";
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
                else{
                    intentPrinted.putExtra("Status",strStatus);
                    intentPrinted.putExtra("Role",strRole);
                    startActivity(intentPrinted);
                }
            }
        });
    }
    // Main function ends here


    // Function to fetch schools for drop down from the api endpoint
    private void fetchUserData() {
        String token = getToken();

        String url = "https://id-card-backend-2.onrender.com/user/schools";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ListStudents.this, "Data Fetched Successfully", Toast.LENGTH_SHORT).show();

                        try {
                            JSONArray schoolsArray = response.getJSONArray("schools");

                            // Loop through the schools array to extract names
                            for (int i = 0; i < schoolsArray.length(); i++) {
                                JSONObject schoolObject = schoolsArray.getJSONObject(i);
                                String schoolName = schoolObject.getString("name");
                                String id = schoolObject.getString("_id");

                                schoolNames.add(schoolName);
                                schoolId.add(id);

                            }
                            updateSchoolDropdown();
                            // Now you have the list of school names
                            // Do whatever you want with this list

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListStudents.this, "Error in data fetching", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization", token);
                return header;
            }
        };

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }

    private void updateSchoolDropdown() {
        // Convert the List to an array
        String[] schoolArray = schoolNames.toArray(new String[0]);
        String[] idArray = schoolId.toArray(new String[0]);

        // Initialize the adapter with the new data
        adapterSchool = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, schoolArray);
        autoCompleteSchool.setAdapter(adapterSchool);

        // Set item click listener
        autoCompleteSchool.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(),"Item: "+idArray[position],Toast.LENGTH_SHORT).show();
                //fetchStudentData(idArray[position]);
                saveSchoolId(idArray[position]); // storing the id of the school locally
            }
        });
    }

    // End of Function to fetch schools from the api endpoint



    // Method to get the token saved in local storage

    private void saveSchoolId(String id) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("schoolId", id);
        editor.apply();
    }
    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    private String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }
    // End of method to get the token saved in local storage

    // --------------------------------------------------------------------------------------------------------------------------
}