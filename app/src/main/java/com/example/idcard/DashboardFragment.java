package com.example.idcard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.slider.LabelFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    TextView textview_total_schools;
    TextView textview_total_student;
    TextView textview_total_staff;


    public DashboardFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Setting the name
        TextView userName = view.findViewById(R.id.userName);
        String name = getUserNameFromSharedPreferences();
        userName.setText(name);

        textview_total_schools = view.findViewById(R.id.textview_total_schools);
        textview_total_student = view.findViewById(R.id.textview_total_student);
        textview_total_staff = view.findViewById(R.id.textview_total_staff);

        fetchData(); // to fetch the data from the api


        return view;
    }
    // Main function ends

    private void fetchData() {
        // Get the token from SharedPreferences
        String token = getTokenFromSharedPreferences();

        // Construct the URL with query parameters
        String url = "https://id-card-backend-2.onrender.com/user/bar-chart";

        // Create a new request queue
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Create a new JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse the JSON response and store the data
                        try {
                            String totalSchool = Integer.toString(response.getInt("schoolCount"));
                            String totalStudent = Integer.toString(response.getInt("studentCount"));
                            String totalStaff = Integer.toString(response.getInt("staffCount"));

                            textview_total_schools.setText(totalSchool);
                            textview_total_student.setText(totalStudent);
                            textview_total_staff.setText(totalStaff);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add the token as a header
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        // Add the request to the queue
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