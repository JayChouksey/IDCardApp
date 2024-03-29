package com.example.idcard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStudent extends AppCompatActivity {
    // Drop Down
    List<String> schoolNames = new ArrayList<>(); // List to store school names
    List<String> schoolId = new ArrayList<>(); // List to store schoolId

    AutoCompleteTextView autoCompleteSchool;
    ArrayAdapter<String> adapterSchool;
    TextView textView; // to test
    Button buttonAddStudent;
    Button saveButton;

    private HashMap<String, String> fieldValueMap = new HashMap<>();

    LinearLayout dynamicLayout; // Dynamic layout for add students data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        // Setting the distributor name
        TextView userName = findViewById(R.id.userName);
        userName.setText(getUserName());


        // School Dropdown
        autoCompleteSchool = findViewById(R.id.school_dropdown);
        fetchUserData();

/*        saveButton = new Button(AddStudent.this); // Initialize saveButton
        saveButton.setId(View.generateViewId()); // Giving id to the button
        saveButton = findViewById(saveButton.getId()); // Retrieve button using its ID*/


        // Add student details dynamically
        buttonAddStudent = findViewById(R.id.buttonAddStudent);
        dynamicLayout = findViewById(R.id.dynamicLayout);
        // It will run when no school is selected from the dropdown
        buttonAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddStudent.this, "Select Any School", Toast.LENGTH_SHORT).show();
            }
        });

        textView = findViewById(R.id.test);


    }
    // Main function ends

    // ------------------------------------------------------------------------------------------------------------------------

    // Method to fetch required fields
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

                            buttonAddStudent.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String[] fields = requiredFields.split(",");
                                    for (String field : fields) {
                                        switch (field.trim()) {
                                            case "Student Name":
                                                addField("Student Name", "Enter Student name", "studentName");
                                                break;
                                            case "Father Name":
                                                addField("Father Name", "Enter Father name", "fatherName");
                                                break;
                                            case "Mother Name":
                                                addField("Mother Name", "Enter Mother Name", "motherName");
                                                break;
                                            case "Date of Birth":
                                                addField("Date of Birth", "Enter Date of Birth", "dob");
                                                break;
                                            case "Mobile":
                                                addField("Mobile", "Enter Mobile", "contact");
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
                                            case "Admission No.":
                                                addField("Admission No.", "Enter Admission No.", "admissionNo");
                                                break;
                                            case "Bus No.":
                                                addField("Bus No.", "Enter Bus No.", "busNo");
                                                break;
                                            case "Blood Group":
                                                addField("Blood Group", "Enter Blood Group", "bloodGroup");
                                                break;
                                            case "Roll No.":
                                                addField("Roll No.", "Enter Roll No.", "rollNo");
                                                break;
                                            case "E-Mail":
                                                addField("E-Mail", "Enter E-Mail", "email");
                                                break;
                                            case "Designation":
                                                addField("Designation", "Enter Designation", "designation");
                                                break;
                                            case "Husband Name":
                                                addField("Husband Name", "Enter Husband Name", "husbandName");
                                                break;
                                            case "Emp ID":
                                                addField("Emp ID", "Enter Emp ID", "empId");
                                                break;
                                            case "Employee Name":
                                                addField("Employee Name", "Enter Employee Name", "employeeName");
                                                break;
                                            // Add cases for other required fields
                                        }
                                    }

                                    // Add the dynamic view for "Upload Photo"
                                    addUploadPhotoView();

                                    // Add LinearLayout for buttons
                                    addButtonsLayout();

                                    // Disable the button after fields are added
                                   buttonAddStudent.setEnabled(false);

                                    textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            textView.setText("Loading...");
                                            addStudentToDatabase(id,requiredFields);
                                        }
                                    });
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddStudent.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(AddStudent.this, "Error fetching required fields", Toast.LENGTH_SHORT).show();
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
    // End of Method to fetch required fields

    // ------------------------------------------------------------------------------------------------------------------------

    // Function to fetch schools for drop down from the api endpoint
    private void fetchUserData() {
        String token = getToken();

        String url = "https://id-card-backend-2.onrender.com/user/schools";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(AddStudent.this, "Data Fetched Successfully", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(AddStudent.this, "Error in data fetching", Toast.LENGTH_SHORT).show();
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

    // -------------------------------------------------------------------------------------------------------------------------

    // Method to add student to the database
    /*private void addStudentToDatabase(String id, String requiredFields) {
        // Get the authorization token
        String token = getToken();

        String url = "https://id-card-backend-2.onrender.com/user/registration/student/" + id;

        // Create JSON object for student data
        JSONObject studentData = new JSONObject();
        try {
            // Split the required fields string to get the list of fields
            String[] fields = requiredFields.split(",");

            // Add dynamic fields data to the JSON object
            for (String field : fields) {
                String trimmedField = field.trim();
                switch (trimmedField) {
                    case "Student Name":
                        String studentName = getEditTextValue("Student Name");
                        studentData.put("name", studentName);
                        break;
                    case "Father Name":
                        String fatherName = getEditTextValue("Father Name");
                        studentData.put("fatherName", fatherName);
                        break;
                    case "Mother Name":
                        String motherName = getEditTextValue("Mother Name");
                        studentData.put("motherName", motherName);
                        break;
                    case "Date of Birth":
                        String dob = getEditTextValue("Date of Birth");
                        studentData.put("dob", dob);
                        break;
                    case "Mobile":
                        String mobile = getEditTextValue("Mobile");
                        studentData.put("contact", mobile);
                        break;
                    case "Address":
                        String address = getEditTextValue("Address");
                        studentData.put("address", address);
                        break;
                    case "Class":
                        String studentClass = getEditTextValue("Class");
                        studentData.put("class", studentClass);
                        break;
                    case "Section":
                        String section = getEditTextValue("Section");
                        studentData.put("section", section);
                        break;
                    case "Admission No.":
                        String admissionNo = getEditTextValue("Admission No.");
                        studentData.put("admissionNo", admissionNo);
                        break;
                    case "Bus No.":
                        String busNo = getEditTextValue("Bus No.");
                        studentData.put("busNo", busNo);
                        break;
                    case "Blood Group":
                        String bloodGroup = getEditTextValue("Blood Group");
                        studentData.put("bloodGroup", bloodGroup);
                        break;
                    case "Roll No.":
                        String rollNo = getEditTextValue("Roll No.");
                        studentData.put("rollNo", rollNo);
                        break;
                    // Add cases for other required fields
                }
            }

            // Add other required fields here if needed

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Make a POST request to add the student to the database
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, studentData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(AddStudent.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                        // Handle successful response
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(AddStudent.this, "Error adding student", Toast.LENGTH_SHORT).show();
                Toast.makeText(AddStudent.this, new String(error.networkResponse.data), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                // Handle error response
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                // Pass your headers here
                Map<String, String> headers = new HashMap<>();
                // Add any required headers, such as authorization token, if needed
                headers.put("Authorization", token);
                return headers;
            }
        };

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }*/

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
                case "Father Name":
                    params.put("fatherName", fieldValueMap.get("fatherName"));
                    break;
                case "Mother Name":
                    params.put("motherName", fieldValueMap.get("motherName"));
                    break;
                case "Date of Birth":
                    params.put("dob", fieldValueMap.get("dob"));
                    break;
                case "Mobile":
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
                case "Admission No.":
                    params.put("admissionNo", fieldValueMap.get("admissionNo"));
                    break;
                case "Bus No.":
                    params.put("busNo", fieldValueMap.get("busNo"));
                    break;
                case "Blood Group":
                    params.put("bloodGroup", fieldValueMap.get("bloodGroup"));
                    break;
                case "Roll No.":
                    params.put("rollNo", fieldValueMap.get("rollNo"));
                    break;
                // Add cases for other required fields
            }
        }

        // Make sure all required fields are provided
        if (params.size() < requiredFields.split(",").length) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiEndpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AddStudent.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                        // will change later
                        textView.setText("Add Student");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Display error message from the server
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data);
                    Toast.makeText(AddStudent.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddStudent.this, "Error adding student", Toast.LENGTH_SHORT).show();
                }
                // Handle error response
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Pass parameters as form data
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
        Volley.newRequestQueue(this).add(stringRequest);
    }


    // Method to get the text value of an EditText field by label
    // Updated code


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
                fetchRequiredFields(idArray[position]);
            }
        });
    }

    // End of Update the drop down list

    // ------------------------------------------------------------------------------------------------------------------------

    // updated addField

    private void addField(String label, String hint, String fieldIdentifier) {
        // Create a new TextView dynamically for label
        TextView textView = new TextView(AddStudent.this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(label + "*");
        textView.setTextSize(16);
        textView.setPadding(10, 10, 10, 10);

        // Create a new EditText dynamically
        EditText editText = new EditText(AddStudent.this);
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

    // dynamic addfield function
    /*private void addField(String label, String hint) {
        // Create a new TextView dynamically for label
        TextView textView = new TextView(AddStudent.this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(label + "*");
        textView.setTextSize(16);
        textView.setPadding(10, 10, 10, 10);

        // Create a new EditText dynamically
        EditText editText = new EditText(AddStudent.this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setBackgroundResource(R.drawable.edittext_background);
        editText.setPadding(30, 30, 30, 30);
        editText.setHint(hint);

        // Add the TextView and EditText to the layout container
        dynamicLayout.addView(textView);
        dynamicLayout.addView(editText);
    }*/

    private void addUploadPhotoView() {
        // Create TextView for "Upload Photo"
        TextView uploadPhotoTextView = new TextView(AddStudent.this);
        uploadPhotoTextView.setText("Upload Photo");
        uploadPhotoTextView.setTextSize(18);
        LinearLayout.LayoutParams uploadPhotoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        uploadPhotoParams.setMargins(0, dpToPx(16), 0, 0); // Convert dp to pixels
        uploadPhotoTextView.setLayoutParams(uploadPhotoParams);

        // Create LinearLayout for "Choose File" button and "No file chosen" TextView
        LinearLayout fileChooseLayout = new LinearLayout(AddStudent.this);
        fileChooseLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams fileChooseLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        fileChooseLayoutParams.setMargins(0, dpToPx(10), 0, 0); // Convert dp to pixels
        fileChooseLayout.setLayoutParams(fileChooseLayoutParams);

        // Create "Choose File" button
        Button chooseFileButton = new Button(AddStudent.this);
        chooseFileButton.setText("Choose File");

        // Create "No file chosen" TextView
        TextView noFileChosenTextView = new TextView(AddStudent.this);
        noFileChosenTextView.setText("No file chosen");
        LinearLayout.LayoutParams noFileChosenParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        noFileChosenParams.setMargins(dpToPx(8), 0, 0, 0); // Convert dp to pixels
        noFileChosenTextView.setLayoutParams(noFileChosenParams);

        // Add views to the fileChooseLayout
        fileChooseLayout.addView(chooseFileButton);
        fileChooseLayout.addView(noFileChosenTextView);

        // Add views to the parent layout
        dynamicLayout.addView(uploadPhotoTextView);
        dynamicLayout.addView(fileChooseLayout);
    }

    private void addButtonsLayout() {
        // Create LinearLayout for buttons
        LinearLayout buttonLayout = new LinearLayout(AddStudent.this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonLayoutParams.setMargins(0, dpToPx(20), 0, dpToPx(20)); // Convert dp to pixels
        buttonLayout.setLayoutParams(buttonLayoutParams);
        buttonLayout.setGravity(Gravity.CENTER);


        // Create Save Button
        saveButton = new Button(AddStudent.this);
        saveButton.setText("Save");
        LinearLayout.LayoutParams saveButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        saveButtonParams.setMargins(dpToPx(10), 0, dpToPx(0), 0); // Set margins in pixels
        saveButton.setLayoutParams(saveButtonParams);
        saveButton.setBackground(getResources().getDrawable(R.drawable.button_background));
        saveButton.setPadding(dpToPx(10), dpToPx(5), dpToPx(10), dpToPx(5)); // Convert dp to pixels
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        buttonLayout.addView(saveButton);

        // Create Reset Button
        Button resetButton = new Button(AddStudent.this);
        resetButton.setText("Reset");
        LinearLayout.LayoutParams resetButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        resetButtonParams.setMargins(dpToPx(10), 0, dpToPx(0), 0); // Set margins in pixels
        resetButton.setLayoutParams(resetButtonParams);
        resetButton.setBackground(getResources().getDrawable(R.drawable.button_background));
        resetButton.setPadding(dpToPx(10), dpToPx(5), dpToPx(10), dpToPx(5)); // Convert dp to pixels
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Reset button click
                resetFields();
                // image and it's text is remaining
            }
        });
        buttonLayout.addView(resetButton);
        // Create Cancel Button

        Button cancelButton = new Button(AddStudent.this);
        cancelButton.setText("Cancel");
        LinearLayout.LayoutParams cancelButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cancelButtonParams.setMargins(dpToPx(10), 0, dpToPx(0), 0); // Set margins in pixels
        cancelButton.setLayoutParams(cancelButtonParams);
        cancelButton.setBackground(getResources().getDrawable(R.drawable.button_background));
        cancelButton.setPadding(dpToPx(10), dpToPx(5), dpToPx(10), dpToPx(5)); // Convert dp to pixels
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Cancel button click
                finish();
            }
        });
        buttonLayout.addView(cancelButton);

        // Add LinearLayout to the parent layout
        dynamicLayout.addView(buttonLayout);
    }

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
    // Method to convert dp to pixel
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    // Method to convert dp to pixel

    // -----------------------------------------------------------------------------------------------------------------------

    // Method to get the token and name saved in local storage
    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    private String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }
    // End of method to get the token and name saved in local storage

    // --------------------------------------------------------------------------------------------------------------------------

}