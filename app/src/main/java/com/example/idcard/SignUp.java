package com.example.idcard;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        {
            // Find references to EditText fields and Button
            EditText editTextFullName = findViewById(R.id.editTextFullName);
            EditText editTextCity = findViewById(R.id.editTextCity);
            EditText editTextDistrict = findViewById(R.id.editTextDistrict);
            EditText editTextState = findViewById(R.id.editTextState);
            EditText editTextEmailId = findViewById(R.id.editTextEmailId);
            EditText editTextFirmName = findViewById(R.id.editTextFirmName);
            EditText editTextPassword = findViewById(R.id.editTextPassword);
            EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
            EditText editTextWhatsApp = findViewById(R.id.editTextWhatsApp);

            Button registerButton = findViewById(R.id.btnSignUp);

            // Set OnClickListener to the registerButton
            registerButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
                @Override
                public void onClick(View v) {

                    registerButton.setText("Loading...");
                    // Get text from EditText fields
                    String fullName = editTextFullName.getText().toString().trim();
                    String city = editTextCity.getText().toString().trim();
                    String district = editTextDistrict.getText().toString().trim();
                    String state = editTextState.getText().toString().trim();
                    String emailId = editTextEmailId.getText().toString().trim();
                    String firmName = editTextFirmName.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                    String whatsApp = editTextWhatsApp.getText().toString().trim();

                    // Make sure fields are not empty
                    if (!fullName.isEmpty() && !city.isEmpty() && !district.isEmpty() && !state.isEmpty() && !emailId.isEmpty()
                            && !firmName.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && !whatsApp.isEmpty()) {
                        // Validate if the mobile number has exactly 10 digits
                        if (whatsApp.length() == 10) {
                            // Create a request body with URL-encoded form data
                            String requestBody = "name=" + URLEncoder.encode(fullName, StandardCharsets.UTF_8)
                                    + "&city=" + URLEncoder.encode(city, StandardCharsets.UTF_8)
                                    + "&district=" + URLEncoder.encode(district, StandardCharsets.UTF_8)
                                    + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8)
                                    + "&email=" + URLEncoder.encode(emailId, StandardCharsets.UTF_8)
                                    + "&companyName=" + URLEncoder.encode(firmName, StandardCharsets.UTF_8)
                                    + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8)
                                    + "&contact=" + URLEncoder.encode(whatsApp, StandardCharsets.UTF_8);

                            // Make POST request using Volley
                            String api = "https://id-card-backend-2.onrender.com/user/registration";
                            StringRequest request = new StringRequest(Request.Method.POST, api,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                // Handle response
                                                JSONObject jsonResponse = new JSONObject(response);
                                                String token = jsonResponse.getString("Token");

                                                // Start the EmailVerification activity and pass the token
                                                Intent intent = new Intent(SignUp.this, EmailVerification.class);
                                                intent.putExtra("token", token);
                                                startActivity(intent);
                                                Toast.makeText(SignUp.this, "OTP sent to your mail!", Toast.LENGTH_SHORT).show();
                                            } catch (JSONException e) {
                                                registerButton.setText("Sign Up");
                                                e.printStackTrace();
                                                // Unexpected response format, show an error message
                                                Toast.makeText(SignUp.this, "Unexpected response. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Handle error if needed
                                    // For example, show an error message
                                    registerButton.setText("Sign Up");
                                    Toast.makeText(SignUp.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
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
                            Volley.newRequestQueue(SignUp.this).add(request);
                        } else {
                            registerButton.setText("Sign Up");
                            // If mobile number does not have 10 digits, show a message to the user
                            Toast.makeText(SignUp.this, "Please enter a 10-digit mobile number", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        registerButton.setText("Sign Up");
                        // If any field is empty, show a message to the user
                        Toast.makeText(SignUp.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

    }
}