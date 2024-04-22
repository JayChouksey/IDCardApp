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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.idcard.AddSchool;
import com.example.idcard.AddStudent;
import com.example.idcard.EditStudent;
import com.example.idcard.MainActivity;
import com.example.idcard.R;
import com.example.idcard.api.SchoolDeletionHelper;
import com.example.idcard.api.StaffDeletionHelper;
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

    // Method to select all students and update selected student IDs string
    public void selectAllStudents(boolean isSelected) {
        selectedStudentIds = ""; // Clear existing selection

        // Update selection status for all students
        for (DynamicStudent student : studentList) {
            student.setSelected(isSelected);
            if (isSelected) {
                selectedStudentIds += student.getStudentId() + ","; // Add student ID to the string
            }
        }

        notifyDataSetChanged(); // Notify adapter of data change
    }

    public class DynamicStudentViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout dynamicLinearLayout;
        private CardView cardView;
        private Button btnEdit;
        private Button btnDelete;
        private ImageView avatar;

        public DynamicStudentViewHolder(@NonNull View itemView) {
            super(itemView);
            dynamicLinearLayout = itemView.findViewById(R.id.dynamicLinearLayout);
            cardView = itemView.findViewById(R.id.cardView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            avatar = itemView.findViewById(R.id.avatar_img);

            sharedPreferences = itemView.getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");


            // Button click listeners
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle Edit button click
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String studentStaffId = studentList.get(position).getValue("_id");
                        String schoolId = studentList.get(position).getValue("school");
                        String role = studentList.get(position).getValue("role");
                        // Storing id in local storage
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("StudentOrStaffIdFromListSchool",studentStaffId);
                        editor.putString("SchoolIdFromListSchool",schoolId);
                        editor.putString("Role",role);
                        editor.apply();

                        Intent intent = new Intent(itemView.getContext(), EditStudent.class);
                        intent.putExtra("Name",studentList.get(position).getValue("name"));
                        intent.putExtra("Father's Name",studentList.get(position).getValue("fatherName"));
                        intent.putExtra("Husband's Name",studentList.get(position).getValue("husbandName"));
                        intent.putExtra("Date of Birth", studentList.get(position).getValue("dob"));
                        intent.putExtra("Qualification", studentList.get(position).getValue("qualification"));
                        intent.putExtra("Designation", studentList.get(position).getValue("designation"));
                        intent.putExtra("Date of Joining", studentList.get(position).getValue("doj"));
                        intent.putExtra("Staff Type", studentList.get(position).getValue("staffType"));
                        intent.putExtra("Address", studentList.get(position).getValue("address"));
                        intent.putExtra("Contact No.", studentList.get(position).getValue("contact"));
                        intent.putExtra("UID No.", studentList.get(position).getValue("uid"));
                        intent.putExtra("E-mail", studentList.get(position).getValue("email"));
                        intent.putExtra("Staff ID", studentList.get(position).getValue("staffID"));
                        intent.putExtra("UDISE Code", studentList.get(position).getValue("udiseCode"));
                        intent.putExtra("School Name", studentList.get(position).getValue("schoolName"));
                        intent.putExtra("Blood Group", studentList.get(position).getValue("bloodGroup"));
                        intent.putExtra("Dispatch No.", studentList.get(position).getValue("dispatchNo"));
                        intent.putExtra("Date of Issue", studentList.get(position).getValue("dateOfissue"));
                        intent.putExtra("IHRMS No.", studentList.get(position).getValue("ihrmsNo"));
                        intent.putExtra("Belt No.", studentList.get(position).getValue("beltNo"));
                        intent.putExtra("Student Name", studentList.get(position).getValue("studentName"));
                        intent.putExtra("Mother's Name", studentList.get(position).getValue("motherName"));
                        intent.putExtra("Class", studentList.get(position).getValue("class"));
                        intent.putExtra("Section", studentList.get(position).getValue("section"));
                        intent.putExtra("Roll No.", studentList.get(position).getValue("rollNo"));
                        intent.putExtra("Admission No.", studentList.get(position).getValue("admissionNo"));
                        intent.putExtra("Student ID", studentList.get(position).getValue("studentID"));
                        intent.putExtra("Aadhar No.", studentList.get(position).getValue("aadharNo"));
                        intent.putExtra("Ribbon Colour", studentList.get(position).getValue("ribbonColour"));
                        intent.putExtra("Route No.", studentList.get(position).getValue("routeNo"));
                        intent.putExtra("Mode of Transport", studentList.get(position).getValue("modeOfTransport"));

                        itemView.getContext().startActivity(intent);
                    }
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle Delete button click
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String studentStaffId = studentList.get(position).getValue("_id");
                        String role = studentList.get(position).getValue("role");

                        if(role.equals("student")){
                            StudentDeletionHelper.deleteStudent(itemView.getContext(), studentStaffId, token);
                        }
                        else{
                            StaffDeletionHelper.deleteStudent(itemView.getContext(), studentStaffId, token);
                        }

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

            String avatarUrl = student.getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.school_list_student) // Placeholder image while loading
                        .error(R.drawable.about_icon); // Error image if loading fails

                Glide.with(itemView.getContext())
                        .load(avatarUrl)
                        .apply(requestOptions)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk cache for now
                        .into(avatar);
            }

            // Loop through all fields and add TextViews dynamically
            for (Map.Entry<String, String> entry : student.getFields().entrySet()) {

                String data = entry.getValue();
                String title = "";
                switch (entry.getKey()) {
                    // for staff
                    case "name":
                        title = "Name: ";
                        break;
                    case "fatherName":
                        title = "Father's Name: ";
                        break;
                    case "husbandName":
                        title = "Husband's Name: ";
                        break;
                    case "dob":
                        title = "Date of Birth: ";
                        break;
                    case "qualification":
                        title = "Qualification: ";
                        break;
                    case "designation":
                        title = "Designation: ";
                        break;
                    case "doj":
                        title = "Date of Joining: ";
                        break;
                    case "staffType":
                        title = "Staff Type: ";
                        break;
                    case "address":
                        title = "Address: ";
                        break;
                    case "contact":
                        title = "Contact No.: ";
                        break;
                    case "uid":
                        title = "UID No.: ";
                        break;
                    case "email":
                        title = "E-mail: ";
                        break;
                    case "staffID":
                        title = "Staff ID: ";
                        break;
                    case "udiseCode":
                        title = "UDISE Code: ";
                        break;
                    case "schoolName":
                        title = "School Name: ";
                        break;
                    case "bloodGroup":
                        title = "Blood Group: ";
                        break;
                    case "dispatchNo":
                        title = "Dispatch No.: ";
                        break;
                    case "dateOfissue":
                        title = "Date of Issue: ";
                        break;
                    case "ihrmsNo":
                        title = "IHRMS No.: ";
                        break;
                    case "beltNo":
                        title = "Belt No.: ";
                        break;

                    // for student
                    case "studentName":
                        title = "Student Name";
                        break;
                    case "motherName":
                        title = "Mother's Name: ";
                        break;
                    case "class":
                        title = "Class: ";
                        break;
                    case "section":
                        title = "Section: ";
                        break;
                    case "rollNo":
                        title = "Roll No.: ";
                        break;
                    case "admissionNo":
                        title = "Admission No.: ";
                        break;
                    case "studentID":
                        title = "Student ID: ";
                        break;
                    case "aadharNo":
                        title = "Aadhar No.: ";
                        break;
                    case "ribbonColour":
                        title = "Ribbon Colour: ";
                        break;
                    case "routeNo":
                        title = "Route No.: ";
                        break;
                    case "modeOfTransport":
                        title = "Mode of Transport: ";
                        break;
                    case "status":
                        title = "Status: ";
                        break;
                }

                if(entry.getValue().equals("Panding")){
                    data = "Pending";
                }

                if (!entry.getKey().equals("_id") && !entry.getKey().equals("school") && !entry.getKey().equals("role")){
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
}

