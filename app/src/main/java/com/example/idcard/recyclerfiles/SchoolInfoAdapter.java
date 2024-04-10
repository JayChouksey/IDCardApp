package com.example.idcard.recyclerfiles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idcard.AddSchool;
import com.example.idcard.MainActivity;
import com.example.idcard.R;

import java.util.List;

import com.example.idcard.api.SchoolDeletionHelper;
import com.squareup.picasso.Picasso;

public class SchoolInfoAdapter extends RecyclerView.Adapter<SchoolInfoAdapter.ViewHolder> {

    private List<SchoolInfo> schoolInfoList;
    private SharedPreferences sharedPreferences;

    public SchoolInfoAdapter(List<SchoolInfo> schoolInfoList) {
        this.schoolInfoList = schoolInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_school_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SchoolInfo schoolInfo = schoolInfoList.get(position);
        holder.bind(schoolInfo);
    }

    @Override
    public int getItemCount() {
        return schoolInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textDistributor;
        private TextView textDistributorName;
        private TextView textSchool;
        private TextView textSchoolName;
        private TextView textEmail;
        private TextView textEmailValue;
        private TextView textSchoolMobile;
        private TextView textSchoolMobileValue;
        private TextView textSchoolAddress;
        private TextView textSchoolAddressValue;
        private TextView textNumberOfStudents;
        private TextView textNumberOfStudentsValue;
        private TextView textStatus;
        private TextView textStatusValue;
        private TextView textCreated;
        private TextView textCreatedValue;
        private TextView textAllowedFields;
        private TextView textAllowedFieldsValue;
        private TextView textAllowedFieldsStaff;
        private TextView textAllowedFieldsValueStaff;
        private Button btnBlock; // later work
        private Button btnEdit;
        private Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDistributor = itemView.findViewById(R.id.textDistributor);
            textDistributorName = itemView.findViewById(R.id.textDistributorName);
            textSchool = itemView.findViewById(R.id.textSchool);
            textSchoolName = itemView.findViewById(R.id.textSchoolName);
            textEmail = itemView.findViewById(R.id.textEmail);
            textEmailValue = itemView.findViewById(R.id.textEmailValue);
            textSchoolMobile = itemView.findViewById(R.id.textSchoolMobile);
            textSchoolMobileValue = itemView.findViewById(R.id.textSchoolMobileValue);
            textSchoolAddress = itemView.findViewById(R.id.textSchoolAddress);
            textSchoolAddressValue = itemView.findViewById(R.id.textSchoolAddressValue);
            textNumberOfStudents = itemView.findViewById(R.id.textNumberOfStudents);
            textNumberOfStudentsValue = itemView.findViewById(R.id.textNumberOfStudentsValue);
            textStatus = itemView.findViewById(R.id.textStatus);
            textStatusValue = itemView.findViewById(R.id.textStatusValue);
            textCreated = itemView.findViewById(R.id.textCreated);
            textCreatedValue = itemView.findViewById(R.id.textCreatedValue);
            textAllowedFields = itemView.findViewById(R.id.textAllowedFields);
            textAllowedFieldsValue = itemView.findViewById(R.id.textAllowedFieldsValue);
            textAllowedFieldsValueStaff = itemView.findViewById(R.id.textAllowedFieldsStaffValue);

            // Buttons
            // btnBlock = itemView.findViewById(R.id.btnBlock); --> Later Work
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            sharedPreferences = itemView.getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");

            // Set click listeners for buttons --> Later work
            /*btnBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle Block button click
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        SchoolInfo schoolInfo = schoolInfoList.get(position);
                    }
                }
            });*/

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle Edit button click
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        SchoolInfo schoolInfo = schoolInfoList.get(position);

                        // Storing id in local storage
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("schoolIdFromListStudent", schoolInfo.getId());
                        editor.apply();

                        Intent intent = new Intent(itemView.getContext(), AddSchool.class);
                        // Pass any data you need to the AddSchool class using intent extras
                        intent.putExtra("from","SchoolList");
                        intent.putExtra("nameSchool",textSchoolName.getText().toString().trim());
                        intent.putExtra("contactSchool",textSchoolMobileValue.getText().toString().trim());
                        intent.putExtra("emailSchool",textEmailValue.getText().toString().trim());
                        intent.putExtra("addressSchool",textSchoolAddressValue.getText().toString().trim());
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
                        SchoolInfo schoolInfo = schoolInfoList.get(position);
                        SchoolDeletionHelper.deleteSchool(itemView.getContext(), schoolInfo.getId(), token);
                        schoolInfoList.remove(position);
                        notifyItemRemoved(position);
                        //Toast.makeText(itemView.getContext(), "Delete button clicked for " + schoolInfo.getId(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void bind(SchoolInfo schoolInfo) {
            textDistributorName.setText(schoolInfo.getDistributorName());
            textSchoolName.setText(schoolInfo.getSchoolName());
            textEmailValue.setText(schoolInfo.getEmail());
            textSchoolMobileValue.setText(schoolInfo.getSchoolMobile());
            textSchoolAddressValue.setText(schoolInfo.getSchoolAddress());
            textNumberOfStudentsValue.setText(schoolInfo.getNumberOfStudents());
            textStatusValue.setText(schoolInfo.getStatus());
            textCreatedValue.setText(schoolInfo.getCreated());
            textAllowedFieldsValue.setText(schoolInfo.getAllowedFields());
            textAllowedFieldsValueStaff.setText(schoolInfo.getAllowedFieldsStaff());
        }
    }
}
