package com.example.idcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Login extends AppCompatActivity {

    private TextView textViewSignup, textViewFrogetPassword;
    Button loginButton;

    Boolean distributorLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Check if the user is already logged in
        if (isLoggedIn()) {
            // Navigate to ProfileActivity
            Intent intent = new Intent(Login.this,MainActivity.class);
            String token = getToken();
            intent.putExtra("token",token);
            startActivity(intent);
            finish(); // Close MainActivity
        }

        // ------------------------------------------------------------------------------------------------------------------

        // Login Control for Student and Distributor
        SwitchCompat switchLogin = findViewById(R.id.switchLogin);
        TextView textViewLoginMode = findViewById(R.id.textViewLoginMode);

        switchLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    distributorLogin = false;
                    textViewLoginMode.setText("School Login");
                    textViewSignup.setVisibility(View.GONE);
                    textViewFrogetPassword.setVisibility(View.GONE);
                } else {
                    distributorLogin = true;
                    textViewLoginMode.setText("Distributor Login");
                    textViewSignup.setVisibility(View.VISIBLE);
                    textViewFrogetPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        // Activity Transitions
        {
            textViewSignup = findViewById(R.id.textview_signup);
            textViewFrogetPassword = findViewById(R.id.textview_forget_password);

            textViewSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Login.this, SignUp.class);
                    startActivity(intent);
                }
            });

            textViewFrogetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Login.this, ForgetPassword.class);
                    startActivity(intent);
                }
            });
        }
        // End of Activity Transitions
        // -------------------------------------------------------------------------------------------------------------------




        // -------------------------------------------------------------------------------------------------------------------
        // Login Button Functionality
        {
            // Find references to EditText fields and Button
            EditText editTextLoginID = findViewById(R.id.editTextEmail);
            EditText editTextPassword = findViewById(R.id.editTextPassword);
            loginButton = findViewById(R.id.btnLogin);

            // Set OnClickListener to the loginButton
            loginButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
                @Override
                public void onClick(View v) {
                    // Get text from EditText fields
                    String loginID = editTextLoginID.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    loginButton.setText("Loading...");

                    if(distributorLogin){
                        if (!loginID.isEmpty() && !password.isEmpty()) {
                            // Create a request body with URL-encoded form data
                            String requestBody = "email=" + URLEncoder.encode(loginID, StandardCharsets.UTF_8)
                                    + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);

                            // Make POST request using Volley
                            String api = "https://id-card-backend-2.onrender.com/user/login"; // login API endpoint
                            StringRequest request = new StringRequest(Request.Method.POST, api,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            // Handle response
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                String token = jsonResponse.getString("token");

                                                // Extract user object
                                                JSONObject userObject = jsonResponse.getJSONObject("user");
                                                // Extract name from user object
                                                String name = userObject.getString("name");

                                                // Save login status
                                                saveLoginStatus(true);
                                                // Save token
                                                saveAuthToken(token);
                                                saveUserName(name);

                                                // Start the Profile activity
                                                Intent intent = new Intent(Login.this, MainActivity.class);
                                                intent.putExtra("token", token);
                                                startActivity(intent);

                                            } catch (JSONException e) {
                                                loginButton.setText("Login");
                                                e.printStackTrace();
                                                // Unexpected response format, show an error message
                                                Toast.makeText(Login.this, "Unexpected response. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                            loginButton.setText("Login");
                                            // Handle error
                                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                                String errorMessage = new String(error.networkResponse.data);
                                                if (errorMessage.contains("Wrong Credientials")) {
                                                    // Password doesn't match, display toast message
                                                    Toast.makeText(Login.this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                                                } else if (errorMessage.contains("User Not Found")) {
                                                    // User not found, display toast message
                                                    Toast.makeText(Login.this, "User not found. Please check your credentials.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Other errors, show a generic error message
                                                    Toast.makeText(Login.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                // No response from server, show a generic error message
                                                Toast.makeText(Login.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }) {
                                @Override
                                public byte[] getBody() {
                                    return requestBody.getBytes(StandardCharsets.UTF_8);
                                }

                                @Override
                                public String getBodyContentType() {
                                    return "application/x-www-form-urlencoded";
                                }
                            };

                            // Add request to Volley request queue
                            Volley.newRequestQueue(Login.this).add(request);
                        } else {
                            loginButton.setText("Login");
                            // If any field is empty, show a message to the user
                            Toast.makeText(Login.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Intent intent = new Intent(Login.this, SchoolHome.class);
                        startActivity(intent);
                    }

                    // Make sure fields are not empty

                }
            });


        }
        // End of Login Button Functionality
        // -------------------------------------------------------------------------------------------------------------------

    }

    // ---------------------------------------------------------------------------------------------------------------
    // Code for Session Management

    // Method to save login status
    public void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    // Method to check if the user is already logged in
    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void saveAuthToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }
    private void saveUserName(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.apply();
    }

    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    @Override
    public void onBackPressed() {
        // Empty
    }

    // End of  Code for not logging again and again

    // ---------------------------------------------------------------------------------------------------------------------
}