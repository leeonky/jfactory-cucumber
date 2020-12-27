package com.github.leeonky.jfactory.cucumber;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.leeonky.util.BeanClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toCollection;

public class Table extends ArrayList<Row> {
    private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectMapper YAML_OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

    public static Table create(List<Map<String, ?>> maps) {
        return maps.stream().map(Row::create).collect(toCollection(Table::new));
    }

    @SuppressWarnings("unchecked")
    public static Table create(String content) throws IOException {
        Object value = parse(content);
        return create(BeanClass.cast(value, List.class).orElseGet(() -> singletonList(value)));
    }

    private static Object parse(String content) throws IOException {
        try {
            return JSON_OBJECT_MAPPER.readValue(content, Object.class);
        } catch (JsonParseException e) {
            return YAML_OBJECT_MAPPER.readValue(content, Object.class);
        }
    }
}
