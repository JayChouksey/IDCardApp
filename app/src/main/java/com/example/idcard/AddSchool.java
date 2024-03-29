package com.example.idcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

// Import statements (assuming Volley library is imported)


public class AddSchool extends AppCompatActivity {

    // String which stores the selected checkbox names
    String selectedNames;

    // Image  picker
    private Button btnChooseImage;
    private static final String ROOT_URL = "https://id-card-backend-2.onrender.com/user/registration/school";
    private static final int REQUEST_PERMISSIONS = 100;
    // private static final int PICK_IMAGE_REQUEST =1 ;

    ImageView imgChosen;
    TextView textNoFileChosen;
    // Gemini
    private String imagePath;
    private final static int PICK_IMAGE_REQUEST = 1;

    // Replace with your base URL and API endpoint
    private static final String BASE_URL = "https://id-card-backend-2.onrender.com/";
    private static final String API_ENDPOINT = "user/registration/school";

    private EditText editTextSchoolName, editTextMobileNo, editTextEmail, editTextPassword, editTextConfirmPassword, editTextAddress, editTextSchoolCode;

    /*    String [] status = {"Active","In-active"};
    AutoCompleteTextView autoCompleteStatus;
    ArrayAdapter<String> adapterStatus;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school);

        TextView userName = findViewById(R.id.userName);
        userName.setText(getUserName());

        // Code to select image from the gallery

            btnChooseImage = findViewById(R.id.btn_choose_file);
            textNoFileChosen = findViewById(R.id.text_no_file_chosen);
            imgChosen = findViewById(R.id.chosen_img);

            btnChooseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
    /*                // Open file picker dialog to select images
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*"); // Set MIME type to image/* for image files only
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST_CODE);*/

                    // Gemini
                    openFileChooser();


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
                imagePath = null;
            }
        });
        // End of Cancel button functionality
        // ---------------------------------------------------------------------------------------------------------------------


        Button btnSave = findViewById(R.id.btn_save);
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

               sendDataToApi(schoolName, mobileNo, email, password, confirmPassword, address, schoolCode);


                // Gemini
                if (imagePath != null) {
                    sendImageToServer();
                } else {
                    // Handle case where no image is chosen
                    Toast.makeText(getApplicationContext(), "Select Image first", Toast.LENGTH_LONG).show();
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
    private void sendDataToApi(String schoolName, String mobileNo, String email, String password, String confirmPassword,
                               String address, String schoolCode) {
        String apiEndpoint = "https://id-card-backend-2.onrender.com/user/registration/school";
        String token = getToken(); // token, saved locally

        if(!schoolName.isEmpty() && !mobileNo.isEmpty() && !email.isEmpty() && !password.isEmpty() &&
        !confirmPassword.isEmpty() && !address.isEmpty() && !schoolCode.isEmpty()){

            if(password.equals(confirmPassword)){
                StringRequest stringRequest = new StringRequest(Request.Method.POST, apiEndpoint,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Handle response from the server
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    // Parse response JSON if needed
                                    Toast.makeText(AddSchool.this, "Data sent successfully", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(AddSchool.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(AddSchool.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("name", schoolName);
                        params.put("contact", mobileNo);
                        params.put("email", email);
                        params.put("password", password);
                        params.put("address", address);
                        params.put("code", schoolCode);
                        params.put("requiredFields", selectedNames);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", token); // Replace "YOUR_TOKEN_HERE" with your actual token
                        return headers;
                    }
                };

                Volley.newRequestQueue(this).add(stringRequest);
            }else{
                Toast.makeText(AddSchool.this, "Password does not match", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(AddSchool.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
        }

    }


    // End of sending text data to api endpoint



    // -------------------------------------------------------------------------------------------------------------------------------

    // Image upload functions

    // Gemini

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            imagePath = getRealPathFromURI(filePath); // Get actual path from URI

            // Display selected image on UI (optional)
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgChosen.setImageBitmap(bitmap);
                textNoFileChosen.setText(filePath.getPath().split(":")[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void sendImageToServer() {
        // Create Gson object for potential additional data (optional)
        Gson gson = new GsonBuilder().setLenient().create();

        // Retrofit interface definition with a method for uploading images
        abstract class UploadService {
            @Multipart
            @POST(API_ENDPOINT)
            public abstract Call<ResponseBody> uploadImage(@Header("Authorization") String token, @Part MultipartBody.Part image);
        }

        // Build Retrofit client
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // Create UploadService instance
        UploadService service = retrofit.create(UploadService.class);

        // Prepare image data for multipart request
        File file = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // Replace "YOUR_TOKEN" with your actual authorization token
        String token = getToken();

        // Send image upload request with authorization header
        Call<ResponseBody> call = service.uploadImage(token, image);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle successful image upload response
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    // (e.g., show a toast message)
                } else {
                    // Handle error during upload
                    // (e.g., show an error message)
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network error
                // (e.g., show a network error message)
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }




    // Gemini


/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                try {
                    Uri imageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    imgChosen.setImageBitmap(bitmap);
                    textNoFileChosen.setVisibility(View.GONE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
*/

    /*private void uploadDataToServer() {

        StringRequest request = new StringRequest(Request.Method.POST, ROOT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("upload", encodeImageToString(bitmap));
                return map;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Add your token here
                headers.put("Authorization", getToken());
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private String encodeImageToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }*/

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    // Handle the selected image
                    String imagePath = imageUri.getPath(); // Get the image path
                    imgFile = new File(imagePath);

                    // Update the TextView with the image name
                    textNoImageChosen.setText(imgFile.getName());
                }
            }
        }
    }

    // Method to get the real path of the image from its URI
    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }*/

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
    // End of method to get the token saved in local storage

    // --------------------------------------------------------------------------------------------------------------------------
}