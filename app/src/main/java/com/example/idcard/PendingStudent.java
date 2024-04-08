package com.example.idcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PendingStudent extends AppCompatActivity {

    RecyclerView recyclerView;
    DynamicStudentAdapter adapter;
    Intent intent;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_student);

        intent = getIntent();
        text = findViewById(R.id.text);

        // Setting user text name to user
        TextView userName = findViewById(R.id.userName);
        userName.setText(getUserName());

        // Fetching student data
        fetchStudentData();

        Button delete = findViewById(R.id.deleteButton);
        Button statusReadyToPrint = findViewById(R.id.moveReadyToPrintButton);
        Button statusPrinted = findViewById(R.id.movePrintedButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getSelectedStudentIds();
                deleteStudents(id);
            }
        });
        statusReadyToPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getSelectedStudentIds();
                changeStatusReadyToPrint(id);
            }
        });
        statusPrinted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getSelectedStudentIds();
                changeStatusPrinted(id);
            }
        });



        //clearSchoolId(); // clearing school id, stored locally

    }
    // Main method ends

    // Fetching student data
    private void fetchStudentData() {
        // Get the authorization token
        String token = getToken();
        String id = getId(); // From local storage
        String status = intent.getStringExtra("Status");

        String url = "https://id-card-backend-2.onrender.com/user/students/" + id + "?status=" + status;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(PendingStudent.this, "Data Fetched Successfully", Toast.LENGTH_SHORT).show();
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
                                   /* if (key.equals("_id")) {
                                        continue;
                                    }*/
                                    String value = studentObject.getString(key);
                                    student.addField(key, value);
                                }
                                studentList.add(student);
                            }

                            // Updating recycler view for data fetching
                            updateRecyclerView(studentList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data);
                    Toast.makeText(PendingStudent.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PendingStudent.this, "Error adding student", Toast.LENGTH_SHORT).show();
                }
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

    // End of Fetching student data

    // Method to update school list recycler view
    private void updateRecyclerView(List<DynamicStudent> studentList) {
        recyclerView = findViewById(R.id.student_list_recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PendingStudent.this));
        adapter = new DynamicStudentAdapter(studentList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged(); // Notify adapter of dataset changes
    }

    // Method to get the id of the student from Student Adapter class to change the status of the student
    private String getSelectedStudentIds() {
      return adapter.getSelectedStudentIds();
    }

    // End of method to update school list recycler view

    // Method to delete and change student status
    public void changeStatusReadyToPrint(String studentIds) {
        // Create JSON object with the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("studentIds", studentIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create request URL
        String schoolId = getId();
        String url = "https://id-card-backend-2.onrender.com/user/student/change-status/readyto/" + schoolId;

        // Create request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        Toast.makeText(PendingStudent.this, "Status of selected students updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        if (error.networkResponse != null) {
                            // If there's an error response from the server, handle it here
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(PendingStudent.this, "Error:" + errorMessage, Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Set authorization header
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                return headers;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(PendingStudent.this);
        requestQueue.add(jsonObjectRequest);
    }

    public void changeStatusPrinted(String studentIds) {
        // Create JSON object with the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("studentIds", studentIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create request URL
        String schoolId = getId();
        String url = "https://id-card-backend-2.onrender.com/user/student/change-status/printed/" + schoolId;

        // Create request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        Toast.makeText(PendingStudent.this, "Status of selected students updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        if (error.networkResponse != null) {
                            // If there's an error response from the server, handle it here
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(PendingStudent.this, "Error:" + errorMessage, Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Set authorization header
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                return headers;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(PendingStudent.this);
        requestQueue.add(jsonObjectRequest);
    }

    public void deleteStudents(String studentIds) {
        // Create JSON object with the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("studentIds", studentIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create request URL
        String schoolId = getId();
        String url = "https://id-card-backend-2.onrender.com/user/students/delete/" + schoolId;

        // Create request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        Toast.makeText(PendingStudent.this, "Status of selected students updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        if (error.networkResponse != null) {
                            // If there's an error response from the server, handle it here
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(PendingStudent.this, "Error:" + errorMessage, Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Set authorization header
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                return headers;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(PendingStudent.this);
        requestQueue.add(jsonObjectRequest);
    }

    // End of Method to delete and change student status



    // Method to get the data saved in local storage
    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    private String getId() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("schoolId", "");
    }

    private String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }

    private void clearSchoolId() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("schoolId");
        editor.apply();
    }
    // End of method to get data saved in local storage

}