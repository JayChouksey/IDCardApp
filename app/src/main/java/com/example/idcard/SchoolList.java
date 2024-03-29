package com.example.idcard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.idcard.recyclerfiles.SchoolInfo;
import com.example.idcard.recyclerfiles.SchoolInfoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolList extends AppCompatActivity {

    RecyclerView recyclerView;
    SchoolInfoAdapter adapter;
    String [] sort = {"Ascending","Descending"};

    AutoCompleteTextView autoCompleteSort;
    ArrayAdapter<String> adapterSort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);

        // Setting the name of the distributor
        TextView userName = findViewById(R.id.userName);
        userName.setText(getUserName());
        // End of Setting the name of the distributor

        // ----------------------------------------------------------------------------
        // Will add later
        // Drop Down options Code
/*        autoCompleteSort = findViewById(R.id.sort_dropdown);

        adapterSort = new ArrayAdapter<String>(this,R.layout.list_item,sort);
        autoCompleteSort.setAdapter(adapterSort);

        autoCompleteSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),"Item: "+item,Toast.LENGTH_SHORT).show();
            }
        });*/
        // End of Drop down options code

        fetchSchoolData();
    }
    // End of main method

    private void fetchSchoolData() {
        // Get the authorization token
        String token = getToken();

        String url = "https://id-card-backend-2.onrender.com/user/schools";

        List<SchoolInfo> schoolInfoList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(SchoolList.this, "Data Fetched Successfully", Toast.LENGTH_SHORT).show();

                        try {
                            JSONArray schoolsArray = response.getJSONArray("schools");

                            for (int i = 0; i < schoolsArray.length(); i++) {
                                JSONObject schoolObject = schoolsArray.getJSONObject(i);
                                String distributorName = getUserName();
                                String name = schoolObject.getString("name");
                                String email = schoolObject.getString("email");
                                int contact = schoolObject.getInt("contact");
                                String contactStr = Integer.toString(contact);
                                String address = schoolObject.getString("address");
                                int code = schoolObject.getInt("code");
                                String codeStr = Integer.toString(code);
                                String studentNumber = Integer.toString(schoolObject.getInt("studentCount"));
                                boolean isActive = schoolObject.getBoolean("isActive");
                                String status = isActive ? "Active" : "Blocked";
                                String date = schoolObject.getString("createdAt");
                                date = date.substring(0,10);
                                JSONArray requiredFieldsArray = schoolObject.getJSONArray("requiredFields");
                                StringBuilder requiredFieldsBuilder = new StringBuilder();
                                for (int j = 0; j < requiredFieldsArray.length(); j++) {
                                    requiredFieldsBuilder.append(requiredFieldsArray.getString(j));
                                    if (j < requiredFieldsArray.length() - 1) {
                                        requiredFieldsBuilder.append(", ");
                                    }
                                }
                                String requiredFields = requiredFieldsBuilder.toString();

                                // Create a SchoolInfo object and add it to the list
                                SchoolInfo schoolInfo = new SchoolInfo(distributorName, name, email, contactStr, address, codeStr,
                                        studentNumber, status, date, requiredFields);


                                schoolInfoList.add(schoolInfo);
                            }

                            // Update adapter with the fetched data
                            updateRecyclerView(schoolInfoList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SchoolList.this, "Error in data fetching", Toast.LENGTH_SHORT).show();
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


    private void updateRecyclerView(List<SchoolInfo> schoolInfoList) {
        recyclerView = findViewById(R.id.school_list_recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SchoolList.this));
        adapter = new SchoolInfoAdapter(schoolInfoList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged(); // Notify adapter of dataset changes
    }

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