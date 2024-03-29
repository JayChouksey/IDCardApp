package com.example.idcard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class ProfileFragment extends Fragment {


    private TextView nameTextView, contactTextView, emailTextView, districtTextView, cityTextView, companyNameTextView, stateTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Setting the name
        TextView userName = view.findViewById(R.id.userName);
        String name = getUserNameFromSharedPreferences();
        userName.setText(name);

        // Initialize TextViews
        nameTextView = view.findViewById(R.id.nameTextView);
        contactTextView = view.findViewById(R.id.contactTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        districtTextView = view.findViewById(R.id.districtTextView);
        cityTextView = view.findViewById(R.id.cityTextView);
        stateTextView = view.findViewById(R.id.stateTextView);
        companyNameTextView = view.findViewById(R.id.companyNameTextView);

        // Fetch profile data from API
        fetchProfileData();

        return view;
    }

    // Function to fetch profile data
    private void fetchProfileData() {
        String url = "https://id-card-backend-2.onrender.com/user/profile";

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Create headers for the request
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization",getTokenFromSharedPreferences());

        // Request a JSON response from the provided URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject user = response.getJSONObject("user");

                            // Extract required fields
                            String name = user.getString("name");
                            String contact = user.getString("contact");
                            String email = user.getString("email");
                            String district = user.getString("district");
                            String city = user.getString("city");
                            String state = user.getString("state");
                            String companyName = user.getString("companyName");

                            // Set data to TextViews
                            nameTextView.setText(name);
                            contactTextView.setText(contact);
                            emailTextView.setText(email);
                            districtTextView.setText(district);
                            cityTextView.setText(city);
                            stateTextView.setText(state);
                            companyNameTextView.setText(companyName);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(requireContext(), "Error fetching profile data", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }



    private String getUserNameFromSharedPreferences() {
        // Obtain the SharedPreferences object from the Activity's context
        Context context = requireActivity(); // or getContext() depending on your Fragment's version
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Retrieve the name from SharedPreferences
        return sharedPreferences.getString("name", "");
    }

    private String getTokenFromSharedPreferences() {
        // Obtain the SharedPreferences object from the Activity's context
        Context context = requireActivity(); // or getContext() depending on your Fragment's version
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Retrieve the name from SharedPreferences
        return sharedPreferences.getString("token", "");
    }
}