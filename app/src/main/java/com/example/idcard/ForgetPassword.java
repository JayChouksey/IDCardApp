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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetPassword extends AppCompatActivity {

    EditText email;
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        email = findViewById(R.id.editTextEmail);
        reset = findViewById(R.id.btnReset);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = email.getText().toString().trim();

                resetPassword(ForgetPassword.this, strEmail);
            }
        });

    }


    private void resetPassword(Context context, String email) {
        String url = "https://id-card-backend-2.onrender.com/user/forgetpassword/email";

        // Create a JSON object to hold the email
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating JSON request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response JSON
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");
                            String token = response.getString("Token");

                            if (success) {
                                // Show success message and save the token
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                // Save the token or pass it to the next activity
                                Intent intent = new Intent(ForgetPassword.this, ResetPassword.class);
                                intent.putExtra("resetToken",token);
                                startActivity(intent);
                            } else {
                                // Show error message
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
                });

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }

}