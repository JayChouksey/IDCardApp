package com.example.idcard.recyclerfiles;

import java.util.HashMap;
import java.util.Map;

public class DynamicStudent {

    private Map<String, String> fields;
    private boolean isSelected; // Field to track selection status

    public DynamicStudent() {
        this.fields = new HashMap<>();
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
}
