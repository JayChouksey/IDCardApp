package com.example.idcard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.idcard.api.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddDataForSchool extends AppCompatActivity {

    String [] role = {"Student", "Staff"};
    String strRole;
    AutoCompleteTextView autoCompleteRole;
    ArrayAdapter<String> adapterRole;
    Bitmap bitmap;
    Button buttonAdd;
    Button buttonChooseImage;
    TextView textNoFileChosen;
    ImageView imgChosen;
    Button saveButton, resetButton, cancelButton;
    private HashMap<String, String> fieldValueMap = new HashMap<>(); // for Student
    private HashMap<String, String> fieldValueMapStaff = new HashMap<>(); // for Staff
    LinearLayout dynamicLayout; // Dynamic layout for add students data
    LinearLayout buttonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data_for_school);

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

        // Role Dropdown
        autoCompleteRole = findViewById(R.id.role_dropdown);
        adapterRole = new ArrayAdapter<String>(this,R.layout.list_item,role);
        autoCompleteRole.setAdapter(adapterRole);

        // components initialization
        saveButton = findViewById(R.id.saveButton);
        resetButton = findViewById(R.id.resetButton);
        cancelButton = findViewById(R.id.cancelButton);
        buttonChooseImage = findViewById(R.id.chooseImageButton);
        textNoFileChosen = findViewById(R.id.noFileChosenTextView);
        imgChosen = findViewById(R.id.imageChosen);
        // Add student details dynamically
        buttonAdd = findViewById(R.id.buttonAddStudent);
        dynamicLayout = findViewById(R.id.dynamicLayout);
        // Find the buttonLayout
        buttonLayout = findViewById(R.id.buttonLayout);

        autoCompleteRole.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Switch case to set strStatus based on selected item
                switch (position) {
                    case 0:
                        strRole = "Student";
                        buttonAdd.setText("Add Student");
                        break;
                    case 1:
                        strRole = "Staff";
                        buttonAdd.setText("Add Staff");
                        break;
                }
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strRole.equals("Student")){
                    fetchRequiredFields(getSchoolId());
                }
                else{
                    fetchRequiredFieldsStaff(getSchoolId());
                }
            }
        });

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
                imgChosen.setImageBitmap(null);
                textNoFileChosen.setText("No file chosen");
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    // Main function ends

    // -------------------------------------------------------------------------------------------------------------------------

    // Function to add student to database
    private void addStudentToDatabase(String id, String requiredFields) {
        // Get the authorization token
        String token = getToken();

        // Construct the API endpoint URL
        String apiEndpoint = "https://id-card-backend-2.onrender.com/user/registration/student/" + id;

        // Create parameters for the POST request
        Map<String, String> params = new HashMap<>();

        // Add other required fields dynamically
        String[] fields = requiredFields.split(",");
        for (String field : fields) {
            String trimmedField = field.trim();
            switch (trimmedField) {
                case "Student Name":
                    params.put("name", fieldValueMap.get("studentName"));
                    break;
                case "Father's Name":
                    params.put("fatherName", fieldValueMap.get("fatherName"));
                    break;
                case "Mother's Name":
                    params.put("motherName", fieldValueMap.get("motherName"));
                    break;
                case "Date of Birth":
                    params.put("dob", fieldValueMap.get("dob"));
                    break;
                case "Contact No.":
                    params.put("contact", fieldValueMap.get("contact"));
                    break;
                case "Address":
                    params.put("address", fieldValueMap.get("address"));
                    break;
                case "Class":
                    params.put("class", fieldValueMap.get("class"));
                    break;
                case "Section":
                    params.put("section", fieldValueMap.get("section"));
                    break;
                case "Roll No.":
                    params.put("rollNo", fieldValueMap.get("rollNo"));
                    break;
                case "Admission No.":
                    params.put("admissionNo", fieldValueMap.get("admissionNo"));
                    break;
                case "Student ID":
                    params.put("studentID", fieldValueMap.get("studentID"));
                    break;
                case "Aadhar No.":
                    params.put("aadharNo", fieldValueMap.get("aadharNo"));
                    break;
                case "Blood Group":
                    params.put("bloodGroup", fieldValueMap.get("bloodGroup"));
                    break;
                case "Ribbon Colour":
                    params.put("ribbonColour", fieldValueMap.get("ribbonColour"));
                    break;
                case "Route No.":
                    params.put("routeNo", fieldValueMap.get("routeNo"));
                    break;
                case "Mode of Transport":
                    params.put("modeOfTransport", fieldValueMap.get("modeOfTransport"));
                    break;
            }
        }

        // Make sure all required fields are provided
        if (params.size() < requiredFields.split(",").length) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the VolleyMultipartRequest
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, apiEndpoint,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(AddDataForSchool.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Display error message from the server
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data);
                    Toast.makeText(AddDataForSchool.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddDataForSchool.this, "Error adding student", Toast.LENGTH_SHORT).show();
                }
                // Handle error response
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Pass parameters as form data
                return params;
            }

            /*
             * Adding photo
             */
            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("file", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(bitmap)));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                // Pass your headers here
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    // End of Function to add student to database

    // ------------------------------------------------------------------------------------------------------------------------

    // Function to add student to database
    private void addStaffToDatabase(String id, String requiredFields) {
        // Get the authorization token
        String token = getToken();

        // Construct the API endpoint URL
        String apiEndpoint = "https://id-card-backend-2.onrender.com/user/registration/staff/" + id;

        // Create parameters for the POST request
        Map<String, String> params = new HashMap<>();

        // Add other required fields dynamically
        String[] fields = requiredFields.split(",");
        for (String field : fields) {
            String trimmedField = field.trim();
            switch (trimmedField) {
                case "Name":
                    params.put("name", fieldValueMapStaff.get("name"));
                    break;
                case "Father's Name":
                    params.put("fatherName", fieldValueMapStaff.get("fatherName"));
                    break;
                case "Husband's Name":
                    params.put("husbandName", fieldValueMapStaff.get("husbandName"));
                    break;
                case "Date of Birth":
                    params.put("dob", fieldValueMapStaff.get("dob"));
                    break;
                case "Qualification":
                    params.put("qualification", fieldValueMapStaff.get("qualification"));
                    break;
                case "Designation":
                    params.put("designation", fieldValueMapStaff.get("designation"));
                    break;
                case "Date of Joining":
                    params.put("doj", fieldValueMapStaff.get("doj"));
                    break;
                case "Staff Type":
                    params.put("staffType", fieldValueMapStaff.get("staffType"));
                    break;
                case "Address":
                    params.put("address", fieldValueMapStaff.get("address"));
                    break;
                case "Contact No.":
                    params.put("contact", fieldValueMapStaff.get("contact"));
                    break;
                case "UID No.":
                    params.put("uid", fieldValueMapStaff.get("UIDNo"));
                    break;
                case "E-mail":
                    params.put("email", fieldValueMapStaff.get("email"));
                    break;
                case "Staff ID":
                    params.put("staffID", fieldValueMapStaff.get("staffID"));
                    break;
                case "UDISE Code":
                    params.put("udiseCode", fieldValueMapStaff.get("UDISECode"));
                    break;
                case "Office Name":
                    params.put("schoolName", fieldValueMapStaff.get("officeName"));
                    break;
                case "Blood Group":
                    params.put("bloodGroup", fieldValueMapStaff.get("bloodGroup"));
                    break;
                case "Dispatch No.":
                    params.put("dispatchNo", fieldValueMapStaff.get("dispatchNo"));
                    break;
                case "Date of Issue":
                    params.put("doi", fieldValueMapStaff.get("doi"));
                    break;
                case "IHRMS No.":
                    params.put("ihrmsNo", fieldValueMapStaff.get("IHRMSNo"));
                    break;
                case "Belt No.":
                    params.put("beltNo", fieldValueMapStaff.get("beltNo"));
                    break;
            }

        }

        // Make sure all required fields are provided
       /* if (params.size() < requiredFields.split(",").length) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }*/

        // Create the VolleyMultipartRequest
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, apiEndpoint,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(AddDataForSchool.this, "Staff added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Display error message from the server
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data);
                    Toast.makeText(AddDataForSchool.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddDataForSchool.this, "Error adding staff", Toast.LENGTH_SHORT).show();
                }
                // Handle error response
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Pass parameters as form data
                return params;
            }

            /*
             * Adding photo
             */
            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("file", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(bitmap)));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                // Pass your headers here
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    // End of Function to add student to database

    // ------------------------------------------------------------------------------------------------------------------------

    // ------------------------------------------------------------------------------------------------------------------------

    // Method to fetch required fields students
    private void fetchRequiredFields(String id) {
        String url = "https://id-card-backend-2.onrender.com/user/school/requiredfields/" + id;

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create the request object
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse the JSON response
                            String requiredFields = response.getString("requiredFields");

                                    String[] fields = requiredFields.split(",");
                                    for (String field : fields) {
                                        switch (field.trim()) {
                                            case "Student Name":
                                                addField("Student Name", "Enter Student name", "studentName");
                                                break;
                                            case "Father's Name":
                                                addField("Father's Name", "Enter Father name", "fatherName");
                                                break;
                                            case "Mother's Name":
                                                addField("Mother's Name", "Enter Mother Name", "motherName");
                                                break;
                                            case "Date of Birth":
                                                addField("Date of Birth", "Enter Date of Birth", "dob");
                                                break;
                                            case "Contact No.":
                                                addField("Contact No.", "Enter Contact No.", "contact");
                                                break;
                                            case "Address":
                                                addField("Address", "Enter Address", "address");
                                                break;
                                            case "Class":
                                                addField("Class", "Enter Class", "class");
                                                break;
                                            case "Section":
                                                addField("Section", "Enter Section", "section");
                                                break;
                                            case "Roll No.":
                                                addField("Roll No.", "Enter Roll No.", "rollNo");
                                                break;
                                            case "Admission No.":
                                                addField("Admission No.", "Enter Admission No.", "admissionNo");
                                                break;
                                            case "Student ID":
                                                addField("Student ID", "Enter Student ID", "studentID");
                                                break;
                                            case "Aadhar No.":
                                                addField("Aadhar No.", "Enter Aadhar No.", "aadharNo");
                                                break;
                                            case "Blood Group":
                                                addField("Blood Group", "Enter Blood Group", "bloodGroup");
                                                break;
                                            case "Ribbon Colour":
                                                addField("Ribbon Colour", "Enter Ribbon Colour", "ribbonColour");
                                                break;
                                            case "Route No.":
                                                addField("Route No.", "Enter Route No.", "routeNo");
                                                break;
                                            case "Mode of Transport":
                                                addField("Mode of Transport", "Enter Mode of Transport", "modeOfTransport");
                                                break;
                                        }
                                    }
                                    // Disable the button after fields are added
                                    // Change the visibility of buttonLayout to VISIBLE
                                    buttonLayout.setVisibility(View.VISIBLE);
                                    buttonAdd.setEnabled(false);

                                    saveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            saveButton.setText("Saving...");
                                            addStudentToDatabase(id,requiredFields);
                                        }
                                    });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddDataForSchool.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(AddDataForSchool.this, "Error fetching required fields", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                // Pass your headers here
                Map<String, String> headers = new HashMap<>();
                // Add any required headers, such as authorization token, if needed
                headers.put("Authorization", getToken());
                return headers;
            }
        };

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }
    // End of Method to fetch required fields students

    // ------------------------------------------------------------------------------------------------------------------------

    // Method to fetch required fields students
    private void fetchRequiredFieldsStaff(String id) {
        String url = "https://id-card-backend-2.onrender.com/user/school/requiredfields/" + id;

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create the request object
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse the JSON response
                            String requiredFieldsStaff = response.getString("requiredFieldsStaff");

                                    String[] fields = requiredFieldsStaff.split(",");
                                    for (String field : fields) {
                                        switch (field.trim()) {
                                            case "Name":
                                                addFieldStaff("Name", "Enter name", "name");
                                                break;
                                            case "Father's Name":
                                                addFieldStaff("Father's Name", "Enter Father's name", "fatherName");
                                                break;
                                            case "Husband's Name":
                                                addFieldStaff("Husband's Name", "Enter Husband's Name", "husbandName");
                                                break;
                                            case "Date of Birth":
                                                addFieldStaff("Date of Birth", "Enter Date of Birth", "dob");
                                                break;
                                            case "Qualification":
                                                addFieldStaff("Qualification", "Enter Qualification", "qualification");
                                                break;
                                            case "Designation":
                                                addFieldStaff("Designation", "Enter Designation", "designation");
                                                break;
                                            case "Date of Joining":
                                                addFieldStaff("Date of Joining", "Enter Date of Joining", "doj");
                                                break;
                                            case "Staff Type":
                                                addFieldStaff("Staff Type", "Enter Staff Type", "staffType");
                                                break;
                                            case "Address":
                                                addFieldStaff("Address", "Enter Address", "address");
                                                break;
                                            case "Contact No.":
                                                addFieldStaff("Contact No.", "Enter Contact No.", "contact");
                                                break;
                                            case "UID No.":
                                                addFieldStaff("UID No.", "Enter UID No.", "UIDNo");
                                                break;
                                            case "E-mail":
                                                addFieldStaff("E-mail", "Enter Email", "email");
                                                break;
                                            case "Staff ID":
                                                addFieldStaff("Staff ID", "Enter Staff ID", "staffID");
                                                break;
                                            case "USIDE Code":
                                                addFieldStaff("USIDE Code", "Enter USIDE Code", "USIDECode");
                                                break;
                                            case "Office Name":
                                                addFieldStaff("Office Name", "Enter Office Name", "officeName");
                                                break;
                                            case "Blood Group":
                                                addFieldStaff("Blood Group", "Enter Blood Group", "bloodGroup");
                                                break;
                                            case "Dispatch No.":
                                                addFieldStaff("Address", "Enter Address", "address");
                                                break;
                                            case "Date of Issue":
                                                addFieldStaff("Date of Issue", "Enter Date of Issue", "doi");
                                                break;
                                            case "IHRMS No.":
                                                addFieldStaff("IHRMS No.", "Enter IHRMS No.", "IHRMSNo");
                                                break;
                                            case "Belt No.":
                                                addFieldStaff("Belt No.", "Enter Belt No.", "beltNo");
                                                break;
                                        }
                                    }
                                    // Disable the button after fields are added
                                    // Change the visibility of buttonLayout to VISIBLE
                                    buttonLayout.setVisibility(View.VISIBLE);
                                    buttonAdd.setEnabled(false);

                                    saveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            saveButton.setText("Saving...");
                                            addStaffToDatabase(id,requiredFieldsStaff);
                                        }
                                    });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddDataForSchool.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(AddDataForSchool.this, "Error fetching required fields", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                // Pass your headers here
                Map<String, String> headers = new HashMap<>();
                // Add any required headers, such as authorization token, if needed
                headers.put("Authorization", getToken());
                return headers;
            }
        };

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }

    // End of Method to fetch required fields students

    // ------------------------------------------------------------------------------------------------------------------------

    // Add Field function
    private void addField(String label, String hint, String fieldIdentifier) {
        // Create a new TextView dynamically for label
        TextView textView = new TextView(AddDataForSchool.this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(label + "*");
        textView.setTextSize(16);
        textView.setPadding(10, 10, 10, 10);

        // Create a new EditText dynamically
        EditText editText = new EditText(AddDataForSchool.this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setBackgroundResource(R.drawable.edittext_background);
        editText.setPadding(30, 30, 30, 30);
        editText.setHint(hint);

        // Add a text change listener to update the HashMap when the text changes
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Update the HashMap with the current value of the EditText
                fieldValueMap.put(fieldIdentifier, s.toString());
            }
        });

        // Add the TextView and EditText to the layout container
        dynamicLayout.addView(textView);
        dynamicLayout.addView(editText);
    }

    private void addFieldStaff(String label, String hint, String fieldIdentifier) {
        // Create a new TextView dynamically for label
        TextView textView = new TextView(AddDataForSchool.this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(label + "*");
        textView.setTextSize(16);
        textView.setPadding(10, 10, 10, 10);

        // Create a new EditText dynamically
        EditText editText = new EditText(AddDataForSchool.this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setBackgroundResource(R.drawable.edittext_background);
        editText.setPadding(30, 30, 30, 30);
        editText.setHint(hint);

        // Add a text change listener to update the HashMap when the text changes
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Update the HashMap with the current value of the EditText
                fieldValueMapStaff.put(fieldIdentifier, s.toString());
            }
        });

        // Add the TextView and EditText to the layout container
        dynamicLayout.addView(textView);
        dynamicLayout.addView(editText);
    }

    // End of Add Field function

    // To reset all the fields
    private void resetFields() {
        // Iterate through the dynamic layout and reset EditText fields
        int childCount = dynamicLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = dynamicLayout.getChildAt(i);
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                editText.setText(""); // Clear the text
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------------

    // Image upload functions

    private void showImagePickerOptions() {
        // Options dialog to choose between picking from gallery or capturing from camera
        CharSequence[] options = {"Choose from Gallery", "Take Photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    // Choose from Gallery
                    chooseImageFromGallery();
                } else if (item == 1) {
                    // Capture from Camera
                    takePhoto();
                }
            }
        });
        builder.show();
    }

    private void chooseImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 200);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100 && data != null) {
                // Image selected from gallery
                Uri imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imgChosen.setImageBitmap(bitmap);
                    textNoFileChosen.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 200 && data != null) {
                // Image captured from camera
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                bitmap = imageBitmap;
                imgChosen.setImageBitmap(imageBitmap);
                textNoFileChosen.setText("");
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // End of Image upload functions

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