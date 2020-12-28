package com.github.leeonky.jfactory.cucumber;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.leeonky.util.BeanClass;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

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

    public Map[] flatSub() {
        return stream().map(this::flat).toArray(Map[]::new);
    }

    private Map<String, Object> merge(Map<String, Object> m1, Map<String, Object> m2) {
        return new LinkedHashMap<String, Object>() {{
            putAll(m1);
            putAll(m2);
        }};
    }

    private Map<String, Object> flatSub(String key, Object value) {
        if (value instanceof Map)
            return combineKey(flat((Map<String, Object>) value), subKey -> key + "." + subKey);
        else if (value instanceof List)
            return combineKey(flat((List<Object>) value), subKey -> key + subKey);
        else
            return new HashMap<String, Object>() {{
                put(key, value);
            }};
    }

    private Map<String, Object> combineKey(Map<String, Object> sub, Function<String, String> rule) {
        return sub.entrySet().stream()
                .collect(toMap(e -> rule.apply(e.getKey()), Map.Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new));
    }

    private Map<String, Object> flat(Map<String, Object> value) {
        return value.entrySet().stream().map(m -> flatSub(m.getKey(), m.getValue()))
                .reduce(new LinkedHashMap<>(), this::merge);
    }

    private Map<String, Object> flat(List<Object> list) {
        Iterator<Integer> index = Stream.iterate(0, i -> i++).iterator();
        return list.stream().map(e -> flatSub("[" + index.next() + "]", e))
                .reduce(new LinkedHashMap<>(), this::merge);
    }
}
