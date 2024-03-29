package com.example.idcard.recyclerfiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idcard.R;

import java.util.List;

public class SchoolInfoAdapter extends RecyclerView.Adapter<SchoolInfoAdapter.ViewHolder> {

    private List<SchoolInfo> schoolInfoList;

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
        private TextView textSchoolCode;
        private TextView textSchoolCodeValue;
        private TextView textNumberOfStudents;
        private TextView textNumberOfStudentsValue;
        private TextView textStatus;
        private TextView textStatusValue;
        private TextView textCreated;
        private TextView textCreatedValue;
        private TextView textAllowedFields;
        private TextView textAllowedFieldsValue;

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
            textSchoolCode = itemView.findViewById(R.id.textSchoolCode);
            textSchoolCodeValue = itemView.findViewById(R.id.textSchoolCodeValue);
            textNumberOfStudents = itemView.findViewById(R.id.textNumberOfStudents);
            textNumberOfStudentsValue = itemView.findViewById(R.id.textNumberOfStudentsValue);
            textStatus = itemView.findViewById(R.id.textStatus);
            textStatusValue = itemView.findViewById(R.id.textStatusValue);
            textCreated = itemView.findViewById(R.id.textCreated);
            textCreatedValue = itemView.findViewById(R.id.textCreatedValue);
            textAllowedFields = itemView.findViewById(R.id.textAllowedFields);
            textAllowedFieldsValue = itemView.findViewById(R.id.textAllowedFieldsValue);
        }

        public void bind(SchoolInfo schoolInfo) {
            textDistributorName.setText(schoolInfo.getDistributorName());
            textSchoolName.setText(schoolInfo.getSchoolName());
            textEmailValue.setText(schoolInfo.getEmail());
            textSchoolMobileValue.setText(schoolInfo.getSchoolMobile());
            textSchoolAddressValue.setText(schoolInfo.getSchoolAddress());
            textSchoolCodeValue.setText(schoolInfo.getSchoolCode());
            textNumberOfStudentsValue.setText(schoolInfo.getNumberOfStudents());
            textStatusValue.setText(schoolInfo.getStatus());
            textCreatedValue.setText(schoolInfo.getCreated());
            textAllowedFieldsValue.setText(schoolInfo.getAllowedFields());
        }
    }
}
