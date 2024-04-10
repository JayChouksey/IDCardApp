package com.example.idcard.recyclerfiles;

import android.content.Context;
import android.content.SharedPreferences;

public class SchoolInfo {
    private String distributorName;
    private String schoolName;
    private String email;
    private String schoolMobile;
    private String schoolAddress;
    private String numberOfStudents;
    private String status;
    private String created;
    private String allowedFields;
    private String allowedFieldsStaff;
    private String id;

    public SchoolInfo(String distributorName, String schoolName, String email, String schoolMobile, String schoolAddress,
                      String numberOfStudents, String status, String created, String allowedFields,
                      String allowedFieldsStaff, String id) {
        this.distributorName = distributorName;
        this.schoolName = schoolName;
        this.email = email;
        this.schoolMobile = schoolMobile;
        this.schoolAddress = schoolAddress;
        this.numberOfStudents = numberOfStudents;
        this.status = status;
        this.created = created;
        this.allowedFields = allowedFields;
        this.allowedFieldsStaff = allowedFieldsStaff;
        this.id = id;
    }

    public String getDistributorName() {
        return distributorName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getEmail() {
        return email;
    }

    public String getSchoolMobile() {
        return schoolMobile;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public String getNumberOfStudents() {
        return numberOfStudents;
    }

    public String getStatus() {
        return status;
    }

    public String getCreated() {
        return created;
    }

    public String getAllowedFields() {
        return allowedFields;
    }

    public String getId() {
        return id;
    }


    public String getAllowedFieldsStaff() {
        return allowedFieldsStaff;
    }
}
