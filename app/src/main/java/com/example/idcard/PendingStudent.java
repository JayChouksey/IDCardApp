package com.example.idcard;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.android.volley.toolbox.Volley;
import com.example.idcard.ImageDownloadHelper.ImageDownloader;
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

public class PendingStudent extends AppCompatActivity {

    RecyclerView recyclerView;
    DynamicStudentAdapter adapter;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_student);

        intent = getIntent();

        String role = intent.getStringExtra("Role");
        if(role.equals("Student")){
            // Fetching student data
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
        Button statusReadyToPrint = findViewById(R.id.moveReadyToPrintButton);
        Button statusPrinted = findViewById(R.id.movePrintedButton);
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
        statusReadyToPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getSelectedStudentIds();
                if(role.equals("Student")){
                    changeStatusReadyToPrint(id);
                }
                else{
                    changeStatusReadyToPrintStaff(id);
                }
                finish();
            }
        });
        statusPrinted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getSelectedStudentIds();
                if(role.equals("Student")){
                    changeStatusPrinted(id);
                }
                else{
                    changeStatusPrintedStaff(id);
                }
                finish();
            }
        });

        exportExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(role.equals("Student")) {
                    downloadExcelFile(PendingStudent.this);
                }
                else{
                    downloadExcelFileStaff(PendingStudent.this);
                }
            }
        });
        downloadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(role.equals("Student")) {
                    downloadImages(PendingStudent.this);
                }
                else{
                    downloadImagesStaff(PendingStudent.this);
                }
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
                                    if (key.equals("avatar") || key.equals("__v") || key.equals("createdAt") ||
                                            key.equals("updatedAt") || key.equals("photoName") || key.equals("user")) {
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
                        Toast.makeText(PendingStudent.this, "Data Fetched Successfully", Toast.LENGTH_SHORT).show();
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
                                            key.equals("updatedAt") || key.equals("photoName") || key.equals("user")) {
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

    // End of Fetching student and staff data


    // Download image and excel
    public void downloadExcelFile(Context context) {
        String schoolId = getId();
        String url = "https://id-card-backend-2.onrender.com/user/excel/data/" + schoolId + "/?status=Panding";

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
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Pending Student Data.xlsx");

        // Add headers to the request
        request.addRequestHeader("Authorization", getToken());

        // Get the download manager and enqueue the request
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        // Optionally, you can listen for download completion to show a toast message
        // using BroadcastReceiver or DownloadManager.Query
    }

    public void downloadExcelFileStaff(Context context) {
        String schoolId = getId();
        String url = "https://id-card-backend-2.onrender.com/user/staff/excel/data/" + schoolId + "/?status=Panding";

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
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Pending Staff Data.xlsx");

        // Add headers to the request
        request.addRequestHeader("Authorization", getToken());

        // Get the download manager and enqueue the request
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        // Optionally, you can listen for download completion to show a toast message
        // using BroadcastReceiver or DownloadManager.Query
    }

    public void downloadImages(Context context) {
        String schoolId = getId();

        String url = "https://id-card-backend-2.onrender.com/user/student/images/" + schoolId + "/?status=Panding";
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray studentImages = response.getJSONArray("studentImages");
                            for (int i = 0; i < studentImages.length(); i++) {
                                String imageUrl = studentImages.getString(i);
                                String folderName = "Pending Student Images";
                                ImageDownloader.downloadImage(context, imageUrl, folderName);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data);
                    Toast.makeText(context, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to fetch image URLs", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Add your headers here
                headers.put("Authorization", getToken());
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    public void downloadImagesStaff(Context context) {
        String schoolId = getId();

        String url = "https://id-card-backend-2.onrender.com/user/staff/images/" + schoolId + "/?status=Panding";
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray studentImages = response.getJSONArray("staffImages");
                            for (int i = 0; i < studentImages.length(); i++) {
                                String imageUrl = studentImages.getString(i);
                                String folderName = "Pending Staff Images";
                                ImageDownloader.downloadImage(context, imageUrl, folderName);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data);
                    Toast.makeText(context, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to fetch image URLs", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Add your headers here
                headers.put("Authorization", getToken());
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
    // End of Download image and excel

    // Method to update school list recycler view
    private void updateRecyclerView(List<DynamicStudent> studentList) {
        recyclerView = findViewById(R.id.student_list_recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PendingStudent.this));
        adapter = new DynamicStudentAdapter(studentList, PendingStudent.this);
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

    public void changeStatusReadyToPrintStaff(String staffIds) {
        // Create JSON object with the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("staffIds", staffIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create request URL
        String schoolId = getId();
        String url = "https://id-card-backend-2.onrender.com/user/staff/change-status/readyto/" + schoolId;

        // Create request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        Toast.makeText(PendingStudent.this, "Status of selected staff updated successfully", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PendingStudent.this, "Status of selected staffs updated successfully", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PendingStudent.this, "Status of selected staffs updated successfully", Toast.LENGTH_SHORT).show();
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