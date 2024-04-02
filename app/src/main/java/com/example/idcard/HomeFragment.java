package com.example.idcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Setting the name
        TextView userName = view.findViewById(R.id.userName);
        String name = getUserNameFromSharedPreferences();
        userName.setText(name);

        // -----------------------------------------------------------------------------------------------------
        // Switching from Fragment to Activity

        // CardView About Transition
        CardView about = view.findViewById(R.id.cardViewAbout);

        // Set OnClickListener to the CardView
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the activity
                Intent intent = new Intent(getActivity(), About.class);

                // Start the activity
                startActivity(intent);
            }
        });

        // CardView Contact Transition
        CardView contact = view.findViewById(R.id.cardViewContact);

        // Set OnClickListener to the CardView
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the activity
                Intent intent = new Intent(getActivity(), Contact.class);

                // Start the activity
                startActivity(intent);
            }
        });

        // CardView AddSchool Transition
        CardView addSchool = view.findViewById(R.id.cardViewAddSchool);

        // Set OnClickListener to the CardView
        addSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the activity
                Intent intent = new Intent(getActivity(), AddSchool.class);
                intent.putExtra("from","Home");

                // Start the activity
                startActivity(intent);
            }
        });

        // CardView SchoolList Transition
        CardView schoolList = view.findViewById(R.id.cardViewSchoolList);

        // Set OnClickListener to the CardView
        schoolList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the activity
                Intent intent = new Intent(getActivity(), SchoolList.class);

                // Start the activity
                startActivity(intent);
            }
        });

        // CardView ListStudent Transition
        CardView listStudent = view.findViewById(R.id.cardViewListStudents);

        // Set OnClickListener to the CardView
        listStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the activity
                Intent intent = new Intent(getActivity(), ListStudents.class);

                // Start the activity
                startActivity(intent);
            }
        });

        // CardView AddStudent Transition
        CardView addStudent = view.findViewById(R.id.cardViewAddStudent);

        // Set OnClickListener to the CardView
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the activity
                Intent intent = new Intent(getActivity(), AddStudent.class);

                // Start the activity
                startActivity(intent);
            }
        });

        // CardView AddStudent Transition
        CardView importStudent = view.findViewById(R.id.cardViewImportStudents);

        // Set OnClickListener to the CardView
        importStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the activity
                Intent intent = new Intent(getActivity(), ImportStudents.class);

                // Start the activity
                startActivity(intent);
            }
        });

        // CardView Services Transition
        CardView services = view.findViewById(R.id.cardViewServices);

        // Set OnClickListener to the CardView
        services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the activity
                Intent intent = new Intent(getActivity(), Service.class);

                // Start the activity
                startActivity(intent);
            }
        });


        return view;

    }

    // Method for fragments to access the data saved locally
    // Method to retrieve the name from SharedPreferences
    private String getUserNameFromSharedPreferences() {
        // Obtain the SharedPreferences object from the Activity's context
        Context context = requireActivity(); // or getContext() depending on your Fragment's version
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Retrieve the name from SharedPreferences
        return sharedPreferences.getString("name", "");
    }

}