package com.example.idcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.idcard.api.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import android.Manifest;


// Import statements (assuming Volley library is imported)


public class AddSchool extends AppCompatActivity {

    String selectedNamesStudents, selectedNamesStaff; // String which stores the selected checkbox names
    private EditText editTextSchoolName, editTextMobileNo, editTextEmail, editTextPassword, editTextConfirmPassword, editTextAddress;
    private TextView addSchool, textViewPassword, textViewConfirmPassword;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school);

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

        addSchool = findViewById(R.id.textview_add_school);
        btnSave = findViewById(R.id.btn_save);


        editTextSchoolName = findViewById(R.id.editTextSchoolName);
        editTextMobileNo = findViewById(R.id.editTextMobileNo);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextAddress = findViewById(R.id.editTextAddress);

        textViewPassword = findViewById(R.id.textFieldPassword);
        textViewConfirmPassword = findViewById(R.id.textFieldConfirmPassword);

        // Checking the last screen of the app
        Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        String id = getSchoolId();

        // Setting UI for Edit School Screen
        if(from.equals("SchoolList")){
            // Changing button and heading of the screen
            addSchool.setText("Edit School");
            btnSave.setText("Edit");

            // Setting the editText from the last screen School's data
            editTextSchoolName.setText(intent.getStringExtra("nameSchool"));
            editTextMobileNo.setText(intent.getStringExtra("contactSchool"));
            editTextAddress.setText((intent.getStringExtra("addressSchool")));
            editTextEmail.setText(intent.getStringExtra("emailSchool"));

            // Making the Password fields invisible
            editTextPassword.setVisibility(View.GONE);
            editTextConfirmPassword.setVisibility(View.GONE);
            textViewPassword.setVisibility(View.GONE);
            textViewConfirmPassword.setVisibility(View.GONE);

            // Checking the required fields check boxes
            String reqFieldsStudent = intent.getStringExtra("reqFieldsStudents");
            String reqFieldsStaff = intent.getStringExtra("reqFieldsStaffs");

            // Split the reqFieldsStudent string into individual field names
            String[] requiredFields = reqFieldsStudent.split(", ");

            // Iterate through each field name
            for (String field : requiredFields) {
                // Find the corresponding checkbox based on the field name
                CheckBox checkBox = null;
                switch (field) {
                    case "Student Name":
                        checkBox = findViewById(R.id.checkBox_student_name);
                        break;
                    case "Father's Name":
                        checkBox = findViewById(R.id.checkBox_father_name);
                        break;
                    case "Mother's Name":
                        checkBox = findViewById(R.id.checkBox_mother_name);
                        break;
                    case "Date of Birth":
                        checkBox = findViewById(R.id.checkBox_date_of_birth);
                        break;
                    case "Contact No.":
                        checkBox = findViewById(R.id.checkBox_mobile);
                        break;
                    case "Address":
                        checkBox = findViewById(R.id.checkBox_address);
                        break;
                    case "Class":
                        checkBox = findViewById(R.id.checkBox_class);
                        break;
                    case "Section":
                        checkBox = findViewById(R.id.checkBox_section);
                        break;
                    case "Roll No.":
                        checkBox = findViewById(R.id.checkBox_roll_no);
                        break;
                    case "Admission No.":
                        checkBox = findViewById(R.id.checkBox_admission_no);
                        break;
                    case "Student ID":
                        checkBox = findViewById(R.id.checkBox_student_id);
                        break;
                    case "Aadhar No.":
                        checkBox = findViewById(R.id.checkBox_aadhar_no);
                        break;
                    case "Blood Group":
                        checkBox = findViewById(R.id.checkBox_blood_group);
                        break;
                    case "Ribbon Colour":
                        checkBox = findViewById(R.id.checkBox_ribbon_colour);
                        break;
                    case "Route No.":
                        checkBox = findViewById(R.id.checkBox_bus_no);
                        break;
                    case "Mode of Transport":
                        checkBox = findViewById(R.id.checkBox_mode_of_transport);
                        break;
                }

                // Check the checkbox if found
                if (checkBox != null) {
                    checkBox.setChecked(true);
                }
            }

            // Split the reqFieldsStaff string into individual field names
            String[] requiredStaffFields = reqFieldsStaff.split(", ");

            // Iterate through each field name
            for (String field : requiredStaffFields) {
                // Find the corresponding checkbox based on the field name
                CheckBox checkBox = null;
                switch (field) {
                    case "Name":
                        checkBox = findViewById(R.id.checkBox_staff_name);
                        break;
                    case "Father's Name":
                        checkBox = findViewById(R.id.checkBox_staff_father_name);
                        break;
                    case "Husband's Name":
                        checkBox = findViewById(R.id.checkBox_staff_husband_name);
                        break;
                    case "Date of Birth":
                        checkBox = findViewById(R.id.checkBox_staff_date_of_birth);
                        break;
                    case "Qualification":
                        checkBox = findViewById(R.id.checkBox_staff_qualification);
                        break;
                    case "Designation":
                        checkBox = findViewById(R.id.checkBox_staff_designation);
                        break;
                    case "Date of Joining":
                        checkBox = findViewById(R.id.checkBox_staff_doj);
                        break;
                    case "Staff Type":
                        checkBox = findViewById(R.id.checkBox_staff_type);
                        break;
                    case "Address":
                        checkBox = findViewById(R.id.checkBox_staff_address);
                        break;
                    case "Contact No.":
                        checkBox = findViewById(R.id.checkBox_staff_mobile);
                        break;
                    case "UID No.":
                        checkBox = findViewById(R.id.checkBox_staff_aadhar_no);
                        break;
                    case "E-mail":
                        checkBox = findViewById(R.id.checkBox_staff_email);
                        break;
                    case "Staff ID":
                        checkBox = findViewById(R.id.checkBox_staff_id);
                        break;
                    case "UDISE Code":
                        checkBox = findViewById(R.id.checkBox_staff_udise_code);
                        break;
                    case "Office Name":
                        checkBox = findViewById(R.id.checkBox_staff_office);
                        break;
                    case "Blood Group":
                        checkBox = findViewById(R.id.checkBox_staff_blood_group);
                        break;
                    case "Dispatch No.":
                        checkBox = findViewById(R.id.checkBox_staff_dispatch_no);
                        break;
                    case "Date of Issue":
                        checkBox = findViewById(R.id.checkBox_staff_doi);
                        break;
                    case "IHRMS No.":
                        checkBox = findViewById(R.id.checkBox_staff_ihrms_no);
                        break;
                    case "Belt No.":
                        checkBox = findViewById(R.id.checkBox_staff_belt_no);
                        break;
                }

                // Check the checkbox if found
                if (checkBox != null) {
                    checkBox.setChecked(true);
                }
            }



        }


        // Find the parent layout containing all the LinearLayouts with TextViews and CheckBoxes
        LinearLayout studentLayout = findViewById(R.id.LinearLayoutStudentData);
        LinearLayout staffLayout = findViewById(R.id.LinearLayoutSatffIdCardData);

        // Iterate over each child view of the student layout to get selected student names
        for (int i = 1; i < studentLayout.getChildCount(); i++) { // starting from index 1 to skip the first TextView
            View view = studentLayout.getChildAt(i);

            // Check if the view is a LinearLayout containing a TextView and a CheckBox
            if (view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;

                // Find the CheckBox in the LinearLayout
                CheckBox checkBox = (CheckBox) linearLayout.getChildAt(1);

                // Add a listener to the CheckBox
                checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    // Update the selected names when the checkbox state changes
                    selectedNamesStudents = getSelectedNames(studentLayout);
                });
            }
        }

        // Iterate over each child view of the staff layout to get selected staff names
        for (int i = 1; i < staffLayout.getChildCount(); i++) { // starting from index 1 to skip the first TextView
            View view = staffLayout.getChildAt(i);

            // Check if the view is a LinearLayout containing a TextView and a CheckBox
            if (view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;

                // Find the CheckBox in the LinearLayout
                CheckBox checkBox = (CheckBox) linearLayout.getChildAt(1);

                // Add a listener to the CheckBox
                checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    // Update the selected names when the checkbox state changes
                    selectedNamesStaff = getSelectedNames(staffLayout);
                });
            }
        }




        // -------------------------------------------------------------------------------------------------------------------
        // Cancel button functionality
        Button cancel = findViewById(R.id.btnCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSchoolName.setText("");
                editTextMobileNo.setText("");
                editTextEmail.setText("");
                editTextAddress.setText("");
                editTextPassword.setText("");
                editTextConfirmPassword.setText("");
            }
        });
        // End of Cancel button functionality
        // ---------------------------------------------------------------------------------------------------------------------


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String schoolName = editTextSchoolName.getText().toString().trim();
                String mobileNo = editTextMobileNo.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();


                if(from.equals("SchoolList")){
                    btnSave.setText("Updating...");
                    updateSchool(schoolName, mobileNo, email, address,id);

                }
                else{
                    btnSave.setText("Saving...");
                    registerSchool(schoolName, mobileNo, email, password, confirmPassword, address);
                }
            }
        });
    }
    // Main function ends
    // -------------------------------------------------------------------------------------------------------------------


    // sending text data to api endpoint
    private void registerSchool(String schoolName, String mobileNo, String email, String password, String confirmPassword,
                              String address) {

        String URL = "https://id-card-backend-2.onrender.com/user/registration/school";
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            btnSave.setText("Save");
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), "School added successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (JSONException e) {
                            btnSave.setText("Save");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            btnSave.setText("Save");
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(AddSchool.this, errorMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            btnSave.setText("Save");
                            Toast.makeText(AddSchool.this, "Error adding school", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", schoolName);
                params.put("address", address);
                params.put("contact", mobileNo);
                params.put("email", email);
                params.put("password", password);
                params.put("requiredFields", selectedNamesStudents);
                params.put("requiredFieldsStaff",selectedNamesStaff);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken()); // Replace "YOUR_TOKEN_HERE" with your actual token
                return headers;
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    btnSave.setText("Save");
                    String errorMessage = new String(error.networkResponse.data);
                    Toast.makeText(AddSchool.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    btnSave.setText("Save");
                    Toast.makeText(AddSchool.this, "Error adding school", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            btnSave.setText("Save");
            Toast.makeText(AddSchool.this, "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if all required fields are filled
        if (schoolName.isEmpty() || mobileNo.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || address.isEmpty()) {
            btnSave.setText("Save");
            Toast.makeText(AddSchool.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    // End of sending text data to api endpoint

    // --------------------------------------------------------------------------------------------------------------------

    // update school data
    private void updateSchool(String schoolName, String mobileNo, String email, String address, String schoolId) {

        String URL = "https://id-card-backend-2.onrender.com/user/edit/school/" + schoolId;
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            btnSave.setText("Edit");
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), "School updated successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (JSONException e) {
                            btnSave.setText("Edit");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            btnSave.setText("Edit");
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(AddSchool.this, errorMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            btnSave.setText("Edit");
                            Toast.makeText(AddSchool.this, "Error adding school", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", schoolName);
                params.put("address", address);
                params.put("contact", mobileNo);
                params.put("email", email);
                params.put("requiredFields", selectedNamesStudents);
                params.put("requiredFieldsStaff",selectedNamesStaff);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                return headers;
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    btnSave.setText("Edit");
                    String errorMessage = new String(error.networkResponse.data);
                    Toast.makeText(AddSchool.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    btnSave.setText("Edit");
                    Toast.makeText(AddSchool.this, "Error adding school", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Check if all required fields are filled
        if (schoolName.isEmpty() || mobileNo.isEmpty() || email.isEmpty() || address.isEmpty()) {
            btnSave.setText("Save");
            Toast.makeText(AddSchool.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
    // end of  update school data

    // -------------------------------------------------------------------------------------------------------------------------

    // Method to get a string which contains the name of checked checkbox
    private String getSelectedNames(LinearLayout parentLayout) {
        StringBuilder selectedNames = new StringBuilder();

        // Iterate over each child view of the parent layout
        for (int i = 1; i < parentLayout.getChildCount(); i++) { // starting from index 1 to skip the first TextView
            View view = parentLayout.getChildAt(i);

            // Check if the view is a LinearLayout containing a TextView and a CheckBox
            if (view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;

                // Find the CheckBox in the LinearLayout
                CheckBox checkBox = (CheckBox) linearLayout.getChildAt(1);

                // Find the TextView in the LinearLayout
                TextView textView = (TextView) linearLayout.getChildAt(0);

                // If the checkbox is checked, add the text to the selectedNames StringBuilder
                if (checkBox.isChecked()) {
                    selectedNames.append(textView.getText()).append(", ");
                }
            }
        }

        // Remove the trailing comma and space if any
        if (selectedNames.length() > 0) {
            selectedNames.delete(selectedNames.length() - 2, selectedNames.length());
        }

        return selectedNames.toString();
    }

    //End of  Method to get a string which contains the name of checked checkbox
    // --------------------------------------------------------------------------------------------------------------------------


    // Method to get the token saved in local storage
    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    private String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }

    private String getSchoolId() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("schoolIdFromListStudent", "");
    }
    // End of method to get the token saved in local storage

    // --------------------------------------------------------------------------------------------------------------------------
}