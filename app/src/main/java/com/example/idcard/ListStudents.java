package com.example.idcard;

import android.content.Context;
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
    String strStatus = "Panding"; // not a typo

    AutoCompleteTextView autoCompleteSchool, autoCompleteStatus;
    ArrayAdapter<String> adapterSchool, adapterStatus;

    Button submit;
    RecyclerView recyclerView;
    DynamicStudentAdapter adapter;

    TextView test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_students);

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
                String item = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(),"Item: "+item,Toast.LENGTH_SHORT).show();
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


    }
    // Main function ends here



    private void fetchStudentData(String id) {
        // Get the authorization token
        String token = getToken();

        String url = "https://id-card-backend-2.onrender.com/user/students/" + id + "?status=" + strStatus;

        //List<SchoolInfo> schoolInfoList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ListStudents.this, "Data Fetched Successfully", Toast.LENGTH_SHORT).show();
                        List<DynamicStudent> studentList = new ArrayList<>();
                        try {
                            JSONArray studentsArray = response.getJSONArray("students");
                            for (int i = 0; i < studentsArray.length(); i++) {
                                JSONObject studentObject = studentsArray.getJSONObject(i);
                                DynamicStudent student = new DynamicStudent();

                                // Iterate over the keys of the JSON object
                                Iterator<String> keys = studentObject.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    // Check if the key is "avatar"
                                    if (key.equals("avatar")) {
                                        // Break the loop if "avatar" is encountered
                                        break;
                                    }
                                    // Skip if the key is "_id"
                                    if (key.equals("_id")) {
                                        continue;
                                    }
                                    String value = studentObject.getString(key);
                                    student.addField(key, value);
                                }
                                studentList.add(student);
                            }
                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    updateRecyclerView(studentList);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListStudents.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                fetchStudentData(idArray[position] );
            }
        });
    }

    // End of Function to fetch schools from the api endpoint


    private void updateRecyclerView(List<DynamicStudent> studentList) {
        recyclerView = findViewById(R.id.student_list_recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ListStudents.this));
        adapter = new DynamicStudentAdapter(studentList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged(); // Notify adapter of dataset changes
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
    // End of method to get the token saved in local storage

    // --------------------------------------------------------------------------------------------------------------------------
}