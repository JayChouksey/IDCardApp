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

    // For dropdown
    String[] months =  {"01","02","03","04","05", "06", "07", "08", "09", "10", "11", "12"};
    String[] years = {"2024","2025","2026","2027","2028"};
    AutoCompleteTextView autoCompleteMonth, autoCompleteYear;
    ArrayAdapter<String> adapterMonth, adapterYear;


    // For graphs
    // variable for our bar chart
    BarChart barChart;
    // Variables to store data from API response

    private int[] schoolData;
    private int[] studentData;
    TextView textview_total_schools;
    TextView textview_total_student;

    TextView textviewTotalSchools;
    TextView textviewTotalStudents;

    String month = "1", year = "2024";


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
        textviewTotalStudents = view.findViewById(R.id.textviewTotalStudents);
        textviewTotalSchools = view.findViewById(R.id.textviewTotalSchools);

        fetchData(); // to fetch the data from the api

        // initializing variable for bar chart.
        barChart = view.findViewById(R.id.idBarChart);



        // spinner option code
        {
            autoCompleteMonth = view.findViewById(R.id.month_dropdown);
            adapterMonth = new ArrayAdapter<>(requireContext(), R.layout.list_item, months);
            autoCompleteMonth.setAdapter(adapterMonth);

            autoCompleteMonth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //String item = parent.getItemAtPosition(position).toString();
                    // Toast.makeText(requireContext(), "Item: " + item, Toast.LENGTH_SHORT).show();
                    month = parent.getItemAtPosition(position).toString();
                    fetchDataGraph(month, year);
                }
            });


            autoCompleteYear = view.findViewById(R.id.year_dropdown);
            adapterYear = new ArrayAdapter<>(requireContext(), R.layout.list_item, years);
            autoCompleteYear.setAdapter(adapterYear);

            autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // String item = parent.getItemAtPosition(position).toString();
                    // Toast.makeText(requireContext(), "Item: " + item, Toast.LENGTH_SHORT).show();
                    year = parent.getItemAtPosition(position).toString();
                    fetchDataGraph(month,year);
                }
            });



        }

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
                            JSONArray schoolArray = response.getJSONArray("school");
                            JSONArray studentArray = response.getJSONArray("student");
                            String totalSchool = Integer.toString(response.getInt("schoolCount"));
                            String totalStudent = Integer.toString(response.getInt("studentCount"));

                            textview_total_schools.setText(totalSchool);
                            textview_total_student.setText(totalStudent);

                            // Store data in member variables
                            schoolData = new int[schoolArray.length()];
                            studentData = new int[studentArray.length()];

                            int schoolCount = 0;
                            int studentCount = 0;

                            for (int i = 0; i < schoolArray.length(); i++) {
                                schoolData[i] = schoolArray.getInt(i);
                                schoolCount += schoolArray.getInt(i);
                            }
                            textviewTotalSchools.setText(Integer.toString(schoolCount));

                            for (int i = 0; i < studentArray.length(); i++) {
                                studentData[i] = studentArray.getInt(i);
                                studentCount += studentArray.getInt(i);
                            }
                            textviewTotalStudents.setText(Integer.toString(studentCount));

                            // Data is fetched, you can now use it as needed
                            // For example, update UI components with the fetched data
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

    private void fetchDataGraph(String month, String year) {
        // Get the token from SharedPreferences
        String token = getTokenFromSharedPreferences();

        // Construct the URL with query parameters
        String url = "https://id-card-backend-2.onrender.com/user/bar-chart?month="+ month + "&year=" + year;

        // Create a new request queue
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Create a new JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse the JSON response and store the data
                        try {
                            JSONArray schoolArray = response.getJSONArray("school");
                            JSONArray studentArray = response.getJSONArray("student");

                            // Store data in member variables
                            schoolData = new int[schoolArray.length()];
                            studentData = new int[studentArray.length()];


                            for (int i = 0; i < schoolArray.length(); i++) {
                                schoolData[i] = schoolArray.getInt(i);
                            }


                            for (int i = 0; i < studentArray.length(); i++) {
                                studentData[i] = studentArray.getInt(i);
                            }


                            // Define the size of the spikes array
                            int[][] spikes = new int[schoolArray.length()][2];

                            // Populate the spikes array with pairs of school and student counts
                            for (int i = 0; i < schoolArray.length(); i++) {
                                int schoolCount = schoolArray.getInt(i);
                                int studentCount = studentArray.getInt(i);
                                spikes[i] = new int[]{schoolCount, studentCount};
                            }
                            setBarChart(spikes);

                            // Data is fetched, you can now use it as needed
                            // For example, update UI components with the fetched data
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

    private void setBarChart(int [][] spikes){
        // Define week labels
        String[] weekLabels = {"Week1", "Week2", "Week3", "Week4"};

        // Create an ArrayList to hold BarEntry objects
        ArrayList<BarEntry> barEntriesArrayList = new ArrayList<>();

        // Define the spikes for each week
        //float[][] spikes = {{2, 1}, {1, 2}, {2, 3}, {3, 4}};

        // Set the width of each spike pair
        float width = 0.2f; // Adjust as needed
        float xOffset = 0.25f; // Adjust as needed
        float distanceBetweenPairs = 1.5f; // Adjust as needed

        // Populate barEntriesArrayList with spikes
        for (int i = 0; i < spikes.length; i++) {
            for (int j = 0; j < spikes[i].length; j++) {
                float xValue = i * distanceBetweenPairs + j * width + xOffset;
                barEntriesArrayList.add(new BarEntry(xValue, spikes[i][j]));
            }
        }

        // creating a new bar data set.
        BarDataSet barDataSet = new BarDataSet(barEntriesArrayList, "Schools, Students");

        // Set different colors for spikes
        barDataSet.setColors(new int[]{Color.RED, Color.GREEN});

        // creating a new bar data and
        // passing our bar data set.
        BarData barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(8f);

        // Set custom labels for X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekLabels));
        xAxis.setGranularity(1f); // Set the granularity to 1 to prevent axis label duplication

        // Set the position of week labels at the bottom of each pair of spikes
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(false); // Disable centered labels

        // Adjust X-axis label rotation for better readability
        xAxis.setLabelRotationAngle(0f); // No rotation

        // Set chart description
        barChart.getDescription().setEnabled(false);

        // Refresh the chart
        barChart.invalidate();
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