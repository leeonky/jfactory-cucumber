package com.github.leeonky.jfactory.cucumber;

import java.util.LinkedHashMap;
import java.util.Map;

public class Row extends LinkedHashMap<String, Object> {
    public static Row create(Map<String, ?> data) {
        Row row = new Row();
        row.putAll(data);
        return row;
    }

    public Row set(String column, Object value) {
        Row row = create(this);
        row.put(column, value);
        return row;
    }
}
