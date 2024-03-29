package com.example.idcard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportStudents extends AppCompatActivity {

    // Drop Down
    List<String> schoolNames = new ArrayList<>(); // List to store school names
    List<String> schoolId = new ArrayList<>();
    List<String> requiredFieldsList = new ArrayList<>(); // List to store the required fields for student details
    AutoCompleteTextView autoCompleteSchool;
    ArrayAdapter<String> adapterSchool;
    private static final int PICK_FILE_REQUEST_CODE = 1;

    private File imgFile; // Global variable to store the selected image file

    // Image  picker
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private Button btnChooseImage;
    private TextView noFileChosen;
    private String filePath; // To store the selected file path
    private static final int REQUEST_CODE = 123;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_students);

        // Setting the distributor name
        TextView userName = findViewById(R.id.userName);
        userName.setText(getUserName());

        // School Dropdown
        autoCompleteSchool = findViewById(R.id.school_dropdown);
        fetchUserData();

        noFileChosen = findViewById(R.id.text_no_file_chosen);
        Button addStudnt = findViewById(R.id.buttonAddStudent);
        addStudnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Set click listener for the "Choose File" button
        Button chooseFileButton = findViewById(R.id.btn_choose_file);
        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open file chooser intent
               /* Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.ms-excel"); // Filter for Excel files
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);*/

                // Open file picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Excel MIME type
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

    }
    // Main function ends

    // ------------------------------------------------------------------------------------------------------------------------

    // File upload functions


    // Call this method when you want to initiate the upload process (e.g., on a button click)


    // new

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri filePath = data.getData();
            String fileName = getRealPathFromUri(filePath); // Get actual path from URI

            // Update UI to show selected file
            TextView textNoFileChosen = findViewById(R.id.text_no_file_chosen);
            textNoFileChosen.setText(fileName);
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String path = null;
        if (uri.getPath().startsWith("/storage")) {
            path = uri.getPath();
        } else {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return path;
    }*/


    // Function to fetch schools from the api endpoint
    private void fetchUserData() {
        String token = getToken();

        String url = "https://id-card-backend-2.onrender.com/user/schools";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ImportStudents.this, "Data Fetched Successfully", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(ImportStudents.this, "Error in data fetching", Toast.LENGTH_SHORT).show();
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

    // End of Function to fetch schools from the api endpoint

    // ------------------------------------------------------------------------------------------------------------------------

    // Update the drop down list
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
            }
        });
    }

    // End of Update the drop down list

    // ------------------------------------------------------------------------------------------------------------------------

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