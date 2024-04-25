package com.example.idcard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.content.CursorLoader;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.idcard.api.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import android.Manifest;

public class ImportStudents extends AppCompatActivity {

    // Drop Down
    List<String> schoolNames = new ArrayList<>(); // List to store school names
    List<String> schoolId = new ArrayList<>();
    AutoCompleteTextView autoCompleteSchool, autoCompleteRole;
    ArrayAdapter<String> adapterSchool, adapterRole;
    String [] role = {"Student", "Staff"};
    String strRole = "";


    // Image  picker
    private TextView noFileChosen, textNoImagesChosen;
    private String filePath; // To store the selected Excel file path
    byte[] fileData;
    private static final int REQUEST_CODE_IMAGE_PICKER = 200; // for image
    private ArrayList<Uri> selectedImageUris; // selected images uri
    private  ArrayList<Bitmap> selectedImagesBitmap; // selected images bitmap
    private ArrayList<String> imageNames;
    private static final int REQUEST_CODE_FILE_PICKER = 2; // for excel

    String idSchool; // to store the id of school from drop down
    Button uploadExcel, uploadImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_students);

        // Setting the distributor name
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

        selectedImageUris = new ArrayList<>();
        selectedImagesBitmap = new ArrayList<>();
        imageNames = new ArrayList<>();

        // School Dropdown
        autoCompleteSchool = findViewById(R.id.school_dropdown);
        fetchUserData();


        noFileChosen = findViewById(R.id.text_no_file_chosen);
        textNoImagesChosen = findViewById(R.id.text_no_images_chosen);

        uploadExcel = findViewById(R.id.buttonAddStudent);
        uploadImages = findViewById(R.id.buttonUploadImages);

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
                        uploadExcel.setText("Upload Student Excel");
                        uploadImages.setText("Upload Student Images");
                        break;
                    case 1:
                        strRole = "Staff";
                        uploadExcel.setText("Upload Staff Excel");
                        uploadImages.setText("Upload Staff Images");
                        break;
                }
            }
        });


        // Set click listener for the "Choose File" button
        Button chooseFileButton = findViewById(R.id.btn_choose_file);
        Button chooseImageButton = findViewById(R.id.btn_choose_images);

        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImagesFromGallery();
            }
        });

        uploadExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(strRole.equals("")){
                    Toast.makeText(ImportStudents.this, "Select Role First", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadExcel.setText("Uploading...");

                if(strRole.equals("Student")){
                    uploadExcelFileStudent(fileData);
                }
                else{
                    uploadExcelFileStaff(fileData);
                }
            }
        });

        uploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImages.setText("Uploading...");

                if(strRole.equals("Student")){
                    uploadPhotosStudents(selectedImagesBitmap, imageNames);
                }
                else{
                    uploadPhotosStaff(selectedImagesBitmap, imageNames);
                }

            }
        });


    }
    // Main function ends

    // ---------------------------------------------------------------------------------------------------------------------

    // Excel upload functions
    private void uploadExcelFileStudent(byte[] excelFileData) {

        String URL = "https://id-card-backend-2.onrender.com/upload-excel/" + idSchool;

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            if(strRole.equals("Student")){
                                uploadExcel.setText("Upload Student Excel");
                            }
                            else{
                                uploadExcel.setText("Upload Staff Excel");
                            }
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), "Excel file uploaded successfully!", Toast.LENGTH_SHORT).show();
                            noFileChosen.setText("No file chosen");
                        } catch (JSONException e) {
                            if(strRole.equals("Student")){
                                uploadExcel.setText("Upload Student Excel");
                            }
                            else{
                                uploadExcel.setText("Upload Staff Excel");
                            }
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(strRole.equals("Student")){
                            uploadExcel.setText("Upload Student Excel");
                        }
                        else{
                            uploadExcel.setText("Upload Staff Excel");
                        }
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(ImportStudents.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ImportStudents.this, "Error uploading Excel file", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {

            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long excelName = System.currentTimeMillis();
                params.put("file", new DataPart(excelName + ".xlsx", excelFileData));
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
                if(strRole.equals("Student")){
                    uploadExcel.setText("Upload Student Excel");
                }
                else{
                    uploadExcel.setText("Upload Staff Excel");
                }
                super.deliverError(error);
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data);
                    Toast.makeText(ImportStudents.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImportStudents.this, "Error uploading Excel file", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Add the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private void uploadExcelFileStaff(byte[] excelFileData) {

        String URL = "https://id-card-backend-2.onrender.com/upload-excel/staff/" + idSchool;

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            if(strRole.equals("Student")){
                                uploadExcel.setText("Upload Student Excel");
                            }
                            else{
                                uploadExcel.setText("Upload Staff Excel");
                            }
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), "Excel file uploaded successfully!", Toast.LENGTH_SHORT).show();
                            noFileChosen.setText("No file chosen");
                        } catch (JSONException e) {
                            if(strRole.equals("Student")){
                                uploadExcel.setText("Upload Student Excel");
                            }
                            else {
                                uploadExcel.setText("Upload Staff Excel");
                            }
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(strRole.equals("Student")){
                            uploadExcel.setText("Upload Student Excel");
                        }
                        else {
                            uploadExcel.setText("Upload Staff Excel");
                        }
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(ImportStudents.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ImportStudents.this, "Error uploading Excel file", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {

            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long excelName = System.currentTimeMillis();
                params.put("file", new DataPart(excelName + ".xlsx", excelFileData));
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
                if(strRole.equals("Student")){
                    uploadExcel.setText("Upload Student Excel");
                }
                else {
                    uploadExcel.setText("Upload Staff Excel");
                }
                super.deliverError(error);
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data);
                    Toast.makeText(ImportStudents.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImportStudents.this, "Error uploading Excel file", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Add the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    // End of upload excel functions

    // ---------------------------------------------------------------------------------------------------------------------

    // Multiple image upload functions
    private void uploadPhotosStudents(final ArrayList<Bitmap> bitmaps, final ArrayList<String> imageNames) {
        String URL = "https://id-card-backend-2.onrender.com/user/student/avatars/" + idSchool;

        // Initialize a counter to keep track of the number of images uploaded
        final AtomicInteger imageUploadCount = new AtomicInteger(0);

        // Initialize a list to store the response messages for each image upload
        final ArrayList<String> responseMessages = new ArrayList<>();

        // Iterate through each bitmap and upload it
        for (int i = 0; i < bitmaps.size(); i++) {
            final Bitmap bitmap = bitmaps.get(i);
            final String imageName = imageNames.get(i);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            try {
                                if(strRole.equals("Student")){
                                    uploadImages.setText("Upload Student Images");
                                } else {
                                    uploadImages.setText("Upload Staff Images");
                                }
                                JSONObject obj = new JSONObject(new String(response.data));
                                responseMessages.add("Image uploaded successfully: " + obj.getString("message"));
                                textNoImagesChosen.setText("No images chosen");
                            } catch (JSONException e) {
                                if(strRole.equals("Student")){
                                    uploadImages.setText("Upload Student Images");
                                } else {
                                    uploadImages.setText("Upload Staff Images");
                                }
                                e.printStackTrace();
                                responseMessages.add("Error uploading image");
                            }

                            // Increment the image upload count
                            imageUploadCount.incrementAndGet();

                            // Check if all images are uploaded
                            if (imageUploadCount.get() == bitmaps.size()) {
                                // All images are uploaded, show response messages
                                for (String message : responseMessages) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(strRole.equals("Student")){
                                uploadImages.setText("Upload Student Images");
                            } else {
                                uploadImages.setText("Upload Staff Images");
                            }
                            String errorMessage = "Error uploading image";
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                errorMessage = new String(error.networkResponse.data);
                            }
                            responseMessages.add(errorMessage);

                            // Increment the image upload count
                            imageUploadCount.incrementAndGet();

                            // Check if all images are uploaded
                            if (imageUploadCount.get() == bitmaps.size()) {
                                // All images are uploaded, show response messages
                                for (String message : responseMessages) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }) {
                @Override
                public Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    // Use imageName as the filename
                    params.put("file", new DataPart(imageName, getFileDataFromDrawable(bitmap)));
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", getToken());
                    return headers;
                }
            };

            // Add the request to volley
            Volley.newRequestQueue(this).add(volleyMultipartRequest);
        }
    }


    private void uploadPhotosStaff(final ArrayList<Bitmap> bitmaps, final ArrayList<String> imageNames) {
        String URL = "https://id-card-backend-2.onrender.com/user/staff/avatars/" + idSchool;

        // Initialize a counter to keep track of the number of images uploaded
        final AtomicInteger imageUploadCount = new AtomicInteger(0);

        // Initialize a list to store the response messages for each image upload
        final ArrayList<String> responseMessages = new ArrayList<>();

        // Iterate through each bitmap and upload it
        for (int i = 0; i < bitmaps.size(); i++) {
            final Bitmap bitmap = bitmaps.get(i);
            final String imageName = imageNames.get(i);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            try {
                                if(strRole.equals("Student")){
                                    uploadImages.setText("Upload Student Images");
                                } else {
                                    uploadImages.setText("Upload Staff Images");
                                }
                                JSONObject obj = new JSONObject(new String(response.data));
                                responseMessages.add("Image uploaded successfully: " + obj.getString("message"));
                                textNoImagesChosen.setText("No images chosen");
                            } catch (JSONException e) {
                                if(strRole.equals("Student")){
                                    uploadImages.setText("Upload Student Images");
                                } else {
                                    uploadImages.setText("Upload Staff Images");
                                }
                                e.printStackTrace();
                                responseMessages.add("Error uploading image");
                            }

                            // Increment the image upload count
                            imageUploadCount.incrementAndGet();

                            // Check if all images are uploaded
                            if (imageUploadCount.get() == bitmaps.size()) {
                                // All images are uploaded, show response messages
                                for (String message : responseMessages) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(strRole.equals("Student")){
                                uploadImages.setText("Upload Student Images");
                            } else {
                                uploadImages.setText("Upload Staff Images");
                            }
                            String errorMessage = "Error uploading image";
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                errorMessage = new String(error.networkResponse.data);
                            }
                            responseMessages.add(errorMessage);

                            // Increment the image upload count
                            imageUploadCount.incrementAndGet();

                            // Check if all images are uploaded
                            if (imageUploadCount.get() == bitmaps.size()) {
                                // All images are uploaded, show response messages
                                for (String message : responseMessages) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }) {
                @Override
                public Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    // Use imageName as the filename
                    params.put("file", new DataPart(imageName, getFileDataFromDrawable(bitmap)));
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", getToken());
                    return headers;
                }
            };

            // Add the request to volley
            Volley.newRequestQueue(this).add(volleyMultipartRequest);
        }
    }

    // End of Multiple image upload functions
    // ---------------------------------------------------------------------------------------------------------------------

    // Image and Excel Upload Helper Functions
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Set MIME type to allow any file type
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Choose File"), REQUEST_CODE_FILE_PICKER);
    }

    private void chooseImagesFromGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), REQUEST_CODE_IMAGE_PICKER);
    }

    // onActivityResult to handle Excel and Image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // for excel file
        if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == RESULT_OK) {
            // Check if a file was selected
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();

                // Get the file path (be cautious about security implications)
                filePath = getRealPathFromUri(uri); // Implement getRealPathFromUri function
                fileData = getFileDataFromExcelFile(uri);
                noFileChosen.setText("Excel File Selected");

            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
        // for images
        else if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK) {
            // Handle image selection
            if (data != null && data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();

                    String imageName = getFileNameFromUri(imageUri);
                    imageNames.add(imageName); // Store the filename

                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    selectedImageUris.add(imageUri);
                    selectedImagesBitmap.add(bitmap);
                }
                updateImageUI();
            }
        }
    }
    private void updateImageUI() {
        if (selectedImageUris.isEmpty()) {
            textNoImagesChosen.setVisibility(View.VISIBLE);
        } else {
            textNoImagesChosen.setText("Images Selected");
        }
    }

    // Extracting path for Excel
    private String getRealPathFromUri(Uri uri) {
        String path = null;
        if (uri.getPath().startsWith("/storage")) {
            // Might be a direct file path, handle with caution
            path = uri.getPath();
        } else {
            // Try using ContentResolver for providers like MediaStore
            CursorLoader loader = new CursorLoader(this, uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            Cursor cursor = loader.loadInBackground();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int pathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    path = cursor.getString(pathIndex);
                }
                cursor.close();
            }
        }
        return path;
    }


    // Method to read excel file into byte array
    public byte[] getFileDataFromExcelFile(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to get filename from Uri
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex != -1) {
                        result = cursor.getString(displayNameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }


    // End of Image and Excel Upload Helper Functions

    // ---------------------------------------------------------------------------------------------------------------------


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
                idSchool = idArray[position];
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