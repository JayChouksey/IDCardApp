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

    // String which stores the selected checkbox names
    String selectedNames;

    // Image  picker
    private Button btnChooseImage;
    ImageView imgChosen;
    TextView textNoFileChosen;
    private static final int REQUEST_CODE_IMAGE_PICKER = 101;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 102;
    File photoFile;
    Bitmap bitmap;
    private String filePath; // Stores the path of the selected image
    Uri imageUri;


    private final static int PICK_IMAGE_REQUEST = 1;

    private EditText editTextSchoolName, editTextMobileNo, editTextEmail, editTextPassword, editTextConfirmPassword, editTextAddress, editTextSchoolCode;
    private TextView addSchool;
    Button btnSave;

    /*    String [] status = {"Active","In-active"};
    AutoCompleteTextView autoCompleteStatus;
    ArrayAdapter<String> adapterStatus;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school);

        TextView userName = findViewById(R.id.userName);
        userName.setText(getUserName());

        addSchool = findViewById(R.id.textview_add_school);
        btnSave = findViewById(R.id.btn_save);



        // Code to select image from the gallery

            btnChooseImage = findViewById(R.id.btn_choose_file);
            textNoFileChosen = findViewById(R.id.text_no_file_chosen);
            imgChosen = findViewById(R.id.chosen_img);

            btnChooseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkStoragePermission()) {
                        chooseImageFromGallery();
                    } else {
                        requestStoragePermission();
                    }
                }
            });


        // End of Code to select image from the gallery


        editTextSchoolName = findViewById(R.id.editTextSchoolName);
        editTextMobileNo = findViewById(R.id.editTextMobileNo);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextSchoolCode = findViewById(R.id.editTextSchoolCode);

        // Checking the last screen of the app
        Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        String id = getSchoolId();

        if(from.equals("SchoolList")){
            addSchool.setText("Edit School");
            btnSave.setText("Edit");
            editTextSchoolName.setText(intent.getStringExtra("nameSchool"));
            editTextMobileNo.setText(intent.getStringExtra("contactSchool"));
            editTextAddress.setText((intent.getStringExtra("addressSchool")));
            editTextSchoolCode.setText((intent.getStringExtra("codeSchool")));
        }


        // Find the parent layout containing all the LinearLayouts with TextViews and CheckBoxes
        LinearLayout parentLayout = findViewById(R.id.LinearLayoutIdCardData);
        // Iterate over each child view of the parent layout
        for (int i = 1; i < parentLayout.getChildCount(); i++) { // starting from index 1 to skip the first TextView
            View view = parentLayout.getChildAt(i);

            // Check if the view is a LinearLayout containing a TextView and a CheckBox
            if (view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;

                // Find the CheckBox in the LinearLayout
                CheckBox checkBox = (CheckBox) linearLayout.getChildAt(1);

                // Add a listener to the CheckBox
                checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    // Update the selected names when the checkbox state changes
                    selectedNames = getSelectedNames(parentLayout);
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
                editTextSchoolCode.setText("");

                // Reset UI and variables
                textNoFileChosen.setText("No file chosen");
                imgChosen.setImageBitmap(null);
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
                String schoolCode = editTextSchoolCode.getText().toString().trim();


                if(from.equals("SchoolList")){
                    btnSave.setText("Updating...");
                    //sendDataToApi(schoolName, mobileNo, email, password, confirmPassword, address, schoolCode,id);

                   if (TextUtils.isEmpty(filePath)) {
                        Toast.makeText(AddSchool.this, "Please select an image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                   // new method try
                }
                else{
                    btnSave.setText("Saving...");
                    registerSchool(bitmap,schoolName, mobileNo, email, password, confirmPassword, address, schoolCode);
                    //sendDataToApi(schoolName, mobileNo, email, password, confirmPassword, address, schoolCode);
                }

            }
        });


        // Will be added later
        // Drop Down menu code
        /*{
            autoCompleteStatus = findViewById(R.id.status_dropdown);

            adapterStatus = new ArrayAdapter<String>(this,R.layout.list_item,status);
            autoCompleteStatus.setAdapter(adapterStatus);

            autoCompleteStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(getApplicationContext(),"Item: "+item,Toast.LENGTH_SHORT).show();
                }
            });
        }*/
        // End of drop down menu code


    }
    // Main function ends
    // -------------------------------------------------------------------------------------------------------------------


    // sending text data to api endpoint
    private void registerSchool(final Bitmap bitmap, String schoolName, String mobileNo, String email, String password, String confirmPassword,
                              String address, String schoolCode) {

        String URL = "https://id-card-backend-2.onrender.com/user/registration/school";
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), "School added successfully!", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(AddSchool.this, errorMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddSchool.this, "Error adding student", Toast.LENGTH_SHORT).show();
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
                params.put("code", schoolCode);
                params.put("password", password);
                params.put("requiredFields", selectedNames);
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken()); // Replace "YOUR_TOKEN_HERE" with your actual token
                return headers;
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data);
                    Toast.makeText(AddSchool.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddSchool.this, "Error adding student", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(AddSchool.this, "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if all required fields are filled
        if (schoolName.isEmpty() || mobileNo.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || address.isEmpty() || schoolCode.isEmpty()) {
            Toast.makeText(AddSchool.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    // End of sending text data to api endpoint

    // update school data

    // end of  update school data

    // -------------------------------------------------------------------------------------------------------------------------------


    // Image upload functions
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return permissionResult == PackageManager.PERMISSION_GRANTED;
        } else {
            return true; // Permission check not required for older versions
        }
    }


    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImageFromGallery();
            } else {
                Toast.makeText(this, "Storage permission required to access gallery", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void chooseImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER);
        startActivityForResult(intent, 100); // change 06/04/24 - 07:53 --> https://www.simplifiedcoding.net/upload-image-to-server/
    }


    // change 06/04/24 - 09:17 --> https://www.simplifiedcoding.net/upload-image-to-server/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                //displaying selected image to imageview
                imgChosen.setImageBitmap(bitmap);
                textNoFileChosen.setText("");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // End of Image upload functions

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