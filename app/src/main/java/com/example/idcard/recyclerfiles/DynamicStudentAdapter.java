package com.example.idcard.recyclerfiles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idcard.AddSchool;
import com.example.idcard.AddStudent;
import com.example.idcard.MainActivity;
import com.example.idcard.R;
import com.example.idcard.api.SchoolDeletionHelper;
import com.example.idcard.api.StudentDeletionHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamicStudentAdapter extends RecyclerView.Adapter<DynamicStudentAdapter.DynamicStudentViewHolder> {
    private List<DynamicStudent> studentList;
    private Context context;
    private SharedPreferences sharedPreferences;
    private String selectedStudentIds = ""; // String to store selected student IDs

    public DynamicStudentAdapter(List<DynamicStudent> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
    }

    // Method to get the id's of the selected students
    public String getSelectedStudentIds() {
        StringBuilder selectedIdsBuilder = new StringBuilder();

        // Iterate through the studentList to find selected students
        for (DynamicStudent student : studentList) {
            if (student.isSelected()) {
                // Append the student ID to the StringBuilder
                selectedIdsBuilder.append(student.getValue("_id")).append(",");
            }
        }

        // Remove the trailing comma if it exists
        if (selectedIdsBuilder.length() > 0) {
            selectedIdsBuilder.deleteCharAt(selectedIdsBuilder.length() - 1);
        }

        return selectedIdsBuilder.toString();
    }

    @NonNull
    @Override
    public DynamicStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_student_list, parent, false);
        return new DynamicStudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DynamicStudentViewHolder holder, int position) {
        DynamicStudent student = studentList.get(position);
        holder.bind(student);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class DynamicStudentViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout dynamicLinearLayout;
        private CardView cardView;
        private Button btnEdit;
        private Button btnDelete;

        public DynamicStudentViewHolder(@NonNull View itemView) {
            super(itemView);
            dynamicLinearLayout = itemView.findViewById(R.id.dynamicLinearLayout);
            cardView = itemView.findViewById(R.id.cardView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            sharedPreferences = itemView.getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");

            // Button click listeners
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle Edit button click
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String studentId = studentList.get(position).getValue("_id");

                        // Storing id in local storage
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("studentIdFromListSchool",studentId);
                        editor.apply();

                        // Temporary
                        Toast.makeText(itemView.getContext(), "Error in Edit Student", Toast.LENGTH_SHORT).show();
                        // Will do it later
                        /*Intent intent = new Intent(itemView.getContext(), AddStudent.class);
                        itemView.getContext().startActivity(intent);*/
                    }
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle Delete button click
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String studentId = studentList.get(position).getValue("_id");
                        StudentDeletionHelper.deleteStudent(itemView.getContext(), studentId, token);
                        studentList.remove(position);
                        notifyItemRemoved(position);

                    }
                }
            });

            /// Handle item click event for selection
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Toggle selection status
                        DynamicStudent student = studentList.get(position);
                        student.setSelected(!student.isSelected());
                        notifyItemChanged(position); // Notify adapter of data change

                        // Adding id of selected student to the string
                        String studentId = student.getValue("_id");
                        if (student.isSelected()) {
                            if (!selectedStudentIds.contains(studentId)) {
                                // Add ID to selectedStudentIds string
                                if (!selectedStudentIds.isEmpty()) {
                                    selectedStudentIds += ",";
                                }
                                selectedStudentIds += studentId;
                            }
                        } else {
                            // Remove ID from selectedStudentIds string
                            if (selectedStudentIds.contains(studentId)) {
                                selectedStudentIds = selectedStudentIds.replace(studentId + ",", "");
                                selectedStudentIds = selectedStudentIds.replace("," + studentId, "");
                                selectedStudentIds = selectedStudentIds.replace(studentId, "");
                            }
                        }
                    }
                }
            });
        }

        public void bind(DynamicStudent student) {
            // Clear existing views
            dynamicLinearLayout.removeAllViews();

            if (student.isSelected()) {
                cardView.setBackgroundResource(R.drawable.border_selected);
            } else {
                cardView.setBackgroundResource(R.drawable.border_unselected);
            }

            // Loop through all fields and add TextViews dynamically
            for (Map.Entry<String, String> entry : student.getFields().entrySet()) {

                String title = entry.getKey();
                String data = entry.getValue();

                // Create LinearLayout
                LinearLayout linearLayout = new LinearLayout(itemView.getContext());
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayout.setPadding(20,10,10,10);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                // Create TextView for Title
                TextView distributorLabel = new TextView(itemView.getContext());
                LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                distributorLabel.setLayoutParams(labelParams);
                distributorLabel.setText(title);
                distributorLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                distributorLabel.setTypeface(null, Typeface.BOLD);
                linearLayout.addView(distributorLabel);

                // Create TextView for detail
                TextView distributorName = new TextView(itemView.getContext());
                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                nameParams.setMargins(10, 0, 0, 0); // Add left margin
                distributorName.setLayoutParams(nameParams);
                distributorName.setText(data);
                linearLayout.addView(distributorName);

                dynamicLinearLayout.addView(linearLayout);
            }
        }
    }
}

