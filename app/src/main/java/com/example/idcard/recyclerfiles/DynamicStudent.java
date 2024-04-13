package com.example.idcard.recyclerfiles;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DynamicStudent {

    private Map<String, String> fields;
    private boolean isSelected; // Field to track selection status
    private String avatarUrl;


    public DynamicStudent() {
        this.fields = new LinkedHashMap<>();
        this.isSelected = false; // Default selection status is false
    }

    public void addField(String key, String value) {
        fields.put(key, value);
    }

    public String getValue(String key) {
        return fields.get(key);
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getStudentId() {
        return getValue("_id");
    }


}
