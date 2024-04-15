package com.example.idcard;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.idcard.recyclerfiles.DynamicStudent;
import com.example.idcard.recyclerfiles.DynamicStudentAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ReadyToPrintStudent extends AppCompatActivity {

    RecyclerView recyclerView;
    DynamicStudentAdapter adapter;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_to_print_student);

        intent = getIntent();

        String role = intent.getStringExtra("Role");
        if(role.equals("Student")){
            fetchStudentData();
        }
        else if(role.equals("Staff")){
            fetchStaffData();
        }

        // Setting user text name to user
        TextView userName = findViewById(R.id.userName);
        userName.setText(getUserName());

        // Select all feature
        CheckBox selectAllCheckbox = findViewById(R.id.select_all);
        selectAllCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.selectAllStudents(isChecked); // Call selectAllStudents method of the adapter
            }
        });


        Button delete = findViewById(R.id.deleteButton);
        Button statusPrinted = findViewById(R.id.movePrintedButton);
        Button statusPending = findViewById(R.id.movePendingButton);
        Button exportExcel = findViewById(R.id.exportExcelButton);
        Button downloadImages = findViewById(R.id.downloadImagesButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getSelectedStudentIds();
                if(role.equals("Student")){
                    deleteStudents(id);
                }
                else{
                    deleteStaffs(id);
                }
                finish();
            }
        });
        statusPrinted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getSelectedStudentIds();
                if(role.equals("Student")) {
                    changeStatusToPrinted(id);
                }
                else{
                    changeStatusPrintedStaff(id);
                }
                finish();
            }
        });
        statusPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getSelectedStudentIds();
                if(role.equals("Student")) {
                    changeStatusToPending(id);
                }
                else{
                    changeStatusToPendingStaff(id);
                }
                finish();
            }
        });
        exportExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadExcelFile(ReadyToPrintStudent.this);
            }
        });
        downloadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReadyToPrintStudent.this, "Error from server side", Toast.LENGTH_SHORT).show();
                //downloadImages(ReadyToPrintStudent.this);
            }
        });


        //clearSchoolId(); // clearing school id, stored locally
    }

    // Main method ends

    // Fetching student and staff data
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
                        Toast.makeText(ReadyToPrintStudent.this, "Data Fetched Successfully", Toast.LENGTH_SHORT).show();
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
                                    if (key.equals("avatar") || key.equals("__v") || key.equals("createdAt") ||
                                            key.equals("updatedAt") || key.equals("user") || key.equals("photoName")) {
                                        continue; // Skip this key
                                    }
                                    String value = studentObject.getString(key);
                                    student.addField(key, value);
                                }
                                // Handling the avatar key separately to extract the URL
                                JSONObject avatarObject = studentObject.optJSONObject("avatar");
                                if (avatarObject != null) {
                                    String avatarUrl = avatarObject.optString("url");
                                    student.setAvatarUrl(avatarUrl);
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
                    Toast.makeText(ReadyToPrintStudent.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReadyToPrintStudent.this, "Error adding student", Toast.LENGTH_SHORT).show();
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

    private void fetchStaffData() {
        // Get the authorization token
        String token = getToken();
        String id = getId(); // From local storage
        String status = intent.getStringExtra("Status");

        String url = "https://id-card-backend-2.onrender.com/user/staffs/" + id + "?status=" + status;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ReadyToPrintStudent.this, "Data Fetched Successfully", Toast.LENGTH_SHORT).show();
                        List<DynamicStudent> studentList = new ArrayList<>();
                        try {
                            JSONArray studentsArray = response.getJSONArray("staff");
                            for (int i = 0; i < studentsArray.length(); i++) {
                                JSONObject studentObject = studentsArray.getJSONObject(i);
                                DynamicStudent student = new DynamicStudent();

                                // Iterate over the keys of the JSON object
                                Iterator<String> keys = studentObject.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    if (key.equals("avatar") || key.equals("__v") || key.equals("createdAt") ||
                                            key.equals("updatedAt") || key.equals("user") || key.equals("photoName")) {
                                        continue; // Skip this key
                                    }
                                    String value = studentObject.getString(key);
                                    student.addField(key, value);
                                }
                                // Handling the avatar key separately to extract the URL
                                JSONObject avatarObject = studentObject.optJSONObject("avatar");
                                if (avatarObject != null) {
                                    String avatarUrl = avatarObject.optString("url");
                                    student.setAvatarUrl(avatarUrl);
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
                    Toast.makeText(ReadyToPrintStudent.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReadyToPrintStudent.this, "Error adding student", Toast.LENGTH_SHORT).show();
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

    // End of Fetching student and staff data

    // Download image and excel
    public void downloadExcelFile(Context context) {
        String schoolId = getId();
        String url = "https://id-card-backend-2.onrender.com/user/excel/data/" + schoolId + "/?status=Printed";

        // Get the directory for the user's public directory
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!directory.exists()) {
            directory.mkdirs(); // Create if it doesn't exist
        }

        // Create a download manager request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Excel File");
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedOverMetered(true); // Allow download over metered connections
        request.setAllowedOverRoaming(true); // Allow download over roaming connections
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Ready to Print Data.xlsx");

        // Add headers to the request
        request.addRequestHeader("Authorization", getToken());

        // Get the download manager and enqueue the request
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        // Optionally, you can listen for download completion to show a toast message
        // using BroadcastReceiver or DownloadManager.Query
    }

    public void downloadImages(Context context) {

        String url = "fkl";
        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle successful response (if needed)
                        Toast.makeText(context, "Images downloading started successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle errors
                Toast.makeText(context, "Failed to download images", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }
    // End of Download image and excel


    // Method to update school list recycler view
    private void updateRecyclerView(List<DynamicStudent> studentList) {
        recyclerView = findViewById(R.id.student_list_recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReadyToPrintStudent.this));
        adapter = new DynamicStudentAdapter(studentList, ReadyToPrintStudent.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged(); // Notify adapter of dataset changes
    }
    // End of Method to delete and change student status

    private String getSelectedStudentIds() {
        return adapter.getSelectedStudentIds();
    }


    // Method to delete and change student status
    public void changeStatusToPrinted(String studentIds) {
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
                        Toast.makeText(ReadyToPrintStudent.this, "Status of selected students updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        if (error.networkResponse != null) {
                            // If there's an error response from the server, handle it here
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(ReadyToPrintStudent.this, "Error:" + errorMessage, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(ReadyToPrintStudent.this);
        requestQueue.add(jsonObjectRequest);
    }
    public void changeStatusToPending(String studentIds) {
        // Create JSON object with the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("studentIds", studentIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create request URL
        String schoolId = getId();
        String url = "https://id-card-backend-2.onrender.com/user/student/change-status/pending/" + schoolId;

        // Create request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        Toast.makeText(ReadyToPrintStudent.this, "Status of selected students updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        if (error.networkResponse != null) {
                            // If there's an error response from the server, handle it here
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(ReadyToPrintStudent.this, "Error:" + errorMessage, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(ReadyToPrintStudent.this);
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
                        Toast.makeText(ReadyToPrintStudent.this, "Status of selected students updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        if (error.networkResponse != null) {
                            // If there's an error response from the server, handle it here
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(ReadyToPrintStudent.this, "Error:" + errorMessage, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(ReadyToPrintStudent.this);
        requestQueue.add(jsonObjectRequest);
    }
    public void changeStatusToPendingStaff(String staffIds) {
        // Create JSON object with the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("staffIds", staffIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create request URL
        String schoolId = getId();
        String url = "https://id-card-backend-2.onrender.com/user/staff/change-status/pending/" + schoolId;

        // Create request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        Toast.makeText(ReadyToPrintStudent.this, "Status of selected staffs updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        if (error.networkResponse != null) {
                            // If there's an error response from the server, handle it here
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(ReadyToPrintStudent.this, "Error:" + errorMessage, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(ReadyToPrintStudent.this);
        requestQueue.add(jsonObjectRequest);
    }
    public void changeStatusPrintedStaff(String staffIds) {
        // Create JSON object with the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("staffIds", staffIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create request URL
        String schoolId = getId();
        String url = "https://id-card-backend-2.onrender.com/user/staff/change-status/printed/" + schoolId;

        // Create request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        Toast.makeText(ReadyToPrintStudent.this, "Status of selected staffs updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        if (error.networkResponse != null) {
                            // If there's an error response from the server, handle it here
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(ReadyToPrintStudent.this, "Error:" + errorMessage, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(ReadyToPrintStudent.this);
        requestQueue.add(jsonObjectRequest);
    }
    public void deleteStaffs(String staffIds) {
        // Create JSON object with the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("staffIds", staffIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create request URL
        String schoolId = getId();
        String url = "https://id-card-backend-2.onrender.com/user/staffs/delete/" + schoolId;

        // Create request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        Toast.makeText(ReadyToPrintStudent.this, "Status of selected staffs updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        if (error.networkResponse != null) {
                            // If there's an error response from the server, handle it here
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(ReadyToPrintStudent.this, "Error:" + errorMessage, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(ReadyToPrintStudent.this);
        requestQueue.add(jsonObjectRequest);
    }



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