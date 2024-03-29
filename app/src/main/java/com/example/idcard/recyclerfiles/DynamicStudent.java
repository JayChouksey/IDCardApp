package com.example.idcard.recyclerfiles;

import java.util.HashMap;
import java.util.Map;

public class DynamicStudent {

    private Map<String, String> fields;

    public DynamicStudent() {
        this.fields = new HashMap<>();
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
}
