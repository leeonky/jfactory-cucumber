package com.github.leeonky.jfactory.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toCollection;

public class Table extends ArrayList<Row> {

    private Table() {
    }

    public static Table create(List<Map<String, ?>> maps) {
        return maps.stream().map(Row::create).collect(toCollection(Table::new));
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, ?>> toMap(String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Object value = objectMapper.readValue(content, Object.class);
        if (value instanceof List)
            return (List) value;
        return (List) singletonList(value);
    }

    static Table create(String content) throws IOException {
        return create(toMap(content));
    }

    static Table create(DataTable dataTable) {
        return create((List) dataTable.asMaps());
    }
}
