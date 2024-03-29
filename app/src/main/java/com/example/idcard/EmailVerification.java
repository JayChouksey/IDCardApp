package com.example.idcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class EmailVerification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // ------------------------------------------------------------------------------------------------------------------
        // Email Verification Code

        // Find references to EditText fields
        EditText editTextDigit1 = findViewById(R.id.editTextDigit1);
        EditText editTextDigit2 = findViewById(R.id.editTextDigit2);
        EditText editTextDigit3 = findViewById(R.id.editTextDigit3);
        EditText editTextDigit4 = findViewById(R.id.editTextDigit4);


        Button buttonVerify = findViewById(R.id.buttonVerify);

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Concatenate each digit to form the OTP
                String otp = editTextDigit1.getText().toString().trim() +
                        editTextDigit2.getText().toString().trim() +
                        editTextDigit3.getText().toString().trim() +
                        editTextDigit4.getText().toString().trim();

                // Make sure OTP is not empty
                if (!otp.isEmpty()) {
                    // Get the token from the previous activity
                    String token = getIntent().getStringExtra("token");
                    // Create a request body with URL-encoded form data
                    String requestBody = "activationCode=" + URLEncoder.encode(otp, StandardCharsets.UTF_8);

                    // Make POST request using Volley
                    String api = "https://id-card-backend-2.onrender.com/user/activate/user";
                    StringRequest request = new StringRequest(Request.Method.POST, api,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Handle response if needed
                                    Toast.makeText(EmailVerification.this, "Email verification successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EmailVerification.this, Login.class);
                                    startActivity(intent);
                                    finish(); // Finish the current activity to prevent going back
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                String errorMessage = new String(error.networkResponse.data);
                                Toast.makeText(EmailVerification.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EmailVerification.this, "Error adding student", Toast.LENGTH_SHORT).show();
                            }
                            /*if (error.networkResponse != null && error.networkResponse.data != null) {
                                String errorMessage = new String(error.networkResponse.data);
                                if (errorMessage.contains("Wrong Activation Code")) {
                                    // Password doesn't match, display toast message
                                    Toast.makeText(EmailVerification.this, "Wrong Activation Code. Please try again.", Toast.LENGTH_SHORT).show();
                                } else if (errorMessage.contains("jwt expired")) {
                                    // User not found, display toast message
                                    Toast.makeText(EmailVerification.this, "OTP Expired. Please Go back and register again.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Other errors, show a generic error message
                                    Toast.makeText(EmailVerification.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // No response from server, show a generic error message
                                Toast.makeText(EmailVerification.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }*/
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

                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", token);
                            return headers;
                        }
                    };

                    // Add request to Volley request queue
                    Volley.newRequestQueue(EmailVerification.this).add(request);
                } else {
                    // If OTP field is empty, show a message to the user
                    Toast.makeText(EmailVerification.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // ---------------------------------------------------------------------------------------------------------------------

    }
}