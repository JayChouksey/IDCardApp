package com.example.idcard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);





        // Find references to EditText fields
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        EditText editTextDigit1 = findViewById(R.id.editTextDigit1);
        EditText editTextDigit2 = findViewById(R.id.editTextDigit2);
        EditText editTextDigit3 = findViewById(R.id.editTextDigit3);
        EditText editTextDigit4 = findViewById(R.id.editTextDigit4);

        Button reset = findViewById(R.id.btnReset);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                String otp = editTextDigit1.getText().toString().trim() +
                        editTextDigit2.getText().toString().trim() +
                        editTextDigit3.getText().toString().trim() +
                        editTextDigit4.getText().toString().trim();

                Intent intent = getIntent();
                String token = intent.getStringExtra("resetToken");

                resetPassword(ResetPassword.this, token, otp, password, confirmPassword);

            }
        });

    }

    private void resetPassword(Context context, String token, String otp, String password, String confirmPassword) {
        String url = "https://id-card-backend-2.onrender.com/user/forgetpassword/code";

        // Create a JSON object to hold the request parameters
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("activationCode", otp);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating JSON request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request queue
        RequestQueue queue = Volley.newRequestQueue(context);

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            if (success) {
                                // Password reset successful
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                // Navigate to login activity
                                Intent intent = new Intent(context, ResetPassword.class);
                                startActivity(intent);
                            } else {
                                // Password reset failed
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context, "Error resetting password", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add the authorization token to the headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }
}